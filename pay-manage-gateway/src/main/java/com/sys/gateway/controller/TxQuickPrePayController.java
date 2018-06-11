package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickPrePayRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickPrePayRequestBody;
import com.sys.boss.api.entry.trade.response.quickpay.TXQuickPrePayResponse;
import com.sys.boss.api.service.trade.handler.ITradeTxQuickPrePayHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.SignUtil;
import com.sys.gateway.common.IpUtil;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Map;

/**
 * TX快捷预消费
 * Created by chenzhaole on 2018/1/20.
 */
@Controller
@RequestMapping(value = "")
public class TxQuickPrePayController {
    protected final Logger logger = LoggerFactory.getLogger(TxQuickPrePayController.class);

    @Autowired
    ITradeTxQuickPrePayHandler iTradeTxQuickPrePayHandler;

    private final String BIZ = "TX快捷预消费-";

    @RequestMapping(value = "/gateway/txQuickPrePay")
    @ResponseBody
    public String txQuickPrePay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        TXQuickPrePayResponse prePayResp = new TXQuickPrePayResponse();
        TXQuickPrePayResponse.TXQuickPrePayResponseHead head = new TXQuickPrePayResponse.TXQuickPrePayResponseHead();
        String midoid = "";//商户ID+商户订单ID

