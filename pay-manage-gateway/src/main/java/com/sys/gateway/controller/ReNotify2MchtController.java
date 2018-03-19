//package com.sys.gateway.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.sys.boss.api.entry.trade.TradeNotify;
//import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
//import com.sys.common.enums.ErrorCodeEnum;
//import com.sys.common.enums.PayStatusEnum;
//import com.sys.common.util.BeanUtils;
//import com.sys.common.util.PostUtil;
//import com.sys.common.util.SignUtil;
//import com.sys.core.dao.dmo.MchtGatewayOrder;
//import com.sys.core.service.MchtGwOrderService;
//import com.sys.core.service.MerchantService;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.HashMap;
//import java.util.List;
//import java.util.TreeMap;
//
///**
// * 补发异步通知to商户
// *
// * Created by chenzhaole on 2018/2/5.
// */
//@Controller
//@RequestMapping(value = "")
//public class ReNotify2MchtController {
//    protected final Logger logger = LoggerFactory.getLogger(ReNotify2MchtController.class);
//
//    @Autowired
//    private MchtGwOrderService mchtGwOrderService;
//
//    @Autowired
//    private MerchantService merchantService;
//
//    private final String BIZ = "补发异步通知-";
//
//    @RequestMapping(value = "/gateway/renotify")
//    @ResponseBody
//    public String renotify(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
//
//            String platOrderId = request.getParameter("orderId");
//            String suffix = request.getParameter("suffix");
//
//            String message = "";
//            String resultStr = "";
//            try {
//                //查询平台订单信息
//                MchtGatewayOrder order = new MchtGatewayOrder();
//                MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
//                mchtGatewayOrder.setPlatOrderId(platOrderId);
//
//                mchtGatewayOrder.setSuffix(suffix);
//                List<MchtGatewayOrder> mchtGatewayOrderList = mchtGwOrderService.list(mchtGatewayOrder);
//                if (mchtGatewayOrderList != null && mchtGatewayOrderList.size() > 0) {
//                    order =  mchtGatewayOrderList.get(0);
//                }
//
//                String notifyUrl = order.getNotifyUrl();
//                //POST发送通知数据
//                if (StringUtils.isNotBlank(notifyUrl)) {
//                    TradeNotify tradeNotify = buildTradeNotify(order);
//                    String requestUrl = tradeNotify.getUrl();
//                    String requestMsg = JSON.toJSONString(tradeNotify.getResponse());
//                    logger.info("[start] 异步通知商户开始，请求地址：{} 请求内容：{}", requestUrl, requestMsg);
//                    String result = PostUtil.postMsg(requestUrl, requestMsg);
//                    logger.info("[end] 异步通知商户结束，请求地址：{} 请求内容：{} 商户响应：{}", requestUrl, requestMsg, response);
//
//                    if ("SUCCESS".equalsIgnoreCase(result)) {
//                        order.setSupplyStatus("0");
//                        message = "补发成功";
//                        resultStr = "SUCCESS";
//                    } else {
//                        order.setSupplyStatus("1");
//                        message = "已补发，商户响应：" + result;
//                        resultStr = "result";
//                    }
//                } else {
//                    order.setSupplyStatus("1");
//                    message = "补发失败，此订单通知地址为空";
//                    logger.info(message + " 订单号：" + order.getPlatOrderId());
//                }
//                order.setSuffix(suffix);
//                mchtGwOrderService.saveByKey(order);
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.error("补发失败，" + e.getMessage());
//                message = "补发失败，" + e.getMessage();
//            } finally {
//                logger.info(message);
//            }
//            return resultStr;
//    }
//
//    private TradeNotify buildTradeNotify(MchtGatewayOrder order) throws Exception {
//        TradeNotify tradeNotify = new TradeNotify();
//        tradeNotify.setUrl(order.getNotifyUrl());
//
//        TradeNotifyResponse tradeNotifyResponse = new TradeNotifyResponse();
//        TradeNotifyResponse.TradeNotifyResponseHead head = new TradeNotifyResponse.TradeNotifyResponseHead();
//        head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//        head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//
//        TradeNotifyResponse.TradeNotifyBody body = new TradeNotifyResponse.TradeNotifyBody();
//        body.setMchtId(order.getMchtId());
//        body.setOrderId(order.getMchtOrderId());
//        body.setStatus(PayStatusEnum.PAY_SUCCESS.getCode().equals(order.getStatus()) ? "SUCCESS" : "PROCESSING");
//        body.setTradeId(order.getId());
//        body.setBankCardNo(order.getBankCardNo());
//        body.setAmount(order.getAmount() + "");
//        TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(body);
//        String mchtKey = merchantService.queryByKey(order.getMchtId()).getMchtKey();
//        String sign = SignUtil.md5Sign(new HashMap<>(treeMap), mchtKey);
//
//        tradeNotifyResponse.setSign(sign);
//        tradeNotifyResponse.setBody(body);
//        tradeNotify.setResponse(tradeNotifyResponse);
//        return tradeNotify;
//    }
//
//}
