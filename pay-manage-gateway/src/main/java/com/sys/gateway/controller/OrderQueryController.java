package com.sys.gateway.controller;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickQueryRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickQueryRequestBody;
import com.sys.boss.api.entry.trade.response.quickpay.TXQuickQueryOrderResponse;
import com.sys.boss.api.service.trade.handler.ITradeQueryOrderHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayChannelEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.service.MchtGwOrderService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TX订单查询
 * Created by chenzhaole on 2018/1/26.
 */
@Controller
public class OrderQueryController {
    protected final Logger logger = LoggerFactory.getLogger(OrderQueryController.class);

    @Autowired
    private ITradeQueryOrderHandler tradeQueryOrderHandler;
    @Autowired
    private TaskLogService taskLogService;;

    private ExecutorService executor = Executors.newFixedThreadPool(20);

    @Autowired
    private MchtGwOrderService mchtGwOrderService;

    /**
     * 单笔订单查询接口
     *
     * @param data
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/gateway/queryOrder")
    @ResponseBody
    public String queryQuickOrder(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        String METHOD = "单笔订单查询-";
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
                //调用查询接口
                TXQuickQueryRequest queryRequest = (TXQuickQueryRequest) checkResp.getData();
                logger.info(METHOD + "调用单笔订单查询接口，传入的TXQuickQueryRequest信息=" + JSONObject.toJSONString(queryRequest));
                logger.info(METHOD + "调用单笔订单查询接口，返回的TXQuickQueryOrderResponse信息=" + JSONObject.toJSONString(queryResp));

                CommonResult commonResult = tradeQueryOrderHandler.process(queryRequest);
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
            logger.error("查单接口抛异常" + e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("支付网关错误");
        }
        logger.info("查单，返回下游商户值：" + JSON.toJSONString(queryResp));
        return JSON.toJSONString(queryResp);
    }

    /**
     * 多笔订单查询接口 定时任务调度(10分钟内订单)
     * @return String
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/gateway/queryMoreOrder")
    @ResponseBody
    public String queryMoreQuickOrder( HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        String METHOD = "多笔订单查询-";
        //构建需要查单的查询条件
        MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
        Date currentTime= new Date();
        mchtGatewayOrder.setUpdateTime(currentTime);
        logger.info("查询的结束时间为："+currentTime);
        //int interval =Integer.valueOf(ConfigUtils.getProperty("query_order_time_interval"))*60*1000;
        int interval =10*60*1000;
        Date beginDate =new Date(currentTime.getTime()-interval);
        mchtGatewayOrder.setCreateTime(beginDate);
        logger.info("查询的开始时间为："+beginDate);
        //查询处理中和提交成功的订单
        //TODO:未知状态
        mchtGatewayOrder.setStatus(PayStatusEnum.SUBMIT_SUCCESS.getCode()+","+PayStatusEnum.PROCESSING.getCode());
        mchtGatewayOrder.setChanCode(PayChannelEnum.YINJUN.getCode());

        List<MchtGatewayOrder> orders=mchtGwOrderService.morelist(mchtGatewayOrder);

        if(orders == null || orders.size() ==0){
            return null;
        }

        for(MchtGatewayOrder order: orders){
            //封装请求参数
            TXQuickQueryRequest mchtRequest = new TXQuickQueryRequest();
            TXQuickQueryRequestBody body = new TXQuickQueryRequestBody();
            TradeReqHead head = new TradeReqHead();
            head.setBiz(order.getPayType());
            body.setTradeId(order.getPlatOrderId());
            mchtRequest.setHead(head);
            mchtRequest.setBody(body);

            try {
                CommonResult commonResult = tradeQueryOrderHandler.processMore(mchtRequest);
            }catch (Exception e){
                logger.info("平台订单号-"+order.getPlatOrderId()+"状态查询异常");
            }

        }
        return null;
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
