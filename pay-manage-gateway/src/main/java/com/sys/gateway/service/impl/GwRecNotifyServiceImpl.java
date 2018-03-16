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
import com.sys.gateway.service.GwRecNotifyService;
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
public class GwRecNotifyServiceImpl implements GwRecNotifyService {

	protected final Logger logger = LoggerFactory.getLogger(GwRecNotifyService.class);


	@Autowired
	ITradePayNotifyHandler tradePayNotifyHandler;

	/**
	 * 接收并解析上有通道的通知内容
     */
	@Override
	public CommonResult reciveNotify(String channelCode, String orderNo, String data) {

		Config config = new Config();
		config.setChannelCode(channelCode);
		Order order = new Order();
		order.setOrderNo(orderNo);

		//调用boss-trade获取缓存中的orderTrade
		CommonResult commonResult = tradePayNotifyHandler.process(null);

		return commonResult;
	}



}
