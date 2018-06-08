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
import com.sys.trans.api.entry.Result;
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
	 * 处理商户请求,仅支持Post请求
	 */
	@RequestMapping(value="mchtCall", method = RequestMethod.POST)
	public String mchtCall(HttpServletRequest request, Model model) throws Exception {
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
			//设备类型由我们自己来通过程序判断，由于不同的设备类型对应页面不一样，且掉支付的方式也不一样，所以会根据设备类型来做判断
			//根据userAgent判断设备类型：pc、手机端、微信内(针对公众号支付)
			String userAgent = this.getUserAgentInfoByRequest(request, midoid);
			logger.info(BIZ+midoid+"根据请求头获取的user-agent为："+userAgent);
			deviceType = HttpUtil.getDeviceType(userAgent);
			logger.info(BIZ+midoid+"通过程序判断deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
			TradeCashierRequest requestInfo = null;
			//解析并校验商户请求参数
			result = gwCashierService.resolveAndcheckParam(request);
			logger.info(BIZ+midoid+"解析并校验商户请求参数后的结果为："+ JSONObject.toJSONString(result));
			if(ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
				//商户请求参数校验通过
				requestInfo = (TradeCashierRequest) result.getData();
				//获取请求ip，值必须真实，某些上游通道要求必须是真实ip
				String ip = requestInfo.getBody().getIp();
				if(StringUtils.isBlank(ip)){
					ip = IpUtil.getRemoteHost(request);
				    logger.info(BIZ+midoid+"通过程序获取的请求ip为："+ip);
				}
				logger.info(BIZ+midoid+"最终获取的请求ip为："+ip+"，最终确定的deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
				//调用handler处理业务
				logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，传入的请求参数是："+JSONObject.toJSONString(requestInfo)+",IP："+ip+"，deviceType："+deviceType);
				result = iTradeCashierMchtHandler.process(requestInfo, ip, deviceType);
				logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，传入的请求参数是："+JSONObject.toJSONString(requestInfo)+",IP："+ip+"，deviceType："+deviceType+"，返回的数据为："+JSONObject.toJSONString(result));
				if(null != result && ErrorCodeEnum.SUCCESS.getCode().equals(result.getRespCode())){
					if(PayTypeEnum.CASHIER_PLAT.getCode().equals(requestInfo.getHead().getBiz())){
						//收银台页面跳转
						page = this.getPageByDeviceType(deviceType, PageTypeEnum.INDEX.getCode(), midoid);
						//设置收银台页面需要的值
						this.addCashierModelInfo(model, result, requestInfo.getBody().getGoods(), requestInfo.getBody().getAmount(), requestInfo.getHead().getMchtId(), midoid );
						logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用收银台页面，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);
					}else{
						//先判断是否跳转上游收银台
						if(isUseChanCashierPage(result.getData(), midoid)){
							//跳转到上游收银台的中转页面
							page = "modules/chanCashier/chanCashier";
							//跳转到上游收银台的中转页面，携带的数据
							this.addChanCashierModelInfo(model, result, midoid);
							logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用上游收银台的中转页面，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);

						}else{
							//使用我司页面
							//非收银台页面跳转,支付类型从result返回值取具体支付类型，找对应中间页
							Map<String, Object> retMapInfo = ( Map<String, Object>)result.getData();
							Result resultInfo = (Result) retMapInfo.get("result");
							page = this.chooseNotCashierPage(deviceType, resultInfo.getPaymentType(), midoid);
							if(DeviceTypeEnum.PC.getCode().equals(deviceType)){
								//pc页面操作，将支付地址带到页面
								this.addPcScanPageModelInfo(model, result, requestInfo.getBody().getAmount(), requestInfo.getHead().getMchtId(), requestInfo.getBody().getGoods(), requestInfo.getBody().getOrderId(), midoid);
								logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，pc端显示支付，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);
							}else{
								if(page.endsWith("scan")){
									//设置扫码中间页需要的参数
									this.addScanCentPageModelInfo(model, result, midoid);
									logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用中间页，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);
								}else if(page.endsWith("center")){
									//设置h5中间页需要的参数
									this.addH5CentPageModelInfo(model, result, userAgent, midoid);
									logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用中间页，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);
								}else{
									result.setRespCode(ErrorCodeEnum.E1018.getCode());
									result.setRespMsg("操作失败");
									page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode(), midoid);
									logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用中间页，但是未找到对应的中间页，返回的CommonResult="+JSONObject.toJSONString(result)+"跳转的页面为："+page);
								}
							}
						}
					}
				}else{
					page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode(), midoid);
					//客服信息
                    if(result != null && null != result.getData() &&  (result.getData() instanceof Map)){
						Map mapData = (Map) result.getData();
						Map<String, String> pageQQandMobile = (Map<String, String>) mapData.get("pageQQandMobile");
						qq = pageQQandMobile.get("qq");
						mobile = pageQQandMobile.get("mobile");
                    }else{
						result.setRespCode(ErrorCodeEnum.E8001.getCode());
						result.setRespMsg("操作失败");
					}
					logger.info(BIZ+midoid+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为失败，返回的CommonResult="+JSONObject.toJSONString(result));
				}
			}else{
				page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode(), midoid);
				logger.info(BIZ+midoid+"解析并校验商户请求参数失败："+ JSONObject.toJSONString(result) );
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setRespCode(ErrorCodeEnum.E8001.getCode());
			result.setRespMsg("操作失败");
			page = this.getPageByDeviceType(deviceType, PageTypeEnum.ERROR.getCode(), midoid);
			logger.error(BIZ+midoid+"接收商户请求异常："+e.getMessage());
		}
        if(StringUtils.isNotBlank(qq)){
		    model.addAttribute("qq", qq);
        }
        if(StringUtils.isNotBlank(mobile)){
		    model.addAttribute("mobile", mobile);
        }
		model.addAttribute("respCode",result.getRespCode());
		model.addAttribute("respMsg",result.getRespMsg());
		logger.info(BIZ+midoid+"处理完业务之后，返回给页面的数据为："+JSONObject.toJSONString(model));
		return page;
	}

	/**
	 * 页面轮询查单
	 * @param request
	 * @return
	 * @throws Exception
	 */
    @RequestMapping(value="queryResult")
    @ResponseBody
    public String queryResult(HttpServletRequest request) throws Exception {
        String status = PayStatusEnum.CREATE_SUCCESS.getCode();
        String platOrderId = "";
//        String deviceType = "";
        try {
//			String userAgent = this.getUserAgentInfoByRequest(request, "");
//			logger.info("页面轮询，根据请求头获取的user-agent为："+userAgent);
//			deviceType = HttpUtil.getDeviceType(userAgent);
//			logger.info("页面轮询，通过程序判断deviceType为："+deviceType+"-->【1：手机端，2：pc端，3：微信内，4：支付宝内】");
//            String ip = IpUtil.getRemoteHost(request);
			String ip = "";
            platOrderId = request.getParameter("platOrderId");
            //调用handler处理业务
            CommonResult result = iTradeCashierMchtHandler.queryStatus(ip, platOrderId);
//            logger.info("页面轮训查单，订单platOrderId="+platOrderId+"，查询结果：CommonResult="+ JSONObject.toJSONString(result));
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
//        logger.info("页面轮训查单，订单platOrderId="+platOrderId+"，查询此订单结果，返回页面status="+status);
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
		paramMap.put("currencyType",request.getParameter("currencyType"));
		System.out.println(request.getParameter("currencyType"));

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
	@RequestMapping("test/{mchtId}/{key}")
	public String test(HttpServletRequest request, Model model, @PathVariable String mchtId, @PathVariable String key) {

		System.out.println("测试页面"+request.getServerName()+request.getServerPort());
		int port = request.getServerPort();
		model.addAttribute("testUrl", request.getServerName()+(80 == port ? "" : ":" +port));
		model.addAttribute("mchtOrderId", IdUtil.getUUID());
		model.addAttribute("mchtId", mchtId);
		model.addAttribute("key", key);
		System.out.println("测试页面");
		return "modules/cashier/pc/test";
	}
}
