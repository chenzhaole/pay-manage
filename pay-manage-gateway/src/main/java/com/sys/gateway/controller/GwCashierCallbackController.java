package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.trade.handler.ITradeCashierCallbackHandler;
import com.sys.common.enums.ClientPayWayEnum;
import com.sys.common.enums.DeviceTypeEnum;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PageTypeEnum;
import com.sys.common.util.HttpUtil;
import com.sys.core.dao.dmo.MchtGatewayOrder;
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

import javax.servlet.http.HttpServletRequest;
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
                boolean showResultPage = (boolean) callbackInfo.get("showResultPage");//支付结果中转页标识
                boolean showResultADPage = (boolean) callbackInfo.get("showResultADPage");//带广告的支付结果页标识
                String callbackUrl = (String) callbackInfo.get("callbackUrl");
                //20180718新增，是否显示带广告的支付结果页面，目前只处理手机端
                if(showResultADPage && !DeviceTypeEnum.PC.getCode().equals(deviceType)){
                    page = this.getPageByDeviceType(deviceType, PageTypeEnum.PAYSUCCESS.getCode());
                    this.generatePaySuccessPageData(model, callbackInfo, platOrderId, payType, midoid);
                }else {
                    if (showResultPage) {
                        //展示支付结果中转页，只是为了解决部分微信h5前台页面回掉问题
                        page = this.getPageByDeviceType(deviceType, PageTypeEnum.RESULT.getCode());
                        //支付结果中转页需要的参数
                        model.addAttribute("callbackUrl", callbackUrl);
                    } else {
                        return "redirect:" + callbackUrl;
                    }
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
            result.setRespMsg("操作失败");
            model.addAttribute("respCode",result.getRespCode());
            model.addAttribute("respMsg",result.getRespMsg());
            page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
            logger.error(BIZ+midoid+"callback页面回调请求异常："+e.getMessage());
        }
        logger.info(BIZ+midoid+"处理callback页面回调请求，跳转的页面为："+page+"，返回给页面的数据为："+JSONObject.toJSONString(model));
        return page;
    }

    /**
     * 生成支付结果广告页需要的数据
     * @param model
     * @param callbackInfo
     * @param midoid
     */
    private void generatePaySuccessPageData(Model model, Map<String, Object> callbackInfo, String platOrderId, String payType, String midoid) {
        model.addAttribute("platOrderId", platOrderId);
        String payTypeName = this.genePayTypeName(payType);
        model.addAttribute("payType", payTypeName);
        model.addAttribute("callbackUrl", callbackInfo.get("callbackUrl"));
        model.addAttribute("goodsName", callbackInfo.get("goodsName"));
        model.addAttribute("amount", callbackInfo.get("amount"));
        String adUrl = (String) callbackInfo.get("adUrl");
        String mchtId  = (String) callbackInfo.get("mchtId");
        String mchtPropertyTag = (String) callbackInfo.get("mchtPropertyTag");
        adUrl = adUrl+"?orderId="+platOrderId+"&merchantId="+mchtId+"&tag="+mchtPropertyTag;
        model.addAttribute("adUrl",adUrl);
        //qq和手机
        Map<String, String> mapQQandMobile = (Map<String, String>)callbackInfo.get("pageQQandMobile");
        model.addAttribute("qq", mapQQandMobile.get("qq"));
        model.addAttribute("mobile", mapQQandMobile.get("mobile"));
        logger.info(midoid+",跳转到支付结果广告页，需要的数据："+JSONObject.toJSONString(model));
    }

    private String genePayTypeName(String payType) {
        String payTypeName = "";
        if(payType.startsWith("wx")){
            payTypeName = "微信支付";
        }else if(payType.startsWith("al")){
            payTypeName = "支付宝支付";
        }else if(payType.startsWith("sn")){
            payTypeName = "苏宁支付";
        }else if(payType.startsWith("qq")){
            payTypeName = "QQ支付";
        }else if(payType.startsWith("jd")){
            payTypeName = "京东支付";
        }else if(payType.startsWith("yl")){
            payTypeName = "银联支付";
        }else if(payType.startsWith("qj")){
            payTypeName = "银行卡支付";
        }else{
            payTypeName = "第三方支付";
        }
        return payTypeName;
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
     * 测试结果页面使用
     * @param request
     * @return
     */
    @RequestMapping("testResult/{mchtOrderId}")
    public String testResult(HttpServletRequest request, @PathVariable String mchtOrderId, Model model) {
        CommonResult commonResult = tradeCashierCallbackHandler.testResult(mchtOrderId);
        if(null != commonResult && null != commonResult.getData()){
            MchtGatewayOrder mchtGatewayOrder = (MchtGatewayOrder) commonResult.getData();
            String status = mchtGatewayOrder.getStatus();
            model.addAttribute("platOrderId", mchtGatewayOrder.getPlatOrderId());
            model.addAttribute("status", status);
        }
        model.addAttribute("mchtOrderId", mchtOrderId);
        return "modules/cashier/pc/testResult";

    }
}
