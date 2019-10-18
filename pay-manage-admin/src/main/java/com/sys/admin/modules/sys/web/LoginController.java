package com.sys.admin.modules.sys.web;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.utils.ClientUtil;
import com.sys.admin.common.utils.CookieUtils;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.portal.dmo.PortalInfo;
import com.sys.admin.modules.sys.entity.Office;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.service.SystemService;
import com.sys.admin.modules.sys.utils.UserUtils;

import com.sys.common.enums.PayTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录Controller
 *
 */
@SuppressWarnings("MVCPathVariableInspection")
@Controller
public class LoginController extends BaseController {


    @Autowired
    private SystemService systemService;

    /**
     * (1)地址栏访问,如果未登录则跳转至sysLogin页面.  管理登录，如果未登陆则到登陆界面；如果已登陆则打开首页
     *
     * @param request  请求
     * @param response 响应
     * @param model    页面传参
     * @return 页面
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        // 如果已经登录，则跳转到管理首页
        if (user.getId() != null) {
            return "redirect:" + GlobalConfig.getAdminPath();
        }
        return "modules/sys/sysLogin";
    }

    /**
     * 登录失败，真正登录的POST请求由Filter完成
     *
     * @param username 登录名
     * @param request  请求
     * @param response 响应
     * @param model    页面传参
     * @return 页面
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
    public String login(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String username, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        // 如果已经登录，则跳转到管理首页
        if (user.getId() != null) {
            if (logger.isInfoEnabled()) {
				logger.info("用户 [" + user.getId() + "] 已经登录");
			}
            return "redirect:" + GlobalConfig.getAdminPath();
        }
        model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
        model.addAttribute("isValidateCodeLogin", UserUtils.isValidateCodeLogin(username, true, false));
        return "modules/sys/sysLogin";
    }



    /**
     * (2)输入账号密码后,点击登陆按钮.  登陆成功，进入管理首页
     *
     * @param request  请求
     * @param response 响应
     * @return 管理首页
     */
    @RequiresUser
    @RequestMapping(value = "${adminPath}")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        User user = UserUtils.getUser();
        // 未登录，则跳转到登录页
        if (user.getId() == null) {
            return "redirect:" + GlobalConfig.getAdminPath() + "/login";
        }
        try {
            systemService.updateUserLoginInfo(user.getId(), getIpAddr(request));
        } catch (Exception e) {
            logger.error("用户 [" + user.getId() + "] 记录登录IP失败");
        }


        if (logger.isInfoEnabled()) {
			logger.info("用户 [" + user.getId() + "] 登录成功");
		}

        // 登录成功后，验证码计算器清零
        UserUtils.isValidateCodeLogin(user.getLoginName(), false, true);
        // 登录成功后，获取上次登录的当前站点ID
//        UserUtils.putCache("siteId",Long.parseLong(CookieUtils.getCookie(request, "siteId")));

        int clientType = ClientUtil.getUserAgentType(request);
        if (clientType == 1 || clientType == 2) {
            //微信浏览器 or 支付宝浏览器
            logger.info("用户 [" + user.getId() + "] 使用的微信浏览器,跳转至wap首页");
            return "redirect:" +  GlobalConfig.getAdminPath()+"/wap/order/preList";
        }
        return "modules/sys/sysIndex";
    }

    /**
     * 获取主题方案
     *
     * @param theme    主题
     * @param request  请求
     * @param response 响应
     * @return 页面
     */
    @RequestMapping(value = "/theme/{theme}")
    public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isNotBlank(theme)) {
            CookieUtils.setCookie(response, "theme", theme);
        } else {
            theme = CookieUtils.getCookie(request, "theme");
            CookieUtils.setCookie(response, "theme", theme);
        }
        return "redirect:" + request.getParameter("url");
    }
    
    /**
     * 列表页展示
     * @param model 页面传参对象
     * @return 页面
     */
    @RequestMapping(value = "${adminPath}/welcome")
    public String list(Model model) {
        User user = UserUtils.getUser();
       
        return "modules/welcome";
    }

    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return 客户端IP
     */
    protected String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        // 如果是多级代理，那么取第一个ip为客户ip
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        return ip;
    }

}
