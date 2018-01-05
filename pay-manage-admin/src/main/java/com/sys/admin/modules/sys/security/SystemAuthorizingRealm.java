package com.sys.admin.modules.sys.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.security.Digests;
import com.sys.admin.modules.portal.service.AgencyService;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.sys.admin.common.servlet.ValidateCodeServlet;
import com.sys.admin.common.utils.SpringContextHolder;
import com.sys.admin.modules.sys.entity.Menu;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.service.SystemService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.util.DateUtils;
import com.sys.common.util.Encodes;

/**
 * 系统安全认证实现类
 */
@Service
@DependsOn({"userDao","roleDao","menuDao"})
public class SystemAuthorizingRealm extends AuthorizingRealm {

	private SystemService systemService;
	private AgencyService agencyService;

	/**
	 * 认证回调函数, 登录时调用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		if (authcToken instanceof UsernamePasswordToken) {
			return useLoginNamePassword((UsernamePasswordToken) authcToken);
		} else if (authcToken instanceof UsernameToken) {
			return useLoginName((UsernameToken) authcToken);
		}

		throw new UnsupportedTokenException("不支持的验证方式.");
	}

	private AuthenticationInfo useLoginNamePassword(UsernamePasswordToken token) throws AuthenticationException {
		if (UserUtils.isValidateCodeLogin(token.getUsername(), false, false)){
			// 判断验证码
			Session session = SecurityUtils.getSubject().getSession();
			String code = (String)session.getAttribute(ValidateCodeServlet.VALIDATE_CODE);
			if (token.getCaptcha() == null || !token.getCaptcha().toUpperCase().equals(code)){
				throw new CaptchaException("验证码错误.");
			}
		}

		User user = getSystemService().getUserByLoginName(token.getUsername());
		if (user != null) {
			if (!getAgencyService().isOfficeNormal(user.getOffice().getId())) {
				throw new AgencyException("机构被冻结.");
			}
			if (!getAgencyService().isPortalInfoNormal(user.getOffice().getId())) {
				throw new AgencyException("机构被冻结或未审核通过.");
			}
			byte[] salt = Encodes.decodeHex(user.getPassword().substring(0,16));
			return new SimpleAuthenticationInfo(new Principal(user),
					user.getPassword().substring(16), ByteSource.Util.bytes(salt), getName());
		} else {
			return null;
		}
	}

	private AuthenticationInfo useLoginName(UsernameToken token) throws AuthenticationException {
        int expire = 5;
        String urlLoginExpire = GlobalConfig.getConfig("timer.urlLogin.expire");
        if (StringUtils.isNumeric(urlLoginExpire)) {
            expire = Integer.parseInt(urlLoginExpire);
        }
		if (DateUtils.pastMinutes(token.getLoginTime()) > expire){
			throw new AuthenticationException("登录超时.");
		}

		User user = getSystemService().getUserByLoginName(token.getUsername());
		if (user != null) {
			if (!getAgencyService().isOfficeNormal(user.getOffice().getId())) {
				throw new AgencyException("机构被冻结.");
			}
			if (!getAgencyService().isPortalInfoNormal(user.getOffice().getId())) {
				throw new AgencyException("机构被冻结或未审核通过.");
			}
			return new SimpleAccount(new Principal(user), Digests.sha1("".getBytes(), null, Digests.HASH_INTERATIONS), getName());
		} else {
			return null;
		}
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Principal principal = (Principal) getAvailablePrincipal(principals);
		User user = getSystemService().getUserByLoginName(principal.getLoginName());
		if (user != null) {
			UserUtils.putCache("user", user);
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			List<Menu> list = UserUtils.getMenuList();
			for (Menu menu : list){
				if (StringUtils.isNotBlank(menu.getPermission())){
					// 添加基于Permission的权限信息
					info.addStringPermission(menu.getPermission());
				}
			}
			// 更新登录IP和时间
//			getSystemService().updateUserLoginInfo(user.getId());
			return info;
		} else {
			return null;
		}
	}
	
	/**
	 * 设定密码校验的Hash算法与迭代次数
	 */
	@PostConstruct
	public void initCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(Digests.SHA1);
		matcher.setHashIterations(Digests.HASH_INTERATIONS);
		setCredentialsMatcher(matcher);
	}
	
	/**
	 * 清空用户关联权限认证，待下次使用时重新加载
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清空所有关联认证
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}

	/**
	 * 获取系统业务对象
	 */
	public SystemService getSystemService() {
		if (systemService == null){
			systemService = SpringContextHolder.getBean(SystemService.class);
		}
		return systemService;
	}

	/**
	 * 获取机构业务对象
	 */
	public AgencyService getAgencyService() {
		if (agencyService == null){
			agencyService = SpringContextHolder.getBean(AgencyService.class);
		}
		return agencyService;
	}
	
	/**
	 * 授权用户信息
	 */
	public static class Principal implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private Long id;
		private String loginName;
		private String name;
		private Map<String, Object> cacheMap;

		public Principal(User user) {
			this.id = user.getId();
			this.loginName = user.getLoginName();
			this.name = user.getName();
		}

		public Long getId() {
			return id;
		}

		public String getLoginName() {
			return loginName;
		}

		public String getName() {
			return name;
		}

		public Map<String, Object> getCacheMap() {
			if (cacheMap==null){
				cacheMap = new HashMap<String, Object>();
			}
			return cacheMap;
		}

	}
}
