package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.RSAUtils;
import com.sys.gateway.common.ConfigUtil;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwSdkPayService;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 支付SDK创建订单接口
 */

@Controller
@RequestMapping(value = "")
public class GwSdkPayController {

    protected final Logger logger = LoggerFactory.getLogger(GwSdkPayController.class);

    private static final String PLAT_SDK_PRIVATE_KEY = ConfigUtil.getValue("plat_sdk_private_key");
    private static final String CLIENT_SDK_PUBLIC_KEY = ConfigUtil.getValue("client_sdk_public_key");

    private final String BIZ_NAME = "支付SDK-创建支付订单-";
    @Autowired
    GwSdkPayService gwSdkPayService;

    /**
     * 支付
     **/
    @RequestMapping(value = "/gateway/sdk/commPay")
    @ResponseBody
    public String commPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        Map rtnMap = new HashMap();
        String rtnStr = "";
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info(BIZ_NAME + "客户端请求ip：" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info(BIZ_NAME + "客户端请求参数做url解码后的值为：" + data);
            String srcData = RSAUtils.decrypt(data, PLAT_SDK_PRIVATE_KEY);
            logger.info(BIZ_NAME + "客户端请求参数RSA解密后的值为：" + srcData);

            //校验请求参数
            CommonResponse checkResp = gwSdkPayService.checkParam(srcData);
            logger.info(BIZ_NAME + "校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));
            if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                rtnMap.put("code", checkResp.getRespCode());
                rtnMap.put("msg", checkResp.getRespMsg());
            } else {
                Map reqData = (Map) checkResp.getData();
                logger.info(BIZ_NAME + "传入的信息：" + JSONObject.toJSONString(reqData));
                rtnMap = gwSdkPayService.pay(reqData, ip);
                logger.info(BIZ_NAME + "返回信息：" + JSONObject.toJSONString(rtnMap));
            }

            String rtnSrcStr = JSON.toJSONString(rtnMap);
            logger.info(BIZ_NAME + "返回下游商户原始字符串：" + rtnSrcStr);
            rtnStr = RSAUtils.encrypt(rtnSrcStr, CLIENT_SDK_PUBLIC_KEY);
            logger.info(BIZ_NAME + "返回下游商户RSA加密后字符串：" + rtnStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ_NAME + "异常" + e.getMessage());
            rtnMap.put("code", ErrorCodeEnum.FAILURE.getCode());
            rtnMap.put("msg", ErrorCodeEnum.FAILURE.getDesc());
        }

        return rtnStr;
    }

}
