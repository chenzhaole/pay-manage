package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwSdkConfigService;
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
 * 通用支付接口
 *
 * @author: ChenZL
 * @time: 2017年11月28日
 */
@Controller
@RequestMapping(value = "")
public class GwSdkConfigController {

	protected final Logger logger = LoggerFactory.getLogger(GwSdkConfigController.class);

	private final String BIZ_NAME="支付SDK-获取配置信息-";

	@Autowired
	GwSdkConfigService gwSdkConfigService;

	/**支付**/
    @RequestMapping(value="/gateway/sdk/config")
    @ResponseBody
    public String config(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		Map rtnMap = new HashMap();
        try {
        	String ip = IpUtil.getRemoteHost(request);//请求ip
    		logger.info(BIZ_NAME+"获取到客户端请求ip："+ip);
        	data = URLDecoder.decode(data, "utf-8");
    		logger.info(BIZ_NAME+"收到客户端请求参数后做url解码后的值为："+data);

    		//校验请求参数
        	CommonResponse checkResp = gwSdkConfigService.checkParam(data);
    		logger.info(BIZ_NAME+"校验请求参数的结果为："+JSONObject.toJSONString(checkResp));
        	if( !ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())){
				rtnMap.put("code",checkResp.getRespCode());
				rtnMap.put("msg",checkResp.getRespMsg());
        	}else{
				rtnMap.put("code",ErrorCodeEnum.SUCCESS.getCode());
				rtnMap.put("msg",ErrorCodeEnum.SUCCESS.getDesc());
        		Map map = (Map) checkResp.getData();
				rtnMap =  gwSdkConfigService.config(map, ip);
        	}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(BIZ_NAME+"comm支付接口抛异常"+e.getMessage());
		}
        logger.info(BIZ_NAME+"返回下游客户端："+JSON.toJSONString(rtnMap));
        return JSON.toJSONString(rtnMap);
    }

}
