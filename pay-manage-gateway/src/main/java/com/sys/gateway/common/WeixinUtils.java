//package com.sys.gateway.common;
//
//import com.alibaba.fastjson.JSONObject;
//import com.sys.common.util.DateUtils2;
//import com.usc.wechat.config.WeixinConstants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Formatter;
//import java.util.concurrent.TimeUnit;
//
///**
// *  2019/7/26. From 李多君
// */
//@Component("weixinUtils")
//public class WeixinUtils {
//    private static final Logger log = LoggerFactory.getLogger(WeixinUtils.class);
//    @Autowired
//    private RestTemplate restTemplate;
//    @Value("${weixin.appid}")
//    private String appid;
//    @Value("${weixin.appsecret}")
//    private String appsecret;
//    @Value("${weixin.templateid}")
//    private String templateid;
//
//    /**
//     * 获取access_tokenw唯一方式
//     *
//     * @return
//     */
//    public String getAccessToken() {
//        String accessToken = (String)redisTemplate.opsForValue().get(WeixinConstants.REDIS_ACCESSTOKEN);
//        if (null == accessToken) {
//            log.info("redis获取accessToken,调用接口获取");
//            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
//            url = url.replace("APPID", appid);
//            url = url.replace("APPSECRET", appsecret);
//            log.info("接口调用URL:{}",url);
//
//            MultiValueMap<String, Object> paraMap = new LinkedMultiValueMap<>();
//            String result = restTemplate.postForObject(url, paraMap, String.class);
//            JSONObject bodyJson = JSONObject.parseObject(result);
//
//            if ((bodyJson.containsKey("access_token"))) {
//                accessToken = bodyJson.get("access_token").toString();
//                redisTemplate.opsForValue().set(WeixinConstants.REDIS_ACCESSTOKEN, accessToken,
//                        WeixinConstants.REDIS_ACCESSTOKEN_TIME, TimeUnit.SECONDS);
//            } else {
//                log.info("调用接口获取token失败:{}", bodyJson);
//            }
//        }
//        log.info("redis获取AccessToken成功");
//        return accessToken;
//    }
//
//    /**
//     * 字符处理
//     *
//     * @param hash
//     * @return
//     */
//    private String byteToHex(final byte[] hash) {
//        Formatter formatter = new Formatter();
//        for (byte b : hash) {
//            formatter.format("%02x", b);
//        }
//        String result = formatter.toString();
//        formatter.close();
//        return result;
//    }
//
//    /**
//     * 订购成功通知模板
//     *
//     * @param openid
//     * @param alarminfo
//     * @param location
//     * @return
//     */
//    public String sendTemplate(String openid, String parkid,String alarminfo,String location) {
//        String accessToken = getAccessToken();
//        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
//        String data = "{" + "\"touser\":\"" + openid + "\","
//                + "\"template_id\":\"" + templateid + "\","
//                + "\"topcolor\":\"#FF0000\","
//                + "\"data\":{\"first\":{\"value\":\"消小宝提醒您有一条告警信息!\",\"color\":\"#000000\"},"
//                + "\"keyword1\":{\"value\":\"" + parkid + "\",\"color\":\"#000000\"},"
//                + "\"keyword2\":{\"value\":\"" + DateUtils2.getNowTimeStr() + "\",\"color\":\"#000000\"},"
//                + "\"keyword3\":{\"value\":\"" + location + "\",\"color\":\"#000000\"},"
//                + "\"keyword4\":{\"value\":\"" + alarminfo + "\",\"color\":\"#000000\"},"
//                + "\"remark\":{\"value\":\"\\r\\n建筑电气火灾隐患巡查系统\",\"color\":\"#000000\"}}}";
//        log.info(data);
//        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        String result = restTemplate.postForObject(url, data, String.class);
//        JSONObject bodyJson = JSONObject.parseObject(result);
//        log.info("发送模板返回={}", bodyJson);
//        return bodyJson.toString();
//    }
//
//    /**
//     * 获取用户头像
//     *
//     * @param openid
//     * @return
//     */
//    public String getUserHeadImg(String openid) {
//        String headimgurlByRedis = (String)redisTemplate.opsForHash().get(WeixinConstants.REDIS_WXUSER_HEADIMGURL, openid);
//        if (null == headimgurlByRedis) {
//            log.info("redis获取头像失败,调用接口获取");
//            String accessToken = getAccessToken();
//            String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
//            log.info("接口调用URL:{}",url);
//
//            MultiValueMap<String, Object> paraMap = new LinkedMultiValueMap<>();
//            String result = restTemplate.postForObject(url, paraMap, String.class);
//            JSONObject bodyJson = JSONObject.parseObject(result);
//            if (bodyJson.containsKey("headimgurl")) {
//                String headimgurl = (String) bodyJson.get("headimgurl");
//                redisTemplate.opsForHash().put(WeixinConstants.REDIS_WXUSER_HEADIMGURL, openid, headimgurl);
//                return headimgurl;
//            } else {
//                return null;
//            }
//        } else {
//            log.info("redis获取头像成功");
//            return headimgurlByRedis;
//        }
//
//    }
//
//    /**
//     * 通过授权code获取access_token
//     *
//     * @param code
//     * @return
//     */
//    public JSONObject getAccessToken(String code) {
//        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
//        url=url.replace("APPID", appid);
//        url=url.replace("SECRET", appsecret);
//        url=url.replace("CODE", code);
//        log.info("通过code获取access_token,URL={}", url);
//
//        MultiValueMap<String, Object> paraMap = new LinkedMultiValueMap<>();
//        String result = restTemplate.postForObject(url, paraMap, String.class);
//        JSONObject bodyJson = JSONObject.parseObject(result);
//        log.info("获取access_token请求的结果为:{}", bodyJson);
//        return bodyJson;
//    }
//}