package com.sys.admin.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class PostUtil {
	/** 日志对象    */
	private static final String CHLKEY = "03d71d20-db96-4b11-a511-03498bfd91f0";
	private static final String APPKEY = "497c3ad7-96f2-4bb2-8667-f3165394fd26";
	private static final String SECRETKEY = "BUS84100";
	private static final String VERSION = "1";
	
	/**
	 * 处理POST请求
	 */
	public static String postMsg(String httpUrl, String content) throws Exception {
		String rsponseStr = null;
		//logger.info(">>>>  PostUtil.postMsg url="+httpUrl+" content="+content);
		HttpURLConnection httpConn = null;
		try {
			URL url = new URL(httpUrl);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setUseCaches(false);
			httpConn.setRequestMethod("POST");

			byte[] requestStringBytes = content.getBytes("UTF-8");
			httpConn.setRequestProperty("Content-Length", ""+ requestStringBytes.length);
//			httpConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			httpConn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
			httpConn.setRequestProperty("Content-Version", "1.0");
			httpConn.setRequestProperty("Charset", "UTF-8");
			httpConn.setRequestProperty("Connection", "Close");
			httpConn.setConnectTimeout(5000);

			OutputStream outputStream = httpConn.getOutputStream();
			outputStream.write(requestStringBytes);
			outputStream.close();
			int responseCode = httpConn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == responseCode) {

				StringBuffer sb = new StringBuffer();
				String readLine;
				BufferedReader responseReader;
				responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					sb.append(readLine).append("\n");
				}
				responseReader.close();
				rsponseStr = sb.toString();
			} 

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (httpConn != null) {
				httpConn.disconnect();
			}
		}

		return rsponseStr;
	}
	
	
	public static String postMsgByHttpclient(String url, String data) throws Exception {
		String res = null;
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		StringEntity myEntity = new StringEntity(data,  ContentType.create("text/plain", "UTF-8"));
		httpPost.setEntity(myEntity);
		CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost);
		try {
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
		        long len = entity.getContentLength();
		        if (len != -1 && len < 2048) {
		        	res = EntityUtils.toString(entity);
		        } else {
		            // Stream content out
		        }
		    }
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally{
			response.close();
		}
		return res;
	}
	
	/**
	 * 处理POST请求
	 */
	public static String postMsg4API(String httpUrl, Map<String,String> headMap, String contentStr) throws Exception {
		String rsponseStr = null;
		
		HttpURLConnection httpConn = null;
		try {
			URL url = new URL(httpUrl);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setUseCaches(false);
			httpConn.setRequestMethod("POST");
			httpConn.setConnectTimeout(5000);
			byte[] requestStringBytes = contentStr.getBytes("UTF-8");
			for(String key : headMap.keySet()){
				String  value = headMap.get(key);
				httpConn.setRequestProperty(key,value);
			}

			OutputStream outputStream = httpConn.getOutputStream();
			outputStream.write(requestStringBytes);
			outputStream.close();
			int responseCode = httpConn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == responseCode) {

				StringBuffer sb = new StringBuffer();
				String readLine;
				BufferedReader responseReader;
				responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					sb.append(readLine).append("\n");
				}
				responseReader.close();
				rsponseStr = sb.toString();
			} 

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (httpConn != null) {
				httpConn.disconnect();
			}
		}
		return rsponseStr;
	}
	
	public static String postForm(String url, Map<String, String > parameters) throws Exception{
		String result = "";// 返回的结果
		BufferedReader in = null;// 读取响应输入流
		PrintWriter out = null;
		StringBuffer sb = new StringBuffer();// 处理请求参数
		String params = "";// 编码之后的参数
		try {
			// 编码请求参数
			if (parameters.size() == 1) {
				for (String name : parameters.keySet()) {
					sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name).toString(), "UTF-8"));
				}
				params = sb.toString();
			} else {
				for (String name : parameters.keySet()) {
					sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name).toString(), "UTF-8")).append("&");
				}
				String temp_params = sb.toString();
				params = temp_params.substring(0, temp_params.length() - 1);
			}
			// 创建URL对象
			URL connURL = new URL(url);
			// 打开URL连接
			HttpURLConnection httpConn = (HttpURLConnection) connURL.openConnection();
			// 设置通用属性
			httpConn.setRequestProperty("Accept", "*/*");
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
			httpConn.setConnectTimeout(100000);
			httpConn.setReadTimeout(100000);
			// 设置POST方式
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			// 获取HttpURLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.write(params);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应，设置编码方式
			in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			String line;
			// 读取返回的内容
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new Exception(ex);
			}
		}
		return result;
	}
	
	/**
	 * 将表单数据转换成Map对象
	 * 
	 * @param formData
	 * @return
	 */
	public static Map convertFormData2Map(String formData){
		String[] strs = URLDecoder.decode(formData).split("&");
		Map<String,String> paramMap = new HashMap<String,String>();
		for(String s : strs){
			String[] vls = s.split("=");
			if(vls.length >=2 ){
				paramMap.put(vls[0], URLDecoder.decode(vls[1]));
			}else{
				paramMap.put(vls[0], "");
			}
		}
		return paramMap;
	}
	
	public static void main(String[] args) throws Exception {
//		String url = "http://101.200.236.127:8680/service-sms/mt/sendContent";
		String url = "http://101.200.236.127:8680/service-sms/mt/sendVerifyCode";
//		String param = "mobile=18640217892&content=333>>成功<<-333Fxx&channel=UBusServer&seq=65329";
		String param = "mobile=18640217892&channel=UBusServer&seq=65329&type=3";
		String rtn = com.sys.common.util.PostUtil.postMsg(url, param);
		System.out.println(rtn);
	}

	
}
