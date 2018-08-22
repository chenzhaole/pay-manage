package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.apipay.ApiPayRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.ApiQueryRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiPayRequest;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderQueryResponse;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.boss.api.service.trade.handler.ITradeApiQueryHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.SignUtil;
import com.sys.gateway.service.GwApiPayService;
import com.sys.gateway.service.GwApiQueryService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 *
 * API查询支付订单实现类
 */
@Service
public class GwApiQueryServiceImpl implements GwApiQueryService {

	protected final Logger logger = LoggerFactory.getLogger(GwApiQueryService.class);

	@Autowired
	private ITradeApiQueryHandler tradeApiQueryHandler;


	/**API支付查单检验参数**/
	@Override
	public CommonResponse checkParam(String paramStr) {
		CommonResponse checkResp = new CommonResponse();
		try {
			if(paramStr.endsWith("=")){
				paramStr = paramStr.substring(0,paramStr.length()-1);
			}
			//解析请求参数
			TradeApiQueryRequest tradeRequest = JSON.parseObject(paramStr, TradeApiQueryRequest.class);
			if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
				logger.error("[head],[body],[sign]请求参数值不能为空，即TradeCommRequest=："+ JSONObject.toJSONString(tradeRequest));
				return checkResp;
			}

			TradeReqHead head = tradeRequest.getHead();
			if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
				logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+ JSONObject.toJSONString(head));
				return checkResp;
			}

			ApiQueryRequestBody body = tradeRequest.getBody();
			if (StringUtils.isBlank(body.getOrderId())
					|| StringUtils.isBlank(body.getOrderTime())) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[orderId],[orderTime]请求参数值不能为空");
				logger.error("[orderId],[orderTime]请求参数值不能为空，即CommRequestBody=："+ JSONObject.toJSONString(body));
				return checkResp;
			}


			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
			checkResp.setData(tradeRequest);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("API支付查单接口校验参数异常："+e.getMessage());
			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
		}
		return checkResp;
	}


	/**API支付查单接口*/
	@Override
	public ApiPayOrderQueryResponse query(TradeBaseRequest tradeRequest, String ip) {

		ApiPayOrderQueryResponse.ApiPayOrderQueryResponseHead head = new ApiPayOrderQueryResponse.ApiPayOrderQueryResponseHead();
		ApiPayOrderQueryResponse.ApiPayOrderQueryResponseBody body = new ApiPayOrderQueryResponse.ApiPayOrderQueryResponseBody();
		String sign  ="";
		try {
			logger.info("调用boss-trade查询支付订单，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
			CommonResult commonResult = tradeApiQueryHandler.process(tradeRequest, ip);
			logger.info("调用boss-trade查询支付订单，返回值commonResult：" + JSON.toJSONString(commonResult));
			if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                Map<String, String> retData = (Map<String, String>) commonResult.getData();
                String data = retData.get("data");
                String signKey = retData.get("signKey");
				// 签名
				Map<String, String> params =  JSON.parseObject(data, Map.class);
				String log_moid = params.get("mchtId")+"-->"+params.get("orderId");
				sign = SignUtil.md5Sign(params, signKey, log_moid);
                head.setRespCode(commonResult.getRespCode());
                head.setRespMsg(commonResult.getRespMsg());
                body = JSON.parseObject(data, ApiPayOrderQueryResponse.ApiPayOrderQueryResponseBody.class);
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
			logger.error("查询支付API支付订单异常 e=" + e.getMessage());
		}
		ApiPayOrderQueryResponse apiOrderQueryResponse = new ApiPayOrderQueryResponse(head, body, sign);
		logger.info("ApiPayOrderQueryResponse="+JSON.toJSONString(apiOrderQueryResponse));
		return apiOrderQueryResponse;
	}


}
