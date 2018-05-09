package com.code.platform.task.web;

import com.code.platform.task.dmo.TaskLog;
import com.code.platform.task.service.TaskLogService;
import com.code.platform.task.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务日志controller
 */
@Controller
@RequestMapping("/job/log")
public class JobLogController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TaskLogService taskLogService;

    /**
     * 默认访问历史页面
     * @return
     */
    @RequestMapping("/")
    public String history() {
        return "job/history";
    }

    /**
     * 查看列表
     * @param pager 分页对象
     * @param logId 日志编号
     * @param taskId 任务编号
     * @return
     */
    @RequestMapping("/all")
    @ResponseBody
    public Object all(ExtPager pager, @RequestParam(required = false) Long logId,@RequestParam(required = false) Long taskId) {
        Criteria criteria = new Criteria();
        List<TaskLog> list = new ArrayList<TaskLog>();
        ExtGridReturn<TaskLog> result = null;
        try{
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
                criteria.setOrderByClause(" TASK_ID desc ");
            }
            if (taskId != null) {
                criteria.put("taskId", taskId);
            }
            list = taskLogService.findGroupTaskLogsByCriteria(criteria);

        int count = taskLogService.getGroupTaskLogsCountByCriteria(criteria);
        result = new ExtGridReturn<TaskLog>(count, list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得分组List页面
     * @param pager 分页对象
     * @param logId 日志编号
     * @param taskId 任务编号
     * @return
     */
    @RequestMapping("/grouplist")
    @ResponseBody
    public Object groupList(ExtPager pager, @RequestParam(required = false) Long logId,@RequestParam(required = false) Long taskId) {
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
            criteria.setOrderByClause(" LOG_ID desc ");
        }
        if (taskId != null) {
            criteria.put("taskId", taskId);
        }

        List<TaskLog> list = taskLogService.findTaskLogsByCriteria(criteria);
        int count = taskLogService.getTaskLogsCountByCriteria(criteria);
        ExtGridReturn<TaskLog> result = new ExtGridReturn<TaskLog>(count, list);

        return result;
    }

    /**
     * 删除日志
     * @param logId 日志编号
     * @return
     * @throws Exception
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(@RequestParam(required = true) long logId) throws Exception {
        GenericResult<Integer> result = new GenericResult<Integer>();
        try {
            taskLogService.removeTaskLog(logId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("delete log error:", e);
            result.fail("error.delete.tasklog", ExceptionUtils.exceptionToString(e));
        }
        return result;
    }

    /**
     * 重启任务
     * @param logId 日志编号
     * @return
     * @throws Exception
     */
    @RequestMapping("/restart")
    @ResponseBody
    public Object restart(@RequestParam(required = true) long logId) throws Exception {
        GenericResult<Integer> result = new GenericResult<Integer>();
        try {
            taskLogService.restartTask(logId);
        } catch (Exception e) {
            logger.error("restart log error:", e);
            result.fail("error.delete.tasklog", ExceptionUtils.exceptionToString(e));
        }
        return result;
    }

    /**
     * 立即执行
     * @param id 编号
     * @param result 结果信息
     * @param desc 结果描述
     * @return
     */
    @RequestMapping(value = "/receipt", produces = "text/plain")
    @ResponseBody
    public Object receipt(@RequestParam(required = true) Long id, @RequestParam(required = true) int result,@RequestParam(required = false) String desc) {
        try {
            taskLogService.receipt(id, result, desc);
            return "0";
        } catch (Exception e) {
            logger.error("receipt error:", e);
            return "1";
        }
    }
}
