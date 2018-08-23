package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import java.util.Map;


/**
 *
 * @Description:支付SDK查询订单接口
 */
public interface GwSdkQueryService {


	/** 校验参数 **/
	CommonResponse checkParam(String paramStr);

	/** 查询支付订单 */
	Map query(Map map, String ip);

}
