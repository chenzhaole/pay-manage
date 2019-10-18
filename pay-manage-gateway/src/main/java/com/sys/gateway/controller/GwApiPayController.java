package com.sys.gateway.controller;

import com.alibaba.dubbo.cache.Cache;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.cache.CacheOrder;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.apipay.ApiPayRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiPayRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderCreateResponse;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.common.enums.ErrorCodeEnum;

import com.sys.common.enums.PayStatusEnum;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.DesUtil32;
import com.sys.common.util.MD5Util;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwApiPayService;
import com.sys.trans.api.entry.ChanMchtPaytypeTO;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.Order;
import com.sys.trans.api.entry.Trade;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * 通用支付接口
 *
 * @author: ChenZL
 * @time: 2017年11月28日
 */
@Controller
@RequestMapping(value = "")
public class GwApiPayController {

    protected final Logger logger = LoggerFactory.getLogger(GwApiPayController.class);

    @Autowired
    GwApiPayService gwApiPayService;
    @Autowired
    ITradeApiPayHandler iTradeApiPayHandler;

    /**
     * 支付
     **/
    @RequestMapping(value = "/gateway/api/commPay")
    @ResponseBody
    public String commPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        ApiPayOrderCreateResponse apiPayResp = new ApiPayOrderCreateResponse();
        ApiPayOrderCreateResponse.ApiPayOrderCreateResponseHead head = new ApiPayOrderCreateResponse.ApiPayOrderCreateResponseHead();

        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info("API支付获取到客户端请求ip：" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("API支付收到客户端请求参数后做url解码后的值为：" + data);
            //校验请求参数
            CommonResponse checkResp = gwApiPayService.checkParam(data);
            logger.info("API支付校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));
            if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                if (!ErrorCodeEnum.E1012.getCode().equals(checkResp.getRespCode())) {
                    TradeApiPayRequest tradeRequest = (TradeApiPayRequest) checkResp.getData();
                    if (tradeRequest.getHead() != null) {
                        iTradeApiPayHandler.insertRedisRequestData(tradeRequest.getHead().getMchtId(),
                                tradeRequest.getBody() == null ? "0" : tradeRequest.getBody().getAmount(), 2);
                    }
                }
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                apiPayResp.setHead(head);
            } else {
                //调动API支付接口
                TradeApiPayRequest tradeRequest = (TradeApiPayRequest) checkResp.getData();
                logger.info("调用API支付接口，传入的TradeCommRequest信息：" + JSONObject.toJSONString(tradeRequest));
                apiPayResp = (ApiPayOrderCreateResponse) gwApiPayService.pay(tradeRequest, ip);
                logger.info("调用API支付接口，返回的CommOrderCreateResponse信息：" + JSONObject.toJSONString(apiPayResp));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("API支付接口抛异常" + e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("支付网关错误：" + e.getMessage());
        }
        logger.info("创建comm订单，返回下游商户值：" + JSON.toJSONString(apiPayResp));
        return JSON.toJSONString(apiPayResp);
    }


    /**
     * 固码支付
     * <p>
     * 固码二维码底链URL,for真实客户微信扫一扫or相册支付
     */
    @RequestMapping(value = "/gateway/qr/{platOrderNo}")
//	@ResponseBody
    public String qrPay(@PathVariable String platOrderNo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            String realOrderNo = DesUtil32.decode(platOrderNo, "12345678");
            logger.info("qrPay固码支付获取到客户端请求ip：" + ip + " platOrderNo数据: " + platOrderNo + " realOrderNo真实数据:" + realOrderNo);

            CommonResult commonResult = gwApiPayService.qrPay(realOrderNo, ip);

            if (!ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                return "订单错误";
            }
            CacheTrade cacheTrade = (CacheTrade) commonResult.getData();
            Trade trade = JSONObject.parseObject(JSON.toJSONString(cacheTrade.getTrade()), Trade.class);// (Trade) cacheTrade.getTrade();
            Order order = trade.getOrder();
            Config config = trade.getConfig();
            ChanMchtPaytypeTO cmp = config.getChanMchtPaytype();

            String url = cmp.getRefundUrl();// "https://openapi.qfpay.com/tool/v1/get_weixin_oauth_code";
            String app_code = cmp.getOpAccount();// "1B6B3CF879654C02BFA35957BADE3DD8";
            String mchid = cmp.getChanMchtNo();
            String redirect_uri = cmp.getSynNotifyUrl() + "/" + cmp.getChanCode() + "/" + order.getOrderNo() + "/" + cmp.getPayType();// "http://hx.yyjh888.com:15081/testQfpayCreateOrder";
            String key = cmp.getChanMchtPassword();// "C25907B30C084715AEC78404708230BF";

            String signOrigin = "app_code=" + app_code + "&mchid=" + mchid + "&redirect_uri=" + redirect_uri + key;
            String sign = MD5Util.MD5Encode(signOrigin);
            logger.info("qrPay固码signOrigin: " + signOrigin);
            logger.info("qrPay固码sign: " + sign);

            url = url + "?app_code=" + app_code + "&mchid=" + mchid + "&redirect_uri=" + redirect_uri + "&sign=" + StringUtils.upperCase(sign);
            logger.info("qrPay固码获取oauth_code请求url: " + url);

//			response.sendRedirect(url);

            return "redirect:" + url;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("qrPay固码接口抛异常" + e.getMessage());
        }
        return "";
    }


