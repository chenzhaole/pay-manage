package com.sys.admin.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sys.admin.common.utils.CacheUtils;
import com.sys.admin.common.utils.PropertiesLoader;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

/**
 * 全局配置类
 */
public class Global {
	private static Logger logger = LoggerFactory.getLogger(Global.class);

	private static String[] getProperties(){
		String[] files = new String[0];
		try {
			URL resource = Global.class.getClassLoader().getResource("properties/");
			if (resource == null) {
				return files;
			}

			File path = new File(resource.toURI().getPath());
			if (path.isDirectory()) {
				String[] fileNames = path.list();
				files = new String[fileNames.length];
				for (int i = 0, len = fileNames.length; i < len; i++) {
					files[i] = "properties/" + fileNames[i];
				}
			}
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}

		return files;
	}
	
	/**
	 * 获取配置
	 */
	public static String getConfig(String key) {
		String res = (String)CacheUtils.getConfigCache(key);
		if (res == null) {
			PropertiesLoader propertiesLoader = new PropertiesLoader(getProperties());
			Enumeration names = propertiesLoader.getNames();
			while (names.hasMoreElements()) {
				String name = (String)names.nextElement();
				String value = propertiesLoader.getProperty(name);

				CacheUtils.putConfigCache(name, value);

				if (name.equals(key)) {
					res = value;
				}
			}

		}
		return res;
	}

}
