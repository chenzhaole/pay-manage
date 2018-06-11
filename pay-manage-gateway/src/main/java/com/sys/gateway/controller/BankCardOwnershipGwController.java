package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.trade.response.ownership.OwnershipResponse;
import com.sys.common.enums.PayTypeEnum;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.BankCardOwnershipGwService;
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

/**
 * 银行卡归属信息查询
 *
 * @author: ChenZL
 * @time: 2017年12月12日
 */

@Controller
@RequestMapping(value = "")
public class BankCardOwnershipGwController {

	protected final Logger logger = LoggerFactory.getLogger(BankCardOwnershipGwController.class);
	private final String BIZ_NAME = PayTypeEnum.QUICK_OWNERSHIP.getDesc() + "-";

	@Autowired
	private BankCardOwnershipGwService bankCardOwnershipGwService;

	/**
	 * 银行卡归属信息查询
	 **/
	@RequestMapping(value = "/gateway/ownership", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String ownership(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {

		OwnershipResponse ownershipResponse = new OwnershipResponse();

		try {
			String ip = IpUtil.getRemoteHost(request);//请求ip
			logger.info(BIZ_NAME + "获取到客户端请求ip为：ip=" + ip);
			data = URLDecoder.decode(data, "utf-8");
			logger.info(BIZ_NAME + "收到客户端请求参数后做url解码后的值为：data=" + data);
			//校验请求参数
			ownershipResponse = bankCardOwnershipGwService.queryBankCardOwnership(data);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(BIZ_NAME + "接口抛异常" + e.getMessage());
		}
		logger.info(BIZ_NAME + "返回下游商户值：" + JSON.toJSONString(ownershipResponse));
		return JSON.toJSONString(ownershipResponse);
	}
}
