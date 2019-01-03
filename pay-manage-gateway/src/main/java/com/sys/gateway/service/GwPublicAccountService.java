package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.apipay.TradeQueryFaceRequest;
import com.sys.boss.api.entry.trade.response.apipay.QueryFaceResponse;

import java.util.Map;


/**
 *
 * 面值库存
 */
public interface GwPublicAccountService {

	/**公户余额查询检验参数**/
	CommonResponse checkParam(Map<String,String> map);

	/**公户余额查询查询接口*/
	Map query(Map<String,String> params);

}
