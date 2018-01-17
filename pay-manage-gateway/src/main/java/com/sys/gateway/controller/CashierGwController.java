package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.response.cashier.TradeCashierResponse;
import com.sys.boss.api.service.trade.TradeCashierService;
import com.sys.boss.api.service.trade.TradeOrderService;
import com.sys.boss.api.service.trade.handler.ITradeCashierHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.SignUtil;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.CashierGwService;
import com.sys.trans.api.entry.Order;
import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 收银台控制器
 */
@Controller
@RequestMapping("gateway/cashier")
public class CashierGwController {
    private Logger logger = LoggerFactory.getLogger(CashierGwController.class);
    @Autowired
    private CashierGwService cashierGwService;
    @Autowired
    private ITradeCashierHandler tradeCashierHandler;
    @Autowired
    private TradeOrderService tradeOrderService;

    /**
     * 请求收银台页面
     * 老支付网关将老商户的入参校验后转发至新支付网关，以调起新收银台页面
     */
    @RequestMapping(value="call4oldGW")
    public String call4oldGW(HttpServletRequest request, RedirectAttributes attributes, Model model){

        CommonResult result = new CommonResult();
        boolean isMobileTerminal = HttpUtil.isMobileTerminal(request.getHeader("user-agent").toLowerCase());


        String data = URLDecoder.decode(request.getParameter("data"));
        logger.info("老网关转发过来的请求参数 data="+data);

        Map<String,Object> map = JSON.parseObject(data, Map.class);

        String mchAppId = (String)map.get("mchAppId");
        String service = (String)map.get("service");
        String deviceType = (String)map.get("deviceType");
        String body = (String)map.get("body");
        int totalFee = (int)map.get("totalFee");
        String serialId = (String)map.get("serialId");
        String callBackUrl = (String)map.get("callBackUrl");
        String notifyUrl = (String)map.get("notifyUrl");
        String openId = (String)map.get("openId");

        String code = (String)map.get("code");
        String desc = (String)map.get("desc");
        String ip = (String)map.get("ip");
        String userId = (String)map.get("userId");
        String sign = (String)map.get("sign");//签名由老网管校验，并且不传给新网关，暂时为空值


        if( !"30".equals(service)){
            logger.info("老网管的service参数不是30收银台");
            return "redirect:/error";
        }

        attributes.addAttribute("oldGwData",URLEncoder.encode(data));

        return "redirect:"+"/gateway/cashier/call";
    }

    /**
     * 请求收银台页面
     */
    @RequestMapping(value="call")
    public String call(HttpServletRequest request,  Model model) throws Exception {

        String aaa = request.getParameter("attr");
        request.setCharacterEncoding("UTF-8");

        String oldGwData = request.getParameter("oldGwData");
        if(StringUtils.isNotBlank(oldGwData)){
            //TODO:老网管重定向过来的请求
            String data = URLDecoder.decode(oldGwData);
        }


        CommonResult result = new CommonResult();
        boolean isMobileTerminal = HttpUtil.isMobileTerminal(request.getHeader("user-agent").toLowerCase());
        String page = isMobileTerminal? "modules/cashier/mobile/error":"modules/cashier/pc/error";
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info("收银台获取到客户端请求ip为：ip="+ip+" 设备类型："+(isMobileTerminal?"移动端":"pc端"));

            result = cashierGwService.checkParam(request);
            if(ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
                result = tradeCashierHandler.call(result.getData(),ip);
            }

            if(ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
                page = isMobileTerminal? "modules/cashier/mobile/index":"modules/cashier/pc/index";
                model.addAttribute("tradeCashierResponse",result.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("请求收银台页面异常："+e.getMessage());
            result.setRespCode(ErrorCodeEnum.E8001.getCode());
            result.setRespMsg(ErrorCodeEnum.E8001.getDesc());
        }
        model.addAttribute("commonResult",result);
        page="modules/cashier/pc/index";
        return page;
    }


    /**
     * 移动端：H5收银台页面请求支付，重定向到支付链接，即可调用手机端app进行支付
     */
    @RequestMapping("pay/{payType}/{sign}")
    public String pay(@PathVariable String payType,@PathVariable String sign, HttpServletRequest request){
        logger.info("收银台移动端支付请求参数 payType="+payType+" sign="+sign);
        CommonResult commonResult = new CommonResult();
        String page = "modules/cashier/mobile/error";
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            commonResult = tradeCashierHandler.pay(payType,sign,ip);

//            if(ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())){
                //todo: 待boss-trade模块可以下单，再组装返回值
                Result tranResult = (Result) commonResult.getData();
                page = "redirect: https://statecheck.swiftpass.cn/pay/wappay?token_id=155e943b0c78277f2580fd34c278f2d3f&service=pay.weixin.wappayv2";
//            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("收银台支付异常："+e.getMessage());
            commonResult.setRespCode(ErrorCodeEnum.E8001.getCode());
            commonResult.setRespMsg(ErrorCodeEnum.E8001.getDesc());
        }
        return page;
    }

