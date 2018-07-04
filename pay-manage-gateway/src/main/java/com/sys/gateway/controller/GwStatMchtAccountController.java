package com.sys.gateway.controller;

import com.sys.gateway.common.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 入账接口
 *
 * @author: ChenZL
 * @time: 2018年7月4日
 */
@Controller
@RequestMapping(value = "")
public class GwStatMchtAccountController {

	protected final Logger logger = LoggerFactory.getLogger(GwStatMchtAccountController.class);


	/**
	 * 商户订单手动入账
	 * 调用账务统计模块的入账service
	 *
	 **/
    @RequestMapping(value="/gateway/stat/mchtAccountDetailById")
    @ResponseBody
    public String mchtAccountDetail(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
        String ip = IpUtil.getRemoteHost(request);//请求ip
		//TODO:通过http调用boss-stat模块的StatMchtAccountController.statPayOrder方法,参数id=待入账平台支付订单ID
        return "";
    }

}
