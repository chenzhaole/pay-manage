package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.java_websocket.WebSocket;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderQueryResponse;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.common.util.NumberUtils;
import com.sys.gateway.common.ClientUtil;
import com.sys.gateway.common.PlatRequestUtil;
import com.sys.gateway.service.GwApiQueryService;
import com.sys.gateway.service.QrCodeWebService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * for开发测试
 * <p>
 * 获取微信openId
 * 获取支付宝userId
 */
@Controller
@RequestMapping(value = "")
public class QrCodeGetOpenIDController {

    protected final Logger logger = LoggerFactory.getLogger(QrCodeGetOpenIDController.class);


    private static String WX_APP_ID_YFB = "wxde0382a243819f04";
    private static String WX_APP_SECRET_YFB = "d4cba970727e255cf5cafd181d7fbe0e";

    private static String WX_APP_ID_QF = "wxeb6e671f5571abce";
    private static String WX_APP_SECRET_QF = "7fe6edd479ca9f47a75f3b395fa9f181";

    public static String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNN92Y79CRjcvpAhSNC8WhTwDltdlo0OOjZ8ZPtD2LnhBgL4rGC6qBGgE8Lcapxw4iKMVjozvHmMSud51QNTeIemyEnkil0r8wj9BribFBL5nlTa3rh5RWjIxugHwE8M+tydrR5KZ1CsYmxc/LM8pbrI20e03HPe1lrp1ZWIfS22c8p89B9x5ttR8CC2sIrbbMAcJ7aTwau6n2fpAEJq8d2RT+GcZYQdAJdbsOixngz73/ub3Ea7YccfAnWX0mQkQYlDJM+CtziOVJBOysIh1nIW4F4j7iz9CEbMBvWm/w5geFzix5jDe2y7hwy51ISudvz4+sSCVXNTOiFjw6Bv9zAgMBAAECggEAfw+wBAZq3DwAJ6Pmh836wRwLi6PmfSfOWl1qEpby9WeABntgWrdub4DNca8iW+otblDO4cqiZAGxneUkF2H6mILGl0CvzvvjaLdNaSTX5vYEe0w8W3p+3qzRQi+65tshkZXYnBgmKZNLHOHJCGWOApQsYCK9pbKzolTNYPGGLBJEzjK9CVyJcqOFnflzJurg03yuA8dFh8TRmqjANeoHXd7u4Io+FoK1zb/g7hVBSyzQnhK0Ty03HO7f54ciLctIcPhtDZtkrwKe9Ei9+CxkHDmNXbPCfQ2G3gXT8GimcBbFFGwyWde3wPXPov+dv/qKoV/bkPb55pi5MKVTPoEvyQKBgQDGApfonvbnAdsySeTVThqEizQdFvUh+5GL0Q/V5xy/NxKX6MiwaGVZRACl9qBjWPIeAkn+s10hfUHQBbr2GsYUIKFqrHrdFukorHTxct9odxzAdtMF8WlMyzatQcC1UIKFs48F6B/LvVBDP1M0aLZJ+wUeOzfSuU9UH3eW4rpn1QKBgQC2k2ooaJ6Q4k1uYLXG7+aBN6zrF335Gumq+sJeg/SM05kYjS6+DgoJ6auiNPJyoRJM2jnsmZDmWAp+r3333HCHmDC/viXnTtfPgpkHGiYBJfvFawnJQbL5z9Oz+oepJRDPqXU6bybxjovnoRvQEyagifqmgetKg2L2jYcGVDF2JwKBgCznPafFFNzCMQEwfLJaqezQ3JqUMDbqo9D/MgYiCQTb6l2Erq5Cnmkl4LfCPBELhKyFfF5EMqR7kUcpZYKWA8FgvPpB7wLgRTOyGDsA/+TiziRfTe+VFXoSw2168casYU0MsSx3vW4ommEFpUrHTD9uq3R1nW0uFO1QzX/sHrWZAoGBAJJQALrxXGFvee4Cwqnyhx72pzSfVuzSjH/hBPMJfGl/CSmLuvHD/neDM3CCTele+3NrTxA04NI3q/FqYeDIX8XKSQbgMy/zFy/M0SXH5rz025eR9/25ENzxmA6bryv18Er62l9BxEvAmI2/prJRJptw99WIOC82q5A6SwLfZePxAoGAN92KLkU9mMHN8eRcSdl2ZGekjFIjJCfU3uR9HuRATbvUDDPpZqUnuAH73vgFtD9iFR7tSMJIsOH2EGscCdu8cgITJfGYjsDLeap3RImFMlTgQQuJKpMFcc8TLkiMBn7dpmv1/6nTtXIIXo35eUzuIh+qeU+b2MVlJzcVwXlnxS8=";
    public static String APP_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjTfdmO/QkY3L6QIUjQvFoU8A5bXZaNDjo2fGT7Q9i54QYC+KxguqgRoBPC3GqccOIijFY6M7x5jErnedUDU3iHpshJ5IpdK/MI/Qa4mxQS+Z5U2t64eUVoyMboB8BPDPrcna0eSmdQrGJsXPyzPKW6yNtHtNxz3tZa6dWViH0ttnPKfPQfcebbUfAgtrCK22zAHCe2k8Grup9n6QBCavHdkU/hnGWEHQCXW7DosZ4M+9/7m9xGu2HHHwJ1l9JkJEGJQyTPgrc4jlSQTsrCIdZyFuBeI+4s/QhGzAb1pv8OYHhc4seYw3tsu4cMudSErnb8+PrEglVzUzohY8Ogb/cwIDAQAB";
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiNyCocH0BP93v4uY5v+EvwDF9HpU8bzLtasbuqHwkAcHdld50WRKlRdg9VBdqFTbq2ful17efZdFEBj1nmq7D4yxgQQfZZzR+2O2gpmXrW2H227OyLcU0WV4455eMngAgs58x7aO1kwMmx6/lfuvw1Thf2eHNglY6k3JC/BpNwK63REFVMQV/M7n7AflRfcZ65CEmI55mjgon72vPAm18M0EczN6JlXzMS6e2v1aCicM58gjZ0pI3OYeUwiGF43xdaGwvyM/jfGap+GyVIhMBJyiJJoZ72gLDv5LjIKFWkl8uVdwPDCYGQhMqSsGPIANvQm1khih3o94Ms4A9gdFBwIDAQAB";

