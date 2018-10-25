package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.web.BaseController;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.trade.handler.ITradeApiRechargePayHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.Order;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TransException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.TreeMap;

@Controller
@RequestMapping("/rechargeNotify")
public class MchtRechargeNotifyController extends BaseController {

    @Autowired
    private ITradeApiRechargePayHandler tradeApiRechargePayHandler;


    private final String BIZ = "接收异步通知GwRecNotifyController->platOrderId：";

    @RequestMapping("/recNotify/data/{chanCode}/{platOrderId}/{payType}")
    @ResponseBody
    public String recNotifyData(@RequestBody String data, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws TransException {
        logger.info(" data数据 [START] chanCode=" + chanCode + " platOrderId=" + platOrderId + " 异步通知原始报文data="+ data + "");

        String resp2chan = "FAILURE";
        try {
            data = URLDecoder.decode(data, "utf-8");
            //解析并校验签名上游通道异步通知的数据
            CommonResult tradeResult = reciveNotify(chanCode, platOrderId, payType, data);
            if (ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode()) || ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())) {
                //解析通道数据成功,更新数据库订单状态成功
                //响应给上游通道的信息--不论是否通知下游商户成功，这里都会响应上游通道接收异步通知成功，因为能执行到此处，说明数据库已经是成功状态，不允许通道方补抛
                resp2chan = tradeResult.getRespMsg();

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
     * 接受统一异步通知结果
     * param参数值
     */
    @RequestMapping("/recNotify/param/{chanCode}/{platOrderId}/{payType}")
    @ResponseBody
    public String recNotifyParam(HttpServletRequest request, HttpServletResponse response, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws TransException {
        String resp2chan = "FAILURE";
        try {
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
            //解析并校验签名上游通道异步通知的数据
            CommonResult tradeResult = reciveNotify(chanCode, platOrderId, payType, data);
            if(ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode())|| ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())){
                //解析通道数据成功,更新数据库订单状态成功
                //响应给上游通道的信息--不论是否通知下游商户成功，这里都会响应上游通道接收异步通知成功，因为能执行到此处，说明数据库已经是成功状态，不允许通道方补抛
                resp2chan = tradeResult.getRespMsg();

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


    public CommonResult reciveNotify(String channelCode, String orderNo, String payType, String data) {
        //封装异步通知url中带来的参数
        Trade trade = new Trade();
        Config config = new Config();
        config.setChannelCode(channelCode);
        config.setPayType(payType);
        Order order = new Order();
        order.setOrderNo(orderNo);

        trade.setConfig(config);
        trade.setOrder(order);
        //异步通知原始报文
        trade.setData(data);
        logger.info("orderNo="+orderNo+"，封装通知参数trade="+ JSONObject.toJSONString(trade));
        //调用boss-trade获取缓存中的orderTrade
        CommonResult commonResult = tradeApiRechargePayHandler.processNotify(trade);
        logger.info("orderNo="+orderNo+"，处理业务逻辑后返回的CommonResult="+ JSONObject.toJSONString(commonResult));

        return commonResult;
    }
}
