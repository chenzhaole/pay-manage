package com.sys.admin.common.utils;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {

	/**
	 * 获取请求来源ip
	 * @param request
	 * @return
	 */
	public static String getRemoteHost(HttpServletRequest request){
		String ip = "";
		String ipstr = request.getHeader("X-Forwarded-For");
		System.out.println("新网关：X-Forwarded-For 获取IP："+ipstr);
		if(StringUtils.isNotBlank(ipstr) && ipstr.contains(",")) {
			ip = ipstr.split(",")[0];
			System.out.println("新网关：X-Forwarded-For，获取的原始ip组为:"+ipstr+", 对ip数组切割后获取的第一个IP："+ip);
		}else {
			ip = request.getHeader("X-Real-IP");
			System.out.println("新网关：X-Forwarded-For 获取的不是IP数组："+ipstr+"说明没用负载均衡策略,采用X-Real-IP获取的ip值为："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
			System.out.println("新网关：X-Forwarded-For 获取IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			System.out.println("新网关：Proxy-Client-IP 获取IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			System.out.println("新网关：WL-Proxy-Client-IP 获取IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			System.out.println("新网关：HTTP_CLIENT_IP 获取IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			System.out.println("新网关：HTTP_X_FORWARDED_FOR 获取IP："+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			System.out.println("新网关：getRemoteAddr 获取IP："+ip);
		}
		if(ip==null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "0:0:0:0:0:0:0:1".equals(ip)){
			ip = "127.0.0.1";
		}
		return ip;
	}

}
