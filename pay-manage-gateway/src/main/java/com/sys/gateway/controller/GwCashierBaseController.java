package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.common.enums.*;
import com.sys.common.util.DesUtil32;
import com.sys.common.util.IdUtil;
import com.sys.common.util.NumberUtils;
import com.sys.common.util.SignUtil;
import com.sys.gateway.common.ConfigUtil;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 第三方支付网页版sdk
 */
public class GwCashierBaseController {
    private Logger logger = LoggerFactory.getLogger(GwCashierBaseController.class);

    /**
     * 付款码支付包装成网页支付，支付类型值后三位是固定值501
     */
    protected static final String BARCODE_NUM = "501";

    /**
     * 设置h5中间页需要的请求参数
     *
     * @param model
     * @param result
     */
    protected void addH5CentPageModelInfo(Model model, CommonResult result, String userAgent, String midoid) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
        Result resultInfo = (Result) retMapInfo.get("result");
        Map<String, String> mapQQandMobile = (Map<String, String>)retMapInfo.get("pageQQandMobile");
        String payType = resultInfo.getPaymentType();
        model.addAttribute("callMode", resultInfo.getClientPayWay());
        model.addAttribute("payInfo", resultInfo.getPayInfo());
        model.addAttribute("platOrderId", resultInfo.getOrderNo());
        model.addAttribute("payType", payType);
        //是否使用扫码转H5方式，即是否通过iframe标签掉起支付，0：使用， 1：不使用
        model.addAttribute("iframe", this.useIfrmnameMark(payType, userAgent, midoid));

