package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.trade.handler.ITradeCashierPlatHandler;
import com.sys.common.enums.*;
import com.sys.common.util.HttpUtil;
import com.sys.gateway.common.IpUtil;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 第三方支付网页版sdk--接收收银台下单的请求
 */
@Controller
@RequestMapping("gateway/cashier")
public class GwCashierPlatController extends GwCashierBaseController {
    private Logger logger = LoggerFactory.getLogger(GwCashierPlatController.class);

    @Autowired
    private ITradeCashierPlatHandler iTradeCashierPlatHandler;

    private static final String BIZ = "网页支付GwCashierPlatController,收银台发起请求->";

    /**
     * pc端收银台异步下单
     */
    @RequestMapping(value="/platPcCall/{mchtId}/{mchtOrderId}/{paymentType}/{extraData}")
    @ResponseBody
    public String platPcCall(HttpServletRequest request, @PathVariable String mchtId, @PathVariable String mchtOrderId, @PathVariable String paymentType, @PathVariable String extraData){
        CommonResult result = new CommonResult();
        result.setRespCode(ErrorCodeEnum.FAILURE.getCode());
        result.setRespMsg(ErrorCodeEnum.FAILURE.getDesc());
        //设备类型,默认pc
        String deviceType = DeviceTypeEnum.PC.getCode();
       //打印日志使用，拼接支付类型，商户订单号，
        String midoid = "";
        try {
            midoid = "pc端收银台异步下单，商户号：" + mchtId +"，商户订单号：" + mchtOrderId + ",随机数extraData：" + extraData + "-->支付类型：" + paymentType + "-->";
            //通过程序判断，由于不同的设备类型对应页面不一样，且掉支付的方式也不一样，所以会根据设备类型来做判断
            logger.info(BIZ + midoid + "通过程序获取deviceType的值");
            //根据userAgent判断设备类型：pc、手机端、微信内(针对公众号支付)
            String userAgent = getUserAgentInfoByRequest(request);
            logger.info(BIZ + midoid + "根据请求头获取的user-agent为：" + userAgent);
            deviceType = HttpUtil.getDeviceType(userAgent);
            logger.info(BIZ + midoid + "通过程序判断deviceType为：" + deviceType + "-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            if(StringUtils.isNotBlank(mchtId) && StringUtils.isNotBlank(mchtOrderId) && StringUtils.isNotBlank(paymentType) && StringUtils.isNotBlank(extraData)) {
                String ip = IpUtil.getRemoteHost(request);
                logger.info(BIZ + midoid + "通过程序获取的ip为：" + ip);
                //调用handler处理业务
                logger.info(BIZ + midoid + "调用ITradeCashierPlatHandler处理业务逻辑，传入的请求参数是mchtId=" + mchtId+", mchtOrderId="+mchtOrderId+", paymentType="+paymentType+", extraData="+extraData+", deviceType="+deviceType+", ip="+ip);
                result = iTradeCashierPlatHandler.process(mchtId, mchtOrderId, paymentType, extraData, deviceType, ip);
                logger.info(BIZ + midoid + "调用ITradeCashierPlatHandler处理业务逻辑，传入的请求参数是mchtId=" + mchtId+", mchtOrderId="+mchtOrderId+", paymentType="+paymentType+", extraData="+extraData+", deviceType="+deviceType+", ip="+ip +"，返回的数据为：" + JSONObject.toJSONString(result));
                if (null != result && ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode()) && null != result.getData()) {
                    Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
                    Result resultInfo = (Result) retMapInfo.get("result");
                    //pc端是异步下单
                    String pcAsynScanInfo = returnPcPageInfo(resultInfo);
                    result.setData(pcAsynScanInfo);
                }else{
                    if(result == null){
                        result.setRespCode(ErrorCodeEnum.E6110.getCode());
                        result.setRespMsg(ErrorCodeEnum.E6110.getDesc());
                    }
                    logger.info(BIZ+midoid+"调用TradeCashierPlatHandler处理业务逻辑，处理结果为失败，返回的CommonResult="+JSONObject.toJSONString(result));
                }
            }else{
                //请求参数不能为空
                result.setRespCode(ErrorCodeEnum.E1017.getCode());
                result.setRespMsg(ErrorCodeEnum.E1017.getDesc());
                logger.info(BIZ+midoid+"接收收银台请求，解析请求参数失败，存在未传的参数，返回的CommonResult="+JSONObject.toJSONString(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setRespCode(ErrorCodeEnum.E8001.getCode());
            result.setRespMsg(ErrorCodeEnum.E8001.getDesc());
            logger.error(BIZ+midoid+"接收收银台页面请求异常："+e.getMessage());
        }
        String resultJson = JSONObject.toJSONString(result);
        logger.info(BIZ+midoid+"处理完业务之后，返回给页面的数据为："+ resultJson);
        return resultJson;
    }


    /**
     * 手机端收银台发起支付
     */
    @RequestMapping(value="/platMobileCall/{mchtId}/{mchtOrderId}/{paymentType}/{extraData}")
    public String platMobileCall(HttpServletRequest request, Model model, @PathVariable String mchtId, @PathVariable String mchtOrderId, @PathVariable String paymentType, @PathVariable String extraData){
        CommonResult result = new CommonResult();
        result.setRespCode(ErrorCodeEnum.FAILURE.getCode());
        result.setRespMsg(ErrorCodeEnum.FAILURE.getDesc());
        //设备类型,默认pc
        String deviceType = DeviceTypeEnum.PC.getCode();
        //跳转页面
        String page = "";
        //打印日志使用，拼接支付类型，商户订单号，
        String midoid = "";
        //客服信息
        String qq = "";
        String mobile = "";
        try {
            midoid = "手机端收银台发起支付, 商户号：" + mchtId +"，商户订单号：" + mchtOrderId + ",随机数extraData：" + extraData + "-->支付类型：" + paymentType + "-->";
            //通过程序判断，由于不同的设备类型对应页面不一样，且掉支付的方式也不一样，所以会根据设备类型来做判断
            logger.info(BIZ + midoid + "通过程序获取deviceType的值");
            //根据userAgent判断设备类型：pc、手机端、微信内(针对公众号支付)
            String userAgent = getUserAgentInfoByRequest(request);
            logger.info(BIZ + midoid + "根据请求头获取的user-agent为：" + userAgent);
            deviceType = HttpUtil.getDeviceType(userAgent);
            logger.info(BIZ + midoid + "通过程序判断deviceType为：" + deviceType + "-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            if(StringUtils.isNotBlank(mchtId) && StringUtils.isNotBlank(mchtOrderId) && StringUtils.isNotBlank(paymentType) && StringUtils.isNotBlank(extraData)) {
                String ip = IpUtil.getRemoteHost(request);
                logger.info(BIZ + midoid + "通过程序获取的ip为：" + ip);
                //调用handler处理业务
                logger.info(BIZ + midoid + "调用ITradeCashierPlatHandler处理业务逻辑，传入的请求参数是mchtId=" + mchtId+", mchtOrderId="+mchtOrderId+", paymentType="+paymentType+", extraData="+extraData+", deviceType="+deviceType+", ip="+ip);
                result = iTradeCashierPlatHandler.process(mchtId, mchtOrderId, paymentType, extraData, deviceType, ip);
                logger.info(BIZ + midoid + "调用ITradeCashierPlatHandler处理业务逻辑，传入的请求参数是mchtId=" + mchtId+", mchtOrderId="+mchtOrderId+", paymentType="+paymentType+", extraData="+extraData+", deviceType="+deviceType+", ip="+ip +"，返回的数据为：" + JSONObject.toJSONString(result));
                if (null != result && ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode()) && null != result.getData()) {
                    Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
                    Result resultInfo = (Result) retMapInfo.get("result");
                    String biz = resultInfo.getPaymentType();
                    //非收银台页面跳转
                    page = this.chooseNotCashierPage(deviceType, biz);
                    if(page.endsWith("scan")){
                        //设置扫码中间页需要的参数
                        this.addScanCentPageModelInfo(model, result);
                    }else if(page.endsWith("center")){
                        //设置h5和公众号支付的中间页需要的参数
                        this.addH5CentPageModelInfo(model, result, userAgent);
                    }
                    logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用中间页，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);

                }else{
                    page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
                    //客服信息
                    if(result != null && null != result.getData() &&  (result.getData() instanceof Map)){
                        Map mapData = (Map) result.getData();
                        Map<String, String> pageQQandMobile = (Map<String, String>) mapData.get("pageQQandMobile");
                        qq = pageQQandMobile.get("qq");
                        mobile = pageQQandMobile.get("mobile");
                    }else{
                        result.setRespCode(ErrorCodeEnum.E8001.getCode());
                        result.setRespMsg(ErrorCodeEnum.E8001.getDesc());
                    }
                    logger.info(BIZ+midoid+"调用TradeCashierPlatHandler处理业务逻辑，处理结果为失败，返回的CommonResult="+JSONObject.toJSONString(result));
                }
            }else{
                //请求参数不能为空
                result.setRespCode(ErrorCodeEnum.E1017.getCode());
                result.setRespMsg(ErrorCodeEnum.E1017.getDesc());
                page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
                logger.info(BIZ+midoid+"接收收银台请求，解析请求参数失败，存在未传的参数，返回的CommonResult="+JSONObject.toJSONString(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setRespCode(ErrorCodeEnum.E8001.getCode());
            result.setRespMsg(ErrorCodeEnum.E8001.getDesc());
            page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
            logger.error(BIZ+midoid+"接收收银台页面请求异常："+e.getMessage());
        }
        if(StringUtils.isNotBlank(qq)){
            model.addAttribute("qq", qq);
        }
        if(StringUtils.isNotBlank(mobile)){
            model.addAttribute("mobile", mobile);
        }
        model.addAttribute("respCode",result.getRespCode());
        model.addAttribute("respMsg",result.getRespMsg());
        logger.info(BIZ+midoid+"处理完业务之后，返回给页面的数据为："+JSONObject.toJSONString(model));
        return page;
    }
}
