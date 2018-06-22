package com.sys.admin.modules.channel.controller;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.enums.AdminPayTypeEnum;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.bo.ChanBankFormInfo;
import com.sys.admin.modules.channel.service.ChanBankAdminService;
import com.sys.admin.modules.channel.service.ChannelAdminService;
import com.sys.admin.modules.merchant.bo.MerchantForm;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatBank;
import com.sys.core.service.PlatBankService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/channel")
public class ChanBankController extends BaseController {

	@Autowired
	private ChannelAdminService channelAdminService;

	@Autowired
	private MerchantAdminService merchantAdminService;

	@Autowired
	private ChanBankAdminService chanBankAdminService;

	@Autowired
	private PlatBankService platBankService;

	/**
	 * 通道银行列表
	 */
	@RequestMapping(value = {"chanBankList", ""})
	public String chanBankList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap
	) {

		ChanBankFormInfo searchInfo = new ChanBankFormInfo(request);

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		searchInfo.setPageInfo(pageInfo);

		List<ChanBankFormInfo> chanInfoList = chanBankAdminService.search(searchInfo);
		//银行列表
		List<PlatBank> platBanks = platBankService.list(new PlatBank());
		Map<String,String> platBankMap = Collections3.extractToMap(platBanks,"bankCode","bankName");

		//通道商户支付方式列表
		List<ChanInfo> chanInfos = channelAdminService.getChannelList(new ChanInfo());
		Map<String, String> channelMap = Collections3.extractToMap(chanInfos, "chanCode", "name");
		for (ChanBankFormInfo chanBankFormInfo : chanInfoList) {
			chanBankFormInfo.setChanName(channelMap.get(chanBankFormInfo.getChanCode()));
			chanBankFormInfo.setBankName(platBankMap.get(chanBankFormInfo.getPlatBankCode()));
		}

//		model.addAttribute("list", chanInfoList);
		int chanCount = chanBankAdminService.chanBankCount(searchInfo);
		Page page = new Page(pageNo, pageInfo.getPageSize(), chanCount, chanInfoList, true);
		model.addAttribute("page", page);

		//所有支付方式
		AdminPayTypeEnum[] payTypeList = AdminPayTypeEnum.values();
		model.addAttribute("paymentTypeInfos", payTypeList);


		model.addAttribute("platBanks", platBanks);

		model.addAttribute("paramMap", paramMap);
		return "modules/channel/chanPaytypeBankList";
	}

	/**
	 * 商户支付通道新增或修改的页面
	 */
	@RequestMapping(value = {"addChanPaytypeBankPage"})
	public String addChanPaytypeBankPage(HttpServletRequest request, HttpServletResponse response, Model model,
										 @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		ChanBankFormInfo searchInfo = new ChanBankFormInfo(request);

		if (StringUtils.isNotBlank(searchInfo.getId())) {
			ChanBankFormInfo chanBankFormInfo = chanBankAdminService.getChanPaytypeBankById(searchInfo.getId());
			model.addAttribute("chanBank", chanBankFormInfo);
			model.addAttribute("op", "edit");
		} else {
			model.addAttribute("op", "add");
		}

		List<ChanInfo> chanInfos = channelAdminService.getChannelList(new ChanInfo());
		model.addAttribute("chanInfos", chanInfos);

		//商户列表 只展示申报商户及服务商
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

		//支付方式
		AdminPayTypeEnum[] payTypeList = AdminPayTypeEnum.values();
		model.addAttribute("paymentTypeInfos", payTypeList);

		//所有银行
		List<PlatBank> platBanks = platBankService.list(new PlatBank());
		model.addAttribute("platBanks", platBanks);

		return "modules/channel/chanPaytypeBankEdit";
	}

	/**
	 * 商户支付通道新增
	 */
	@RequestMapping(value = {"addChanPaytypeBank"})
	public String addChanMchPayType(HttpServletRequest request, HttpServletResponse response, Model model,
									@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result = 0;
		String message, messageType;
		try {
			ChanBankFormInfo searchInfo = new ChanBankFormInfo(request);

			if ("add".equals(paramMap.get("op"))) {

				if (StringUtils.isBlank(searchInfo.getPlatBankCode())) {
					message = "未选择银行";
					messageType = "error";
					redirectAttributes.addFlashAttribute("messageType", messageType);
					redirectAttributes.addFlashAttribute("message", message);
					return "redirect:" + GlobalConfig.getAdminPath() + "/channel/chanBankList";
				}

				//校验重复
				ChanBankFormInfo searchCMP = new ChanBankFormInfo();
				searchCMP.setChanCode(searchInfo.getChanCode());
				searchCMP.setPlatBankCode(searchInfo.getPlatBankCode());
				searchCMP.setPayType(searchInfo.getPayType());
				List<ChanBankFormInfo> ChanBankFormInfos = chanBankAdminService.search(searchCMP);

				if (CollectionUtils.isEmpty(ChanBankFormInfos)) {
					searchInfo.setOperatorId((UserUtils.getUser().getId()));
					result = chanBankAdminService.addChanPaytypeBank(searchInfo);
				} else {
					result = 99;
				}

			} else if ("edit".equals(paramMap.get("op"))) {
				ChanBankFormInfo chanBankFormInfo = chanBankAdminService.getChanPaytypeBankById(searchInfo.getId());
				searchInfo.setChanCode(chanBankFormInfo.getChanCode());
				searchInfo.setPayType(chanBankFormInfo.getPayType());
				searchInfo.setPlatBankCode(chanBankFormInfo.getPlatBankCode());
				searchInfo.setCreateDate(chanBankFormInfo.getCreateDate());
				searchInfo.setUpdateDate(new Date());
				result = chanBankAdminService.updateChanPaytypeBank(searchInfo);
			}

			if (result == 1) {
				message = "保存成功";
				messageType = "success";
			} else if (result == 99) {
				message = "该通道支付方式银行已存在";
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
		return "redirect:" + GlobalConfig.getAdminPath() + "/channel/chanBankList";
	}

	/**
	 * 商户支付通道删除
	 */
	@RequestMapping(value = {"deleteChanPaytypeBank"})
	public String deleteChanPaytypeBank(HttpServletRequest request, HttpServletResponse response, Model model,
										@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {

		ChanBankFormInfo searchInfo = new ChanBankFormInfo(request);

		String message, messageType;

		int result = 0;
		try {
			result = chanBankAdminService.deleteChanPaytypeBank(searchInfo);
		} catch (Exception e) {
			logger.info("删除失败", e);
		}

		if (result == 1) {
			message = "删除成功";
			messageType = "success";
		} else {
			message = "删除失败";
			messageType = "error";
		}

		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/channel/chanBankList";
	}
}
