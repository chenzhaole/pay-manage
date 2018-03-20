package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.gateway.service.GwRecNotifyService;
import com.sys.gateway.service.GwSendNotifyService;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TransException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * 接收通道异步通知
 * 向商户发送异步通知
 *
 * Created by chenzhaole on 2018/3/15.
 */
@Controller
@RequestMapping(value = "")
public class GwRecNotifyController {
    protected final Logger logger = LoggerFactory.getLogger(GwRecNotifyController.class);

    @Autowired
    private GwRecNotifyService recNotifyService;

    @Autowired
    private GwSendNotifyService sendNotifyService;


    private final String BIZ = "接收异步通知GwRecNotifyController->platOrderId：";

    /**
     * 接受统一异步通知结果
     * POST请求
     */
    @RequestMapping("/recNotify/{chanCode}/{platOrderId}/{payType}")
    @ResponseBody
    public String recNotify(@RequestBody String data, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws TransException {
        logger.info(BIZ+"[START] chanCode=" + chanCode + " platOrderId=" + platOrderId + " 异步通知原始报文data="+ data + "");

        String resp2chan = "FAILURE";
        //解析并校验签名上游通道异步通知的数据
        CommonResult tradeResult = recNotifyService.reciveNotify(chanCode, platOrderId, payType, data);
        if(ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode())){
            //解析通道数据成功,更新数据库订单状态成功
            //响应给上游通道的信息--不论是否通知下游商户成功，这里都会响应上游通道接收异步通知成功，因为能执行到此处，说明数据库已经是成功状态，不允许通道方补抛
            resp2chan = tradeResult.getRespMsg();
            //通知商户信息源
            CacheTrade redisOrderTrade = (CacheTrade) tradeResult.getData();
            logger.info(BIZ+platOrderId+"，bossTrade查询的缓存订单Trade对象:"+JSON.toJSONString(redisOrderTrade));
            CommonResult serviceResult = sendNotifyService.sendNotify(payType,redisOrderTrade);
            if(ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())){
                logger.info(BIZ+platOrderId+"，通知商户成功");
            }else{
                logger.info(BIZ+platOrderId+"，通知商户失败");
            }
        }else if(ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())){
            //防止上游重复补抛异步通知--暂时返回success，后期会改造从平台录入

            resp2chan = "success";
            logger.info(BIZ+platOrderId+"，bossTrade处理上游通道异步通知请求,此笔订单已经成功，这是上游在重复补抛，不予处理，给上游通道返回resp2chan="+resp2chan);
        }else{
            logger.info(BIZ+platOrderId+"，bossTrade处理上游通道异步通知请求失败."+JSON.toJSONString(tradeResult));
        }

        logger.info(BIZ+platOrderId+"，接收上游通道异步通知接口-END,返回通道响应: "+resp2chan);
        return resp2chan;
    }

    /**
     * 模拟商户接受异步通知
     * @param request
     * @return
     */
    @RequestMapping("testNotify")
    @ResponseBody
    public String testNotify(@RequestBody String data) {
        String result = "ERROR";
        try {
            if(StringUtils.isNotBlank(data)){
                TradeNotifyResponse beanData = JSON.parseObject(data, TradeNotifyResponse.class);
                String mchtId = beanData.getBody().getMchtId();
                String platOrderId = beanData.getBody().getTradeId();
                String mchtOrderId = beanData.getBody().getOrderId();
                logger.info("模拟商户接收异步通知，接收到的数据为："+ JSONObject.toJSONString(data));
                CommonResult commonResult = sendNotifyService.testMchtNotifyInfo(mchtId);
                if(null != commonResult && null != commonResult.getData()){
                    MchtInfo mchtInfo = (MchtInfo) commonResult.getData();
                    String key = mchtInfo.getMchtKey();
                    TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(beanData.getBody());
                    String sign = SignUtil.md5Sign(new HashMap<String, String>(treeMap), key);
                    logger.info("模拟商户接收异步通知，签名串为："+ JSONObject.toJSONString(treeMap)+"，密钥为key："+key);
                    if(beanData.getSign().equals(sign)){
                        result = "SUCCESS";
                        logger.info("模拟商户接收异步通知，platOrderId："+platOrderId+"，mchtOrderId："+mchtOrderId+"，签名通过，返回给支付平台的数据为："+result);
                    }
                }
            }else{
                logger.info("模拟商户接收异步通知，接收到的数据为null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("模拟商户接收异步通知，返回给支付平台的结果为："+result);
        return result;
    }


}
