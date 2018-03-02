package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;

/**
 * @Description:重新通知商户交易结果
 * 
 * @author: ChenZL
 * @time: 2018年2月6日
 */
public interface ReNotify2MchtGwService {

	CommonResponse notify2mcht(String mchtId, String mchtKey, String url, String Content);

}
