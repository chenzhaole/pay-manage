package com.sys.admin.common.exception;

/**
 * 接口异常定义
 */
public class InterfaceException extends Exception {

	private static final long serialVersionUID = -5995434434196299378L;

	private String returnCode;

	/**
	 * 返回参数
	 */
	public static final String RETURN_CODE_SUCCESS = "1";
	public static final String RETURN_CODE_FAIL = "0";

	public InterfaceException() {
		super();
	}

	public InterfaceException(String message) {
		super(message);
	}

	public InterfaceException(String code, String message) {
		super(message);
		returnCode = code;
	}

	public InterfaceException(String message, Throwable t) {
		super(message, t);
	}

	public InterfaceException(String code, String message, Throwable t) {
		super(message, t);
		returnCode = code;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
}
