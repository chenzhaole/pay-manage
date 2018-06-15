package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickPrePayRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickValidPayRequest;
import com.sys.boss.api.entry.trade.response.TradeBaseResponse;


/**
 *
 * @Description:网关接口业务处理接口
 *
 * @author: ChenZL
 * @time: 2017年11月28日
 */
public interface GwApiPayService {


	/**wap支付校验参数**/
	CommonResponse checkParam(String paramStr);

	/**wap支付接口*/
	TradeBaseResponse pay(TradeBaseRequest tradeRequest, String ip);

}
