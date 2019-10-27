package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.registe.TradeRegisteMchtRequest;
import com.sys.boss.api.entry.trade.response.registe.RegisteMchtResponse;
import com.sys.boss.api.service.trade.handler.ITradeRegisteMchtInfoHandler;
import com.sys.gateway.common.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * 商户入驻
 */

@Controller
@RequestMapping(value = "")
public class GwFtpController {

    protected final Logger logger = LoggerFactory.getLogger(GwFtpController.class);

    @Autowired
    ITradeRegisteMchtInfoHandler tradeRegisteMchtInfoHandler;

    private final String BIZ = "FTP服务-";


    @RequestMapping(value = "/gateway/ftp/test")
    @ResponseBody
    public String test(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {

        String respStr ;
        if (data.endsWith("=")) {
            logger.info(BIZ + "收到客户端请求参数,最后一个字母为=，需要截取掉，截取之前的值为data=" + data);
            data = data.substring(0, data.length() - 1);
        }
        logger.info(BIZ + "收到客户端请求参数：data=" + data);
        String midoid = "";//商户ID+商户订单ID
        RegisteMchtResponse registeMchtResponse = new RegisteMchtResponse();
        RegisteMchtResponse.RegisteMchtResponseHead head = new RegisteMchtResponse.RegisteMchtResponseHead();
        RegisteMchtResponse.RegisteMchtResponseBody body = new RegisteMchtResponse.RegisteMchtResponseBody();
        try {
            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info(BIZ + midoid + " 获取到客户端请求ip：" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info(BIZ + midoid + " 收到客户端请求参数后做url解码后的值为data：" + data);

            //解析请求参数
//            TradeRegisteMchtRequest tradeMchtRegisteRequest = JSON.parseObject(data, TradeRegisteMchtRequest.class);
//            midoid = tradeMchtRegisteRequest.getHead().getMchtId() + "-" + tradeMchtRegisteRequest.getBody().getOrderId();

            TradeRegisteMchtRequest tradeMchtRegisteRequest = new TradeRegisteMchtRequest();
            tradeMchtRegisteRequest.setSign(data);
            logger.info(BIZ + midoid + "请求trade层【start】参数值tradeRequest：" );
            CommonResult commonResult = tradeRegisteMchtInfoHandler.process(tradeMchtRegisteRequest, ip);
            logger.info(BIZ + midoid + "请求trade层【end】返回值commonResult：" + JSON.toJSONString(commonResult));
            respStr = (String) commonResult.getData();


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ + midoid + " 系统异常 e.msg：" + e.getMessage());
            respStr = "{\"head\":{\"respCode\":\"FT001\",\"respMsg\":\"入驻处理异常\"}}";
        }
        logger.info(BIZ + midoid + " 返回下游商户信息：" + respStr);
        return respStr;
    }



}

