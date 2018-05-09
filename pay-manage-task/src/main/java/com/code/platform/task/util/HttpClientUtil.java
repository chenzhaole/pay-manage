package com.code.platform.task.util;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.*;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private HttpClientUtil() {
    }

    private static CloseableHttpClient httpClient = null;

    private static HttpClient httpsClient = null;

    private static PoolingHttpClientConnectionManager connManager = null;

    static {
        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null, new TrustManager[] {new X509TrustManager() {

                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                public void checkClientTrusted(
                                    X509Certificate[] certs, String authType
                                ) {
                                }

                                public void checkServerTrusted(
                                    X509Certificate[] certs, String authType
                                ) {
                                }
                            }}, null
            );
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                "http", PlainConnectionSocketFactory.INSTANCE).register("https",
                                                                        new SSLConnectionSocketFactory(
                                                                            sslContext)
            ).build();

            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

                public boolean retryRequest(
                    IOException exception, int executionCount, HttpContext context
                ) {
                    if (executionCount >= 5) {
                        logger.warn("=====httpClient has retry 5 times====");
                        // 如果已经重试了5次，就放弃
                        return false;
                    }
                    if (exception instanceof InterruptedIOException) {
                        // 超时
                        logger.warn("=====httpClient InterruptedIOException====");
                        return false;
                    }
                    if (exception instanceof UnknownHostException) {
                        // 目标服务器不可达
                        logger.warn("=====httpClient UnknownHostException====");
                        return false;
                    }
                    if (exception instanceof SSLException) {
                        // ssl握手异常
                        logger.warn("=====httpClient SSLException====");
                        return false;
                    }
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    HttpRequest request = clientContext.getRequest();
                    boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                    if (idempotent) {
                        // 如果请求是幂等的，就再次尝试
                        return true;
                    }
                    return false;
                }

            };

            ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                    // Honor 'keep-alive' header
                    HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                    while (it.hasNext()) {
                        HeaderElement he = it.nextElement();
                        String param = he.getName();
                        String value = he.getValue();
                        if (value != null && param.equalsIgnoreCase("timeout")) {
                            try {
                                return Long.parseLong(value) * 1000;
                            } catch (NumberFormatException ignore) {
                            }
                        }
                    }
                    //                    HttpHost target = (HttpHost) context.getAttribute(
                    //                        HttpClientContext.HTTP_TARGET_HOST);
                    //                    if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
                    //                        // Keep alive for 5 seconds only
                    //                        return 5 * 1000;
                    //                    } else {
                    // otherwise keep alive for 30 seconds
                    return TimeUnit.HOURS.toSeconds(4);
                    //                    }
                }

            };

            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(
                200).setMaxLineLength(2000).build();
            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(
                CodingErrorAction.IGNORE).setUnmappableInputAction(
                CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).setMessageConstraints(
                messageConstraints).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(200);
            httpClient = HttpClients.custom().setConnectionManager(
                connManager).setRetryHandler(myRetryHandler).setKeepAliveStrategy(
                myStrategy).build();
        } catch (KeyManagementException e) {
            logger.error("KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException", e);
        }
    }

    /**
     * 默认的HTTP响应实体编码 = "UTF-8"
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    // <<Get>>

    /**
     * HTTP Get
     * <p/>
     * 响应内容实体采用<code>UTF-8</code>字符集
     *
     * @param url 请求url
     * @return 响应内容实体
     */
    public static String get(String url) {
        return get(url, DEFAULT_CHARSET);
    }

    /**
     * 获取请求返回byte数组
     *
     * @param url 请求url
     * @return 响应内容实体
     */
    public static byte[] get2Bytes(String url) {
        if (logger.isDebugEnabled())
            logger.debug("Get [" + url + "] ...");
        HttpGet getMethod = null;
        try {
            getMethod = new HttpGet(url);
            HttpResponse response = httpClient.execute(getMethod);
            return consumeResponseEntity(response);
        } catch (Exception e) {
            logger.error("httpclient get error:", e);
            throw new SystemException(e);
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
    }

    /**
     * HTTP Get
     *
     * @param url             请求url
     * @param responseCharset 响应内容字符集
     * @return 响应内容实体
     */
    public static String get(String url, String responseCharset) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get [" + url + "] ...");
        }
        HttpGet getMethod = null;
        try {
            getMethod = new HttpGet(url);
            HttpResponse response = httpClient.execute(getMethod);
            return consumeResponseEntity(response, responseCharset);
        } catch (Exception e) {
            logger.error("httpclient get error: url " + url, e);
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
        return null;
    }

    // /////////////////////////////////////////////////////////////////////////
    // <<Post>>

    /**
     * HTTP Post
     *
     * @param url    请求url
     * @param params 请求参数
     * @return 响应内容实体
     */
    public static String post(String url, Map<String, String> params) {
        return post(url, params, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    /**
     * Post 获取返回byte数组
     *
     * @param url            请求url
     * @param params         请求参数
     * @param requestCharset 请求字符集
     * @return 响应内容实体
     */
    public static byte[] post2Bytes(
        String url, Map<String, String> params, String requestCharset
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Post [" + url + "] ...");

        if (requestCharset == null) {
            requestCharset = DEFAULT_CHARSET;
        }
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (String key : params.keySet()) {
                    paramList.add(new BasicNameValuePair(key, params.get(key)));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramList,
                                                                           requestCharset);
                post.setEntity(formEntity);
            }

            HttpResponse response = httpClient.execute(post);
            return consumeResponseEntity(response);
        } catch (Exception e) {
            logger.error("httpclient post error:", e);
            throw new SystemException(e);
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
    }

    /**
     * HTTP Post XML（使用默认字符集）
     *
     * @param url 请求的URL
     * @param xml XML格式请求内容
     * @return 响应内容实体
     */
    public static String postXml(String url, String xml) {
        return postXml(url, xml, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    /**
     * HTTP Post XML
     *
     * @param url             请求的URL
     * @param xml             XML格式请求内容
     * @param requestCharset  请求内容字符集
     * @param responseCharset 响应内容字符集
     * @return 响应内容实体
     */
    public static String postXml(
        String url, String xml, String requestCharset, String responseCharset
    ) {
        return post(url, xml, "text/xml; charset=" + requestCharset, "text/xml",
                    requestCharset, responseCharset);
    }

    /**
     * HTTP Post JSON（使用默认字符集）
     *
     * @param url  请求的URL
     * @param json JSON格式请求内容
     * @return 响应内容实体
     */
    public static String postJson(String url, String json) {
        return postJson(url, json, DEFAULT_CHARSET, DEFAULT_CHARSET);
    }

    /**
     * HTTP Post JSON
     *
     * @param url             请求的URL
     * @param json            JSON格式请求内容
     * @param requestCharset  请求内容字符集
     * @param responseCharset 响应内容字符集
     * @return 响应内容实体
     */
    public static String postJson(
        String url, String json, String requestCharset, String responseCharset
    ) {
        return post(url, json, "application/json; charset=" + requestCharset,
                    "application/json", requestCharset, responseCharset);
    }

    /**
     * HTTP Post
     *
     * @param url             请求URL
     * @param params          请求参数
     * @param paramEncoding   请求参数编码
     * @param responseCharset 响应内容字符集
     * @return 响应内容实体
     */
    public static String post(
        String url, Map<String, String> params, String paramEncoding, String responseCharset
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Post [" + url + "] ...");
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (String key : params.keySet()) {
                    paramList.add(new BasicNameValuePair(key, params.get(key)));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramList,
                                                                           paramEncoding);
                post.setEntity(formEntity);
            }

            HttpResponse response = httpClient.execute(post);
            return consumeResponseEntity(response, responseCharset);
        } catch (Exception e) {
            logger.error("httpclient post error:", e);
            throw new SystemException(e);
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
    }

    /**
     * HTTP Post
     *
     * @param url             请求的URL
     * @param content         请求内容
     * @param contentType     请求内容类型，HTTP Header中的<code>Content-type</code>
     * @param mimeType        请求内容MIME类型
     * @param requestCharset  请求内容字符集
     * @param responseCharset 响应内容字符集
     * @return 响应内容实体
     */
    public static String post(
        String url, String content, String contentType, String mimeType, String requestCharset,
        String responseCharset
    ) {
        if (logger.isDebugEnabled())
            logger.debug("Post [" + url + "] ...");
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            post.setHeader("Content-Type", contentType);
            HttpEntity requestEntity = new StringEntity(content, ContentType.create(mimeType,
                                                                                    requestCharset));
            post.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(post);
            return consumeResponseEntity(response, responseCharset);
        } catch (Exception e) {
            logger.error("httpclient post error:", e);
            throw new SystemException(e);
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // <<SSL Get>>

    public static String sslGet(
        String url, Map<String, String> params, String keyType, String keyPath,
        String keyPassword, int sslport, String authString
    ) {
        try {
            Scheme sch = getSslScheme(keyType, keyPath, keyPassword, sslport);
            return sslGet(url, params, sch, authString);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new SystemException(e);
        }
    }

    public static String sslGet(
        String url, Map<String, String> params, Scheme sch, String authString
    ) {
        if (logger.isDebugEnabled())
            logger.debug("SSL Get [" + url + "] ...");
        HttpGet getMethod = null;
        try {
            httpsClient.getConnectionManager().getSchemeRegistry().register(sch);
            getMethod = new HttpGet(url);

            if (authString != null)
                getMethod.setHeader("Authorization", authString);

            if (params != null) {
                HttpParams httpParams = new BasicHttpParams();
                for (String key : params.keySet()) {
                    httpParams.setParameter(key, params.get(key));
                }
                getMethod.setParams(httpParams);
            }

            HttpResponse response = httpsClient.execute(getMethod);
            return consumeResponseEntity(response, DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("httpclient ssl get error:", e);
            throw new SystemException(e);
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // <<SSL Post>>

    /**
     * SSL Post XML（使用默认字符集）
     *
     * @param url         请求的URL
     * @param xml         XML格式请求内容
     * @param keyType     密钥类型
     * @param keyPath     密钥文件路径
     * @param keyPassword 密钥文件密码
     * @param sslPort     SSL端口
     * @param authString  头部认证信息
     * @return 响应内容实体
     */
    public static String sslPostXml(
        String url, String xml, String keyType, String keyPath, String keyPassword,
        int sslPort, String authString
    ) {
        try {
            Scheme sch = getSslScheme(keyType, keyPath, keyPassword, sslPort);
            return sslPost(url, xml, "text/xml; charset=" + DEFAULT_CHARSET, "text/xml",
                           DEFAULT_CHARSET, DEFAULT_CHARSET, sch, authString);
        } catch (Exception e) {
            logger.error("httpclient post xml error:", e);
            throw new SystemException(e);
        }
    }

    /**
     * SSL Post JSON（使用默认字符集）
     *
     * @param url         请求的URL
     * @param json        JSON格式请求内容
     * @param keyType     密钥类型
     * @param keyPath     密钥文件路径
     * @param keyPassword 密钥文件密码
     * @param sslPort     SSL端口
     * @param authString  头部认证信息
     * @return 响应内容实体
     */
    public static String sslPostJson(
        String url, String json, String keyType, String keyPath, String keyPassword,
        int sslPort, String authString
    ) {
        try {
            Scheme sch = getSslScheme(keyType, keyPath, keyPassword, sslPort);
            return sslPost(url, json, "application/json; charset=" + DEFAULT_CHARSET,
                           "application/json", DEFAULT_CHARSET, DEFAULT_CHARSET, sch,
                           authString);
        } catch (Exception e) {
            logger.error("httpclient post json error:", e);
            throw new SystemException(e);
        }
    }

    /**
     * SSL Post
     *
     * @param url             请求的URL
     * @param content         请求内容
     * @param contentType     请求内容类型，HTTP Header中的<code>Content-type</code>
     * @param mimeType        请求内容MIME类型
     * @param requestCharset  请求内容字符集
     * @param responseCharset 响应内容字符集
     * @param sch             Scheme
     * @param authString      头部信息中的<code>Authorization</code>
     * @return 响应内容实体
     */
    public static String sslPost(
        String url, String content, String contentType, String mimeType, String requestCharset,
        String responseCharset, Scheme sch, String authString
    ) {
        if (logger.isDebugEnabled())
            logger.debug("SSL Post [" + url + "] ...");
        HttpPost post = null;
        try {
            httpsClient.getConnectionManager().getSchemeRegistry().register(sch);
            post = new HttpPost(url);

            if (authString != null)
                post.setHeader("Authorization", authString);
            if (contentType != null)
                post.setHeader("Content-Type", contentType);

            HttpEntity requestEntity = new StringEntity(content, ContentType.create(mimeType,
                                                                                    requestCharset));
            post.setEntity(requestEntity);

            HttpResponse response = httpsClient.execute(post);
            return consumeResponseEntity(response, responseCharset);
        } catch (Exception e) {
            logger.error("httpclient post error:", e);
            throw new SystemException(e);
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // <<内部辅助方法>>

    /**
     * 安全的消耗（获取）响应内容实体
     * <p/>
     * 使用 {@link org.apache.http.util.EntityUtils} 将响应内容实体转换为字符串，同时关闭输入流
     * <p/>
     * <b>注意：</b> 响应内容太长不适宜使用 EntityUtils
     *
     * @param response        HttpResponse
     * @param responseCharset 响应内容字符集
     * @return 响应内容实体
     * @throws java.io.IOException IOException
     */
    private static String consumeResponseEntity(
        HttpResponse response, String responseCharset
    ) throws IOException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            if (logger.isDebugEnabled())
                logger.debug("Response status line: " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();
            // long len = responseEntity.getContentLength();
            // if (len != -1) { //&& len < 65536) {
            String responseBody = EntityUtils.toString(responseEntity, responseCharset);
            if (logger.isDebugEnabled())
                logger.debug("Response body: \n" + responseBody);
            return responseBody;
            // }
        } else {
            if (logger.isWarnEnabled())
                logger.warn("Response status line: " + response.getStatusLine());

            return null;
        }
    }

    /**
     * 安全的消耗（获取）响应内容实体
     * <p/>
     * 使用 {@link org.apache.http.util.EntityUtils} 将响应内容实体转换为字符串，同时关闭输入流
     * <p/>
     * <b>注意：</b> 响应内容太长不适宜使用 EntityUtils
     *
     * @param response HttpResponse
     * @return 响应内容实体
     * @throws java.io.IOException
     */
    private static byte[] consumeResponseEntity(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            if (logger.isDebugEnabled())
                logger.debug("Response status line: " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();
            // long len = responseEntity.getContentLength();
            // if (len != -1) { //&& len < 65536) {
            return EntityUtils.toByteArray(responseEntity);
            // }
        } else {
            if (logger.isWarnEnabled())
                logger.warn("Response status line: " + response.getStatusLine());

            return null;
        }
    }

    private static Scheme getSslScheme(
        String keyType, String keyPath, String keyPassword, int sslPort
    ) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(keyType);
        FileInputStream instream = new FileInputStream(new File(keyPath));
        try {
            trustStore.load(instream, keyPassword.toCharArray());
        } finally {
            try {
                instream.close();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }

        // 验证密钥源
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
        kmf.init(trustStore, keyPassword.toCharArray());

        // 同位体验证信任决策源
        TrustManager[] trustManagers = {new X509TrustManager() {
            /*
             * Delegate to the default trust manager.
             */
            public void checkClientTrusted(
                X509Certificate[] chain, String authType
            ) throws CertificateException {
            }

            /*
             * Delegate to the default trust manager.
             */
            public void checkServerTrusted(
                X509Certificate[] chain, String authType
            ) throws CertificateException {
            }

            /*
             * Merely pass this through.
             */
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};

        // 初始化安全套接字
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(kmf.getKeyManagers(), trustManagers, null);
        SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext);
        return new Scheme("https", sslPort, socketFactory);
    }

}
