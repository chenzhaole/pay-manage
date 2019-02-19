package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PageTypeEnum;
import com.sys.common.enums.PayStatusEnum;
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
import java.net.URLDecoder;
import java.util.*;

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
        try {
            data = URLDecoder.decode(data, "utf-8");
            //解析并校验签名上游通道异步通知的数据
            CommonResult tradeResult = recNotifyService.reciveNotify(chanCode, platOrderId, payType, data);
            if (ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode()) || ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())) {
                //解析通道数据成功,更新数据库订单状态成功
                //响应给上游通道的信息--不论是否通知下游商户成功，这里都会响应上游通道接收异步通知成功，因为能执行到此处，说明数据库已经是成功状态，不允许通道方补抛
                resp2chan = tradeResult.getRespMsg();
                //数据不为空，才会给商户通知
                if (null != tradeResult.getData()) {
                    //通知商户信息源
                    CacheTrade redisOrderTrade = (CacheTrade) tradeResult.getData();
                    logger.info(BIZ + platOrderId + "，bossTrade查询的缓存订单Trade对象:" + JSON.toJSONString(redisOrderTrade));
                    if(PayStatusEnum.PAY_SUCCESS.getCode().equals(redisOrderTrade.getCacheOrder().getStatus())){
                        CommonResult serviceResult = sendNotifyService.sendNotify(payType, redisOrderTrade);
                        if (ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())) {
                            logger.info(BIZ + platOrderId + "，通知商户成功");
                        } else {
                            logger.info(BIZ + platOrderId + "，通知商户失败");
                            //开启线程，异步通知商户,补抛机制,为了便于排查多线程问题，这里给线程指定名称
                            new Thread("Thread-name-"+platOrderId){
                                @Override
                                public void run() {
                                    int count = 1;//开始补抛
                                    logger.info(BIZ + platOrderId +"，异步通知商户失败，开始执行补抛,count="+count+",payType="+payType+"，CacheTrade="+JSONObject.toJSONString(redisOrderTrade));
                                    throwMchtNotifyInfo(BIZ, platOrderId, payType, redisOrderTrade, count);
                                }
                            }.start();
                        }
                    }
                }
            } else {
                logger.info(BIZ + platOrderId + "，bossTrade处理上游通道异步通知请求失败." + JSON.toJSONString(tradeResult));
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error(BIZ+platOrderId+"接收上游通道异步通知请求异常："+e.getMessage());
        }
        logger.info(BIZ+platOrderId+"，接收上游通道异步通知接口data数据 [END],返回通道响应信息为: "+resp2chan);
        return resp2chan;
    }

    /**
     * 补抛商户异步通知流水
     * @param BIZ
     * @param platOrderId
     * @param payType
     * @param redisOrderTrade
     * @param count  补抛次数
     */
    private void throwMchtNotifyInfo(String BIZ, String platOrderId,String payType, CacheTrade redisOrderTrade, int count) {
        //总共补抛四次
        int totalCount = 4;
        try{
            if(count > totalCount){
                logger.info(BIZ+platOrderId+"，当前补抛次数是"+count+",补抛次数已经超过"+totalCount+"次，不再对商户异步通知进行补抛");
                return;
            }
            switch (count){
                case 1 :
                    logger.info(BIZ + platOrderId +"，异步通知商户失败，开始执行补抛,当前是第"+count+"次补抛,payType="+payType+"，CacheTrade="+JSONObject.toJSONString(redisOrderTrade));
                    break;
                case 2 :
                    Thread.sleep(60000);
                    logger.info(BIZ + platOrderId +"，异步通知商户失败，开始执行补抛,当前是第"+count+"次补抛,payType="+payType+"，CacheTrade="+JSONObject.toJSONString(redisOrderTrade));
                    break;
                case 3 :
                    Thread.sleep(60000);
                    logger.info(BIZ + platOrderId +"，异步通知商户失败，开始执行补抛,当前是第"+count+"次补抛,payType="+payType+"，CacheTrade="+JSONObject.toJSONString(redisOrderTrade));
                    break;
                case 4 :
                    Thread.sleep(60000);
                    logger.info(BIZ + platOrderId +"，异步通知商户失败，开始执行补抛,当前是第"+count+"次补抛,payType="+payType+"，CacheTrade="+JSONObject.toJSONString(redisOrderTrade));
                    break;
            }
            CommonResult serviceResult = sendNotifyService.sendNotify(payType, redisOrderTrade);
            logger.info(BIZ + platOrderId +"，异步通知商户失败，执行补抛，当前是第"+count+"次补抛，补抛后sendNotifyService返回的CommonResult="+JSONObject.toJSONString(serviceResult));

            if (ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())) {
                logger.info(BIZ + platOrderId +"，异步通知商户失败，执行补抛，当前是第"+count+"次补抛，补抛结果为：通知商户成功");
                return;
            } else {
                logger.info(BIZ + platOrderId +"，异步通知商户失败，执行补抛，当前是第"+count+"次补抛，补抛结果为：通知商户失败");
                throwMchtNotifyInfo(BIZ, platOrderId, payType, redisOrderTrade, count+1);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info(BIZ+platOrderId+"，异步通知商户失败，执行补抛，当前是第"+count+"次补抛，抛异常，Exception="+e.getMessage());
            if(count <= totalCount){
                throwMchtNotifyInfo(BIZ, platOrderId, payType, redisOrderTrade, count+1);
            }
        }
    }


    /**
     * 接受统一异步通知结果
     * param参数值
     */
    @RequestMapping("/recNotify/param/{chanCode}/{platOrderId}/{payType}")
    @ResponseBody
    public String recNotifyParam(HttpServletRequest request, HttpServletResponse response, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws TransException {
        String resp2chan = "FAILURE";
        try {
            TreeMap<String, String> dataMap = new TreeMap<String, String>();
//            Enumeration<?> temp = request.getParameterNames();
//            if (null != temp) {
//                while (temp.hasMoreElements()) {
//                    String en = (String) temp.nextElement();
//                    String value = request.getParameter(en);
//                    dataMap.put(en, value);
//                }
//            }
            //修改为这种，可能有数组问题支付宝
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                dataMap.put(name, valueStr);
            }
            String data = JSON.toJSONString(dataMap);
            logger.info(BIZ+" parameter数据 [START] chanCode=" + chanCode + " platOrderId=" + platOrderId + " 异步通知原始报文data="+ data + "");
            logger.info(BIZ+"[START] data=" + data);
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
                    if(PayStatusEnum.PAY_SUCCESS.getCode().equals(redisOrderTrade.getCacheOrder().getStatus())){
                        CommonResult serviceResult = sendNotifyService.sendNotify(payType, redisOrderTrade);
                        if (ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())) {
                            logger.info(BIZ + platOrderId + "，通知商户成功");
                        } else {
                            logger.info(BIZ + platOrderId + "，通知商户失败");
                            //开启线程，异步通知商户,补抛机制,为了便于排查多线程问题，这里给线程指定名称
                            new Thread("Thread-name-"+platOrderId){
                                @Override
                                public void run() {
                                    int count = 1;//开始补抛
                                    logger.info(BIZ + platOrderId +"，异步通知商户失败，开始执行补抛,count="+count+",payType="+payType+"，CacheTrade="+JSONObject.toJSONString(redisOrderTrade));
                                    throwMchtNotifyInfo(BIZ, platOrderId, payType, redisOrderTrade, count);
                                }
                            }.start();
                        }
                    }
                }
            }else{
                logger.info(BIZ+platOrderId+"，bossTrade处理上游通道异步通知请求失败."+JSON.toJSONString(tradeResult));
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error(BIZ+platOrderId+"接收上游通道异步通知请求异常："+e.getMessage());
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
        try {
            if(StringUtils.isBlank(data)){
              logger.info("模拟商户接收异步通知，收到的data = null");
            }else{
                data = URLDecoder.decode(data,"utf-8");
                TradeNotifyResponse tradeNotifyResponse = JSON.parseObject(data, TradeNotifyResponse.class);
                mchtOrdertId = tradeNotifyResponse.getBody().getOrderId();
                String retSign = tradeNotifyResponse.getSign();
                    logger.info("商户订单号："+mchtOrdertId+"，模拟商户接收异步通知，对接收的tradeNotifyResponse="+JSONObject.toJSONString(tradeNotifyResponse));
                    TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(tradeNotifyResponse.getBody());
                    logger.info("商户订单号："+mchtOrdertId+"，模拟商户接收异步通知，对接收的数据签名，签名key="+mchtKey+"，签名treeMap="+ treeMap);
                    String log_moid = tradeNotifyResponse.getBody().getMchtId()+"-->"+mchtOrdertId;
                    String sign = SignUtil.md5Sign(new HashMap<String, String>(treeMap), mchtKey, log_moid);
                    logger.info("商户订单号："+mchtOrdertId+"，模拟商户接收异步通知，对接收的数据签名，签名结果sign="+ sign);

                if(sign.equals(retSign)){
                    ret = "SUCCESS";
                }
            }
        } catch (Exception e) {
             e.printStackTrace();
            logger.error("商户订单号："+mchtOrdertId+"模拟商户接收异步通知请求异常："+e.getMessage());
        }
        logger.info("商户订单号："+mchtOrdertId+"，模拟商户接收异步通知,返回的结果="+ ret);
        return ret;
    }


    /**
     * 上游配置异步通知地址-接受统一异步通知结果
     * 异步通知固定链接.不会按订单号动态生成对应的异步通知地址
     * data数据
     */
    @RequestMapping("/recNotify/data")
    @ResponseBody
    public String recNotify(HttpServletRequest httpServletRequest) throws TransException {
        String data = null;
        String resp2chan = "FAILURE";


        Map<String, String[]> paramMap = httpServletRequest.getParameterMap();
        if(paramMap == null || paramMap.size() == 0){
            logger.info("接收到固定异步通知链接信息,参数为空.");
            return resp2chan;
        }
        for(Map.Entry<String, String[]> map: paramMap.entrySet()){
            data = map.getKey();
            logger.info("接收到固定异步通知链接信息,参数为data:"+ data);
            break;
        }

        String platOrderNo =null;
        String sign =httpServletRequest.getHeader("X-QF-SIGN");
        try {
            data = URLDecoder.decode(data, "utf-8");
            logger.info("接收到固定异步通知链接信息,参数为:" + data);
            //解析并校验签名上游通道异步通知的数据
            CommonResult tradeResult = recNotifyService.reciveNotify(data,sign);
            if (ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode()) || ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())) {
                //解析通道数据成功,更新数据库订单状态成功
                //响应给上游通道的信息--不论是否通知下游商户成功，这里都会响应上游通道接收异步通知成功，因为能执行到此处，说明数据库已经是成功状态，不允许通道方补抛
                resp2chan = tradeResult.getRespMsg();
                //数据不为空，才会给商户通知
                if (null != tradeResult.getData()) {
                    //通知商户信息源
                    CacheTrade redisOrderTrade = (CacheTrade) tradeResult.getData();
                    String platOrderId=redisOrderTrade.getCacheOrder().getPlatOrderId();
                    String payType =redisOrderTrade.getCacheOrder().getPayType();
                    platOrderNo =platOrderId;
                    logger.info(BIZ + platOrderId + "，bossTrade查询的缓存订单Trade对象:" + JSON.toJSONString(redisOrderTrade));
                    if(PayStatusEnum.PAY_SUCCESS.getCode().equals(redisOrderTrade.getCacheOrder().getStatus())){
                        CommonResult serviceResult = sendNotifyService.sendNotify(payType, redisOrderTrade);
                        if (ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())) {
                            logger.info(BIZ + platOrderId + "，通知商户成功");
                        } else {
                            logger.info(BIZ + platOrderId + "，通知商户失败");
                            //开启线程，异步通知商户,补抛机制,为了便于排查多线程问题，这里给线程指定名称
                            new Thread("Thread-name-"+platOrderId){
                                @Override
                                public void run() {
                                    int count = 1;//开始补抛
                                    logger.info(BIZ + platOrderId +"，异步通知商户失败，开始执行补抛,count="+count+",payType="+payType+"，CacheTrade="+JSONObject.toJSONString(redisOrderTrade));
                                    throwMchtNotifyInfo(BIZ, platOrderId, payType, redisOrderTrade, count);
                                }
                            }.start();
                        }
                    }
                }
            } else {
                logger.info(BIZ + platOrderNo + "，bossTrade处理上游通道异步通知请求失败." + JSON.toJSONString(tradeResult));
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error(BIZ+platOrderNo+"接收上游通道异步通知请求异常："+e.getMessage());
        }
        logger.info(BIZ+platOrderNo+"，接收上游通道异步通知接口data数据 [END],返回通道响应信息为: "+resp2chan);
        return resp2chan;
    }

}
