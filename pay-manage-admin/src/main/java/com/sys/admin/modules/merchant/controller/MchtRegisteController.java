package com.sys.admin.modules.merchant.controller;

import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.merchant.bo.MchtRegisteForm;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.Collections3;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/merchant")
public class MchtRegisteController extends BaseController {

	@Autowired
	private MchtChanRegisteService mchtChanRegisteService;

	@Autowired
	private MchtChanRegisteOrderService mchtChanRegisteOrderService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private ChanMchtPaytypeService chanMchtPaytypeService;

	/**
	 * 通道补录 选择通道商户支付方式
	 */
	@RequestMapping(value = {"reRegistePage"})
	public String reRegistePage(Model model) {
		//通道商户支付方式列表
		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);
		return "modules/merchant/RegisteSelectChan";
	}

	/**
	 * 通道补录
	 */
	@RequestMapping(value = {"reRegiste", ""})
	public String reRegiste(HttpServletRequest request, HttpServletResponse response, Model model,
								  @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		//要补录的通道商户支付方式Id
		String chanId = paramMap.get("chanMchtPaytypeId");
		//要补录的商户code
		List<String> mchtCodes = new ArrayList<>();

		MchtChanRegiste mchtChanRegiste = new MchtChanRegiste();
		mchtChanRegiste.setStatus(StatusEnum.VALID.getCode());
		List<MchtChanRegiste> mchtInfoList = mchtChanRegisteService.list(mchtChanRegiste);

		for (MchtChanRegiste chanRegiste : mchtInfoList) {
			mchtCodes.add(chanRegiste.getMchtCode());
		}

		return "";
	}

	@RequestMapping(value = {"registeIndex"})
	public String registerIndex(Model model){
		initListPage(model);
		return "modules/merchant/mchtRegisteList";
	}

	@RequestMapping(value = {"registeOrderIndex"})
	public String registeOrderIndex(Model model){
		initListPage(model);
		return "modules/merchant/mchtRegisteOrderList";
	}

	private void initListPage(Model model){
		//查询商户列表
		List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
		//通道商户支付方式列表
		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

		model.addAttribute("chanInfoList", chanInfoList);
		model.addAttribute("mchtInfos", mchtList);
		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);
		model.addAttribute("paramMap", new HashMap<String,String>());
		model.addAttribute("page",new Page(1,new PageInfo().getPageSize(),0,true));
	}


	/**
	 * 商户入驻列表
	 */
	@RequestMapping(value = {"registeList"})
	public String registeList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		MchtChanRegiste mchtChanRegiste = new MchtChanRegiste();
		mchtChanRegiste.setMchtCode(paramMap.get("mchtCode"));
		mchtChanRegiste.setChanMchtPaytypeId(paramMap.get("chanMchtPaytypeId"));
		mchtChanRegiste.setStatus(paramMap.get("status"));

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		mchtChanRegiste.setPageInfo(pageInfo);

		List<MchtChanRegiste> mchtInfoList = mchtChanRegisteService.list(mchtChanRegiste);

		//查询商户列表
		List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
		//通道商户支付方式列表
		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

		model.addAttribute("chanInfoList", chanInfoList);
		model.addAttribute("mchtInfos", mchtList);
		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);


		int mchtCount = mchtChanRegisteService.count(mchtChanRegiste);

		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtList, "id", "name");
		Map<String, String> mchtMobileMap = Collections3.extractToMap(mchtList, "id", "mobile");
		Map<String, String> chanMchtPaytypeMap = Collections3.extractToMap(chanMchtPaytypeList, "id", "chanId");
		Map<String, String> paytypeMap = Collections3.extractToMap(chanMchtPaytypeList, "id", "payType");


		List<MchtRegisteForm> mchtRegisteForms = new ArrayList<>();
		MchtRegisteForm mchtRegisteForm;
		for (MchtChanRegiste chanRegiste : mchtInfoList) {
			mchtRegisteForm = new MchtRegisteForm();
			BeanUtils.copyProperties(chanRegiste, mchtRegisteForm);
			mchtRegisteForm.setMchtName(mchtMap.get(chanRegiste.getMchtCode()));
			mchtRegisteForm.setPhone(mchtMobileMap.get(chanRegiste.getMchtCode()));
			mchtRegisteForm.setChanName(channelMap.get(chanMchtPaytypeMap.get(mchtRegisteForm.getChanMchtPaytypeId())));
			mchtRegisteForm.setPaytype(paytypeMap.get(mchtRegisteForm.getChanMchtPaytypeId()));
			mchtRegisteForms.add(mchtRegisteForm);
		}

		Page page = new Page(pageNo, pageInfo.getPageSize(), mchtCount, mchtRegisteForms, true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/merchant/mchtRegisteList";
	}


	/**
	 * 商户入驻流水
	 */
	@RequestMapping(value = {"registeOrderList"})
	public String registeOrderList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		MchtChanRegisteOrder mchtChanRegiste = new MchtChanRegisteOrder();
		mchtChanRegiste.setMchtCode(paramMap.get("mchtCode"));
		mchtChanRegiste.setChanMchtPaytypeId(paramMap.get("chanMchtPaytypeId"));
		mchtChanRegiste.setStatus(paramMap.get("status"));
		mchtChanRegiste.setMchtOrderId(paramMap.get("mchtOrderId"));
		mchtChanRegiste.setPlatOrderId(paramMap.get("platOrderId"));
		mchtChanRegiste.setChanOrderId(paramMap.get("chanOrderId"));

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		mchtChanRegiste.setPageInfo(pageInfo);

		List<MchtChanRegisteOrder> mchtInfoList = mchtChanRegisteOrderService.list(mchtChanRegiste);

		//通道商户支付方式列表
		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
		int mchtCount = mchtChanRegisteOrderService.count(mchtChanRegiste);

		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> chanMchtPaytypeMap = Collections3.extractToMap(chanMchtPaytypeList, "id", "chanId");

		List<MchtRegisteForm> mchtRegisteForms = new ArrayList<>();
		MchtRegisteForm mchtRegisteForm;
		for (MchtChanRegisteOrder chanRegiste : mchtInfoList) {
			mchtRegisteForm = new MchtRegisteForm();
			BeanUtils.copyProperties(chanRegiste, mchtRegisteForm);
			mchtRegisteForm.setChanName(channelMap.get(chanMchtPaytypeMap.get(mchtRegisteForm.getChanMchtPaytypeId())));
			mchtRegisteForm.setMchtOrderId(chanRegiste.getMchtOrderId());
			mchtRegisteForm.setPlatOrderId(chanRegiste.getPlatOrderId());
			mchtRegisteForm.setChanOrderId(chanRegiste.getChanOrderId());
			mchtRegisteForm.setChan2PlatResCode(chanRegiste.getChan2platResCode());
			mchtRegisteForm.setChan2PlatResMsg(chanRegiste.getChan2platResMsg());
			mchtRegisteForms.add(mchtRegisteForm);
		}

		Page page = new Page(pageNo, pageInfo.getPageSize(), mchtCount, mchtRegisteForms, true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/merchant/mchtRegisteOrderList";
	}

	/**
	 * 商户入驻流水详情页
	 */
	@RequestMapping("mchtRegisteOrderDetail")
	public String mchtRegisteOrderDetail(String id,Model model){
		MchtChanRegisteOrder registeOrder = mchtChanRegisteOrderService.queryByKey(id);
		ChanMchtPaytype chanMchtPaytype = chanMchtPaytypeService.queryByKey(registeOrder.getChanMchtPaytypeId());
		model.addAttribute("registeOrder",registeOrder);
		model.addAttribute("chanMchtPayType",chanMchtPaytype!=null?chanMchtPaytype.getName():"");
		return "modules/merchant/mchtRegisteOrderDetail";
	}


	/**
	 * 商户删除
	 */
	@RequestMapping(value = {"deleteRegiste"})
	public String deleteMcht(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

//		result = merchantAdminService.deleteMerchantById(paramMap.get("id"));
//		if (result == 1) {
//			message = "删除成功";
//			messageType = "success";
//		}
//
//		redirectAttributes.addFlashAttribute("messageType", messageType);
//		redirectAttributes.addFlashAttribute("message", message);
//		response.setCharacterEncoding("UTF-8");
//		return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/list";
		return null;
	}
}
