package com.sys.admin.modules.sys.service.impl;

import com.sys.admin.modules.sys.dmo.Provider;
import com.sys.admin.modules.sys.dmo.ProviderExample;
import com.sys.admin.modules.sys.mapper.ProviderMapper;
import com.sys.admin.modules.sys.service.ProviderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分平台/提供商数据操作接口实现类
 */
@Service
public class ProviderServiceImpl implements ProviderService {
    @Autowired
    private ProviderMapper providerMapper;

    
    @Override
	public int countByExample(ProviderExample example) {
        return providerMapper.countByExample(example);
    }

    
    @Override
	public int deleteByExample(ProviderExample example) {
        return providerMapper.deleteByExample(example);
    }

    
    @Override
	public int deleteByPrimaryKey(Long id) {
        return providerMapper.deleteByPrimaryKey(id);
    }

    
    @Override
	public int insert(Provider record) {
        return providerMapper.insert(record);
    }

    
    @Override
	public int insertSelective(Provider record) {
        return providerMapper.insertSelective(record);
    }

    
    @Override
	public List<Provider> selectByExample(ProviderExample example) {
        return providerMapper.selectByExample(example);
    }

    
    @Override
	public Provider selectByPrimaryKey(Long id) {
        return providerMapper.selectByPrimaryKey(id);
    }

    
    @Override
	public Provider selectByProviderId(Integer providerId) {
        ProviderExample example = new ProviderExample();
        example.createCriteria().andProviderIdEqualTo(providerId);
        List<Provider> providerList = providerMapper.selectByExample(example);
        if (providerList != null && providerList.size() > 0) {
            return providerList.get(0);
        }
        return null;
    }

    
    @Override
	public int updateByExampleSelective(@Param("record") Provider record, @Param("example") ProviderExample example) {
        return providerMapper.updateByExampleSelective(record, example);
    }

    
    @Override
	public int updateByExample(@Param("record") Provider record, @Param("example") ProviderExample example) {
        return providerMapper.updateByExample(record, example);
    }

    
    @Override
	public int updateByPrimaryKeySelective(Provider record) {
        return providerMapper.updateByPrimaryKeySelective(record);
    }

    
    @Override
	public int updateByPrimaryKey(Provider record) {
        return providerMapper.updateByPrimaryKey(record);
    }
}
