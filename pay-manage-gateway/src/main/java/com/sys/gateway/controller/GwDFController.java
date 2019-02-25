package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.df.TradeDFCreateOrderRequest;
import com.sys.boss.api.entry.trade.request.df.TradeDFQueryBalanceRequest;
import com.sys.boss.api.entry.trade.request.df.TradeDFQueryOrderRequest;
import com.sys.boss.api.entry.trade.response.df.DFCreateOrderResponse;
import com.sys.boss.api.entry.trade.response.df.DFQueryBalanceResponse;
import com.sys.boss.api.entry.trade.response.df.DFQueryOrderResponse;
import com.sys.boss.api.service.trade.handler.ITradeDFBalanceChanHandler;
import com.sys.boss.api.service.trade.handler.ITradeDFBalancePlatHandler;
import com.sys.boss.api.service.trade.handler.ITradeDFBatchHandler;
import com.sys.boss.api.service.trade.handler.ITradeDFCreateHandler;
import com.sys.boss.api.service.trade.handler.ITradeDFQueryChanHandler;
import com.sys.boss.api.service.trade.handler.ITradeDFQueryPlatHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.*;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.ChanMchtPaytype;
import com.sys.core.service.TaskLogService;
import com.sys.gateway.common.ConfigUtil;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.impl.GwPublicAccountServiceImpl;
import com.sys.trans.api.entry.ChanMchtPaytypeTO;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.SingleDF;
import com.sys.trans.api.entry.Trade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("df/gateway")
public class GwDFController {
    private Logger logger = LoggerFactory.getLogger(GwDFController.class);

    @Autowired
    private ITradeDFCreateHandler tradeDFCreateHandler;
    @Autowired
    private ITradeDFBalancePlatHandler tradeDFBalancePlatHandler;
    @Autowired
    private ITradeDFBalanceChanHandler tradeDFBalanceChanHandler;
    @Autowired
    private ITradeDFQueryPlatHandler tradeDFQueryPlatHandler;
    @Autowired
    private ITradeDFQueryChanHandler tradeDFQueryChanHandler;
    @Autowired
    private ITradeDFBatchHandler tradeDFBatchHandler;
    @Autowired
    private TaskLogService taskLogService;

    @Autowired
    private GwPublicAccountServiceImpl gwPublicAccountService;



