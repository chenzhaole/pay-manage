package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.apipay.ApiQueryRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.boss.api.entry.trade.request.apipay.TradeQueryFaceRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderQueryResponse;
import com.sys.boss.api.entry.trade.response.apipay.QueryFaceResponse;
import com.sys.boss.api.service.trade.handler.ITradeApiQueryHandler;
import com.sys.boss.api.service.trade.handler.ITradeQueryFaceHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.SignUtil;
import com.sys.gateway.service.GwApiQueryService;
import com.sys.gateway.service.GwQueryFaceService;
import com.sys.trans.api.entry.FaceCount;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * 库存面值查询实现类
 */
@Service
public class GwQueryFaceServiceImpl implements GwQueryFaceService {

	protected final Logger logger = LoggerFactory.getLogger(GwQueryFaceService.class);

	@Autowired
	private ITradeQueryFaceHandler tradeQueryFaceHandler;


	/**库存面值检验参数**/
	@Override
	public CommonResponse checkParam(String paramStr) {
		CommonResponse checkResp = new CommonResponse();
		try {
			if(paramStr.endsWith("=")){
				paramStr = paramStr.substring(0,paramStr.length()-1);
			}
			//解析请求参数
			TradeQueryFaceRequest queryFaceRequest = JSON.parseObject(paramStr, TradeQueryFaceRequest.class);
			if (queryFaceRequest.getMchtId() == null || queryFaceRequest.getSign() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[mchtId],[sign]请求参数值不能为空");
				logger.error("[mchtId],[sign]请求参数值不能为空，即TradeCommRequest=："+ JSONObject.toJSONString(queryFaceRequest));
				return checkResp;
			}

			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
			checkResp.setData(queryFaceRequest);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("库存面值校验参数异常："+e.getMessage());
			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
		}
		return checkResp;
	}


	/**库存面值查询接口*/
	@Override
	public QueryFaceResponse query(TradeQueryFaceRequest tradeRequest, String ip) {
		QueryFaceResponse queryFaceResponse = new QueryFaceResponse();
		try {
			logger.info("调用boss-trade查询面值库存，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
			CommonResult commonResult = tradeQueryFaceHandler.process(tradeRequest, ip);
			logger.info("调用boss-trade查询面值库存，返回值commonResult：" + JSON.toJSONString(commonResult));

			if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
				queryFaceResponse.setCode(commonResult.getRespCode());
				queryFaceResponse.setMsg(commonResult.getRespMsg());
				queryFaceResponse.setData(JSON.toJSON(commonResult.getData()));
			}else{
				String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
				String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
				queryFaceResponse.setCode(respCode);
				queryFaceResponse.setMsg(respMsg);
			}
		} catch (Exception e) {
			queryFaceResponse.setCode(ErrorCodeEnum.E8001.getCode());
			queryFaceResponse.setMsg(ErrorCodeEnum.E8001.getDesc());
			logger.error("面值库存查询异常",e);
		}
		logger.info("queryFaceResponse="+JSON.toJSONString(queryFaceResponse));
		return queryFaceResponse;
	}

}
