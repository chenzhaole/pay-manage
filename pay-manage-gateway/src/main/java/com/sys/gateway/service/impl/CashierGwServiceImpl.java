package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.cashier.CashierRequestBody;
import com.sys.boss.api.entry.trade.request.cashier.TradeCashierRequest;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.service.CashierGwService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CashierGwServiceImpl implements CashierGwService{
    private Logger logger = LoggerFactory.getLogger(CashierGwServiceImpl.class);

    @Override
    public CommonResult checkParam(HttpServletRequest request) {
        CommonResult checkResp = new CommonResult();
        try {

            //解析请求参数
            TradeCashierRequest tradeRequest = buildFromRequest(request);

            logger.info("请求收银台 接收参数："+ JSON.toJSONString(tradeRequest));

            if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
                logger.error("[head],[body],[sign]请求参数值不能为空，即TradeWapRequest=："+ JSONObject.toJSONString(tradeRequest));
                return checkResp;
            }

            TradeReqHead head = tradeRequest.getHead();
            if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
                logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+ JSONObject.toJSONString(head));
                return checkResp;
            }

            CashierRequestBody body = tradeRequest.getBody();
            if (StringUtils.isBlank(body.getOrderId())
                    || StringUtils.isBlank(body.getAmount())
                    || StringUtils.isBlank(body.getGoods())
                    || StringUtils.isBlank(body.getNotifyUrl())
                    || StringUtils.isBlank(body.getOrderTime())) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空");
                logger.error("[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空，即WapRequestBody=："+ JSONObject.toJSONString(body));
                return checkResp;
            }

            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(tradeRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("收银台校验参数异常："+e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }

    private TradeCashierRequest buildFromRequest(HttpServletRequest request){
        TradeCashierRequest cashierRequest = new TradeCashierRequest();
        TradeReqHead head = new TradeReqHead();
        head.setBiz(request.getParameter("biz"));
        head.setMchtId(request.getParameter("mchtId"));
        head.setVersion(request.getParameter("version"));

        CashierRequestBody cashierRequestBody = new CashierRequestBody();
        cashierRequestBody.setAmount(request.getParameter("amount"));
        cashierRequestBody.setDesc(request.getParameter("desc"));
        cashierRequestBody.setGoods(request.getParameter("goods"));
        cashierRequestBody.setNotifyUrl(request.getParameter("notifyUrl"));
        cashierRequestBody.setOrderId(request.getParameter("orderId"));
        cashierRequestBody.setOperator(request.getParameter("operator"));
        cashierRequestBody.setOrderTime(request.getParameter("orderTime"));

        cashierRequest.setBody(cashierRequestBody);
        cashierRequest.setHead(head);
        cashierRequest.setSign(request.getParameter("sign"));
        return cashierRequest;
    }
}
