package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import java.util.Map;


/**
 *
 * @Description:支付SDK创建订单接口
 */
public interface GwSdkPayService {


	/** 校验参数 **/
	CommonResponse checkParam(String paramStr);

	/** 支付接口 */
	Map pay(Map tradeRequest, String ip);

}
