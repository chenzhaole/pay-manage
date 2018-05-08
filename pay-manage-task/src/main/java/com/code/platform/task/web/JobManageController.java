package com.code.platform.task.web;

import java.util.List;

import com.code.platform.task.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.code.platform.task.dmo.QuartzTrigger;
import com.code.platform.task.dmo.TaskConfig;
import com.code.platform.task.service.QuartzTriggerService;

/**
 * Job管理
 */
@Controller
@RequestMapping("/job/manager")
public class JobManageController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QuartzTriggerService quartzTriggerService;

    @RequestMapping("/")
    public String list() {
        return "job/list";
    }

    /**
     * 获取任务列表信息
     * @param pager 分页对象
     * @param triggerName 触发器名称
     * @param triggerGroup 触发器分组
     * @return
     */
    @RequestMapping("/all")
    @ResponseBody
    public Object all(ExtPager pager, @RequestParam(required = false) String triggerName, @RequestParam(required = false) String triggerGroup ) {
        Criteria criteria = new Criteria();
        // 设置分页信息
        if (pager.getLimit() != null && pager.getStart() != null) {
            criteria.setOracleEnd(pager.getLimit() + pager.getStart());
            criteria.setOracleStart(pager.getStart());
        }
        // 排序信息
        if (StringUtils.isNotBlank(pager.getDir()) && StringUtils.isNotBlank(
            pager.getSort())) {
            criteria.setOrderByClause(pager.getCommonDBSort() + " " + pager.getDir());
        } else {
            criteria.setOrderByClause(" TRIGGER_GROUP desc ");
        }
        if (StringUtils.isNotBlank(triggerName)) {
            criteria.put("triggerName", triggerName);
        }
        if (StringUtils.isNotBlank(triggerGroup)) {
            criteria.put("triggerGroup", triggerGroup);
        }

        List<QuartzTrigger> list = quartzTriggerService.findQuazTriggersByCriteria(criteria);
        int count = quartzTriggerService.getQuazTriggersCountByCriteria(criteria);
        ExtGridReturn<QuartzTrigger> result = new ExtGridReturn<QuartzTrigger>(count, list);

        return result;
    }

    /**
     * 保存任务
     * @param taskConfig 任务对象
     * @param cronExpression CRON表达式
     * @return
     * @throws Exception
     */
    @RequestMapping("/save")
    @ResponseBody
    public Object save(TaskConfig taskConfig, @RequestParam(required = true) String cronExpression) throws Exception {
        GenericResult<Integer> result = new GenericResult<Integer>();
        try {
            logger.debug("{} cronExpression=[{}]", taskConfig.toString(), cronExpression);
            Long editTaskId = 0l;
            if (taskConfig.getTaskId() != null) {
                editTaskId = taskConfig.getTaskId();
            }
            if (quartzTriggerService.checkTaskConfigIsExist(editTaskId,
                                                           taskConfig.getTriggerName(),
                                                           taskConfig.getTriggerGroup())) {
                result.fail("error.save.trigger",
                            "Trigger名称已经在" + taskConfig.getTriggerGroup() + "分组下存在！");
                return result;
            }
            if (taskConfig.getTaskId() == null) {//新增

                int taskId = quartzTriggerService.addTrigger(taskConfig, cronExpression);
                result.setObject(taskId);
            } else {//修改
                quartzTriggerService.editTrigger(taskConfig, cronExpression);
            }
        } catch (Exception e) {
            logger.error("save task error:", e);
            result.fail("error.save.trigger", ExceptionUtils.exceptionToString(e));
        }
        return result;
    }

    /**
     * 删除任务
     * @param taskId 任务编号
     * @return
     * @throws Exception
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(@RequestParam(required = true) long taskId) throws Exception {
        GenericResult<Integer> result = new GenericResult<Integer>();
        try {
            quartzTriggerService.removeTrigger(taskId);
        } catch (Exception e) {
            logger.error("delete task error:", e);
            result.fail("error.remove.trigger", ExceptionUtils.exceptionToString(e));
        }
        return result;
    }

    /**
     * 暂定任务
     * @param taskId 任务编号
     * @return
     * @throws Exception
     */
    @RequestMapping("/suspend")
    @ResponseBody
    public Object suspend(@RequestParam(required = true) long taskId) throws Exception {
        GenericResult<Integer> result = new GenericResult<Integer>();
        try {
            quartzTriggerService.pauseTrigger(taskId);
        } catch (Exception e) {
            logger.error("suspend taskconfig error:", e);
            result.fail("error.suspend.trigger", ExceptionUtils.exceptionToString(e));
        }
        return result;
    }

    /**
     * 重启任务
     * @param taskId 任务编号
     * @return
     * @throws Exception
     */
    @RequestMapping("/resume")
    @ResponseBody
    public Object resume(@RequestParam(required = true) long taskId) throws Exception {
        GenericResult<Integer> result = new GenericResult<Integer>();
        try {
            quartzTriggerService.resumeTrigger(taskId);
        } catch (Exception e) {
            logger.error("resume task error:", e);
            result.fail("error.resume.trigger", ExceptionUtils.exceptionToString(e));
        }
        return result;
    }

    /**
     * 立即执行任务
     * @param taskId 任务编号
     * @return
     * @throws Exception
     */
    @RequestMapping("/immediatelyCall")
    @ResponseBody
    public Object immediatelyCall(@RequestParam(required = true) long taskId) throws Exception {
        GenericResult<Integer> result = new GenericResult<Integer>();
        try {
            quartzTriggerService.immediatelyCall(taskId);
        } catch (Exception e) {
            logger.error("immediatelyCall task error:", e);
            result.fail("error.immediatelyCall.trigger", ExceptionUtils.exceptionToString(e));
        }
        return result;
    }
}
