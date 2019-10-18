package com.sys.manage.notify.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheOrder;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.boss.api.entry.trade.request.cashier.TradeCashierRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickPrePayRequest;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.*;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.service.MchtGwOrderService;
import com.sys.core.service.MerchantService;
import com.sys.manage.notify.service.GwRecNotifyService;
import com.sys.manage.notify.service.GwSendNotifyService;
import com.sys.trans.api.entry.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    private final String BIZ = "给下游商户异步通知流水信息GwSendNotifyServiceImpl->";

    @Override
    public CommonResult sendNotify(String payType, CacheTrade cacheTrade) {
        CommonResult commonResult = new CommonResult();
        commonResult.setRespCode(ErrorCodeEnum.FAILURE.getCode());
        TradeNotify tradeNotify = this.geneTradeNotifyInfo(payType, cacheTrade);
        if(null == tradeNotify){
            commonResult.setRespCode(ErrorCodeEnum.E9001.getCode());
            commonResult.setRespMsg("操作失败");
            logger.info(BIZ+"异步通知商户信息TradeNotify为Null，请求参数源CacheTrade="+JSONObject.toJSONString(cacheTrade));
            return commonResult;
        }

        String url = tradeNotify.getUrl();
        String contentType = "application/json";
        String content = JSON.toJSONString(tradeNotify.getResponse());
        try {
            String mchtOrderId = ((TradeNotifyResponse)tradeNotify.getResponse()).getBody().getOrderId();
            String platOrderId = ((TradeNotifyResponse)tradeNotify.getResponse()).getBody().getTradeId();
            //HTTP异步通知商户交易结果
            logger.info(BIZ+"商户订单号："+mchtOrderId+"，平台订单号："+platOrderId+",异步通知商户信息为："+content);
            String mchtRes = HttpUtil.postConnManager(url, content, contentType, "UTF-8", "UTF-8");
            logger.info(BIZ+"商户订单号："+mchtOrderId+"，平台订单号："+platOrderId+",异步通知商户信息为："+content+",商户返回的结果为："+mchtRes);

            //补发通知成功后，修改补发状态
            MchtGatewayOrder order = new MchtGatewayOrder();
            order.setSuffix(IdUtil.getPlatOrderIdSuffix(platOrderId));
            MchtGatewayOrder selectiveOrder = new MchtGatewayOrder();
            selectiveOrder.setPlatOrderId(platOrderId);
            if ("SUCCESS".equals(mchtRes)) {
                commonResult.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                //补单是否成功:0：成功，1：失败
                order.setSupplyStatus("0");
            }else{
                //补单是否成功:0：成功，1：失败
                order.setSupplyStatus("1");
            }
            mchtGwOrderService.updateBySelective(order,selectiveOrder);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ+"异步通知商户系统异常,e.msg:" + e.getMessage());
            commonResult.setRespMsg("操作失败");
        }
        logger.info(BIZ+"异步通知商户信息后返回给上层的commonResult："+content+",请求参数源CacheTrade为："+JSONObject.toJSONString(cacheTrade));
        return commonResult;
    }

    private int updateDbStatus(String platOrderId, Result result){
        MchtGatewayOrder order = new MchtGatewayOrder();

        if(StringUtils.isNotBlank(result.getRespMsg())){
            order.setChan2platResMsg(result.getRespMsg());
        }
        order.setSuffix(IdUtil.getPlatOrderIdSuffix(platOrderId));
        order.setUpdateTime(new Date());

        MchtGatewayOrder selectiveOrder = new MchtGatewayOrder();
        selectiveOrder.setPlatOrderId(platOrderId);
        return mchtGwOrderService.updateBySelective(order,selectiveOrder);
    }

    /**
     * 根据CacheTrade拼接通知商户的TradeNotify信息
     * @param payType
     * @param cacheTrade
     * @return
     */
    private TradeNotify geneTradeNotifyInfo(String payType, CacheTrade cacheTrade) {
        //从CacheTrade对象获取信息
        //不允许为空
        Trade trade = (Trade) cacheTrade.getTrade();
        //不允许为空
        CacheOrder cacheOrder = cacheTrade.getCacheOrder();
        //不允许为空
        CacheMcht cacheMcht = cacheTrade.getCacheMcht();
        if(null == trade || null == cacheOrder || cacheMcht == null ){
            logger.info(BIZ+"从CacheTrade中未获取到需要的数据，不给商户异步通知，CacheTrade="+ JSONObject.toJSONString(cacheTrade));
            return null;
        }
        return this.buildTradeNotifyInfo(trade, cacheOrder, cacheMcht, payType, cacheTrade);
    }

    /**
     * 封装TradeNotify信息
     * @param trade
     * @param cacheOrder
     * @param cacheMcht
     * @param payType
     * @param cacheTrade
     * @return
     */
    private TradeNotify buildTradeNotifyInfo(Trade trade, CacheOrder cacheOrder, CacheMcht cacheMcht, String payType, CacheTrade cacheTrade) {
        TradeNotify tradeNotify = new TradeNotify();
        String respCode = ErrorCodeEnum.SUCCESS.getCode();
        String respMsg = ErrorCodeEnum.SUCCESS.getDesc();
        try {
            //平台分配的下游商户号
            String mchtId = cacheMcht.getMchtId();
            //商户流水号
            String mchtOrderId = cacheOrder.getMchtOrderId();
            //平台流水号
            String platOrderId = cacheOrder.getPlatOrderId();
            String bankCardNo = "";
            String status = cacheOrder.getStatus();
            status = PayStatusEnum.PAY_SUCCESS.getCode().equals(status) ?
                    "SUCCESS" : (PayStatusEnum.PROCESSING.getCode().equals(status) ? "PROCESSING" : "FAILURE");
            String mchtKey = cacheMcht.getMchtKey();
            String notifyUrl = "";
            //根据支付类型返回不同格式数据
            //快捷支付
            QuickPay quick = null;
            //第三方支付
            Order order = null;
            //金额
            String amount = "";
            String biz = "";
            if (PayTypeEnum.QUICK_TX.getCode().equals(payType) || PayTypeEnum.QUICK_COMB_DK.getCode().equals(payType)){
                quick = trade.getQuickPay();
                amount = quick.getAmount();
                bankCardNo = quick.getBankCardNo();
                //可以为空，缓存中如果不存在的话，会从quick信息里边取出notifyUrl，否则从TradeBaseRequest中取出
                if(null == cacheTrade.getTradeBaseRequest()){
                    //缓存数据失效，去数据库查询的时间，商户通知url暂存在quick实体中
                    notifyUrl = quick.getNotifyUrl();
                }else{
                    Object prePayRequest = cacheTrade.getTradeBaseRequest();
                    TXQuickPrePayRequest tXQuickPrePayRequest = JSON.parseObject(JSON.toJSONString(prePayRequest), TXQuickPrePayRequest.class);
                    notifyUrl = tXQuickPrePayRequest.getBody().getNotifyUrl();
                    biz = tXQuickPrePayRequest.getHead().getBiz();
                }
                logger.info(BIZ+",此订单是快捷支付流水，"+"商户订单号："+mchtOrderId+"，平台订单号："+platOrderId+",notifyUrl："+notifyUrl);
            } else if (PayTypeEnum.QUICK_REGISTER.equals(payType)) {
                //TODO 商户入驻
            }else{
                //第三方支付
                order = trade.getOrder();
                amount = order.getAmount();
                bankCardNo = order.getBankCardNo();
                //可以为空，缓存中如果不存在的话，会从Order信息里边取出notifyUrl，否则从TradeBaseRequest中取出
                if(null == cacheTrade.getTradeBaseRequest()){
                    //缓存数据失效，去数据库查询的时间，商户通知url暂存在Order实体中
                    notifyUrl = order.getNotifyUrl();
                }else{
                    TradeCashierRequest tradeCashierRequest = JSON.parseObject(JSONObject.toJSONString(cacheTrade.getTradeBaseRequest()), TradeCashierRequest.class);
                    notifyUrl = tradeCashierRequest.getBody().getNotifyUrl();
                    biz = tradeCashierRequest.getHead().getBiz();
                }
                logger.info(BIZ+",此订单第三方支付流水，"+"商户订单号："+mchtOrderId+"，平台订单号："+platOrderId+",notifyUrl："+notifyUrl);
            }
            //商户异步通知url
            tradeNotify.setUrl(notifyUrl);

            TradeNotifyResponse tradeNotifyResponse = new TradeNotifyResponse();
            TradeNotifyResponse.TradeNotifyResponseHead head = new TradeNotifyResponse.TradeNotifyResponseHead();
            head.setRespCode(respCode);
            head.setRespMsg(respMsg);
            TradeNotifyResponse.TradeNotifyBody body = new TradeNotifyResponse.TradeNotifyBody();
            body.setMchtId(mchtId);
            body.setOrderId(mchtOrderId);
            body.setTradeId(platOrderId);
            body.setStatus(status);
            body.setAmount(amount);
            body.setBankCardNo(bankCardNo);
            body.setSeq(IdUtil.getUUID());
            body.setChargeTime(DateUtils.getNoSpSysTimeString());
            if(StringUtils.isBlank(biz)){
               biz = payType;
            }
            body.setBiz(biz);
            body.setPayType(payType.substring(0, 2));

            tradeNotifyResponse.setHead(head);
            tradeNotifyResponse.setBody(body);

            TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(body);
            String log_moid = mchtOrderId+"-->"+mchtId;
            String sign = SignUtil.md5Sign(new HashMap<String, String>(treeMap), mchtKey, log_moid);
            tradeNotifyResponse.setSign(sign);

            tradeNotify.setResponse(tradeNotifyResponse);
            logger.info(BIZ+"商户订单号："+mchtOrderId+"，平台订单号："+platOrderId+",tradeNotify："+JSONObject.toJSONString(tradeNotify));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ+"组合商户异步通知对象异常 msg：" + e.getMessage());
        }

        return tradeNotify;
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
                String result = HttpUtil.postConnManager(requestUrl, requestMsg, "application/json", "UTF-8", "UTF-8");
                logger.info("[end] 异步通知商户结束，请求地址：{} 请求内容：{} 商户响应：{}", requestUrl, requestMsg,result);

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
        body.setMchtId(order.getMchtCode());
        body.setOrderId(order.getMchtOrderId());
        body.setStatus(PayStatusEnum.PAY_SUCCESS.getCode().equals(order.getStatus()) ? "SUCCESS" : "PROCESSING");
        body.setTradeId(order.getId());
        body.setBankCardNo(order.getBankCardNo());
        body.setAmount(order.getAmount() + "");
        body.setBiz(order.getPayType());
        body.setChargeTime(order.getUpdateTime()==null?DateUtils.getNoSpSysTimeString():DateUtils.formatDate(order.getUpdateTime(),"yyyyMMddHHmmss"));
        body.setPayType(order.getPayType()!=null?order.getPayType().substring(0, 2):"");
        body.setSeq(IdUtil.getUUID());
        TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(body);
        String mchtKey = merchantService.queryByKey(order.getMchtCode()).getMchtKey();
        String log_moid = order.getMchtCode()+"-->"+order.getPlatOrderId();
        String sign = SignUtil.md5Sign(new HashMap<>(treeMap), mchtKey, log_moid);

        tradeNotifyResponse.setSign(sign);
        tradeNotifyResponse.setHead(head);
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
            String log_moid = mchtId+"-->"+orderId;
            String sign = SignUtil.md5Sign(new HashMap<String, String>(treeMap), mchtKey, log_moid);
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
