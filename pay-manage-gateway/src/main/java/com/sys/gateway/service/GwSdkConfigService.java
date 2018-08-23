package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import java.util.Map;


/**
 *
 * @Description:支付SDK获取配置信息接口
 */
public interface GwSdkConfigService {


	/** 校验参数 **/
	CommonResponse checkParam(String paramStr);

	/** 获取配置信息 **/
	Map config(Map map, String ip);

}
