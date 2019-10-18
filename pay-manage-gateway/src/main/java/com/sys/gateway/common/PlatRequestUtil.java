package com.sys.gateway.common;

import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.apipay.ApiPayRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.ApiQueryRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiPayRequest;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.SignUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenzhaole on 2019/8/1.
 */
public class PlatRequestUtil {


    /** *********************************************************************************** */

    /**
     * 生成api查询接口所需request对象for内部请求
     */
    public static TradeApiQueryRequest buildTradeApiQueryRequest(String mchtId, String key, String orderId, String tradeId, String orderTime) {
        TradeApiQueryRequest request = new TradeApiQueryRequest();
        try {
            Map<String, String> bodyMap = getApiQueryBodyMap(orderId, tradeId, orderTime);
            String sign = SignUtil.md5Sign(bodyMap, key, mchtId);

            TradeReqHead head = getCommonRequestHead(mchtId, "");
            ApiQueryRequestBody body = getApiQueryRequestBody(orderId, tradeId, orderTime);

            request.setHead(head);
            request.setBody(body);
            request.setSign(sign);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    private static Map<String, String> getApiQueryBodyMap(String orderId, String tradeId, String orderTime) {
        Map<String, String> map = new HashMap();
        map.put("orderId", orderId);
        map.put("tradeId", tradeId);
        map.put("orderTime", orderTime);
        return map;
    }

    private static ApiQueryRequestBody getApiQueryRequestBody(String orderId, String tradeId, String orderTime) {
        ApiQueryRequestBody body = new ApiQueryRequestBody();
        body.setOrderId(orderId);
        body.setTradeId(tradeId);
        body.setOrderTime(orderTime);
        return body;
    }


    /** *********************************************************************************** */

    /**
     * 生成api支付接口所需request对象for内部请求
     */
    public static TradeApiPayRequest buildTradeApiPayRequest(String mchtId, String biz, String key, String mchtOrderId, String amount, String orderName, String notifyUrl, String frontUrl, String appId, String userId, String phone, String name, String email) {
        TradeApiPayRequest request = new TradeApiPayRequest();
        try {
            Map<String, String> bodyMap = getApiPayBodyMap(mchtOrderId, amount, orderName, notifyUrl, frontUrl, appId, userId, phone, name, email);
            String moid = mchtId + "-" + mchtOrderId;
            String sign = SignUtil.md5Sign(bodyMap, key, moid);

            TradeReqHead head = getCommonRequestHead(mchtId, biz);
            ApiPayRequestBody body = getApiPayRequestBody(mchtOrderId, amount, orderName, notifyUrl, frontUrl, appId, userId, phone, name, email);

            request.setHead(head);
            request.setBody(body);
            request.setSign(sign);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }


    private static Map<String, String> getApiPayBodyMap(String mchtOrderId, String amount, String orderName, String notifyUrl, String frontUrl, String appId, String userId, String phone, String name, String email) {
        Map<String, String> map = new HashMap();
        map.put("orderId", mchtOrderId);
        map.put("orderTime", DateUtils2.getNowTimeStr(DateUtils2.STR_DATEFORMATE_yyyyMMddHHmmss));
        map.put("amount", amount);
        map.put("currencyType", "CNY");
        map.put("goods", orderName);
        map.put("notifyUrl", notifyUrl);
        map.put("callBackUrl", frontUrl);
        map.put("appId", appId);
        map.put("userId", userId);
        map.put("phone", phone);
        map.put("name", name);
        map.put("email", email);
        return map;
    }

    private static ApiPayRequestBody getApiPayRequestBody(String mchtOrderId, String amount, String orderName, String notifyUrl, String frontUrl, String appId, String userId, String phone, String name, String email) {
        ApiPayRequestBody body = new ApiPayRequestBody();
        body.setOrderId(mchtOrderId);
        body.setOrderTime(DateUtils2.getNowTimeStr(DateUtils2.STR_DATEFORMATE_yyyyMMddHHmmss));
        body.setAmount(amount);
        body.setCurrencyType("CNY");
        body.setGoods(orderName);
        body.setNotifyUrl(notifyUrl);
        body.setCallBackUrl(frontUrl);
        body.setAppId(appId);
        body.setUserId(userId);
        body.setPhone(phone);
        body.setName(name);
        body.setEmail(email);
        return body;
    }


    /**
     * **********************************************************************************
     */
    private static TradeReqHead getCommonRequestHead(String mchtId, String biz) {
        TradeReqHead head = new TradeReqHead();
        head.setBiz(biz);
        head.setMchtId(mchtId);
        head.setVersion("2.0");
        return head;
    }


}
