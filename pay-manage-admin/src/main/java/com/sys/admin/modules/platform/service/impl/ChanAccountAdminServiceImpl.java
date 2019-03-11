package com.sys.admin.modules.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.admin.modules.platform.service.ChanAccountAdminService;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.common.db.JedisConnPool;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.CaChanAccountDetail;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChanAccountAdminServiceImpl implements ChanAccountAdminService {
	private static final Logger logger = LoggerFactory.getLogger(ChanAccountAdminServiceImpl.class);
	private static final String CONFIG_URL = ConfigUtil.getValue("order.url");


	@Override
	public List<CaChanAccountDetail> list(CaChanAccountDetail caChanAccountDetail) {

		String url = CONFIG_URL + "chanAccountList";
		Map<String, String> params = new HashMap<>();
		params.put("caChanAccountDetail", JSON.toJSONString(caChanAccountDetail));

		String result;

		try {
			result = HttpUtil.postConnManager(url, params);
		} catch (Exception e) {
			logger.error("查询上游账务模块出错：", e);
			return null;
		}

		List<CaChanAccountDetail> list = JSON.parseArray(result, CaChanAccountDetail.class);

		if (!CollectionUtils.isEmpty(list)) {
			return list;
		}
		return null;
	}

	@Override
	public int count(CaChanAccountDetail caChanAccountDetail) {

		String url = CONFIG_URL + "chanAccountCount";
		Map<String, String> params = new HashMap<>();
		params.put("caChanAccountDetail", JSON.toJSONString(caChanAccountDetail));

		String result;
		int count = 0;

		try {
			result = HttpUtil.postConnManager(url, params);
			count = Integer.parseInt(result);
		} catch (Exception e) {
			logger.error("查询上游账务模块出错：", e);
			return 0;
		}
		return count;
	}



}
