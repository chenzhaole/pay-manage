package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwPublicAccountService;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 公户账务查询接口
 */
@Controller
@RequestMapping(value = "")
public class GwPublicAccountController {

	protected final Logger logger = LoggerFactory.getLogger(GwPublicAccountController.class);

	@Autowired
	GwPublicAccountService gwPublicAccountService;

	private String BIZ = "公户余额查询";

	/**
	 *  公户余额查询
	 */
	@RequestMapping(value="/publicaccount/queryBalance")
	@ResponseBody
	public String queryBalance(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("code",ErrorCodeEnum.SUCCESS.getCode());
		try {
			//请求ip
			String ip = IpUtil.getRemoteHost(request);
			logger.info(BIZ+"获取到客户端请求ip："+ip+",publicAccountCode="+request.getParameter("publicAccountCode")+",sign="+request.getParameter("sign"));
			//校验请求参数
			Map<String,String> params = new HashMap<>();
			params.put("publicAccountCode",request.getParameter("publicAccountCode"));
			params.put("sign",request.getParameter("sign"));
			CommonResponse checkResp = gwPublicAccountService.checkParam(params);
			logger.info(BIZ+"校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
			if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				resultMap.put("code",checkResp.getRespCode());
				resultMap.put("msg",checkResp.getRespMsg());
			}else{
				resultMap = gwPublicAccountService.query(params);
				logger.info(BIZ+"，返回的resultMap信息："+JSONObject.toJSONString(resultMap));
			}
		} catch (Exception e) {
			logger.error(BIZ+"异常",e);
			resultMap.put("code",ErrorCodeEnum.FAILURE.getCode());
			resultMap.put("msg",ErrorCodeEnum.FAILURE.getDesc());
		}
		logger.info(BIZ+"，返回值："+JSON.toJSONString(resultMap));
		return JSON.toJSONString(resultMap);
	}
}
