package com.code.platform.task.web;

import com.alibaba.fastjson.JSON;
import com.code.platform.task.dmo.TaskConfig;
import com.code.platform.task.dmo.TaskLog;
import com.code.platform.task.service.QuartzTriggerService;
import com.code.platform.task.service.TaskLogService;
import com.code.platform.task.util.ExceptionUtils;
import com.code.platform.task.util.Result;
import com.code.platform.task.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 对外开放API接口控制类
 */
@Controller
@RequestMapping("/open")
public class JobOpenController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    QuartzTriggerService quartzTriggerService;
    @Autowired
    TaskLogService taskLogService;



    /**
     * 创建定时任务
     * @param taskConfig 任务对象
     * @param cronExpression CRON表达式
     */
    @RequestMapping("/createJob")
    @ResponseBody
    public String create(TaskConfig taskConfig, @RequestParam(required = true) String cronExpression) {
        Result result = new Result();
        try {
            logger.debug("{} cronExpression=[{}]", taskConfig.toString(), cronExpression);

            String checkResult = checkParams(taskConfig,cronExpression);
            if(!StringUtils.isBlank(checkResult))
                return checkResult;


            if(quartzTriggerService.checkTaskConfigIsExist(0L,
                    taskConfig.getTriggerName(),taskConfig.getTriggerGroup())){
                result.fail("createJob failure",
                        "Trigger名称已经在" + taskConfig.getTriggerGroup() + "分组下存在！");
                return JSON.toJSONString(result);
            }

            if (quartzTriggerService.checkTaskConfigIsExist(taskConfig.getTriggerName(),taskConfig.getTriggerGroup(),
                    taskConfig.getBusinessType(),taskConfig.getBusinessId())) {
                result.fail("createJob failure",
                        "[businessType="+taskConfig.getBusinessType()+"] [businessId="+taskConfig.getBusinessId()+"] " +
                                "[triggerName="+taskConfig.getTriggerName()+"] [triggerGroup="+taskConfig.getTriggerGroup()+"] 定时任务已存在！");
                return JSON.toJSONString(result);
            }

            quartzTriggerService.addTrigger(taskConfig, cronExpression);
            result.setErrorCode("createJob success");
        } catch (Exception e) {
            logger.error("createJob error:", e);
            result.fail("createJob failure", ExceptionUtils.exceptionToString(e));
        }
        return JSON.toJSONString(result);
    }


    /**
     * 删除定时任务
     * @param taskConfig 任务对象
     * @param cronExpression CRON表达式
     */
    @RequestMapping("/deleteJob")
    @ResponseBody
    public String deleteJob(TaskConfig taskConfig){
        Result result = new Result();
        try {
            if(StringUtils.isBlank(taskConfig.getBusinessType())){
                result.fail("deleteJob failure","删除失败，businessType不能为空");
                return JSON.toJSONString(result);
            }

            if(StringUtils.isBlank(taskConfig.getBusinessId())){
                result.fail("deleteJob failure","删除失败，businessId不能为空");
                return JSON.toJSONString(result);
            }

            TaskConfig taskConfigOld = quartzTriggerService.selectByBusinessTypeAndBusinessId(
                    taskConfig.getBusinessType(),taskConfig.getBusinessId());
            if(taskConfigOld==null){
                result.fail("deleteJob failure","删除失败，任务不存在");
                return JSON.toJSONString(result);
            }
            quartzTriggerService.removeTrigger(taskConfig.getBusinessType(),taskConfig.getBusinessId());
            result.setErrorCode("deleteJob success");
        } catch (Exception e) {
            logger.error("deleteJob error:", e);
            result.fail("deleteJob failure", ExceptionUtils.exceptionToString(e));
        }
        return JSON.toJSONString(result);
    }


    /**
     * 定时任务执行结果回调
     * @param logId 执行日志ID
     * @param status 执行状态
     * @param resultDesc 执行结果描述
     */
    @RequestMapping("/callback")
    @ResponseBody
    public String callback(Long logId,String status,String resultDesc,Long endTime){
        Result result = new Result();

        try {
            if(logId == null || logId==0l){
                result.fail("callback failure","logId不能为空");
                return JSON.toJSONString(result);
            }

            if(StringUtils.isBlank(status)){
                result.fail("callback failure","status不能为空");
                return JSON.toJSONString(result);
            }

            if(StringUtils.isBlank(resultDesc)){
                result.fail("callback failure","resultDesc不能为空");
                return JSON.toJSONString(result);
            }

            if(endTime == null || endTime==0L){
                result.fail("callback failure","endTime不能为空");
                return JSON.toJSONString(result);
            }

            TaskLog taskLog = taskLogService.getTaskLogById(logId);
            if(taskLog == null){
                result.fail("callback failure","任务执行日志不存在");
                return JSON.toJSONString(result);
            }

            taskLog.setStatus(status);
            taskLog.setResultDesc(resultDesc);
            taskLog.setEndTime(new Date(endTime));
            taskLogService.editTaskLog(taskLog);
        } catch (Exception e) {
            logger.error("callback error:", e);
            result.fail("callback failure", ExceptionUtils.exceptionToString(e));
        }
        return JSON.toJSONString(result);
    }



    public String checkParams(TaskConfig taskConfig,String cronExpression){
        if(StringUtils.isBlank(taskConfig.getTriggerGroup())){
            return "triggerGroup 不能为空！";
        }

        if(StringUtils.isBlank(taskConfig.getTriggerName())){
            return "triggerName 不能为空！";
        }

        if(StringUtils.isBlank(taskConfig.getTriggerUrl())){
            return "triggerUrl 不能为空！";
        }

        if(StringUtils.isBlank(cronExpression)){
            return "cronExpression 不能为空！";
        }

        if(StringUtils.isBlank(taskConfig.getBusinessType())){
            return "businessType 不能为空！";
        }

        if(StringUtils.isBlank(taskConfig.getBusinessId())){
            return "businessId 不能为空！";
        }

        return null;
    }
}
