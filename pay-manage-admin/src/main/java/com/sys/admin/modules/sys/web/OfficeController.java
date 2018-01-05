package com.sys.admin.modules.sys.web;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.exception.BusinessException;
import com.sys.admin.common.utils.*;
import com.sys.admin.common.web.BaseController;
//import com.sys.admin.modules.check.dmo.CheckFailReason;
//import com.sys.admin.modules.check.service.CheckFailReasonService;
//import com.sys.admin.modules.check.service.CheckService;
import com.sys.admin.modules.portal.dmo.PortalInfo;
import com.sys.admin.modules.portal.service.AgencyService;
//import com.sys.admin.modules.station.utils.StationUtils;
import com.sys.admin.modules.sys.entity.Office;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.util.DateUtils;
import com.sys.common.util.Encodes;
import com.sys.common.util.FtpClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 机构Controller
 *
 */
@SuppressWarnings("MVCPathVariableInspection")
@Controller
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeController extends BaseController {

    @Autowired
    private AgencyService agencyService;

//    @Autowired
//    private CheckFailReasonService checkFailReasonService;
//
//    @Autowired
//    private CheckService checkService;

    private static final String FTP_DIR = "/userfiles/agency/file/";

    private static final String CHECK_FAIL_REASON_TYPE = "office";

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
     * 表页展示
     * @param office 查询参数
     * @param model 页面传参对象
     * @return 页面
     */
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = {"list", ""})
    public String list(Office office, Model model) {
        User user = UserUtils.getUser();
        if (user.isAdmin()) {
            office.setId(1L);
        } else {
            office.setId(user.getOffice().getId());
        }
        model.addAttribute("office", office);
        List<Office> list = Lists.newArrayList();
        List<Office> sourceList = agencyService.findAllOffice();
        Office.sortList(list, sourceList, office.getId());
        for (Office o : list) {
            PortalInfo info = agencyService.getInfoById(o.getId());
            o.setPortalInfo(info);
        }
        model.addAttribute("list", list);
        return "modules/sys/officeList";
    }

    /**
     * form表单页
     * @param office form表单对象
     * @param model 页面传参对象
     * @return 页面
     */
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "form")
    public String form(Office office, Model model) {
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            office.setParent(user.getOffice());
        }
        office.setParent(agencyService.getOfficeById(office.getParent().getId()));
        if (office.getArea() == null) {
            office.setArea(user.getOffice().getArea());
        }
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

        List<Long> list = agencyService.getRelStationIds(office.getId());
        String ids = "";
        for (Long i : list) {
            ids += "," + i;
        }
        if (StringUtils.isNotBlank(ids)) {
            ids = ids.substring(1);
        }
        model.addAttribute("office", office);
        model.addAttribute("imgServer", GlobalConfig.getImgServer());
