package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.selectpay.TradeSelectPayRequest;
import com.sys.boss.api.entry.trade.response.selectpay.SelectOrderCreateResponse;

/**
 * @Description:网关接口业务处理接口
 * 
 * @author: ChenZL
 * @time: 2017年7月30日
 */
public interface OrderGwService {
	

	CommonResponse checkQueryOrderParam(String paramStr);

	SelectOrderCreateResponse queryResult(TradeSelectPayRequest tradeQueryRequest);

}
