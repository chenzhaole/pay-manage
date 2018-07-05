package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.trade.handler.ITradeDirectTransHandler;
import com.sys.common.enums.ErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 不做业务处理,无需路由直接调用通道层(for反扫)
 * Created by chenzhaole on 2018/7/3.
 */
@Controller
@RequestMapping("gateway/cashier")
public class GwDirectTradeController {
    private Logger logger = LoggerFactory.getLogger(GwDirectTradeController.class);

    private static final String BIZ = "网页支付GwDirectTradeController,付款码包装成网页支付发起请求->";

    @Autowired
    private ITradeDirectTransHandler tradeDirectTransHandler;

    /**
     * pc端收银台异步下单
     */
    @RequestMapping(value="/platBarcodeCall/{authCode}/{platOrderId}")
    @ResponseBody
    public String platBarcodeCall(HttpServletRequest request, @PathVariable String authCode, @PathVariable String platOrderId) {
        logger.info(BIZ+"页面提交的数据为，authCode："+authCode+",platOrderId："+platOrderId);
        //下单结果 1：请求不成功， 2：请求成功
        String midoid = "付款码包装成网页支付，提交条码信息操作，平台流水号：" + platOrderId +"，条码信息：" + authCode +"，-->";
        String status = "1";
        try {
            //为了防止商户提交的条码信息两边有空格，此处trim一下
            if(StringUtils.isBlank(authCode) || StringUtils.isBlank(platOrderId)){
                logger.info(BIZ+midoid+"页面提交的数据为空，authCode："+authCode+",platOrderId="+platOrderId+"，返回给页面的数据status："+status);
                return status;
            }
            authCode = authCode.trim();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("authCode", authCode);
            jsonObject.put("platOrderId", platOrderId);
            logger.info(BIZ+midoid+"付款码包装成网页支付，掉trade模块，传过去的数据："+jsonObject.toJSONString());
            CommonResult processResult = tradeDirectTransHandler.process(jsonObject.toJSONString());
            logger.info(BIZ+midoid+"付款码包装成网页支付，掉trade模块，响应回来的数据："+JSONObject.toJSONString(processResult));
            if(null != processResult && processResult.getRespCode().equals(ErrorCodeEnum.SUCCESS.getCode())){
                logger.info(BIZ+midoid+"付款码包装成网页支付，掉trade模块，处理业务为：成功");
                status = "2";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ+midoid+"接收付款码页面请求异常："+e.getMessage());
        }
        logger.info(BIZ+midoid+"付款码包装成网页支付，掉trade模块，处理业务后，最终返回给页面的status="+status);
        return status;
    }
}
