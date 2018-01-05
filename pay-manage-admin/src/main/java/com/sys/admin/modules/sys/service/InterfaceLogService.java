package com.sys.admin.modules.sys.service;

import com.sys.admin.modules.sys.dmo.InterfaceLog;
import com.sys.admin.modules.sys.dmo.InterfaceLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 第三方接口调用日志相关业务接口
 */
public interface InterfaceLogService {
    /**
     * 查询满足条件的数据条数
     * @param example 条件对象
     * @return 查询结果
     */
    int countByExample(InterfaceLogExample example);

    /**
     * 根据条件删除数据
     * @param example 条件对象
     * @return 删除的条数
     */
    int deleteByExample(InterfaceLogExample example);

    /**
     * 根据主键删除数据
     * @param id 主键ID
     * @return 删除的条数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入新数据
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insert(InterfaceLog record);

    /**
     * 插入新数据 -- 只保存不为空的字段
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insertSelective(InterfaceLog record);

    /**
     * 根据条件查询数据
     * @param example 条件对象
     * @return 查询结果
     */
    List<InterfaceLog> selectByExample(InterfaceLogExample example);

    /**
     * 根据主键查询数据
     * @param id 主键ID
     * @return 查询结果
     */
    InterfaceLog selectByPrimaryKey(Long id);

    /**
     * 更新满足条件的数据 -- 只更新不为空的字段
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExampleSelective(@Param("record") InterfaceLog record, @Param("example") InterfaceLogExample example);

    /**
     * 更新满足条件的数据
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExample(@Param("record") InterfaceLog record, @Param("example") InterfaceLogExample example);

    /**
     * 根据主键更新数据-- 只更新不为空的字段
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKeySelective(InterfaceLog record);

    /**
     * 根据主键更新数据
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKey(InterfaceLog record);
}