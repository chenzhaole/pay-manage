package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.apipay.ApiPayRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiPayRequest;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.common.enums.ClientPayWayEnum;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.DesUtil32;
import com.sys.common.util.IdUtil;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MerchantService;
import com.sys.gateway.common.ConfigUtil;
import com.sys.gateway.service.GwSdkPayService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class GwSdkPayServiceImpl implements GwSdkPayService {

    protected final Logger logger = LoggerFactory.getLogger(GwSdkPayServiceImpl.class);

    private final String BIZ_NAME = "支付SDK-创建支付订单-";

    @Autowired
    private ITradeApiPayHandler tradeApiPayHandler;

    @Autowired
    private MerchantService merchantService;

    /**
     * wap支付校验参数
     **/
    @Override
    public CommonResponse checkParam(String paramStr) {
        CommonResponse checkResp = new CommonResponse();
        try {
            if (paramStr.endsWith("=")) {
                paramStr = paramStr.substring(0, paramStr.length() - 1);
            }
            //解析请求参数
            Map map = JSON.parseObject(paramStr, Map.class);
            String mchtId = map.containsKey("mchtId") ? (String) map.get("mchtId") : "";
            String payType = map.containsKey("payType") ? (String) map.get("payType") : "";
            String orderId = map.containsKey("orderId") ? (String) map.get("orderId") : "";
            String orderTime = map.containsKey("orderTime") ? (String) map.get("orderTime") : "";
            String amount = map.containsKey("amount") ? (String) map.get("amount") : "";
            String currencyType = map.containsKey("currencyType") ? (String) map.get("currencyType") : "";
            String goods = map.containsKey("goods") ? (String) map.get("goods") : "";
            String notifyUrl = map.containsKey("notifyUrl") ? (String) map.get("notifyUrl") : "";
            String sign = map.containsKey("sign") ? (String) map.get("sign") : "";

            if (StringUtils.isBlank(mchtId)
                    || StringUtils.isBlank(payType)
                    || StringUtils.isBlank(orderId)
                    || StringUtils.isBlank(orderTime)
                    || StringUtils.isBlank(amount)
                    || StringUtils.isBlank(currencyType)
                    || StringUtils.isBlank(goods)
                    || StringUtils.isBlank(notifyUrl)
                    || StringUtils.isBlank(sign)) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("必填参数值不能为空");
                logger.info(BIZ_NAME + "必填请求参数值不能为空，即客户端请求参数：" + JSON.toJSONString(map));
                return checkResp;
            }

            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ_NAME + "参数异常e：" + e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }

    /**
     * 支付接口
     *
     * @param map
     * @param ip
     */
    @Override
    public Map pay(Map map, String ip) {
        Map rtnMap = new HashMap<>();
        String sign = "";
        try {
            String mchtId = (String) map.get("mchtId");
            String mchtOrderId = (String) map.get("orderId");
            String moid = mchtId + "-" + mchtOrderId;

            MchtInfo merchant = merchantService.queryByKey(mchtId);
            if (merchant == null) {//判断商户是否存在
                rtnMap.put("code", ErrorCodeEnum.E1113.getCode());
                rtnMap.put("msg", ErrorCodeEnum.E1113.getDesc());
                logger.info(BIZ_NAME + "判断商户是否存在,查询商户信息为null,mchtId:" + mchtId + ",返回客户端:" + JSON.toJSONString(rtnMap));
                return rtnMap;
            }

            if (!"1".equals(merchant.getStatus())) {//判断商户关停
                rtnMap.put("code", ErrorCodeEnum.E1002.getCode());
                rtnMap.put("msg", ErrorCodeEnum.E1002.getDesc());
                logger.info(BIZ_NAME + "判断商户关停,状态为关停,商户status:" + merchant.getStatus() + ",返回客户端:" + JSON.toJSONString(rtnMap));
                return rtnMap;
            }

            String mchtKey = merchant.getMchtKey();
            String mchtSignStr = (String) map.get("sign");
            map.remove("sign");//参数sign不参与签名
            String platSignStr = SignUtil.md5Sign(map, mchtKey, moid);// 签名
            logger.info(BIZ_NAME + "支付SDK下单接口验签,平台签名原始字符串: " + JSON.toJSONString(map));
            logger.info(BIZ_NAME + "支付SDK下单接口验签,平台签名结果字符串: " + platSignStr.toUpperCase());
            logger.info(BIZ_NAME + "支付SDK下单接口验签,商户签名参数字符串: " + mchtSignStr);
            if (!mchtSignStr.equalsIgnoreCase(platSignStr)) {//支付SDK下单接口验签
                rtnMap.put("code", ErrorCodeEnum.E1119.getCode());
                rtnMap.put("msg", ErrorCodeEnum.E1119.getDesc());
                logger.info(BIZ_NAME + "支付SDK下单接口验签,签名错误,返回客户端:" + JSON.toJSONString(rtnMap));
                return rtnMap;
            }

            TradeApiPayRequest tradeRequest = new TradeApiPayRequest();
            ApiPayRequestBody body = new ApiPayRequestBody();
            TradeReqHead head = new TradeReqHead();
            head.setBiz((String) map.get("payType"));
            head.setMchtId(mchtId);
            head.setVersion((String) map.get("version"));
            body.setOrderId(mchtOrderId);
            body.setOrderTime((String) map.get("orderTime"));
            body.setAmount((String) map.get("amount"));
            body.setCurrencyType((String) map.get("currencyType"));
            body.setGoods((String) map.get("goods"));
            body.setNotifyUrl((String) map.get("notifyUrl"));
            body.setCallBackUrl((String) map.get("callBackUrl"));
            body.setDesc((String) map.get("desc"));
            body.setAppId((String) map.get("appId"));
            body.setOperator((String) map.get("operator"));
            body.setExpireTime((String) map.get("expireTime"));
            body.setIp((String) map.get("ip"));
            sign = SignUtil.md5Sign(JSON.parseObject(JSON.toJSONString(body), Map.class), mchtKey, moid);// 签名
            tradeRequest.setHead(head);
            tradeRequest.setBody(body);
            tradeRequest.setSign(sign);

            logger.info(BIZ_NAME + "调用boss-trade创建订单，参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
            CommonResult commonResult = tradeApiPayHandler.process(tradeRequest, ip);
            logger.info(BIZ_NAME + "调用boss-trade创建订单，返回值commonResult：" + JSON.toJSONString(commonResult));

            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                Result mchtResult = (Result) commonResult.getData();
                rtnMap.put("code", ErrorCodeEnum.SUCCESS.getCode());
                rtnMap.put("msg", ErrorCodeEnum.SUCCESS.getDesc());
                rtnMap.put("mchtId", mchtResult.getMchtId());
                rtnMap.put("orderId", mchtResult.getMchtOrderNo());//商户订单号
                if(ClientPayWayEnum.SCAN_INSIDE.getCode().equals(mchtResult.getClientPayWay())) {
                    rtnMap.put("payInfo",this.geneQrcodeUrl(mchtResult.getQrCodeDomain(),mchtResult.getPayInfo()));
                }else if(ClientPayWayEnum.H5_FORM.getCode().equals(mchtResult.getClientPayWay()) ||
                        ClientPayWayEnum.CHAN_CASHIER_FORM.getCode().equals(mchtResult.getClientPayWay())){
                    rtnMap.put("payInfo",this.getFromUrl(mchtResult.getQrCodeDomain(),mchtResult.getPayInfo()));
                }else {
                    rtnMap.put("payInfo", mchtResult.getPayInfo());
                }

                rtnMap.put("payType", mchtResult.getPaymentType());
                rtnMap.put("tradeId", mchtResult.getOrderNo());//平台订单号
                sign = SignUtil.md5Sign(rtnMap, mchtKey, mchtId + "-" + mchtResult.getOrderNo());// 签名
                rtnMap.put("sign", sign);
            } else {
                rtnMap.put("code", commonResult.getRespCode());
                rtnMap.put("msg", commonResult.getRespMsg());
            }
        } catch (Exception e) {
            rtnMap.put("code", ErrorCodeEnum.E8001.getCode());
            rtnMap.put("msg", ErrorCodeEnum.E8001.getDesc());
            e.printStackTrace();
            logger.error(BIZ_NAME + "创建支付订单异常e:" + e.getMessage());
        }
        logger.info(BIZ_NAME + "返回gateway客户端:" + JSON.toJSONString(rtnMap));
        return rtnMap;
    }

    private String geneQrcodeUrl(String qrCodeDomain, String qrCodepayInfo) {
        String payInfo = "";
        try {
            String seq = IdUtil.getUUID();
            String url = "http://"+qrCodeDomain+"/qrCode/gen";
            Map<String, String> desData = new HashMap();
            desData.put("seq", seq);
            desData.put("qrCodepayInfo", qrCodepayInfo);
            String desResult = DesUtil32.encode(JSONObject.toJSONString(desData), "Zhrt2018");
            logger.info("生成二维码之前对数据进行des加密，密钥为：Zhrt2018"+"，加密后的值为："+desResult);
            payInfo = url + "?uuid=" + URLEncoder.encode(qrCodepayInfo, "UTF-8")+"&seq="+seq+"&data="+desResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("扫码支付，自己封装的二维码url："+payInfo);
        return payInfo;
    }

    private String getFromUrl(String qrCodeDomain, String payInfo) {
        try {

            String url = "http://"+qrCodeDomain+"/form/data";
            String desResult = DesUtil32.encode(payInfo, "Zhrt2018");
            logger.info("生成二维码之前对数据进行des加密，密钥为：Zhrt2018"+"，加密后的值为："+desResult);
            payInfo = url + "?data="+desResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("from表单，自己封装的url："+payInfo);
        return payInfo;
    }




}
