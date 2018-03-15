package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.PostUtil;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.gateway.service.NotifyService;
import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TransException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * 接收通道异步通知
 * 向商户发送异步通知
 *
 * Created by chenzhaole on 2018/3/15.
 */
@Controller
@RequestMapping(value = "")
public class RecNotifyController {
    protected final Logger logger = LoggerFactory.getLogger(RecNotifyController.class);

//    @Autowired
//    private MchtGwOrderService mchtGwOrderService;
//
//    @Autowired
//    private MerchantService merchantService;

    @Autowired
    private NotifyService notifyService;


    private final String BIZ = "接收异步通知-";

    /**
     * 接受统一异步通知结果
     * POST请求
     */
    @RequestMapping("/recNotify/{chanCode}/{platOrderId}/{payType}")
    @ResponseBody
    public String recNotify(@RequestBody String data, @PathVariable String chanCode, @PathVariable String platOrderId, @PathVariable String payType) throws TransException {
        logger.info(BIZ+"[START] chanCode=" + chanCode + " platOrderId=" + platOrderId + " data="+ data + "");

        String resp2chan = "FAILURE";
        CommonResult tradeResult = notifyService.notify4chan(chanCode,platOrderId,data);
        if(ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode())){
            //解析通道数据成功,更新数据库订单状态成功
            Trade redisOrderTrade = (Trade) tradeResult.getData();
            logger.info("bossTrade查询的缓存订单Trade对象:"+JSON.toJSONString(redisOrderTrade));


            CommonResult serviceResult = notifyService.notify2mcht(payType,redisOrderTrade);
            if(ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())){
                logger.info("通知商户成功");
                //存库成功,通知商户成功,最终猜响应给通道成功
                resp2chan = tradeResult.getRespMsg();//TODO:按时使用该字段存储返回通道的响应值
            }else{
                logger.info("通知商户失败");
            }

        }else{
            logger.info("bossTrade查询的缓存订单Trade对象,失败."+JSON.toJSONString(tradeResult));
        }


        logger.info("接收上游通道异步通知接口-END,返回通道响应: "+resp2chan);
        return resp2chan;
    }




