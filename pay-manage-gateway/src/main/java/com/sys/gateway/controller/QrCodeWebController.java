package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderQueryResponse;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.*;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

/**
 * 通用支付接口
 */
@Controller
@RequestMapping(value = "")
public class QrCodeWebController {

    protected final Logger logger = LoggerFactory.getLogger(QrCodeWebController.class);

//    //微信
//    @Value("${weixin.getCodeUrl}")
//    private String getCodeUrl;
//
//    //微信
//    @Value("${weixin.appId}")
//    private String appId;

    private static String WX_APP_ID = "wxde0382a243819f04";
    private static String WX_APP_SECRET = "d4cba970727e255cf5cafd181d7fbe0e";

    public static String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNN92Y79CRjcvpAhSNC8WhTwDltdlo0OOjZ8ZPtD2LnhBgL4rGC6qBGgE8Lcapxw4iKMVjozvHmMSud51QNTeIemyEnkil0r8wj9BribFBL5nlTa3rh5RWjIxugHwE8M+tydrR5KZ1CsYmxc/LM8pbrI20e03HPe1lrp1ZWIfS22c8p89B9x5ttR8CC2sIrbbMAcJ7aTwau6n2fpAEJq8d2RT+GcZYQdAJdbsOixngz73/ub3Ea7YccfAnWX0mQkQYlDJM+CtziOVJBOysIh1nIW4F4j7iz9CEbMBvWm/w5geFzix5jDe2y7hwy51ISudvz4+sSCVXNTOiFjw6Bv9zAgMBAAECggEAfw+wBAZq3DwAJ6Pmh836wRwLi6PmfSfOWl1qEpby9WeABntgWrdub4DNca8iW+otblDO4cqiZAGxneUkF2H6mILGl0CvzvvjaLdNaSTX5vYEe0w8W3p+3qzRQi+65tshkZXYnBgmKZNLHOHJCGWOApQsYCK9pbKzolTNYPGGLBJEzjK9CVyJcqOFnflzJurg03yuA8dFh8TRmqjANeoHXd7u4Io+FoK1zb/g7hVBSyzQnhK0Ty03HO7f54ciLctIcPhtDZtkrwKe9Ei9+CxkHDmNXbPCfQ2G3gXT8GimcBbFFGwyWde3wPXPov+dv/qKoV/bkPb55pi5MKVTPoEvyQKBgQDGApfonvbnAdsySeTVThqEizQdFvUh+5GL0Q/V5xy/NxKX6MiwaGVZRACl9qBjWPIeAkn+s10hfUHQBbr2GsYUIKFqrHrdFukorHTxct9odxzAdtMF8WlMyzatQcC1UIKFs48F6B/LvVBDP1M0aLZJ+wUeOzfSuU9UH3eW4rpn1QKBgQC2k2ooaJ6Q4k1uYLXG7+aBN6zrF335Gumq+sJeg/SM05kYjS6+DgoJ6auiNPJyoRJM2jnsmZDmWAp+r3333HCHmDC/viXnTtfPgpkHGiYBJfvFawnJQbL5z9Oz+oepJRDPqXU6bybxjovnoRvQEyagifqmgetKg2L2jYcGVDF2JwKBgCznPafFFNzCMQEwfLJaqezQ3JqUMDbqo9D/MgYiCQTb6l2Erq5Cnmkl4LfCPBELhKyFfF5EMqR7kUcpZYKWA8FgvPpB7wLgRTOyGDsA/+TiziRfTe+VFXoSw2168casYU0MsSx3vW4ommEFpUrHTD9uq3R1nW0uFO1QzX/sHrWZAoGBAJJQALrxXGFvee4Cwqnyhx72pzSfVuzSjH/hBPMJfGl/CSmLuvHD/neDM3CCTele+3NrTxA04NI3q/FqYeDIX8XKSQbgMy/zFy/M0SXH5rz025eR9/25ENzxmA6bryv18Er62l9BxEvAmI2/prJRJptw99WIOC82q5A6SwLfZePxAoGAN92KLkU9mMHN8eRcSdl2ZGekjFIjJCfU3uR9HuRATbvUDDPpZqUnuAH73vgFtD9iFR7tSMJIsOH2EGscCdu8cgITJfGYjsDLeap3RImFMlTgQQuJKpMFcc8TLkiMBn7dpmv1/6nTtXIIXo35eUzuIh+qeU+b2MVlJzcVwXlnxS8=";
    public static String APP_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjTfdmO/QkY3L6QIUjQvFoU8A5bXZaNDjo2fGT7Q9i54QYC+KxguqgRoBPC3GqccOIijFY6M7x5jErnedUDU3iHpshJ5IpdK/MI/Qa4mxQS+Z5U2t64eUVoyMboB8BPDPrcna0eSmdQrGJsXPyzPKW6yNtHtNxz3tZa6dWViH0ttnPKfPQfcebbUfAgtrCK22zAHCe2k8Grup9n6QBCavHdkU/hnGWEHQCXW7DosZ4M+9/7m9xGu2HHHwJ1l9JkJEGJQyTPgrc4jlSQTsrCIdZyFuBeI+4s/QhGzAb1pv8OYHhc4seYw3tsu4cMudSErnb8+PrEglVzUzohY8Ogb/cwIDAQAB";
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiNyCocH0BP93v4uY5v+EvwDF9HpU8bzLtasbuqHwkAcHdld50WRKlRdg9VBdqFTbq2ful17efZdFEBj1nmq7D4yxgQQfZZzR+2O2gpmXrW2H227OyLcU0WV4455eMngAgs58x7aO1kwMmx6/lfuvw1Thf2eHNglY6k3JC/BpNwK63REFVMQV/M7n7AflRfcZ65CEmI55mjgon72vPAm18M0EczN6JlXzMS6e2v1aCicM58gjZ0pI3OYeUwiGF43xdaGwvyM/jfGap+GyVIhMBJyiJJoZ72gLDv5LjIKFWkl8uVdwPDCYGQhMqSsGPIANvQm1khih3o94Ms4A9gdFBwIDAQAB";

