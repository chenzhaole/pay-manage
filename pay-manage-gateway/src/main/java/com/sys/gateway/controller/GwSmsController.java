package com.sys.gateway.controller;


import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.common.enums.ErrorCodeEnum;

import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

/**
 * 短信接口
 * <p>
 * Created by chenzhaole on 2018/3/30.
 */

@Controller
public class GwSmsController {

    protected final Logger logger = LoggerFactory.getLogger(GwSmsController.class);

    @Autowired
    GwSmsService gwSmsService;

    /**
     * 发送短信验证码接口
     */
    @RequestMapping(value = "/gateway/sms/send")
    @ResponseBody
    public Object smsSend(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        String BIZ_NAME = "短信接口(发送验证码)-";
        String rtnStr = ErrorCodeEnum.FAILURE.getCode();
        String midoid = "";
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info(BIZ_NAME + "获取到客户端请求ip为：ip=" + ip);

            TreeMap<String, String> data = new TreeMap<String, String>();
            Enumeration<?> temp = request.getParameterNames();
            if (null != temp) {
                while (temp.hasMoreElements()) {
                    String en = (String) temp.nextElement();
                    String value = request.getParameter(en);
                    data.put(en, value);
                }
            }

            logger.info(BIZ_NAME + "收到客户端请求参数后做url解码后的值为：data=" + JSON.toJSONString(data));
            CommonResult smsRes = gwSmsService.smsSend(data);
            midoid = smsRes.getData() == null ? "" : (String) smsRes.getData();
            if (ErrorCodeEnum.SUCCESS.getCode().equals(smsRes.getRespCode())) {
                rtnStr = ErrorCodeEnum.SUCCESS.getCode();
            }

        } catch (Exception e) {
            logger.error(BIZ_NAME + midoid + " 发送验证码异常,e.getMessage()=" + e.getMessage());
            e.printStackTrace();
        }
        logger.info(BIZ_NAME + midoid + " 发送验证码,返回客户端:" + rtnStr);
        return rtnStr;
    }


    /**
     * 校验验证码接口
     */
    @RequestMapping(value = "/gateway/sms/verify")
    @ResponseBody
    public Object smsVerify(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        String BIZ_NAME = "短信接口(校验验证码)-";
        String rtnStr = ErrorCodeEnum.FAILURE.getCode();
        String midoid = "";
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info(BIZ_NAME + "获取到客户端请求ip为：ip=" + ip);

            TreeMap<String, String> data = new TreeMap<String, String>();
            Enumeration<?> temp = request.getParameterNames();
            if (null != temp) {
                while (temp.hasMoreElements()) {
                    String en = (String) temp.nextElement();
                    String value = request.getParameter(en);
                    data.put(en, value);
                }
            }

            logger.info(BIZ_NAME + "收到客户端请求参数后做url解码后的值为：data=" + JSON.toJSONString(data));
            CommonResult smsRes = gwSmsService.smsVerify(data);
            midoid = smsRes.getData() == null ? "" : (String) smsRes.getData();
            if (ErrorCodeEnum.SUCCESS.getCode().equals(smsRes.getRespCode())) {
                rtnStr = ErrorCodeEnum.SUCCESS.getCode();
            }

        } catch (Exception e) {
            logger.error(BIZ_NAME + midoid + " 校验验证码异常,e.getMessage()=" + e.getMessage());
            e.printStackTrace();
        }

        logger.info(BIZ_NAME + midoid + " 发送验证码,返回客户端:" + rtnStr);
        return rtnStr;
    }

}
