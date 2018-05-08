package com.code.platform.task.service.impl;

import com.code.platform.task.dao.TaskConfigDAO;
import com.code.platform.task.dao.TaskLogDAO;
import com.code.platform.task.dmo.TaskConfig;
import com.code.platform.task.dmo.TaskLog;
import com.code.platform.task.service.TaskLogService;
import com.code.platform.task.util.Criteria;
import com.code.platform.task.util.TaskHelper;
import com.code.platform.task.util.TaskStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * 任务日志业务实现类
 */
@Service
public class TaskLogServiceImpl implements TaskLogService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TaskLogDAO taskLogDAO;

    @Autowired
    TaskConfigDAO taskConfigDAO;

    /**
     * {@inheritDoc}
     */
    
    public List<TaskLog> findTaskLogsByCriteria(Criteria criteria) {
        return taskLogDAO.selectByExample(criteria);
    }

    /**
     * {@inheritDoc}
     */
    
    public List<TaskLog> findGroupTaskLogsByCriteria(Criteria criteria) {
        return taskLogDAO.selectGroupTaskLog(criteria);
    }

    /**
     * {@inheritDoc}
     */
    
    public int getTaskLogsCountByCriteria(Criteria criteria) {
        return taskLogDAO.countByExample(criteria);
    }

    /**
     * {@inheritDoc}
     */
    public int getGroupTaskLogsCountByCriteria(Criteria criteria) {
        return taskLogDAO.countGroupTaskLogByExample(criteria);
    }

    /**
     * {@inheritDoc}
     */
    
    public TaskLog getTaskLogById(Long logId) {
        return taskLogDAO.selectByPrimaryKey(logId);
    }

    /**
     * {@inheritDoc}
     */
    
    public void editTaskLog(TaskLog taskLog) {
        taskLogDAO.updateByPrimaryKeySelective(taskLog);
    }

    /**
     * {@inheritDoc}
     */
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,
                   rollbackFor = {Exception.class})
    public void receipt(Long logId, int result, String desc) {
        TaskLog taskLog = taskLogDAO.selectByPrimaryKey(logId);
        if (result == 0) {// 任务执行成功
            taskLog.setStatus(TaskStatusEnum.END.getStatusEN());
        } else {// 任务执行失败
            taskLog.setStatus(TaskStatusEnum.FAILURE.getStatusEN());
        }
        taskLog.setResultDesc(desc);
        taskLog.setEndTime(new Timestamp(System.currentTimeMillis()));
        taskLogDAO.updateByPrimaryKeySelective(taskLog);
    }

    /**
     * {@inheritDoc}
     */
    
    public void removeTaskLog(Long logId) {
        taskLogDAO.deleteByPrimaryKey(logId);
    }

    /**
     * {@inheritDoc}
     */
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED,
                   rollbackFor = {Exception.class})
    public void restartTask(Long logId) {
        TaskLog taskLog = taskLogDAO.selectByPrimaryKey(logId);
        TaskConfig taskConfig = taskConfigDAO.selectByPrimaryKey(taskLog.getTaskId());

        try {
            // 修改log状态
            taskLog.setStartTime(new Timestamp(System.currentTimeMillis()));
            taskLog.setStatus(TaskStatusEnum.START.getStatusEN());
            taskLog.setEndTime(new Timestamp(0));
            taskLogDAO.updateByPrimaryKeySelective(taskLog);
            // 再次调用任务URL
            TaskHelper.invokeTaskUrl(taskLog.getLogId(), taskConfig.getTriggerName(),
                                     taskConfig.getTriggerUrl());
        } catch (Exception e) {
            logger.error("restart invoke task url error:", e);
            // 修改调用记录状态
            invokeFail(taskLog, e.getMessage());
        }
    }

    /**
     * 记录调用失败log
     *
     * @param taskLog
     */
    private void invokeFail(TaskLog taskLog, String errorMsg) {
        taskLog.setStatus(TaskStatusEnum.FAILURE.getStatusEN());
        taskLog.setEndTime(new Timestamp(System.currentTimeMillis()));
        taskLog.setResultDesc(errorMsg);
        taskLogDAO.updateByPrimaryKeySelective(taskLog);
    }

}