    public static String SERVICE_WINDOW_NAME = "易付宝";
    public static String ALIPAY_APP_ID = "2019081066147860";//"2019081066188046";

    @Autowired
    QrCodeWebService qrCodeWebService;

    @Autowired
    GwApiQueryService gwApiQueryService;

    private static String BIZ = "gwqrPay_";

    /**
     * No.1
     * 客户端扫描二维码台牌
     */
    @RequestMapping("/gwqr/{storeId}")
    public String gwqr(HttpServletRequest request, HttpServletResponse response, @PathVariable String storeId, Model model) {
        String methodName = "扫描二维码台牌接口: storeId=" + storeId + "  ";
        String userAgent = request.getHeader("user-agent");
        logger.info(BIZ + methodName + "收款二维码,storeId=" + storeId + ",  浏览器userAgent信息= " + userAgent);
        try {
            String verison = request.getParameter("v");
            String sign = request.getParameter("s");//签名=MD5(storeId+87654321);todo:上线后开启校验
            logger.info(BIZ + methodName + "收款二维码,version(v)=" + verison + ",  sign(s)= " + sign);
            if (!"1".equals(verison)) {
                logger.info(BIZ + methodName + " version不等于1, 跳转至错误页面");
                return "modules/qrcode/qrCodeWebFail";
            }

            String payType = "";
            int clientType = ClientUtil.getUserAgentType(request);
            if (clientType == 1) {
                //支付宝(服务窗)
                payType = PayTypeEnum.ALIPAY_SERVICE_WINDOW.getCode();
                logger.info(BIZ + methodName + "客户端:支付宝,payType=" + payType);
            } else if (clientType == 2) {
                //微信(公众号),JSAPI支付
                payType = PayTypeEnum.WX_PUBLIC_NATIVE.getCode();
                logger.info(BIZ + methodName + "客户端:微信, payType=" + payType);

            } else {
                //其它客户端
                model.addAttribute("mchtName", "请使用支付宝或微信扫码");
                model.addAttribute("qrMessage", "请使用支付宝或微信扫码支付");
                logger.info(storeId + "客户端:非支付宝非微信,跳转至错误页面");
                return "modules/qrcode/qrCodeWebFail";
            }
            Map dataMap = qrCodeWebService.prepareOrder(storeId, payType);
            logger.info(BIZ + methodName + "返回QR页面dataMap=" + JSON.toJSONString(dataMap));
            model.addAttribute("dataMap", dataMap);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "modules/qrcode/qrCodeWebFail";
        }

        return "modules/qrcode/qrCodeWeb";
    }


