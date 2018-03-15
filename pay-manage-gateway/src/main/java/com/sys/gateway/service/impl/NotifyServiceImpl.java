package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.boss.api.service.trade.handler.ITradePayNotifyHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.SignUtil;
import com.sys.gateway.service.NotifyService;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.Order;
import com.sys.trans.api.entry.QuickPay;
import com.sys.trans.api.entry.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @Description:网关支付业务处理实现类
 * 
 * @author: ChenZL
 * @time: 2017年9月9日
 */
@Service
public class NotifyServiceImpl implements NotifyService {

	protected final Logger logger = LoggerFactory.getLogger(NotifyServiceImpl.class);

	@Autowired
	ITradePayNotifyHandler tradePayNotifyHandler;

	/**
	 * 接收并解析上有通道的通知内容
     */
	@Override
	public CommonResult notify4chan(String channelCode, String orderNo, String data) {

		Config config = new Config();
		config.setChannelCode(channelCode);
		Order order = new Order();
		order.setOrderNo(orderNo);

		//调用boss-trade获取缓存中的orderTrade
		CommonResult commonResult = tradePayNotifyHandler.process(null);

		return commonResult;
	}

	/**
	 * 向下游商户发送订单结果
	 *
     * @return 下游商户返回的响应
     */
	@Override
	public CommonResult notify2mcht(String payType, Trade trade) {

		CommonResult commonResult = new CommonResult();
		commonResult.setRespCode(ErrorCodeEnum.FAILURE.getCode());
		TradeNotify tradeNotify = null;

		//根据支付类型返回不同格式数据
		if(payType.startsWith("wx") || payType.startsWith("ali") || payType.startsWith("qq")){
			//收银台支付
			tradeNotify = buildTradeNotify4Order(trade,"status");
		}
		else if (PayTypeEnum.QUICK_TX.equals(payType) || PayTypeEnum.QUICK.equals(payType)) {
			//快捷
			QuickPay quick = trade.getQuickPay();
			tradeNotify = buildTradeNotify4TXQuick(trade,"status");
		}
		else if (PayTypeEnum.MERCHANT_REGISTER.equals(payType)) {
			//商户入驻
			tradeNotify = buildTradeNotify4Registe(trade,"status");
		}

		String url = tradeNotify.getUrl();
		String contentType="application/json";
		String content = JSON.toJSONString(tradeNotify.getResponse());
		try {
			//HTTP异步通知商户交易结果
			String mchtRes = HttpUtil.postConnManager(url,content,contentType,"UTF-8", "UTF-8");
			logger.info("异步通知商户返回:"+mchtRes);
			if("SUCCESS".equals(mchtRes)){
				commonResult.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("异步通知商户系统异常,e.msg:"+e.getMessage());
			commonResult.setRespMsg("异步通知商户系统异常,e.msg:"+e.getMessage());
		}

		return null;
	}



	/**
	 * 支付订单通知
	 */
	private TradeNotify buildTradeNotify4Order(Trade trade, String status) {
		Config config = trade.getConfig();
		Order order = trade.getOrder();
		Map oriReqMap = (Map) trade.getData();
		TradeNotify tradeNotify = new TradeNotify();

		try {
			String respCode = ErrorCodeEnum.SUCCESS.getCode();
			String respMsg = ErrorCodeEnum.SUCCESS.getDesc();

			String mchtId = config.getMchtId();
			String orderId = order.getMchtOrderNo();
			String tradeId = order.getOrderNo();
			String bankCardNo = order.getBankCardNo();
			status = PayStatusEnum.PAY_SUCCESS.getCode().equals(status) ?
					"SUCCESS" : (PayStatusEnum.PROCESSING.getCode().equals(status) ? "PROCESSING" : "FAILURE");
			String amount = order.getAmount();
			String mchtKey = config.getMchtKey();
			String notifyUrl = (String) oriReqMap.get("notifyUrl");
			tradeNotify.setUrl(notifyUrl);

			TradeNotifyResponse tradeNotifyResponse = new TradeNotifyResponse();
			TradeNotifyResponse.TradeNotifyResponseHead head = new TradeNotifyResponse.TradeNotifyResponseHead();
			head.setRespCode(respCode);
			head.setRespMsg(respMsg);
			TradeNotifyResponse.TradeNotifyBody body = new TradeNotifyResponse.TradeNotifyBody();
			body.setMchtId(mchtId);
			body.setOrderId(orderId);
			body.setTradeId(tradeId);
			body.setStatus(status);
			body.setAmount(amount);
			body.setBankCardNo(bankCardNo);

			tradeNotifyResponse.setHead(head);
			tradeNotifyResponse.setBody(body);

			TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(body);
			String sign = SignUtil.md5Sign(new HashMap<String, String>(treeMap), mchtKey);
			tradeNotifyResponse.setSign(sign);

			tradeNotify.setResponse(tradeNotifyResponse);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("组合商户异步通知对象异常 msg：" + e.getMessage());
		}

		return tradeNotify;
	}


	/**
	 * 商户入驻结果通知
	 */
	private TradeNotify buildTradeNotify4Registe(Trade trade, String status) {
		//TODO:
		return null;
	}

	/**
	 * TX快捷订单通知
	 */
	private TradeNotify buildTradeNotify4TXQuick(Trade trade, String status) {
		//TODO:
		return null;
	}

}
