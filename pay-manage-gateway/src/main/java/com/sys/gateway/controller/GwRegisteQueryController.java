package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.registe.MchtRegisteRequestBody;
import com.sys.boss.api.entry.trade.request.registe.TradeMchtRegisteRequest;
import com.sys.boss.api.entry.trade.response.registe.MchtRegisteResponse;
import com.sys.boss.api.service.trade.handler.ITradeRegisteQueryHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
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
 * 商户入驻查询
 */

@Controller
@RequestMapping(value = "")
public class GwRegisteQueryController {

    protected final Logger logger = LoggerFactory.getLogger(GwRegisteQueryController.class);

    @Autowired
    ITradeRegisteQueryHandler tradeRegisteQueryHandler;

    private final String BIZ = PayTypeEnum.QUICK_REGISTER.getDesc() + "-";


    @RequestMapping(value = "/gateway/api/registeQuery")
    @ResponseBody
    public String mchtRegiste(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        MchtRegisteResponse registeResp = new MchtRegisteResponse();
        MchtRegisteResponse.MchtRegisteResponseHead head = new MchtRegisteResponse.MchtRegisteResponseHead();

        String midoid = "";//商户ID+商户订单ID
        try {
            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info(BIZ + midoid + " 获取到客户端请求ip：" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info(BIZ + midoid + " 收到客户端请求参数后做url解码后的值为data：" + data);

            if (data.endsWith("=")) {
                data = data.substring(0, data.length() - 1);
            }
            //解析请求参数
            TradeMchtRegisteRequest mchtRequest = JSON.parseObject(data, TradeMchtRegisteRequest.class);
            midoid = mchtRequest.getHead().getMchtId() + "-" + mchtRequest.getBody().getOrderId();

            //校验请求参数
            CommonResponse checkResp = checkRequestParam(mchtRequest, midoid);
            logger.info(BIZ + midoid + " 校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));

            if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                logger.info(BIZ + midoid + " 校验请求参数结果失败");
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                registeResp.setHead(head);
            } else {
                logger.info(BIZ + midoid + " 校验请求参数结果成功，调用Boss-Trade商户入驻接口");
                TradeMchtRegisteRequest tradeRequest = (TradeMchtRegisteRequest) checkResp.getData();
                logger.info(BIZ + midoid + " 调用Boss-Trade商户入驻接口，传入的TradeMchtRegisteRequest信息：" + JSONObject.toJSONString(tradeRequest));
                registeResp = callHandler(tradeRequest, ip, midoid);
                logger.info(BIZ + midoid + " 调用Boss-Trade商户入驻接口，返回的MchtRegisteResponse信息：" + JSONObject.toJSONString(registeResp));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ + midoid + " 系统异常，e.msg：" + e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("商户入驻网关错误");
        }
        logger.info(BIZ + midoid + " 返回下游商户信息：" + JSON.toJSONString(registeResp));
        return JSON.toJSONString(registeResp);
    }

    /**
     * 校验参数
     **/
    public CommonResponse checkRequestParam(TradeMchtRegisteRequest mchtRequest, String midoid) {
        CommonResponse checkResp = new CommonResponse();
        try {

            if (mchtRequest.getHead() == null || mchtRequest.getBody() == null || mchtRequest.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("[head],[body],[sign]必填参数值不能为空");
                logger.info(BIZ + midoid + " [head],[body],[sign]必填参数为空，即TradeMchtRegisteRequest：" + JSONObject.toJSONString(mchtRequest));
                return checkResp;
            }

            TradeReqHead head = mchtRequest.getHead();
            if (head.getMchtId() == null || head.getVersion() == null || head.getBiz() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("[mchtId],[version],[biz]必填参数值不能为空");
                logger.info(BIZ + midoid + " [mchtId],[version],[biz]必填参数为空，即TradeMchtRegisteRequest：" + JSONObject.toJSONString(head));
                return checkResp;
            }

            MchtRegisteRequestBody body = mchtRequest.getBody();
            if (StringUtils.isBlank(body.getOrderId())
                    || StringUtils.isBlank(body.getName())
                    || StringUtils.isBlank(body.getNickName())
                    || StringUtils.isBlank(body.getAddress())
                    || StringUtils.isBlank(body.getMchtType())
                    || StringUtils.isBlank(body.getLegalName())
                    || StringUtils.isBlank(body.getLegalCertType())
                    || StringUtils.isBlank(body.getLegalCertNo())
                    || StringUtils.isBlank(body.getSettleCardType())
                    || StringUtils.isBlank(body.getBankAccountMobile())
                    || StringUtils.isBlank(body.getSettleBankAccountNo())
                    || StringUtils.isBlank(body.getSettleAccountName())
                    || StringUtils.isBlank(body.getSettleBankName())
                    || StringUtils.isBlank(body.getSettleBankAcctType())
                    || StringUtils.isBlank(body.getBankDmType())
                    || StringUtils.isBlank(body.getBankRateType())
                    || StringUtils.isBlank(body.getBankSettleCycle())
                    || StringUtils.isBlank(body.getBankRate())
                    || StringUtils.isBlank(body.getOpType())) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("必填参数值不能为空");
                logger.info(BIZ + midoid + " body中必填参数值不能为空[orderId],[name],[nickName],[address],[tel],[mchtType],[legalName],[legalCertType],[legalCertNo],[settleCardType],[bankAccountMobile],[settleBankAccountNo],[settleAccountName],[settleBankName],[settleBankAcctType],[bankDmType],[bankRateType],[bankSettleCycle],[bankRate],[opType]，即MchtRegisteRequestBody：" + JSONObject.toJSONString(body));
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
     * 调用商户入驻接口
     */
    public MchtRegisteResponse callHandler(TradeBaseRequest tradeRequest, String ip, String midoid) {

        MchtRegisteResponse.MchtRegisteResponseHead head = new MchtRegisteResponse.MchtRegisteResponseHead();
        MchtRegisteResponse.MchtRegisteResponseBody body = new MchtRegisteResponse.MchtRegisteResponseBody();
        String sign = "";
        String METHOD = "调用Boss-Trade商户入驻Handler接口";
        try {
            logger.info(BIZ + midoid + METHOD + "【start】参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
            CommonResult commonResult = (CommonResult) tradeRegisteQueryHandler.process(tradeRequest, ip);
            logger.info(BIZ + midoid + METHOD + "【end】返回值commonResult：" + JSON.toJSONString(commonResult));
            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                logger.info(BIZ + midoid + METHOD + " 入驻成功");
                Result mchtResult = (Result) commonResult.getData();
                head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());

                body.setMchtId(mchtResult.getMchtId());//商户号
                body.setMchtKey(mchtResult.getMchtKey());//商户key
                body.setStatus("SUCCESS");
                Map<String, String> signMap = JSONObject.parseObject(
                        JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
                        });
                sign = SignUtil.md5Sign(signMap, mchtResult.getMchtKey(),"");// 签名
            } else {
                logger.info(BIZ + midoid + METHOD + " 入驻失败");
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
        MchtRegisteResponse registeResponse = new MchtRegisteResponse(head, body, sign);
        logger.info(BIZ + midoid + METHOD + "返回客户端MchtRegisteResponse：" + JSON.toJSONString(registeResponse));
        return registeResponse;
    }
}

