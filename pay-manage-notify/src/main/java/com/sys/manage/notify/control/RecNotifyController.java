package com.sys.manage.notify.control;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.trade.TradeNotifyService;
import com.sys.boss.api.service.trade.TradeOrderService;
import com.sys.boss.api.service.trade.handler.ITradeRecNotifyHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.manage.notify.service.IChannelQuickPayService;
import com.sys.manage.notify.service.IChannelService;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 接收支付结果异步通知
 * 
 * 统一扫码接口专用
 * 1、微信浏览器H5 2、微信公众号 3、微信原生扫码 4、支付宝扫码
 * 
 * @author: ChenZL
 * @time: 2017年11月26日
 */
@Controller
public class RecNotifyController extends BaseController {

	protected final Logger logger = LoggerFactory.getLogger(RecNotifyController.class);

	@Autowired
	TradeOrderService tradeOrderService;

	@Autowired
	ITradeRecNotifyHandler tradeRecNotifyHandler;

	@Autowired
	TradeNotifyService tradeNotifyService;


	/**
	 * 接收上游通道的异步通知的订单结果data
	 * 调用Boss-trade查询Config对象
	 * 判断Config，将data分发至对应通道模块的NotifyHandle解析，返回Trade对象
	 *
	 * @param data
	 * @param chanCode
	 * @param platOrderId
	 * @return
	 * @throws TranException
	 */
	@RequestMapping("/reciveNotify/{chanCode}/{platOrderId}")
//	@RequestMapping("abc")
	@ResponseBody
	public String reciveNotify(@RequestBody String data, @PathVariable String chanCode, @PathVariable String platOrderId) throws TranException {
		logger.info("接收上游通道异步通知接口[start]，chanCode=["+chanCode+"] , platOrderId=["+platOrderId+"] , data=["+data+"]");
		Result tranResult = notFindChannelResult() ;
		CommonResult bossResult = new CommonResult();

		//接受到订单状态异步通知后，返回给上游通道的内容
		String resp2chan = "";
		try {
			//查询原始Trade信息by platOrderId
			Trade rediseTrade = (Trade) tradeOrderService.query(platOrderId);
			if(rediseTrade == null){
				logger.info("redis中查询原始Trade信息返回：null");
				return resp2chan;
			}
			logger.info("查询原始Trade信息返回："+JSON.toJSONString(rediseTrade));

			//获取所调用的通道模块
			Trade tdSign = new Trade();
			Config config = rediseTrade.getConfig();
			tdSign.setConfig(config);
			logger.info("选择通道service对象 chanCode=" + chanCode);
			tdSign.setData(data);//换成通道返回的数据，用各通道代码解析
			if(!PayTypeEnum.COMB_DK.getCode().equals(config.getPayType())){
				IChannelService service = getChannelService(tdSign);
				logger.info("选择通道service对象 service=" + service);
				if(service != null) {
					tranResult = service.reciveNotify(tdSign);
				}
			}else{
				IChannelQuickPayService quickPayService = getChannelQuickPayService(tdSign);
				if(quickPayService != null) {
					tranResult = quickPayService.notifyQuickCardPay(tdSign);
					logger.info("选择通道service对象 service=" + quickPayService);
				}
			}
			//如果解析通道数据不为空，则调用boss-trade更新平台数据和notify商户
			if(null != tranResult){
				Object[] dataObjs = new Object[]{rediseTrade.getData(), tranResult, chanCode, platOrderId};
				tdSign.setData(dataObjs);//解析后的Result + 商户原始报文MAP
				bossResult = (CommonResult) tradeNotifyService.process(tdSign);				logger.info("调用tradeNotifyService异步通知服务返回值："+JSON.toJSONString(bossResult));
			}
			//如果解析通道的数据成功，且，平台保存和通知商户成功，则返回给通道成功标识
			if(Result.RESP_CODE_SUCCESS.equals(tranResult.getStatus())
					&& ErrorCodeEnum.SUCCESS.getCode().equals(bossResult.getRespCode())){
				resp2chan = tranResult.getRespChanMsg();//返回给上游的信息
			}else{
				resp2chan = "error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("接收上游通道异步通知接口[ERROR] chanCode=["+chanCode+"],platOrderId=["+platOrderId+"] e=:"+e.getMessage());
			resp2chan = "failure";
		}
		logger.info("接收上游通道异步通知接口[END]，chanCode=["+chanCode+"] , platOrderId=["+platOrderId+"] respMsg=["+resp2chan+"]");
		return resp2chan;
	}

	/**
	 * 接受统一异步通知结果
	 * POST请求
	 */
	@RequestMapping("/recNotify/{chanCode}/{platOrderId}")
	@ResponseBody
	public String recNotify(@RequestBody String data, @PathVariable String chanCode, @PathVariable String platOrderId) throws TranException {
		logger.info("接收上游通道异步通知接口-START chanCode=" + chanCode + " platOrderId=" + platOrderId + " data="+ data + "");
		Result tranResult = notFindChannelResult();
		CommonResult bossResult = new CommonResult();

		String resp2chan = "FAILSE";
		try {
			Trade trade = (Trade) tradeOrderService.query(platOrderId);
			if (trade == null) {
				logger.info("查询原始Trade信息返回：null");
				return resp2chan;
			}
			logger.info("查询原始Trade信息返回：" + JSON.toJSONString(trade));

			Object mchtReqData = trade.getData();
			Config cf = new Config();
			cf.setChannelCode(chanCode);
			Trade td = new Trade();
			td.setConfig(cf);
			logger.info("选择通道service对象 chanCode=" + chanCode);
			IChannelService service = getChannelService(td);
			logger.info("选择通道service对象 service=" + service);
			if (service != null) {
				trade.setData(data);
				tranResult = service.reciveNotify(trade);
			} else {
				logger.info("接收上游通道异步通知接口[ERROR] 未找到 chanCode=[" + chanCode + "],platOrderId=[" + platOrderId + "] 业务处理service类");
			}

			if (Result.RESP_CODE_SUCCESS.equals(tranResult.getRespCode())) {
				Object[] dataObjs = {trade.getData(), mchtReqData};
				trade.setData(dataObjs);
				bossResult = (CommonResult) tradeRecNotifyHandler.process(trade);
				logger.info("调用tradeNotifyService异步通知服务返回值：" + JSON.toJSONString(bossResult));
			}

			if ((Result.RESP_CODE_SUCCESS.equals(tranResult.getRespCode()))
					&& (ErrorCodeEnum.SUCCESS.getCode().equals(bossResult.getRespCode()))) {
				resp2chan = tranResult.getRespMsg();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("接收上游通道异步通知接口[ERROR] chanCode=[" + chanCode + "],platOrderId=[" + platOrderId + "] e=:" + e.getMessage());
			resp2chan = "failure";
		}
		logger.info("接收上游通道异步通知接口-END");
		return resp2chan;
	}
	
	/**
	 * 接收返回的异步通知结果（固定接收地址）
	 * 不支持自定义回调地址的特殊通道
	 * 
	 */
	@RequestMapping(value = "/recNotify/ch2")
	@ResponseBody
	public String recNotify2(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> paramMap) throws TranException {
		logger.info(request.getParameter("attach"));
		logger.info("接收上游通道异步通知接口ch2-START paramMap="+JSON.toJSONString(paramMap));
		CommonResult bossResult = new CommonResult();
		String resp2chan = "FAILSE";
		try {
			Trade trade = new Trade();
			trade.setData(paramMap);
			//Trans-通道Module处理
			Result result = null;
			
			//查询原始Trade信息by platOrderId
			Trade rediseTrade = (Trade) tradeOrderService.query(result.getOrderNo());
			if(rediseTrade == null){
				logger.info("redis中查询原始Trade信息返回null");
				return resp2chan;
			}
			logger.info("查询原始Trade信息返回："+JSON.toJSONString(rediseTrade));
			
			if(Result.RESP_CODE_SUCCESS.equals(result.getRespCode())){
				Object[] objs = new Object[]{result,rediseTrade.getData()};
				rediseTrade.setData(objs);
				
				//Boss-业务Trade处理
				bossResult = (CommonResult) tradeRecNotifyHandler.process(rediseTrade);
				logger.info("调用tradeNotifyService异步通知服务返回值：" + JSON.toJSONString(bossResult));
				resp2chan =  "SUCCESS";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("接收返回的异步通知结果GET请求异常，e="+e.getMessage());
		}
		logger.info("接收上游通道异步通知接口ch2-END");
		return resp2chan;
	}
		
		
}
