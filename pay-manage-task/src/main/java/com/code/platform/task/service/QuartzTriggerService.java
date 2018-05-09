package com.code.platform.task.service;

import com.code.platform.task.dmo.QuartzTrigger;
import com.code.platform.task.dmo.TaskConfig;
import com.code.platform.task.util.Criteria;

import java.util.List;

/**
 * QUARTZ 触发器服务接口类
 */
public interface QuartzTriggerService {

    /**
     * 根据条件查询记录集
     *
     * @param criteria 查询类
     * @return 记录集
     */
    List<QuartzTrigger> findQuazTriggersByCriteria(Criteria criteria);

    /**
     * 根据条件查询记录总数
     *
     * @param criteria 查询类
     * @return 记录总数
     */
    int getQuazTriggersCountByCriteria(Criteria criteria);

    /**
     * 根据trigger名称 分组查询对应的trigger
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return QuartzTrigger
     */
    QuartzTrigger getQuazTriggerByTriggerNameAndGroup(
            String triggerName, String triggerGroup
    );

    /**
     * 根据taskId查询QuazTrigger
     *
     * @param taskId 任务编号
     * @return QuartzTrigger
     */
    QuartzTrigger getQuazTriggerByTaskId(long taskId);

    /**
     * 新增任务
     *
     * @param cronExpression CRON表达式
     */
    int addTrigger(TaskConfig taskConfig, String cronExpression);

    /**
     * 修改任务
     *
     * @param cronExpression CRON表达式
     */
    void editTrigger(TaskConfig taskConfig, String cronExpression);

    /**
     * 修改任务调用url
     *
     * @param taskId     任务编号
     * @param triggerUrl 触发URL地址
     */
    void editTriggerUrl(long taskId, String triggerUrl);

    /**
     * 修改任务执行时间
     *
     * @param taskId         任务编号
     * @param cronExpression CRON表达式
     */
    void editTriggerCron(long taskId, String cronExpression);

    /**
     * 删除任务
     *
     * @param taskId 任务编号
     */
    void removeTrigger(long taskId);

    /**
     * 暂停触发器
     *
     * @param taskId 任务编号
     */
    void pauseTrigger(long taskId);

    /**
     * 重启触发器
     *
     * @param taskId 任务编号
     */
    void resumeTrigger(long taskId);

    /**
     * 立即调用触发器
     *
     * @param taskId 任务编号
     */
    void immediatelyCall(long taskId);

    /**
     * 判断TaskConfig是否已经存在
     *
     * @return 存在：true；不存在：false
     */
    boolean checkTaskConfigIsExist(
            Long taskId, String triggerName, String triggerGroup
    );

    boolean checkTaskConfigIsExist(String triggerName, String triggerGroup,String businessType,String businessId);

    void removeTrigger(String businessType,String businessId);

    TaskConfig selectByBusinessTypeAndBusinessId(String businessType,String businessId);
}
