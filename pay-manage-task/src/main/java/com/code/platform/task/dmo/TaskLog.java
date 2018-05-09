package com.code.platform.task.dmo;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务日志POJO类
 */
public class TaskLog implements Serializable {

    /**
     * 日志编码
     */
    private Long logId;

    /**
     * 任务编号
     */
    private Long taskId;

    /**
     * 状态
     */
    private String status;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    public TaskLog() {

    }

    /**
     * @return the logId
     */
    public Long getLogId() {
        return logId;
    }

    /**
     * @param logId the logId to set
     */
    public void setLogId(Long logId) {
        this.logId = logId;
    }

    /**
     * @return the taskId
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the resultDesc
     */
    public String getResultDesc() {
        return resultDesc;
    }

    /**
     * @param resultDesc the resultDesc to set
     */
    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(
            "TaskLog [logId=%s, taskId=%s, status=%s, resultDesc=%s, startTime=%s, endTime=%s]",
            logId, taskId, status, resultDesc, startTime, endTime);
    }
}
