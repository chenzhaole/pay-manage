package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiPayRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderCreateResponse;
import com.sys.common.enums.ErrorCodeEnum;

import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwApiPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLDecoder;

/**
 * 通用支付接口
 *
 * @author: ChenZL
 * @time: 2017年11月28日
 */
@Controller
@RequestMapping(value = "")
public class GwApiPayController {

	protected final Logger logger = LoggerFactory.getLogger(GwApiPayController.class);

	@Autowired
	GwApiPayService gwApiPayService;

	/**支付**/
    @RequestMapping(value="/gateway/api/commPay")
    @ResponseBody
    public String commPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
        ApiPayOrderCreateResponse apiPayResp = new ApiPayOrderCreateResponse();
        ApiPayOrderCreateResponse.ApiPayOrderCreateResponseHead head = new ApiPayOrderCreateResponse.ApiPayOrderCreateResponseHead();

        try {
        	String ip = IpUtil.getRemoteHost(request);//请求ip
    		logger.info("comm支付获取到客户端请求ip："+ip);
        	data = URLDecoder.decode(data, "utf-8");
    		logger.info("comm支付收到客户端请求参数后做url解码后的值为："+data);
    		//校验请求参数
        	CommonResponse checkResp = gwApiPayService.checkParam(data);
    		logger.info("comm支付校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
        	if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
        		head.setRespCode(checkResp.getRespCode());
        		head.setRespMsg(checkResp.getRespMsg());
        		apiPayResp.setHead(head);
        	}else{
        		//掉comm支付接口
        		TradeApiPayRequest tradeRequest = (TradeApiPayRequest) checkResp.getData();
				logger.info("调用comm支付接口，传入的TradeCommRequest信息："+JSONObject.toJSONString(tradeRequest));
				apiPayResp = (ApiPayOrderCreateResponse) gwApiPayService.pay(tradeRequest, ip);
				logger.info("调用comm支付接口，返回的CommOrderCreateResponse信息："+JSONObject.toJSONString(apiPayResp));
        	}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("comm支付接口抛异常"+e.getMessage());
			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			head.setRespMsg("支付网关错误："+e.getMessage());
		}
        logger.info("创建comm订单，返回下游商户值："+JSON.toJSONString(apiPayResp));
        return JSON.toJSONString(apiPayResp);
    }

}
