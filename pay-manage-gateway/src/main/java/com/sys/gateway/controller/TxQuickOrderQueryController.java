package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickQueryRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickQueryRequestBody;
import com.sys.boss.api.entry.trade.response.quickpay.TXQuickPayResponse;
import com.sys.boss.api.entry.trade.response.quickpay.TXQuickQueryOrderListResponse;
import com.sys.boss.api.entry.trade.response.quickpay.TXQuickQueryOrderResponse;
import com.sys.boss.api.service.trade.handler.ITradeTxDFQueryHandler;
import com.sys.boss.api.service.trade.handler.ITradeTxQueryOrderHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.PostUtil;
import com.sys.common.util.SignUtil;
import com.sys.core.service.TaskLogService;
import com.sys.gateway.common.IpUtil;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TX快捷订单查询
 * Created by chenzhaole on 2018/1/26.
 */
@Controller
public class TxQuickOrderQueryController {
    protected final Logger logger = LoggerFactory.getLogger(TxQuickOrderQueryController.class);

    @Autowired
    private ITradeTxQueryOrderHandler tradeTxQueryOrderHandler;
    @Autowired
    private ITradeTxDFQueryHandler tradeTxDFQueryHandler;
    @Autowired
    private TaskLogService taskLogService;;

    private ExecutorService executor = Executors.newFixedThreadPool(20);

