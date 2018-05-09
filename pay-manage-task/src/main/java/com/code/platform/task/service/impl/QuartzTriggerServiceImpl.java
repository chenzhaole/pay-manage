package com.code.platform.task.service.impl;

import com.code.platform.task.dao.QuartzTriggerDAO;
import com.code.platform.task.dao.TaskConfigDAO;
import com.code.platform.task.dmo.QuartzTrigger;
import com.code.platform.task.dmo.TaskConfig;
import com.code.platform.task.service.QuartzTriggerService;
import com.code.platform.task.service.SimpleJobService;
import com.code.platform.task.util.Criteria;
import com.code.platform.task.util.TriggerStatusEnum;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * QUARTZ 触发器服务实现类
 */
@Service
public class QuartzTriggerServiceImpl implements QuartzTriggerService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QuartzTriggerDAO quartzTriggerDAO;

    @Autowired
    TaskConfigDAO taskConfigDAO;

    @Autowired
    SimpleJobService simpleJobService;

    @Autowired
    @Qualifier("quartzScheduler")
    private Scheduler scheduler;

    @Autowired
    @Qualifier("jobDetail")
    private JobDetail jobDetail;

    /**
     * {@inheritDoc}
     */
    
    public List<QuartzTrigger> findQuazTriggersByCriteria(Criteria criteria) {
        return quartzTriggerDAO.findByCriteria(criteria);
    }

    /**
     * {@inheritDoc}
     */
    
    public int getQuazTriggersCountByCriteria(Criteria criteria) {
        return quartzTriggerDAO.countByCriteria(criteria);
    }

    /**
     * {@inheritDoc}
     */
    
    public QuartzTrigger getQuazTriggerByTriggerNameAndGroup(String triggerName, String triggerGroup) {
        return quartzTriggerDAO.getQuazTriggerByTriggerNameAndGroup(triggerName, triggerGroup);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int addTrigger(TaskConfig taskConfig, String cronExpression) {
        int taskId = taskConfigDAO.insert(taskConfig);

        // 新增quartz 任务
        addQuazTrigger(taskConfig.getTriggerName(), taskConfig.getTriggerGroup(), cronExpression);

        return taskId;
    }

    /**
     * {@inheritDoc}
     */
    
    public QuartzTrigger getQuazTriggerByTaskId(long taskId) {
        return quartzTriggerDAO.getQuazTriggerByTaskId(taskId);
    }

    /**
     * {@inheritDoc}
     */
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void editTrigger(TaskConfig taskConfig, String cronExpression) {
        TaskConfig oldConfig = taskConfigDAO.selectByPrimaryKey(taskConfig.getTaskId());
        // 修改任务cron TODO:是否需要判断cronExpression是否修改过，再进行修改
        editTriggerCron(oldConfig, taskConfig, cronExpression);

        // 修改任务调用URL
        taskConfigDAO.updateByPrimaryKeySelective(taskConfig);
    }

    /**
     * {@inheritDoc}
     */
    
    public void editTriggerUrl(long taskId, String triggerUrl) {
        TaskConfig taskConfig = taskConfigDAO.selectByPrimaryKey(taskId);
        taskConfigDAO.updateByPrimaryKeySelective(taskConfig);
    }

    /**
     * {@inheritDoc}
     */
    
    public void editTriggerCron(long taskId, String cronExpression) {
        TaskConfig taskConfig = taskConfigDAO.selectByPrimaryKey(taskId);

        editTriggerCron(taskConfig, taskConfig, cronExpression);
    }

    /**
     * 修改任务运行cron
     *
     * @param cronExpression Cron 表达式
     */
    private void editTriggerCron(TaskConfig oldConfig, TaskConfig newConfig, String cronExpression) {
        // TODO:可以看cronExpression是否发生修改，来判断是否需要重建trigger
        // 先删除老的quartz trigger
        removeQuazTrigger(oldConfig.getTriggerName(), oldConfig.getTriggerGroup());
        // 再增加新的quartz trigger
        addQuazTrigger(newConfig.getTriggerName(), newConfig.getTriggerGroup(), cronExpression);
    }

    /**
     * {@inheritDoc}
     */
    
    public void removeTrigger(long taskId) {
        TaskConfig taskConfig = taskConfigDAO.selectByPrimaryKey(taskId);

        // 先删除quartz trigger
        removeQuazTrigger(taskConfig.getTriggerName(), taskConfig.getTriggerGroup());

        // 再删除自定义的config TODO:是否要删除，要不要保持一个历史记录?
        taskConfigDAO.deleteByPrimaryKey(taskId);
    }

    public void removeTrigger(String businessType,String businessId) {
        TaskConfig taskConfig = selectByBusinessTypeAndBusinessId(businessType,businessId);

        if(taskConfig!=null){
            // 先删除quartz trigger
            removeQuazTrigger(taskConfig.getTriggerName(), taskConfig.getTriggerGroup());

            // 再删除自定义的config TODO:是否要删除，要不要保持一个历史记录?
            taskConfigDAO.deleteByPrimaryKey(taskConfig.getTaskId());
        }
    }

    @Override
    public TaskConfig selectByBusinessTypeAndBusinessId(String businessType, String businessId) {
        Criteria criteria = new Criteria();
        criteria.put("businessId",businessId);
        criteria.put("businessType",businessType);
        return taskConfigDAO.selectByExample(criteria);
    }

    /**
     * {@inheritDoc}
     */
    
    public void pauseTrigger(long taskId) {
        TaskConfig taskConfig = taskConfigDAO.selectByPrimaryKey(taskId);
        pauseQuazTrigger(taskConfig.getTriggerName(), taskConfig.getTriggerGroup());
    }

    /**
     * {@inheritDoc}
     */
    
    public void resumeTrigger(long taskId) {
        TaskConfig taskConfig = taskConfigDAO.selectByPrimaryKey(taskId);
        resumeQuazTrigger(taskConfig.getTriggerName(), taskConfig.getTriggerGroup());
    }

    /**
     * {@inheritDoc}
     */
    
    public void immediatelyCall(long taskId) {
        TaskConfig taskConfig = taskConfigDAO.selectByPrimaryKey(taskId);
        simpleJobService.doJob(taskConfig);
    }

    /**
     * 新增quartz任务
     *
     * @param triggerName    触发器名称
     * @param triggerGroup   触发器分组
     * @param cronExpression Cron 表达式
     */
    private void addQuazTrigger(String triggerName, String triggerGroup, String cronExpression) {
        try {
            scheduler.addJob(jobDetail, true);
            Trigger cronTrigger = newTrigger().withIdentity(triggerName,
                    triggerGroup).withSchedule(
                    cronSchedule(new CronExpression(cronExpression))).forJob(
                    jobDetail.getKey().getName(), Scheduler.DEFAULT_GROUP).build();

            scheduler.scheduleJob(cronTrigger);
            scheduler.rescheduleJob(TriggerKey.triggerKey(cronTrigger.getKey().getName(),
                    cronTrigger.getKey().getGroup()), cronTrigger);
        } catch (SchedulerException e) {
            logger.error("SchedulerException: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ParseException e) {
            logger.error("ParseException: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除quartz任务
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return 删除成功：true；删除失败：false
     */
    private boolean removeQuazTrigger(String triggerName, String triggerGroup) {
        try {
            // 先暂停
            pauseQuazTrigger(triggerName, triggerGroup);
            return scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroup)); // 移除触发器
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 重启触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    private void resumeQuazTrigger(String triggerName, String triggerGroup) {
        try {
            scheduler.resumeTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    private void pauseQuazTrigger(String triggerName, String triggerGroup) {
        try {
            QuartzTrigger trigger = quartzTriggerDAO.getQuazTriggerByTriggerNameAndGroup(
                    triggerName, triggerGroup);
            // 判断当前trigger是否暂停状态,不是暂停状态再暂停
            if (!TriggerStatusEnum.PAUSED.getStatusEN().equals(trigger.getTriggerState())) {
                scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    
    public boolean checkTaskConfigIsExist(Long taskId, String triggerName, String triggerGroup) {
        return taskConfigDAO.checkExistsTask(taskId, triggerName, triggerGroup) >= 1;
    }

    public boolean checkTaskConfigIsExist(String triggerName, String triggerGroup,String businessType,String businessId) {
        return taskConfigDAO.checkExistsTaskEx(triggerName,triggerGroup,businessType,businessId)>=1;
    }

}