//        model.addAttribute("treeData", StationUtils.stationTreeData());
        model.addAttribute("stationIds", ids);
        return "modules/sys/officeForm";
    }

    /**
     * 保存修改操作
     * @param office form表单对象
     * @param model 页面传参对象
     * @param redirectAttributes 跳转参数
     * @param companyLogoFile logo文件
     * @param qrcodeFile 二维码文件
     * @param stationIds 站点ID
     * @return 跳转/重定向页面地址
     */
    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "save")
    public String save(Office office, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request,
                       @RequestParam(value = "companyLogoFile", required = false) MultipartFile companyLogoFile,
                       @RequestParam(value = "companyIconFile", required = false) MultipartFile companyIconFile,
                       @RequestParam(value = "qrcodeFile", required = false) MultipartFile qrcodeFile,
                       @RequestParam(value = "stationIds", required = false) String stationIds) {
        if (!beanValidator(model, office)) {
            return form(office, model);
        }
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
                        return form(office, model);
                    }

                    String fileDir = FTP_DIR + DateUtils.formatDate(new Date(), "yyyyMMdd/");
                    FtpClient ftp = new FtpClient(
                            GlobalConfig.getFTPUrl(),
                            Integer.valueOf(GlobalConfig.getFTPPort()),
                            GlobalConfig.getFTPUser(),
                            GlobalConfig.getFTPPwd());
                    ftp.ftpLogin();
                    ftp.uploadFile(tempFile, fileDir);
                    ftp.ftpLogOut();
                    portalInfo.setCompanyIcon(fileDir + fileName);
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

        Long[] ids;
        if (StringUtils.isNotBlank(stationIds)) {
            String[] strIdsArr = stationIds.split(",");
            ids = new Long[strIdsArr.length];
            for (int i = 0, len = strIdsArr.length; i < len; i++) {
                ids[i] = Long.valueOf(strIdsArr[i].trim());
            }
        } else {
            ids = new Long[0];
        }

        agencyService.save(office, portalInfo, ids);
        if (ConstUtils.PORTAL_CONST.STATUS_CHECK.equals(portalInfo.getStatus())) {
            try {
//                checkService.createCheckRecord(office.getId() + "", CHECK_FAIL_REASON_TYPE, UserUtils.getUser().getOffice().getId());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        addMessage(redirectAttributes, "保存微门户'" + office.getName() + "'成功");
        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/office/";
    }

    /**
     * 删除操作
     * @param id 删除的ID
     * @param redirectAttributes 跳转参数
     * @return 跳转/重定向页面地址
     */
    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "delete")
    public String delete(Long id, RedirectAttributes redirectAttributes) {
        if (Office.isRoot(id)) {
            addMessage(redirectAttributes, "删除微门户失败, 不允许删除顶级微门户或编号空");
        } else {
            agencyService.delete(id);
            addMessage(redirectAttributes, "删除微门户成功");
        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/office/";
    }

    /**
     * 状态修改
     * @param id 对象ID
     * @param redirectAttributes 跳转参数
     * @return 跳转/重定向页面地址
     */
    @RequiresPermissions("sys:office:edit")
    @RequestMapping(value = "changeStatus")
    public String changeStatus(Long id, RedirectAttributes redirectAttributes) {
        if (Office.isRoot(id)) {
            addMessage(redirectAttributes, "修改状态微门户失败, 不允许修改顶级微门户的状态");
        } else {
            agencyService.changeStatus(id);
            addMessage(redirectAttributes, "微门户状态修改成功");
        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/office/";
    }

    /**
     * 审核页面
     * @param office 审核对象ID
     * @param model 页面传参对象
     * @return 页面
     */
    @RequiresPermissions("sys:office:check")
    @RequestMapping(value = "checkPage")
    public String checkPage(Office office, Model model) {
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            office.setParent(user.getOffice());
        }
        office.setParent(agencyService.getOfficeById(office.getParent().getId()));
        if (office.getArea() == null) {
            office.setArea(user.getOffice().getArea());
        }
        if (office.getPortalInfo() == null) {
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
        }
//        List<CheckFailReason> reasons = checkFailReasonService.getCheckFailReasonListByObjType(CHECK_FAIL_REASON_TYPE);
        model.addAttribute("office", office);
        model.addAttribute("imgServer", GlobalConfig.getImgServer());
//        model.addAttribute("reasonList", reasons);
        return "modules/sys/officeCheck";
    }

    /**
     * 审核操作
     * @param officeId 审核对象ID
     * @param checkResult 审核结果
     * @param failReason 失败原因
     * @param redirectAttributes 跳转参数
     * @return 跳转/重定向页面地址
     */
    @RequiresPermissions("sys:office:check")
    @RequestMapping(value = "check")
    public String check(Long officeId, String checkResult, String failReason, RedirectAttributes redirectAttributes) {
        User user = UserUtils.getUser();
        PortalInfo portalInfo = agencyService.getInfoById(officeId);
        portalInfo.setStatus(checkResult);
        try {
//            if (ConstUtils.PORTAL_CONST.STATUS_NORMAL.equals(checkResult)) {
//                checkService.checkSuccess(officeId + "", CHECK_FAIL_REASON_TYPE, user.getId(), user.getOffice().getId());
//            } else if (ConstUtils.PORTAL_CONST.STATUS_FAIL.equals(checkResult)) {
//                checkService.checkFail(officeId + "", CHECK_FAIL_REASON_TYPE, user.getId(), user.getOffice().getId(), failReason);
//            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        agencyService.save(portalInfo);
        addMessage(redirectAttributes, "操作成功");
        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/office/";
    }

    /**
     * 门户树形结构
     * @param extId 排除的ID
     * @param type 门户类型
     * @param grade 门户级别
     * @param response 响应参数
     * @return 返回数据
     */
    @RequiresUser
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) Long extId, @RequestParam(required = false) Long type,
                                              @RequestParam(required = false) Long grade, HttpServletResponse response) {
        response.setContentType("application/json; charset=UTF-8");
        List<Map<String, Object>> mapList = Lists.newArrayList();
//		User user = UserUtils.getUser();
        List<Office> list = agencyService.findAllOffice();
        for (Office e : list) {
            if ((extId == null || (!extId.equals(e.getId()) && !e.getParentIds().contains("," + extId + ",")))
                    && (type == null || (Integer.parseInt(e.getType()) <= type.intValue()))
                    && (grade == null || (Integer.parseInt(e.getGrade()) <= grade.intValue()))) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
//				map.put("pId", !user.isAdmin() && e.getId().equals(user.getOffice().getId())?0:e.getParent()!=null?e.getParent().getId():0);
                map.put("pId", e.getParent() != null ? e.getParent().getId() : 0);
                map.put("name", Encodes.unescapeHtml(e.getName()));
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 校验前处理
     * @param object
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
        if ((office.getId() == null && portalInfo.getOfficeId() == null) || ConstUtils.PORTAL_CONST.STATUS_FAIL.equals(portalInfo.getStatus())) {
            portalInfo.setStatus(ConstUtils.PORTAL_CONST.STATUS_CHECK);
        }
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
     * @param object
     * @throws BusinessException
     */
    @Override
	protected void afterBeanValidator(Object object) throws BusinessException {
    }

}
