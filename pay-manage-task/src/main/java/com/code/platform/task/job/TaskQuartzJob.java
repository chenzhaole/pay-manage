package com.code.platform.task.job;

import com.code.platform.task.service.SimpleJobService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * quartz job 入口
 */
public class TaskQuartzJob extends QuartzJobBean {

    private SimpleJobService simpleJobService;

    public void setSimpleJobService(SimpleJobService simpleJobService) {
        this.simpleJobService = simpleJobService;
    }

    @Override
    protected void executeInternal(
        JobExecutionContext jobexecutioncontext
    ) throws JobExecutionException {
        Trigger trigger = jobexecutioncontext.getTrigger();
        String triggerName = trigger.getKey().getName();
        String triggerGroup = trigger.getKey().getGroup();

        //处理具体job
        simpleJobService.doJob(triggerName, triggerGroup);
    }
}
