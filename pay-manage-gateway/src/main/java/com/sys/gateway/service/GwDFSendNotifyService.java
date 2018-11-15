package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.core.dao.dmo.PlatProxyDetail;

/**
 * @Description:代付明细通知商户交易结果
 * 
 * @author: duanjintang
 * @time: 2018年11月1日
 */
public interface GwDFSendNotifyService {

	CommonResult sendNotify(PlatProxyDetail detail,String batchStatus,String notifyUrl,String log_tag);

}
