package com.sys.admin.modules.sys.security;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 机构异常处理类
 */
public class AgencyException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public AgencyException() {
		super();
	}

	public AgencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public AgencyException(String message) {
		super(message);
	}

	public AgencyException(Throwable cause) {
		super(cause);
	}

}
