package com.sys.admin.modules.channel.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.enums.AdminPayTypeEnum;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;
import com.sys.admin.modules.channel.bo.ChannelFormInfo;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.channel.service.ChannelAdminService;
import com.sys.admin.modules.merchant.bo.MerchantForm;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.admin.modules.platform.bo.ProductRelaFormInfo;
import com.sys.admin.modules.platform.service.ProductAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.CommonResult;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.PlatFeerateService;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.SingleDF;
import com.sys.trans.api.entry.Trade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.sys.core.service.ConfigSysService;

@Controller
@RequestMapping(value = "${adminPath}/channel")
public class ChannelController extends BaseController {

	@Autowired
	ChannelAdminService channelAdminService;

	@Autowired
	MerchantAdminService merchantAdminService;

	@Autowired
	ChanMchtAdminService chanMchtAdminService;

//	@Autowired
//	ConfigSysService configSysService;

	@Autowired
	PlatFeerateService platFeerateService;

	@Autowired
	ProductAdminService productAdminService;

	/**
	 * 通道列表
	 */
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		ChanInfo searchInfo = new ChanInfo();
		//获取页面查询条件参数
		String chanNo = paramMap.get("chanCode");
		String name = paramMap.get("name");
		String pageNoString = paramMap.get("pageNo");
		searchInfo.setChanCode(chanNo);
		searchInfo.setName(name);

		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		searchInfo.setPageInfo(pageInfo);
		//调用channelService获取支付通道列表
		List<ChanInfo> chanInfoList = channelAdminService.getChannelList(searchInfo);
		int chanCount = channelAdminService.chanCount(searchInfo);
		Page page = new Page(pageNo, pageInfo.getPageSize(), chanCount, chanInfoList, true);
		model.addAttribute("page", page);
//		model.addAttribute("list", chanInfoList);
		model.addAttribute("paramMap", paramMap);
		return "modules/channel/channelList";
	}

	/**
	 * 通道查询
	 */
	@RequestMapping(value = {"select", ""})
	public String select(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		try {
			String url = GlobalConfig.getConfig("boss.url") + "channel/list";
//        	String resp = HttpUtil.post(url, paramMap);
//        	DataResponse dataResponse = JSONObject.parseObject(resp, DataResponse.class);
//        	List<MchtInfo> list = new ArrayList<MchtInfo>();
//        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(dataResponse.getCode())){
//        		String data = JSONObject.toJSONString(dataResponse.getData());
//        		list = JSONObject.parseObject(data,List.class);
//        	}
//            model.addAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return "modules/channel/channelList";
	}

	/**
	 * 通道编辑
	 */
	@RequestMapping(value = {"add"})
	public String add(HttpServletRequest request, HttpServletResponse response, Model model,
					  @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		model.addAttribute("op", "add");

		return "modules/channel/channelEdit";
	}

	@RequestMapping(value = {"addSave", ""})
	public String addSave(HttpServletRequest request, HttpServletResponse response, Model model,
						  @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		try {
			//创建者UserId
			String userId = String.valueOf(UserUtils.getUser().getId());
			paramMap.put("userId", userId);
			ChannelFormInfo channelFormInfo = new ChannelFormInfo(request);
			ChannelFormInfo oldChannelFormInfo = channelAdminService.getChanInfoById(channelFormInfo.getChanCode());
			if (null != oldChannelFormInfo && channelFormInfo.getChanCode().equals(oldChannelFormInfo.getChanCode())) {
				redirectAttributes.addFlashAttribute("message", "通道编号已存在！");
				redirectAttributes.addFlashAttribute("messageType", "error");
				model.addAttribute("channel", paramMap);
				model.addAttribute("message", "通道编号已存在！");
				model.addAttribute("messageType", "error！");
				return "modules/channel/channelEdit";
			}

			if (ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(channelFormInfo.getCode())) {
				channelFormInfo.setOperatorId(Long.valueOf(userId));
				//进行新增操作
				String result = channelAdminService.addChanInfo(channelFormInfo);
				if ("success".equals(result)) {
					redirectAttributes.addFlashAttribute("message", "保存记录成功！");
					redirectAttributes.addFlashAttribute("messageType", "success");
					return "redirect:" + GlobalConfig.getAdminPath() + "/channel/list";
				}
			} else {
				redirectAttributes.addFlashAttribute("message", "操作失败！");
				redirectAttributes.addFlashAttribute("messageType", "error");
				return "modules/channel/channelEdit";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/channel/channelEdit";
	}

	@RequestMapping(value = {"edit"})
	public String edit(ChanInfo chanInfo, Model model) {
		model.addAttribute("op", "edit");
		ChannelFormInfo chanFormInfo = channelAdminService.getChanInfoById(chanInfo.getId());
		model.addAttribute("channel", chanFormInfo);
		return "modules/channel/channelEdit";
	}

	@RequestMapping(value = {"editSave", ""})
	public String editSave(HttpServletRequest request, HttpServletResponse response, Model model,
						   @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		try {
			//更新人
			String userId = String.valueOf(UserUtils.getUser().getId());
			paramMap.put("userId", userId);
			ChannelFormInfo channelFormInfo = new ChannelFormInfo(request);
			ChannelFormInfo oldChannelFormInfo = channelAdminService.getChanInfoById(channelFormInfo.getChanCode());
			if (null != oldChannelFormInfo && channelFormInfo.getChanCode().equals(oldChannelFormInfo.getChanCode())) {
				redirectAttributes.addFlashAttribute("message", "通道编号已存在！");
				redirectAttributes.addFlashAttribute("messageType", "error");
				return "modules/channel/channelEdit";
			}
			if (ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(channelFormInfo.getCode())) {
				channelFormInfo.setOperatorId(Long.valueOf(userId));
				//进行更新操作
				String result = channelAdminService.updateChanInfo(channelFormInfo);
				if ("success".equals(result)) {
					redirectAttributes.addFlashAttribute("message", "更新通道信息成功！");
					redirectAttributes.addFlashAttribute("messageType", "success");
					return "redirect:" + GlobalConfig.getAdminPath() + "/channel/list";
				}
			} else {
				redirectAttributes.addFlashAttribute("message", "操作失败！");
				redirectAttributes.addFlashAttribute("messageType", "error");
				return "modules/channel/channelEdit";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return "modules/channel/channelEdit";
	}

	@RequestMapping(value = {"deleteChannel", ""})
	public String deleteChannel(HttpServletRequest request, HttpServletResponse response, Model model,
								@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result;
		String message;

		ChanMchtFormInfo chanMchtFormInfo = new ChanMchtFormInfo();
		chanMchtFormInfo.setChanId(paramMap.get("id"));
		List<ChanMchtFormInfo> chanMchtFormInfos = chanMchtAdminService.getChannelListSimple(chanMchtFormInfo);
		if (CollectionUtils.isEmpty(chanMchtFormInfos)) {
			result = channelAdminService.deleteChanInfo(paramMap.get("id"));

			if (result == 1) {
				message = "删除成功";
				redirectAttributes.addFlashAttribute("messageType", "success");
			} else {
				message = "删除失败";
				redirectAttributes.addFlashAttribute("messageType", "error");
			}

		} else {
			message = "该通道已配置通道商户支付方式，无法删除";
			redirectAttributes.addFlashAttribute("messageType", "error");
		}
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:" + GlobalConfig.getAdminPath() + "/channel/list";
	}

	/**
	 * 支付通道列表
	 */
	@RequestMapping(value = {"mchPaytypeList", ""})
	public String mchPaytypelist(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap
	) {

		ChanMchtFormInfo searchInfo = new ChanMchtFormInfo(request);

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		searchInfo.setPageInfo(pageInfo);

		List<ChanMchtFormInfo> chanInfoList = chanMchtAdminService.getChannelList(searchInfo);
//		model.addAttribute("list", chanInfoList);
		int chanCount = chanMchtAdminService.chanMchtCount(searchInfo);
		Page page = new Page(pageNo, pageInfo.getPageSize(), chanCount, chanInfoList, true);
		model.addAttribute("page", page);

		//所有支付方式
		AdminPayTypeEnum[] prePayTypeList = AdminPayTypeEnum.values();
		//过滤掉不常用的
		model.addAttribute("paymentTypeInfos", prePayTypeList);
//		List<PaymentTypeInfo> paymentTypeInfos = configSysService.listAllPaymentTypeInfo();
//		model.addAttribute("paymentTypeInfos", paymentTypeInfos);

		model.addAttribute("paramMap", paramMap);
		return "modules/channel/chanMchtPaytypeList";
	}


	/**
	 * 商户支付通道新增或修改的页面
	 */
	@RequestMapping(value = {"addChanMchtPayTypePage"})
	public String addChanMchtPayTypePage(HttpServletRequest request, HttpServletResponse response, Model model,
										 @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		ChanMchtFormInfo searchInfo = new ChanMchtFormInfo(request);

		if (StringUtils.isNotBlank(searchInfo.getId())) {
			ChanMchtFormInfo chanMchtFormInfo = chanMchtAdminService.getChanMchtPaytypeById(searchInfo.getId());
			model.addAttribute("chanMchPaytye", chanMchtFormInfo);
			model.addAttribute("op", "edit");
		} else {
			ChanMchtFormInfo chanMchtFormInfo = new ChanMchtFormInfo();
			chanMchtFormInfo.setTradeEndTimeH("23");
			chanMchtFormInfo.setTradeEndTimeS("59");
			model.addAttribute("chanMchPaytye", chanMchtFormInfo);
			model.addAttribute("op", "add");
		}

		List<ChanInfo> chanInfos = channelAdminService.getChannelList(new ChanInfo());
		model.addAttribute("chanInfos", chanInfos);

		//商户列表 只展示申报商户及服务商`
		List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(new MchtInfo());
		List<MerchantForm> mchtInfosResult = new ArrayList<>();
		for (MerchantForm mchtInfo : mchtInfos) {
			if (StringUtils.isBlank(mchtInfo.getSignType())) {
				continue;
			}
			if (!mchtInfo.getSignType().contains(SignTypeEnum.SINGLE_MCHT.getCode())) {
				if (mchtInfo.getSignType().contains(SignTypeEnum.SIGN_MCHT.getCode())
						|| mchtInfo.getSignType().contains(SignTypeEnum.SERVER_MCHT.getCode())) {
					mchtInfosResult.add(mchtInfo);
				}
			}
		}
		model.addAttribute("mchtInfos", mchtInfosResult);

		//签了服务商合同的通道商户支付方式列表，选上级用
		List<ChanMchtFormInfo> chanInfoList = chanMchtAdminService.getAllChannel(searchInfo);
		model.addAttribute("chanMchts", chanInfoList);

		//支付方式
		AdminPayTypeEnum[] payTypeList = AdminPayTypeEnum.values();
		model.addAttribute("paymentTypeInfos", payTypeList);
//		List<PaymentTypeInfo> paymentTypeInfos = configSysService.listAllPaymentTypeInfo();
//		model.addAttribute("paymentTypeInfos", paymentTypeInfos);

		return "modules/channel/chanMchtPayTypeEdit";
	}

	/**
	 * 商户支付通道新增
	 */
	@RequestMapping(value = {"addChanMchtPayType"})
	public String addChanMchPayType(HttpServletRequest request, HttpServletResponse response, Model model,
									@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result = 0;
		String message, messageType;
		try {
			ChanMchtFormInfo searchInfo = new ChanMchtFormInfo(request);

			if ("add".equals(paramMap.get("op"))) {
				//校验重复
				ChanMchtFormInfo searchCMP = new ChanMchtFormInfo();
				searchCMP.setChanId(searchInfo.getChanId());
				searchCMP.setMchtId(searchInfo.getMchtId());
				searchCMP.setPayType(searchInfo.getPayType());
				searchCMP.setContractType(searchInfo.getContractType());
				List<ChanMchtFormInfo> chanMchtFormInfos = chanMchtAdminService.getChannelListSimple(searchCMP);

				if (CollectionUtils.isEmpty(chanMchtFormInfos)) {
					result = chanMchtAdminService.addChanMchtPaytype(searchInfo);
				} else {
					result = 99;
				}

			} else if ("edit".equals(paramMap.get("op"))) {
				result = chanMchtAdminService.updateChanMchtPaytype(searchInfo);
			}

			if (result == 1) {
				message = "保存成功";
				messageType = "success";
			} else if (result == 99) {
				message = "该通道商户支付方式已存在";
				messageType = "error";
			} else {
				message = "保存失败";
				messageType = "error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = "保存失败";
			messageType = "error";
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/channel/mchPaytypeList";
	}

	/**
	 * 商户支付通道删除
	 */
	@RequestMapping(value = {"deleteChanMchPayType"})
	public String deleteChanMchPayType(HttpServletRequest request, HttpServletResponse response, Model model,
									   @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {

		ChanMchtFormInfo searchInfo = new ChanMchtFormInfo(request);

		String message, messageType;

		ProductRelaFormInfo productRelaFormInfo = new ProductRelaFormInfo();
		productRelaFormInfo.setChanMchtPaytypeId(searchInfo.getId());

		ChanMchtFormInfo chanMchtFormInfo = chanMchtAdminService.getChanMchtPaytypeById(searchInfo.getId());

		boolean active = false;
		if (StatusEnum.VALID.getCode().equals(chanMchtFormInfo.getStatus())) {
			active = true;
		}

		List<String> productIdsTemp = productAdminService.getProductIdByRela(productRelaFormInfo);

		if (active) {
			message = "该通道商户支付方式已启用，无法删除";
			messageType = "error";
		} else {
			if (CollectionUtils.isEmpty(productIdsTemp)) {
				int result = chanMchtAdminService.deleteChanMchtPaytype(searchInfo);

				if (result == 1) {
					message = "删除成功";
					messageType = "success";
				} else {
					message = "删除失败";
					messageType = "error";
				}
			} else {
				message = "该通道商户支付方式已配置产品，无法删除";
				messageType = "error";
			}
		}

		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/channel/mchPaytypeList";
	}

	/**
	 * 商户支付通道余额查询
	 */
	@RequestMapping(value = {"queryBalance"})
	public String queryBalance(HttpServletRequest request, HttpServletResponse response, Model model,
							   @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {

		ChanMchtFormInfo chanMchtPaytype = chanMchtAdminService.getChanMchtPaytypeById(paramMap.get("chanId"));

		Config config = new Config();
		config.setPayUrl(chanMchtPaytype.getPayUrl());
		config.setQueryUrl(chanMchtPaytype.getQueryBalanceUrl());
		config.setTranUrl(chanMchtPaytype.getTranUrl());
		config.setChannelCode(chanMchtPaytype.getChanCode());
		config.setCancelUrl(chanMchtPaytype.getCancelUrl());
		config.setNotifyUrl(chanMchtPaytype.getAsynNotifyUrl());
		config.setPayType(chanMchtPaytype.getPayType());
		config.setMchtId(chanMchtPaytype.getChanMchtNo());
		config.setMchtKey(chanMchtPaytype.getChanMchtPassword());
		config.setCertPath1(chanMchtPaytype.getCertPath1());
		config.setCertPath2(chanMchtPaytype.getCertPath2());
		config.setPlatId(chanMchtPaytype.getTerminalNo());
		config.setPubKey(chanMchtPaytype.getCertContent1());
		config.setPriKey(chanMchtPaytype.getCertContent2());

		SingleDF df = new SingleDF();
		df.setOrderNo("ADMIN0" + IdUtil.createCode());
		df.setAmount("1");

		Trade trade = new Trade();
		trade.setConfig(config);
		trade.setSingleDF(df);

		try {

			String topUrl = ConfigUtil.getValue("gateway.url");
			if (topUrl.endsWith("/")) {
				topUrl = topUrl.substring(0, topUrl.length() - 1);
			}
			String gatewayUrl = topUrl + "/df/gateway/chanBalanceForAdmin";
			Map<String, String> params = new HashMap<>();
			params.put("tradeString", JSON.toJSONString(trade));
			logger.info(trade + " 查询上游商户余额,请求URL: " + gatewayUrl + " 请求参数: " + JSON.toJSONString(params));
			String balanceString = HttpUtil.postConnManager(gatewayUrl, params, true);
			logger.info(balanceString);
			CommonResult processResult = JSON.parseObject(balanceString, CommonResult.class);
			if (processResult != null) {
				String balance = (String) processResult.getData();
				if (StringUtils.isNotBlank(balance)){
					balance = NumberUtils.changeF2Y(balance);
				}
				model.addAttribute("balance", balance);
			}
		} catch (Exception e) {
 			logger.error("查询余额失败", e);
		}
		return "modules/channel/chanBalance";
	}
}


