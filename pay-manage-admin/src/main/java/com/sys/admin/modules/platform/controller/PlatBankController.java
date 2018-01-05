package com.sys.admin.modules.platform.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.core.service.PlatBankService;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.PlatBank;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/platform")
public class PlatBankController extends BaseController {

	@Autowired
	private PlatBankService platBankService;

	/**
	 * 平台银行列表
	 */
	@RequestMapping(value = {"platBankList", ""})
	public String platBankList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap
	) {

		PlatBank searchInfo = new PlatBank();
		searchInfo.setBankName(paramMap.get("bankName"));
		searchInfo.setBankCode(paramMap.get("bankCode"));
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

		List<PlatBank> chanInfoList = platBankService.list(searchInfo);

		int chanCount = platBankService.count(searchInfo);
		Page page = new Page(pageNo, pageInfo.getPageSize(), chanCount, chanInfoList, true);
		model.addAttribute("page", page);

		model.addAttribute("paramMap", paramMap);
		return "modules/platform/platBankList";
	}

	/**
	 * 平台银行新增或修改的页面
	 */
	@RequestMapping(value = {"addPlatBankPage"})
	public String addChanPaytypeBankPage(HttpServletRequest request, HttpServletResponse response, Model model,
										 @RequestParam Map<String, String> paramMap) {

		if (StringUtils.isNotBlank(paramMap.get("id"))) {
			PlatBank platBank = platBankService.queryByKey(Integer.parseInt(paramMap.get("id")));
			model.addAttribute("platBank", platBank);
			model.addAttribute("op", "edit");
		} else {
			model.addAttribute("op", "add");
		}

		return "modules/platform/platBankEdit";
	}

	/**
	 * 平台银行新增
	 */
	@RequestMapping(value = {"addPlatBank"})
	public String addChanMchPayType(HttpServletRequest request, HttpServletResponse response, Model model,
									@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result = 0;
		String message, messageType;
		try {

			PlatBank searchInfo = new PlatBank();
			searchInfo.setBankName(paramMap.get("bankName"));
			searchInfo.setBankCode(paramMap.get("bankCode"));
			searchInfo.setCardType(paramMap.get("cardType"));
			searchInfo.setStatus(paramMap.get("status"));
			searchInfo.setExtend(paramMap.get("extend"));
			searchInfo.setCreateDate(new Date());

			if ("add".equals(paramMap.get("op"))) {

				//校验重复
				PlatBank searchCMP = new PlatBank();
				searchCMP.setBankCode(searchInfo.getBankCode());
				List<PlatBank> platCardBins = platBankService.list(searchCMP);

				PlatBank platBankSearch = new PlatBank();
				platBankSearch.setBankCode(searchInfo.getBankCode());
				List<PlatBank> platBanks = platBankService.list(platBankSearch);
				if (CollectionUtils.isNotEmpty(platBanks)){
					searchInfo.setBankName(platBanks.get(0).getBankName());
				}

				if (CollectionUtils.isEmpty(platCardBins)) {
					result = platBankService.create(searchInfo);
				} else {
					result = 99;
				}

			} else if ("edit".equals(paramMap.get("op"))) {
				searchInfo.setId(Integer.parseInt(paramMap.get("id")));
				PlatBank platCardBin = platBankService.queryByKey(searchInfo.getId());
				platCardBin.setBankName(searchInfo.getBankName());
				platCardBin.setExtend(searchInfo.getExtend());
				platCardBin.setStatus(searchInfo.getStatus());
				searchInfo.setUpdateDate(new Date());
				result = platBankService.saveByKey(searchInfo);
			}

			if (result == 1) {
				message = "保存成功";
				messageType = "success";
			} else if (result == 99) {
				message = "银行代码重复";
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
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platBankList";
	}

//	/**
//	 * 删除
//	 */
//	@RequestMapping(value = {"deletePlatBank"})
//	public String deleteChanPaytypeBank(HttpServletRequest request, HttpServletResponse response, Model model,
//									   @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
//
//
//		String message, messageType;
//
//		int result = 0;
//		try {
////			result = platBankService.delete(Integer.parseInt(paramMap.get("id")));
//		} catch (Exception e) {
//			logger.info("删除失败", e);
//		}
//
//		if (result == 1) {
//				message = "删除成功";
//				messageType = "success";
//			} else {
//				message = "删除失败";
//				messageType = "error";
//			}
//
//		redirectAttributes.addFlashAttribute("messageType", messageType);
//		redirectAttributes.addFlashAttribute("message", message);
//		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platBankList";
//	}
}
