package com.code.platform.task.util;

public class Table {

    /**
     * 把dmo字段转为数据库字段<br>
     * fileName -> FILE_NAME
     *
     * @param field
     *
     * @return
     */
    public static String toClumn(String field) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char c = field.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append("_").append(Character.toUpperCase(c));
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }
}
