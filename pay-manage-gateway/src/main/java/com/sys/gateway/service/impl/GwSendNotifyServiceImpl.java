package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.boss.api.service.trade.handler.ITradePayNotifyHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.PostUtil;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.service.MchtGwOrderService;
import com.sys.core.service.MerchantService;
import com.sys.gateway.service.GwRecNotifyService;
import com.sys.gateway.service.GwSendNotifyService;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.Order;
import com.sys.trans.api.entry.QuickPay;
import com.sys.trans.api.entry.Trade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description:网关支付业务处理实现类
 * @author: ChenZL
 * @time: 2017年9月9日
 */
@Service
public class GwSendNotifyServiceImpl implements GwSendNotifyService {

    protected final Logger logger = LoggerFactory.getLogger(GwRecNotifyService.class);

    @Autowired
    private MchtGwOrderService mchtGwOrderService;


    @Autowired
    private MerchantService merchantService;


    @Override
    public CommonResult sendNotify(String payType, Trade trade) {
        CommonResult commonResult = new CommonResult();
        commonResult.setRespCode(ErrorCodeEnum.FAILURE.getCode());
        TradeNotify tradeNotify = null;

        //根据支付类型返回不同格式数据
        if (payType.startsWith("wx") || payType.startsWith("ali") || payType.startsWith("qq")) {
            //收银台支付
            tradeNotify = buildTradeNotify4Order(trade, "status");
        } else if (PayTypeEnum.QUICK_TX.equals(payType) || PayTypeEnum.QUICK.equals(payType)) {
            //快捷
            QuickPay quick = trade.getQuickPay();
            tradeNotify = buildTradeNotify4TXQuick(trade, "status");
        } else if (PayTypeEnum.MERCHANT_REGISTER.equals(payType)) {
            //商户入驻
            tradeNotify = buildTradeNotify4Registe(trade, "status");
        }

        String url = tradeNotify.getUrl();
        String contentType = "application/json";
        String content = JSON.toJSONString(tradeNotify.getResponse());
        try {
            //HTTP异步通知商户交易结果
            String mchtRes = HttpUtil.postConnManager(url, content, contentType, "UTF-8", "UTF-8");
            logger.info("异步通知商户返回:" + mchtRes);
            if ("SUCCESS".equals(mchtRes)) {
                commonResult.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("异步通知商户系统异常,e.msg:" + e.getMessage());
            commonResult.setRespMsg("异步通知商户系统异常,e.msg:" + e.getMessage());
        }

        return null;
    }

    @Override
    public CommonResult sendNotifyAgain(String platOrderId, String suffix) {
        String message = "";
        String resultStr = "";
        CommonResult commonResult = new CommonResult();
        try {
            //查询平台订单信息
            MchtGatewayOrder order = new MchtGatewayOrder();
            MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
            mchtGatewayOrder.setPlatOrderId(platOrderId);

            mchtGatewayOrder.setSuffix(suffix);
            List<MchtGatewayOrder> mchtGatewayOrderList = mchtGwOrderService.list(mchtGatewayOrder);
            if (mchtGatewayOrderList != null && mchtGatewayOrderList.size() > 0) {
                order = mchtGatewayOrderList.get(0);
            }

            String notifyUrl = order.getNotifyUrl();
            //POST发送通知数据
            if (StringUtils.isNotBlank(notifyUrl)) {
                TradeNotify tradeNotify = buildOrderNotify(order);
                String requestUrl = tradeNotify.getUrl();
                String requestMsg = JSON.toJSONString(tradeNotify.getResponse());
                logger.info("[start] 异步通知商户开始，请求地址：{} 请求内容：{}", requestUrl, requestMsg);
                String result = PostUtil.postMsg(requestUrl, requestMsg);
                logger.info("[end] 异步通知商户结束，请求地址：{} 请求内容：{} 商户响应：", requestUrl, requestMsg);

                if ("SUCCESS".equalsIgnoreCase(result)) {
                    order.setSupplyStatus("0");
                    commonResult.setRespCode("SUCCESS");
                    commonResult.setRespMsg("补发成功");
                } else {
                    order.setSupplyStatus("1");
                    commonResult.setRespCode("SUCCESS");
                    commonResult.setRespMsg("已补发，商户响应：" + result);
                }
            } else {
                order.setSupplyStatus("1");
                message = "补发失败，此订单通知地址为空";
                logger.info(message + " 订单号：" + order.getPlatOrderId());
            }
            order.setSuffix(suffix);
            mchtGwOrderService.saveByKey(order);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("补发失败，" + e.getMessage());
            message = "补发失败，" + e.getMessage();
        } finally {
            logger.info(message);
        }
        return commonResult;
    }


    private TradeNotify buildOrderNotify(MchtGatewayOrder order) throws Exception {
        TradeNotify tradeNotify = new TradeNotify();
        tradeNotify.setUrl(order.getNotifyUrl());

        TradeNotifyResponse tradeNotifyResponse = new TradeNotifyResponse();
        TradeNotifyResponse.TradeNotifyResponseHead head = new TradeNotifyResponse.TradeNotifyResponseHead();
        head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
        head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());

        TradeNotifyResponse.TradeNotifyBody body = new TradeNotifyResponse.TradeNotifyBody();
        body.setMchtId(order.getMchtId());
        body.setOrderId(order.getMchtOrderId());
        body.setStatus(PayStatusEnum.PAY_SUCCESS.getCode().equals(order.getStatus()) ? "SUCCESS" : "PROCESSING");
        body.setTradeId(order.getId());
        body.setBankCardNo(order.getBankCardNo());
        body.setAmount(order.getAmount() + "");
        TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(body);
        String mchtKey = merchantService.queryByKey(order.getMchtId()).getMchtKey();
        String sign = SignUtil.md5Sign(new HashMap<>(treeMap), mchtKey);

        tradeNotifyResponse.setSign(sign);
        tradeNotifyResponse.setBody(body);
        tradeNotify.setResponse(tradeNotifyResponse);
        return tradeNotify;
    }

