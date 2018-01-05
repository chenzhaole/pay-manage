package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.authcardelement.TradeAuthCardElementRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickPrePayRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickUnbundRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickValidPayRequest;
import com.sys.boss.api.entry.trade.request.wappay.TradeWapRequest;
import com.sys.boss.api.entry.trade.response.authcardelement.AuthCardeLElementCreateResponse;
import com.sys.boss.api.entry.trade.response.quickpay.QuickPrePayOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.quickpay.QuickUnbundOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.quickpay.QuickValidPayOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.wappay.WapOrderCreateResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.PayGwService;

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
 * @Description:网关支付接口--API
 * 
 * @author: ChenZL
 * @time: 2017年9月7日
 */
@Controller
@RequestMapping(value = "pay")
public class PayGwController {
	
	protected final Logger logger = LoggerFactory.getLogger(PayGwController.class);

	@Autowired
	PayGwService payGwService;

	/**wap支付**/
    @RequestMapping(value="/gateway/wapPay")
    @ResponseBody
    public String wapPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
    	logger.info("wap支付收到客户端请求参数：data="+data);
        WapOrderCreateResponse wapResp = new WapOrderCreateResponse();
        WapOrderCreateResponse.WapOrderCreateResponseHead head = new WapOrderCreateResponse.WapOrderCreateResponseHead();

