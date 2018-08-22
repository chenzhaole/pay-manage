package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderQueryResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwApiPayService;
import com.sys.gateway.service.GwApiQueryService;
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
 * 查询支付订单接口
 */
@Controller
@RequestMapping(value = "")
public class GwApiQueryController {

	protected final Logger logger = LoggerFactory.getLogger(GwApiQueryController.class);

	@Autowired
	GwApiQueryService gwApiQueryService;
	/**
	 * api支付查询支付订单
     */
	@RequestMapping(value="/gateway/api/queryPay")
	@ResponseBody
	public String queryPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		ApiPayOrderQueryResponse apiPayResp = new ApiPayOrderQueryResponse();
		ApiPayOrderQueryResponse.ApiPayOrderQueryResponseHead head = new ApiPayOrderQueryResponse.ApiPayOrderQueryResponseHead();

		try {
			//请求ip
			String ip = IpUtil.getRemoteHost(request);
			logger.info("queryPay查询支付订单获取到客户端请求ip："+ip);
			data = URLDecoder.decode(data, "utf-8");
			logger.info("queryPay查询支付订单收到客户端请求参数后做url解码后的值为："+data);
			//校验请求参数
			CommonResponse checkResp = gwApiQueryService.checkParam(data);
			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
			logger.info("queryPay查询支付订单校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				head.setRespCode(checkResp.getRespCode());
				head.setRespMsg(checkResp.getRespMsg());
				apiPayResp.setHead(head);
			}else{
				//queryPay查询支付订单接口
				TradeApiQueryRequest tradeRequest = (TradeApiQueryRequest) checkResp.getData();
				logger.info("调用queryPay查询支付订单接口，传入的TradeCommRequest信息："+JSONObject.toJSONString(tradeRequest));
				apiPayResp = (ApiPayOrderQueryResponse) gwApiQueryService.query(tradeRequest, ip);
				logger.info("调用queryPay查询支付订单接口，返回的CommOrderQueryResponse信息："+JSONObject.toJSONString(apiPayResp));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("queryPay查询支付订单接口抛异常"+e.getMessage());
			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			head.setRespMsg("支付网关错误："+e.getMessage());
		}
		logger.info("queryPay查询支付订单接口，返回下游商户值："+JSON.toJSONString(apiPayResp));
		return JSON.toJSONString(apiPayResp);
	}

}
