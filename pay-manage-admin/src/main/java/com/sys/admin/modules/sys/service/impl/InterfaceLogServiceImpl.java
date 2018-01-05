package com.sys.admin.modules.sys.service.impl;

import com.sys.admin.modules.sys.dmo.InterfaceLog;
import com.sys.admin.modules.sys.dmo.InterfaceLogExample;
import com.sys.admin.modules.sys.mapper.InterfaceLogMapper;
import com.sys.admin.modules.sys.service.InterfaceLogService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 第三方接口调用日志相关业务接口实现类
 */
@Service
public class InterfaceLogServiceImpl implements InterfaceLogService {
    @Autowired
    private InterfaceLogMapper interfaceLogMapper;

    
    @Override
	public int countByExample(InterfaceLogExample example) {
        return interfaceLogMapper.countByExample(example);
    }

    
    @Override
	public int deleteByExample(InterfaceLogExample example) {
        return interfaceLogMapper.deleteByExample(example);
    }

    
    @Override
	public int deleteByPrimaryKey(Long id) {
        return interfaceLogMapper.deleteByPrimaryKey(id);
    }

    
    @Override
	public int insert(InterfaceLog record) {
        return interfaceLogMapper.insert(record);
    }

    
    @Override
	public int insertSelective(InterfaceLog record) {
        return interfaceLogMapper.insertSelective(record);
    }

    
    @Override
	public List<InterfaceLog> selectByExample(InterfaceLogExample example) {
        return interfaceLogMapper.selectByExample(example);
    }

    
    @Override
	public InterfaceLog selectByPrimaryKey(Long id) {
        return interfaceLogMapper.selectByPrimaryKey(id);
    }

    
    @Override
	public int updateByExampleSelective(@Param("record") InterfaceLog record, @Param("example") InterfaceLogExample example) {
        return interfaceLogMapper.updateByExampleSelective(record, example);
    }

    
    @Override
	public int updateByExample(@Param("record") InterfaceLog record, @Param("example") InterfaceLogExample example) {
        return interfaceLogMapper.updateByExample(record, example);
    }

    
    @Override
	public int updateByPrimaryKeySelective(InterfaceLog record) {
        return interfaceLogMapper.updateByPrimaryKeySelective(record);
    }

    
    @Override
	public int updateByPrimaryKey(InterfaceLog record) {
        return interfaceLogMapper.updateByPrimaryKey(record);
    }
}
