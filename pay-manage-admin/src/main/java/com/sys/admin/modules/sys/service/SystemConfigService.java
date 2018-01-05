package com.sys.admin.modules.sys.service;

import com.sys.admin.modules.sys.dmo.SysConfig;

import java.util.List;

/**
 * 系统参数接口
 */
public interface SystemConfigService {

    /**
     * 获取定时任务锁
     *
     * @return false表示获取失败 true表示获取成功
     */
    boolean gainTimerLock();

    /**
     * 释放定时任务锁
     *
     * @return false表示释放失败 true表示释放成功
     */
    boolean releaseTimerLock();

    /**
     * 获取配置
     */
    SysConfig getConfigByKey(String key);

    /**
     * 获取配置的值
     */
    String getConfigValue(String key);

    /**
     * 给已存在的设置配置的新值
     */
    void setConfigValue(String key, String value);

    /**
     * 获取所有配置
     */
    List<SysConfig> getAll();

    /**
     * 获取所有系统配置分类
     */
    List<String> getCategories();

    List<SysConfig> getConfigListByCategory(String category);

    int updateByPrimaryKey(SysConfig record);

    int updateByPrimaryKey(SysConfig record, String oldConfigName);

    void save(SysConfig record);

    SysConfig selectByPrimaryKey(String id);

    int deleteByPrimaryKey(String id);
}
