package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.trade.handler.ITradeCashierPlatHandler;
import com.sys.common.enums.*;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.NumberUtils;
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
import org.springframework.web.bind.annotation.RequestMethod;
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
            String bankCode =request.getParameter("bankCode");
            if(PayTypeEnum.LOCAL_BANK.getCode().equals(bankCode) && StringUtils.isEmpty(bankCode)){
                logger.info(BIZ+midoid+"银行编码为空");
                result.setRespCode(ErrorCodeEnum.E6110.getCode());
                result.setRespMsg("操作失败");
                return JSONObject.toJSONString(result);
            }
            //通过程序判断，由于不同的设备类型对应页面不一样，且掉支付的方式也不一样，所以会根据设备类型来做判断
            logger.info(BIZ + midoid + "通过程序获取deviceType的值");
            //根据userAgent判断设备类型：pc、手机端、微信内(针对公众号支付)
            String userAgent = getUserAgentInfoByRequest(request, midoid);
            logger.info(BIZ + midoid + "根据请求头获取的user-agent为：" + userAgent);
            deviceType = HttpUtil.getDeviceType(userAgent);
            logger.info(BIZ + midoid + "通过程序判断deviceType为：" + deviceType + "-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            if(StringUtils.isNotBlank(mchtId) && StringUtils.isNotBlank(mchtOrderId) && StringUtils.isNotBlank(paymentType) && StringUtils.isNotBlank(extraData)) {
                String ip = IpUtil.getRemoteHost(request);
                logger.info(BIZ + midoid + "通过程序获取的ip为：" + ip);
                //调用handler处理业务
                logger.info(BIZ + midoid + "调用ITradeCashierPlatHandler处理业务逻辑，传入的请求参数是mchtId=" + mchtId+", mchtOrderId="+mchtOrderId+", paymentType="+paymentType+", extraData="+extraData+", deviceType="+deviceType+", ip="+ip);
                result = iTradeCashierPlatHandler.process(mchtId, mchtOrderId, paymentType, extraData, deviceType, ip,bankCode);
                logger.info(BIZ + midoid + "调用ITradeCashierPlatHandler处理业务逻辑，传入的请求参数是mchtId=" + mchtId+", mchtOrderId="+mchtOrderId+", paymentType="+paymentType+", extraData="+extraData+", deviceType="+deviceType+", ip="+ip +"，返回的数据为：" + JSONObject.toJSONString(result));
                if (null != result && ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode()) && null != result.getData()) {
                    Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
                    Result resultInfo = (Result) retMapInfo.get("result");
                    Map<String, String> mapQQandMobile = (Map<String, String>)retMapInfo.get("pageQQandMobile");
                    //pc端是异步下单
                    String pcAsynScanInfo = "";
                    if(isUseChanCashierPage(result.getData(), midoid)){
                        //判断是否直接跳向上游收银台
                        pcAsynScanInfo = returnChanCasgierPageInfo(resultInfo, midoid);
                    }else{
                        if(StringUtils.isNotBlank(resultInfo.getPaymentType()) && resultInfo.getPaymentType().endsWith(BARCODE_NUM)){
                            //支付类型枚举类中定义的条码支付值的后三位是501
                            //拼接地址，将参数拼接在url之后
                            pcAsynScanInfo = returnBarcodeUrlInfo(resultInfo, midoid);
                        }else{
                            //使用我司页面
                            pcAsynScanInfo = returnPcPageInfo(resultInfo, midoid);
                        }
                    }
                    result.setData(pcAsynScanInfo);
                }else{
                    if(result == null){
                        result.setRespCode(ErrorCodeEnum.E6110.getCode());
                        result.setRespMsg("操作失败");
                    }
                    logger.info(BIZ+midoid+"调用TradeCashierPlatHandler处理业务逻辑，处理结果为失败，返回的CommonResult="+JSONObject.toJSONString(result));
                }
            }else{
                //请求参数不能为空
                result.setRespCode(ErrorCodeEnum.E1017.getCode());
                result.setRespMsg("操作失败");
                logger.info(BIZ+midoid+"接收收银台请求，解析请求参数失败，存在未传的参数，返回的CommonResult="+JSONObject.toJSONString(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setRespCode(ErrorCodeEnum.E8001.getCode());
            result.setRespMsg("操作失败");
            logger.error(BIZ+midoid+"接收收银台页面请求异常："+e.getMessage());
        }
        String resultJson = JSONObject.toJSONString(result);
        logger.info(BIZ+midoid+"处理完业务之后，返回给页面的数据为："+ resultJson);
        return resultJson;
    }

    /**
     * pc端收银台异步下单付款码支付，页面跳转中转站
     */
    @RequestMapping(value="/platPcBarcode/{platOrderId}/{mchtOrderId}/{paymentType}/{amount}", method = RequestMethod.GET)
    public String platPcBarcode(HttpServletRequest request, Model model, @PathVariable String platOrderId, @PathVariable String mchtOrderId, @PathVariable String paymentType, @PathVariable String amount) {
        model.addAttribute("platOrderId", platOrderId);
        model.addAttribute("mchtOrderId", mchtOrderId);
        model.addAttribute("payType", paymentType);
        model.addAttribute("paymentType", paymentType.substring(0, 2));
        amount = NumberUtils.changeF2Y(amount);
        model.addAttribute("amount", amount);
        String page = "modules/cashier/barcode/barcode";
        return page;
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
            String bankCode =request.getParameter("bankCode");
            if(PayTypeEnum.LOCAL_BANK.getCode().equals(bankCode) && StringUtils.isEmpty(bankCode)){
                logger.info(BIZ+midoid+"银行编码为空");
                result.setRespCode(ErrorCodeEnum.E6110.getCode());
                result.setRespMsg("操作失败");
                return JSONObject.toJSONString(result);
            }
            //通过程序判断，由于不同的设备类型对应页面不一样，且掉支付的方式也不一样，所以会根据设备类型来做判断
            logger.info(BIZ + midoid + "通过程序获取deviceType的值");
            //根据userAgent判断设备类型：pc、手机端、微信内(针对公众号支付)
            String userAgent = getUserAgentInfoByRequest(request, midoid);
            logger.info(BIZ + midoid + "根据请求头获取的user-agent为：" + userAgent);
            deviceType = HttpUtil.getDeviceType(userAgent);
            logger.info(BIZ + midoid + "通过程序判断deviceType为：" + deviceType + "-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            if(StringUtils.isNotBlank(mchtId) && StringUtils.isNotBlank(mchtOrderId) && StringUtils.isNotBlank(paymentType) && StringUtils.isNotBlank(extraData)) {
                String ip = IpUtil.getRemoteHost(request);
                logger.info(BIZ + midoid + "通过程序获取的ip为：" + ip);
                //调用handler处理业务
                logger.info(BIZ + midoid + "调用ITradeCashierPlatHandler处理业务逻辑，传入的请求参数是mchtId=" + mchtId+", mchtOrderId="+mchtOrderId+", paymentType="+paymentType+", extraData="+extraData+", deviceType="+deviceType+", ip="+ip);
                result = iTradeCashierPlatHandler.process(mchtId, mchtOrderId, paymentType, extraData, deviceType, ip,bankCode);
                logger.info(BIZ + midoid + "调用ITradeCashierPlatHandler处理业务逻辑，传入的请求参数是mchtId=" + mchtId+", mchtOrderId="+mchtOrderId+", paymentType="+paymentType+", extraData="+extraData+", deviceType="+deviceType+", ip="+ip +"，返回的数据为：" + JSONObject.toJSONString(result));
                if (null != result && ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode()) && null != result.getData()) {
                    Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
                    Result resultInfo = (Result) retMapInfo.get("result");
                    String payType = resultInfo.getPaymentType();
                    //先判断是否跳转上游收银台
                    if(isUseChanCashierPage(result.getData(), midoid)){
                        //跳转到上游收银台的中转页面
                        page = "modules/chanCashier/chanCashier";
                        //跳转到上游收银台的中转页面，携带的数据
                        logger.info(BIZ + midoid + "调用TradeCashierPlatHandler处理业务逻辑，处理结果为成功，需要使用上游收银台的中转页面，返回的CommonResult=" + JSONObject.toJSONString(result) + "跳转的页面为：" + page);
                        this.addChanCashierModelInfo(model, result, midoid);
                    }else {
                        //非收银台页面跳转
                        page = this.chooseNotCashierPage(deviceType, payType, midoid);
                        if (page.endsWith("scan")) {
                            //设置扫码中间页需要的参数
                            this.addScanCentPageModelInfo(model, result, midoid);
                            logger.info(BIZ + midoid + "调用TradeCashierPlatHandler处理业务逻辑，处理结果为成功，需要使用中间页，返回的CommonResult=" + JSONObject.toJSONString(result) + "跳转的页面为：" + page);
                        } else if (page.endsWith("center")) {
                            //设置h5和公众号支付的中间页需要的参数
                            this.addH5CentPageModelInfo(model, result, userAgent, midoid);
                            logger.info(BIZ + midoid + "调用TradeCashierPlatHandler处理业务逻辑，处理结果为成功，需要使用中间页，返回的CommonResult=" + JSONObject.toJSONString(result) + "跳转的页面为：" + page);
                        }else if(page.endsWith("barcode")){
                            //设置付款码中间页需要的参数
                            this.addBarcodeCentPageModelInfo(model, result, midoid);
                            logger.info(BIZ+midoid+"调用TradeCashierPlatHandler处理业务逻辑，处理结果为成功，需要使用中间页，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);
                        } else {
                            result.setRespCode(ErrorCodeEnum.E1018.getCode());
                            result.setRespMsg("操作失败");
                            page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode(), midoid);
                            logger.info(BIZ + midoid + "调用TradeCashierPlatHandler处理业务逻辑，处理结果为成功，需要使用中间页，但是未找到对应的中间页，返回的CommonResult=" + JSONObject.toJSONString(result) + "跳转的页面为：" + page);
                        }
                    }
                }else{
                    page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode(), midoid);
                    //客服信息
                    if(result != null && null != result.getData() &&  (result.getData() instanceof Map)){
                        Map mapData = (Map) result.getData();
                        Map<String, String> pageQQandMobile = (Map<String, String>) mapData.get("pageQQandMobile");
                        qq = pageQQandMobile.get("qq");
                        mobile = pageQQandMobile.get("mobile");
                    }else{
                        result.setRespCode(ErrorCodeEnum.E8001.getCode());
                        result.setRespMsg("操作失败");
                    }
                    logger.info(BIZ+midoid+"调用TradeCashierPlatHandler处理业务逻辑，处理结果为失败，返回的CommonResult="+JSONObject.toJSONString(result));
                }
            }else{
                //请求参数不能为空
                result.setRespCode(ErrorCodeEnum.E1017.getCode());
                result.setRespMsg("操作失败");
                page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode(), midoid);
                logger.info(BIZ+midoid+"接收收银台请求，解析请求参数失败，存在未传的参数，返回的CommonResult="+JSONObject.toJSONString(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setRespCode(ErrorCodeEnum.E8001.getCode());
            result.setRespMsg("操作失败");
            page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode(), midoid);
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
