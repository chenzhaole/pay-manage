package com.sys.admin.common.exception;

/**
 * 页面找不到异常定义
 */
public class NotFindViewException extends RuntimeException {

    private static final long serialVersionUID = 4789874080907686720L;

    public NotFindViewException() {
        super();
    }

    public NotFindViewException(String message) {
        super(message);
    }

    public NotFindViewException(Throwable cause) {
        super(cause);
    }

    public NotFindViewException(String message, Throwable cause) {
        super(message, cause);
    }
}
