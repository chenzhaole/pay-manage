package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.cashier.CashierRequestBody;
import com.sys.boss.api.entry.trade.request.cashier.TradeCashierRequest;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.service.GwCashierMchtService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class GwCashierMchtServiceImpl implements GwCashierMchtService {
    private Logger logger = LoggerFactory.getLogger(GwCashierMchtServiceImpl.class);

    private static final String BIZ = "网页支付GwCashierServiceImpl->";

    /**
     * 解析并校验请求参数
     * @param request
     * @return
     */
    @Override
    public CommonResult resolveAndcheckParam(HttpServletRequest request) {
        CommonResult checkResp = new CommonResult();
        try {
            //解析商户请求参数
            TradeCashierRequest tradeRequestInfo = this.buildFromRequest(request);
            if(null == tradeRequestInfo){
                checkResp.setRespCode(ErrorCodeEnum.E1017.getCode());
                checkResp.setRespMsg(ErrorCodeEnum.E1017.getDesc());
                logger.info(BIZ + "接收的商户请求参数为空");
                return checkResp;
            }
            logger.info(BIZ + "解析的商户请求参数为："+ JSON.toJSONString(tradeRequestInfo));

            if (tradeRequestInfo.getHead() == null || tradeRequestInfo.getBody() == null || tradeRequestInfo.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg(ErrorCodeEnum.E1003.getDesc());
                logger.error(BIZ + "[head],[body],[sign]请求参数值不能为空，即TradeCashierRequest=："+ JSONObject.toJSONString(tradeRequestInfo));
                return checkResp;
            }

            TradeReqHead head = tradeRequestInfo.getHead();
            if (StringUtils.isBlank(head.getMchtId()) || StringUtils.isBlank(head.getVersion()) || StringUtils.isBlank(head.getBiz())) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg(ErrorCodeEnum.E1003.getDesc());
                logger.error(BIZ + "[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+ JSONObject.toJSONString(head));
                return checkResp;
            }

            CashierRequestBody body = tradeRequestInfo.getBody();
            if (StringUtils.isBlank(body.getOrderId())
                    || StringUtils.isBlank(body.getOrderTime())
                    || StringUtils.isBlank(body.getAmount())
                    || StringUtils.isBlank(body.getGoods())
                    || StringUtils.isBlank(body.getNotifyUrl())
                    ) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg(ErrorCodeEnum.E1003.getDesc());
                logger.error(BIZ + "[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空，即WapRequestBody=："+ JSONObject.toJSONString(body));
                return checkResp;
            }

            //校验签名


            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(tradeRequestInfo);
            logger.error(BIZ + "解析并校验商户请求参数通过，返回给上层的参数为："+ JSONObject.toJSONString(checkResp));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ +"解析并校验商户请求参数抛异常："+e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1016.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1016.getDesc());
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
        cashierRequestBody.setOrderId(request.getParameter("orderId"));
        cashierRequestBody.setOrderTime(request.getParameter("orderTime"));
        cashierRequestBody.setAmount(request.getParameter("amount"));
        cashierRequestBody.setGoods(request.getParameter("goods"));
        cashierRequestBody.setNotifyUrl(request.getParameter("notifyUrl"));
        cashierRequestBody.setCallBackUrl(request.getParameter("callBackUrl"));
        cashierRequestBody.setDesc(request.getParameter("desc"));
        cashierRequestBody.setAppId(request.getParameter("appId"));
        cashierRequestBody.setAppName(request.getParameter("appName"));
        cashierRequestBody.setOperator(request.getParameter("operator"));
        cashierRequestBody.setExpireTime(request.getParameter("expireTime"));
        cashierRequestBody.setDeviceType(request.getParameter("deviceType"));
        cashierRequestBody.setOpenId(request.getParameter("openId"));
        cashierRequestBody.setIp(request.getParameter("ip"));

        cashierRequest.setBody(cashierRequestBody);
        cashierRequest.setHead(head);
        cashierRequest.setSign(request.getParameter("sign"));
        logger.error(BIZ + "解析的商户请求参数为："+ JSONObject.toJSONString(cashierRequest));
        return cashierRequest;
    }
}
