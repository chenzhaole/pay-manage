package com.sys.admin.modules.sys.service;

import com.sys.admin.modules.sys.dmo.LogWithBLOBs;

/**
 * 后台操作日志表Mybatis框架业务接口类
 */
public interface MybatisLogService {
    /**
     * 保存日志
     * @param log 带有大字段的日志对象
     * @return 保存的条数
     */
    int save(LogWithBLOBs log);
}