    public static String ALIPAY_APP_ID = "2019081066147860";//"2019081066188046";

    private static String BIZ = "gwqrGetInfo_";

    /**
     * WX-01
     * <p>
     * 获取微信openId等信息
     */
    @RequestMapping("/gwqr/wx/getInfo/{storeId}")
    public String getWXInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable String storeId, Model model) {

        String methodName = "获取微信openId接口: storeId=" + storeId + "  ";
        String userAgent = request.getHeader("user-agent");
        logger.info(BIZ + methodName + "  浏览器userAgent信息= " + userAgent);
        try {
            String wxPublicName = request.getParameter("n");
            if (StringUtils.isBlank(wxPublicName)) {
                wxPublicName = "yfb";
            }
            String redirectUrl = "https://wx.chenzhaole.com/gwqr/wx/getInfo/recive/" + storeId + "/" + wxPublicName;
            logger.info(BIZ + methodName + "微信获取code后跳转至redirectURL:" + redirectUrl);
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");

            // 获取微信官方网页受权code请求URL,如果成功则跳转至redirect_uri地址,方法recWeiXinRedirect接收.
            // 如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE
            // 默认使用"易付宝"公众号
            String getWeiXinCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WX_APP_ID_YFB + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

            if ("yfb".equalsIgnoreCase(wxPublicName)) {//易付宝公众号
                getWeiXinCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WX_APP_ID_YFB + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
            }
            if ("qf".equalsIgnoreCase(wxPublicName)) {//钱方公众号
                getWeiXinCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WX_APP_ID_QF + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
            }

            logger.info(BIZ + methodName + "获取微信受权code请求URL:" + getWeiXinCodeUrl);

            String forward = "redirect:" + getWeiXinCodeUrl;
            logger.info(BIZ + methodName + "获取微信受权code请求重定向URL:" + forward);
            return forward;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "modules/qrcode/qrCodeWebFail";
        }

    }


    /**
     * WX-02
     * <p>
     * 获取code时,接收微信重定向
     * 如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE
     * 公众号支付所需6个参数值(appId,timeStamp,nonceStr,package,signType,paySign)传给前台页面,JSAPI自动调端支付
     * 注: package为页面关键字需重命名
     */
    @RequestMapping("/gwqr/wx/getInfo/recive/{storeId}/{wxPublicName}")
    @ResponseBody
    public String reciveWX(HttpServletRequest request, HttpServletResponse response, Model model,
                           @PathVariable String storeId, @PathVariable String wxPublicName) {

        String methodName = "获取微信openId(接收WX重定向)接口: storeId=" + storeId + ", wxPublicName=" + wxPublicName;
        try {
            String code = request.getParameter("code");
            String state = request.getParameter("state");
            logger.info(BIZ + methodName + "接收到微信网页受权信息:code=" + code + ",state=" + state);

            String appId = "";
            String getAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WX_APP_ID_YFB + "&secret=" + WX_APP_SECRET_YFB + "&code=" + code + "&grant_type=authorization_code";

            if ("yfb".equalsIgnoreCase(wxPublicName)) {//易付宝公众号
                appId = WX_APP_ID_YFB;
                getAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WX_APP_ID_YFB + "&secret=" + WX_APP_SECRET_YFB + "&code=" + code + "&grant_type=authorization_code";
            }
            if ("qf".equalsIgnoreCase(wxPublicName)) {//钱方公众号
                appId = WX_APP_ID_QF;
                getAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WX_APP_ID_QF + "&secret=" + WX_APP_SECRET_QF + "&code=" + code + "&grant_type=authorization_code";
            }

            logger.info(BIZ + methodName + "获取code后获取access_token的getAccessTokenUrl: " + getAccessTokenUrl);
            String resp = HttpUtil.get(getAccessTokenUrl);
            logger.info(BIZ + methodName + "获取code后获取access_token原始返回值resp: " + resp);
            JSONObject json = JSON.parseObject(resp);
            String openId = json.getString("openid");
            logger.info(BIZ + methodName + "获取 OpenId= " + openId);

            JSONObject j = new JSONObject();
            j.put("respCode", "OK");
            j.put("respMsg", "成功");
            j.put("appId", appId);
            j.put("wxPublicName", wxPublicName);
            j.put("code", code);
            j.put("openId", openId);
            return j.toJSONString();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ + methodName + e.getMessage());
            JSONObject j = new JSONObject();
            j.put("respCode", "ERROR");
            j.put("respMsg", e.getMessage());
            return j.toJSONString();
        }
    }

    /**
     * WX-h5
     * 唤起公众号支付H5-demo页面
     */
    @RequestMapping("/gwqr/demoCallWX")
    public String demoCallWX(HttpServletRequest request, HttpServletResponse response, Model model) {
        logger.info("唤起公众号支付wx-public-demo-h5页面");
        return "modules/qrcode/demoCallWX";
    }


    /** ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* **/

    /** ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* ********* **/


    /**
     * Alipay-01
     * <p>
     * 获取支付宝code等信息
     */
    @RequestMapping("/gwqr/alipay/getInfo/{storeId}")
    public String getAilpayInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable String storeId, Model model) {

        String methodName = "获取支付宝code接口: storeId=" + storeId + "  ";
        String userAgent = request.getHeader("user-agent");
        logger.info(BIZ + methodName + "  浏览器userAgent信息= " + userAgent);
        try {

            String redirectUrl = "https://wx.chenzhaole.com/gwqr/alipay/getInfo/recive/" + storeId;
            logger.info(BIZ + methodName + "getInfo支付宝-获取code后跳转至redirectURL:" + redirectUrl);
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");

            String url = "https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm?app_id=" + ALIPAY_APP_ID + "&scope=auth_base&redirect_uri=" + redirectUrl;
            String forward = "redirect:" + url;
            logger.info(BIZ + methodName + "获取支付宝受权code请求URL:" + forward);
            return forward;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "modules/qrcode/qrCodeWebFail";
        }

    }


    /**
     * Alipay-02
     * <p>
     * 获取code时,接收支付宝重定向
     */
    @RequestMapping("/gwqr/alipay/getInfo/recive/{storeId}")
    @ResponseBody
    public String recAlipayRedirectUri(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String storeId) {
        String methodName = "获取支付宝code(接收alipay重定向)接口: storeId=" + storeId + "  ";
        try {
            String app_id = request.getParameter("app_id");
            String auth_code = request.getParameter("auth_code");
            logger.info(BIZ + methodName + "接收到支付宝网页受权信息: app_id=" + app_id + " auth_code=" + auth_code);

            //APP_ID位沙箱里面写的appid。APP_PRIVATE_KEY为自己的私钥，用于报文签名.  ALIPAY_PUBLIC_KEY 这个是支付宝公钥,用于验签
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                    ALIPAY_APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
            AlipaySystemOauthTokenRequest alipayRequest = new AlipaySystemOauthTokenRequest();
            alipayRequest.setCode(auth_code);
            alipayRequest.setGrantType("authorization_code");//固定值
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(alipayRequest);
            logger.info(BIZ + methodName + "接收到支付宝网页受权信息 oauthTokenResponse= " + JSON.toJSONString(oauthTokenResponse));

            if (!oauthTokenResponse.isSuccess()) {
                logger.info(BIZ + methodName + "接收到支付宝网页受权信息失败,msg=" + oauthTokenResponse.getSubMsg());
                JSONObject j = new JSONObject();
                j.put("respCode", "ERROR");
                j.put("respMsg", oauthTokenResponse.getSubMsg());
                return j.toJSONString();
            }

            String userId = oauthTokenResponse.getUserId();

            JSONObject j = new JSONObject();
            j.put("respCode", "OK");
            j.put("respMsg", "成功");
            j.put("appId", ALIPAY_APP_ID);
            j.put("app_id", app_id);
            j.put("auth_code", auth_code);
            j.put("userId", userId);

            return j.toJSONString();

        } catch (Exception e) {
            logger.error(BIZ + methodName + "获取code时,接收支付宝重定向异常", e.getMessage());
            e.printStackTrace();
            JSONObject j = new JSONObject();
            j.put("respCode", "ERROR");
            j.put("respMsg", e.getMessage());
            return j.toJSONString();
        }
    }

}
