package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.myepay.tran.api.entry.Result;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.authcardelement.AuthCardElementRequestBody;
import com.sys.boss.api.entry.trade.request.authcardelement.TradeAuthCardElementRequest;
import com.sys.boss.api.entry.trade.request.quickpay.*;
import com.sys.boss.api.entry.trade.request.wappay.TradeWapRequest;
import com.sys.boss.api.entry.trade.request.wappay.WapRequestBody;
import com.sys.boss.api.entry.trade.response.TradeBaseResponse;
import com.sys.boss.api.entry.trade.response.authcardelement.AuthCardeLElementCreateResponse;
import com.sys.boss.api.entry.trade.response.quickpay.QuickPrePayOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.quickpay.QuickUnbundOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.quickpay.QuickValidPayOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.selectpay.SelectOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.wappay.WapOrderCreateResponse;
import com.sys.boss.api.service.config.ChanBankcardValidService;
import com.sys.boss.api.service.config.PlatBankcardValidService;
import com.sys.boss.api.service.trade.TradeService;
import com.sys.boss.api.service.trade.handler.ITradeCommPayHandler;
import com.sys.boss.dao.dmo.ChanBankcardValid;
import com.sys.boss.dao.dmo.PlatBankcardValid;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.SignUtil;
import com.sys.gateway.service.CommPayService;
import com.sys.gateway.service.PayGwService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @Description:网关支付业务处理实现类
 * 
 * @author: ChenZL
 * @time: 2017年11月28日
 */
@Service
public class CommPayServiceImpl implements CommPayService {
	
	protected final Logger logger = LoggerFactory.getLogger(CommPayServiceImpl.class);

	@Autowired
	private ITradeCommPayHandler tradeCommPayHandler;


	/**wap支付校验参数**/
	@Override
	public CommonResponse checkWapParam(String paramStr) {
		CommonResponse checkResp = new CommonResponse();
		try {
			if(paramStr.endsWith("=")){
				paramStr = paramStr.substring(0,paramStr.length()-1);
			}
			//解析请求参数
			TradeWapRequest tradeRequest = JSON.parseObject(paramStr, TradeWapRequest.class);
			if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
				logger.error("[head],[body],[sign]请求参数值不能为空，即TradeWapRequest=："+ JSONObject.toJSONString(tradeRequest));
				return checkResp;
			}
			
			TradeReqHead head = tradeRequest.getHead();
			if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
				logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+ JSONObject.toJSONString(head));
				return checkResp;
			}
			
			WapRequestBody body = tradeRequest.getBody();
			if (StringUtils.isBlank(body.getOrderId())
					|| StringUtils.isBlank(body.getAmount())
					|| StringUtils.isBlank(body.getGoods())
					|| StringUtils.isBlank(body.getNotifyUrl())
					|| StringUtils.isBlank(body.getOrderTime())) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空");
				logger.error("[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空，即WapRequestBody=："+ JSONObject.toJSONString(body));
				return checkResp;
			}

			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
			checkResp.setData(tradeRequest);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("wap支付校验参数异常："+e.getMessage());
			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
		}
		return checkResp;
	}


	/**wap支付接口*/
	@Override
	public WapOrderCreateResponse wapPay(TradeBaseRequest tradeRequest, String ip) {

		WapOrderCreateResponse.WapOrderCreateResponseHead head = new WapOrderCreateResponse.WapOrderCreateResponseHead();
		WapOrderCreateResponse.WapOrderCreateResponseBody body = new WapOrderCreateResponse.WapOrderCreateResponseBody();
		String sign  ="";
		try {
			logger.info("调用boss-trade创建wap订单，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
			CommonResult commonResult = (CommonResult) tradeCommPayHandler.process(tradeRequest, ip);
			logger.info("调用boss-trade创建wap订单，返回值commonResult：" + JSON.toJSONString(commonResult));
			if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
				Result mchtResult = (Result) commonResult.getData();
				head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
				head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
				body.setMchtId(mchtResult.getMchtId());
				body.setOrderId(mchtResult.getMchtOrderNo());//商户订单号
				body.setPayUrl(mchtResult.getPayInfo());
				body.setTradeId(mchtResult.getOrderNo());//平台订单号
				// 签名
				Map<String, String> params =  JSONObject.parseObject(
						JSON.toJSONString(body), new TypeReference<Map<String, String>>(){});
				sign = SignUtil.md5Sign(params, mchtResult.getMchtKey());
			}else{
				String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
				String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
				head.setRespCode(respCode);
				head.setRespMsg(respMsg);
			}
		} catch (Exception e) {
			head.setRespCode(ErrorCodeEnum.E8001.getCode());
			head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
			e.printStackTrace();
			logger.error("创建wap支付订单异常 e=" + e.getMessage());
		}
		WapOrderCreateResponse wapOrderCreateResponse = new WapOrderCreateResponse(head, body, sign);
		logger.info("返回gateway客户端WapOrderCreateResponse="+JSON.toJSONString(wapOrderCreateResponse));
		return wapOrderCreateResponse;
	}


}
