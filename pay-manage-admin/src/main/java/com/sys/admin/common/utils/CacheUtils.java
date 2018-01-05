package com.sys.admin.common.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.shiro.cache.ehcache.EhCacheManager;

/**
 * Cache工具类
 */
public class CacheUtils {
	
	private static CacheManager cacheManager = ((EhCacheManager)SpringContextHolder.getBean("cacheManager")).getCacheManager();

	static {
		if (cacheManager == null) {
			EhCacheManager ehCacheManager = SpringContextHolder.getBean("cacheManager");
			ehCacheManager.init();
			cacheManager = ehCacheManager.getCacheManager();
		}
	}

	private static final String SYS_CACHE = "sysCache";

	private static final String CMS_CACHE = "cmsCache";

	private static final String CONFIG_CACHE = "configCache";

	private static final String TICKET_CACHE = "ticketCache";

	private static final String STATION_CACHE = "stationCache";

	public static Object getSysCache(String key) {
		return get(SYS_CACHE, key);
	}

	public static void putSysCache(String key, Object value) {
		put(SYS_CACHE, key, value);
	}

	public static void removeSysCache(String key) {
		remove(SYS_CACHE, key);
	}

	public static Object getCmsCache(String key) {
		return get(CMS_CACHE, key);
	}

	public static void putCmsCache(String key, Object value) {
		put(CMS_CACHE, key, value);
	}

	public static void removeCmsCache(String key) {
		remove(CMS_CACHE, key);
	}

	public static Object getConfigCache(String key) {
		return get(CONFIG_CACHE, key);
	}

	public static void putConfigCache(String key, Object value) {
		put(CONFIG_CACHE, key, value);
	}

	public static void removeConfigCache(String key) {
		remove(CONFIG_CACHE, key);
	}

	public static Object getTicketCache(String key) {
		return get(TICKET_CACHE, key);
	}

	public static void putTicketCache(String key, Object value) {
		put(TICKET_CACHE, key, value);
	}

	public static void removeTicketCache(String key) {
		remove(TICKET_CACHE, key);
	}

	public static Object getStationCache(String key) {
		return get(STATION_CACHE, key);
	}

	public static void putStationCache(String key, Object value) {
		put(STATION_CACHE, key, value);
	}

	public static void removeStationCache(String key) {
		remove(STATION_CACHE, key);
	}
	
	public static Object get(String cacheName, String key) {
		Element element = getCache(cacheName).get(key);
		return element==null?null:element.getObjectValue();
	}

	public static void put(String cacheName, String key, Object value) {
		Element element = new Element(key, value);
		getCache(cacheName).put(element);
	}

	public static void remove(String cacheName, String key) {
		getCache(cacheName).remove(key);
	}
	
	/**
	 * 获得一个Cache，没有则创建一个。
	 */
	private synchronized static Cache getCache(String cacheName){
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null){
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
			cache.getCacheConfiguration().setEternal(false);
		}
		return cache;
	}

	
}
