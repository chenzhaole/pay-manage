package com.sys.admin.modules.portal.web;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.exception.BusinessException;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.portal.dmo.PortalInfo;
import com.sys.admin.modules.portal.service.AgencyService;
import com.sys.admin.modules.sys.entity.Office;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 门户基本信息
 */
@SuppressWarnings("MVCPathVariableInspection")
@Controller
@RequestMapping(value = "${adminPath}/portal/info")
public class PortalInfoController extends BaseController {

    @Autowired
    private AgencyService agencyService;


    /**
     * 图片保存地址
     */
    private static final String FTP_DIR = "/userfiles/agency/file/";


    /**
     * 获取页面对象
     * @param id 对象ID
     * @return 返回对象
     */
    @ModelAttribute("office")
    public Office get(@RequestParam(required = false) Long id) {
        if (id != null) {
            return agencyService.getOfficeById(id);
        } else {
            return new Office();
        }
    }

    /**
     * 列表页展示
     * @param model 页面传参对象
     * @return 页面
     */
    @RequiresPermissions("portal:info:view")
    @RequestMapping(value = {"list", ""})
    public String list(Model model) {
        User user = UserUtils.getUser();
        Office office = agencyService.getOfficeById(user.getOffice().getId());
        if (office.getId() != null) {
            PortalInfo portalInfo = agencyService.getInfoById(office.getId());
            if (portalInfo == null) {
                office.setPortalInfo(new PortalInfo());
            } else {
                office.setPortalInfo(portalInfo);
            }
        } else {
            office.setPortalInfo(new PortalInfo());
        }
        model.addAttribute("office", office);
        model.addAttribute("imgServer", GlobalConfig.getImgServer());
        return "modules/portal/infoForm";
    }

    /**
     * 保存修改操作
     * @param office form表单对象
     * @param model 页面传参对象
     * @param redirectAttributes 跳转参数
     * @param companyLogoFile 上传的logo
     * @param qrcodeFile 上传的二维码
     * @return 跳转/重定向页面地址
     */
    @RequiresPermissions("portal:info:edit")
    @RequestMapping(value = "save")
    public String save(Office office, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request,
                       @RequestParam(value = "companyLogoFile", required = false) MultipartFile companyLogoFile,
                       @RequestParam(value = "companyIconFile", required = false) MultipartFile companyIconFile,
                       @RequestParam(value = "qrcodeFile", required = false) MultipartFile qrcodeFile) {
        PortalInfo portalInfo = office.getPortalInfo();
        if (companyIconFile != null && StringUtils.isNotBlank(companyIconFile.getOriginalFilename())) {
            try {
                String originalFileName = companyIconFile.getOriginalFilename();
                // 获取文件扩展名
                String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
                //如果文件不是图片，则不上传
                if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)
                        || "png".equalsIgnoreCase(ext) || "gif".equalsIgnoreCase(ext)
                        || "bmp".equalsIgnoreCase(ext)) {
                    SimpleDateFormat fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    String fileName = fileFormatter.format(new Date()) + new Random().nextInt(1000) + "." + ext;
                    String dir = getImageFileStorePath(request);
                    File file = new File(dir);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    File tempFile = new File(dir + fileName);
                    if (!tempFile.createNewFile()) {
                        return null;
                    }
                    companyIconFile.transferTo(tempFile);
                    BufferedImage buff = ImageIO.read(tempFile);
                    // 判断图片大小
                    if(buff.getWidth() != 200 || buff.getHeight() != 200){
                        addMessage(model, "保存失败，手机端图标尺寸不正确");
                        return list(model);
                    }

//                    String fileDir = FTP_DIR + DateUtils.formatDate(new Date(), "yyyyMMdd/");
//                    FtpClient ftp = new FtpClient(
//                            GlobalConfig.getFTPUrl(),
//                            Integer.valueOf(GlobalConfig.getFTPPort()),
//                            GlobalConfig.getFTPUser(),
//                            GlobalConfig.getFTPPwd());
//                    ftp.ftpLogin();
//                    ftp.uploadFile(tempFile, fileDir);
//                    ftp.ftpLogOut();
//                    portalInfo.setCompanyIcon(fileDir + fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("手机端图标上传失败" + e.getMessage());
            }
        }

        /**
         * 文件上传FTP
         */
        if (companyLogoFile != null && StringUtils.isNotBlank(companyLogoFile.getOriginalFilename())) {
            String fileName = companyLogoFile.getOriginalFilename();
            // 获取文件扩展名
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            //如果文件不是图片，则不上传
            if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)
                    || "png".equalsIgnoreCase(ext) || "gif".equalsIgnoreCase(ext)
                    || "bmp".equalsIgnoreCase(ext)) {
                String filePath = uploadFile(companyLogoFile);
                portalInfo.setCompanyLogo(filePath);
            }
        }

        if (qrcodeFile != null && StringUtils.isNotBlank(qrcodeFile.getOriginalFilename())) {
            String fileName = qrcodeFile.getOriginalFilename();
            // 获取文件扩展名
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            //如果文件不是图片，则不上传
            if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)
                    || "png".equalsIgnoreCase(ext) || "gif".equalsIgnoreCase(ext)
                    || "bmp".equalsIgnoreCase(ext)) {
                String filePath = uploadFile(qrcodeFile);
                portalInfo.setWechatBarcode(filePath);
            }
        }

        agencyService.save(office, portalInfo);

        addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
        return "redirect:" + GlobalConfig.getAdminPath() + "/portal/info/list";
    }

    /**
     * 校验前处理
     * @param object 对象
     * @throws BusinessException
     */
    @Override
	protected void beforeBeanValidator(Object object) throws BusinessException {
        Office office = (Office) object;
        PortalInfo portalInfo = office.getPortalInfo();

        if (office.getParent() == null) {
            office.setParent(new Office());
            office.getParent().setId(0L);
            office.getParent().setParentIds("");
        }
//        if ((office.getId() == null && portalInfo.getOfficeId() == null) || ConstUtils.PORTAL_CONST.STATUS_FAIL.equals(portalInfo.getStatus())) {
//            portalInfo.setStatus(ConstUtils.PORTAL_CONST.STATUS_CHECK);
//        }
        if (StringUtils.isBlank(office.getGrade())) {
            String parentGrade = agencyService.getOfficeById(office.getParent().getId()).getGrade();
            String grade = "" + (Integer.valueOf(parentGrade) + 1);
            if (Integer.valueOf(parentGrade) >= 3) {
                throw new BusinessException("微门户层级过多!");
            }
            office.setGrade(grade);
        }
        office.setType("1");
    }

    /**
     * 校验后处理
     * @param object 对象
     * @throws BusinessException
     */
    @Override
	protected void afterBeanValidator(Object object) throws BusinessException {
    }
}
