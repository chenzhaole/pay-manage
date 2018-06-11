package com.sys.gateway.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.ownership.OwnershipRequestBody;
import com.sys.boss.api.entry.trade.request.ownership.TradeOwnershipRequest;
import com.sys.boss.api.entry.trade.response.ownership.OwnershipResponse;
import com.sys.boss.api.entry.trade.response.ownership.OwnershipResponse.OwnershipResponseBody;
import com.sys.boss.api.entry.trade.response.ownership.OwnershipResponse.OwnershipResponseHead;
import com.sys.boss.api.service.trade.handler.ITradeBankCardOwnershipHandler;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.SignUtil;
import com.sys.gateway.service.BankCardOwnershipGwService;

@Service
public class BankCardOwnershipGwServiceImpl implements BankCardOwnershipGwService {

	private Logger logger = LoggerFactory.getLogger(BankCardOwnershipGwServiceImpl.class);
	private final String BIZ_NAME = PayTypeEnum.QUICK_OWNERSHIP.getDesc()+"-";
	@Autowired
	ITradeBankCardOwnershipHandler tradeBankCardOwnershipHandler;

	@Override
	public OwnershipResponse queryBankCardOwnership(String paramStr) {

//		CommonResult tradeResult = new CommonResult();
		OwnershipResponse ownershipResponse = new OwnershipResponse();
		OwnershipResponseHead head = new OwnershipResponseHead();
		OwnershipResponseBody body = new OwnershipResponseBody();
		
		try {
			if(paramStr.endsWith("=")){
				paramStr = paramStr.substring(0,paramStr.length()-1);
			}
			//解析请求参数
			TradeOwnershipRequest ownershipRequest = JSON.parseObject(paramStr, TradeOwnershipRequest.class);
			
			CommonResult checkResp = check(ownershipRequest);
			if(!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				head.setRespCode(checkResp.getRespCode());
				head.setRespMsg(checkResp.getRespMsg());
				ownershipResponse.setHead(head);
				return ownershipResponse;
			}
			
			TradeOwnershipRequest tradeRequest = (TradeOwnershipRequest) checkResp.getData();
			CommonResult tradeResult = tradeBankCardOwnershipHandler.process(tradeRequest, "");
			logger.info(BIZ_NAME+"调用Trade返回tradeResult="+JSON.toJSONString(tradeResult));
			
			if(ErrorCodeEnum.SUCCESS.getCode().equals(tradeResult.getRespCode())){
				Object[] data = (Object[]) tradeResult.getData();
				Map<String,String> map = (Map<String, String>) data[0];
				head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
				head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
				body.setCardNo(map.get("cardNo"));
				body.setCardType(map.get("cardType"));
				body.setCardName(map.get("cardName"));
				body.setBankCode(map.get("bankCode"));
				body.setBankName(map.get("bankName"));
				body.setParam(map.get("param"));
				
				MchtInfo merchant = (MchtInfo) data[1];
				String sign = SignUtil.md5Sign(map, merchant.getMchtKey(),"");
				
			}else{
				head.setRespCode(tradeResult.getRespCode());
				head.setRespMsg(tradeResult.getRespMsg());
			}
			ownershipResponse.setHead(head);
			ownershipResponse.setBody(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ownershipResponse;
	}

	protected CommonResult check(TradeOwnershipRequest tradeRequest) {
		CommonResult checkResp = new CommonResult();
		try {

			logger.info(BIZ_NAME+"请求接收参数：" + JSON.toJSONString(tradeRequest));

			//判断biz参数值
			if (!PayTypeEnum.QUICK_OWNERSHIP.getCode().equals(tradeRequest.getHead().getBiz()) && !"55".equals(tradeRequest.getHead().getBiz())) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[biz]请求参数值错误");
				logger.info(BIZ_NAME+"[biz]请求参数值不是银行卡归属信息查询的支付类型CODE 入参biz=" + tradeRequest.getHead().getBiz() +" 正确的biz=" + PayTypeEnum.QUICK_OWNERSHIP.getCode());
				return checkResp;
			}
			
			if (tradeRequest.getHead() == null
					|| tradeRequest.getBody() == null
					|| tradeRequest.getSign() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
				logger.info(BIZ_NAME+"[head],[body],[sign]请求参数值不能为空，即TradeOwnershipRequest=：" + JSONObject.toJSONString(tradeRequest));
				return checkResp;
			}

			TradeReqHead head = tradeRequest.getHead();
			if (head.getMchtId() == null || head.getVersion() == null
					|| head.getBiz() == null) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
				logger.info(BIZ_NAME+"[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=：" + JSONObject.toJSONString(head));
				return checkResp;
			}

			OwnershipRequestBody body = tradeRequest.getBody();
			if (StringUtils.isBlank(body.getOrderId())
					|| StringUtils.isBlank(body.getCardNo())
					|| StringUtils.isBlank(body.getOrderTime())) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespCode("[orderId],[orderTime],[cardNo]请求参数值不能为空");
				logger.info(BIZ_NAME+"[orderId],[orderTime],[cardNo]请求参数值不能为空，即OwnershipRequestBody=：" + JSONObject.toJSONString(body));
				return checkResp;
			}

			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
			checkResp.setData(tradeRequest);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(BIZ_NAME+"系统异常：" + e.getMessage());
			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
		}
		return checkResp;
	}

//	private TradeOwnershipRequest buildFromRequest(HttpServletRequest request) {
//		TradeOwnershipRequest ownershipRequest = new TradeOwnershipRequest();
//		TradeReqHead head = new TradeReqHead();
//		head.setBiz(request.getParameter("biz"));
//		head.setMchtId(request.getParameter("mchtId"));
//		head.setVersion(request.getParameter("version"));
//
//		OwnershipRequestBody body = new OwnershipRequestBody();
//		body.setOrderId(request.getParameter("orderId"));
//		body.setCardNo(request.getParameter("cardNo"));
//		body.setOrderTime(request.getParameter("orderTime"));
//		body.setParam(request.getParameter("param"));
//
//		ownershipRequest.setBody(body);
//		ownershipRequest.setHead(head);
//		ownershipRequest.setSign(request.getParameter("sign"));
//		return ownershipRequest;
//	}

}
