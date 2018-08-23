package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwSdkQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * SDK查询支付订单接口
 */
@Controller
@RequestMapping(value = "")
public class GwSdkQueryController {

	protected final Logger logger = LoggerFactory.getLogger(GwSdkQueryController.class);
	private final String BIZ_NAME = "支付SDK-查询支付订单-";
	@Autowired
	GwSdkQueryService gwSdkQueryService;

	/**支付**/
    @RequestMapping(value="/gateway/sdk/queryPay")
    @ResponseBody
    public String commPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		Map rtnMap = new HashMap();
		try {
			String ip = IpUtil.getRemoteHost(request);//请求ip
			logger.info(BIZ_NAME + "客户端请求ip：" + ip);
			data = URLDecoder.decode(data, "utf-8");
			logger.info(BIZ_NAME + "客户端请求参数后做url解码后的值为：" + data);
			//校验请求参数
			CommonResponse checkResp = gwSdkQueryService.checkParam(data);
			logger.info(BIZ_NAME + "校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));
			if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
				rtnMap.put("code", checkResp.getRespCode());
				rtnMap.put("msg", checkResp.getRespMsg());
			} else {
				Map reqData = (Map) checkResp.getData();
				logger.info(BIZ_NAME + "传入的信息：" + JSONObject.toJSONString(reqData));
				rtnMap = gwSdkQueryService.query(reqData, ip);
				logger.info(BIZ_NAME + "返回信息：" + JSONObject.toJSONString(rtnMap));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(BIZ_NAME + "异常" + e.getMessage());
			rtnMap.put("code", ErrorCodeEnum.FAILURE.getCode());
			rtnMap.put("msg", ErrorCodeEnum.FAILURE.getDesc());
		}
		logger.info(BIZ_NAME + "返回下游商户：" + JSON.toJSONString(rtnMap));
		return JSON.toJSONString(rtnMap);
    }

}
