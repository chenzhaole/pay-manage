package com.sys.admin.common.config;

import com.sys.admin.common.utils.CacheUtils;
import com.sys.admin.common.utils.SpringContextHolder;
import com.sys.admin.modules.sys.dmo.SysConfig;
import com.sys.admin.modules.sys.service.SystemConfigService;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 全局配置类
 */
public class GlobalConfig {


    /**
     * 获取配置
     */
    public static String getConfig(String key) {
        String res = Global.getConfig(key);
        if (res == null) {
            SystemConfigService srv = SpringContextHolder.getBean(SystemConfigService.class);
            List<SysConfig> list = srv.getAll();
            for (SysConfig config : list) {
                CacheUtils.putConfigCache(config.getConfigName(), config.getConfigValue());
                if (key.equals(config.getConfigName())) {
                    res = config.getConfigValue();
                }
            }
        }

        return res;
    }

    /////////////////////////////////////////////////////////

    public static String getAdminPath() {
        return getConfig("adminPath");
    }

    public static String getFrontPath() {
        return getConfig("frontPath");
    }

    public static String getUrlSuffix() {
        return getConfig("urlSuffix");
    }

    public static String getFTPUrl() {
        return getConfig("ftp.ip");
    }

    public static String getFTPPort() {
        return getConfig("ftp.port");
    }

    public static String getFTPUser() {
        return getConfig("ftp.user");
    }

    public static String getFTPPwd() {
        return getConfig("ftp.password");
    }

    public static String getImagePath() {
        return getConfig("image.path");
    }

    public static String getImageThumbPath() {
        return getConfig("image.thumb.path");
    }

    public static String getImgServer() {
        return getConfig("image.server");
    }

    public static String getImgThumbServer() {
        return getConfig("image.thumb.server");
    }

    public static String getImagePathMB() {
        return getConfig("image.path.mb");
    }

    public static String getImageThumbPathMB() {
        return getConfig("image.thumb.path.mb");
    }

    public static String getImgServerMB() {
        return getConfig("image.server.mb");
    }

    public static String getImgThumbServerMB() {
        return getConfig("image.thumb.server.mb");
    }

    public static String getWatermarkSwitch() {
        return getConfig("proj.image.watermark.switch");
    }

    public static String getCustomRestPwdMsg() {
        return getConfig("custom.restpwd.msg");
    }

    public static String getCustomDefaultPwd() {
        return getConfig("custom.default.pwd");
    }

    public static int getPageSize(){
        if (StringUtils.isNumeric(getConfig("page.pageSize"))) {
            return Integer.parseInt(getConfig("page.pageSize"));
        }
        return 20;
    }

    public static long getSMSIntervalDate(){
        if (StringUtils.isNumeric(getConfig("sms.intervalDate.minutes"))) {
            return Long.parseLong(getConfig("sms.intervalDate.minutes"))*60;
        }
        return 60;
    }

    public static long getSMSExpiryDate(){
        if (StringUtils.isNumeric(getConfig("sms.expiryDate.minutes"))) {
            return Long.parseLong(getConfig("sms.expiryDate.minutes"))*60;
        }
        return 30*60;
    }

    public static String getMaxSellNum() {
        return getConfig("book.maxSell.number");
    }

}
