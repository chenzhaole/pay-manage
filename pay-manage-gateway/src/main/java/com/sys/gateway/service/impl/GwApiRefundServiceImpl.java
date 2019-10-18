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
import com.sys.boss.api.entry.trade.request.refund.RefundRequestBody;
import com.sys.boss.api.entry.trade.request.refund.TradeRefundRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.refund.RefundResponse;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.boss.api.service.trade.handler.ITradeApiQRPayHandler;
import com.sys.boss.api.service.trade.handler.ITradeRefundCreateHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.IdUtil;
import com.sys.common.util.SignUtil;
import com.sys.gateway.service.GwApiPayService;
import com.sys.gateway.service.GwApiRefundService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description:退款业务处理实现类
 * @author: ChenZL
 * @time: 2019年10月18日
 */
@Service
public class GwApiRefundServiceImpl implements GwApiRefundService {

    protected final Logger logger = LoggerFactory.getLogger(GwApiPayService.class);

    @Autowired
    private ITradeRefundCreateHandler tradeRefundCreateHandler;


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
            TradeRefundRequest tradeRequest = JSON.parseObject(paramStr, TradeRefundRequest.class);
            checkResp.setData(tradeRequest);

            if (tradeRequest.getHead() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[head]请求参数值不能为空");
                logger.error("[head]请求参数值不能为空，即TradeCommRequest=：" + JSONObject.toJSONString(tradeRequest));
                return checkResp;
            }

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

            RefundRequestBody body = tradeRequest.getBody();
            if (StringUtils.isBlank(body.getOrderId())
                    || StringUtils.isBlank(body.getAmount())
                    || StringUtils.isBlank(body.getOriTradeId())
                    || StringUtils.isBlank(body.getOriOrderTime())) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[orderId],[oriOrderTime],[amount],[oriTradeId]必填参数值不能为空");
                logger.error("[orderId],[oriOrderTime],[amount],[oriTradeId]必填参数值不能为空，即CommRequestBody=：" + JSONObject.toJSONString(body));
                return checkResp;
            }

            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(tradeRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("退单单校验参数异常：" + e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }


    /**
     * API订单接口
     */
    @Override
    public RefundResponse refund(TradeBaseRequest tradeRequest, String ip) {

        RefundResponse.RefundResponseHead head = new RefundResponse.RefundResponseHead();
        RefundResponse.RefundResponseBody body = new RefundResponse.RefundResponseBody();
        String sign = "";
        try {
            logger.info("调用boss-trade创建退款订单，参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
            CommonResult commonResult = tradeRefundCreateHandler.process(tradeRequest, ip);
            logger.info("调用boss-trade创建退款订单，返回值commonResult：" + JSON.toJSONString(commonResult));
            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                Result result = (Result) commonResult.getData();
                head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
                body.setMchtId(result.getMchtId());
                body.setOrderId(result.getMchtOrderNo());//商户订单号
                body.setTradeId(result.getOrderNo());//平台订单号
                body.setOriTradeId(result.getOriOrderNo());
                body.setStatus(result.getStatus());
                body.setSeq(IdUtil.getUUID());

                // 签名
                Map<String, String> params = JSONObject.parseObject(
                        JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
                        });
                String moid = result.getMchtId() + "-" + result.getMchtOrderNo();
                sign = SignUtil.md5Sign(params, result.getMchtKey(), moid);
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
            logger.error("创建退款订单订单异常 e:" + e.getMessage());
        }
        RefundResponse refundResponse = new RefundResponse(head, body, sign);
        logger.info("返回gateway客户端RefundResponse:" + JSON.toJSONString(refundResponse));
        return refundResponse;
    }


}
