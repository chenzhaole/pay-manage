package com.sys.admin.common.web;

import com.sys.admin.common.beanvalidator.BeanValidators;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.exception.BusinessException;
import com.sys.common.util.DateUtils;
import com.sys.common.util.FileUtils;
import com.sys.common.util.FtpClient;
import com.sys.common.util.ImageUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 控制器支持类
 */
public abstract class BaseController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 图片文件存放路径（相对于应用根目录）
     */
    public static final String IMAGES_PATH = "/images/";

    /**
     * 验证Bean实例对象
     */
    @Autowired
    protected Validator validator;

    /**
     * 校验前处理
     * @param object 验证的实体对象
     * @throws BusinessException
     */
    protected void beforeBeanValidator(Object object) throws BusinessException {

    }

    /**
     * 校验后处理
     * @param object 验证的实体对象
     * @throws BusinessException
     */
    protected void afterBeanValidator(Object object) throws BusinessException {

    }

    /**
     * 服务端参数有效性验证
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
     */
    protected boolean beanValidator(Model model, Object object, Class<?>... groups) {
        try {
            beforeBeanValidator(object);
            BeanValidators.validateWithException(validator, object, groups);
            afterBeanValidator(object);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addMessage(model, list.toArray(new String[]{}));
            return false;
        } catch (BusinessException e) {
            List<String> list = new ArrayList<String>();
            list.add(0, "数据验证失败：");
            list.add(1, e.getMessage());
            addMessage(model, list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    /**
     * 服务端参数有效性验证
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 flash message 中
     */
    protected boolean beanValidator(RedirectAttributes redirectAttributes, Object object, Class<?>... groups) {
        try {
            beforeBeanValidator(object);
            BeanValidators.validateWithException(validator, object, groups);
            afterBeanValidator(object);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addMessage(redirectAttributes, list.toArray(new String[]{}));
            return false;
        } catch (BusinessException e) {
            List<String> list = new ArrayList<String>();
            list.add(0, "数据验证失败：");
            list.add(1, e.getMessage());
            addMessage(redirectAttributes, list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    /**
     * 添加Model消息
     */
    protected void addMessage(Model model, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        model.addAttribute("message", sb.toString());
    }

    /**
     * 添加Flash消息
     */
    protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        redirectAttributes.addFlashAttribute("message", sb.toString());
    }

    /**
     * 初始化数据绑定
     * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
     * 2. 将字段中Date类型转换为String类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : text);
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? value.toString() : "";
            }
        });
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 图片上传，上传后文件名重命名，按照指定的尺寸进行压缩，并按参数的尺寸生成缩略图
     * 1、例如源文件保存在/userfile/bus100/20141024/20141024155312804.JPG，那么缩略图地址为/thumbs/userfile/bus100/20141024/20141024155312804.JPG
     * 2、若compWidth和compHeight都为0表示不压缩
     * 3、若thumbWidth和thumbHeight都为0表示不生成缩略图
     * @param imgFile     上传的文件
     * @param thumbWidth  缩略图宽度，为0表示只按高度压缩
     * @param thumbHeight 缩略图高度，为0表示只按宽度压缩
     * @param compWidth   原图压缩后的宽度，为0表示只按高度压缩
     * @param compHeight  原图压缩后的高度，为0表示只按宽度压缩
     * @param proportion  是否按等比例压缩
     * @return 文件所在服务器的完整目录，如/userfile/bus100/20141024/20141024155312804.JPG
     */
    protected String uploadImageWithThumbAndComp(MultipartFile imgFile, int thumbWidth, int thumbHeight, int compWidth, int compHeight, boolean proportion) {
        if (imgFile == null) {
            return null;
        }

        String fileName = imgFile.getOriginalFilename();
        // 获取文件扩展名
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 设置上传文件名
        SimpleDateFormat fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // 源文件重命名,文件命名格式:yyyyMMddHHmmssSSS
        fileName = fileFormatter.format(new Date()) + "." + ext;

        //如果文件不是图片，则不上传
        if (!"jpg".equalsIgnoreCase(ext) && !"jpeg".equalsIgnoreCase(ext)
                && !"png".equalsIgnoreCase(ext) && !"gif".equalsIgnoreCase(ext)
                && !"bmp".equalsIgnoreCase(ext)) {
            return null;
        }
        try {
            //将上传的图片文件临时保存到服务器的临时目录中
            String outputDirectory = System.getProperty("java.io.tmpdir") + "uploadImageFile/";
            File file = new File(outputDirectory);
            if (!file.exists()) {
                if (!file.mkdir()) {//生成临时目录
                    return null;
                }
            }
            File tempFile = new File(outputDirectory + fileName);
            if (!tempFile.createNewFile()) {//生成临时文件
                return null;
            }
            imgFile.transferTo(tempFile);

            String outputCompDirectory = System.getProperty("java.io.tmpdir") + "uploadImageCompFile/";
            File fileComp = new File(outputCompDirectory);
            if (!fileComp.exists()) {
                if (!fileComp.mkdir()) {//生成临时目录
                    return null;
                }
            }
            boolean isCompressed = false;//图片是否被压缩
            ImageUtils imageUtils = new ImageUtils();
            boolean result = imageUtils.compressPic(outputDirectory, outputCompDirectory, fileName, fileName, compWidth, compHeight, proportion);
            if (result) {
                isCompressed = true;
            }

            //生成缩略图
            String outputThumbDirectory = System.getProperty("java.io.tmpdir") + "uploadImageThumbFile/";
            File fileThumb = new File(outputThumbDirectory);
            if (!fileThumb.exists()) {
                if (!fileThumb.mkdir()) {//生成临时目录
                    return null;
                }
            }
            imageUtils.compressPic(outputDirectory, outputThumbDirectory, fileName, fileName, thumbWidth, thumbHeight, proportion);

            String imgPath = GlobalConfig.getImagePath() + DateUtils.getNoSpSysDateString() + "/";
            String imgThumbPath = GlobalConfig.getImageThumbPath() + DateUtils.getNoSpSysDateString() + "/";

            String ftp_url = GlobalConfig.getFTPUrl();
            int ftp_port = Integer.valueOf(GlobalConfig.getFTPPort());
            String ftp_user = GlobalConfig.getFTPUser();
            String ftp_password = GlobalConfig.getFTPPwd();
            FtpClient ftp = new FtpClient(ftp_url, ftp_port, ftp_user, ftp_password);
            ftp.ftpLogin();
            //上传原图片
            if (isCompressed) {
                ftp.uploadFile(new File(outputCompDirectory + fileName), imgPath);
            } else {
                ftp.uploadFile(tempFile, imgPath);
            }
            ftp.ftpLogOut();
            ftp.ftpLogin();//此处登陆两次是为了保证第二次文件拷贝时目录正确，由于有ftpClient.changeWorkingDirectory(dir);导致第二次拷贝文件时是在第一次目录的基础上进行叠加
            //上传缩略图
            ftp.uploadFile(new File(outputThumbDirectory + fileName), imgThumbPath);
            ftp.ftpLogOut();

            //最后删除临时文件 包括原图、压缩图和缩略图
            try {
                tempFile.delete();
                new File(outputCompDirectory + fileName).delete();
                new File(outputThumbDirectory + fileName).delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imgPath + fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 将客户端图片上传至工程部署根目录的images目录下，用于临时的展示及作为裁剪时的原图片
     * @param img_file 图片文件
     * @return 图片路径
     */
    protected String uploadImageToWebServer(HttpServletRequest request, MultipartFile img_file) {
        try {
            String fileName = img_file.getOriginalFilename();// getOriginalFilename();
            // 获取文件扩展名
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

            //如果文件不是图片，则不上传
            if (!"jpg".equalsIgnoreCase(ext) && !"jpeg".equalsIgnoreCase(ext)
                    && !"png".equalsIgnoreCase(ext) && !"gif".equalsIgnoreCase(ext)
                    && !"bmp".equalsIgnoreCase(ext)) {
                return null;
            }

            // 设置上传文件名
            SimpleDateFormat fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            fileName = fileFormatter.format(new Date()) + new Random().nextInt(1000) + "." + ext;
            String dir = getImageFileStorePath(request);
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }

            File tempFile = new File(dir + fileName);
            if (!tempFile.createNewFile()) {
                return null;
            }
            img_file.transferTo(tempFile);

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件上传，上传后文件名重命名
     *
     * @param img_article 上传的文件
     * @return 文件所在服务器的完整目录，如/userfile/bus100/20141024/20141024155312804.JPG
     */
    protected String uploadFile(MultipartFile img_article) {

        String ftp_url = GlobalConfig.getFTPUrl();
        int ftp_port = Integer.valueOf(GlobalConfig.getFTPPort());
        String ftp_user = GlobalConfig.getFTPUser();
        String ftp_password = GlobalConfig.getFTPPwd();
        String fileName = img_article.getOriginalFilename();
        // 获取文件扩展名
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 设置上传文件名
        SimpleDateFormat fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        // 文件命名格式:yyyyMMddHHmmssSSS
        fileName = fileFormatter.format(new Date()) + "." + ext;
        FtpClient ftp = new FtpClient(ftp_url, ftp_port, ftp_user, ftp_password);
        ftp.ftpLogin();
        String imgPath = GlobalConfig.getImagePath() + DateUtils.getNoSpSysDateString() + "/";
        try {
            ftp.uploadFile(img_article.getInputStream(), fileName, imgPath);
//            String image_server = GlobalConfig.getConfig("image.server");
            return imgPath + fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        ftp.ftpLogOut();
        return "";
    }

    /**
     * 文件上传，上传后文件名保持不变
     *
     * @param img_article 上传的文件
     * @param timestamp 按时间戳创建目录，文件放在该目录下；如果未null，则不创建
     * @return 文件所在服务器的完整目录，如/userfile/bus100/20141024/test.JPG
     */
    protected String uploadFileKeepName(MultipartFile img_article, Long timestamp) {
        String ftp_url = GlobalConfig.getFTPUrl();
        int ftp_port = Integer.valueOf(GlobalConfig.getFTPPort());
        String ftp_user = GlobalConfig.getFTPUser();
        String ftp_password = GlobalConfig.getFTPPwd();
        String fileName = img_article.getOriginalFilename();
        FtpClient ftp = new FtpClient(ftp_url, ftp_port, ftp_user, ftp_password);
        ftp.ftpLogin();
        String imgPath = "";
        imgPath = GlobalConfig.getImagePath() + DateUtils.getNoSpSysDateString() + "/";
        if (timestamp != null) {
            imgPath += timestamp + "/";
        }
        try {
            ftp.uploadFile(img_article.getInputStream(), fileName, imgPath);
//            String image_server = GlobalConfig.getConfig("image.server");
            return imgPath + fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        ftp.ftpLogOut();
        return "";
    }

    protected String getParameter(String key) {
        if (getParameterMap() == null || getParameterMap().isEmpty()) {
            return null;
        }
        if (getParameterMap().get(key) == null) {
            return null;
        }
        return ((String[]) (getParameterMap().get(key)))[0];
    }

    //获取HttpServletRequest对象
    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    //获取请求参数
    private Map getParameterMap() {
        return getRequest().getParameterMap();
    }


    /**
     * 项目图片上传，上传后文件名重命名，按照指定的尺寸进行压缩，并按参数的尺寸生成缩略图
     * 1、例如源文件保存在/userfile/bus100/20141024/20141024155312804.JPG，那么缩略图地址为/thumbs/userfile/bus100/20141024/20141024155312804.JPG
     * 2、针对移动端的地址分别为：/mobilefile/userfile/bus100/20141024/20141024155312804.JPG，缩略图地址为/mobilefile/thumbs/userfile/bus100/20141024/20141024155312804.JPG
     * @param imgType  图片类型
     * @param imgName   已经上传到WEB服务器的临时图片名称
     * @param x1 裁剪起始的X坐标
     * @param y1 裁剪起始的Y坐标
     * @param cw 裁剪后的宽度
     * @param ch 裁剪后的高度
     * @param thumbWith 缩略图宽
     * @param thumbHeight 缩略图高
     * @return 文件所在服务器的完整目录，如/userfile/bus100/20141024/20141024155312804.JPG
     */
    protected String cutProjectImage(HttpServletRequest request, String imgType, String imgName, int x1, int y1, int cw, int ch, int thumbWith, int thumbHeight) {
        if (StringUtils.isBlank(imgName)) {
            return null;
        }
        try {
            File imgFile = new File(getImageFileStorePath(request) + imgName);
            String fileName = imgFile.getName();// getOriginalFilename();
            // 获取文件扩展名
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            // 设置上传文件名
            SimpleDateFormat fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            // 源文件重命名,文件命名格式:yyyyMMddHHmmssSSS
            fileName = fileFormatter.format(new Date()) + "." + ext;

            //如果文件不是图片，则不上传
            if (!"jpg".equalsIgnoreCase(ext) && !"jpeg".equalsIgnoreCase(ext)
                    && !"png".equalsIgnoreCase(ext) && !"gif".equalsIgnoreCase(ext)
                    && !"bmp".equalsIgnoreCase(ext)) {
                return null;
            }

            String outputDirectory = System.getProperty("java.io.tmpdir") + "uploadImageFile/";
            File file = new File(outputDirectory);
            if (!file.exists()) {
                if (!file.mkdir()) {//生成临时目录
                    return null;
                }
            }
            ImageUtils imageUtils = new ImageUtils();
            //裁剪图片
            if (cw > 0 && ch > 0) {
                boolean isCut = imageUtils.cutImage(getImageFileStorePath(request), outputDirectory, imgName, fileName, x1, y1, cw, ch);
                if (!isCut) {
                    return null;
                }
            } else {
                //不裁剪，直接拷贝
                FileUtils.copyFileCover(getImageFileStorePath(request) + imgName, outputDirectory + fileName, true);
            }

            //创建缩略图存放的临时目录
            String outputThumbDirectory = System.getProperty("java.io.tmpdir") + "uploadImageThumbFile/";
            File fileThumb = new File(outputThumbDirectory);
            if (!fileThumb.exists()) {
                if (!fileThumb.mkdir()) {//生成临时目录
                    return null;
                }
            }
            imageUtils.compressPic(outputDirectory, outputThumbDirectory, fileName, fileName, thumbWith, thumbHeight, true, true);

            String imgPath = GlobalConfig.getImagePath() + DateUtils.getNoSpSysDateString() + "/";
            String imgThumbPath = GlobalConfig.getImageThumbPath() + DateUtils.getNoSpSysDateString() + "/";

            String ftp_url = GlobalConfig.getFTPUrl();
            int ftp_port = Integer.valueOf(GlobalConfig.getFTPPort());
            String ftp_user = GlobalConfig.getFTPUser();
            String ftp_password = GlobalConfig.getFTPPwd();
            FtpClient ftp = new FtpClient(ftp_url, ftp_port, ftp_user, ftp_password);
            ftp.ftpLogin();
            //上传原图片
            ftp.uploadFile(new File(outputDirectory + fileName), imgPath);
            ftp.ftpLogOut();
            ftp.ftpLogin();//此处登陆两次是为了保证第二次文件拷贝时目录正确，由于有ftpClient.changeWorkingDirectory(dir);导致第二次拷贝文件时是在第一次目录的基础上进行叠加
            //上传缩略图
            ftp.uploadFile(new File(outputThumbDirectory + fileName), imgThumbPath);
            ftp.ftpLogOut();

            ftp.ftpLogin();
            //最后删除临时文件 包括原图和缩略图
            try {
                imgFile.delete();
                new File(outputDirectory + fileName).delete();
                new File(outputThumbDirectory + fileName).delete();
            } catch (Exception e) {
               logger.error(e.toString());
                e.printStackTrace();
            }

            return imgPath + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * 项目图片上传，上传后文件名重命名，按照指定的尺寸进行压缩，并按参数的尺寸生成缩略图
     * 1、例如源文件保存在/userfile/wumBuddhism/20141024/20141024155312804.JPG，那么缩略图地址为/thumbs/userfile/wumBuddhism/20141024/20141024155312804.JPG
     * 2、针对移动端的地址分别为：/mobilefile/userfile/wumBuddhism/20141024/20141024155312804.JPG，缩略图地址为/mobilefile/thumbs/userfile/wumBuddhism/20141024/20141024155312804.JPG
     *
     * @param imgName 已经上传到WEB服务器的临时图片名称
     * @param x1      裁剪起始的X坐标
     * @param y1      裁剪起始的Y坐标
     * @param cw      裁剪后的宽度
     * @param ch      裁剪后的高度
     * @return 文件所在服务器的完整目录，如/userfile/wumBuddhism/20141024/20141024155312804.JPG
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected String cutProjectImageWithCompress(HttpServletRequest request, String imgName, int width, int height, int x1, int y1, int cw, int ch) {
        if (StringUtils.isBlank(imgName)) {
            return null;
        }
        try {
            File imgFile = new File(getImageFileStorePath(request) + imgName);
            String fileName = imgFile.getName();// getOriginalFilename();
            // 获取文件扩展名
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            // 设置上传文件名
            SimpleDateFormat fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            // 源文件重命名,文件命名格式:yyyyMMddHHmmssSSS
            fileName = fileFormatter.format(new Date()) + "." + ext;

            //如果文件不是图片，则不上传
            if (!"jpg".equalsIgnoreCase(ext) && !"jpeg".equalsIgnoreCase(ext)
                    && !"png".equalsIgnoreCase(ext) && !"gif".equalsIgnoreCase(ext)
                    && !"bmp".equalsIgnoreCase(ext)) {
                return null;
            }

            String outputDirectory = System.getProperty("java.io.tmpdir") + "uploadImageFile/";
            File file = new File(outputDirectory);
            if (!file.exists()) {
                if (!file.mkdir()) {//生成临时目录
                    return null;
                }
            }
            ImageUtils imageUtils = new ImageUtils();
            //裁剪图片
            if (cw > 0 && ch > 0) {
                boolean isCut = imageUtils.cutImage(getImageFileStorePath(request), outputDirectory, imgName, fileName, x1, y1, cw, ch);
                if (!isCut) {
                    return null;
                }
            } else {
                //不裁剪，直接拷贝
                FileUtils.copyFileCover(getImageFileStorePath(request) + imgName, outputDirectory + fileName, true);
            }

            //PC端 - 创建压缩后图片存放的临时目录
            String outputCompDirectory = System.getProperty("java.io.tmpdir") + "uploadImageCompFile/";
            File fileComp = new File(outputCompDirectory);
            if (!fileComp.exists()) {
                if (!fileComp.mkdir()) {
                    return null;
                }
            }
            boolean isCompressed = false;//图片是否被压缩

            boolean result = imageUtils.compressPic(outputDirectory, outputCompDirectory, fileName, fileName, width, height, true);
            if (result) {
                isCompressed = true;
            }

            //创建缩略图存放的临时目录
            String outputThumbDirectory = System.getProperty("java.io.tmpdir") + "uploadImageThumbFile/";
            File fileThumb = new File(outputThumbDirectory);
            if (!fileThumb.exists()) {
                if (!fileThumb.mkdir()) {//生成临时目录
                    return null;
                }
            }
            imageUtils.compressPic(outputDirectory, outputThumbDirectory, fileName, fileName, width, height, true);

            String imgPath = GlobalConfig.getImagePath() + DateUtils.getNoSpSysDateString() + "/";
            String imgThumbPath = GlobalConfig.getImageThumbPath() + DateUtils.getNoSpSysDateString() + "/";

            String ftp_url = GlobalConfig.getFTPUrl();
            int ftp_port = Integer.valueOf(GlobalConfig.getFTPPort());
            String ftp_user = GlobalConfig.getFTPUser();
            String ftp_password = GlobalConfig.getFTPPwd();
            FtpClient ftp = new FtpClient(ftp_url, ftp_port, ftp_user, ftp_password);
            ftp.ftpLogin();
            //上传原图片
            if (isCompressed) {
                ftp.uploadFile(new File(outputCompDirectory + fileName), imgPath);
            } else {
                ftp.uploadFile(imgFile, imgPath);
            }
            ftp.ftpLogOut();
            ftp.ftpLogin();//此处登陆两次是为了保证第二次文件拷贝时目录正确，由于有ftpClient.changeWorkingDirectory(dir);导致第二次拷贝文件时是在第一次目录的基础上进行叠加
            //上传缩略图
            ftp.uploadFile(new File(outputThumbDirectory + fileName), imgThumbPath);
            ftp.ftpLogOut();

            ftp.ftpLogin();

            //最后删除临时文件 包括原图、压缩图和缩略图
            try {
                imgFile.delete();

                new File(outputDirectory + fileName).delete();
                new File(outputCompDirectory + fileName).delete();
                new File(outputThumbDirectory + fileName).delete();
            } catch (Exception ignored) {

            }

            return imgPath + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * 获取图片文件上传到服务器应用下的全路径名
     *
     * @param request 请求
     */
    protected String getImageFileStorePath(HttpServletRequest request) {
        // 获取工程所在部署的路径
        return request.getSession().getServletContext().getRealPath("/") + IMAGES_PATH;
    }

}
