package com.demo.util;

import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class PostUtil {
    
    public static String post(String url, String postContent) {
		String result = "";
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 20000);
			HttpConnectionParams.setSoTimeout(params, 20000);
			HttpPost httppost = new HttpPost(url);
			StringEntity reqEntity = new StringEntity(postContent, "UTF-8");
			// 设置类型
			reqEntity.setContentType("application/x-www-form-urlencoded");

			// 设置请求的数据
			httppost.setEntity(reqEntity);
			// 执行
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity);
			}
		} catch (Exception e) {

		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return result;
	}

	public static String post(String url, String postContent,String charset) {
		String result = "";
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 20000);
			HttpConnectionParams.setSoTimeout(params, 20000);
			HttpPost httppost = new HttpPost(url);
			StringEntity reqEntity = new StringEntity(postContent, charset);
			// 设置类型
			reqEntity.setContentType("application/x-www-form-urlencoded");

			// 设置请求的数据
			httppost.setEntity(reqEntity);
			// 执行
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity,"UTF-8");
			}
		} catch (Exception e) {

		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
}