        //qq和手机
        model.addAttribute("qq", mapQQandMobile.get("qq"));
        model.addAttribute("mobile", mapQQandMobile.get("mobile"));
        logger.info(midoid+"，h5中间页需要的参数："+JSONObject.toJSONString(model));

    }

    /**
     * 设置付款码中间页需要的请求参数
     *
     * @param model
     * @param result
     */
    protected void addBarcodeCentPageModelInfo(Model model, CommonResult result, String midoid) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
        Result resultInfo = (Result) retMapInfo.get("result");
        Map<String, String> mapQQandMobile = (Map<String, String>)retMapInfo.get("pageQQandMobile");
        String payType = resultInfo.getPaymentType();
        model.addAttribute("platOrderId", resultInfo.getOrderNo());
        model.addAttribute("mchtOrderId", resultInfo.getMchtOrderNo());
        model.addAttribute("payType", payType);
        model.addAttribute("paymentType", payType.substring(0,2));
        //将分转成元
        String amount = NumberUtils.changeF2Y(resultInfo.getOrderAmount());
        model.addAttribute("amount", amount);

        //qq和手机
        model.addAttribute("qq", mapQQandMobile.get("qq"));
        model.addAttribute("mobile", mapQQandMobile.get("mobile"));
        logger.info(midoid+"，付款码中间页需要的参数："+JSONObject.toJSONString(model));

    }

    /**
     * 是否通过iframe标签掉起支付，0：使用， 1：不使用
     * @param payType
     * @param userAgent
     * @return
     */
    protected String useIfrmnameMark(String payType, String userAgent, String midoid) {
        //默认不使用
        String iframe = "1";
        if(PayTypeEnum.QQ_SCAN2WAP.getCode().equals(payType)){
            //不支持qq扫码转h5的浏览器（即在这些浏览器掉不起支付）：苹果自带浏览器、uc浏览器、百度浏览器
            //过滤掉qq扫码转h5不支持的浏览器，不使用iframe标签
            if(browserbSupportQQScan2H5(userAgent, midoid)){
                iframe = "0";
            }
        }else if(PayTypeEnum.ALIPAY_ONLINE_SCAN2WAP.getCode().equals(payType) ){
            //不支持支付宝扫码转h5的浏览器（即在这些浏览器掉不起支付）：苹果自带浏览器、qq浏览器、百度浏览器
            //过滤掉支付宝扫码转h5不支持的浏览器，不使用iframe标签
            if(browserbSupportAliScan2H5(userAgent, midoid)){
                iframe = "0";
            }
        }
        logger.info(midoid+"，是否通过iframe标签掉起支付，iframe："+iframe+"，【0：使用， 1：不使用】");
        return iframe;
    }

    /**
     * qq扫码转h5不支持：苹果自带浏览器、uc浏览器、百度浏览器，过滤掉qq扫码转h5不支持的浏览器，
     * @param userAgent
     * @return
     */
    protected boolean browserbSupportQQScan2H5(String userAgent, String midoid) {
        if(StringUtils.isNotBlank(userAgent)){
            userAgent = userAgent.toLowerCase();
        }else{
            return false;
        }
        if (userAgent.contains("iphone")){
            //ios手机自带浏览器
            //仅仅是苹果浏览器内核，即苹果自带浏览器
            if((userAgent.contains("safari"))
                    && !userAgent.contains("chrome")
                    && !userAgent.contains("firefox")
                    && !userAgent.contains("opera")){
                //不支持
                logger.info(midoid+"，qq扫码转h5支付，不支持iPhone手机的自带浏览器");
                return false;
            }
        }else if(userAgent.contains("android")) {
            //目前测试人员测出：手机百度浏览器、手机谷歌浏览器
            // Android手机

            //uc如下：判断依据 包含UCBrowser
            //Mozilla/5.0 (Linux; U; Android 6.0.1; zh-CN; vivo X9 Build/MMB29M) AppleWebKit/537.36 (KHTML, like Gecko)
//	    	  Version/4.0 Chrome/40.0.2214.89  UCBrowser/11.6.4.950 Mobile Safari/537.36

            //手机百度如下：判断依据 包含baidu
//	    	  user-agent = Mozilla/5.0 (Linux; Android 6.0.1; vivo X9 Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko)
//	    	  Version/4.0 Chrome/48.0.2564.116 Mobile Safari/537.36 T7/9.1 baiduboxapp/9.1.0.12 (Baidu; P1 6.0.1)
            /** 1. 安卓手机uc浏览器， 2.安卓手机百度浏览器*/
            if(userAgent.contains("baidu")) {
                logger.info(midoid+"，qq扫码转h5支付，不支持Android手机的百度【baidu】浏览器");
                return false;
            }
            if(userAgent.contains("ucbrowser")){
                logger.info(midoid+"，qq扫码转h5支付，不支持Android手机的UC【UCBrowser】浏览器");
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
    protected boolean browserbSupportAliScan2H5(String userAgent, String midoid) {
        if(StringUtils.isNotBlank(userAgent)){
            userAgent = userAgent.toLowerCase();
        }else{
            return false;
        }
        if (userAgent.contains("iphone")){
            //ios手机自带浏览器
            //仅仅是苹果浏览器内核，即苹果自带浏览器
            if((userAgent.contains("safari"))
                    && !userAgent.contains("chrome")
                    && !userAgent.contains("firefox")
                    && !userAgent.contains("opera")){
                //不支持
                logger.info(midoid+"，支付宝扫码转h5支付，不支持iPhone手机的自带浏览器");
                return false;
            }
        }else if(userAgent.contains("android")) {
            //目前测试人员测出：手机百度浏览器、手机谷歌浏览器、手机qq浏览器
            // Android手机

            //手机百度如下：判断依据 包含baidu
//	    	  user-agent = Mozilla/5.0 (Linux; Android 6.0.1; vivo X9 Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko)
//	    	  Version/4.0 Chrome/48.0.2564.116 Mobile Safari/537.36 T7/9.1 baiduboxapp/9.1.0.12 (Baidu; P1 6.0.1)

            /** 1. 安卓手机qq浏览器， 2.安卓手机百度浏览器*/
            if(userAgent.contains("baidu")) {
                logger.info(midoid+"，支付宝扫码转h5支付，不支持Android手机的百度【baidu】浏览器");
                return false;
            }
        }
        return true;
    }
    /**
     * 设置pc页面非收银台支付方式需要的请求参数
     *
     * @param model
     * @param result
     */
    protected void addPcScanPageModelInfo(Model model, CommonResult result, String amount, String mchtId, String goods, String mchtOrderId, String midoid) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
        Result resultInfo = (Result) retMapInfo.get("result");
        Map<String, String> mapQQandMobile = (Map<String, String>)retMapInfo.get("pageQQandMobile");
        String startPayType = resultInfo.getClientPayWay();
        //封装生成二维码支付链接
        String payInfo = "";
        if(ClientPayWayEnum.SCAN_INSIDE.getCode().equals(startPayType)){
            payInfo = this.geneQrcodeUrl(resultInfo.getQrCodeDomain(), resultInfo.getPayInfo(), midoid);
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
        String countdownTime  = String.valueOf(resultInfo.getCountdownTime());
        model.addAttribute("countdownTime", countdownTime);

        //非收银台模式下，extraData字段存入平台订单号，以便pc端扫码从新下单，从缓存中获取商户请求信息
        model.addAttribute("extraData", resultInfo.getOrderNo());


        logger.info(midoid+",pc页面非收银台支付方式需要的参数："+JSONObject.toJSONString(model));
    }

    /**
     * 设置扫码中间页需要的请求参数
     *
     * @param model
     * @param result
     */
    protected void addScanCentPageModelInfo(Model model, CommonResult result, String midoid) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
        Result resultInfo = (Result) retMapInfo.get("result");
        Map<String, String> mapQQandMobile = (Map<String, String>)retMapInfo.get("pageQQandMobile");
        String startPayType = resultInfo.getClientPayWay();
        //封装生成二维码支付链接
        String payInfo = "";
        if(ClientPayWayEnum.SCAN_INSIDE.getCode().equals(startPayType)){
            payInfo = this.geneQrcodeUrl(resultInfo.getQrCodeDomain(), resultInfo.getPayInfo(), midoid);
        }else if(ClientPayWayEnum.SCAN_EXTERNAL.getCode().equals(startPayType)){
            payInfo = resultInfo.getPayInfo();
        }

        model.addAttribute("platOrderId", resultInfo.getOrderNo());
        model.addAttribute("payInfo", payInfo);
        model.addAttribute("payType", resultInfo.getPaymentType());
        String paymentType = resultInfo.getPaymentType().substring(0, 2);
        model.addAttribute("paymentType", paymentType);
        //qq和手机
        model.addAttribute("qq", mapQQandMobile.get("qq"));
        model.addAttribute("mobile", mapQQandMobile.get("mobile"));
        logger.info(midoid+",扫码中间页需要的数据："+JSONObject.toJSONString(model));
    }

    /**
     * 扫码支付，自己封装的二维码url
     * @param qrCodeDomain
     * @param qrCodepayInfo
     * @param midoid
     * @return
     */
    private String geneQrcodeUrl(String qrCodeDomain, String qrCodepayInfo, String midoid) {
        String payInfo = "";
        try {
            String seq = IdUtil.getUUID();
            String url = "http://"+qrCodeDomain+"/qrCode/gen";
            Map<String, String> desData = new HashMap();
            desData.put("seq", seq);
            desData.put("qrCodepayInfo", qrCodepayInfo);
            String desResult = DesUtil32.encode(JSONObject.toJSONString(desData), "Zhrt2018");
            logger.info("生成二维码之前对数据进行des加密，密钥为：Zhrt2018"+"，加密后的值为："+desResult);
            payInfo = url + "?uuid=" + URLEncoder.encode(qrCodepayInfo, "UTF-8")+"&seq="+seq+"&data="+desResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(midoid+"，扫码支付，自己封装的二维码url："+payInfo);
        return payInfo;
    }

    /**
     *  pc端页面异步下单，返回页面的信息
     * @param result
     * @return
     */
    protected String returnPcPageInfo(Result result, String midoid) {
        String payInfo = this.geneQrcodeUrl(result.getQrCodeDomain(), result.getPayInfo(), midoid);
        String payType = result.getPaymentType();
        String platOrderId = result.getOrderNo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payInfo", payInfo);
        jsonObject.put("platOrderId", platOrderId);
        String countdownTime = String.valueOf(result.getCountdownTime());
        jsonObject.put("countdownTime",countdownTime);
        jsonObject.put("payType",payType);
        String data = jsonObject.toString();
        logger.info(midoid+"，pc端页面异步下单，返回页面的信息："+data);
        return data;
    }

    /**
     *  pc端页面拼接付款码地址
     * @param result
     * @return
     */
    protected String returnBarcodeUrlInfo(Result result, String midoid) {
        String platOrderId = result.getOrderNo();
        String paymentType = result.getPaymentType();
        String mchtOrderId = result.getMchtOrderNo();
        String amount = result.getOrderAmount();
        String barcodeUrl = "/gateway/cashier/platPcBarcode";
        barcodeUrl = barcodeUrl+"/"+platOrderId+"/"+mchtOrderId+"/"+paymentType+"/"+amount;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("barcodeUrl", barcodeUrl);
        jsonObject.put("barcode", "barcode");
        String data = jsonObject.toString();
        logger.info(midoid+"，pc端页面异步下单付款码支付，返回页面的信息："+data);
        return data;
    }

    /**
     * 收银台页面需要的数据
     * @param model
     */
    protected void addCashierModelInfo(Model model, CommonResult result, String goods, String amount, String mchtId, String midoid) {
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
        logger.info(midoid+"，收银台页面需要的数据："+JSONObject.toJSONString(model));
    }

    /**
     * 收银台页面需要的数据
     * @param model
     */
    protected void addCashierBankModelInfo(Model model, CommonResult result, String goods, String amount, String mchtId, String midoid) {
        model.addAttribute("goods", goods);
        //将分转成元
        amount = NumberUtils.changeF2Y(amount);
        model.addAttribute("amount", amount);
        model.addAttribute("mchtId", mchtId);
        Map mapData = (Map) result.getData();
        model.addAttribute("payType",PayTypeEnum.LOCAL_BANK.getCode());
        model.addAttribute("bankCodes", mapData.get("bankCodes"));
        model.addAttribute("mchtOrderId", mapData.get("mchtOrderId"));
        model.addAttribute("extraData", mapData.get("extraData"));
        Map<String, String> pageQQandMobile = (Map<String, String>) mapData.get("pageQQandMobile");
        model.addAttribute("qq", pageQQandMobile.get("qq"));
        model.addAttribute("mobile", pageQQandMobile.get("mobile"));
        logger.info(midoid+"，收银台页面需要的数据："+JSONObject.toJSONString(model));
    }

    /**
     * 跳转上游收银台页面需要的数据
     * @param model
     */
    protected void addChanCashierModelInfo(Model model, CommonResult result, String midoid) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
        Result resultInfo = (Result) retMapInfo.get("result");
        model.addAttribute("clientPayWay", resultInfo.getClientPayWay());
        model.addAttribute("payInfo", resultInfo.getPayInfo());
        logger.info(midoid+"，直接跳转到上游收银台页面需要的数据："+JSONObject.toJSONString(model));
    }

    /**
     * 非收银台选择跳转页面
     * @return
     */
    protected String chooseNotCashierPage(String deviceType, String payType,  String midoid) {
        //根据设备类型，确定跳转页面
        String page = "";
        if(DeviceTypeEnum.PC.getCode().equals(deviceType)){
            //支付类型枚举类中定义的 条码支付值的后三位是501，会显示输入条形码的页面
            if(payType.endsWith(BARCODE_NUM)){
                page = "modules/cashier/barcode/barcode";
            }else{
                //pc页面--扫码使用
                page = this.getPageByDeviceType(deviceType, PageTypeEnum.INDEX.getCode(), midoid );
            }
        }else{
            //支付类型枚举类中定义的 条码支付值的后三位是501，会显示输入条形码的页面
            if(payType.endsWith(BARCODE_NUM)){
                page = "modules/cashier/barcode/barcode";
            }else{
                //手机端和微信内，需要判断展示哪一种中间页,因为h5支付和公众号支付使用的是一种中间页，扫码支付使用的是另一种中间页
                //根据支付类型解析出具体中间页
                String pageTypeCode = this.resolvePageType(payType, midoid);
                page = this.getPageByDeviceType(deviceType, pageTypeCode, midoid);
            }
        }
        logger.info(midoid+"，非收银台选择跳转页面page="+page);
        return page;
    }

    /**
     * 手机端根据支付类型找到具体的支付中间页
     * @param biz
     * @return
     */
    protected String resolvePageType(String biz, String midoid) {
        //具体页面
        String pageType = "";
        if(PayTypeEnum.WX_WAP.getCode().equals(biz) || //微信h5支付
                PayTypeEnum.WX_PUBLIC_NATIVE.getCode().equals(biz)  || //微信原生公众号支付
                PayTypeEnum.WX_PUBLIC_NOT_NATIVE.getCode().equals(biz)  || //微信非原生公众号支付
                PayTypeEnum.ALIPAY_ONLINE_SCAN2WAP.getCode().equals(biz)  || //支付宝扫码转h5支付
                PayTypeEnum.ALIPAY_H5.getCode().equals(biz)  || //支付宝h5支付
                PayTypeEnum.ALIPAY_PC.getCode().equals(biz)  || //支付宝PC支付
                PayTypeEnum.QQ_WAP.getCode().equals(biz)  || //QQh5支付
                PayTypeEnum.QQ_SCAN2WAP.getCode().equals(biz)  || //QQ扫码转h5支付
                PayTypeEnum.SUNING_SCAN2WAP.getCode().equals(biz)  || //苏宁扫码转h5支付
                PayTypeEnum.JD_WAP.getCode().equals(biz)  || //京东h5支付
                PayTypeEnum.JD_SCAN2WAP.getCode().equals(biz) ||//京东扫码转h5支付
                PayTypeEnum.UNIONPAY_SCAN2WAP.getCode().equals(biz) //京东扫码转h5支付
                ){

            pageType = PageTypeEnum.CENTER.getCode();

        }else if(PayTypeEnum.COMBINE_QRCODE.getCode().equals(biz) || //聚合二维码
                PayTypeEnum.WX_QRCODE.getCode().equals(biz)  || //微信扫码支付
                PayTypeEnum.ALIPAY_ONLINE_QRCODE.getCode().equals(biz)  || //支付宝扫码
                PayTypeEnum.SUNING_QRCODE.getCode().equals(biz)  || //苏宁扫码
                PayTypeEnum.QQ_QRCODE.getCode().equals(biz)  || //QQ扫码支付
                PayTypeEnum.JD_SCAN.getCode().equals(biz)  || //京东扫码支付
                PayTypeEnum.UNIONPAY_QRCODE.getCode().equals(biz) //银联二维码支付
                ){

            pageType = PageTypeEnum.SCAN.getCode();

        }else{
            //错误页面
            pageType = PageTypeEnum.ERROR.getCode();
        }
        logger.info(midoid+"，手机端根据支付类型找到具体的支付中间页,传入的支付类型="+biz+"，最终页面page="+pageType);
        return pageType;
    }


    /**
     * 获取请求的ua，由于不同浏览器可能是User-Agent也可能是user-agent
     * @param request
     * @return
     */
    protected String getUserAgentInfoByRequest(HttpServletRequest request, String midoid) {
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
        if(StringUtils.isNotBlank(midoid)){
            logger.info(midoid+"，获取的请求头userAgent="+userAgent);
        }
        return userAgent;
    }

    /**
     * 根据设备类型，找对应页面
     * @param deviceType
     * @return
     */
    protected String getPageByDeviceType(String deviceType, String pageType, String midoid) {
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
        String url = "modules/cashier/"+deviceTypeName+"/"+pageType;
        logger.info(midoid+"，根据设备类型，找对应页面为："+url);
        return url;
    }

    /**
     * 判断是否使用上游收银台页面
     * @param result
     * @return
     */
    protected boolean isUseChanCashierPage(Object result, String midoid) {
        Map<String, Object> retMapInfo = ( Map<String, Object>)result;
        Result resultInfo = (Result) retMapInfo.get("result");
        String clientPayWay  = resultInfo.getClientPayWay();
        logger.info(midoid+"，根据clientPayWay的值："+clientPayWay+"，判断是否需要使用上游收银台页面");
        if(ClientPayWayEnum.CHAN_CASHIER_URL.getCode().equals(clientPayWay) ||
                ClientPayWayEnum.CHAN_CASHIER_JS.getCode().equals(clientPayWay) ||
                ClientPayWayEnum.CHAN_CASHIER_FORM.getCode().equals(clientPayWay)){
            logger.info(midoid+"，根据clientPayWay的值："+clientPayWay+"，判断出需要使用上游收银台页面");
            return true;
        }
        logger.info(midoid+"，根据clientPayWay的值："+clientPayWay+"，判断出不需要使用上游收银台页面");
        return false;
    }

    /**
     * 跳转到上游收银台页面需要的参数
     * @param resultInfo
     * @param midoid
     * @return
     */
    protected String returnChanCasgierPageInfo(Result resultInfo, String midoid) {
        String payInfo = resultInfo.getPayInfo();
        String clientPayWay = resultInfo.getClientPayWay();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payInfo", payInfo);
        jsonObject.put("clientPayWay", clientPayWay);
        String data = jsonObject.toString();
        logger.info(midoid+"，pc端页面异步下单，跳转到上游收银台页面需要的参数："+data);
        return data;
    }
}
