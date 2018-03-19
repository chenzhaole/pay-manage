package com.sys.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.cashier.TradeCashierRequest;
import com.sys.boss.api.service.trade.handler.ITradeCashierMchtHandler;
import com.sys.common.enums.DeviceTypeEnum;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PageTypeEnum;
import com.sys.common.util.HttpUtil;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwCashierMchtService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 第三方支付网页版sdk--接收收银台下单的请求
 */
@Controller
@RequestMapping("gateway/cashier")
public class GwCashierPlatController {
    private Logger logger = LoggerFactory.getLogger(GwCashierPlatController.class);

    @Autowired
    private GwCashierMchtService gwCashierService;

    @Autowired
    private ITradeCashierMchtHandler iTradeCashierMchtHandler;

    private static final String BIZ = "网页支付GwCashierController->";



    /**
     * 解析并校验请求参数
     */
    @RequestMapping(value="platCall")
    public String platCall(HttpServletRequest request, Model model) throws Exception {
        CommonResult result = new CommonResult();
        //设备类型,默认pc
        String deviceType = DeviceTypeEnum.PC.getCode();
        //跳转页面
        String page = "";
        try {
            //设备类型 如果商户没有传，就由我们自己来通过程序判断，由于不同的设备类型对应页面不一样，且掉支付的方式也不一样，所以会根据设备类型来做判断
            deviceType = request.getParameter("deviceType");
            logger.info(BIZ+"请求参数中获取的deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            if(StringUtils.isEmpty(deviceType)){
                logger.info(BIZ+"请求参数中未获取到deviceType，需要通过程序获取deviceType的值");
                //根据userAgent判断设备类型：pc、手机端、微信内(针对公众号支付)
                String userAgent = this.getUserAgentInfoByRequest(request);
                logger.info(BIZ+"根据请求头获取的user-agent为："+userAgent);
                deviceType = HttpUtil.getDeviceType(userAgent);
                logger.info(BIZ+"商户没传设备类型，通过程序判断deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
            }
            //解析并校验商户请求参数
            TradeCashierRequest requestInfo = null;
            result = gwCashierService.resolveAndcheckParam(request);
            logger.info(BIZ+"解析并校验商户请求参数后的结果为："+ JSONObject.toJSONString(result));
            if(ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
                //商户请求参数校验通过
                requestInfo = (TradeCashierRequest) result.getData();
                //商户ID+支付类型+商户订单ID
                String midoid = requestInfo.getHead().getMchtId()+"-->"+requestInfo.getHead().getBiz()+"-->"+requestInfo.getBody().getOrderId();
                //获取请求ip，值必须真实，某些上游通道要求必须是真实ip
                //老网关转发的请求，会带过来真实ip,另外商户也可以传ip，如果请求参数的ip为空，我们才通过程序获取
                String ip = requestInfo.getBody().getIp();
                if(StringUtils.isBlank(ip)){
                    ip = IpUtil.getRemoteHost(request);
                }
                logger.info(BIZ+","+midoid+"，获取的请求ip为："+ip);
                logger.info(BIZ+","+midoid+"，请求参数中获取的deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
                //将设备类型和ip传进去，以便handler使用
                requestInfo.getBody().setDeviceType(deviceType);
                requestInfo.getBody().setIp(ip);
                //调用handler处理业务
//                result = iTradeCashierMchtHandler.process(requestInfo, ip);
                logger.info(BIZ+","+midoid+"，调用TradeCashierMchtHandler处理业务逻辑，传入的请求参数是："+JSONObject.toJSONString(requestInfo)+",IP："+ip+"，返回的数据为："+JSONObject.toJSONString(result));
                if(null != result && ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
                    //根据返回结果，确定跳转页面
                    if(DeviceTypeEnum.MOBILE.getCode().equals(deviceType) || DeviceTypeEnum.WECHAT.getCode().equals(deviceType)){
                       //手机端和微信内，需要判断展示哪一种中间页,因为h5支付和公众号支付使用的是一种中间页，扫码支付使用的是另一种中间页


                    }else if(DeviceTypeEnum.PC.getCode().equals(deviceType)){
                        //pc页面


                    }
                }else{
                    page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
                }
            }else{
                page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
                logger.info(BIZ+"解析并校验商户请求参数失败："+ JSONObject.toJSONString(result) );
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setRespCode(ErrorCodeEnum.E8001.getCode());
            result.setRespMsg(ErrorCodeEnum.E8001.getDesc());
            page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode());
            logger.error(BIZ+"接收商户请求异常："+e.getMessage());
        }
        return page;
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
        }else if(DeviceTypeEnum.PC.getCode().equals(deviceType)){
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
