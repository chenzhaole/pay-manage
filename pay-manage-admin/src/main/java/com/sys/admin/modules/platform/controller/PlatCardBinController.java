package com.sys.admin.modules.platform.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.core.service.PlatBankService;
import com.sys.core.service.PlatCardBinService;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.PlatBank;
import com.sys.core.dao.dmo.PlatCardBin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/platform")
public class PlatCardBinController extends BaseController {

	@Autowired
	private PlatCardBinService platCardBinService;

	@Autowired
	private PlatBankService platBankService;

	/**
	 * 通道银行列表
	 */
	@RequestMapping(value = {"cardBinList", ""})
	public String cardBinList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap
	) {

		PlatCardBin searchInfo = new PlatCardBin();
		searchInfo.setCardBinNo(paramMap.get("cardBinNo"));
		searchInfo.setBankCode(paramMap.get("bankCode"));
		searchInfo.setCardType(paramMap.get("cardType"));
		searchInfo.setStatus(paramMap.get("status"));

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		searchInfo.setPageInfo(pageInfo);

		List<PlatCardBin> chanInfoList = platCardBinService.list(searchInfo);

		int chanCount = platCardBinService.recordCount(searchInfo);
		Page page = new Page(pageNo, pageInfo.getPageSize(), chanCount, chanInfoList, true);
		model.addAttribute("page", page);

		//银行列表
		List<PlatBank> platBanks = platBankService.list(new PlatBank());
		model.addAttribute("platBanks", platBanks);

		model.addAttribute("paramMap", paramMap);
		return "modules/platform/platCardBinList";
	}

	/**
	 * 商户支付通道新增或修改的页面
	 */
	@RequestMapping(value = {"addCardBinPage"})
	public String addChanPaytypeBankPage(HttpServletRequest request, HttpServletResponse response, Model model,
										 @RequestParam Map<String, String> paramMap) {

		if (StringUtils.isNotBlank(paramMap.get("id"))) {
			PlatCardBin platCardBin = platCardBinService.queryByKey(Integer.parseInt(paramMap.get("id")));
			model.addAttribute("platCardBin", platCardBin);
			model.addAttribute("op", "edit");
		} else {
			model.addAttribute("op", "add");
		}

		//所有银行
		List<PlatBank> platBanks = platBankService.list(new PlatBank());
		model.addAttribute("platBanks", platBanks);

		return "modules/platform/platCardBinEdit";
	}

	/**
	 * 商户支付通道新增
	 */
	@RequestMapping(value = {"addCardBin"})
	public String addChanMchPayType(HttpServletRequest request, HttpServletResponse response, Model model,
									@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result = 0;
		String message, messageType;
		try {

			PlatCardBin searchInfo = new PlatCardBin();
			searchInfo.setCardBinNo(paramMap.get("cardBinNo"));
			searchInfo.setCardName(paramMap.get("cardName"));
			searchInfo.setBankCode(paramMap.get("bankCode"));
			searchInfo.setOrgNo(paramMap.get("orgNo"));
			searchInfo.setCardLength(Integer.parseInt(paramMap.get("cardLength")));
			searchInfo.setCardType(paramMap.get("cardType"));
			searchInfo.setAccType(paramMap.get("accType"));
			searchInfo.setStatus(paramMap.get("status"));
			searchInfo.setExtend(paramMap.get("extend"));
			searchInfo.setCreateDate(new Date());

			if ("add".equals(paramMap.get("op"))) {

				if (StringUtils.isBlank(paramMap.get("bankCode"))){
					message = "未选择银行";
					messageType = "error";
					redirectAttributes.addFlashAttribute("messageType", messageType);
					redirectAttributes.addFlashAttribute("message", message);
					return "redirect:" + GlobalConfig.getAdminPath() + "/platform/cardBinList";
				}

				//校验重复
				PlatCardBin searchCMP = new PlatCardBin();
				searchCMP.setCardBinNo(searchInfo.getCardBinNo());
				List<PlatCardBin> platCardBins = platCardBinService.list(searchCMP);

				PlatBank platBankSearch = new PlatBank();
				platBankSearch.setBankCode(searchInfo.getBankCode());
				List<PlatBank> platBanks = platBankService.list(platBankSearch);
				if (CollectionUtils.isNotEmpty(platBanks)){
					searchInfo.setBankName(platBanks.get(0).getBankName());
				}

				if (CollectionUtils.isEmpty(platCardBins)) {
					result = platCardBinService.create(searchInfo);
				} else {
					result = 99;
				}

			} else if ("edit".equals(paramMap.get("op"))) {
				searchInfo.setId(Integer.parseInt(paramMap.get("id")));
				PlatCardBin platCardBin = platCardBinService.queryByKey(searchInfo.getId());
				searchInfo.setBankCode(platCardBin.getBankCode());
				searchInfo.setBankName(platCardBin.getBankName());
				searchInfo.setUpdateDate(new Date());
				result = platCardBinService.saveByKey(searchInfo);
			}

			if (result == 1) {
				message = "保存成功";
				messageType = "success";
			} else if (result == 99) {
				message = "卡BIN号重复";
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
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/cardBinList";
	}

	/**
	 * 商户支付通道删除
	 */
	@RequestMapping(value = {"deleteCardBin"})
	public String deleteChanPaytypeBank(HttpServletRequest request, HttpServletResponse response, Model model,
									   @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {


		String message, messageType;

		int result = 0;
		try {
			result = platCardBinService.deleteByKey(Integer.parseInt(paramMap.get("id")));
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
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/cardBinList";
	}
}