    /**
     * 批量查询TX快捷订单For定时任务
     *
     */
    @RequestMapping(value="/gateway/txQueryOrder")
    @ResponseBody
    public String query(@RequestParam(required = false,value = "id") Integer logId) {
        String BIZ = "支付结果通知商户-";
        try {
            CommonResult result = tradeTxQueryOrderHandler.process(null);
            if (!ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())) {
                return "fail";
            }
            List<TradeNotify> list = (List<TradeNotify>) result.getData();
            for (TradeNotify tradeNotify : list) {
                TXQuickPayResponse txResp = (TXQuickPayResponse) tradeNotify.getResponse();
                String mchtId = txResp.getBody().getMchtId();
                String mchtgOrderId = txResp.getBody().getOrderId();
                final String bizMidOid = BIZ + mchtId + "-" + mchtgOrderId;
                final String url = tradeNotify.getUrl();
                final String content = JSON.toJSONString(txResp);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            logger.info(bizMidOid + " http通知url=" + url);
                            logger.info(bizMidOid + " http通知content=" + content);
                            // HTTP-POST通知商户支付结果
                            String mchtResp = PostUtil.postMsg(url, content);
                            logger.info(bizMidOid + " http商户应答=" + mchtResp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            taskLogService.recordLog(logId,result);

        } catch (Exception e) {
            logger.equals(BIZ + " 系统异常 e.Msg=" + e.getMessage());
            e.printStackTrace();
        }

        return "ok";
    }

    /**
     * 定时任务代付查单
     */
    @RequestMapping(value="/gateway/txDFQueryOrder")
    @ResponseBody
    public String txDFQueryOrder(@RequestParam(required = false,value = "id") Integer logId) {
        CommonResult result = tradeTxDFQueryHandler.process();
        taskLogService.recordLog(logId,result);
        return "ok";
    }



    /**
     * 单笔快捷订单查询接口
     *
     * @param data
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/gateway/queryQuickOrder")
    @ResponseBody
    public String queryQuickOrder(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        String METHOD = "TX快捷单笔查询-";
        TXQuickQueryOrderResponse queryResp = new TXQuickQueryOrderResponse();
        TXQuickQueryOrderResponse.TXQuickQueryOrderResponseHead head = new TXQuickQueryOrderResponse.TXQuickQueryOrderResponseHead();
        TXQuickQueryOrderResponse.TXQuickQueryOrderResponseBody body = new TXQuickQueryOrderResponse.TXQuickQueryOrderResponseBody();
        String midoid = "";//商户ID+商户订单ID
        String sign;
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info(METHOD + "获取到客户端请求ip为：ip=" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info(METHOD + "收到客户端请求参数后做url解码后的值为：data=" + data);

            if (data.endsWith("=")) {
                data = data.substring(0, data.length() - 1);
            }

            //解析请求参数
            TXQuickQueryRequest mchtRequest = JSON.parseObject(data, TXQuickQueryRequest.class);
            midoid = mchtRequest.getHead().getMchtId() + "-" + mchtRequest.getBody().getTradeId();

            //校验请求参数
            CommonResponse checkResp = checkRequestParam(mchtRequest, midoid, METHOD);
            logger.info(METHOD + "校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));

            if (ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                //调用快捷查询接口
                TXQuickQueryRequest queryRequest = (TXQuickQueryRequest) checkResp.getData();
                logger.info(METHOD + "调用快捷单笔查询接口，传入的TXQuickQueryRequest信息=" + JSONObject.toJSONString(queryRequest));
                logger.info(METHOD + "调用快捷捷单笔查询接口，返回的TXQuickQueryOrderResponse信息=" + JSONObject.toJSONString(queryResp));

                CommonResult commonResult = tradeTxQueryOrderHandler.process(queryRequest);
                logger.info(METHOD + midoid + METHOD + " 返回值commonResult：" + JSON.toJSONString(commonResult));
                if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                    Result mchtResult = (Result) commonResult.getData();
                    head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                    head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
                    body.setOrderId(mchtResult.getMchtOrderNo());//商户订单号
                    body.setTradeId(mchtResult.getOrderNo());//我司订单号
                    body.setMchtId(mchtResult.getMchtId());
                    body.setStatus(mchtResult.getStatus());
                    body.setBankCardNo(mchtResult.getBankCardNo());
                    body.setAmount(mchtResult.getOrderAmount());
                    // 签名
                    Map<String, String> params = JSONObject.parseObject(
                            JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
                            });
                    sign = SignUtil.md5Sign(params, mchtResult.getMchtKey(),"");
                    queryResp.setSign(sign);
                    queryResp.setHead(head);
                    queryResp.setBody(body);
                } else {
                    String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode() : commonResult.getRespCode();
                    String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc() : commonResult.getRespMsg();
                    head.setRespCode(respCode);
                    head.setRespMsg(respMsg);
                    queryResp.setHead(head);
                }


            } else {
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                queryResp.setHead(head);
            }

            queryResp.setHead(head);
            queryResp.setBody(body);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("快捷查单接口抛异常" + e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("支付网关错误");
        }
        logger.info("快捷查单，返回下游商户值：" + JSON.toJSONString(queryResp));
        return JSON.toJSONString(queryResp);
    }

    /**
     * 批量快捷订单查询接口
     *
     * @param data
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/gateway/queryQuickOrderList")
    @ResponseBody
    public String queryQuickOrderList(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        String METHOD = "TX快捷批量查询-";
        TXQuickQueryOrderListResponse queryResp = new TXQuickQueryOrderListResponse();
        TXQuickQueryOrderListResponse.TXQuickQueryOrderListResponseHead head = new TXQuickQueryOrderListResponse.TXQuickQueryOrderListResponseHead();

        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info(METHOD + "获取到客户端请求ip为：ip=" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info(METHOD + "收到客户端请求参数后做url解码后的值为：data=" + data);
            //校验请求参数
            CommonResponse checkResp = null;//TODO:待实现
            logger.info(METHOD + "校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));
            if (ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                //调用快捷查询接口
                TXQuickQueryRequest queryRequest = (TXQuickQueryRequest) checkResp.getData();
                logger.info(METHOD + "调用快捷批量查询接口，传入的TXQuickQueryRequest信息为=" + JSONObject.toJSONString(queryRequest));
                queryResp = null;//(TXQuickQueryOrderResponse) commPayService.wapPay(tradeRequest, ip);
                logger.info(METHOD + "调用快捷批量查询接口，返回的TXQuickQueryOrderListResponse信息=" + JSONObject.toJSONString(queryResp));

            } else {
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                queryResp.setHead(head);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("wap支付接口抛异常" + e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("支付网关错误");
        }
        logger.info("创建wap订单，返回下游商户值：" + JSON.toJSONString(queryResp));
        return JSON.toJSONString(queryResp);
    }

    /**
     * 校验参数
     **/
    public CommonResponse checkRequestParam(TXQuickQueryRequest mchtRequest, String midoid, String BIZ) {
        CommonResponse checkResp = new CommonResponse();
        try {
            //解析请求参数
            if (mchtRequest.getHead() == null || mchtRequest.getBody() == null || mchtRequest.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("[head],[body],[sign]必填参数值不能为空");
                logger.info(BIZ + midoid + " [head],[body],[sign]必填参数为空，即TradeSelectPayRequest：" + JSONObject.toJSONString(mchtRequest));
                return checkResp;
            }

            TradeReqHead head = mchtRequest.getHead();
            if (head.getMchtId() == null || head.getVersion() == null || head.getBiz() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("[mchtId],[version],[biz]必填参数值不能为空");
                logger.info(BIZ + midoid + " [mchtId],[version],[biz]必填参数为空，即TradeSelectPayRequest：" + JSONObject.toJSONString(head));
                return checkResp;
            }

            TXQuickQueryRequestBody body = mchtRequest.getBody();
            if (StringUtils.isBlank(body.getOrderTime())
                    || StringUtils.isBlank(body.getTradeId())) {

                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespMsg("必填参数值不能为空");
                logger.info(BIZ + midoid + "[orderTime],[tradeId]请求参数值不能为空，SelectPayRequestBody：" + JSONObject.toJSONString(body));
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

}
