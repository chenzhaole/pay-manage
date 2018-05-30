package com.sys.admin.modules.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.common.db.JedisConnPool;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @author ALI
 * at 2018/5/30 9:51
 */
@Service
public class AccountAdminServiceImpl implements AccountAdminService {
	private static final Logger logger = LoggerFactory.getLogger(AccountAdminServiceImpl.class);

	@Autowired
	private MerchantService merchantService;

	/**
	 * 插入商户账户信息至Redis队列
	 */
	@Override
	public int insert2redisAccTask(CacheMchtAccount cacheMchtAccount) {
		JedisPool pool = null;
		Jedis jedis = null;
		long rs = 0;
		try {
			pool = JedisConnPool.getPool("缓存插入cacheMchtAccount信息");
			jedis = pool.getResource();
			int accType = cacheMchtAccount.getType();
			switch (accType) {
				case 3: //调账
					rs = jedis.lpush(IdUtil.REDIS_ACCT_MCHT_ACCOUNT_ADJUST_TASK_LIST, JSON.toJSONString(cacheMchtAccount));
					break;
				case 2: //代付
					rs = jedis.lpush(IdUtil.REDIS_ACCT_MCHT_ACCOUNT_DF_TASK_LIST, JSON.toJSONString(cacheMchtAccount));
					break;
				default: //支付
					rs = jedis.lpush(IdUtil.REDIS_ACCT_MCHT_ACCOUNT_TASK_LIST, JSON.toJSONString(cacheMchtAccount));
					break;
			}
			logger.info("插入了一个新的任务： rsPay = " + rs);
		} catch (JedisConnectionException je) {
			logger.error("Redis Jedis连接异常：" + je.getMessage());
			je.printStackTrace();
			rs = -1;
		} catch (Exception e) {
			logger.error("<insertData-error>error[" + e.getMessage() + "]</insertData-error>");
			e.printStackTrace();
			rs = -1;
		} finally {
			JedisConnPool.returnResource(pool, jedis, "");
		}
		return (int) rs;
	}

	/**
	 * 查询redis中的CacheMcht
	 *
	 * @param mchtId
	 * @return
	 */
	@Override
	public CacheMcht queryCacheMcht(String mchtId) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = JedisConnPool.getPool();
			jedis = pool.getResource();
			String key = IdUtil.REDIS_TRADE_PAY_MCHT_INFO + mchtId;
			if (!jedis.exists(key)) {
				MchtInfo mchtInfo = merchantService.queryByKey(mchtId);
				CacheMcht cacheMcht = new CacheMcht();
				cacheMcht.setMchtId(mchtId);
				cacheMcht.setMchtKey(mchtInfo.getMchtKey());
				cacheMcht.setParentMchtId(mchtInfo.getParentId());
				cacheMcht.setTotalAmoutPayPerDay(0);
				cacheMcht.setPayDataUpdateUtc(0);
				cacheMcht.setTotalAmoutDFPerDay(0);
				cacheMcht.setTotalTimesBatchDFPerDay(0);
				cacheMcht.setTotalTimesSingleDFPerDay(0);
				cacheMcht.setDfDataUpdateUtc(0);

				int rs = insert2redisCacheMcht(cacheMcht);
				if (rs > 0) {
					return cacheMcht;
				} else {
					return null;
				}
			} else {
				String value = jedis.get(key);
				CacheMcht cacheMcht = JSON.parseObject(value, CacheMcht.class);
				return cacheMcht;
			}

		} catch (JedisConnectionException je) {
			je.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JedisConnPool.returnResource(pool, jedis, "");
		}
		return null;
	}

	/**
	 * 插入缓存商户对象
	 */
	protected int insert2redisCacheMcht(CacheMcht cacheMcht) {
		JedisPool pool = null;
		Jedis jedis = null;
		int rtn = 0;
		try {
			pool = JedisConnPool.getPool();
			jedis = pool.getResource();
			String key = IdUtil.REDIS_TRADE_PAY_MCHT_INFO + cacheMcht.getMchtId();
			String rs = jedis.set(key, JSON.toJSONString(cacheMcht));
			if (StringUtils.isNotBlank(rs)) {
				rtn = 1;
			}

		} catch (JedisConnectionException je) {
			je.printStackTrace();
			rtn = -1;
		} catch (Exception e) {
			e.printStackTrace();
			rtn = -1;
		} finally {
			JedisConnPool.returnResource(pool, jedis, "");
		}
		return rtn;
	}
}
