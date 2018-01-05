package com.sys.admin.modules.sys.service.impl;

import com.sys.admin.common.exception.SystemException;
import com.sys.admin.common.service.BaseService;
import com.sys.admin.modules.sys.dmo.SysConfig;
import com.sys.admin.modules.sys.dmo.SysConfigExample;
import com.sys.admin.modules.sys.mapper.SysConfigMapper;
import com.sys.admin.modules.sys.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统参数接口实现类
 */
@Service
@Transactional(readOnly = true)
public class SystemConfigServiceImpl extends BaseService implements SystemConfigService {
    @Autowired
    private SysConfigMapper mapper;

    /**
     * 获取定时任务锁
     *
     * @return false表示获取失败 true表示获取成功
     */
    
    @Override
	@Transactional(readOnly = false)
    public boolean gainTimerLock() {
        return mapper.gainTimerLock() == 1;
    }

    /**
     * 释放定时任务锁
     *
     * @return false表示释放失败 true表示释放成功
     */
    
    @Override
	@Transactional(readOnly = false)
    public boolean releaseTimerLock() {
        return mapper.releaseTimerLock() == 1;
    }

    
    @Override
	public SysConfig getConfigByKey(String key) {
        return mapper.selectByPrimaryKey(key);
    }

    
    @Override
	public String getConfigValue(String key) {
        SysConfig config = getConfigByKey(key);
        if (config != null) {
            return config.getConfigValue();
        }
        return null;
    }

    
    @Override
	public void setConfigValue(String key, String value) {
        SysConfig config = getConfigByKey(key);
        if (config != null) {
            config.setConfigValue(value);
            mapper.updateByPrimaryKeySelective(config);
        } else {
            throw new SystemException("没有当前配置!");
        }
    }

    
    @Override
	public List<SysConfig> getAll() {
        return mapper.getAll();
    }


    
    @Override
	public List<String> getCategories() {
        return mapper.getCategorys();
    }

    
    @Override
	public List<SysConfig> getConfigListByCategory(String category) {
        SysConfigExample configExample = new SysConfigExample();
        configExample.createCriteria().andCategoryEqualTo(category);
        return mapper.selectByExample(configExample);
    }

    
    @Override
	public
    int updateByPrimaryKey(SysConfig record){
        return mapper.updateByPrimaryKey(record);
    }

    
    @Override
	public int updateByPrimaryKey(SysConfig record, String oldConfigName) {
        return mapper.updateByConfigName(record, oldConfigName);
    }

    
    @Override
	public void save(SysConfig record) {
        mapper.insertSelective(record);
    }

    
    @Override
	public SysConfig selectByPrimaryKey(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    
    @Override
	public int deleteByPrimaryKey(String id) {
        return mapper.deleteByPrimaryKey(id);
    }
}