    /**
     * 异步通知
     * 微信固码支付时,接受上游异步通知的OAuthCode值
     * <p>
     * 表单数据
     */
    @RequestMapping("/gateway/api/qrPay/authCode/{chanCode}/{platOrderId}/{payType}")
//	@ResponseBody
    public String recNotifyAuthCode(HttpServletRequest request, HttpServletResponse response, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType, Model model) throws Exception {

        try {
            String moid = platOrderId + "-";
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info(moid + "qrPay固码支付接收oauthCode-获取到客户端请求ip：" + ip + " platOrderNo数据: " + platOrderId);
            CommonResult commonResult = gwApiPayService.qrPay(platOrderId, ip);

            if (!ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                return "订单错误2";
            }
            CacheTrade cacheTrade = (CacheTrade) commonResult.getData();
            Trade trade = JSONObject.parseObject(JSON.toJSONString(cacheTrade.getTrade()), Trade.class);
            Order order = trade.getOrder();
            Config config = trade.getConfig();

            ChanMchtPaytypeTO cmp = config.getChanMchtPaytype();
            String appCode = cmp.getOpAccount();
            String mchid = cmp.getChanMchtNo();
            String key = cmp.getChanMchtPassword();
            moid = mchid + "-" + platOrderId + "-";

            TradeApiPayRequest tradeApiPayRequest = JSONObject.parseObject(JSON.toJSONString(cacheTrade.getTradeBaseRequest()), TradeApiPayRequest.class);
            ApiPayRequestBody apiPayRequestBody = tradeApiPayRequest.getBody();

            String code = request.getParameter("code");
            logger.info(moid + "qrPay固码支付接收oauthCode-code返回值: " + code);


            String signOrigin = "code=" + code + "&mchid=" + mchid + key;
            String sign = MD5Util.MD5Encode(signOrigin);
            logger.info(moid + "qrPay固码支付接收oauthCode-signOrigin: " + signOrigin);
            logger.info(moid + "qrPay固码支付接收oauthCode-sign: " + sign);

            // "https://openapi.qfpay.com/tool/v1/get_weixin_openid?code=" + code + "&mchid=" + mchid;
            String url = cmp.getQueryBalanceUrl() + "?code=" + code + "&mchid=" + mchid;
            logger.info(moid + "qrPay固码支付接收oauthCode-获取openID的请求 url: " + url);
            String resp = this.httpGet(url, appCode, sign);
            //{"resperr": "", "respcd": "0000", "respmsg": "", "openid": "oS7Zr1FCVA7NHW8UpmR7Q69_8u-8"}
            logger.info(moid + "qrPay固码支付接收oauthCode-获取openID返回值 resp: " + resp);

            JSONObject json = JSON.parseObject(resp);
            String openId = (String) json.get("openid");//todo: if openId isBlank
            logger.info(moid + "qrPay固码支付接收oauthCode-openId返回值: " + openId);

            logger.info(moid + "qrPay固码支付接收oauthCode-openId 开始使用openId请求pay_param唤起支付值   openId: " + openId);
            TreeMap<String, String> map = new TreeMap<String, String>();
            map.put("txamt", order.getAmount());
            map.put("txcurrcd", "CNY");
            map.put("pay_type", "800207");
            map.put("out_trade_no", order.getOrderNo());
            map.put("txdtm", DateUtils2.getNowTimeStr());
            map.put("sub_openid", openId);
            map.put("goods_name", order.getOrderName());
            map.put("mchid", mchid);

            String sign2 = this.md5Sign(map, key);
            String param2 = JSON.toJSONString(map);
            String url2 = cmp.getPayUrl();// "https://openapi.qfpay.com/trade/v1/payment";
            logger.info(moid + "qrPay固码支付接收oauthCode-url2: " + url2);
            logger.info(moid + "qrPay固码支付接收oauthCode-param2: " + param2);
            String resp2 = this.httpPost(url2, map, appCode, sign2);
            logger.info(moid + "qrPay固码支付接收oauthCode-resp2: " + resp2);
            JSONObject json2 = JSON.parseObject(resp2);
            if (!json2.containsKey("pay_params")) {
                model.addAttribute("msg", "支付异常,请重新尝试");
                return "modules/qr/error-qf-wx-public-createOrder";
            }
            String pay_params = json2.getString("pay_params");
            JSONObject payParamsJson = JSON.parseObject(pay_params);
            String packageP = payParamsJson.getString("package");
            String timeStamp = payParamsJson.getString("timeStamp");
            String signType = payParamsJson.getString("signType");
            String paySign = payParamsJson.getString("paySign");
            String appId = payParamsJson.getString("appId");
            String nonceStr = payParamsJson.getString("nonceStr");

            //显示在上游or我们的扫码后支付页面的支付信息,页面上点击确认按钮唤起支付.20190425优化成之间跳转上游调端支付
            String redUrl = "https://o2.qfpay.com/q/direct" + "?";//TODO:chenzl:配置页面chanMchtPaytype无处配置
            redUrl = redUrl + "mchntnm=" + URLEncoder.encode("支付合作商9B") + "&";
            redUrl = redUrl + "txamt=" + order.getAmount() + "&";
            redUrl = redUrl + "goods_name=" + URLEncoder.encode(order.getOrderName()) + "&";
            redUrl = redUrl + "redirect_url=" + apiPayRequestBody.getCallBackUrl() + "&";
            redUrl = redUrl + "package=" + packageP + "&";
            redUrl = redUrl + "timeStamp=" + timeStamp + "&";
            redUrl = redUrl + "signType=" + signType + "&";
            redUrl = redUrl + "paySign=" + paySign + "&";
            redUrl = redUrl + "appId=" + appId + "&";
            redUrl = redUrl + "nonceStr=" + nonceStr;
            logger.info(moid + "qrPay固码支付接收oauthCode-direct调端URL: " + redUrl);

            return "redirect:" + redUrl;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(platOrderId + "qrPay固码支付接收oauthCode-异常：" + e.getMessage());
            model.addAttribute("msg", e.getMessage());
        }
        //默认跳转页面
        return "modules/qr/error-qf-wx-public-createOrder";
    }

