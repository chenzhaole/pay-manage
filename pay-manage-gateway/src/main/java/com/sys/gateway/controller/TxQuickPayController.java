package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickCommPayRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickCommPayRequestBody;
import com.sys.boss.api.entry.trade.response.quickpay.TXQuickCommPayResponse;
import com.sys.boss.api.service.trade.handler.ITradeTxQuickPayHandler;
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
 * TX快捷提交支付
 * Created by chenzhaole on 2018/1/20.
 */
@Controller
@RequestMapping(value = "")
public class TxQuickPayController {
    protected final Logger logger = LoggerFactory.getLogger(TxQuickPrePayController.class);

    @Autowired
    ITradeTxQuickPayHandler iTradeTxQuickPayHandler;

    @RequestMapping(value="/gateway/txQuickCommPay")
    @ResponseBody
    public String txQuickCommPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes)throws java.io.IOException {
        TXQuickCommPayResponse validPayResp = new TXQuickCommPayResponse();
        TXQuickCommPayResponse.TXQuickCommPayResponseHead head = new TXQuickCommPayResponse.TXQuickCommPayResponseHead();
        try {
            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info("一户一报商户快捷支付获取到客户端请求ip为：ip="+ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("一户一报商户快捷支付收到客户端请求参数后做url解码后的值为：data="+data);
            //校验请求参数
            CommonResponse checkResp = checkWapParam(data);
            logger.info("一户一报商户快捷支付校验请求参数的结果为："+ JSONObject.toJSONString(checkResp));
            if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                validPayResp.setHead(head);
            }else{
                //掉一户一报商户快捷支付支付接口
                TXQuickCommPayRequest tradeRequest = (TXQuickCommPayRequest) checkResp.getData();
                logger.info("掉一户一报商户快捷支付接口，传入的TXQuickCommPayRequest信息为：TXQuickCommPayRequest="+ JSONObject.toJSONString(tradeRequest));
                validPayResp = callHandler(tradeRequest, ip);
                logger.info("掉一户一报商户快捷支付接口，返回的TXQuickCommPayResponse信息为：TXQuickCommPayResponse="+JSONObject.toJSONString(validPayResp));
                //TODO 返回信息体
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("掉一户一报商户快捷支付接口抛异常"+e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("掉一户一报商户快捷支付网关错误："+e.getMessage());
        }
        logger.info("创建掉一户一报商户快捷支付订单，返回下游商户值："+ JSON.toJSONString(validPayResp));
        return JSON.toJSONString(validPayResp);
    }

    /** 校验参数 **/
    public CommonResponse checkWapParam(String paramStr) {
        CommonResponse checkResp = new CommonResponse();
        try {
            if(paramStr.endsWith("=")){
                paramStr = paramStr.substring(0,paramStr.length()-1);
            }
            //解析请求参数
            TXQuickCommPayRequest tradeRequest = JSON.parseObject(paramStr, TXQuickCommPayRequest.class);
            if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("一户一报商户快捷支付[head],[body],[sign]请求参数值不能为空");
                logger.error("一户一报商户快捷支付[head],[body],[sign]请求参数值不能为空，即TradeQuickPrePayRequest=："+ JSONObject.toJSONString(tradeRequest));
                return checkResp;
            }

            TradeReqHead head = tradeRequest.getHead();
            if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("一户一报商户快捷支付[mchtId],[version],[biz]请求参数值不能为空");
                logger.error("一户一报商户快捷支付[mchtId],[version],[biz]请求参数值不能为空，即TradeMchtRegisteRequest=："+ JSONObject.toJSONString(head));
                return checkResp;
            }

            TXQuickCommPayRequestBody body = tradeRequest.getBody();
            if (StringUtils.isBlank(body.getTradeId())
                    || StringUtils.isBlank(body.getSmsCode())
                    || StringUtils.isBlank(body.getAccountName())
                    || StringUtils.isBlank(body.getBankCardNo())
                    || StringUtils.isBlank(body.getAmount())
                    || StringUtils.isBlank(body.getUserId())
                    || StringUtils.isBlank(body.getNotifyUrl())) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("[tradeId],[smsCode],[accountName],[bankCardNo],[userId],[notifyUrl]请求参数值不能为空");
                logger.error("[tradeId],[smsCode],[accountName],[bankCardNo],[userId],[notifyUrl]请求参数值不能为空，即TXQuickCommPayRequestBody=："+ JSONObject.toJSONString(body));
                return checkResp;
            }

            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(tradeRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("一户一报商户快捷支付校验参数异常："+e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }
    /** 调用商户快捷支付接口 */
    public TXQuickCommPayResponse callHandler(TradeBaseRequest tradeRequest, String ip) {

        TXQuickCommPayResponse.TXQuickCommPayResponseHead head = new TXQuickCommPayResponse.TXQuickCommPayResponseHead();
        TXQuickCommPayResponse.TXQuickCommPayResponseBody body = new TXQuickCommPayResponse.TXQuickCommPayResponseBody();
        String sign  ="";
        try {
            logger.info("调用boss-trade创建一户一报商户快捷支付订单，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
            CommonResult commonResult = (CommonResult) iTradeTxQuickPayHandler.process(tradeRequest, ip);
            logger.info("调用boss-trade创建一户一报商户快捷支付订单，返回值commonResult：" + JSON.toJSONString(commonResult));
            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode()) || ErrorCodeEnum.E8003.getCode().equals(commonResult.getRespCode())) {
                Result mchtResult = (Result) commonResult.getData();
                head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
                body.setMchtId(mchtResult.getMchtId());
				body.setOrderId(mchtResult.getMchtOrderNo());//商户订单号
				body.setTradeId(mchtResult.getOrderNo());//我司订单号
                body.setAmount(mchtResult.getOrderAmount());
                body.setTradeTime(mchtResult.getPayTime());
                body.setBankCardNo(mchtResult.getBankCardNo());
                if(Result.STATUS_SUCCESS.equals(mchtResult.getStatus())){
                    body.setStatus("SUCCESS");
                }else{
                    body.setStatus("FAILURE");
                }
                // 签名
                Map<String, String> params =  JSONObject.parseObject(
                        JSON.toJSONString(body), new TypeReference<Map<String, String>>(){});
                sign = SignUtil.md5Sign(params, mchtResult.getMchtKey());
            }else{
                String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
                String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
                head.setRespCode(respCode);
                head.setRespMsg(respMsg);
            }
        } catch (Exception e) {
            head.setRespCode(ErrorCodeEnum.E8001.getCode());
            head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
            e.printStackTrace();
            logger.error("一户一报商户快捷支付创建订单异常 e=" + e.getMessage());
        }
        TXQuickCommPayResponse validPayResponse = new TXQuickCommPayResponse(head, body, sign);
        logger.info("一户一报商户快捷支付返回gateway客户端TXQuickCommPayResponse="+JSON.toJSONString(validPayResponse));
        return validPayResponse;
    }
}
