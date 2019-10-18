package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiPayRequest;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.boss.api.service.trade.handler.ITradeApiQRPayHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.DesUtil32;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MerchantService;
import com.sys.gateway.common.PlatRequestUtil;
import com.sys.gateway.service.QrCodeWebService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by chenzhaole on 2019/7/20.
 */
@Service
public class QrCodeWebServiceImpl implements QrCodeWebService {

    protected final Logger logger = LoggerFactory.getLogger(QrCodeWebServiceImpl.class);

    @Autowired
    private ITradeApiQRPayHandler tradeApiQrPayHandler;

    @Autowired
    private ITradeApiPayHandler tradeApiPayHandler;

    @Autowired
    private MerchantService merchantService;

    @Override
    public String convertKey(String mchtCode) {
        if (mchtCode.length() != 16) {
            return "";
        }
        //平台key=商户code后8位
        return mchtCode.substring((mchtCode.length() - 8), mchtCode.length());
    }


    @Override
    public boolean checkQrcodeUrl(String mchtCode, String data) {
        try {
            if (mchtCode.length() != 16) {
                return false;
            }
            String key = convertKey(mchtCode);
            String clientMchtCode = DesUtil32.decode(data, key);

            //商户code和加密data是否相等,校验二维码URL,未使用MD5为了存数据
            if (mchtCode.equals(clientMchtCode)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    @Override
    public boolean checkQrcodeConfirm(String mchtCode, String data) {
        return false;
    }


    @Override
    public Map prepareOrder(String storeId, String payType) {
        Map<String, String> rtnMap = new HashMap<>();
        try {
            rtnMap.put("storeId", storeId);
            rtnMap.put("mchtName", "测试商户");
            rtnMap.put("payType", payType);
            rtnMap.put("timestamp", String.valueOf(DateUtils2.getNowUTC()));
            rtnMap.put("postUrl", "/gwqr/confirmPage/" + storeId + "/"+payType);
            rtnMap.put("sessionId", UUID.randomUUID().toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rtnMap;
    }


    @Override
    public Map confirmOrder(Map dataMap, String ip) {

        try {
            String appId = (String) dataMap.get("appId");
            String buyerUserId = (String) dataMap.get("buyerUserId");
            String sessionId = (String) dataMap.get("sessionId");
            String payType = (String) dataMap.get("payType");
            String mchtId =(String) dataMap.get("storeId");// "3310000000666666";//TODO:通过sessionId查询缓存中的mchtInfo对象
            MchtInfo mchtInfo = findMchtInfo(mchtId);
            String biz = payType;
            String key = mchtInfo.getMchtKey();
            String mchtOrderId = sessionId;//todo:暂时用sessionId
            String amount = (String) dataMap.get("amount");
            String amountFen = (amount);//分
            String nameParam = (String) dataMap.get("orderName");
            if (StringUtils.isBlank(nameParam)) {
                nameParam = "购买商品";
            }
            String orderName = nameParam;
            String notifyUrl = "";
            String frontUrl = "https://news.163.com";//todo:
            String phone = "";
            String name = "";
            String email = "";

            TradeApiPayRequest tradeRequest = PlatRequestUtil.buildTradeApiPayRequest(mchtId, biz, key, mchtOrderId, amountFen, orderName, notifyUrl, frontUrl, appId, buyerUserId, phone, name, email);
            logger.info("QR收款码支付调用boss-trade创建API订单，参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
            CommonResult commonResult = tradeApiPayHandler.process(tradeRequest, ip);
            logger.info("QR收款码支付调用boss-trade创建API订单，返回值commonResult：" + JSON.toJSONString(commonResult));

            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                Result mchtResult = (Result) commonResult.getData();
                String payInfoStr = mchtResult.getPayInfo();
                Map payInfoMap = JSON.parseObject(payInfoStr, Map.class);
                if (payInfoMap.containsKey("package")) {
                    payInfoMap.put("packageV", payInfoMap.get("package"));
                }
                payInfoMap.put("mchtOrderId",mchtOrderId);
                return payInfoMap;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public MchtInfo findMchtInfo(String mchtCode) {
        MchtInfo merchant = merchantService.queryByKey(mchtCode);
        return merchant;
    }

    public static void main(String[] args) throws Exception {
        String amount = "1.56";
        System.out.println(NumberUtils.changeY2F(amount));
        System.out.println(URLEncoder.encode(amount, "UTF-8"));
        JSONObject json = new JSONObject();
        json.put("sessionId", UUID.randomUUID());
        json.put("amount", "9999.01");
        System.out.println(json.toJSONString());
        String encodeStr = DesUtil32.encode(json.toJSONString(), "1234abcd");
        System.out.println(encodeStr);
        System.out.println(DesUtil32.decode(encodeStr, "1234abcd"));
    }
}
