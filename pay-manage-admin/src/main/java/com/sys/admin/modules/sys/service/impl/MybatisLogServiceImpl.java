package com.sys.admin.modules.sys.service.impl;

import com.sys.admin.modules.sys.dmo.LogWithBLOBs;
import com.sys.admin.modules.sys.mapper.LogMapper;
import com.sys.admin.modules.sys.service.MybatisLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 后台操作日志表Mybatis框架业务接口实现类
 */
@Service
@Transactional(readOnly = true)
public class MybatisLogServiceImpl implements MybatisLogService {
    @Autowired
    private LogMapper logMapper;

    @Override
	@Transactional(readOnly = false)
    public int save(LogWithBLOBs log) {
    	return 0;
    }
}
