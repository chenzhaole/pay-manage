package com.sys.manage.notify.service;

import com.sys.boss.api.entry.CommonResult;

/**
 * @Description:重新通知商户交易结果
 * 
 * @author: ChenZL
 * @time: 2018年2月6日
 */
public interface GwRecNotifyService {


	CommonResult reciveNotify(String channelCode, String orderNo, String payType, String data);
	CommonResult reciveNotify(String data, String sign);

}
