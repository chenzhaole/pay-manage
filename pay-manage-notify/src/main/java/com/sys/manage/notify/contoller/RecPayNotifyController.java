package com.sys.manage.notify.contoller;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.manage.notify.service.GwRecNotifyService;
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
 */
@Controller
@RequestMapping(value = "")
public class RecPayNotifyController {

    protected final Logger logger = LoggerFactory.getLogger(RecPayNotify4DiffChanController.class);

    private final String BIZ = "接收异步通知GwRecNotifyController->platOrderId：";

    @Autowired
    private GwRecNotifyService recNotifyService;


    /**
     * 接受统一异步通知结果
     * data数据
     */
    @RequestMapping("/recNotify/data/{chanCode}/{platOrderId}/{payType}")
    @ResponseBody
    public String recNotifyData(@RequestBody String data, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws Exception {
        logger.info(BIZ+" data数据 [START] chanCode=" + chanCode + " platOrderId=" + platOrderId + " 异步通知原始报文data="+ data + "");

        String resp2chan = "FAILURE";
        try {
            data = URLDecoder.decode(data, "utf-8");
            //解析并校验签名上游通道异步通知的数据
            CommonResult tradeResult = recNotifyService.reciveNotify(chanCode, platOrderId, payType, data);

            if(ErrorCodeEnum.E8003.getCode().equals(tradeResult.getRespCode())){
                resp2chan = tradeResult.getRespMsg();
                logger.info(BIZ+platOrderId+"，该笔订单平台已成功状态,返回通道响应信息为: "+resp2chan);
                return resp2chan;
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
    public String recNotifyParam(HttpServletRequest request, HttpServletResponse response, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws Exception {
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

        }catch (Exception e){
            e.printStackTrace();
            logger.error(BIZ+platOrderId+"接收上游通道异步通知请求异常："+e.getMessage());
        }
        logger.info(BIZ+platOrderId+"，接收上游通道异步通知接口parameter数据 [END],返回通道响应: "+resp2chan);
        return resp2chan;
    }







}
