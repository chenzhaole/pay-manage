package com.sys.admin.modules.sys.utils;

import com.sys.admin.common.config.GlobalConfig;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisConnPool {
	static Logger logger = LoggerFactory.getLogger(JedisConnPool.class);
	private static JedisPool pool = null;

	public static JedisPool getPool() {
		try {
			if (pool == null) {
				JedisPoolConfig config = new JedisPoolConfig();
				
				String REDIS_IP = GlobalConfig.getConfig("redis.ip");
				int REDIS_PORT = Integer.parseInt(GlobalConfig.getConfig("redis.port"));
				String REDIS_PWD = GlobalConfig.getConfig("redis.pwd");
				
				String maxTotalStr = GlobalConfig.getConfig("redis.maxTotal");
				if(StringUtils.isNotBlank(maxTotalStr)){
					int maxTotal = Integer.parseInt(maxTotalStr);
					config.setMaxTotal(maxTotal);
				}

				String maxIdleStr = GlobalConfig.getConfig("redis.maxIdle");
				if(StringUtils.isNotBlank(maxIdleStr)){
					int maxIdle =  Integer.parseInt(maxIdleStr);
					config.setMaxIdle(maxIdle);
				}

				String minIdleStr = GlobalConfig.getConfig("redis.minIdle");
				if(StringUtils.isNotBlank(minIdleStr)){
					int minIdle =  Integer.parseInt(minIdleStr);
					config.setMinIdle(minIdle);
				}
				
				String maxWaitMillisStr = GlobalConfig.getConfig("redis.maxWaitMillis");
				if(StringUtils.isNotBlank(maxWaitMillisStr)){
					long maxWaitMillis = Long.parseLong(maxWaitMillisStr);
					config.setMaxWaitMillis(maxWaitMillis);
				}
				
				//在return给pool时，是否提前进行validate操作；
				String testOnReturnStr = GlobalConfig.getConfig("redis.testOnReturn");
				if("true".equalsIgnoreCase(testOnReturnStr)){
					config.setTestOnReturn(true);
				}

				String timeoutStr = GlobalConfig.getConfig("redis.timeout");
				int timeout = 1000;
				if(StringUtils.isNotBlank(timeoutStr)){
					timeout =  Integer.parseInt(timeoutStr);
				}else{
					timeout=1000;
				}
				
				//如果密码为空，则更换实例化方式
				if(StringUtils.isNotBlank(REDIS_PWD)){
					pool = new JedisPool(config, REDIS_IP, REDIS_PORT, timeout, REDIS_PWD);
				}else{
					pool = new JedisPool(config, REDIS_IP, REDIS_PORT, timeout);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());

		}
		return pool;
	}
	

	public static JedisPool getPool(String msg) {
//		logger.info("<JedisPool-getPool> "+msg);
		return getPool();
	}
	
	
	public static void returnBrokenResource(JedisPool pool, Jedis jedis){
		if (pool != null && jedis != null) 
        {
			pool.returnBrokenResource(jedis);
            jedis = null;
        }
	}
	

	public static void returnBrokenResource(JedisPool pool, Jedis jedis, String msg){
//		logger.info("<JedisPool-returnBrokenResource> "+msg);
		returnBrokenResource(pool, jedis);
	}
	
	public static void returnResource(JedisPool pool, Jedis jedis){
		if(pool != null && jedis != null){
			pool.returnResource(jedis);
        }
	}
	
	public static void returnResource(JedisPool pool, Jedis jedis, String msg){
//		logger.info("<JedisPool-returnResource> "+msg);
		returnResource(pool, jedis);
	}
	
}