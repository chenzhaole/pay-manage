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
import com.sys.boss.api.service.trade.handler.*;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.core.service.TaskLogService;
import com.sys.gateway.common.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

@Controller
@RequestMapping("df/gateway")
public class GwDFController {
    private Logger logger = LoggerFactory.getLogger(GwDFController.class);

    @Autowired
    private ITradeDFReqHandler tradeDFReqHandler;
    @Autowired
    private ITradeDFBalanceHandler tradeDFBalanceHandler;
    @Autowired
    private ITradeDFQueryOrderHandler tradeDFQueryOrderHandler;
    @Autowired
    private ITradeDFBatchHandler tradeDFBatchHandler;
    @Autowired
    private ITradeDFQueryHandler tradeDFQueryHandler;
    @Autowired
    private TaskLogService taskLogService;

    /**
     * 代付请求接口
     */
    @RequestMapping(value="req",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String req(@RequestBody String data, HttpServletRequest request){
        logger.info("代付API，【代付请求接口】收到客户端请求参数：data="+data);
        DFCreateOrderResponse resp = new DFCreateOrderResponse();
        DFCreateOrderResponse.DFCreateResponseHead head = new DFCreateOrderResponse.DFCreateResponseHead();

        try {
            String ip = IpUtil.getRemoteHost(request);//请求ip
            logger.info("代付API，【代付请求接口】获取到客户端请求ip为：ip="+ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("代付API，【代付请求接口】收到客户端请求参数后做url解码后的值为：data="+data);

            //校验请求参数
            CommonResponse checkResp = tradeDFReqHandler.checkParam(data);
            logger.info("代付API，【代付请求接口】校验请求参数的结果为："+ JSONObject.toJSONString(checkResp));

            if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                resp.setHead(head);
            }else{
                TradeDFCreateOrderRequest tradeRequest = (TradeDFCreateOrderRequest) checkResp.getData();
                logger.info("代付API，【代付请求接口】传入的TradeProxyPayCreateOrderRequest信息为：TradeProxyPayCreateOrderRequest="+JSONObject.toJSONString(tradeRequest));
                resp = JSON.parseObject(tradeDFReqHandler.req(tradeRequest, ip),DFCreateOrderResponse.class);
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
            CommonResponse checkResp = tradeDFBalanceHandler.checkParam(data);
            logger.info("代付API，【代付余额查询接口】校验请求参数的结果为："+ JSONObject.toJSONString(checkResp));

            if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                resp.setHead(head);
            }else{
                TradeDFQueryBalanceRequest tradeRequest = (TradeDFQueryBalanceRequest) checkResp.getData();
                logger.info("代付API，【代付余额查询接口】传入的TradeDFQueryBalanceRequest信息为：TradeDFQueryBalanceRequest="+JSONObject.toJSONString(tradeRequest));
                resp = JSON.parseObject(tradeDFBalanceHandler.balance(tradeRequest, ip),DFQueryBalanceResponse.class);
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
     * 代付查单接口
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
            CommonResponse checkResp = tradeDFQueryOrderHandler.checkParam(data);
            logger.info("代付API，【代付查单接口】校验请求参数的结果为："+ JSONObject.toJSONString(checkResp));

            if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                resp.setHead(head);
            }else{
                TradeDFQueryOrderRequest tradeRequest = (TradeDFQueryOrderRequest) checkResp.getData();
                logger.info("代付API，【代付查单接口】传入的TradeDFQueryOrderRequest信息为：TradeDFQueryOrderRequest="+JSONObject.toJSONString(tradeRequest));
                resp = JSON.parseObject(tradeDFQueryOrderHandler.query(tradeRequest, ip),DFQueryOrderResponse.class);
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
     * 定时任务发起代付
     */
    @RequestMapping("execProxyPay")
    @ResponseBody
    public String execProxyPay(@RequestParam(required = false,value = "id") Integer logId){
        logger.info("代付API，【定时任务发起代付】任务执行logId开始："+logId);
        CommonResult result = tradeDFBatchHandler.process(null);
        taskLogService.recordLog(logId,result);
        logger.info("代付API，【定时任务发起代付】任务执行logId结束："+logId+" "+JSON.toJSONString(result));
        return "ok";
    }

    /**
     * 定时任务代付查单
     */
    @RequestMapping("execQuery")
    @ResponseBody
    public String execQuery(@RequestParam(required = false,value = "id") Integer logId){
        logger.info("代付API，【定时任务代付查单】任务执行logId开始："+logId);
        CommonResult result = tradeDFQueryHandler.process(null);
        taskLogService.recordLog(logId,result);
        logger.info("代付API，【定时任务代付查单】任务执行logId结束："+logId+" "+JSON.toJSONString(result));
        return "ok";
    }

}
