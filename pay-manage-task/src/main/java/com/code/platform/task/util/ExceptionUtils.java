package com.code.platform.task.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception帮助类
 */
public class ExceptionUtils {

    /**
     * 将系统异常对象转字符串
     *
     * @param e 异常对象
     *
     * @return
     */
    public static String exceptionToString(Exception e) {
        StringWriter w = new StringWriter();
        e.printStackTrace(new PrintWriter(w));
        return w.toString().replaceAll("\n", "<br/>").replaceAll("\r", "<br/>")
                .replaceAll("<br/><br/>", "<br/>");
    }
}
