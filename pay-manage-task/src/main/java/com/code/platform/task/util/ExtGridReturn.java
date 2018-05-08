package com.code.platform.task.util;

import java.util.List;

/**
 * Ext Grid返回对象
 */
public class ExtGridReturn<T> {

    /**
     * 总共条数
     */
    private int totalCount;
    /**
     * 所有数据
     */
    private List<T> rows;

    public ExtGridReturn() {
    }

    public ExtGridReturn(int totalCount, List<T> rows) {
        this.totalCount = totalCount;
        this.rows = rows;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

}
