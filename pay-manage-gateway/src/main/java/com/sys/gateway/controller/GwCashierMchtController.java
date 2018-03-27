package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.cashier.TradeCashierRequest;
import com.sys.boss.api.service.trade.handler.ITradeCashierMchtHandler;
import com.sys.common.enums.*;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.common.util.SignUtil;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwCashierMchtService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方支付网页版sdk--接收商户请求
 */
@Controller
@RequestMapping("gateway/cashier")
public class GwCashierMchtController extends GwCashierBaseController {
    private Logger logger = LoggerFactory.getLogger(GwCashierMchtController.class);

    @Autowired
    private GwCashierMchtService gwCashierService;

    @Autowired
    private ITradeCashierMchtHandler iTradeCashierMchtHandler;

    private static final String BIZ = "网页支付GwCashierMchtController，商户发起支付请求->";



    /**
     * 处理商户请求
     */
    @RequestMapping(value="mchtCall")
    public String mchtCall(HttpServletRequest request, Model model){
        CommonResult result = new CommonResult();
        result.setRespCode(ErrorCodeEnum.FAILURE.getCode());
        result.setRespMsg(ErrorCodeEnum.FAILURE.getDesc());
        //设备类型,默认pc
        String deviceType = DeviceTypeEnum.PC.getCode();
        //跳转页面
        String page = "";
        //打印日志使用，拼接商户号，支付类型，商户订单号，
        String midoid = "";
        //客服信息
        String qq = "";
        String mobile = "";
        try {
            midoid = "商户号："+request.getParameter("mchtId")+"-->支付类型："+request.getParameter("biz")+"-->商户订单号："+request.getParameter("orderId")+"-->";
            //设备类型 如果商户没有传，就由我们自己来通过程序判断，由于不同的设备类型对应页面不一样，且掉支付的方式也不一样，所以会根据设备类型来做判断
            deviceType = request.getParameter("deviceType");
            logger.info(BIZ+midoid+"请求参数中获取的deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            String userAgent = "";
            if(StringUtils.isEmpty(deviceType)){
                logger.info(BIZ+midoid+"请求参数中未获取到deviceType，需要通过程序获取deviceType的值");
                //根据userAgent判断设备类型：pc、手机端、微信内(针对公众号支付)
                userAgent = this.getUserAgentInfoByRequest(request);
                logger.info(BIZ+midoid+"根据请求头获取的user-agent为："+userAgent);
                deviceType = HttpUtil.getDeviceType(userAgent);
                logger.info(BIZ+midoid+"商户没传设备类型，通过程序判断deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            }
            //解析并校验商户请求参数
            TradeCashierRequest requestInfo = null;
            result = gwCashierService.resolveAndcheckParam(request);
            logger.info(BIZ+midoid+"解析并校验商户请求参数后的结果为："+ JSONObject.toJSONString(result));
            if(ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
                //商户请求参数校验通过
                requestInfo = (TradeCashierRequest) result.getData();
                //商户ID+支付类型+商户订单ID
                //获取请求ip，值必须真实，某些上游通道要求必须是真实ip
                //老网关转发的请求，会带过来真实ip,另外商户也可以传ip，如果请求参数的ip为空，我们才通过程序获取
                String ip = requestInfo.getBody().getIp();
                if(StringUtils.isBlank(ip)){
                    ip = IpUtil.getRemoteHost(request);
                }
                logger.info(BIZ+midoid+"获取的请求ip为："+ip);
                logger.info(BIZ+midoid+"最终确定的deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
                //调用handler处理业务
                result = iTradeCashierMchtHandler.process(requestInfo, ip, deviceType);
                logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，传入的请求参数是："+JSONObject.toJSONString(requestInfo)+",IP："+ip+"，deviceType："+deviceType+"，返回的数据为："+JSONObject.toJSONString(result));
                if(null != result && ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
                    if(PayTypeEnum.CASHIER_PLAT.getCode().equals(requestInfo.getHead().getBiz())){
                        //收银台页面跳转
                        page = this.getPageByDeviceType(deviceType, PageTypeEnum.INDEX.getCode());
                        //设置收银台页面需要的值
                        this.addCashierModelInfo(model, result, requestInfo.getBody().getGoods(), requestInfo.getBody().getAmount(), requestInfo.getHead().getMchtId() );
                        logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用收银台页面，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);
                    }else{
                        //非收银台页面跳转
                        page = this.chooseNotCashierPage(deviceType, requestInfo.getHead().getBiz());
                        if(page.endsWith("scan")){
                            //设置扫码中间页需要的参数
                            this.addScanCentPageModelInfo(model, result);
                        }else if(page.endsWith("center")){
                            //设置h5和公众号支付的中间页需要的参数
                            this.addH5CentPageModelInfo(model, result, userAgent);
                        }
                        logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用中间页，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);
                    }
                }else{
                    page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
                    //客服信息
                    if(result != null && null != result.getData() &&  (result.getData() instanceof Map)){
                        qq = (String) ((Map) result.getData()).get("qq");
                        mobile = (String) ((Map) result.getData()).get("mobile");
                        model.addAttribute("qq", qq);
                        model.addAttribute("mobile", mobile);
                    }
                    logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为失败，返回的CommonResult="+JSONObject.toJSONString(result));
                }
            }else{
                page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
                logger.info(BIZ+midoid+"解析并校验商户请求参数失败："+ JSONObject.toJSONString(result) );
                model.addAttribute("qq", qq);
                model.addAttribute("mobile", mobile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setRespCode(ErrorCodeEnum.E8001.getCode());
            result.setRespMsg(ErrorCodeEnum.E8001.getDesc());
            model.addAttribute("qq", qq);
            model.addAttribute("mobile", mobile);
            page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
            logger.error(BIZ+midoid+"接收商户请求异常："+e.getMessage());
        }
        model.addAttribute("respCode",result.getRespCode());
        model.addAttribute("respMsg",result.getRespMsg());

        logger.info(BIZ+midoid+"处理完业务之后，返回给页面的数据为："+JSONObject.toJSONString(model));
        return page;
    }

    /**
     * 页面轮询查单
     */
    @RequestMapping(value="queryResult")
    @ResponseBody
    public String queryResult(HttpServletRequest request) throws Exception {
        String status = PayStatusEnum.CREATE_SUCCESS.getCode();
        String platOrderId = "";
        try {
            String ip = IpUtil.getRemoteHost(request);
            platOrderId = request.getParameter("platOrderId");
            //调用handler处理业务
           CommonResult result = iTradeCashierMchtHandler.queryStatus(ip, platOrderId);
           logger.info("页面轮训查单，订单platOrderId="+platOrderId+"，查询结果：CommonResult="+ JSONObject.toJSONString(result));
           if(null != result &&  null != result.getData()){
                status = (String) result.getData();
                if(PayStatusEnum.PAY_SUCCESS.getCode().equals(status)){
                    logger.info("页面轮训查单，订单platOrderId="+platOrderId+"，查询结果此订单成功，返回页面status="+PayStatusEnum.PAY_SUCCESS.getCode());
                    return PayStatusEnum.PAY_SUCCESS.getCode();
                }
           }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(platOrderId+"页面轮训查单请求异常："+e.getMessage());
        }
        logger.info("页面轮训查单，订单platOrderId="+platOrderId+"，查询此订单结果，返回页面status="+status);
        return status;
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
    public String test(HttpServletRequest request, Model model) {

        System.out.println("测试页面"+request.getServerName()+request.getServerPort());
        int port = request.getServerPort();
        model.addAttribute("testUrl", request.getServerName()+(80 == port ? "" : ":" +port));
        model.addAttribute("mchtOrderId", IdUtil.getUUID());
        return "modules/cashier/pc/test";

    }
}
