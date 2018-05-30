//package com.sys.gateway.controller;
//
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.TypeReference;
//import com.sys.boss.api.entry.CommonResponse;
//import com.sys.boss.api.entry.trade.request.selectpay.TradeSelectPayRequest;
//import com.sys.boss.api.entry.trade.response.selectpay.SelectOrderCreateResponse;
//import com.sys.common.enums.ErrorCodeEnum;
//import com.sys.common.util.SignUtil;
//import com.sys.gateway.common.IpUtil;
//import com.sys.gateway.service.OrderGwService;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//
//import java.net.URLDecoder;
//import java.util.Map;
//
///**
// *
// * @Description:
// *
// * @author: ChenZL
// * @time: 2017年7月26日
// */
//@Controller
//@RequestMapping(value = "pay")
//public class OrderGwController {
//
//	protected final Logger logger = LoggerFactory.getLogger(OrderGwController.class);
//
//	@Autowired
//	private OrderGwService orderGwService;
//
//	/**查询接口**/
//	@RequestMapping(value="/gateway/queryOrder")
//	@ResponseBody
//	public String queryOrder(@RequestBody String data, HttpServletRequest request)throws java.io.IOException {
//		logger.info("查询接口收到客户端请求参数：data="+data);
//		SelectOrderCreateResponse response = new SelectOrderCreateResponse();
//		SelectOrderCreateResponse.SelectOrderCreateResponseHead respHead = new SelectOrderCreateResponse.SelectOrderCreateResponseHead();
//		SelectOrderCreateResponse.SelectOrderCreateResponseBody respBody = new SelectOrderCreateResponse.SelectOrderCreateResponseBody();
//		response.setHead(respHead);
//		response.setBody(respBody);
//		String sign = "";
//		response.setSign(sign);
//		try {
//			String ip = IpUtil.getRemoteHost(request);//请求ip
//			logger.info("查询接口获取到客户端请求ip为：ip="+ip);
//			data = URLDecoder.decode(data, "utf-8");
//			if(data.endsWith("=")){
//				data = data.substring(0,data.length()-1);
//			}
//			logger.info("查询接口收到客户端请求参数后做url解码后的值为：data="+data);
//			//校验请求参数
//			CommonResponse checkResp = orderGwService.checkQueryOrderParam(data);
//			logger.info("查询接口校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
//			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
//				respHead.setRespCode(checkResp.getRespCode());
//				respHead.setRespMsg(checkResp.getRespMsg());
//			}else{
//				//掉查单接口
//				String mchtKey = (String)checkResp.getData();
//				TradeSelectPayRequest tradeRequest = JSON.parseObject(data, TradeSelectPayRequest.class);
//				logger.info("查询接口，传入的TradeQueryRequest信息为：TradeQueryRequest="+JSONObject.toJSONString(tradeRequest));
//				response = orderGwService.queryResult(tradeRequest);
//				if(ErrorCodeEnum.SUCCESS.getCode().equals(response.getHead().getRespCode())){
//					// 签名
//					Map<String, String> params =  JSONObject.parseObject(
//							JSON.toJSONString(response.getBody()), new TypeReference<Map<String, String>>(){});
//					logger.info("查询接口，返回给商户时签名原始串为bodyMap="+params);
//					sign = SignUtil.md5Sign(params, mchtKey);
//					response.setSign(sign);
//				}
//				logger.info("查询接口，返回的TradeQueryResponse信息为：TradeQueryResponse="+JSONObject.toJSONString(response));
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("查询接口抛异常"+e.getMessage());
//			respHead.setRespCode(ErrorCodeEnum.FAILURE.getCode());
//			respHead.setRespMsg("支付网关错误："+e.getMessage());
//		}
//		logger.info("查询接口订单，返回下游商户值："+JSON.toJSONString(response));
//		return JSON.toJSONString(response);
//	}
//
//
//}
