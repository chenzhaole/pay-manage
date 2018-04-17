package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.SignUtil;
import com.sys.gateway.service.GwRecNotifyService;
import com.sys.gateway.service.GwSendNotifyService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
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
     * data数据
     */
    @RequestMapping("/recNotify/data/{chanCode}/{platOrderId}/{payType}")
    @ResponseBody
    public String recNotifyData(@RequestBody String data, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws TransException {
        logger.info(BIZ+" data数据 [START] chanCode=" + chanCode + " platOrderId=" + platOrderId + " 异步通知原始报文data="+ data + "");

        String resp2chan = "FAILURE";
        //解析并校验签名上游通道异步通知的数据
        CommonResult tradeResult = recNotifyService.reciveNotify(chanCode, platOrderId, payType, data);
        if(ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode()) || ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())){
            //解析通道数据成功,更新数据库订单状态成功
            //响应给上游通道的信息--不论是否通知下游商户成功，这里都会响应上游通道接收异步通知成功，因为能执行到此处，说明数据库已经是成功状态，不允许通道方补抛
            resp2chan = tradeResult.getRespMsg();
            //数据不为空，才会给商户通知
            if(null != tradeResult.getData()){
                //通知商户信息源
                CacheTrade redisOrderTrade = (CacheTrade) tradeResult.getData();
                logger.info(BIZ+platOrderId+"，bossTrade查询的缓存订单Trade对象:"+JSON.toJSONString(redisOrderTrade));
                CommonResult serviceResult = sendNotifyService.sendNotify(payType,redisOrderTrade);
                if(ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())){
                    logger.info(BIZ+platOrderId+"，通知商户成功");
                }else{
                    logger.info(BIZ+platOrderId+"，通知商户失败");
                }
            }
        }else{
            logger.info(BIZ+platOrderId+"，bossTrade处理上游通道异步通知请求失败."+JSON.toJSONString(tradeResult));
        }

        logger.info(BIZ+platOrderId+"，接收上游通道异步通知接口data数据 [END],返回通道响应: "+resp2chan);
        return resp2chan;
    }


    /**
     * 接受统一异步通知结果
     * param参数值
     */
    @RequestMapping("/recNotify/param/{chanCode}/{platOrderId}/{payType}")
    @ResponseBody
    public String recNotifyParam(HttpServletRequest request, HttpServletResponse response, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws TransException {

        TreeMap<String, String> dataMap = new TreeMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                dataMap.put(en, value);
            }
        }
        String data = JSON.toJSONString(dataMap);
        logger.info(BIZ+" parameter数据 [START] chanCode=" + chanCode + " platOrderId=" + platOrderId + " 异步通知原始报文data="+ data + "");
        logger.info(BIZ+"[START] data=" + data);
        String resp2chan = "FAILURE";
        //解析并校验签名上游通道异步通知的数据
        CommonResult tradeResult = recNotifyService.reciveNotify(chanCode, platOrderId, payType, data);
        if(ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode())|| ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())){
            //解析通道数据成功,更新数据库订单状态成功
            //响应给上游通道的信息--不论是否通知下游商户成功，这里都会响应上游通道接收异步通知成功，因为能执行到此处，说明数据库已经是成功状态，不允许通道方补抛
            resp2chan = tradeResult.getRespMsg();
            //数据不为空，才会给商户通知
            if(null != tradeResult.getData()) {
                //通知商户信息源
                CacheTrade redisOrderTrade = (CacheTrade) tradeResult.getData();
                logger.info(BIZ + platOrderId + "，bossTrade查询的缓存订单Trade对象:" + JSON.toJSONString(redisOrderTrade));
                CommonResult serviceResult = sendNotifyService.sendNotify(payType, redisOrderTrade);
                if (ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())) {
                    logger.info(BIZ + platOrderId + "，通知商户成功");
                } else {
                    logger.info(BIZ + platOrderId + "，通知商户失败");
                }
            }
        }else{
            logger.info(BIZ+platOrderId+"，bossTrade处理上游通道异步通知请求失败."+JSON.toJSONString(tradeResult));
        }

        logger.info(BIZ+platOrderId+"，接收上游通道异步通知接口parameter数据 [END],返回通道响应: "+resp2chan);
        return resp2chan;
    }

    /**
     * 接受统一异步通知结果
     * param参数值
     */
    @RequestMapping("/testNotify/{mchtKey}")
    @ResponseBody
    public String testNotify(@RequestBody String data, @PathVariable String mchtKey, HttpServletRequest request, HttpServletResponse response) throws TransException {
        logger.info("模拟商户接收异步通知，收到的data="+ data);
        String ret = "FAIL";
        String mchtOrdertId = "";
        if(StringUtils.isBlank(data)){
          logger.info("模拟商户接收异步通知，收到的data = null");
        }else{
            TradeNotifyResponse tradeNotifyResponse = JSON.parseObject(data, TradeNotifyResponse.class);
            mchtOrdertId = tradeNotifyResponse.getBody().getOrderId();
            String retSign = tradeNotifyResponse.getSign();
                logger.info("商户订单号："+mchtOrdertId+"，模拟商户接收异步通知，对接收的tradeNotifyResponse="+JSONObject.toJSONString(tradeNotifyResponse));
            String sign = "";
            try {
                TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(tradeNotifyResponse.getBody());
                logger.info("商户订单号："+mchtOrdertId+"，模拟商户接收异步通知，对接收的数据签名，签名key="+mchtKey+"，签名treeMap="+ treeMap);
                sign = SignUtil.md5Sign(new HashMap<String, String>(treeMap), mchtKey);
                logger.info("商户订单号："+mchtOrdertId+"，模拟商户接收异步通知，对接收的数据签名，签名结果sign="+ sign);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(sign.equals(retSign)){
                ret = "SUCCESS";
            }
        }
        logger.info("商户订单号："+mchtOrdertId+"，模拟商户接收异步通知,返回的结果="+ ret);
        return ret;
    }

}
