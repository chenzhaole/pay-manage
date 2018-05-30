//package com.sys.gateway.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.TypeReference;
//import com.sys.boss.api.entry.CommonResponse;
//import com.sys.boss.api.entry.CommonResult;
//import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
//import com.sys.boss.api.entry.trade.request.TradeReqHead;
//import com.sys.boss.api.entry.trade.request.authcardelement.AuthCardElementRequestBody;
//import com.sys.boss.api.entry.trade.request.authcardelement.TradeAuthCardElementRequest;
//import com.sys.boss.api.entry.trade.request.quickpay.QuickPrePayRequestBody;
//import com.sys.boss.api.entry.trade.request.quickpay.QuickUnbundRequestBody;
//import com.sys.boss.api.entry.trade.request.quickpay.QuickValidPayRequestBody;
//import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickPrePayRequest;
//import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickUnbundRequest;
//import com.sys.boss.api.entry.trade.request.quickpay.TradeQuickValidPayRequest;
//import com.sys.boss.api.entry.trade.request.wappay.TradeWapRequest;
//import com.sys.boss.api.entry.trade.request.wappay.WapRequestBody;
//import com.sys.boss.api.entry.trade.response.TradeBaseResponse;
//import com.sys.boss.api.entry.trade.response.authcardelement.AuthCardeLElementCreateResponse;
//import com.sys.boss.api.entry.trade.response.quickpay.QuickPrePayOrderCreateResponse;
//import com.sys.boss.api.entry.trade.response.quickpay.QuickUnbundOrderCreateResponse;
//import com.sys.boss.api.entry.trade.response.quickpay.QuickValidPayOrderCreateResponse;
//import com.sys.boss.api.entry.trade.response.selectpay.SelectOrderCreateResponse;
//import com.sys.boss.api.entry.trade.response.wappay.WapOrderCreateResponse;
//import com.sys.boss.api.service.trade.TradeService;
//import com.sys.common.enums.ErrorCodeEnum;
//import com.sys.common.enums.PayStatusEnum;
//import com.sys.common.enums.PayTypeEnum;
//import com.sys.common.util.SignUtil;
//import com.sys.core.dao.dmo.ChanBankcardValid;
//import com.sys.core.dao.dmo.PlatBankcardValid;
//import com.sys.core.service.ChanBankcardValidService;
//import com.sys.core.service.PlatBankcardValidService;
//import com.sys.gateway.service.PayGwService;
//import com.sys.trans.api.entry.Result;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
///**
// *
// * @Description:网关支付业务处理实现类
// *
// * @author: ChenZL
// * @time: 2017年9月9日
// */
//@Service
//public class PayGwServiceImpl implements PayGwService {
//
//	protected final Logger logger = LoggerFactory.getLogger(PayGwServiceImpl.class);
//
////	@Autowired
////	public TradeService tradeService;
//
//	@Autowired
//	private PlatBankcardValidService platBankcardValidService;
//
//	@Autowired
//	private ChanBankcardValidService chanBankcardValidService;
//
//	/**wap支付校验参数**/
//	@Override
//	public CommonResponse checkWapParam(String paramStr) {
//		CommonResponse checkResp = new CommonResponse();
//		try {
//			if(paramStr.endsWith("=")){
//				paramStr = paramStr.substring(0,paramStr.length()-1);
//			}
//			//解析请求参数
//			TradeWapRequest tradeRequest = JSON.parseObject(paramStr, TradeWapRequest.class);
//			if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
//				logger.error("[head],[body],[sign]请求参数值不能为空，即TradeWapRequest=："+ JSONObject.toJSONString(tradeRequest));
//				return checkResp;
//			}
//
//			TradeReqHead head = tradeRequest.getHead();
//			if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
//				logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+ JSONObject.toJSONString(head));
//				return checkResp;
//			}
//
//			WapRequestBody body = tradeRequest.getBody();
//			if (StringUtils.isBlank(body.getOrderId())
//					|| StringUtils.isBlank(body.getAmount())
//					|| StringUtils.isBlank(body.getGoods())
//					|| StringUtils.isBlank(body.getNotifyUrl())
//					|| StringUtils.isBlank(body.getOrderTime())) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空");
//				logger.error("[orderId],[orderTime],[amount],[goods],[notifyUrl]请求参数值不能为空，即WapRequestBody=："+ JSONObject.toJSONString(body));
//				return checkResp;
//			}
//
//			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//			checkResp.setData(tradeRequest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("wap支付校验参数异常："+e.getMessage());
//			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
//		}
//		return checkResp;
//	}
//
//	/**快捷预消费接口校验参数**/
//	@Override
//	public CommonResponse checkQuickPrePayParam(String paramStr) {
//		CommonResponse checkResp = new CommonResponse();
//		try {
//			if(paramStr.endsWith("=")){
//				paramStr = paramStr.substring(0,paramStr.length()-1);
//			}
//			//解析请求参数
//			TradeQuickPrePayRequest tradeRequest = JSON.parseObject(paramStr, TradeQuickPrePayRequest.class);
//			if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
//				logger.error("[head],[body],[sign]请求参数值不能为空，即TradeQuickPrePayRequest=："+JSONObject.toJSONString(tradeRequest));
//				return checkResp;
//			}
//
//			TradeReqHead head = tradeRequest.getHead();
//			if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
//				logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+JSONObject.toJSONString(head));
//				return checkResp;
//			}
//
//			QuickPrePayRequestBody body = tradeRequest.getBody();
//			if (StringUtils.isBlank(body.getOrderId())
//					|| StringUtils.isBlank(body.getOrderTime())
//					|| StringUtils.isBlank(body.getAmount())
//					|| StringUtils.isBlank(body.getGoods())
//					|| StringUtils.isBlank(body.getNotifyUrl())
//					|| StringUtils.isBlank(body.getBankCardNo())
//					|| StringUtils.isBlank(body.getAccountName())
//					|| StringUtils.isBlank(body.getCardType())
//					|| StringUtils.isBlank(body.getCertificateNo())
//					|| StringUtils.isBlank(body.getMobilePhone())
//					|| StringUtils.isBlank(body.getIp())){
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("快捷支付预消费接口缺少必传参数");
//				logger.error("快捷支付预消费接口缺少必传参数，即QuickPrePayRequestBody=："+JSONObject.toJSONString(body));
//				return checkResp;
//			}
//
//			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//			checkResp.setData(tradeRequest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("快捷支付预消费接口校验参数异常："+e.getMessage());
//			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
//		}
//		return checkResp;
//	}
//
//	/**快捷验证接口校验参数*/
//	@Override
//	public CommonResponse checkQuickValidPayParam(String paramStr) {
//		CommonResponse checkResp = new CommonResponse();
//		try {
//			if(paramStr.endsWith("=")){
//				paramStr = paramStr.substring(0,paramStr.length()-1);
//			}
//			//解析请求参数
//			TradeQuickValidPayRequest tradeRequest = JSON.parseObject(paramStr, TradeQuickValidPayRequest.class);
//			if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
//				logger.error("[head],[body],[sign]请求参数值不能为空，即TradeQuickValidPayRequest=："+JSONObject.toJSONString(tradeRequest));
//				return checkResp;
//			}
//
//			TradeReqHead head = tradeRequest.getHead();
//			if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
//				logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+JSONObject.toJSONString(head));
//				return checkResp;
//			}
//
//			QuickValidPayRequestBody body = tradeRequest.getBody();
//			if (StringUtils.isBlank(body.getTradeId())
//					|| StringUtils.isBlank(body.getOrderTime())
//					|| StringUtils.isBlank(body.getSmscode())
//					|| StringUtils.isBlank(body.getIp())){
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("快捷支付验证支付接口缺少必传参数");
//				logger.error("快捷支付验证支付接口缺少必传参数，即QuickValidPayRequestBody=："+JSONObject.toJSONString(body));
//				return checkResp;
//			}
//
//			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//			checkResp.setData(tradeRequest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("校验参数异常："+e.getMessage());
//			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
//		}
//		return checkResp;
//	}
//
//	/**快捷解绑接口校验参数*/
//	@Override
//	public CommonResponse checkQuickUnbundParam(String paramStr) {
//		CommonResponse checkResp = new CommonResponse();
//		try {
//			if(paramStr.endsWith("=")){
//				paramStr = paramStr.substring(0,paramStr.length()-1);
//			}
//			//解析请求参数
//			TradeQuickUnbundRequest tradeRequest = JSON.parseObject(paramStr, TradeQuickUnbundRequest.class);
//			if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
//				logger.error("[head],[body],[sign]请求参数值不能为空，即TradeQuickUnbundRequest=："+JSONObject.toJSONString(tradeRequest));
//				return checkResp;
//			}
//
//			TradeReqHead head = tradeRequest.getHead();
//			if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
//				logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+JSONObject.toJSONString(head));
//				return checkResp;
//			}
//
//			QuickUnbundRequestBody body = tradeRequest.getBody();
//			if (StringUtils.isBlank(body.getCertificateNo())
//					|| StringUtils.isBlank(body.getMobilePhone())
//					|| StringUtils.isBlank(body.getBankCardNo())
//					|| StringUtils.isBlank(body.getAccountName())){
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("快捷支付解绑接口缺少必传参数");
//				logger.error("快捷支付解绑接口缺少必传参数，即QuickUnbundRequestBody=："+JSONObject.toJSONString(body));
//				return checkResp;
//			}
//
//			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//			checkResp.setData(tradeRequest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("校验参数异常："+e.getMessage());
//			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
//		}
//		return checkResp;
//	}
//	/**扫码接口校验参数*/
//	@Override
//	public CommonResponse checkScanParam(String paramStr) {
//		return null;
//	}
//
//	/**公众号支付接口校验参数*/
//	@Override
//	public CommonResponse checkPubParam(String paramStr) {
//		return null;
//	}
//	/**查单接口校验参数*/
//	@Override
//	public CommonResponse checkSelectParam(String paramStr) {
//		return null;
//	}
//	/**实名认证接口校验参数*/
//	@Override
//	public CommonResponse checkAuthCardParam(String paramStr) {
//		CommonResponse checkResp = new CommonResponse();
//		try {
//			if(paramStr.endsWith("=")){
//				paramStr = paramStr.substring(0,paramStr.length()-1);
//			}
//			//解析请求参数
//			TradeAuthCardElementRequest tradeRequest = JSON.parseObject(paramStr, TradeAuthCardElementRequest.class);
//			if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
//				logger.error("[head],[body],[sign]请求参数值不能为空，即TradeAuthCardElementRequest=："+JSONObject.toJSONString(tradeRequest));
//				return checkResp;
//			}
//
//			TradeReqHead head = tradeRequest.getHead();
//			if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
//				logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+JSONObject.toJSONString(head));
//				return checkResp;
//			}
//
//			AuthCardElementRequestBody body = tradeRequest.getBody();
//			if (StringUtils.isBlank(body.getOrderId())
//					|| StringUtils.isBlank(body.getIdType())
//					|| StringUtils.isBlank(body.getIdCard())
//					|| StringUtils.isBlank(body.getUserName())
//					|| StringUtils.isBlank(body.getBankCard())
//					|| StringUtils.isBlank(body.getMobile())
//					|| StringUtils.isBlank(body.getAuthCode())
//					|| StringUtils.isBlank(body.getAuthMethod())){
//				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
//				checkResp.setRespCode("实名认证接口缺少必传参数");
//				logger.error("实名认证接口缺少必传参数，即AuthCardElementRequestBody=："+JSONObject.toJSONString(body));
//				return checkResp;
//			}
//
//			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//			checkResp.setData(tradeRequest);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("校验参数异常："+e.getMessage());
//			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
//			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
//		}
//		return checkResp;
//	}
//
//
//
//	/**wap支付接口*/
//	@Override
//	public WapOrderCreateResponse wapPay(TradeBaseRequest tradeRequest, String ip) {
//
//		WapOrderCreateResponse.WapOrderCreateResponseHead head = new WapOrderCreateResponse.WapOrderCreateResponseHead();
//		WapOrderCreateResponse.WapOrderCreateResponseBody body = new WapOrderCreateResponse.WapOrderCreateResponseBody();
//		String sign  ="";
//		try {
//			logger.info("调用boss-trade创建wap订单，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
//			CommonResult commonResult = (CommonResult) tradeService.process(tradeRequest, ip);
//			logger.info("调用boss-trade创建wap订单，返回值commonResult：" + JSON.toJSONString(commonResult));
//			if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
//				Result tranResult = (Result) commonResult.getData();
//				head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//				head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//				body.setMchtId(tranResult.getMchtId());
//				body.setOrderId(tranResult.getMchtOrderNo());//商户订单号
//				body.setPayUrl(tranResult.getPayInfo());
//				body.setTradeId(tranResult.getOrderNo());//平台订单号
//				// 签名
//				Map<String, String> params =  JSONObject.parseObject(
//						JSON.toJSONString(body), new TypeReference<Map<String, String>>(){});
//				sign = SignUtil.md5Sign(params, tranResult.getMchtKey());
//			}else{
//				String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
//				String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
//				head.setRespCode(respCode);
//				head.setRespMsg(respMsg);
//			}
//		} catch (Exception e) {
//			head.setRespCode(ErrorCodeEnum.E8001.getCode());
//			head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
//			e.printStackTrace();
//			logger.error("创建wap支付订单异常 e=" + e.getMessage());
//		}
//
//		return new WapOrderCreateResponse(head, body, sign);
//	}
//
//	/**实名认证**/
//	@Override
//	public TradeBaseResponse authCardElement(TradeBaseRequest tradeRequest,
//			String ip) {
//		AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseHead head = new AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseHead();
//		AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseBody body = new AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseBody();
//		String sign  ="";
//		try {
//			//实名认证前，先查下数据库是否已经存在，验证通过的，即根据四要素
//			AuthCardElementRequestBody authCardElementRequestBody = ((TradeAuthCardElementRequest) tradeRequest).getBody();
//			PlatBankcardValid platBankcardValid = this.checkAuthCardElement(authCardElementRequestBody);
//			if(null != platBankcardValid){
//				//已经认证过
//				logger.info("调用boss-trade创建authCardElement实名认证订单前，从数据看查出改四要素已经认证过，即参数值tradeRequest："+JSON.toJSONString(tradeRequest)+", 数据库查出的数据为PlatBankcardValid："+JSONObject.toJSONString(platBankcardValid));
//				head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//				head.setRespMsg(ErrorCodeEnum.E1014.getDesc());
//			}else {
//				logger.info("调用boss-trade创建authCardElement实名认证订单，参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
//				CommonResult commonResult = (CommonResult) tradeService.process(tradeRequest, ip);
//				logger.info("调用boss-trade创建authCardElement实名认证订单，返回值commonResult：" + JSON.toJSONString(commonResult));
//				if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
//					Result tranResult = (Result)commonResult.getData();
//					head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//					head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//					body.setMchtId(tranResult.getMchtId());
//					body.setOrderId(tranResult.getMchtOrderNo());//商户订单号
//					body.setPlatId(tranResult.getOrderNo());//平台订单号
//					body.setSeq(UUID.randomUUID().toString().replaceAll("-", ""));
//					// 签名
//					Map<String, String> params = JSONObject.parseObject(
//							JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
//							});
//					sign = SignUtil.md5Sign(params, tranResult.getMchtKey());
//				}else{
//					String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
//					String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
//					head.setRespCode(respCode);
//					head.setRespMsg(respMsg);
//				}
//			}
//		} catch (Exception e) {
//			head.setRespCode(ErrorCodeEnum.E8001.getCode());
//			head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
//			e.printStackTrace();
//			logger.error("创建authCardElement实名认证订单异常 e=" + e.getMessage());
//		}
//		return new AuthCardeLElementCreateResponse(head, body, sign);
//	}
//	/**查找四要素是否已经验证过*/
//	private PlatBankcardValid checkAuthCardElement(AuthCardElementRequestBody authCardElementRequestBody) {
//		String idCard = authCardElementRequestBody.getIdCard();//证件号码
//		String userName = authCardElementRequestBody.getUserName();//	姓名
//		String bankCard = authCardElementRequestBody.getBankCard();//	银行卡号
//		String mobile = authCardElementRequestBody.getMobile();//手机号
//		PlatBankcardValid platBankcardValid = new PlatBankcardValid();
//		platBankcardValid.setPayType(PayTypeEnum.QUICK_REAL_AUTH.getCode());
//		platBankcardValid.setCertId(idCard);
//		platBankcardValid.setCardName(userName);
//		platBankcardValid.setCardNo(bankCard);
//		platBankcardValid.setMobile(mobile);
//		//鉴权状态 0:初始创建,-1:未知失败,2:支付成功,3:处理中，6:鉴权交易成功，鉴权结果失败,4006:鉴权交易失败，7:组合支付--鉴权成功待支付
//		platBankcardValid.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
//		List<PlatBankcardValid> bankcardValids = platBankcardValidService.list(platBankcardValid);
//		if(null != bankcardValids && bankcardValids.size() > 0){
//			return bankcardValids.get(0);
//		}
//		return null;
//	}
//
//	/**公众号支付**/
//	@Override
//	public TradeBaseResponse pubPay(TradeBaseRequest tradeRequest, String ip) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	/**查询接口**/
//	@Override
//	public TradeBaseResponse queryOrder(TradeBaseRequest tradeRequest, String ip) {
//		SelectOrderCreateResponse.SelectOrderCreateResponseHead head = new SelectOrderCreateResponse.SelectOrderCreateResponseHead();
//		SelectOrderCreateResponse.SelectOrderCreateResponseBody body = new SelectOrderCreateResponse.SelectOrderCreateResponseBody();
//		String sign = "";
//		try {
//			logger.info("调用boss-trade创建queryOrder订单，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
//			CommonResult commonResult = (CommonResult) tradeService.process(tradeRequest, ip);
//			logger.info("调用boss-trade创建queryOrder订单，返回值commonResult：" + JSON.toJSONString(commonResult));
//			if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
//				Result tranResult = (Result) commonResult.getData();
//				head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//				head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//				body.setMchtId(tranResult.getMchtId());
//				body.setOrderId(tranResult.getMchtOrderNo());//商户订单号
//				body.setTradeId(tranResult.getOrderNo());//平台订单号
//				body.setStatus(tranResult.getStatus());
//				body.setAmount(tranResult.getOrderAmount());
//				body.setChargeTime(tranResult.getPayTime());
//				body.setBiz(tranResult.getPaymentType());
//				body.setSeq(UUID.randomUUID().toString().replaceAll("-", ""));
//				// 签名
//				Map<String, String> params =  JSONObject.parseObject(
//						JSON.toJSONString(body), new TypeReference<Map<String, String>>(){});
//				sign = SignUtil.md5Sign(params, tranResult.getMchtKey());
//			}else{
//				String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
//				String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
//				head.setRespCode(respCode);
//				head.setRespMsg(respMsg);
//			}
//		} catch (Exception e) {
//			head.setRespCode(ErrorCodeEnum.E8001.getCode());
//			head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
//			e.printStackTrace();
//			logger.error("创建QuickPrePay支付订单异常 e=" + e.getMessage());
//		}
//		return new SelectOrderCreateResponse(head,body,sign);
//	}
//
//	/**快捷预消费**/
//	@Override
//	public TradeBaseResponse quickPrePay(TradeQuickPrePayRequest tradeRequest,
//			String ip) {
//		QuickPrePayOrderCreateResponse.QuickPrePayOrderCreateResponseHead head = new QuickPrePayOrderCreateResponse.QuickPrePayOrderCreateResponseHead();
//		QuickPrePayOrderCreateResponse.QuickPrePayOrderCreateResponseBody body = new QuickPrePayOrderCreateResponse.QuickPrePayOrderCreateResponseBody();
//		String sign = "";
//		try {
//			logger.info("调用boss-trade创建QuickPrePay订单，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
//			//快捷支付预消费接口有两种情况：1. 新用户：绑卡接口，绑卡验证接口 2.老用户： 支付接口，支付验证接口，
//			//1.判断新老用户
//			PlatBankcardValid platBankcardValid = this.selectNewOrOldUserInfo(tradeRequest);
//			if(null != platBankcardValid){//  1 是老用户
//				tradeRequest.setNewOrOldUser("1");//老用户--直接掉支付验证接口
//				//去chan_bankcard_valid (通道绑卡鉴权流水表) 查出 plat_user_id  掉上游接口使用
////				ChanBankcardValid chanBankcardValid = this.selectChanBankcardValidInfo(tradeRequest);
////				String userId = "";
////				if(null != chanBankcardValid){
////					userId = chanBankcardValid.getPlatUserId();
////					tradeRequest.getBody().setUserId(userId);//老用户设置值
////				}else{
////					logger.error("系统异常，在平台绑卡鉴权流水表能查到数据，但是在通道绑卡鉴权流水表查不到数据，请联系技术排查");
////					return null;
////				}
//			}else{//  0是新用户
//				tradeRequest.setNewOrOldUser("0");//新用户--直接掉绑卡验证接口
//			}
//			CommonResult commonResult = (CommonResult) tradeService.process(tradeRequest, ip);
//			logger.info("调用boss-trade创建QuickPrePay订单，返回值commonResult：" + JSON.toJSONString(commonResult));
//			if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
//
//				Result tranResult = (Result)commonResult.getData();
//				head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//				head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//				body.setMchtId(tranResult.getMchtId());
//				body.setOrderId(tranResult.getMchtOrderNo());//商户订单号
//				body.setTradeId(tranResult.getOrderNo());//平台订单号
//				body.setSeq(UUID.randomUUID().toString().replaceAll("-", ""));
//				// 签名
//				String mchtKey  = tranResult.getMchtKey();
//				Map<String, String> params =  JSONObject.parseObject(
//						JSON.toJSONString(body), new TypeReference<Map<String, String>>(){});
//				sign = SignUtil.md5Sign(params, mchtKey);
//			}else{
//				String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
//				String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
//				head.setRespCode(respCode);
//				head.setRespMsg(respMsg);
//			}
//		} catch (Exception e) {
//			head.setRespCode(ErrorCodeEnum.E8001.getCode());
//			head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
//			e.printStackTrace();
//			logger.error("创建QuickPrePay支付订单异常 e=" + e.getMessage());
//		}
//		return new QuickPrePayOrderCreateResponse(head, body, sign);
//	}
//
//	/**快捷验证支付接口*/
//	@Override
//	public TradeBaseResponse quickValidPay(TradeQuickValidPayRequest tradeRequest,
//										   String ip) {
//		QuickValidPayOrderCreateResponse.QuickValidOrderCreateResponseHead head = new QuickValidPayOrderCreateResponse.QuickValidOrderCreateResponseHead();
//		QuickValidPayOrderCreateResponse.QuickValidOrderCreateResponseBody body = new QuickValidPayOrderCreateResponse.QuickValidOrderCreateResponseBody();
//		String sign = "";
//		try {
//			logger.info("调用boss-trade创建QuickValid订单，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
//			//快捷验证支付接口有两种情况：1. 新用户：提交绑卡验证码接口，支付接口 2.老用户： 提交支付验证码接口
//			//1.判断新老用户--根据plat_bankcard_valid (平台绑卡鉴权流水表) 中的平台鉴权流水号plat_order_id 过滤，如果有记录
//			//且状态是 status(鉴权状态0申请 1成功 2失败)为0， 则是新用户，其他情况都是老用户
////			PlatBankcardValid platBankcardValid = this.selectPlatBankcardValidInfo(tradeRequest);
////			if(null != platBankcardValid){//  1 是老用户
////				tradeRequest.setNewOrOldUser("1");//老用户--直接掉支付验证接口
////			}else{//  0是新用户
////				tradeRequest.setNewOrOldUser("0");//新用户--直接掉绑卡验证接口
////			}
//			//短信内容 sms_text 和  验证码 identify_code 不为空，说明是我司发短信
////			String smsText = platBankcardValid.getSmsText();
////			String identifyCode = platBankcardValid.getIdentifyCode();
//			//两个功能
//			//1.校验验证码是否正确
//			//2. 一个标识 如果正确 标识为 我司发短信还是上游发短信 ， 0 是我司发短信  1 是上游发短信
////			if(StringUtils.isNotBlank(smsText) && StringUtils.isNotBlank(identifyCode)){
////				//商户提交的验证码
////				String mchtSmsCode = tradeRequest.getBody().getSmscode();
////				if(!mchtSmsCode.equals(identifyCode)){
////					//验证码不一致，直接返回验证码有误请重新提交，即在有效时间和有效次数内才允许提交
////					//从缓存读取有效次数
////					String memoryNumStr = "";//从缓存读取有效次数
////					if(StringUtils.isNotBlank(memoryNumStr)){
////						int memoryNum = Integer.valueOf(memoryNumStr);//缓存次数
////						if(0 != memoryNum){
////							//允许再次提交
////							head.setRespCode(ErrorCodeEnum.E5104.getCode());
////							head.setRespMsg(ErrorCodeEnum.E5104.getDesc());
////							return new QuickValidPayOrderCreateResponse(head, body, sign);
////						}
////					}
////				}
////				//0 是我司发短信
////				tradeRequest.setSmsSend("0");
////			}else{
////				//1 是上游发短信
////				tradeRequest.setSmsSend("1");
////			}
//
//			CommonResult commonResult = (CommonResult) tradeService.process(tradeRequest, ip);
//			logger.info("调用boss-trade创建QuickValid订单，返回值commonResult：" + JSON.toJSONString(commonResult));
//			if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
//				//在这里如果是新用户，并且我司发短信的，需要再次调用支付借口
////				if("0".equals(tradeRequest.getNewOrOldUser()) && "0".equals(tradeRequest.getSmsSend())){
////					//newOrOldUser：0是新用户  smsSend：0 是我司发短信
////					tradeRequest.setSmsSend("");//将其置为空，在tradeService中使用
////					commonResult = (CommonResult) tradeService.process(tradeRequest, ip);
////					logger.info("调用boss-trade创建QuickValid订单，返回值commonResult：" + JSON.toJSONString(commonResult));
////				}
//				Result tranResult = (Result)commonResult.getData();
//				head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//				head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//				body.setMchtId(tranResult.getMchtId());
//				body.setOrderId(tranResult.getMchtOrderNo());//商户订单号
//				body.setTradeId(tranResult.getOrderNo());//平台订单号
//				body.setSeq(UUID.randomUUID().toString().replaceAll("-", ""));
//				// 签名
//				Map<String, String> params =  JSONObject.parseObject(
//						JSON.toJSONString(body), new TypeReference<Map<String, String>>(){});
//				sign = SignUtil.md5Sign(params, tranResult.getMchtKey());
//			}else{
//				String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
//				String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
//				head.setRespCode(respCode);
//				head.setRespMsg(respMsg);
//			}
//		} catch (Exception e) {
//			head.setRespCode(ErrorCodeEnum.E8001.getCode());
//			head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
//			e.printStackTrace();
//			logger.error("创建QuickValid支付订单异常 e=" + e.getMessage());
//		}
//		return new QuickValidPayOrderCreateResponse(head, body, sign);
//	}
//	/**商户提交验证码时，判断新老用户*/
//	private PlatBankcardValid selectPlatBankcardValidInfo(TradeQuickValidPayRequest tradeRequest) {
//		//1.判断新老用户--根据plat_bankcard_valid (平台绑卡鉴权流水表) 中的平台鉴权流水号plat_order_id 过滤，如果有记录
//		//且状态是 status(鉴权状态0申请 1成功 2失败)为0， 则是新用户，其他情况都是老用户
//		//根据商户号，平台绑卡鉴权流水号，状态(0)
//		String mchtId = tradeRequest.getHead().getMchtId();//商户号
//		String platOrderId  = tradeRequest.getBody().getTradeId();//平台鉴权流水号
//		PlatBankcardValid platBankcardValid = new PlatBankcardValid();
//		platBankcardValid.setMchtCode(mchtId);
//		platBankcardValid.setPlatOrderId(platOrderId);
//		//鉴权状态 0:初始创建,-1:未知失败,2:支付成功,3:处理中，6:鉴权交易成功，鉴权结果失败,4006:鉴权交易失败，7:组合支付--鉴权成功待支付
//		platBankcardValid.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
//		List<PlatBankcardValid> listPlatBankcardValid = platBankcardValidService.list(platBankcardValid);
//		if(null != listPlatBankcardValid){
//			return listPlatBankcardValid.get(0);
//		}
//		return null;
//	}
//
//	/**去平台绑卡鉴权流水表查找是否是新老用户*/
//	private PlatBankcardValid selectNewOrOldUserInfo(TradeQuickPrePayRequest tradeRequest) {
//		//目前只按照四要素去查，如果查出多个成功的记录只取第一个
//		//四要素
//		String certId = tradeRequest.getBody().getCertificateNo();//身份证
//		String mobile = tradeRequest.getBody().getMobilePhone();//手机号
//		String cardNo = tradeRequest.getBody().getBankCardNo();//银行卡号
//		String cardName = tradeRequest.getBody().getAccountName();//开户名
//
//		PlatBankcardValid platBankcardValid = new PlatBankcardValid();
//		platBankcardValid.setCertId(certId);
//		platBankcardValid.setMobile(mobile);
//		platBankcardValid.setCardNo(cardNo);
//		platBankcardValid.setCardName(cardName);
//		//鉴权状态 0:初始创建,-1:未知失败,2:支付成功,3:处理中，6:鉴权交易成功，鉴权结果失败,4006:鉴权交易失败，7:组合支付--鉴权成功待支付
//		platBankcardValid.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
//		//必须是快捷支付
//		platBankcardValid.setPayType(PayTypeEnum.QUICK_COMB_DK.getCode());
//		List<PlatBankcardValid> listPlatBankcardValid = platBankcardValidService.list(platBankcardValid);
//		//这里目前的处理逻辑是 只根据四要素来判断是否是老用户，如果已经存在就判断为是
//		if(null != listPlatBankcardValid && listPlatBankcardValid.size()>0){
//			//TODO 默认只取出一个，后期会改造
//			return listPlatBankcardValid.get(0);
//		}else{
//			return null;
//		}
//	}
//
//
//	/**去通道绑卡鉴权流水表查找绑卡信息*/
//	private ChanBankcardValid selectChanBankcardValidInfo(TradeQuickPrePayRequest tradeRequest) {
//		//目前只按照四要素去查，如果查出多个成功的记录只取第一个
//		//四要素 和商户code
//		String certId = tradeRequest.getBody().getCertificateNo();//身份证
//		String mobile = tradeRequest.getBody().getMobilePhone();//手机号
//		String cardNo = tradeRequest.getBody().getBankCardNo();//银行卡号
//		String cardName = tradeRequest.getBody().getAccountName();//开户名
//
//		ChanBankcardValid chanBankcardValid = new ChanBankcardValid();
//		chanBankcardValid.setCertId(certId);
//		chanBankcardValid.setMobile(mobile);
//		chanBankcardValid.setCardNo(cardNo);
//		chanBankcardValid.setCardName(cardName);
//		//鉴权状态 0:初始创建,-1:未知失败,2:支付成功,3:处理中，6:鉴权交易成功，鉴权结果失败,4006:鉴权交易失败，7:组合支付--鉴权成功待支付
//		chanBankcardValid.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
//		List<ChanBankcardValid> listChanBankcardValid = chanBankcardValidService.list(chanBankcardValid);
//		if(null != listChanBankcardValid && listChanBankcardValid.size()>0){
//			//返回通道绑卡鉴权信息
//			return listChanBankcardValid.get(0);
//		}else{
//			return null;
//		}
//	}
//
//	/**快捷解绑接口**/
//	@Override
//	public TradeBaseResponse quickUnbundPay(TradeBaseRequest tradeRequest,
//			String ip) {
//		QuickUnbundOrderCreateResponse.QuickUnbundOrderCreateResponseHead head = new QuickUnbundOrderCreateResponse.QuickUnbundOrderCreateResponseHead();
//		QuickUnbundOrderCreateResponse.QuickUnbundOrderCreateResponseBody body = new QuickUnbundOrderCreateResponse.QuickUnbundOrderCreateResponseBody();
//		String sign = "";
//		try {
//			logger.info("调用boss-trade创建QuickUnbund订单，参数值tradeRequest："+JSON.toJSONString(tradeRequest));
//			CommonResult commonResult = (CommonResult) tradeService.process(tradeRequest, ip);
//			logger.info("调用boss-trade创建QuickUnbund订单，返回值commonResult：" + JSON.toJSONString(commonResult));
//			if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
//				Result tranResult = (Result) commonResult.getData();
//				head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
//				head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
//				body.setCertificateNo(tranResult.getCertificateNo());
//				body.setMobilePhone(tranResult.getMobilePhone());
//				body.setBankCardNo(tranResult.getBankCardNo());
//				body.setAccountName(tranResult.getAccountName());
//				body.setSeq(UUID.randomUUID().toString().replaceAll("-", ""));
//				// 签名
//				Map<String, String> params =  JSONObject.parseObject(
//						JSON.toJSONString(body), new TypeReference<Map<String, String>>(){});
//				sign = SignUtil.md5Sign(params, tranResult.getMchtKey());
//			}else{
//				String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
//				String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
//				head.setRespCode(respCode);
//				head.setRespMsg(respMsg);
//			}
//		} catch (Exception e) {
//			head.setRespCode(ErrorCodeEnum.E8001.getCode());
//			head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
//			e.printStackTrace();
//			logger.error("创建QuickUnbund支付订单异常 e=" + e.getMessage());
//		}
//		return new QuickUnbundOrderCreateResponse(head, body, sign);
//	}
//
//
//	/**扫码支付**/
//	@Override
//	public TradeBaseResponse scanPay(TradeBaseRequest tradeRequest, String ip) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