    /**
     * pc端：pc收银台页面通过ajax发起支付，返回支付二维码地址
     */
    @RequestMapping("ajaxPay")
    @ResponseBody
    public String ajaxPay(String payType,String sign, HttpServletRequest request){
        logger.info("收银台PC支付请求参数 payType="+payType+" sign="+sign);
        CommonResult commonResult = new CommonResult();
        commonResult.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
        TradeCashierResponse response = new TradeCashierResponse();
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            commonResult = tradeCashierHandler.pay(payType,sign,ip);
            //todo: 待boss-trade模块可以下单，再组装返回值
//            if(ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())){
               response.setPayUrl("http://newpay.kspay.net:8181/ks_smpay/pay/qrcode?codeuuid=https%3A%2F%2Fqr.95516.com%2F00010001%2F62792205567773205232715092016565");
               response.setPlatOrderId("P5654645");
//            }
            commonResult.setData(response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("收银台支付异常："+e.getMessage());
            commonResult.setRespCode(ErrorCodeEnum.E8001.getCode());
            commonResult.setRespMsg(ErrorCodeEnum.E8001.getDesc());
        }
        return JSON.toJSONString(commonResult);
    }


    /**
     * 支付完成后，跳转到商家页面
     */
    @RequestMapping("payResult/{platOrderId}/{sign}")
    public String payResult(@PathVariable String platOrderId,@PathVariable String sign){
        //todo: 向上游下单时，callback值为 http://ip:port/gateway/cashier/payResult/{platOrderId}/{sign}
        //1、校验签名

        //2、缓存里查询订单
        Trade trade = (Trade) tradeOrderService.query(platOrderId);
        Order order = trade.getOrder();
        return "redirect:"+order.getFrontNotifyUrl();
    }

    /**
     * 查单接口
     * <p>
     *     pc端页面定时查询订单状态，当订单支付完成，跳转到商户页面
     * </p>
     */
    @RequestMapping("queryResult")
    @ResponseBody
    public String queryResult(String platOrderId){
//        Trade trade = (Trade) tradeOrderService.query(platOrderId);
        //todo: 缓存里trade对象没有存订单的最终状态
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("status","1");
        resultMap.put("callbackUrl","https://www.baidu.com");
        return JSON.toJSONString(resultMap);
    }



    /********************* 收银台测试demo，后期提出来，封装成单独小项目 *********************/

    @RequestMapping("test")
    public String test(){
        return "modules/cashier/pc/test";
    }

    @RequestMapping("genSign")
    @ResponseBody
    public String genSign(HttpServletRequest request){
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("amount",request.getParameter("amount"));
        paramMap.put("desc",request.getParameter("desc"));
        paramMap.put("goods",request.getParameter("goods"));
        paramMap.put("notifyUrl",request.getParameter("notifyUrl"));
        paramMap.put("orderId",request.getParameter("orderId"));
        paramMap.put("operator",request.getParameter("operator"));
        paramMap.put("orderTime",request.getParameter("orderTime"));
        String mchtKey = request.getParameter("mchtKey");

        String sign = null;
        try {
            sign = SignUtil.md5Sign(paramMap,mchtKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }
}
