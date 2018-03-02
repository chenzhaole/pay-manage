//package com.sys.manage.gateway.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sys.boss.api.entry.CommonResponse;
//import com.sys.boss.api.entry.trade.request.authcardelement.TradeAuthCardElementRequest;
//import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickPrePayRequest;
//import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickUnbundRequest;
//import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickValidPayRequest;
//import com.sys.boss.api.entry.trade.request.wappay.TradeWapRequest;
//import com.sys.boss.api.entry.trade.response.authcardelement.AuthCardeLElementCreateResponse;
//import com.sys.boss.api.entry.trade.response.quickpay.QuickPrePayOrderCreateResponse;
//import com.sys.boss.api.entry.trade.response.quickpay.QuickUnbundOrderCreateResponse;
//import com.sys.boss.api.entry.trade.response.quickpay.QuickValidPayOrderCreateResponse;
//import com.sys.boss.api.entry.trade.response.wappay.WapOrderCreateResponse;
//import com.sys.common.enums.ErrorCodeEnum;
//import com.sys.manage.gateway.common.IpUtil;
//import com.sys.manage.gateway.service.CommPayService;
//import com.sys.manage.gateway.service.PayGwService;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import java.net.URLDecoder;
//
///**
// * 统一扫码网关支付接口
// *
// * @author: ChenZL
// * @time: 2017年11月28日
// */
//@Controller
//@RequestMapping(value = "")
//public class CommPayController {
//
//	protected final Logger logger = LoggerFactory.getLogger(CommPayController.class);
//
//	@Autowired
//	CommPayService commPayService;
//
//	/**wap支付**/
//    @RequestMapping(value="/gateway/wapPay")
//    @ResponseBody
//    public String wapPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
//        WapOrderCreateResponse wapResp = new WapOrderCreateResponse();
//        WapOrderCreateResponse.WapOrderCreateResponseHead head = new WapOrderCreateResponse.WapOrderCreateResponseHead();
//
//        try {
//        	String ip = IpUtil.getRemoteHost(request);//请求ip
//    		logger.info("wap支付获取到客户端请求ip为：ip="+ip);
//        	data = URLDecoder.decode(data, "utf-8");
//    		logger.info("wap支付收到客户端请求参数后做url解码后的值为：data="+data);
//    		//校验请求参数
//        	CommonResponse checkResp = commPayService.checkWapParam(data);
//    		logger.info("wap支付校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
//        	if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
//        		head.setRespCode(checkResp.getRespCode());
//        		head.setRespMsg(checkResp.getRespMsg());
//        		wapResp.setHead(head);
//        	}else{
//        		//掉wap支付接口
//        		TradeWapRequest tradeRequest = (TradeWapRequest) checkResp.getData();
//				logger.info("掉wap支付接口，传入的TradeWapRequest信息为：TradeWapRequest="+JSONObject.toJSONString(tradeRequest));
//				wapResp = (WapOrderCreateResponse) commPayService.wapPay(tradeRequest, ip);
//				logger.info("掉wap支付接口，返回的WapOrderCreateResponse信息为：WapOrderCreateResponse="+JSONObject.toJSONString(wapResp));
//        	}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("wap支付接口抛异常"+e.getMessage());
//			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
//			head.setRespMsg("支付网关错误："+e.getMessage());
//		}
//        logger.info("创建wap订单，返回下游商户值："+JSON.toJSONString(wapResp));
//        return JSON.toJSONString(wapResp);
//    }
//
//}
