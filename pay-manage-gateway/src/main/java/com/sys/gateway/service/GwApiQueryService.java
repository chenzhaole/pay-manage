package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.response.TradeBaseResponse;


/**
 *
 * 查询支付订单
 */
public interface GwApiQueryService {


	/**wap支付校验参数**/
	CommonResponse checkParam(String paramStr);

	/**wap支付接口*/
	TradeBaseResponse query(TradeBaseRequest tradeRequest, String ip);

}
