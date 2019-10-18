package com.sys.gateway.common;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chenzhaole on 2019/7/20.
 */
public class ClientUtil {

    /**
     *
     * @param request
     * @return  1=支付宝  2=微信
     */
    public static int getUserAgentType(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        if (userAgent != null && userAgent.contains("AlipayClient")) {
            return 1;
        } else if (userAgent != null && userAgent.contains("MicroMessenger")) {
            return 2;
        } else {
            return -1;
        }
    }

}
