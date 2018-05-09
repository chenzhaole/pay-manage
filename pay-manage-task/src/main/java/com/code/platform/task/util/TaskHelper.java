package com.code.platform.task.util;

import java.util.HashMap;
import java.util.Map;


/**
 * 请输入功能描述
 */
public class TaskHelper {
    /**
     * 调用任务URL
     *
     * @param triggerName
     */
    public static void invokeTaskUrl(Long logId, String triggerName, String triggerUrl) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.PARAM_TASK_LOG_ID, logId.toString());
        params.put(Constant.PARAM_TRIGGER_NAME, triggerName);
        HttpClientUtil.post(triggerUrl, params);
    }
}
