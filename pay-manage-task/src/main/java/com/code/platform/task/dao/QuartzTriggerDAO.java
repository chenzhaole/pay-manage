package com.code.platform.task.dao;

import com.code.platform.task.dmo.QuartzTrigger;
import com.code.platform.task.util.Criteria;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Quartz Trigger DAO
 */
@Repository
public interface QuartzTriggerDAO {

    /**
     * 根据条件查询记录集
     *
     * @param criteria 条件
     * @return 记录集
     */
    List<QuartzTrigger> findByCriteria(Criteria criteria);

    /**
     * 根据条件查询记录总数
     *
     * @param criteria 条件
     * @return 记录总数
     */
    int countByCriteria(Criteria criteria);

    /**
     * 根据trigger名称 分组查询对应的trigger
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return QuartzTriggers
     */
    QuartzTrigger getQuazTriggerByTriggerNameAndGroup(
            @Param("triggerName") String triggerName, @Param("triggerGroup") String triggerGroup
    );

    /**
     * 根据taskId查询对于的trigger
     *
     * @param taskId 任务ID
     * @return QuartzTrigger
     */
    QuartzTrigger getQuazTriggerByTaskId(@Param("taskId") long taskId);
}