    /**
     * @param treeMap
     * @param signkey
     * @return
     */
    public String md5Sign(TreeMap<String, String> treeMap, String signkey) {
        //开始验签
        String platSignOrigStr = "";
        String platSignStr = "";
        Set<String> keys = treeMap.keySet();
        for (String key : keys) {
            Object value = treeMap.get(key);
            platSignOrigStr = platSignOrigStr + key + "=" + value + "&";
        }
        platSignOrigStr = platSignOrigStr.substring(0, platSignOrigStr.length() - 1) + signkey;
        platSignStr = MD5Util.MD5Encode(platSignOrigStr);
        logger.info("-->平台签名2-原始字符串: " + platSignOrigStr);
        logger.info("-->平台签名2-签名后字符串: " + StringUtils.upperCase(platSignStr));
        return StringUtils.upperCase(platSignStr);
    }

    /**
     * @param url
     * @param appCode
     * @param sign
     * @return
     */
    public String httpGet(String url, String appCode, String sign) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("X-QF-APPCODE", appCode);
            httpGet.addHeader("X-QF-SIGN", sign);
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url
     * @param params
     * @return
     */
    public String httpPost(String url, Map<String, String> params, String appCode, String sign) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);// 创建httpPost
        httpPost.setHeader("X-QF-APPCODE", appCode);
        httpPost.setHeader("X-QF-SIGN", sign);

        CloseableHttpResponse response = null;
        if (params != null) {
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                paramList.add(new BasicNameValuePair(key, params.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramList, "UTF-8");
            httpPost.setEntity(formEntity);
        }
        try {
            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            } else {
                logger.info("请求返回:" + state + "(" + url + ")");
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