        try {

            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info(BIZ + midoid + " 获取到客户端请求ip为：" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info(BIZ + midoid + " 收到客户端请求参数后做url解码后的data：" + data);

            if (data.endsWith("=")) {
                data = data.substring(0, data.length() - 1);
            }

            //解析请求参数
            TXQuickPrePayRequest mchtRequest = JSON.parseObject(data, TXQuickPrePayRequest.class);
            midoid = mchtRequest.getHead().getMchtId() + "-" + mchtRequest.getBody().getOrderId();

            //校验请求参数
            CommonResponse checkResp = checkRequestParam(mchtRequest, midoid);
            logger.info(BIZ + midoid + " 校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));

            if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                logger.info(BIZ + midoid + " 校验请求参数结果失败");
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                prePayResp.setHead(head);
            } else {
                logger.info(BIZ + midoid + " 校验请求参数结果成功，调用Boss-Trade快捷预消费接口");
                TXQuickPrePayRequest tradeRequest = (TXQuickPrePayRequest) checkResp.getData();
                logger.info(BIZ + midoid + " 调用Boss-Trade快捷预消费接口，传入的TradeQuickPrePayRequest信息：" + JSONObject.toJSONString(tradeRequest));
                prePayResp = callHandler(tradeRequest, ip, midoid);
                logger.info(BIZ + midoid + " 调用Boss-Trade快捷预消费接口，返回的QuickPrePayOrderCreateResponse信息：" + JSONObject.toJSONString(prePayResp));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ + midoid + " 系统异常，e.msg：" + e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("快捷预消费网关错误");
        }
        logger.info(BIZ + midoid + " 返回下游商户信息：" + JSON.toJSONString(prePayResp));
        return JSON.toJSONString(prePayResp);
    }

    /**
     * 校验参数
     **/
    public CommonResponse checkRequestParam(TXQuickPrePayRequest mchtRequest, String midoid) {
        CommonResponse checkResp = new CommonResponse();
        try {
            //解析请求参数
            if (mchtRequest.getHead() == null || mchtRequest.getBody() == null || mchtRequest.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("[head],[body],[sign]必填参数值不能为空");
                logger.info(BIZ + midoid + " [head],[body],[sign]必填参数为空，即TXQuickPrePayRequest：" + JSONObject.toJSONString(mchtRequest));
                return checkResp;
            }

            TradeReqHead head = mchtRequest.getHead();
            if (head.getMchtId() == null || head.getVersion() == null || head.getBiz() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("[mchtId],[version],[biz]必填参数值不能为空");
                logger.info(BIZ + midoid + " [mchtId],[version],[biz]必填参数为空，即TXQuickPrePayRequest：" + JSONObject.toJSONString(head));
                return checkResp;
            }

            TXQuickPrePayRequestBody body = mchtRequest.getBody();
            if (StringUtils.isBlank(body.getOrderId())
                    || StringUtils.isBlank(body.getGoods())
                    || StringUtils.isBlank(body.getAccountName())
                    || StringUtils.isBlank(body.getCertType())
                    || StringUtils.isBlank(body.getCertificateNo())
                    || StringUtils.isBlank(body.getCardType())
                    || StringUtils.isBlank(body.getBankCardNo())
                    || StringUtils.isBlank(body.getBankCode())
                    || StringUtils.isBlank(body.getMobilePhone())
                    || StringUtils.isBlank(body.getAmount())
                    || StringUtils.isBlank(body.getOrderTime())
                    || StringUtils.isBlank(body.getNotifyUrl())
                    || StringUtils.isBlank(body.getUserId())
                    || StringUtils.isBlank(body.getTxType())) {

                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("必填参数值不能为空");
                logger.info(BIZ + midoid + "[orderId],[goods],[accountName],[certType],[certificateNo],[cardType],[bankCardNo],[mobilePhone],[amount],[orderTime],[notifyUrl],[userId],[txType]请求参数值不能为空，即TXQuickPrePayRequestBody：" + JSONObject.toJSONString(body));
                return checkResp;
            }

            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(mchtRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ + midoid + " 校验请求参数系统异常，e.msg：" + e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }

    /**
     * 调用商户快捷预消费接口
     */
    public TXQuickPrePayResponse callHandler(TradeBaseRequest tradeRequest, String ip, String midoid) {

        TXQuickPrePayResponse.TXQuickPrePayResponseHead head = new TXQuickPrePayResponse.TXQuickPrePayResponseHead();
        TXQuickPrePayResponse.TXQuickPrePayResponseBody body = new TXQuickPrePayResponse.TXQuickPrePayResponseBody();
        String sign = "";
        String METHOD = "调用Boss-Trade快捷预消费Handler接口";
        try {
            logger.info(BIZ + midoid + METHOD + " 参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
            CommonResult commonResult = (CommonResult) iTradeTxQuickPrePayHandler.process(tradeRequest, ip);
            logger.info(BIZ + midoid + METHOD + " 返回值commonResult：" + JSON.toJSONString(commonResult));
            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                Result mchtResult = (Result) commonResult.getData();
                head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
                body.setMchtId(mchtResult.getMchtId());
                body.setOrderId(mchtResult.getMchtOrderNo());//商户订单号
                body.setTradeId(mchtResult.getOrderNo());//我司订单号
                body.setAmount(mchtResult.getOrderAmount());//金额
                body.setTradeTime(mchtResult.getPayTime());
                body.setStatus("SUCCESS");
                body.setUserId(mchtResult.getUserId());
                body.setPayUrl(mchtResult.getPayInfo());
                // 交易类型；1:短信，2:网页 qrCode字段不为空，表示是网页形式
                /*if(StringUtils.isNotBlank(mchtResult.getQrCode())){
                    body.setTrType("2");
                    body.setPayUrl(mchtResult.getQrCode());
                }else{*/
                body.setTrType("2");
//                }
                // 签名
                Map<String, String> params = JSONObject.parseObject(
                        JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
                        });
                sign = SignUtil.md5Sign(params, mchtResult.getMchtKey(), "");
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
            logger.error(BIZ + midoid + METHOD + " 系统异常 e.msg：" + e.getMessage());
        }
        TXQuickPrePayResponse prePayResponse = new TXQuickPrePayResponse(head, body, sign);
        logger.info(BIZ + midoid + METHOD + " 返回客户端MchtRegisteResponse：" + JSON.toJSONString(prePayResponse));
        return prePayResponse;
    }
}
