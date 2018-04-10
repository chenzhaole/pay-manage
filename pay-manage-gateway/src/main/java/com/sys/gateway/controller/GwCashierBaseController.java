package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.common.enums.*;
import com.sys.common.util.NumberUtils;
import com.sys.gateway.common.ConfigUtil;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 第三方支付网页版sdk
 */
public class GwCashierBaseController {
    private Logger logger = LoggerFactory.getLogger(GwCashierBaseController.class);

    /**
     * 设置h5中间页需要的请求参数
     *
     * @param model
     * @param result
     */
    protected void addH5CentPageModelInfo(Model model, CommonResult result, String userAgent) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
        Result resultInfo = (Result) retMapInfo.get("result");
        Map<String, String> mapQQandMobile = (Map<String, String>)retMapInfo.get("pageQQandMobile");
        String payType = resultInfo.getPaymentType();
        model.addAttribute("callMode", resultInfo.getClientPayWay());
        model.addAttribute("payInfo", resultInfo.getPayInfo());
        model.addAttribute("platOrderId", resultInfo.getOrderNo());
        model.addAttribute("payType", payType);
        //是否使用扫码转H5方式，即是否通过iframe标签掉起支付，0：使用， 1：不使用
        model.addAttribute("iframe", this.useIfrmnameMark(payType, userAgent));

