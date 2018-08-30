package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.apipay.ApiQueryRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.boss.api.service.trade.handler.ITradeApiQueryHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MerchantService;
import com.sys.gateway.service.GwSdkQueryService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GwSdkQueryServiceImpl implements GwSdkQueryService {

    protected final Logger logger = LoggerFactory.getLogger(GwSdkQueryServiceImpl.class);
    private final String BIZ_NAME = "支付SDK-查询支付订单-";

    @Autowired
    private ITradeApiQueryHandler tradeApiQueryHandler;

    @Autowired
    private MerchantService merchantService;

    /**
     * 校验参数
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
            String orderId = map.containsKey("orderId") ? (String) map.get("orderId") : "";
            String sign = map.containsKey("sign") ? (String) map.get("sign") : "";

            if (StringUtils.isBlank(mchtId)
                    || StringUtils.isBlank(orderId)
                    || StringUtils.isBlank(sign)) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("必填参数值不能为空");
                logger.info("必填请求参数值不能为空，即客户端请求参数：" + JSON.toJSONString(map));
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
     * 查询支付订单
     */
    @Override
    public Map query(Map map, String ip) {
        Map rtnMap = new HashMap<>();
        String sign = "";
        try {
            String mchtId = (String) map.get("mchtId");
            String mchtOrderId = (String) map.get("mchtId");
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
            String platSignStr = SignUtil.md5Sign(map, mchtKey, mchtId);// 签名
            logger.info(BIZ_NAME + "平台签名原始字符串: " + JSON.toJSONString(map));
            logger.info(BIZ_NAME + "平台签名结果字符串: " + platSignStr.toUpperCase());
            logger.info(BIZ_NAME + "商户签名参数字符串: " + mchtSignStr);
            if (!mchtSignStr.equalsIgnoreCase(platSignStr)) {//支付SDK下单接口验签
                rtnMap.put("code", ErrorCodeEnum.E1119.getCode());
                rtnMap.put("msg", ErrorCodeEnum.E1119.getDesc());
                logger.info(BIZ_NAME + "签名错误,返回客户端:" + JSON.toJSONString(rtnMap));
                return rtnMap;
            }


            TradeApiQueryRequest queryRequest = new TradeApiQueryRequest();
            ApiQueryRequestBody body = new ApiQueryRequestBody();
            TradeReqHead head = new TradeReqHead();
            head.setBiz("");
            head.setMchtId(mchtId);
            head.setVersion("20");
            body.setOrderId((String) map.get("orderId"));
            body.setOrderTime((String) map.get("orderTime"));
            body.setTradeId((String) map.get("tradeId"));
            sign = SignUtil.md5Sign(JSON.parseObject(JSON.toJSONString(body), Map.class), mchtKey, moid);// 签名
            queryRequest.setHead(head);
            queryRequest.setBody(body);
            queryRequest.setSign(sign);

            logger.info(BIZ_NAME + "调用boss-trade查询订单，参数值queryRequest：" + JSON.toJSONString(queryRequest));
            CommonResult commonResult = tradeApiQueryHandler.process(queryRequest, ip);
            logger.info(BIZ_NAME + "调用boss-trade查询订单，返回值commonResult：" + JSON.toJSONString(commonResult));

            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                Map tradeData = (Map) commonResult.getData();
                rtnMap  = JSON.parseObject ((String)tradeData.get("data"),Map.class);

                logger.info(BIZ_NAME + "返回值sdk客户端原始tradeData-1---：" + JSON.toJSONString(tradeData));
                logger.info(BIZ_NAME + "返回值sdk客户端原始tradeData-2---：" + JSON.toJSONString(rtnMap));

                rtnMap.put("code", ErrorCodeEnum.SUCCESS.getCode());
                rtnMap.put("msg", ErrorCodeEnum.SUCCESS.getDesc());
//                rtnMap.put("mchtId", (String)tradeData.get("mchtId"));
//                rtnMap.put("orderId", (String)tradeData.get("orderId"));//商户订单号
//                rtnMap.put("payType", map.get("payType"));
//                rtnMap.put("amount", (String)tradeData.get("amount"));
//                rtnMap.put("tradeId", (String)tradeData.get("tradeId"));//平台订单号
//                rtnMap.put("status", (String)tradeData.get("status"));
//                rtnMap.put("chargeTime" rtnMap = tradeData;
                logger.info(BIZ_NAME + "返回值sdk客户端原始rtnMap：" + JSON.toJSONString(rtnMap));
                sign = SignUtil.md5Sign(rtnMap, merchant.getMchtKey(), moid);// 签名
                rtnMap.put("sign", sign);
            } else {
                rtnMap.put("code", ErrorCodeEnum.FAILURE.getCode());
                rtnMap.put("msg", ErrorCodeEnum.FAILURE.getDesc());
            }
        } catch (Exception e) {
            rtnMap.put("code", ErrorCodeEnum.E8001.getCode());
            rtnMap.put("msg", ErrorCodeEnum.E8001.getDesc());
            e.printStackTrace();
            logger.error(BIZ_NAME + "查询支付订单异常e:" + e.getMessage());
        }
        logger.info(BIZ_NAME + "返回gateway客户端:" + JSON.toJSONString(rtnMap));
        return rtnMap;
    }


}
