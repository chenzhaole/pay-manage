package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResult;
import com.sys.trans.api.entry.Trade;

/**
 * @Description:重新通知商户交易结果
 * 
 * @author: ChenZL
 * @time: 2018年2月6日
 */
public interface GwSendNotifyService {

	CommonResult sendNotify(String payType, Trade redisOrderTrade);

	CommonResult sendNotifyAgain(String platOrderId, String suffix);

}
