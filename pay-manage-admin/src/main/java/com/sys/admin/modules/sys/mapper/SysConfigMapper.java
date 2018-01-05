package com.sys.admin.modules.sys.mapper;

import com.sys.admin.modules.sys.dmo.SysConfig;
import com.sys.admin.modules.sys.dmo.SysConfigExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统参数配置表数据操作接口
 */
@Repository
public interface SysConfigMapper {
    /**
     * 查询满足条件的数据条数
     * @param example 条件对象
     * @return 查询结果
     */
    int countByExample(SysConfigExample example);

    /**
     * 根据条件删除数据
     * @param example 条件对象
     * @return 删除的条数
     */
    int deleteByExample(SysConfigExample example);

    /**
     * 根据主键删除数据
     * @param configName 主键ID
     * @return 删除的条数
     */
    int deleteByPrimaryKey(String configName);

    /**
     * 插入新数据
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insert(SysConfig record);

    /**
     * 插入新数据 -- 只保存不为空的字段
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insertSelective(SysConfig record);

    /**
     * 根据条件分页查询数据
     * @param example 条件对象
     * @return 查询结果
     */
    List<SysConfig> selectByExampleWithRowbounds(SysConfigExample example, RowBounds rowBounds);

    /**
     * 根据条件查询数据
     * @param example 条件对象
     * @return 查询结果
     */
    List<SysConfig> selectByExample(SysConfigExample example);
    /**
     * 根据主键查询数据
     * @param configName 主键ID
     * @return 查询结果
     */
    SysConfig selectByPrimaryKey(String configName);

    /**
     * 更新满足条件的数据 -- 只更新不为空的字段
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExampleSelective(@Param("record") SysConfig record, @Param("example") SysConfigExample example);

    /**
     * 更新满足条件的数据
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExample(@Param("record") SysConfig record, @Param("example") SysConfigExample example);

    /**
     * 根据主键更新满足数据-- 只更新不为空的字段
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKeySelective(SysConfig record);

    /**
     * 根据主键更新数据
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKey(SysConfig record);

    /**
     * 根据主键更新数据
     * @param record 要更新的数据
     * @param configName 主键
     * @return 更新的条数
     */
    int updateByConfigName(@Param("record") SysConfig record, @Param("oldConfigName") String configName);

    /**
     * 获取定时任务锁
     * @return 0表示获取失败 1表示获取成功
     */
    int gainTimerLock();

    /**
     * 释放定时任务锁
     * @return 0表示释放失败 1表示释放成功
     */
    int releaseTimerLock();

    /**
     * 获取所有系统配置
     */
    List<SysConfig> getAll();

    /**
     * 获取所有系统配置分类
     */
    List<String> getCategorys();
}