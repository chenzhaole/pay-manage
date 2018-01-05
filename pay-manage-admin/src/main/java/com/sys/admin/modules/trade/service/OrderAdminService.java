package com.sys.admin.modules.trade.service;

import com.sys.core.dao.dmo.MchtGatewayOrder;

import java.util.List;

/**
 * 订单流水接口
 * 向 Order 模块发送 HTTP 请求获取数据
 * @author ALI
 * at 2017/12/08 9:11
 */
public interface OrderAdminService {

	List<MchtGatewayOrder> list(MchtGatewayOrder order);

	MchtGatewayOrder queryByKey(String id);

	/**
	 * 交易记录总数
	 * @param order
	 * @return
	 */
	int ordeCount(MchtGatewayOrder order);

	/**
	 * 交易金额总数
	 * @param order
	 * @return
	 */
	long amount(MchtGatewayOrder order);
}
