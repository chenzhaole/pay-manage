package com.code.platform.task.dmo;

import java.io.Serializable;

/**
 * 任务配置POJO类
 */
@SuppressWarnings("serial")
public class TaskConfig implements Serializable {

    /**
     * 任务编号
     */
    private Long taskId;

    /**
     * 触发器名称
     */
    private String triggerName;

    /**
     * 触发器分组
     */
    private String triggerGroup;

    /**
     * 触发URL
     */
    private String triggerUrl;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private String businessId;


    public TaskConfig() {

    }

    /**
     * @param triggerName
     * @param triggerGroup
     * @param triggerUrl
     */
    public TaskConfig(String triggerName, String triggerGroup, String triggerUrl) {
        super();
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
        this.triggerUrl = triggerUrl;
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
     * @return the triggerName
     */
    public String getTriggerName() {
        return triggerName;
    }

    /**
     * @param triggerName the triggerName to set
     */
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    /**
     * @return the triggerGroup
     */
    public String getTriggerGroup() {
        return triggerGroup;
    }

    /**
     * @param triggerGroup the triggerGroup to set
     */
    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    /**
     * @return the triggerUrl
     */
    public String getTriggerUrl() {
        return triggerUrl;
    }

    /**
     * @param triggerUrl the triggerUrl to set
     */
    public void setTriggerUrl(String triggerUrl) {
        this.triggerUrl = triggerUrl;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(
            "TaskConfig [taskId=%s, triggerName=%s, triggerGroup=%s, triggerUrl=%s]", taskId,
            triggerName, triggerGroup, triggerUrl);
    }


}
