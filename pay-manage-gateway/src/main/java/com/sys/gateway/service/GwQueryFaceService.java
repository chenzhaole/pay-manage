package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.apipay.TradeQueryFaceRequest;
import com.sys.boss.api.entry.trade.response.TradeBaseResponse;
import com.sys.boss.api.entry.trade.response.apipay.QueryFaceResponse;


/**
 *
 * 面值库存
 */
public interface GwQueryFaceService {


	/**面值库存查询校验参数**/
	CommonResponse checkParam(String paramStr);

	/**面值库存接口*/
	QueryFaceResponse query(TradeQueryFaceRequest tradeRequest, String ip);

}