//	/**
//	 * 接受统一异步通知结果
//	 * POST请求
//	 */
//	@RequestMapping("/recNotify_BK/{chanCode}/{platOrderId}")
//	@ResponseBody
//	public String recNotify_BK(@RequestBody String data, @PathVariable String chanCode, @PathVariable String platOrderId) throws TransException {
//		logger.info(BIZ+"[START] chanCode=" + chanCode + " platOrderId=" + platOrderId + " data="+ data + "");
//		Result tranResult = notFindChannelResult();
//		CommonResult bossResult = new CommonResult();
//
//		String resp2chan = "FAILSE";
//		try {
//			Trade trade = (Trade) tradeOrderService.query(platOrderId);
//			if (trade == null) {
//				logger.info("查询原始Trade信息返回：null");
//				return resp2chan;
//			}
//			logger.info("查询原始Trade信息返回：" + JSON.toJSONString(trade));
//
//			Object mchtReqData = trade.getData();
//			Config cf = new Config();
//			cf.setChannelCode(chanCode);
//			Trade td = new Trade();
//			td.setConfig(cf);
//			logger.info("选择通道service对象 chanCode=" + chanCode);
//			IChannelService service = getChannelService(td);
//			logger.info("选择通道service对象 service=" + service);
//			if (service != null) {
//				trade.setData(data);
//				tranResult = service.reciveNotify(trade);
//			} else {
//				logger.info("接收上游通道异步通知接口[ERROR] 未找到 chanCode=[" + chanCode + "],platOrderId=[" + platOrderId + "] 业务处理service类");
//			}
//
//			if (Result.RESP_CODE_SUCCESS.equals(tranResult.getRespCode())) {
//				Object[] dataObjs = {trade.getData(), mchtReqData};
//				trade.setData(dataObjs);
//				bossResult = (CommonResult) tradeRecNotifyHandler.process(trade);
//				logger.info("调用tradeNotifyService异步通知服务返回值：" + JSON.toJSONString(bossResult));
//			}
//
//			if ((Result.RESP_CODE_SUCCESS.equals(tranResult.getRespCode()))
//					&& (ErrorCodeEnum.SUCCESS.getCode().equals(bossResult.getRespCode()))) {
//				resp2chan = tranResult.getRespMsg();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("接收上游通道异步通知接口[ERROR] chanCode=[" + chanCode + "],platOrderId=[" + platOrderId + "] e=:" + e.getMessage());
//			resp2chan = "failure";
//		}
//		logger.info("接收上游通道异步通知接口-END");
//		return resp2chan;
//	}
//
//
//
//    @RequestMapping(value = "/gateway/renotify")
//    @ResponseBody
//    public String renotify(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
//
//            String platOrderId = request.getParameter("orderId");
//            String suffix = request.getParameter("suffix");
//
//            String message = "";
//            String resultStr = "";
//            try {
//                //查询平台订单信息
//                MchtGatewayOrder order = new MchtGatewayOrder();
//                MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
//                mchtGatewayOrder.setPlatOrderId(platOrderId);
//
//                mchtGatewayOrder.setSuffix(suffix);
//                List<MchtGatewayOrder> mchtGatewayOrderList = mchtGwOrderService.list(mchtGatewayOrder);
//                if (mchtGatewayOrderList != null && mchtGatewayOrderList.size() > 0) {
//                    order =  mchtGatewayOrderList.get(0);
//                }
//
//                String notifyUrl = order.getNotifyUrl();
//                //POST发送通知数据
//                if (StringUtils.isNotBlank(notifyUrl)) {
//                    TradeNotify tradeNotify = buildTradeNotify(order);
//                    String requestUrl = tradeNotify.getUrl();
//                    String requestMsg = JSON.toJSONString(tradeNotify.getResponse());
//                    logger.info("[start] 异步通知商户开始，请求地址：{} 请求内容：{}", requestUrl, requestMsg);
//                    String result = PostUtil.postMsg(requestUrl, requestMsg);
//                    logger.info("[end] 异步通知商户结束，请求地址：{} 请求内容：{} 商户响应：{}", requestUrl, requestMsg, response);
//
//                    if ("SUCCESS".equalsIgnoreCase(result)) {
//                        order.setSupplyStatus("0");
//                        message = "补发成功";
//                        resultStr = "SUCCESS";
//                    } else {
//                        order.setSupplyStatus("1");
//                        message = "已补发，商户响应：" + result;
//                        resultStr = "result";
//                    }
//                } else {
//                    order.setSupplyStatus("1");
//                    message = "补发失败，此订单通知地址为空";
//                    logger.info(message + " 订单号：" + order.getPlatOrderId());
//                }
//                order.setSuffix(suffix);
//                mchtGwOrderService.saveByKey(order);
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.error("补发失败，" + e.getMessage());
//                message = "补发失败，" + e.getMessage();
//            } finally {
//                logger.info(message);
//            }
//            return resultStr;
//    }
//
//    private TradeNotify buildTradeNotify(MchtGatewayOrder order) throws Exception {
//        TradeNotify tradeNotify = new TradeNotify();
//        tradeNotify.setUrl(order.getNotifyUrl());
//
//        TradeNotifyResponse tradeNotifyResponse = new TradeNotifyResponse();
//        TradeNotifyResponse.TradeNotifyResponseHead head = new TradeNotifyResponse.TradeNotifyResponseHead();
//        head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//        head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//
//        TradeNotifyResponse.TradeNotifyBody body = new TradeNotifyResponse.TradeNotifyBody();
//        body.setMchtId(order.getMchtId());
//        body.setOrderId(order.getMchtOrderId());
//        body.setStatus(PayStatusEnum.PAY_SUCCESS.getCode().equals(order.getStatus()) ? "SUCCESS" : "PROCESSING");
//        body.setTradeId(order.getId());
//        body.setBankCardNo(order.getBankCardNo());
//        body.setAmount(order.getAmount() + "");
//        TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(body);
//        String mchtKey = merchantService.queryByKey(order.getMchtId()).getMchtKey();
//        String sign = SignUtil.md5Sign(new HashMap<>(treeMap), mchtKey);
//
//        tradeNotifyResponse.setSign(sign);
//        tradeNotifyResponse.setBody(body);
//        tradeNotify.setResponse(tradeNotifyResponse);
//        return tradeNotify;
//    }

}
