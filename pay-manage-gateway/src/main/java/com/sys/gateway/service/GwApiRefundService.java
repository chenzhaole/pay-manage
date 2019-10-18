package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.response.TradeBaseResponse;


/**
 *
 * @Description:退款接口业务处理接口
 *
 * @author: ChenZL
 * @time: 2019年10月18日
 */
public interface GwApiRefundService {


	/**wap支付校验参数**/
	CommonResponse checkParam(String paramStr);

	/**wap支付接口*/
	TradeBaseResponse refund(TradeBaseRequest tradeRequest, String ip);

}