    /**
     * No.2
     * 二维码收款页面输入金额后,提交订单
     * 请求微信授权code
     */
    @RequestMapping("/gwqr/confirmPage/{storeId}/{payType}")
    public String confirm(HttpServletRequest request, HttpServletResponse response, @PathVariable String storeId, @PathVariable String payType, Model model) {
        String methodName = "扫描二维码台牌输入金额后提交订单: storeId=" + storeId + "  ";
        try {
            String sessionId = request.getParameter("sessionId");//交易sessionnId
            String transAmt = request.getParameter("transAmt");//页面交易金额(元)
//            String payType = request.getParameter("payType");

            logger.info(BIZ + methodName + "收到QR页面确认请求: sessionId=" + sessionId + " transAmt=" + transAmt + " storeId=" + payType + " payType=" + payType);
            if (StringUtils.isBlank(sessionId)) {
                sessionId = IdUtil.getUUID();//todo:作为页面验证token或者商户订单号
            }
            if (StringUtils.isBlank(transAmt)) {
                transAmt = "0";
            }
            String transAmtFen = NumberUtils.changeY2F(transAmt);
            logger.info(BIZ + methodName + "收到QR页面确认请求,整理后: sessionId=" + sessionId + " transAmt=" + transAmt + " storeId=" + payType + " payType=" + payType);

            if (PayTypeEnum.ALIPAY_SERVICE_WINDOW.getCode().equals(payType)) {
                String redirectUrl = "https://wx.chenzhaole.com/gwqr/recAlipayRedirectUri/" + storeId + "/" + sessionId + "/" + transAmtFen;
                logger.info(BIZ + methodName + "支付宝获取code后跳转至redirectURL:" + redirectUrl);
                redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");

                String url = "https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm?app_id=" + ALIPAY_APP_ID + "&scope=auth_base&redirect_uri=" + redirectUrl;
                String forward = "redirect:" + url;
                logger.info(BIZ + methodName + "获取支付宝受权code请求URL:" + forward);
                return forward;
            } else {
                // 调用微信官方网页受权,获取code成功后跳转至redirectURL
                String redirectUrl = "https://wx.chenzhaole.com/gwqr/recWeiXinRedirectUri/" + storeId + "/" + sessionId + "/" + transAmtFen;
                logger.info(BIZ + methodName + "微信获取code后跳转至redirectURL:" + redirectUrl);
                redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");

                // 获取微信官方网页受权code请求URL,如果成功则跳转至redirect_uri地址,方法recWeiXinRedirect接收.
                // 如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE
                String getWeiXinCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WX_APP_ID + "&redirect_uri=" + redirectUrl + "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
                logger.info(BIZ + methodName + "获取微信受权code请求URL:" + getWeiXinCodeUrl);

                String forward = "redirect:" + getWeiXinCodeUrl;
                logger.info(BIZ + methodName + "获取微信受权code请求重定向URL:" + forward);
                return forward;
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ + methodName + "  异常:" + e.getMessage());
            return "modules/qrcode/qrCodeWebFail";
        }
    }

