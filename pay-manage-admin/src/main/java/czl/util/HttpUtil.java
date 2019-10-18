package czl.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * HTTP Client 工具类
 */
public class HttpUtil {
	
    public static final int SOCKET_TIMEOUT = 180000;

    public static final int CONNECT_TIMEOUT = 60*1000;

    public static final int CONNECTION_REQUEST_TIMEOUT = 60000;

    /**
     * 默认的HTTP响应实体编码 = "UTF-8"
     */
    private static final String DEFAULT_CHARSET = "UTF-8";


    private static HttpClient httpClient = null;


    private static PoolingHttpClientConnectionManager connManager =null;

    public static  boolean isOpenProxy =false;
	//代理httpclent https
	public static  String  proxyHttps=null;
	//代理httpclent http
	public static  String  proxyHttpIp=null;

	public static  int  proxyHttpPort=0;
    //代理httpurconnect https
	public static  String  proxyHttpsConnectIp=null;

	public static  Integer  proxyHttpsConnectPort =0;

	//代理httpurlconnect http
	public static  String  proxyHost =null;



    private static  InputStream inputStream=null;

    private static  Logger logger = LoggerFactory.getLogger(HttpUtil.class);



    static {
    	try{
			SSLContext sslContext =createIgnoreVerifySSL();
			//设置协议http和https对应的处理socket链接工厂的对象
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
						@Override
						public void verify(String host, SSLSocket ssl) throws IOException {

						}

						@Override
						public void verify(String host, X509Certificate cert) throws SSLException {

						}

						@Override
						public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

						}

						@Override
						public boolean verify(String s, SSLSession sslSession) {
							return true;
						}
					})).build();
			connManager =  new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			//最大连接数
			connManager.setMaxTotal(500);
			//默认的每个路由的最大连接数
			connManager.setDefaultMaxPerRoute(300);

			//加载配置文件
			inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/proxy.properties");
			Properties properties = new Properties();
			properties.load(inputStream);
			isOpenProxy =Boolean.valueOf(properties.getProperty("is_open_proxy"));

			proxyHttpIp =properties.getProperty("proxy_http_ip");
			proxyHttpPort =Integer.parseInt(properties.getProperty("proxy_http_port"));

			proxyHttps =properties.getProperty("proxy_https");

			proxyHttpsConnectIp =properties.getProperty("proxy_https_connet_ip");
			proxyHttpsConnectPort =Integer.parseInt(properties.getProperty("proxy_https_connet_port"));

			proxyHost =properties.getProperty("proxy_http_connet_host");



		}catch (Exception e){
    		isOpenProxy =false;
    		logger.error("httpUtil类初始化参数异常",e);
		}finally {
			try {
				if(inputStream!=null)
				inputStream.close();
			} catch (IOException e) {
				logger.error("httpUtit关闭流异常",e);
			}
		}


	}


    /**
     * HTTP Post---使用 了连接池。
     * 星罗通道跑如下异常：java.lang.IllegalStateException: Invalid use of BasicClientConnManager: connection still allocated.
     * 所以加上了连接池。
     * 以后如果用的连接池的话，可以使用此方法
     *
     * @param url    请求url
     * @param params 请求参数
     * @return 响应内容实体
     * @throws Exception 
     */
    public static Map<String, String> postConnManager(String url, Map<String, String> params) throws Exception {
    	return postConnManager(url, params, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    /**
     * HTTP Post---使用 了连接池。
     * 星罗通道跑如下异常：java.lang.IllegalStateException: Invalid use of BasicClientConnManager: connection still allocated.
     * 所以加上了连接池。
     *
     * @param url             请求URL
     * @param params          请求参数集合
     * @param paramEncoding   请求参数编码
     * @param responseCharset 响应内容字符集
     * @return 响应内容实体
     * @throws Exception 
     */
    public static Map<String, String> postConnManager(String url, Map<String, String> params, String paramEncoding, String responseCharset) throws Exception {
    	HttpPost post = null;
    	HttpResponse response=null;
    	HttpHost httpHost =null;
		RequestConfig requestConfig=null;
    	try {
    		//是否走代理
			System.out.println("代理开启参数为"+isOpenProxy);
			if(isOpenProxy){
				if(url.contains("https")){
					post =new HttpPost(proxyHttps);
					post.setHeader("real-url",url);
					requestConfig= RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(
							CONNECT_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
							.build();
				}else{
					post =new HttpPost(url);
					post.setHeader("real-url",url);
					httpHost = new HttpHost(proxyHttpIp,proxyHttpPort, HttpHost.DEFAULT_SCHEME_NAME);
					requestConfig= RequestConfig.custom().setProxy(httpHost).setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(
							CONNECT_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
							.build();
				}
			}else{
				post =new HttpPost(url);
				requestConfig= RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(
						CONNECT_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
						.build();
			}
			//获取请求头
			post.setHeader("X-QF-APPCODE",params.get("X-QF-APPCODE"));
			post.setHeader("X-QF-SIGN",params.get("X-QF-SIGN"));
			params.remove("X-QF-APPCODE");
			params.remove("X-QF-SIGN");

    		if (params != null) {
    			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
    			for (String key : params.keySet()) {
    				paramList.add(new BasicNameValuePair(key, params.get(key)));
    			}
    			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramList, paramEncoding);
    			post.setEntity(formEntity);
    		}

    		//设置请求和传输超时时间
    		requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(
    				CONNECT_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).build();
    		httpClient = HttpClients.custom()
            .setConnectionManager(connManager)             //连接管理器
            .setDefaultRequestConfig(requestConfig) //默认请求配置
            .build();
    		//执行请求
    		response = httpClient.execute(post);
    		//获取响应头
			Map<String,String> responseMap =null;
			System.out.println(response.getStatusLine().getStatusCode()+"");
			String respString =consumeResponseEntity(response, responseCharset);
//			respString = Native2AsciiUtils.ascii2Native(respString);
			System.out.println("响应元数据为："+respString);
			if(StringUtils.isBlank(respString)){
				return responseMap;
			}else{

				responseMap = JSON.parseObject(respString,new TypeReference<Map<String,String>>(){});
				responseMap.put("X-QF-SIGN",response.getHeaders("X-QF-SIGN")[0].getValue());
				responseMap.put("oriString",respString);
				return responseMap;
			}
    	} catch (Exception e) {
    		throw new Exception(e);
    	} finally {
    		if (post != null) {
    			post.releaseConnection();
    		}
    		
    		if (response != null) {
    			EntityUtils.consumeQuietly(response.getEntity());
    		}
    	}
    }

    /**
     * 安全的消耗（获取）响应内容实体
     * <p/>
     * 使用 {@link EntityUtils} 将响应内容实体转换为字符串，同时关闭输入流
     * <p/>
     * <b>注意：</b> 响应内容太长不适宜使用 EntityUtils
     *
     * @param response        HttpResponse
     * @param responseCharset 响应内容字符集
     * @return 响应内容实体
     * @throws IOException IOException
     */
    private static String consumeResponseEntity(HttpResponse response, String responseCharset) throws IOException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity, responseCharset);
            return responseBody;
        } else {
            return null;
        }
    }

	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

			// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
			X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}

}
