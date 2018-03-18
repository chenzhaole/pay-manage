package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.service.GwRecNotifyService;
import com.sys.gateway.service.GwSendNotifyService;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TransException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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


    private final String BIZ = "接收异步通知-";

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
            Trade redisOrderTrade = (Trade) tradeResult.getData();
            logger.info("bossTrade查询的缓存订单Trade对象:"+JSON.toJSONString(redisOrderTrade));

            CommonResult serviceResult = sendNotifyService.sendNotify(payType,redisOrderTrade);
            if(ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())){
                logger.info("通知商户成功");
                //存库成功,通知商户成功,最终猜响应给通道成功
                resp2chan = tradeResult.getRespMsg();//TODO:按时使用该字段存储返回通道的响应值
            }else{
                logger.info("通知商户失败");
            }

        }else{
            logger.info("bossTrade查询的缓存订单Trade对象,失败."+JSON.toJSONString(tradeResult));
        }


        logger.info("接收上游通道异步通知接口-END,返回通道响应: "+resp2chan);
        return resp2chan;
    }




}
