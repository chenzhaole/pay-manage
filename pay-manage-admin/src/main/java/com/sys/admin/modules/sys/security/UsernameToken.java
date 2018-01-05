package com.sys.admin.modules.sys.security;

import java.util.Date;

/**
 * 用户和密码（包含验证码）令牌类
 */
public class UsernameToken extends org.apache.shiro.authc.UsernamePasswordToken {

	private static final long serialVersionUID = 1L;

	private Date loginTime;

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public UsernameToken() {
		super();
	}

	public UsernameToken(String username, char[] password, boolean rememberMe, String host, Date loginTime) {
		super(username, password, false, host);
		this.loginTime = loginTime;
	}

}