    /**
     * No.3 AliPay-ServiceWindow
     * <p>
     * 获取code时,接收支付宝重定向
     */
    @RequestMapping("/gwqr/recAlipayRedirectUri/{storeId}/{sessionnId}/{tranAmt}")
    public String recAlipayRedirectUri(HttpServletRequest request, HttpServletResponse response, Model model,
                                       @PathVariable String storeId, @PathVariable String sessionnId, @PathVariable String tranAmt) {
        String methodName = "接收支付宝重定向: storeId=" + storeId + "  ";
        try {
            String app_id = request.getParameter("app_id");
            String auth_code = request.getParameter("auth_code");
            logger.info(BIZ + methodName + "接收到支付宝网页受权信息: app_id=" + app_id + " auth_code=" + auth_code + " sessionnId=" + sessionnId + " tranAmt=" + tranAmt);
            logger.info(BIZ + methodName + "接收到支付宝网页受权信息:request.map=" + JSON.toJSONString(request.getParameterMap()));

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
                return "modules/qrcode/qrCodeWebFail";
            }

            String userId = oauthTokenResponse.getUserId();

            //TODO:boss-handler调用trans应返回的6个调端参数
            Map orderMap = new HashMap();
            orderMap.put("appId", ALIPAY_APP_ID);
            orderMap.put("buyerUserId", userId);
            orderMap.put("storeId", storeId);
            orderMap.put("sessionId", sessionnId);
            orderMap.put("amount", tranAmt);
            orderMap.put("payType", PayTypeEnum.ALIPAY_SERVICE_WINDOW.getCode());//todo:硬编码，待对接支付宝后整理代码
            Map payInfoMap = qrCodeWebService.confirmOrder(orderMap, "");//todo:公众号支付返回map以后优化为对象
            logger.info(BIZ + methodName + "唤起支付宝服务窗支付，boss-trade返回值payInfoMap：" + JSON.toJSONString(payInfoMap));

            if (payInfoMap == null) {
                return "modules/qrcode/qrCodeWebFail";
            }
            model.addAttribute("tradeNO", payInfoMap.get("tradeNO"));//todo:与微信的区别:: 唤起支付宝前端js只需1个参数"tradeNO",微信需要6个.
            model.addAttribute("mchtName", "测试商户");
            model.addAttribute("amount", NumberUtils.changeF2Y(tranAmt));
            model.addAttribute("mchtOrderId", sessionnId);

        } catch (Exception e) {
            logger.error(BIZ + methodName + "获取code时,接收支付宝重定向异常", e.getMessage());
            return "modules/qrcode/qrCodeWebFail";
        }
        return "modules/qrcode/qrCodeWebConfirmAlipay";
    }

    /**
     * No.3 WX-Public
     * <p>
     * 获取code时,接收微信重定向
     * 如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE
     * 公众号支付所需6个参数值(appId,timeStamp,nonceStr,package,signType,paySign)传给前台页面,JSAPI自动调端支付
     * 注: package为页面关键字需重命名
     */
    @RequestMapping("/gwqr/recWeiXinRedirectUri/{storeId}/{sessionnId}/{tranAmt}")
    public String recWeiXinRedirectUri(HttpServletRequest request, HttpServletResponse response, Model model,
                                       @PathVariable String storeId, @PathVariable String sessionnId, @PathVariable String tranAmt) {
        String methodName = "接收微信重定向: storeId=" + storeId + "  ";
        try {
            String code = request.getParameter("code");
            String state = request.getParameter("state");
            logger.info(BIZ + methodName + "接收到微信网页受权信息:code=" + code + ",state=" + state + " 原始订单信息:sessionId=" + sessionnId + ",tranAmt=" + tranAmt);
            logger.info(BIZ + methodName + "原始订单信息URLDecoder.decode后的值, sessionId=" + sessionnId + ",tranAmt=" + tranAmt);

            String getAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WX_APP_ID + "&secret=" + WX_APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
            logger.info(BIZ + methodName + "获取code后获取access_token的getAccessTokenUrl: " + getAccessTokenUrl);
            String resp = HttpUtil.get(getAccessTokenUrl);
            logger.info(BIZ + methodName + "获取code后获取access_token原始返回值resp: " + resp);
            JSONObject json = JSON.parseObject(resp);
            String openId = json.getString("openid");
            logger.info(BIZ + methodName + "获取OpenId= " + openId);

            //TODO:boss-handler调用trans应返回的6个调端参数
            Map orderMap = new HashMap();
            orderMap.put("appId", WX_APP_ID);
            orderMap.put("buyerUserId", openId);
            orderMap.put("storeId", storeId);
            orderMap.put("sessionId", sessionnId);
            orderMap.put("amount", tranAmt);
            orderMap.put("payType", PayTypeEnum.WX_PUBLIC_NATIVE.getCode());//todo:硬编码，待对接支付宝后整理代码
            Map payInfoMap = qrCodeWebService.confirmOrder(orderMap, "");//todo:公众号支付返回map以后优化为对象
            logger.info(BIZ + methodName + "唤起微信H5支付，boss-trade返回值payInfoMap：" + JSON.toJSONString(payInfoMap));

            if (payInfoMap == null) {
                return "modules/qrcode/qrCodeWebFail";
            }
            model.addAttribute("jsonWechatMap", payInfoMap);
            model.addAttribute("mchtName", "测试商户");
            model.addAttribute("amount", NumberUtils.changeF2Y(tranAmt));
            model.addAttribute("payType", PayTypeEnum.WX_PUBLIC_NATIVE.getCode());
            model.addAttribute("mchtOrderId", sessionnId);

            logger.info(BIZ + methodName + "唤起微信H5支付:jsonWechatMap=" + JSON.toJSONString(payInfoMap));
            logger.info(BIZ + methodName + "唤起微信H5支付:转向页面 = modules/qrcode/qrCodeWebConfirmWX");

        } catch (Exception e) {
            e.printStackTrace();
            return "modules/qrcode/qrCodeWebFail";
        }
        return "modules/qrcode/qrCodeWebConfirmWX";
    }


    /**
     * 完成页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/gwqr/toQrCodeOrderFinishPage")
    public String toQrCodeOrderFinishPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        String methodName = "支付完成页面: storeId=" + "xxxxxx" + "  ";
        try {
            String mchtOrderId = request.getParameter("mchtOrderId");
            if (StringUtils.isBlank(mchtOrderId)) {
                return "modules/qrcode/qrCodeWebFinish";
            }
            String orderTime = DateUtils2.getNowTimeStr(DateUtils2.STR_DATEFORMATE_yyyyMMddHHmmss);
            logger.info(BIZ + methodName + "聚合二维码查询订单:mchtOrderId=" + mchtOrderId + ",orderTime=" + orderTime);

            String mchtId = "3310000000666666";//TODO:通过sessionId查询缓存中的mchtInfo对象
            String key = "12345678901234567890123456789012";
            TradeApiQueryRequest queryRequest = PlatRequestUtil.buildTradeApiQueryRequest(mchtId, key, mchtOrderId, "", orderTime);
            logger.info(BIZ + methodName + "聚合二维码查询订单,queryRequest=" + JSON.toJSONString(queryRequest));
            ApiPayOrderQueryResponse queryResponse = (ApiPayOrderQueryResponse) gwApiQueryService.query(queryRequest, "");
            logger.info(BIZ + methodName + "聚合二维码查询订单,queryResponse=" + JSON.toJSONString(queryResponse));
            ApiPayOrderQueryResponse.ApiPayOrderQueryResponseBody body = queryResponse.getBody();
            String payStatus = body.getStatus();

            if ("SUCCESS".equals(payStatus)) {
                String platOrderId = body.getTradeId();
                String verifyCode = platOrderId.substring(platOrderId.length() - 5, platOrderId.length() - 1);
                model.addAttribute("verifyCode", verifyCode);

                String amout = body.getAmount();
                model.addAttribute("amount", NumberUtils.changeF2Y(amout));
            }


            model.addAttribute("mchtOrderId", mchtOrderId);
            model.addAttribute("payStatus", payStatus);


        } catch (Exception e) {
            logger.error(BIZ + methodName + "聚合二维码查询订单异常!", e);
            return "modules/qrcode/qrCodeWebFail";
        }
        return "modules/qrcode/qrCodeWebFinish";
    }


}
