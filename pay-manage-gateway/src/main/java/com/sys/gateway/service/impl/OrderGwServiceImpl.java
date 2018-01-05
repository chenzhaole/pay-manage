package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.selectpay.SelectPayRequestBody;
import com.sys.boss.api.entry.trade.request.selectpay.TradeSelectPayRequest;
import com.sys.boss.api.entry.trade.response.selectpay.SelectOrderCreateResponse;
import com.sys.boss.api.service.order.OrderService;
import com.sys.boss.api.service.trade.TradeCheckDataService;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.gateway.service.OrderGwService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @Description:网关Order业务处理实现类
 * 
 * @author: ChenZL
 * @time: 2017年7月30日
 */
@Service
public class OrderGwServiceImpl implements OrderGwService {
	
	protected final Logger logger = LoggerFactory.getLogger(OrderGwServiceImpl.class);

	@Autowired
	private OrderService orderService;

	@Autowired
	private TradeCheckDataService tradeCheckDataService;
	

	@Override
	public CommonResponse checkQueryOrderParam(String paramStr) {
		CommonResponse checkResp = new CommonResponse();
		try {
			TradeSelectPayRequest queryRequest = JSON.parseObject(paramStr, TradeSelectPayRequest.class);

			if (queryRequest.getHead() == null || queryRequest.getBody() == null || queryRequest.getSign() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
				logger.error("[head],[body],[sign]请求参数值不能为空，即TradeQueryRequest=："+ JSONObject.toJSONString(queryRequest));
				return checkResp;
			}

			TradeReqHead head = queryRequest.getHead();
			if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
				logger.error("[mchtId],[version],[biz]请求参数值不能为空，即即TradeQueryRequest.TradeReqHead=："+ JSONObject.toJSONString(head));
				return checkResp;
			}

			SelectPayRequestBody body = queryRequest.getBody();
			if((StringUtils.isBlank(body.getOrderId()) && StringUtils.isBlank(body.getTradeId())) || StringUtils.isBlank(body.getOrderTime())){
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[orderId],[tradeId],[orderTime]请求参数值不能为空");
				logger.error("[orderId],[tradeId],[orderTime]请求参数值不能为空，即TradeQueryRequest.TradeQueryBody=："+ JSONObject.toJSONString(body));
				return checkResp;
			}

			CommonResult checkMchtInfoResult = tradeCheckDataService.checkMchtInfo(head.getMchtId());
			if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkMchtInfoResult.getRespCode())) {
				checkResp.setRespCode(checkMchtInfoResult.getRespCode());
				checkResp.setRespMsg(checkMchtInfoResult.getRespMsg());
				logger.error("查询订单，检验商户信息 "+JSON.toJSONString(checkMchtInfoResult));
				return checkResp;
			}

			MchtInfo merchant = (MchtInfo) checkMchtInfoResult.getData();
			String sign = queryRequest.getSign();
			TreeMap<String,String> bodyMap = BeanUtils.bean2TreeMap(body);

			CommonResult checkSignResult = tradeCheckDataService.checkSign(bodyMap,sign,merchant);
			if(!ErrorCodeEnum.SUCCESS.getCode().equals(checkSignResult.getRespCode())){
				checkResp.setRespCode(ErrorCodeEnum.E1009.getCode());
				checkResp.setRespMsg(ErrorCodeEnum.E1009.getDesc());
				logger.error("查询订单，检验签名信息 "+JSON.toJSONString(checkSignResult));
				return checkResp;
			}

			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
			checkResp.setData(merchant.getMchtKey());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询订单校验参数异常："+e.getMessage());
			checkResp.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			checkResp.setRespMsg("订单校验参数失败");
		}
		return checkResp;
	}

	@Override
	public SelectOrderCreateResponse queryResult(TradeSelectPayRequest tradeQueryRequest) {
		SelectOrderCreateResponse response = new SelectOrderCreateResponse();
		SelectOrderCreateResponse.SelectOrderCreateResponseHead respHead = new SelectOrderCreateResponse.SelectOrderCreateResponseHead();
		SelectOrderCreateResponse.SelectOrderCreateResponseBody respBody = new SelectOrderCreateResponse.SelectOrderCreateResponseBody();
		respHead.setRespCode(ErrorCodeEnum.E8005.getCode());
		respHead.setRespMsg(ErrorCodeEnum.E8005.getDesc());
		try {
			SelectPayRequestBody reqBody = tradeQueryRequest.getBody();
			MchtGatewayOrder queryBO = new MchtGatewayOrder();
			//我司流水号
			String suffix = "";
			if(StringUtils.isNotBlank(reqBody.getTradeId())){
				queryBO.setPlatOrderId(reqBody.getTradeId());
				//平台订单号不为空，设置分表定位
				suffix = IdUtil.getPlatOrderIdSuffix(reqBody.getTradeId());
				queryBO.setSuffix(suffix);
			}else{
				if(StringUtils.isNotBlank(reqBody.getOrderId())){
					queryBO.setMchtOrderId(reqBody.getOrderId());
					//商户流水号结合订单时间分表定位
					suffix = reqBody.getOrderTime().substring(0,6);
					System.out.println("月表后缀："+suffix);
					queryBO.setSuffix(suffix);
				}
			}
			queryBO.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
			List<MchtGatewayOrder> queryList = orderService.list(queryBO);
			//结果集为空 且 平台订单号为空，分表定位设置上月
			if(Collections3.isEmpty(queryList) && StringUtils.isBlank(reqBody.getTradeId())){
                queryBO.setSuffix(DateUtils.formatDate(DateUtils.addMonths(new Date(),-1),"yyyyMM"));
                queryList.addAll(orderService.list(queryBO));
            }
			MchtGatewayOrder order = Collections3.isEmpty(queryList)?null:queryList.get(0);
			if(order != null){
				this.geneRetSelectBosyInfo(respBody,order);
				respHead.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
				respHead.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询订单异常："+e.getMessage());
			respHead.setRespCode(ErrorCodeEnum.E8001.getCode());
			respHead.setRespMsg(ErrorCodeEnum.E8001.getDesc());
		}
		response.setBody(respBody);
		response.setHead(respHead);
		return response;
	}
	/**查单返回给商户的的信息**/
	private void geneRetSelectBosyInfo(SelectOrderCreateResponse.SelectOrderCreateResponseBody respBody, MchtGatewayOrder order) {
		respBody.setMchtId(order.getMchtCode());
		respBody.setOrderId(order.getMchtOrderId());
		respBody.setTradeId(order.getPlatOrderId());
		String status = order.getStatus();
		status = PayStatusEnum.PAY_SUCCESS.getCode().equals(status)?"SUCCESS":(PayStatusEnum.PROCESSING.getCode().equals(status)?"PROCESSING":"FAILURE");
		respBody.setStatus(status);
		respBody.setAmount(order.getAmount()+"");
		respBody.setChargeTime(new SimpleDateFormat("yyyyMMddHHmmss").format(order.getUpdateTime()));
		respBody.setSeq(UUID.randomUUID().toString().replaceAll("-", ""));
		respBody.setBiz(order.getPayType());
	}
}
