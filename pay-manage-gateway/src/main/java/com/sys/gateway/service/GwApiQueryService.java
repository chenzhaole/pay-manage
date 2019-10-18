package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.response.TradeBaseResponse;
import com.sys.core.dao.dmo.MchtGatewayOrder;

import java.util.List;
import java.util.Map;


/**
 *
 * 查询支付订单
 */
public interface GwApiQueryService {


	/** 校验参数 **/
	CommonResponse checkParam(String paramStr);

	/** 数据集总数 **/
	int amount(MchtGatewayOrder order);

	/** 查询接口-单笔 */
	TradeBaseResponse query(TradeBaseRequest tradeRequest, String ip);

	/** 查询接口-列表 */
	List<MchtGatewayOrder> list(MchtGatewayOrder mchtGatewayOrder);
	List<MchtGatewayOrder> list(Map param);

}
