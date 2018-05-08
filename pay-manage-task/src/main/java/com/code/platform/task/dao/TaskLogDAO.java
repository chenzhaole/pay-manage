package com.code.platform.task.dao;

import com.code.platform.task.dmo.TaskLog;
import com.code.platform.task.util.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * task log dao
 */
@Repository
public interface TaskLogDAO {
    /**
     * 保存记录,不管记录里面的属性是否为空
     */
    Long insertSelective(TaskLog record);

    /**
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(long logId);

    /**
     * 根据条件查询记录集
     */
    List<TaskLog> selectByExample(Criteria example);

    /**
     * 根据条件查询记录集
     */
    List<TaskLog> selectGroupTaskLog(Criteria example);

    /**
     * 根据主键查询记录
     */
    TaskLog selectByPrimaryKey(long logId);

    /**
     * 根据条件查询记录总数
     *
     * @return
     */
    int countByExample(Criteria example);

    /**
     * 根据条件查询分组记录总数
     *
     * @param example
     * @return
     */
    int countGroupTaskLogByExample(Criteria example);

    /**
     * 根据主键更新属性不为空的记录
     */
    int updateByPrimaryKeySelective(TaskLog record);
}