        try {
        	String ip = IpUtil.getRemoteHost(request);//请求ip
    		logger.info("wap支付获取到客户端请求ip为：ip="+ip);
        	data = URLDecoder.decode(data, "utf-8");
    		logger.info("wap支付收到客户端请求参数后做url解码后的值为：data="+data);
    		//校验请求参数
        	CommonResponse checkResp = payGwService.checkWapParam(data);
    		logger.info("wap支付校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
        	if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
        		head.setRespCode(checkResp.getRespCode());
        		head.setRespMsg(checkResp.getRespMsg());
        		wapResp.setHead(head);
        	}else{
        		//掉wap支付接口
        		TradeWapRequest tradeRequest = (TradeWapRequest) checkResp.getData();
				logger.info("掉wap支付接口，传入的TradeWapRequest信息为：TradeWapRequest="+JSONObject.toJSONString(tradeRequest));
				wapResp = (WapOrderCreateResponse) payGwService.wapPay(tradeRequest, ip);
				logger.info("掉wap支付接口，返回的WapOrderCreateResponse信息为：WapOrderCreateResponse="+JSONObject.toJSONString(wapResp));
        	}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("wap支付接口抛异常"+e.getMessage());
			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			head.setRespMsg("支付网关错误："+e.getMessage());
		}
        logger.info("创建wap订单，返回下游商户值："+JSON.toJSONString(wapResp));
        return JSON.toJSONString(wapResp);
    }

	/**扫码支付**/
	@RequestMapping(value="/gateway/scanPay")
	@ResponseBody
	public String scanPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		logger.info("queryListResult收到客户端请求参数：data="+data);
		JSONObject respObject = new JSONObject();
		try {
			//请求ip
			String ip = IpUtil.getRemoteHost(request);
			data = URLDecoder.decode(data, "utf-8");
			CommonResponse checkResp = payGwService.checkScanParam(data);
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				respObject.put("code", "-1");
				respObject.put("desc", checkResp.getRespMsg());
				return JSON.toJSONString(respObject);
			}

			TradeWapRequest tradeRequest = (TradeWapRequest) checkResp.getData();
			Object queryResp = payGwService.scanPay(tradeRequest, ip);

		} catch (Exception e) {
			e.printStackTrace();
			respObject.put("code", "-1");
			respObject.put("desc", "失败");
		}

		return respObject.toJSONString();
	}

	/**公众号支付**/
	@RequestMapping(value="/gateway/pubPay")
	@ResponseBody
	public String pubPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		logger.info("queryListResult收到客户端请求参数：data="+data);
		JSONObject respObject = new JSONObject();
		try {
			//请求ip
			String ip = IpUtil.getRemoteHost(request);
			data = URLDecoder.decode(data, "utf-8");
			CommonResponse checkResp = payGwService.checkPubParam(data);
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				respObject.put("code", "-1");
				respObject.put("desc", checkResp.getRespMsg());
				return JSON.toJSONString(respObject);
			}

			TradeWapRequest tradeRequest = (TradeWapRequest) checkResp.getData();
			Object queryResp = payGwService.pubPay(tradeRequest, ip);

		} catch (Exception e) {
			e.printStackTrace();
			respObject.put("code", "-1");
			respObject.put("desc", "失败");
		}

		return respObject.toJSONString();
	}

	/**快捷支付--快捷预消费接口**/
	@RequestMapping(value="/gateway/quickPrePay")
	@ResponseBody
	public String quickPrePay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		logger.info("快捷支付--快捷预消费接口收到客户端请求参数：data="+data);
		QuickPrePayOrderCreateResponse quickPrePayResp = new QuickPrePayOrderCreateResponse();
		QuickPrePayOrderCreateResponse.QuickPrePayOrderCreateResponseHead head = new QuickPrePayOrderCreateResponse.QuickPrePayOrderCreateResponseHead();

		try {
			String ip = IpUtil.getRemoteHost(request);//请求ip
			logger.info("快捷支付--快捷预消费接口获取到客户端请求ip为：ip="+ip);
			data = URLDecoder.decode(data, "utf-8");
			logger.info("快捷支付--快捷预消费接口收到客户端请求参数后做url解码后的值为：data="+data);
			//校验请求参数
			CommonResponse checkResp = payGwService.checkQuickPrePayParam(data);
			logger.info("快捷支付--快捷预消费接口校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				head.setRespCode(checkResp.getRespCode());
				head.setRespMsg(checkResp.getRespMsg());
				quickPrePayResp.setHead(head);
			}else{
				//掉快捷预消费支付接口
				TradeQuickPrePayRequest tradeRequest = (TradeQuickPrePayRequest) checkResp.getData();
				logger.info("掉快捷支付--快捷预消费接口，传入的TradeQuickPrePayRequest信息为：TradeQuickPrePayRequest="+JSONObject.toJSONString(tradeRequest));
				quickPrePayResp = (QuickPrePayOrderCreateResponse) payGwService.quickPrePay(tradeRequest, ip);
				logger.info("掉快捷支付--快捷预消费接口，返回的QuickPrePayOrderCreateResponse信息为：QuickPrePayOrderCreateResponse="+JSONObject.toJSONString(quickPrePayResp));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("快捷支付--快捷预消费接口抛异常"+e.getMessage());
			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			head.setRespMsg("支付网关错误："+e.getMessage());
		}
		logger.info("创建快捷支付--快捷预消费接口订单，返回下游商户值："+JSON.toJSONString(quickPrePayResp));
		return JSON.toJSONString(quickPrePayResp);
	}

	/**快捷支付--快捷验证支付接口**/
	@RequestMapping(value="/gateway/quickValidPay")
	@ResponseBody
	public String quickValidPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		logger.info("快捷支付--快捷验证支付接口收到客户端请求参数：data="+data);
		QuickValidPayOrderCreateResponse quickValidPayResp = new QuickValidPayOrderCreateResponse();
		QuickValidPayOrderCreateResponse.QuickValidOrderCreateResponseHead head = new QuickValidPayOrderCreateResponse.QuickValidOrderCreateResponseHead();

		try {
			String ip = IpUtil.getRemoteHost(request);//请求ip
			logger.info("快捷支付--快捷验证支付接口获取到客户端请求ip为：ip="+ip);
			data = URLDecoder.decode(data, "utf-8");
			logger.info("快捷支付--快捷验证支付接口收到客户端请求参数后做url解码后的值为：data="+data);
			//校验请求参数
			CommonResponse checkResp = payGwService.checkQuickValidPayParam(data);
			logger.info("快捷支付--快捷验证支付接口校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				head.setRespCode(checkResp.getRespCode());
				head.setRespMsg(checkResp.getRespMsg());
				quickValidPayResp.setHead(head);
			}else{
				//掉快捷验证支付支付接口
				TradeQuickValidPayRequest tradeRequest = (TradeQuickValidPayRequest) checkResp.getData();
				logger.info("掉快捷支付--快捷验证支付接口，传入的TradeQuickValidPayRequest信息为：TradeQuickValidPayRequest="+JSONObject.toJSONString(tradeRequest));
				quickValidPayResp = (QuickValidPayOrderCreateResponse) payGwService.quickValidPay(tradeRequest, ip);
				logger.info("掉快捷支付--快捷验证支付接口，返回的QuickValidOrderCreateResponse信息为：QuickValidOrderCreateResponse="+JSONObject.toJSONString(quickValidPayResp));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("快捷支付--快捷验证支付接口抛异常"+e.getMessage());
			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			head.setRespMsg("支付网关错误："+e.getMessage());
		}
		logger.info("创建快捷支付--快捷验证支付接口订单，返回下游商户值："+JSON.toJSONString(quickValidPayResp));
		return JSON.toJSONString(quickValidPayResp);
	}

	/**快捷支付--快捷解绑接口**/
	@RequestMapping(value="/gateway/quickUnbundPay")
	@ResponseBody
	public String quickUnbundPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		logger.info("快捷支付--快捷解绑接口收到客户端请求参数：data="+data);
		QuickUnbundOrderCreateResponse quickUnbundResp = new QuickUnbundOrderCreateResponse();
		QuickUnbundOrderCreateResponse.QuickUnbundOrderCreateResponseHead head = new QuickUnbundOrderCreateResponse.QuickUnbundOrderCreateResponseHead();

		try {
			String ip = IpUtil.getRemoteHost(request);//请求ip
			logger.info("快捷支付--快捷解绑接口获取到客户端请求ip为：ip="+ip);
			data = URLDecoder.decode(data, "utf-8");
			logger.info("快捷支付--快捷解绑接口收到客户端请求参数后做url解码后的值为：data="+data);
			//校验请求参数
			CommonResponse checkResp = payGwService.checkQuickUnbundParam(data);
			logger.info("快捷支付--快捷解绑接口校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				head.setRespCode(checkResp.getRespCode());
				head.setRespMsg(checkResp.getRespMsg());
				quickUnbundResp.setHead(head);
			}else{
				//掉快捷解绑支付接口
				TradeQuickUnbundRequest tradeRequest = (TradeQuickUnbundRequest) checkResp.getData();
				logger.info("掉快捷支付--快捷解绑接口，传入的TradeQuickUnbundRequest信息为：TradeQuickUnbundRequest="+JSONObject.toJSONString(tradeRequest));
				quickUnbundResp = (QuickUnbundOrderCreateResponse) payGwService.quickUnbundPay(tradeRequest, ip);
				logger.info("掉快捷支付--快捷解绑接口，返回的QuickValidOrderCreateResponse信息为：QuickValidOrderCreateResponse="+JSONObject.toJSONString(quickUnbundResp));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("快捷支付--快捷解绑接口抛异常"+e.getMessage());
			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			head.setRespMsg("支付网关错误："+e.getMessage());
		}
		logger.info("创建快捷支付--快捷解绑接口订单，返回下游商户值："+JSON.toJSONString(quickUnbundResp));
		return JSON.toJSONString(quickUnbundResp);
	}

	/**四要素和六要素认证**/
	@RequestMapping(value="/gateway/authCardElement")
	@ResponseBody
	public String authCardElement(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		logger.info("实名认证即四要素、六要素认证接口收到客户端请求参数：data="+data);
		AuthCardeLElementCreateResponse authResp = new AuthCardeLElementCreateResponse();
		AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseHead head = new AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseHead();

		try {
			String ip = IpUtil.getRemoteHost(request);//请求ip
			logger.info("实名认证即四要素、六要素认证接口获取到客户端请求ip为：ip="+ip);
			data = URLDecoder.decode(data, "utf-8");
			logger.info("实名认证即四要素、六要素认证接口收到客户端请求参数后做url解码后的值为：data="+data);
			//校验请求参数
			CommonResponse checkResp = payGwService.checkAuthCardParam(data);
			logger.info("实名认证即四要素、六要素认证接口校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				head.setRespCode(checkResp.getRespCode());
				head.setRespMsg(checkResp.getRespMsg());
				authResp.setHead(head);
			}else{
				//掉实名认证即四要素、六要素认证接口
				TradeAuthCardElementRequest tradeRequest = (TradeAuthCardElementRequest) checkResp.getData();
				logger.info("掉实名认证即四要素、六要素认证接口，传入的TradeAuthCardElementRequest信息为：TradeAuthCardElementRequest="+JSONObject.toJSONString(tradeRequest));
				authResp = (AuthCardeLElementCreateResponse) payGwService.authCardElement(tradeRequest, ip);
				logger.info("掉实名认证即四要素、六要素认证接口，返回的QuickValidOrderCreateResponse信息为：QuickValidOrderCreateResponse="+JSONObject.toJSONString(authResp));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("实名认证即四要素、六要素认证接口抛异常"+e.getMessage());
			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			head.setRespMsg("支付网关错误："+e.getMessage());
		}
		logger.info("创建实名认证即四要素、六要素认证接口订单，返回下游商户值："+JSON.toJSONString(authResp));
		return JSON.toJSONString(authResp);
	}
}