        //qq和手机
        model.addAttribute("qq", mapQQandMobile.get("qq"));
        model.addAttribute("mobile", mapQQandMobile.get("mobile"));

    }

    /**
     * 是否通过iframe标签掉起支付，0：使用， 1：不使用
     * @param payType
     * @param userAgent
     * @return
     */
    protected String useIfrmnameMark(String payType, String userAgent) {
        //默认不使用
        String iframe = "1";
        if(PayTypeEnum.QQ_SCAN2WAP.getCode().equals(payType)){
            //qq扫码转h5不支持：苹果自带浏览器、uc浏览器、百度浏览器
            //过滤掉qq扫码转h5不支持的浏览器，
            if(browserbSupportQQScan2H5(userAgent)){
               iframe = "0";
            }
        }else if(PayTypeEnum.ALIPAY_OFFLINE_SCAN2WAP.getCode().equals(payType) || PayTypeEnum.ALIPAY_ONLINE_SCAN2WAP.getCode().equals(payType) ){
            if(browserbSupportAliScan2H5(userAgent)){
                iframe = "0";
            }
        }
        return iframe;
    }

    /**
     * qq扫码转h5不支持：苹果自带浏览器、uc浏览器、百度浏览器，过滤掉qq扫码转h5不支持的浏览器，
     * @param userAgent
     * @return
     */
    protected boolean browserbSupportQQScan2H5(String userAgent) {
        if (userAgent.contains("iPhone")){
            //ios手机自带浏览器
            //仅仅是苹果浏览器内核，即苹果自带浏览器
            if(userAgent.contains("Safari")
                    && !userAgent.contains("Chrome")
                    && !userAgent.contains("Firefox")
                    && !userAgent.contains("Opera")){
                //不支持
                return false;
            }
        }else if(userAgent.contains("Android")) {
            //目前测试人员测出：手机百度浏览器、手机谷歌浏览器
            // Android手机

            //uc如下：判断依据 包含UCBrowser
            //Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; vivo X9 Build/MMB29M) AppleWebKit/537.36 (KHTML, like Gecko)
//	    	  Version/4.0 Chrome/40.0.2214.89  UCBrowser/11.6.4.950 Mobile Safari/537.36

            //手机百度如下：判断依据 包含baidu
//	    	  user-agent = Mozilla/5.0 (Linux; Android 6.0.1; vivo X9 Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko)
//	    	  Version/4.0 Chrome/48.0.2564.116 Mobile Safari/537.36 T7/9.1 baiduboxapp/9.1.0.12 (Baidu; P1 6.0.1)
            /** 1. 安卓手机uc浏览器， 2.安卓手机百度浏览器*/
            if(userAgent.contains("baidu") || userAgent.contains("UCBrowser")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 支付宝扫码转h5不支持：
     * @param userAgent
     * @return
     */
    protected boolean browserbSupportAliScan2H5(String userAgent) {

        return true;
    }

    /**
     * 设置pc页面支付需要的请求参数
     *
     * @param model
     * @param result
     */
    protected void addPcScanPageModelInfo(Model model, CommonResult result, String amount, String mchtId, String goods, String mchtOrderId) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
        Result resultInfo = (Result) retMapInfo.get("result");
        Map<String, String> mapQQandMobile = (Map<String, String>)retMapInfo.get("pageQQandMobile");
        String startPayType = resultInfo.getClientPayWay();
        //封装生成二维码支付链接
        String payInfo = "";
        if(ClientPayWayEnum.SCAN_INSIDE.getCode().equals(startPayType)){
            try {
                String preUrl = ConfigUtil.getValue("qrCode.domain");
                String url = "http://"+preUrl+"/qrCode/gen";
                payInfo = url + "?uuid=" + URLEncoder.encode(resultInfo.getPayInfo(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if(ClientPayWayEnum.SCAN_EXTERNAL.getCode().equals(startPayType)){
            payInfo = resultInfo.getPayInfo();
        }
        String payType = resultInfo.getPaymentType();
        Set<String> paymentTypes = new HashSet<>();
        paymentTypes.add(payType.substring(0, 2));
        model.addAttribute("paymentTypes", paymentTypes);
        model.addAttribute("platOrderId", resultInfo.getOrderNo());
        model.addAttribute("payInfo", payInfo);
        model.addAttribute("payType", payType);
        //1表示非收银台，直接显示二维码
        model.addAttribute("pcIscashier", "1");
        //将分转成元
        amount = NumberUtils.changeF2Y(amount);
        model.addAttribute("amount", amount);
        model.addAttribute("mchtId", mchtId);
        model.addAttribute("goods", goods);
        model.addAttribute("mchtOrderId", mchtOrderId);
        String countdownTime  = ConfigUtil.getValue("qrCode.countdownTime");
        model.addAttribute("countdownTime", countdownTime);

    }

    /**
     * 设置扫码中间页需要的请求参数
     *
     * @param model
     * @param result
     */
    protected void addScanCentPageModelInfo(Model model, CommonResult result) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
        Result resultInfo = (Result) retMapInfo.get("result");
        Map<String, String> mapQQandMobile = (Map<String, String>)retMapInfo.get("pageQQandMobile");
        String startPayType = resultInfo.getClientPayWay();
        //封装生成二维码支付链接
        String payInfo = "";
        if(ClientPayWayEnum.SCAN_INSIDE.getCode().equals(startPayType)){
            try {
                String preUrl = ConfigUtil.getValue("qrCode.domain");
                String url = "http://"+preUrl+"/qrCode/gen";
                payInfo = url + "?uuid=" + URLEncoder.encode(resultInfo.getPayInfo(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if(ClientPayWayEnum.SCAN_EXTERNAL.getCode().equals(startPayType)){
            payInfo = resultInfo.getPayInfo();
        }

        model.addAttribute("platOrderId", resultInfo.getOrderNo());
        model.addAttribute("payInfo", payInfo);
        model.addAttribute("payType", resultInfo.getPaymentType());
        //qq和手机
        model.addAttribute("qq", mapQQandMobile.get("qq"));
        model.addAttribute("mobile", mapQQandMobile.get("mobile"));
    }

    /**
     *  pc端页面异步下单，返回页面的信息
     * @param result
     * @return
     */
    protected String returnPcPageInfo(Result result) {
        String payInfo = "";
        try {
            String preUrl = ConfigUtil.getValue("qrCode.domain");
            String url = "http://"+preUrl+"/qrCode/gen";
            payInfo = url + "?uuid=" + URLEncoder.encode(result.getPayInfo(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String payType = result.getPaymentType();
        String platOrderId = result.getOrderNo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payInfo", payInfo);
        jsonObject.put("platOrderId", platOrderId);
        String countdownTime = ConfigUtil.getValue("qrCode.countdownTime");
        jsonObject.put("countdownTime",countdownTime);
        jsonObject.put("payType",payType);
        return jsonObject.toString();
    }


    /**
     * 收银台页面需要的数据
     * @param model
     */
    protected void addCashierModelInfo(Model model, CommonResult result, String goods, String amount, String mchtId) {
        model.addAttribute("goods", goods);
        //将分转成元
        amount = NumberUtils.changeF2Y(amount);
        model.addAttribute("amount", amount);
        model.addAttribute("mchtId", mchtId);
        Map mapData = (Map) result.getData();
        model.addAttribute("paymentTypes", mapData.get("paymentTypes"));
        model.addAttribute("mchtOrderId", mapData.get("mchtOrderId"));
        model.addAttribute("extraData", mapData.get("extraData"));
        Map<String, String> pageQQandMobile = (Map<String, String>) mapData.get("pageQQandMobile");
        model.addAttribute("qq", pageQQandMobile.get("qq"));
        model.addAttribute("mobile", pageQQandMobile.get("mobile"));
        //2表示展示收银台， 1表示不展示收银台
        model.addAttribute("pcIscashier", "2");
    }

    /**
     * 非收银台选择跳转页面
     * @return
     */
    protected String chooseNotCashierPage(String deviceType, String biz) {
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
    protected String resolvePageType(String biz) {
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
    protected String getUserAgentInfoByRequest(HttpServletRequest request) {
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
    protected String getPageByDeviceType(String deviceType, String pageType) {
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

}
