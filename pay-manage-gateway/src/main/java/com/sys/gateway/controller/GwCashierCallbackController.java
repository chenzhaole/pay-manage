package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.cashier.TradeCashierRequest;
import com.sys.boss.api.service.trade.handler.ITradeCashierCallbackHandler;
import com.sys.boss.api.service.trade.handler.ITradeCashierMchtHandler;
import com.sys.common.enums.*;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.SignUtil;
import com.sys.gateway.common.ConfigUtil;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwCashierMchtService;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方支付网页版sdk--接收前台页面回调callBack请求
 */
@Controller
@RequestMapping("gateway/cashier")
public class GwCashierCallbackController {
    private Logger logger = LoggerFactory.getLogger(GwCashierCallbackController.class);

    @Autowired
    private ITradeCashierCallbackHandler tradeCashierCallbackHandler;
    private static final String BIZ = "网页支付GwCashierCallbackController->";



    /**
     * 接收上游callback请求
     */
    @RequestMapping(value="/chanCallBack/{platOrderId}/{payType}")
    public String chanCallBack(HttpServletRequest request, @PathVariable String platOrderId, @PathVariable String payType, Model model) throws Exception {
        CommonResult result = new CommonResult();
        //设备类型,默认pc
        String deviceType = DeviceTypeEnum.PC.getCode();
        //跳转页面
        String page = "";
        //打印日志使用，拼接商户号，支付类型，商户订单号，
        String midoid = "";
        try {
            midoid = "支付类型payType："+payType+"-->平台订单号："+platOrderId+"-->";
            //设备类型 如果商户没有传，就由我们自己来通过程序判断，由于不同的设备类型对应页面不一样，且掉支付的方式也不一样，所以会根据设备类型来做判断
            deviceType = request.getParameter("deviceType");
            logger.info(BIZ+midoid+"请求参数中获取的deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            if(StringUtils.isEmpty(deviceType)){
                logger.info(BIZ+midoid+"请求参数中未获取到deviceType，需要通过程序获取deviceType的值");
                //根据userAgent判断设备类型：pc、手机端、微信内(针对公众号支付)
                String userAgent = this.getUserAgentInfoByRequest(request);
                logger.info(BIZ+midoid+"根据请求头获取的user-agent为："+userAgent);
                deviceType = HttpUtil.getDeviceType(userAgent);
                logger.info(BIZ+midoid+"商户没传设备类型，通过程序判断deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            }
            String ip = IpUtil.getRemoteHost(request);
            logger.info(BIZ+midoid+"获取的请求ip为："+ip);
            logger.info(BIZ+midoid+"最终确定的deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");

            logger.info(BIZ+midoid+"调用tradeCashierCallbackHandler处理业务逻辑，传入的请求参数platOrderId="+platOrderId+",payType="+payType);
            result = tradeCashierCallbackHandler.process(platOrderId, payType);
            logger.info(BIZ+midoid+"调用tradeCashierCallbackHandler处理业务逻辑，传入的请求参数platOrderId="+platOrderId+",payType="+payType+"，返回值为："+JSONObject.toJSONString(result));
            if(null != result && ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
                Map<String, Object> callbackInfo = (Map) result.getData();
                boolean showResultPage = (boolean) callbackInfo.get("showResultPage");
                String callbackUrl = (String) callbackInfo.get("callbackUrl");
                if(showResultPage){
                    //展示支付结果页
                    page = this.getPageByDeviceType(deviceType, PageTypeEnum.RESULT.getCode());
                }else{
                    return "redirect:"+callbackUrl;
                }

            }else{
                model.addAttribute("respCode",result.getRespCode());
                model.addAttribute("respMsg",result.getRespMsg());
                page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
                logger.info(BIZ + midoid+"处理callback页面回调请求业务逻辑，处理结果为失败，返回的CommonResult="+JSONObject.toJSONString(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setRespCode(ErrorCodeEnum.E8001.getCode());
            result.setRespMsg(ErrorCodeEnum.E8001.getDesc());
            model.addAttribute("respCode",result.getRespCode());
            model.addAttribute("respMsg",result.getRespMsg());
            page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
            logger.error(BIZ+midoid+"callback页面回调请求异常："+e.getMessage());
        }
        logger.info(BIZ+midoid+"处理callback页面回调请求之后，返回给页面的数据为："+JSONObject.toJSONString(model));
        return page;
    }

    /**
     * 设置h5中间页需要的请求参数
     *
     * @param model
     * @param result
     */
    private void addH5CentPageModelInfo(Model model, CommonResult result) {

        Result resultInfo = (Result) result.getData();
        model.addAttribute("callMode", resultInfo.getStartPayType());
        model.addAttribute("payInfo", resultInfo.getPayInfo());
    }

    /**
     * 设置扫码中间页需要的请求参数
     *
     * @param model
     * @param result
     */
    private void addScanCentPageModelInfo(Model model, CommonResult result) {
        Result resultInfo = (Result) result.getData();
        String startPayType = resultInfo.getStartPayType();
        //封装生成二维码支付链接
        String payInfo = "";
        if(StartPayTypeEnum.SCAN_INSIDE.getCode().equals(startPayType)){
            try {
                String preUrl = ConfigUtil.getValue("qrCode.domain");
                String url = "http://"+preUrl+"/qrCode/gen";
                payInfo = url + "?uuid=" + URLEncoder.encode(resultInfo.getPayInfo(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if(StartPayTypeEnum.SCAN_EXTERNAL.getCode().equals(startPayType)){
            payInfo = resultInfo.getPayInfo();
        }

        model.addAttribute("payInfo", payInfo);
        model.addAttribute("paymentType", StringUtils.isNotBlank(resultInfo.getPaymentType())?resultInfo.getPaymentType().substring(0,2):"");
    }
    /**
     * 收银台页面需要的数据
     * @param model
     */
    private void addCashierModelInfo(Model model, CommonResult result, String goods, String amount) {
        model.addAttribute("goods", goods);
        model.addAttribute("amount", amount);
        Map mapData = (Map) result.getData();
        model.addAttribute("paymentTypes", mapData.get("paymentTypes"));
        model.addAttribute("mchtOrderId", mapData.get("mchtOrderId"));
        model.addAttribute("extraData", mapData.get("extraData"));
    }

    /**
     * 非收银台选择跳转页面
     * @return
     */
    private String chooseNotCashierPage(String deviceType, String biz) {
        //根据设备类型，确定跳转页面
        String page = "";
        if(DeviceTypeEnum.PC.getCode().equals(deviceType)){
            //pc页面
            page = this.getPageByDeviceType(deviceType, PageTypeEnum.INDEX.getCode());
        }else{
            //手机端和微信内，需要判断展示哪一种中间页,因为h5支付和公众号支付使用的是一种中间页，扫码支付使用的是另一种中间页
            //根据支付类型解析出具体中间页
            String pageTypeCode = this.resolvePageType(biz);
            page = this.getPageByDeviceType(deviceType, pageTypeCode);
        }
        return page;
    }

    /**
     * 根据支付类型找到具体的场景
     * @param biz
     * @return
     */
    private String resolvePageType(String biz) {
        if(PayTypeEnum.WX_WAP.getCode().equals(biz) || //微信h5支付
                PayTypeEnum.WX_PUBLIC.getCode().equals(biz)  || //微信公众号支付
                PayTypeEnum.ALIPAY_ONLINE_SCAN2WAP.getCode().equals(biz)  || //支付宝线上扫码转h5支付
                PayTypeEnum.ALIPAY_OFFLINE_SCAN2WAP.getCode().equals(biz)  || //支付宝线下扫码转h5支付
                PayTypeEnum.QQ_WAP.getCode().equals(biz)  || //QQh5支付
                PayTypeEnum.QQ_SCAN2WAP.getCode().equals(biz)  || //QQ扫码转h5支付
                PayTypeEnum.SUNING_SCAN2WAP.getCode().equals(biz)  || //苏宁扫码转h5支付
                PayTypeEnum.JD_WAP.getCode().equals(biz)  || //京东h5支付
                PayTypeEnum.JD_SCAN2WAP.getCode().equals(biz) //京东扫码转h5支付
                ){

            return PageTypeEnum.CENTER.getCode();

        }else if(PayTypeEnum.COMBINE_QRCODE.getCode().equals(biz) || //聚合二维码
                PayTypeEnum.WX_QRCODE.getCode().equals(biz)  || //微信扫码支付
                PayTypeEnum.ALIPAY_ONLINE_QRCODE.getCode().equals(biz)  || //支付宝线上扫码
                PayTypeEnum.ALIPAY_OFFLINE_QRCODE.getCode().equals(biz)  || //支付宝线下扫码
                PayTypeEnum.SUNING_QRCODE.getCode().equals(biz)  || //苏宁扫码
                PayTypeEnum.QQ_QRCODE.getCode().equals(biz)  || //QQ扫码支付
                PayTypeEnum.JD_SCAN.getCode().equals(biz)  || //京东扫码支付
                PayTypeEnum.UNIONPAY_QRCODE.getCode().equals(biz) //银联二维码支付
        ){
            return PageTypeEnum.SCAN.getCode();
        }else{
            //错误页面
            return PageTypeEnum.ERROR.getCode();
        }
    }


    /**
     * 获取请求的ua，由于不同浏览器可能是User-Agent也可能是user-agent
     * @param request
     * @return
     */
    private String getUserAgentInfoByRequest(HttpServletRequest request) {
        String userAgent = StringUtils.isNotEmpty(request.getHeader("user-agent"))
                ?request.getHeader("user-agent").toLowerCase():"";
        if (StringUtils.isEmpty(userAgent)){
            userAgent = StringUtils.isNotEmpty(request.getHeader("User-Agent"))
                    ?request.getHeader("User-Agent").toLowerCase():"";
        }
        //如果取不到userAgent 就置为unknow
        if(StringUtils.isEmpty(userAgent)){
            userAgent = "unknow";
        }
        return userAgent;
    }

    /**
     * 根据设备类型，找对应页面
     * @param deviceType
     * @return
     */
    private String getPageByDeviceType(String deviceType, String pageType) {
        String deviceTypeName = "";
        if(DeviceTypeEnum.PC.getCode().equals(deviceType)){
            deviceTypeName = "pc";
        }else if(DeviceTypeEnum.MOBILE.getCode().equals(deviceType)){
            deviceTypeName = "mobile";
        }else if(DeviceTypeEnum.WECHAT.getCode().equals(deviceType)){
            //TODO 微信公众号支付
        }else if(DeviceTypeEnum.ALIPAY.getCode().equals(deviceType)){
            //TODO 支付宝服务窗支付
        }else{
            //默认pc
            deviceTypeName = "pc";
        }
        return "modules/cashier/"+deviceTypeName+"/"+pageType;
    }


    /**
     * 测试页面使用
     * @param request
     * @return
     */
    @RequestMapping("genSign")
    @ResponseBody
    public String genSign(HttpServletRequest request){
        Map<String,String> paramMap = new HashMap<String,String>();

        paramMap.put("amount",request.getParameter("amount"));
        System.out.println(request.getParameter("amount"));
        if(StringUtils.isNotBlank(request.getParameter("appId"))){
            paramMap.put("appId",request.getParameter("appId"));
            System.out.println(request.getParameter("appId"));
        }
        if(StringUtils.isNotBlank(request.getParameter("appName"))){
            String appName = "";
            try {
                 appName = URLDecoder.decode(request.getParameter("appName"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            paramMap.put("appName", appName);
            System.out.println(appName);
        }
        if(StringUtils.isNotBlank(request.getParameter("callBackUrl"))){
            paramMap.put("callBackUrl",request.getParameter("callBackUrl"));
            System.out.println(request.getParameter("callBackUrl"));
        }
        if(StringUtils.isNotBlank(request.getParameter("desc"))){
            String desc = "";
            try {
                 desc = URLDecoder.decode(request.getParameter("desc"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            paramMap.put("desc", desc);
            System.out.println(desc);
        }
        if(StringUtils.isNotBlank(request.getParameter("deviceType"))){
            paramMap.put("deviceType",request.getParameter("deviceType"));
            System.out.println(request.getParameter("deviceType"));
        }
        if(StringUtils.isNotBlank(request.getParameter("expireTime"))){
            paramMap.put("expireTime",request.getParameter("expireTime"));
            System.out.println(request.getParameter("expireTime"));
        }
        String goods = "";
        try {
            goods = URLDecoder.decode(request.getParameter("goods"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        paramMap.put("goods",goods);

        if(StringUtils.isNotBlank(request.getParameter("ip"))){
            paramMap.put("ip",request.getParameter("ip"));
            System.out.println(request.getParameter("ip"));
        }
        if(StringUtils.isNotBlank(request.getParameter("openId"))){
            paramMap.put("openId",request.getParameter("openId"));
            System.out.println(request.getParameter("openId"));
        }
        if(StringUtils.isNotBlank(request.getParameter("operator"))){
            paramMap.put("operator",request.getParameter("operator"));
            System.out.println(request.getParameter("operator"));
        }
        paramMap.put("orderId",request.getParameter("orderId"));
        System.out.println(request.getParameter("orderId"));
        paramMap.put("orderTime",request.getParameter("orderTime"));
        System.out.println(request.getParameter("orderTime"));


        paramMap.put("notifyUrl",request.getParameter("notifyUrl"));
        System.out.println(request.getParameter("notifyUrl"));

        String mchtKey = request.getParameter("mchtKey");

        String sign = null;
        try {
            sign = SignUtil.md5Sign(paramMap,mchtKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 测试页面使用
     * @param request
     * @return
     */
    @RequestMapping("test")
    public String test(HttpServletRequest request) {

        System.out.println("测试页面");
        return "modules/cashier/pc/test";

    }
}
