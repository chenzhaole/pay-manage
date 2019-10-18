package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.authcardelement.TradeAuthCardElementRequest;
import com.sys.boss.api.entry.trade.response.authcardelement.AuthCardeLElementCreateResponse;
import com.sys.boss.api.entry.trade.response.ownership.OwnershipResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.BankCardOwnershipGwService;
import com.sys.gateway.service.GwAuthCardElementService;
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
 * 银行卡N要素认证
 *
 * @author: ChenZL
 * @time: 2018年3月28日
 */

@Controller
@RequestMapping(value = "")
public class GwAuthCardElementController {

	protected final Logger logger = LoggerFactory.getLogger(GwAuthCardElementController.class);
	private final String BIZ_NAME = PayTypeEnum.QUICK_OWNERSHIP.getDesc()+"-";

	@Autowired
	private GwAuthCardElementService gwAuthCardElementService;

	/**四要素和六要素认证**/
	@RequestMapping(value="/pay/gateway/authCardElement", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String ownership(@RequestBody String data, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes)throws java.io.IOException {
		this.logger.info("实名认证即四要素、六要素认证接口收到客户端请求参数：data=" + data);
		AuthCardeLElementCreateResponse authResp = new AuthCardeLElementCreateResponse();
		AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseHead head = new AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseHead();

		try {
			String e = IpUtil.getRemoteHost(request);
			this.logger.info("实名认证即四要素、六要素认证接口获取到客户端请求ip为：ip=" + e);
			data = URLDecoder.decode(data, "utf-8");
			this.logger.info("实名认证即四要素、六要素认证接口收到客户端请求参数后做url解码后的值为：data=" + data);
			CommonResponse checkResp = gwAuthCardElementService.checkAuthCardParam(data);
			this.logger.info("实名认证即四要素、六要素认证接口校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));
			if(!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
				head.setRespCode(checkResp.getRespCode());
				head.setRespMsg(checkResp.getRespMsg());
				authResp.setHead(head);
			} else {
				TradeAuthCardElementRequest tradeRequest = (TradeAuthCardElementRequest)checkResp.getData();
				this.logger.info("掉实名认证即四要素、六要素认证接口，传入的TradeAuthCardElementRequest信息为：TradeAuthCardElementRequest=" + JSONObject.toJSONString(tradeRequest));
				authResp = (AuthCardeLElementCreateResponse)gwAuthCardElementService.authCardElement(tradeRequest, e);
				this.logger.info("掉实名认证即四要素、六要素认证接口，返回的QuickValidOrderCreateResponse信息为：QuickValidOrderCreateResponse=" + JSONObject.toJSONString(authResp));
			}
		} catch (Exception var10) {
			var10.printStackTrace();
			this.logger.error("实名认证即四要素、六要素认证接口抛异常" + var10.getMessage());
			head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
			head.setRespMsg("支付网关错误：" + var10.getMessage());
		}

		this.logger.info("创建实名认证即四要素、六要素认证接口订单，返回下游商户值：" + JSON.toJSONString(authResp));
		return JSON.toJSONString(authResp);
	}
}
