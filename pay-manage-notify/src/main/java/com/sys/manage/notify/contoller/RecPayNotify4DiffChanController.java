package com.sys.manage.notify.contoller;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.manage.notify.service.GwRecNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

/**
 * Created by chenzhaole on 2019/6/23.
 *
 * 不同上游通道订制接收方式
 */

@Service
public class RecPayNotify4DiffChanController {
    protected final Logger logger = LoggerFactory.getLogger(RecPayNotify4DiffChanController.class);
    private GwRecNotifyService recNotifyService;
    private final String BIZ = "接收异步通知GwRecNotifyController->platOrderId：";
    /**
     * for钱方好近
     *
     * 上游配置异步通知地址-接受统一异步通知结果
     * 异步通知固定链接.不会按订单号动态生成对应的异步通知地址
     * data数据
     */
    @RequestMapping("/recNotify/qfData")
    @ResponseBody
    public String recNotifyQfData(@RequestBody String data, HttpServletRequest httpServletRequest) throws Exception {
        String resp2chan = "FAILURE";
        String platOrderNo =null;
        String sign =httpServletRequest.getHeader("X-QF-SIGN");
        try {
            data = URLDecoder.decode(data, "utf-8");
            logger.info("钱方接收异步通知固定地址收到数据:" + data);
            //解析并校验签名上游通道异步通知的数据
            CommonResult tradeResult = recNotifyService.reciveNotify(data,sign);

            //add by 3310 20190517
            String respCode = tradeResult.getRespCode();
            logger.info("钱方接收异步通知固定地址 service层处理结果respCode:"+respCode);
            if(ErrorCodeEnum.E8003.getCode().equalsIgnoreCase(respCode)){
                //支付流水已成功,重复通知,直接返回
                logger.info("钱方接收异步通知固定地址,平台判断订单已成功,直接返回:SUCCESS");
                return "SUCCESS";
            }

            if (ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode()) || ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())) {
                //解析通道数据成功,更新数据库订单状态成功
                //响应给上游通道的信息--不论是否通知下游商户成功，这里都会响应上游通道接收异步通知成功，因为能执行到此处，说明数据库已经是成功状态，不允许通道方补抛
                resp2chan = "SUCCESS";//tradeResult.getRespMsg();
                //数据不为空，才会给商户通知
                if (null != tradeResult.getData()) {
                    //通知商户信息源
                    CacheTrade redisOrderTrade = (CacheTrade) tradeResult.getData();
                    String platOrderId=redisOrderTrade.getCacheOrder().getPlatOrderId();
                    String payType =redisOrderTrade.getCacheOrder().getPayType();
                    platOrderNo =platOrderId;
                    logger.info(BIZ + platOrderId + "，bossTrade查询的缓存订单Trade对象:" + JSON.toJSONString(redisOrderTrade));
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
