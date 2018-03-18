package com.sys.gateway.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件读取
 * @author <a href="mailto:guobin@zhrt.com">Ryan</a>
 * @version $Revision: 1.1 $ $Date: 2017/10/10 08:19:40
 */
public class ConfigUtil {

	private static Properties prop;

	public ConfigUtil() {
		this("properties/common.properties");
	}

	public ConfigUtil(String propFile) {
		// TODO Auto-generated constructor stub

		prop = new Properties();
		try {
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(propFile);
			prop.load(ins);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public ConfigUtil(String propFile, String setplace) {
		// TODO Auto-generated constructor stub
		String pathFile = this.getClass().getClassLoader().getResource(propFile).getPath();
		int ps = pathFile.indexOf("WEB-INF");
		propFile = pathFile.substring(0, ps) + propFile;
		prop = new Properties();
		try {
			InputStream ins = new FileInputStream(propFile);// 
			prop.load(ins);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 * 
	 * @param value
	 * @return
	 */
	public synchronized static String getValue(String value) {
		if (prop==null || prop.size() == 0){
			new ConfigUtil();
		}
		return prop.getProperty(value);
	}

	/**
	 * 
	 * 
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized static void setValue(String key, String value) {
		prop.setProperty(key, value);

	}

	public boolean UserConfigStore(String propFile) {
		boolean succ = true;
		try {
			String pathFile = this.getClass().getClassLoader().getResource(propFile).getPath();
			int ps = pathFile.indexOf("WEB-INF");
			propFile = pathFile.substring(0, ps - 1) + propFile;
			java.io.OutputStream out = new FileOutputStream(propFile);
			// synchronized (prop) {
			prop.store(out, "");
			// }
		} catch (Exception e) {
			succ = false;
		}
		return succ;
	}

}