    private static final String CREATED_DF_ORDER_URL = ConfigUtil.getValue("created_df_order_url");
    /**
     * 代付请求接口
     */
    @RequestMapping(value="req",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String req(@RequestBody String data, HttpServletRequest request){
        //2018年7月27日，通服-创海商户联调代付api，现网接口传输的报文最后一个字母多了一个=号，针对此问题特殊处理
        if(data.endsWith("=")){
            logger.info("代付API，【代付请求接口】收到客户端请求参数,最后一个字母为=号，需要截取掉，截取之前的值为data="+data);
            data = data.substring(0, data.length()-1);
        }
        logger.info("代付API，【代付请求接口】收到客户端请求参数：data="+data);
        DFCreateOrderResponse resp = new DFCreateOrderResponse();
        DFCreateOrderResponse.DFCreateResponseHead head = new DFCreateOrderResponse.DFCreateResponseHead();

        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info("代付API，【代付请求接口】获取到客户端请求ip为：ip="+ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("代付API，【代付请求接口】收到客户端请求参数后做url解码后的值为：data="+data);

            //校验请求参数
            CommonResponse checkResp = tradeDFCreateHandler.checkParam(data,ip);
            logger.info("代付API，【代付请求接口】校验请求参数的结果为："+ JSONObject.toJSONString(checkResp));

            if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                resp.setHead(head);
            }else{
                TradeDFCreateOrderRequest tradeRequest = (TradeDFCreateOrderRequest) checkResp.getData();
                logger.info("代付API，【代付请求接口】传入的TradeProxyPayCreateOrderRequest信息为：TradeProxyPayCreateOrderRequest="+JSONObject.toJSONString(tradeRequest));
                resp = JSON.parseObject(tradeDFCreateHandler.create(tradeRequest, ip),DFCreateOrderResponse.class);
                logger.info("代付API，【代付请求接口】返回的ProxyPayCreateOrderResponse信息为：ProxyPayCreateOrderResponse="+JSONObject.toJSONString(resp));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("代付API，【代付请求接口】接口抛异常"+e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("代付网关错误："+e.getMessage());
        }
        logger.info("代付API，【代付请求接口】返回下游商户值："+JSON.toJSONString(resp));
        return JSON.toJSONString(resp);
    }


    /**
     *  代付余额查询
     */
    @RequestMapping(value="balance",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String balance(@RequestBody String data, HttpServletRequest request){
        logger.info("代付API，【代付余额查询接口】收到客户端请求参数：data="+data);
        DFQueryBalanceResponse resp = new DFQueryBalanceResponse();
        DFQueryBalanceResponse.DFQueryBalanceResponseHead head = new DFQueryBalanceResponse.DFQueryBalanceResponseHead();

        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info("代付API，【代付余额查询接口】获取到客户端请求ip为：ip="+ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("代付API，【代付余额查询接口】收到客户端请求参数后做url解码后的值为：data="+data);


            //校验请求参数
            CommonResponse checkResp = tradeDFBalancePlatHandler.checkParam(data,ip);
            logger.info("代付API，【代付余额查询接口】校验请求参数的结果为："+ JSONObject.toJSONString(checkResp));

            if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                resp.setHead(head);
            }else{
                TradeDFQueryBalanceRequest tradeRequest = (TradeDFQueryBalanceRequest) checkResp.getData();
                logger.info("代付API，【代付余额查询接口】传入的TradeDFQueryBalanceRequest信息为：TradeDFQueryBalanceRequest="+JSONObject.toJSONString(tradeRequest));
                resp = JSON.parseObject(tradeDFBalancePlatHandler.balance(tradeRequest, ip),DFQueryBalanceResponse.class);
                logger.info("代付API，【代付余额查询接口】返回的DFQueryBalanceResponse信息为：DFQueryBalanceResponse="+JSONObject.toJSONString(resp));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("代付API，【代付余额查询接口】接口抛异常"+e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("代付网关错误："+e.getMessage());
        }
        logger.info("代付API，【代付余额查询接口】返回下游商户值："+JSON.toJSONString(resp));
        return JSON.toJSONString(resp);
    }

    /**
     *  代付余额查询（admin使用）
     */
    @RequestMapping(value="balanceForAdmin")
    @ResponseBody
    public String balanceForAdmin(String mchtId, HttpServletRequest request){
        logger.info("代付API，【代付余额查询接口】收到Admin请求参数：mchtId="+mchtId);
        if (StringUtils.isBlank(mchtId)){
            return "0";
        }
        CommonResult balanceResponse = new CommonResult();
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info("代付API，【代付请求接口】获取到客户端请求ip为：ip="+ip);
            balanceResponse = tradeDFBalancePlatHandler.process(mchtId);
            logger.info("代付API，【代付余额查询接口】返回的信息为：="+JSONObject.toJSONString(balanceResponse));

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("代付API，【代付余额查询接口】接口抛异常"+e.getMessage());
            balanceResponse.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            balanceResponse.setRespMsg("代付网关错误："+e.getMessage());
        }
        logger.info("代付API，【代付余额查询接口】返回下游商户值："+JSON.toJSONString(balanceResponse));
        return balanceResponse.getData().toString();
    }

    /**
     *  通道余额查询（admin使用）
     */
    @RequestMapping(value="chanBalanceForAdmin")
    @ResponseBody
    public String chanBalanceForAdmin(String tradeString, HttpServletRequest request){
        logger.info("代付API，【通道余额查询接口】收到Admin请求参数：trade="+tradeString);
        Trade trade = JSON.parseObject(tradeString, Trade.class);
        if (trade == null){
            return "";
        }
        CommonResult balanceResponse = new CommonResult();
        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info("代付API，【代付请求接口】获取到客户端请求ip为：ip="+ip);
            balanceResponse = tradeDFBalanceChanHandler.process(trade);
            logger.info("代付API，【通道余额查询接口】返回的信息为：="+JSONObject.toJSONString(balanceResponse));

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("代付API，【通道余额查询接口】接口抛异常"+e.getMessage());
            balanceResponse.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            balanceResponse.setRespMsg("代付网关错误："+e.getMessage());
        }
        logger.info("代付API，【通道余额查询接口】返回admin："+JSON.toJSONString(balanceResponse));
        return JSON.toJSONString(balanceResponse);
    }

    /**
     * 代付订单,平台查询接口
     */
    @RequestMapping(value="query",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String query(@RequestBody String data,HttpServletRequest request){
        logger.info("代付API，【代付查单接口】收到客户端请求参数：data="+data);
        DFQueryOrderResponse resp = new DFQueryOrderResponse();
        DFQueryOrderResponse.DFQueryOrderResponseHead head = new DFQueryOrderResponse.DFQueryOrderResponseHead();

        try{
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info("代付API，【代付查单接口】获取到客户端请求ip为：ip="+ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("代付API，【代付查单接口】收到客户端请求参数后做url解码后的值为：data="+data);
            //校验请求参数
            CommonResponse checkResp = tradeDFQueryPlatHandler.checkParam(data,ip);
            logger.info("代付API，【代付查单接口】校验请求参数的结果为："+ JSONObject.toJSONString(checkResp));

            if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                resp.setHead(head);
            }else{
                TradeDFQueryOrderRequest tradeRequest = (TradeDFQueryOrderRequest) checkResp.getData();
                logger.info("代付API，【代付查单接口】传入的TradeDFQueryOrderRequest信息为：TradeDFQueryOrderRequest="+JSONObject.toJSONString(tradeRequest));
                resp = JSON.parseObject(tradeDFQueryPlatHandler.query(tradeRequest, ip),DFQueryOrderResponse.class);
                logger.info("代付API，【代付查单接口】返回的DFQueryOrderResponse信息为：DFQueryOrderResponse="+JSONObject.toJSONString(resp));
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("代付API，【代付查单接口】接口抛异常"+e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("代付网关错误："+e.getMessage());
        }
        logger.info("代付API，【代付查单接口】返回下游商户值："+JSON.toJSONString(resp));
        return JSON.toJSONString(resp);
    }

    /**
     * 首次发起代付
     * -查询代付状态为 审核中 的代付订单，并向上游发起
     * -若余额不足或查询余额失败，将代付状态置为 审核通过
     */
    /*@RequestMapping("taskProxyPayFirstTime")
    @ResponseBody
    public String taskProxyPayFirstTime(@RequestParam(required = false,value = "id") Integer logId){
//        logger.debug("代付API，【定时任务发起代付】任务执行logId开始："+logId);
        CommonResult result = tradeDFBatchHandler.process(true);
        taskLogService.recordLog(logId,result);
        logger.info("代付API，【定时任务发起代付】任务执行logId结束："+logId+" "+JSON.toJSONString(result));
        return "ok";
    }*/

    /**
     * 定时发起代付
     * -查询代付状态为 审核通过 的代付订单，并向上游发起
     */
    @RequestMapping("taskProxyPay")
    @ResponseBody
    public String taskProxyPay(@RequestParam(required = false,value = "id") Integer logId){
        logger.debug("代付下单API，【定时任务发起代付】任务执行logId开始："+logId);
        //CommonResult result = tradeDFBatchHandler.process(false);
        CommonResult result = new CommonResult();
        try {
            String res = PostUtil.postMsg((CREATED_DF_ORDER_URL), "false");
            logger.debug("代付下单API，【定时任务发起代付】任务执行logId开始："+logId+",res"+ res);
            result = JSON.parseObject(res, CommonResult.class);
        } catch (Exception e) {
            logger.error("代付基类调用TranWeb异常,errorMsg=" + e.getMessage(), e);
            result.setRespMsg(e.getMessage());
        }
        taskLogService.recordLog(logId,result);
        logger.info("代付下单API，【定时任务发起代付】任务执行logId结束："+logId+" "+JSON.toJSONString(result));
        return "ok";
    }

    /**
     * 定时任务代付查单
     */
    @RequestMapping("taskProxyQuery")
    @ResponseBody
    public String taskProxyQuery(@RequestParam(required = false,value = "id") Integer logId){
        logger.info("代付API，【定时任务代付查单】任务执行logId开始："+logId);
        CommonResult result = tradeDFQueryChanHandler.process(null);
        taskLogService.recordLog(logId,result);
        logger.info("代付API，【定时任务代付查单】任务执行logId结束："+logId+" "+JSON.toJSONString(result));
        return "ok";
    }


    /**
     * 定时器查询生效的通道余额大于20W发送信息到公众号
     * @return
     */
    @RequestMapping("taskProxyQueryBalance")
    @ResponseBody
    public String taskProxyQueryBalance(@RequestParam(required = false,value = "id") Integer logId){
        ChanMchtPaytype chanMchtPaytype = new ChanMchtPaytype();
        chanMchtPaytype.setPayType(PayTypeEnum.SINGLE_DF.getCode());
        chanMchtPaytype.setStatus(StatusEnum.VALID.getCode());
        List<ChanMchtPaytype> chanMchtPaytypeList = tradeDFBalancePlatHandler.queryChanMchtPaytypesByExample(chanMchtPaytype);

        Map<String, ChanInfo> chanInfoMap = queryChanInfoMap();

        for(ChanMchtPaytype cmp: chanMchtPaytypeList){
            if(StringUtils.isEmpty(cmp.getChanCode())){
                logger.info("定时器查询生效的通道余额大于20W发送信息到公众号,通道编号是空");
                continue;
            }
            if(cmp.getChanCode().equals("gfb") || cmp.getChanCode().equals("yinyingtongnew") ||
                    cmp.getChanCode().equals("hangzhoucityzencard") ||cmp.getChanCode().equals("xianfeng")){
                logger.info("定时器查询生效的通道余额大于20W发送信息到公众号,通道编号是:" + cmp.getChanCode() + ",不校验.");
                continue;
            }
            queryChanMchtPaytypeBalance(cmp, chanInfoMap);
        }
        CommonResult result = new CommonResult();
        result.setRespMsg("SUCCESS");
        result.setRespCode("000000");
        taskLogService.recordLog(logId, result);
        logger.info("定时器查询生效的通道余额大于20W发送信息到公众号："+logId+" "+JSON.toJSONString(result));
        return "ok";



    }



    public String queryChanMchtPaytypeBalance(ChanMchtPaytype chanMchtPaytype, Map<String, ChanInfo> chanInfoMap){
        String reportAnEmergencyUrl = ConfigUtil.getValue("report_an_emergency_url");

        Config config = new Config();
        ChanMchtPaytypeTO chanMchtPaytypeTO = new ChanMchtPaytypeTO();
        org.springframework.beans.BeanUtils.copyProperties(chanMchtPaytype, chanMchtPaytypeTO);
        config.setChanMchtPaytype(chanMchtPaytypeTO);
        config.setPayUrl(chanMchtPaytype.getPayUrl());
        config.setQueryUrl(chanMchtPaytype.getQueryBalanceUrl());
        config.setTranUrl(chanMchtPaytype.getTranUrl());
        config.setChannelCode(chanMchtPaytype.getChanCode());
        config.setCancelUrl(chanMchtPaytype.getCancelUrl());
        config.setNotifyUrl(chanMchtPaytype.getAsynNotifyUrl());
        config.setPayType(chanMchtPaytype.getPayType());
        config.setMchtId(chanMchtPaytype.getChanMchtNo());
        config.setMchtKey(chanMchtPaytype.getChanMchtPassword());
        config.setCertPath1(chanMchtPaytype.getCertPath1());
        config.setCertPath2(chanMchtPaytype.getCertPath2());
        config.setPlatId(chanMchtPaytype.getTerminalNo());
        config.setPubKey(chanMchtPaytype.getCertContent1());
        config.setPriKey(chanMchtPaytype.getCertContent2());
        config.setMerchantName(chanMchtPaytype.getOpAccount());
        try {
            config.setExtend(URLEncoder.encode(chanMchtPaytype.getCertContent3(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("平台公钥编码错误", e);
        }

        SingleDF df = new SingleDF();
        df.setOrderNo("ADMIN0" + IdUtil.createCode());
        df.setAmount("1");

        Trade trade = new Trade();
        trade.setConfig(config);
        trade.setSingleDF(df);

        try {
            String topUrl = ConfigUtil.getValue("gateway.url");
            if (topUrl.endsWith("/")) {
                topUrl = topUrl.substring(0, topUrl.length() - 1);
            }
            String gatewayUrl = topUrl + "/df/gateway/chanBalanceForAdmin";
            Map<String, String> params = new HashMap<>();
            params.put("tradeString", JSON.toJSONString(trade));
            logger.info(trade + " 查询上游商户余额,请求URL: " + gatewayUrl + " 请求参数: " + JSON.toJSONString(params));
            String balanceString = HttpUtil.postConnManager(gatewayUrl, params, true);
            logger.info(balanceString);
            CommonResult processResult = JSON.parseObject(balanceString, CommonResult.class);
            if (processResult != null) {
                String balance = (String) processResult.getData();
                if (StringUtils.isNotBlank(balance)){
                    balance = NumberUtils.changeF2Y(balance);
                    String chanName = null;
                    ChanInfo chanInfo = chanInfoMap.get(chanMchtPaytype.getChanCode());
                    if(chanInfo!= null){
                        chanName = chanInfo.getName();
                    }
                    String content = "时间:" + DateUtils.getDateTime() + ",通道名称:" + chanMchtPaytype.getName() + ",通道余额为:" + balance;
                    if(new BigDecimal(balance).compareTo(new BigDecimal(150000))  >= 0 ){
                        logger.info("告警内容为:" + content);
                        Map<String, String> contentMap = new HashMap<>();
                        contentMap.put("content", content);
                        String currentTime = DateUtils.getDateTime();
                        currentTime = currentTime.replaceAll(" ", "%20");
                        reportAnEmergencyUrl = String.format("%s%s", reportAnEmergencyUrl, "&datetime=" + currentTime);
                        String responseDate = execPost(reportAnEmergencyUrl, contentMap);
                        logger.info("告警返回信息为:" + responseDate);
                    }else{
                        logger.info("内容为:" + content + "不告警");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询余额失败", e);
        }
        return "modules/channel/chanBalance";

    }


    public String execPost(String url, Map<String, String> contentMap){
        String responseDate = null;
        try {
            responseDate =  HttpUtil.postConnManager(url, contentMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return responseDate;
    }

    public Map<String, ChanInfo> queryChanInfoMap(){
        Map<String, ChanInfo> chanInfoMap = new HashMap<>();
        ChanInfo chanInfo = new ChanInfo();
        List<ChanInfo> chanInfos = tradeDFBalancePlatHandler.queryChanInfos(chanInfo);
        if(chanInfos== null || chanInfos.size() == 0){
            return chanInfoMap;
        }
        for(ChanInfo ci: chanInfos){
            chanInfoMap.put(ci.getId(), ci);
        }
        return chanInfoMap;

    }

}
