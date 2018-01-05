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
 * @time: 2017年9月9日
 */
public interface PayGwService {

	/**wap支付校验参数**/
	CommonResponse checkWapParam(String paramStr);
	/**扫码支付校验参数**/
	CommonResponse checkScanParam(String paramStr);
	/**w公众号支付校验参数**/
	CommonResponse checkPubParam(String paramStr);
	/**支付查询接口校验参数**/
	CommonResponse checkSelectParam(String paramStr);
	/**实名认证接口即四要素、六要素认证校验参数**/
	CommonResponse checkAuthCardParam(String paramStr);
	/**快捷预消费接口校验参数**/
	CommonResponse checkQuickPrePayParam(String paramStr);
	/**快捷验证接口校验参数*/
	CommonResponse checkQuickValidPayParam(String paramStr);
	/**快捷解绑接口校验参数*/
	CommonResponse checkQuickUnbundParam(String paramStr);

	/**wap支付接口*/
	TradeBaseResponse wapPay(TradeBaseRequest tradeRequest, String ip);

	/**实名认证即四要素、六要素认证接口*/
	TradeBaseResponse authCardElement(TradeBaseRequest tradeRequest, String ip);

	/**公众号支付接口*/
	TradeBaseResponse pubPay(TradeBaseRequest tradeRequest, String ip);

	/**支付查单接口*/
	TradeBaseResponse queryOrder(TradeBaseRequest tradeRequest, String ip);

	/**快捷预下单接口*/
	TradeBaseResponse quickPrePay(TradeQuickPrePayRequest tradeRequest, String ip);

	/**快捷解绑接口*/
	TradeBaseResponse quickUnbundPay(TradeBaseRequest tradeRequest, String ip);

	/**快捷验证支付接口*/
	TradeBaseResponse quickValidPay(TradeQuickValidPayRequest tradeRequest, String ip);

	/**扫码支付接口*/
	TradeBaseResponse scanPay(TradeBaseRequest tradeRequest, String ip);


}
