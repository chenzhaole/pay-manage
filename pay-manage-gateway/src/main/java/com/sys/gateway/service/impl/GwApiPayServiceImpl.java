package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.apipay.ApiPayRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiPayRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderCreateResponse;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.boss.api.service.trade.handler.ITradeApiQRPayHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.SignUtil;

import com.sys.gateway.service.GwApiPayService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description:通用支付业务处理实现类
 * @author: ChenZL
 * @time: 2017年11月28日
 */
@Service
public class GwApiPayServiceImpl implements GwApiPayService {

    protected final Logger logger = LoggerFactory.getLogger(GwApiPayService.class);

    @Autowired
    private ITradeApiPayHandler tradeApiPayHandler;

    @Autowired
    private ITradeApiQRPayHandler tradeApiQrPayHandler;


    /**
     * API订单校验参数
     **/
    @Override
    public CommonResponse checkParam(String paramStr) {
        CommonResponse checkResp = new CommonResponse();
        try {
            if (paramStr.endsWith("=")) {
                paramStr = paramStr.substring(0, paramStr.length() - 1);
            }
            //解析请求参数
            TradeApiPayRequest tradeRequest = JSON.parseObject(paramStr, TradeApiPayRequest.class);
            checkResp.setData(tradeRequest);

            if (tradeRequest.getHead() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[head]请求参数值不能为空");
                logger.error("[head]请求参数值不能为空，即TradeCommRequest=：" + JSONObject.toJSONString(tradeRequest));
                return checkResp;
            }
            tradeApiPayHandler.insertRedisRequestData(tradeRequest.getHead().getMchtId(), tradeRequest.getBody() == null ? "0" : tradeRequest.getBody().getAmount(), 1);

            if (tradeRequest.getBody() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[body]请求参数值不能为空");
                logger.error("[body]请求参数值不能为空，即TradeCommRequest=：" + JSONObject.toJSONString(tradeRequest));
                return checkResp;
            }

            if (tradeRequest.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[sign]请求参数值不能为空");
                logger.error("[sign]请求参数值不能为空，即TradeCommRequest=：" + JSONObject.toJSONString(tradeRequest));
                return checkResp;
            }

            TradeReqHead head = tradeRequest.getHead();
            if (head.getMchtId() == null || head.getVersion() == null || head.getBiz() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
                logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=：" + JSONObject.toJSONString(head));
                return checkResp;
            }

            ApiPayRequestBody body = tradeRequest.getBody();
            if (StringUtils.isBlank(body.getOrderId())
                    || StringUtils.isBlank(body.getAmount())
                    || StringUtils.isBlank(body.getGoods())
                    || StringUtils.isBlank(body.getNotifyUrl())
                    || StringUtils.isBlank(body.getOrderTime())) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空");
                logger.error("[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空，即CommRequestBody=：" + JSONObject.toJSONString(body));
                return checkResp;
            }

            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(tradeRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("API订单校验参数异常：" + e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }


    /**
     * API订单接口
     */
    @Override
    public ApiPayOrderCreateResponse pay(TradeBaseRequest tradeRequest, String ip) {

        ApiPayOrderCreateResponse.ApiPayOrderCreateResponseHead head = new ApiPayOrderCreateResponse.ApiPayOrderCreateResponseHead();
        ApiPayOrderCreateResponse.ApiPayOrderCreateResponseBody body = new ApiPayOrderCreateResponse.ApiPayOrderCreateResponseBody();
        String sign = "";
        try {
            logger.info("调用boss-trade创建API支付订单，参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
            CommonResult commonResult = tradeApiPayHandler.process(tradeRequest, ip);
            logger.info("调用boss-trade创建API支付订单，返回值commonResult：" + JSON.toJSONString(commonResult));
            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                Result mchtResult = (Result) commonResult.getData();
                head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
                body.setMchtId(mchtResult.getMchtId());
                body.setOrderId(mchtResult.getMchtOrderNo());//商户订单号

                if (tradeRequest.getHead() != null && PayTypeEnum.WX_PUBLIC_NATIVE.getCode().equals(tradeRequest.getHead().getBiz())
                        || tradeRequest.getHead() != null && PayTypeEnum.ALIPAY_SERVICE_WINDOW.getCode().equals(tradeRequest.getHead().getBiz())) {
                    //如果是原生微信公众号和支付宝服务窗,则设置payInfo值
                    body.setPayUrl("");
                    body.setPayInfo(mchtResult.getPayInfo());
                } else {
                    body.setPayUrl(mchtResult.getPayInfo());
                    body.setPayInfo("");
                }
                body.setTradeId(mchtResult.getOrderNo());//平台订单号
                // 签名
                Map<String, String> params = JSONObject.parseObject(
                        JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
                        });
                String log_moid = mchtResult.getMchtId() + "-->" + mchtResult.getMchtOrderNo();
                sign = SignUtil.md5Sign(params, mchtResult.getMchtKey(), log_moid);
            } else {
                String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode() : commonResult.getRespCode();
                String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc() : commonResult.getRespMsg();
                head.setRespCode(respCode);
                head.setRespMsg(respMsg);
            }
        } catch (Exception e) {
            head.setRespCode(ErrorCodeEnum.E8001.getCode());
            head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
            e.printStackTrace();
            logger.error("创建API支付订单订单异常 e=" + e.getMessage());
        }
        ApiPayOrderCreateResponse apiOrderCreateResponse = new ApiPayOrderCreateResponse(head, body, sign);
        logger.info("返回gateway客户端CommOrderCreateResponse=" + JSON.toJSONString(apiOrderCreateResponse));
        return apiOrderCreateResponse;
    }

    @Override
    public CommonResult qrPay(String platOrderNo, String ip) {
        TradeApiPayRequest request = new TradeApiPayRequest();
        ApiPayRequestBody body = new ApiPayRequestBody();
        body.setOrderId(platOrderNo);
        request.setBody(body);
        CommonResult commonResult = tradeApiQrPayHandler.processAuth(request, ip);

        return commonResult;
    }


}
