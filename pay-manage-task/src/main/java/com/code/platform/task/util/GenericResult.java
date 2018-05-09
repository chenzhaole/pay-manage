package com.code.platform.task.util;

/**
 * 泛型结果类
 * <p/>
 * 可返回指定类型的结果
 *
 */
public class GenericResult<T> extends Result {

    private static final long serialVersionUID = 6884388024939222192L;

    /**
     * 结果对象
     */
    protected T object;

    /**
     * 获取结果对象
     *
     * @return 结果对象
     */
    public T getObject() {
        return object;
    }

    /**
     * 设置结果对象
     *
     * @param object 结果对象
     */
    public void setObject(T object) {
        this.object = object;
    }

}
