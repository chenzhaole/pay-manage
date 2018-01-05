package com.sys.admin.modules.sys.interceptor;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.service.BaseService;
import com.sys.admin.common.utils.SpringContextHolder;
import com.sys.admin.modules.sys.dmo.LogWithBLOBs;
import com.sys.admin.modules.sys.entity.Log;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.mapper.LogMapper;
import com.sys.admin.modules.sys.utils.UserUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 系统拦截器
 * <p/>
 * <b>注意：</b>
 * <p/>
 * 使用原来的 {@link com.sys.admin.modules.sys.dao.LogDao} 记录日志存在错误，
 * 在 Controller 中未开始调用 Service 执行数据库操作提前返回的情况下，如果被请求的方法符合记录日志的条件，
 * 如果有 Hibernate 的实体类发生了变化，则会跟随一起进行更新，而这不是系统所期望的。
 *
 */
public class LogInterceptor extends BaseService implements HandlerInterceptor {


    /**
     * 使用 Mapper 进行数据库操作，使用 {@link com.sys.admin.modules.sys.dao.LogDao} 和
     * {@link com.sys.admin.modules.sys.service.MybatisLogService}都有 问题
     */
    private static LogMapper logMapper = SpringContextHolder.getBean(LogMapper.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        String requestRri = request.getRequestURI();
        String uriPrefix = request.getContextPath() + GlobalConfig.getAdminPath();

        if ((StringUtils.startsWith(requestRri, uriPrefix) && (StringUtils.endsWith(requestRri, "/save")
                || StringUtils.endsWith(requestRri, "/delete") || StringUtils.endsWith(requestRri, "/import")
                || StringUtils.endsWith(requestRri, "/updateSort"))) || ex != null) {

            User user = UserUtils.getUser();
            if (user != null && user.getId() != null) {

                StringBuilder params = new StringBuilder();
                int index = 0;
                for (Object param : request.getParameterMap().keySet()) {
                    params.append(index++ == 0 ? "" : "&").append(param).append("=");
                    params.append(abbr(StringUtils.endsWithIgnoreCase((String) param, "password")
                            ? "" : request.getParameter((String) param), 100));
                }

                LogWithBLOBs log = new LogWithBLOBs();
                log.setType(ex == null ? Log.TYPE_ACCESS : Log.TYPE_EXCEPTION);
                log.setCreateBy(null);
                log.setCreateDate(new Date());
                log.setRemoteAddr(getRemoteAddr(request));
                log.setUserAgent(request.getHeader("user-agent"));
                log.setRequestUri(request.getRequestURI());
                log.setMethod(request.getMethod());
                log.setParams(params.toString());
                log.setException(ex != null ? ex.toString() : "");
                if (log.getException().length() >= 65535) {
                    log.setException(log.getException().substring(0, 65534));
                }

                if (logger.isDebugEnabled()) {
					logger.debug("save log {type: " + log.getType() + ", loginName: " + user.getLoginName() + ", uri: " + log.getRequestUri() + "}, ");
				}
            }
        }

    }
    
    /**
     * 缩略字符串（不区分中英文字符）
     *
     * @param str    目标字符串
     * @param length 截取长度
     * @return 缩略字符串
     */
    public String abbr(String str, int length) {
        if (str == null) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (char c : str.toCharArray()) {
                currentLength += String.valueOf(c).getBytes("GBK").length;
                if (currentLength <= length - 3) {
                    sb.append(c);
                } else {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得用户远程地址
     */
    private String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

}
