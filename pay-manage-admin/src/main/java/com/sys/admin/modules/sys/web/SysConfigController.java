package com.sys.admin.modules.sys.web;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.dmo.SysConfig;
import com.sys.admin.modules.sys.dmo.SysConfigCategoryEnum;
import com.sys.admin.modules.sys.service.SystemConfigService;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 *
 */
@SuppressWarnings("MVCPathVariableInspection")
@Controller
@RequestMapping(value = "${adminPath}/sys/config")
public class SysConfigController extends BaseController {
    @Autowired
    private SystemConfigService systemConfigService;

    @RequiresPermissions("sys:config:view")
    @RequestMapping(value = {"list", ""})
    public String list(Model model) {
        try {
            List<SysConfig> resultList = new ArrayList<SysConfig>();
            List<SysConfig> sysConfigList = systemConfigService.getAll();
            if (sysConfigList != null && sysConfigList.size() > 0) {
                for (SysConfig sysConfig : sysConfigList) {
                    boolean isExcept = false;
                    for (SysConfigCategoryEnum category : SysConfigCategoryEnum.values()) {
                        if (sysConfig.getCategory().equals(category.getValue())) {
                            isExcept = true;
                            break;
                        }
                    }
                    if (!isExcept) {
                        resultList.add(sysConfig);
                    }
                }
            }

            model.addAttribute("list", resultList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "modules/sys/sysConfig";
    }

    @RequiresPermissions("sys:config:view")
    @RequestMapping(value = {"save"})
    public String save(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            String categoryId = request.getParameter("categoryId");

            List<SysConfig> sysConfigList = systemConfigService.getConfigListByCategory(categoryId);
            for (SysConfig sysConfig : sysConfigList) {
                String resourceValue = request.getParameter(sysConfig
                        .getConfigName());
                sysConfig.setConfigValue(resourceValue);
                systemConfigService.updateByPrimaryKey(sysConfig);
            }
            addMessage(redirectAttributes, "保存成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addMessage(redirectAttributes, "保存失败：" + e.getMessage());
        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/config/list?objType=";
    }

    /**
     * 打开网站统计脚本编辑页面
     *
     * @param model 页面传参
     * @return 编辑页面地址
     */
    @RequiresPermissions("sys:config:statScript:view")
    @RequestMapping(value = {"toEditStatScript"})
    public String toEditStatScript(Model model) {
        try {
            List<SysConfig> sysConfigList = systemConfigService.getConfigListByCategory("script");
            model.addAttribute("sysConfigList", sysConfigList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addMessage(model, e.getMessage());
        }
        return "modules/sys/statScriptConfig";
    }

    @RequiresPermissions("sys:config:statScript:view")
    @RequestMapping(value = {"saveStatScript"})
    public String saveStatScript(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            List<SysConfig> sysConfigList = systemConfigService.getConfigListByCategory("script");
            for (SysConfig sysConfig : sysConfigList) {
                String resourceValue = request.getParameter(sysConfig
                        .getConfigName());
                sysConfig.setConfigValue(resourceValue);
                systemConfigService.updateByPrimaryKey(sysConfig);
            }
            addMessage(redirectAttributes, "保存成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            addMessage(redirectAttributes, "保存失败：" + e.getMessage());
        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/config/toEditStatScript";
    }

    /**
     * 打开系统配置编辑界面
     *
     * @param sysConfig 配置对象
     * @param model     页面传参
     * @return 编辑页面
     */
    @RequiresPermissions("sys:config:view")
    @RequestMapping(value = "form")
    public String form(SysConfig sysConfig, Model model) {
        if (sysConfig != null && StringUtils.isNotBlank(sysConfig.getConfigName())) {
            sysConfig = systemConfigService.selectByPrimaryKey(sysConfig.getConfigName());
        }
        model.addAttribute("sysConfig", sysConfig);
        return "modules/sys/sysConfigForm";
    }

    /**
     * 保存系统配置
     *
     * @param sysConfig          系统配置对象
     * @param oldConfigName      原有名称
     * @param model              页面传参
     * @param redirectAttributes 重定向参数
     * @return 重定向页面
     */
    @RequiresPermissions("sys:config:edit")
    @RequestMapping(value = "saveForm")
    public String saveForm(SysConfig sysConfig, String oldConfigName, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, sysConfig)) {
            return form(sysConfig, model);
        }
        if (StringUtils.isBlank(sysConfig.getCategory())) {
            sysConfig.setCategory("system");
        }
        if (StringUtils.isNotBlank(oldConfigName)) {//修改
            SysConfig sc = systemConfigService.selectByPrimaryKey(sysConfig.getConfigName());
            if (sc != null) {
                if (!oldConfigName.equals(sysConfig.getConfigName())) {
                    addMessage(model, "系统参数名称已存在！");
                    return form(sysConfig, model);
                }
            }
            systemConfigService.updateByPrimaryKey(sysConfig, oldConfigName);
        } else {//新增
            SysConfig sc = systemConfigService.selectByPrimaryKey(sysConfig.getConfigName());
            if (sc != null) {
                addMessage(model, "系统参数名称已存在！");
                return form(sysConfig, model);
            }
            systemConfigService.save(sysConfig);
        }

        addMessage(redirectAttributes, "保存系统参数'" + sysConfig.getDescription() + "'成功");
        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/config/?repage";
    }

    /**
     * 删除配置
     *
     * @param configName         配置ID
     * @param redirectAttributes 重定向参数
     * @return 跳转/重定向页面地址
     */
    @RequiresPermissions("sys:config:edit")
    @RequestMapping(value = "/delete")
    public String delete(String configName, RedirectAttributes redirectAttributes) {
        if (configName != null) {
            if (systemConfigService.deleteByPrimaryKey(configName) > 0) {
                addMessage(redirectAttributes, "删除成功");
            } else {
                addMessage(redirectAttributes, "删除失败，没有找到对应的配置信息");
            }

        } else {
            addMessage(redirectAttributes, "删除失败，请检查参数");
        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/config/?repage";
    }
}
