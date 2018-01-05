package com.sys.admin.modules.trade.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.admin.modules.trade.service.OrderAdminService;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单接口实现
 * 向 Order 模块发送 HTTP 请求获取数据
 *
 * @author ALI
 * at 2017/12/08 9:11
 */
@Service
public class OrderAdminServiceImpl implements OrderAdminService {
	private static final Logger log = LoggerFactory.getLogger(OrderAdminServiceImpl.class);

	private static final String CONFIG_URL = "http://127.0.0.1:28081/";

	@Override
	public List<MchtGatewayOrder> list(MchtGatewayOrder order) {

		String url = CONFIG_URL + "orderQuery";
		Map<String, String> params = new HashMap<>();
		params.put("order", JSON.toJSONString(order));

		String result;

		try {
			result = HttpUtil.postConnManager(url, params);
		} catch (Exception e) {
			log.error("查询 Order 模块出错：", e);
			return null;
		}

		List<MchtGatewayOrder> mchtGatewayOrders = JSON.parseArray(result, MchtGatewayOrder.class);

		if (!CollectionUtils.isEmpty(mchtGatewayOrders)) {
			return mchtGatewayOrders;
		}

		return null;
	}

	@Override
	public MchtGatewayOrder queryByKey(String id) {
		return null;
	}

	@Override
	public int ordeCount(MchtGatewayOrder order) {

		String url = CONFIG_URL + "ordeCount";
		Map<String, String> params = new HashMap<>();
		params.put("order", JSON.toJSONString(order));

		String result;
		int resultInt = 0;

		try {
			result = HttpUtil.postConnManager(url, params);
			if (StringUtils.isNotBlank(result)) {
				resultInt = Integer.parseInt(result);
			}

		} catch (Exception e) {
			log.error("查询 Order 模块出错：", e);
			return resultInt;
		}
		return resultInt;
	}

	@Override
	public long amount(MchtGatewayOrder order) {

		String url = CONFIG_URL + "orderAmount";
		Map<String, String> params = new HashMap<>();
		params.put("order", JSON.toJSONString(order));

		String result;
		long resultInt = 0;

		try {
			result = HttpUtil.postConnManager(url, params);
			if (StringUtils.isNotBlank(result)) {
				resultInt = Long.parseLong(result);
			}
		} catch (Exception e) {
			log.error("查询 Order 模块出错：", e);
			return resultInt;
		}
		return resultInt;
	}

}
