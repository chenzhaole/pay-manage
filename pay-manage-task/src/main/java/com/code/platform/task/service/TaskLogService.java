package com.code.platform.task.service;

import com.code.platform.task.dmo.TaskLog;
import com.code.platform.task.util.Criteria;

import java.util.List;

/**
 * 请输入功能描述
 */
public interface TaskLogService {
    /**
     * 根据条件查询记录集
     *
     * @param criteria 查询对象
     * @return
     */
    public List<TaskLog> findTaskLogsByCriteria(Criteria criteria);

    /**
     * 查询分组log，每个分组显示最新的一条log记录
     *
     * @param criteria 查询对象
     * @return
     */
    public List<TaskLog> findGroupTaskLogsByCriteria(Criteria criteria);

    /**
     * 根据条件查询记录总数
     *
     * @param criteria 查询对象
     * @return
     */
    public int getTaskLogsCountByCriteria(Criteria criteria);

    /**
     * 根据条件查询分组记录总数
     *
     * @param criteria 查询对象
     * @return
     */
    public int getGroupTaskLogsCountByCriteria(Criteria criteria);

    /**
     * 根据logId查询TaskLog
     *
     * @param logId 日志编号
     * @return
     */
    public TaskLog getTaskLogById(Long logId);

    /**
     * 修改TaskLog
     *
     * @param taskLog 日志对象
     */
    public void editTaskLog(TaskLog taskLog);

    /**
     * 记录TaskLog回执
     */
    public void receipt(Long logId, int result, String desc);

    /**
     * 删除TaskLog
     */
    public void removeTaskLog(Long logId);

    /**
     * 重新执行任务
     *
     * @param logId 日志编号
     */
    public void restartTask(Long logId);
}