    /**
     * 支付订单通知
     *
     * @param trade
     * @param status
     * @return
     */
    private TradeNotify buildTradeNotify4Order(Trade trade, String status) {
        Config config = trade.getConfig();
        Order order = trade.getOrder();
        Map oriReqMap = (Map) trade.getData();
        TradeNotify tradeNotify = new TradeNotify();

        try {
            String respCode = ErrorCodeEnum.SUCCESS.getCode();
            String respMsg = ErrorCodeEnum.SUCCESS.getDesc();

            String mchtId = config.getMchtId();
            String orderId = order.getMchtOrderNo();
            String tradeId = order.getOrderNo();
            String bankCardNo = order.getBankCardNo();
            status = PayStatusEnum.PAY_SUCCESS.getCode().equals(status) ?
                    "SUCCESS" : (PayStatusEnum.PROCESSING.getCode().equals(status) ? "PROCESSING" : "FAILURE");
            String amount = order.getAmount();
            String mchtKey = config.getMchtKey();
            String notifyUrl = (String) oriReqMap.get("notifyUrl");
            tradeNotify.setUrl(notifyUrl);

            TradeNotifyResponse tradeNotifyResponse = new TradeNotifyResponse();
            TradeNotifyResponse.TradeNotifyResponseHead head = new TradeNotifyResponse.TradeNotifyResponseHead();
            head.setRespCode(respCode);
            head.setRespMsg(respMsg);
            TradeNotifyResponse.TradeNotifyBody body = new TradeNotifyResponse.TradeNotifyBody();
            body.setMchtId(mchtId);
            body.setOrderId(orderId);
            body.setTradeId(tradeId);
            body.setStatus(status);
            body.setAmount(amount);
            body.setBankCardNo(bankCardNo);

            tradeNotifyResponse.setHead(head);
            tradeNotifyResponse.setBody(body);

            TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(body);
            String sign = SignUtil.md5Sign(new HashMap<String, String>(treeMap), mchtKey);
            tradeNotifyResponse.setSign(sign);

            tradeNotify.setResponse(tradeNotifyResponse);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("组合商户异步通知对象异常 msg：" + e.getMessage());
        }

        return tradeNotify;
    }


    /**
     * 商户入驻结果通知
     */
    private TradeNotify buildTradeNotify4Registe(Trade trade, String status) {
        //TODO:
        return null;
    }

    /**
     * TX快捷订单通知
     */
    private TradeNotify buildTradeNotify4TXQuick(Trade trade, String status) {
        //TODO:
        return null;
    }

}
