package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.trans.api.entry.Trade;

/**
 * @Description:重新通知商户交易结果
 * 
 * @author: ChenZL
 * @time: 2018年2月6日
 */
public interface NotifyService {


	CommonResult notify4chan(String channelCode, String orderNo, String data);

	CommonResult notify2mcht(String payType, Trade redisOrderTrade);

}
