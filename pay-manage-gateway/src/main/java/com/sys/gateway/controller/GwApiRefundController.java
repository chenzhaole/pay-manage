package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;

import com.sys.boss.api.entry.trade.request.refund.TradeRefundRequest;
import com.sys.boss.api.entry.trade.response.refund.RefundResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwApiRefundService;
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

/**
 * 退单接口
 */
@Controller
@RequestMapping(value = "")
public class GwApiRefundController {

    protected final Logger logger = LoggerFactory.getLogger(GwApiRefundController.class);

    @Autowired
    GwApiRefundService gwApiRefundService;


    /**
     * api退单
     */
    @RequestMapping(value = "/gateway/api/refundPay")
    @ResponseBody
    public String refundPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        RefundResponse refundResponse = new RefundResponse();
        RefundResponse.RefundResponseHead head = new RefundResponse.RefundResponseHead();

        try {
            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info("refundPay退单获取到客户端请求ip：" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("refundPay退单收到客户端请求参数后做url解码后的值为：" + data);
            //校验请求参数
            CommonResponse checkResp = gwApiRefundService.checkParam(data);
            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            logger.info("refundPay退单校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));
            if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                refundResponse.setHead(head);
            } else {
                //refundPay退单接口
                TradeRefundRequest tradeRequest = (TradeRefundRequest) checkResp.getData();
                logger.info("调用refundPay退单接口，传入的TradeCommRequest信息：" + JSONObject.toJSONString(tradeRequest));
                refundResponse = (RefundResponse) gwApiRefundService.refund(tradeRequest, ip);
                logger.info("调用refundPay退单接口，返回的CommOrderQueryResponse信息：" + JSONObject.toJSONString(refundResponse));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("refundPay退单接口抛异常" + e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("支付网关错误：" + e.getMessage());
        }
        logger.info("refundPay退单接口，返回下游商户值：" + JSON.toJSONString(refundResponse));
        return JSON.toJSONString(refundResponse);
    }


}
