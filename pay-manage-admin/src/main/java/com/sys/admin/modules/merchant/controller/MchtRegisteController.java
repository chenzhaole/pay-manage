package com.sys.admin.modules.merchant.controller;

import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.merchant.bo.MchtRegisteForm;
import com.sys.admin.modules.platform.bo.MchtProductFormInfo;
import com.sys.admin.modules.platform.service.MchtProductAdminService;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.registe.MchtRegisteRequestBody;
import com.sys.boss.api.entry.trade.request.registe.TradeMchtRegisteRequest;
import com.sys.boss.api.service.trade.handler.ITradeMchtRegiste4ExistingMchtHandler;
import com.sys.boss.api.service.trade.handler.ITradeTxQuickPayHandler;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.ChanMchtPaytype;
import com.sys.core.dao.dmo.MchtBankCard;
import com.sys.core.dao.dmo.MchtChanRegiste;
import com.sys.core.dao.dmo.MchtChanRegisteOrder;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.MchtProduct;
import com.sys.core.dao.dmo.PlatBank;
import com.sys.core.dao.dmo.PlatFeerate;
import com.sys.core.dao.dmo.PlatProduct;
import com.sys.core.service.ChanMchtPaytypeService;
import com.sys.core.service.ChannelService;
import com.sys.core.service.MchtBankCardService;
import com.sys.core.service.MchtChanRegisteOrderService;
import com.sys.core.service.MchtChanRegisteService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatBankService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

	@Autowired
	private MchtBankCardService mchtBankCardService;

	@Autowired
	private PlatBankService platBankService;

	@Autowired
	private ProductService productService;

	@Autowired
	private MchtProductAdminService mchtProductAdminService;

	@Autowired
	private PlatFeerateService platFeerateService;

	@Autowired
	private ITradeMchtRegiste4ExistingMchtHandler tradeMchtRegiste4ExistingMchtHandler;

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
	@ResponseBody
	public String reRegiste(HttpServletRequest request, HttpServletResponse response, Model model,
							@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		CommonResult result = new CommonResult();
		result.setRespMsg("执行失败");

		//要补录的记录Id
		String mchtChanRegisteId = paramMap.get("mchtChanRegisteId");

		//要补录的通道商户支付方式Id
		String chanMchtPaytypeId = paramMap.get("chanMchtPaytypeId");

		//要补录的卡类型
		String cardType = paramMap.get("cardType");

		// 根据卡类型、卡号筛选最新的入驻记录
		List<MchtChanRegiste> mchtInfoList;

		if (StringUtils.isBlank(mchtChanRegisteId)){
			mchtInfoList = new ArrayList<>();
			mchtInfoList.add(mchtChanRegisteService.queryByKey(mchtChanRegisteId));
		}else {
			mchtInfoList = mchtChanRegisteService.reRegisteList(cardType);
		}

		if (Collections3.isEmpty(mchtInfoList)){
			result.setRespMsg("执行失败");
		}

		// 用户银行卡
		List<MchtBankCard> mchtBankCards = mchtBankCardService.list(new MchtBankCard());
		Map<String, MchtBankCard> mchtBankCardMap = Collections3.extractToMap(mchtBankCards, "id");

		//查询商户列表
		List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
		Map<String, MchtInfo> mchtMap = Collections3.extractToMap(mchtList, "id");

		//查询平台银行列表
		List<PlatBank> platBanks = platBankService.list(new PlatBank());
		Map<String, String> platBankMap = Collections3.extractToMap(platBanks, "bankCode", "bankName");


		TradeMchtRegisteRequest registeRequest;
		TradeReqHead head;
		MchtRegisteRequestBody registeRequestBody;
		MchtBankCard mchtBankCard;
		MchtInfo mchtInfo;
		MchtProductFormInfo mchtProductFormInfo = new MchtProductFormInfo();
		List<MchtProduct> mchtProducts;
		PlatFeerate platFeerate;

		// 遍历入驻新通道
		for (MchtChanRegiste chanRegiste : mchtInfoList) {

			//排除已入驻该通道的记录
			if (mchtBankCardMap.get(chanRegiste.getMchtCode()) == null ||
					mchtMap.get(chanRegiste.getMchtCode()) == null ||
					chanMchtPaytypeId.equals(chanRegiste.getChanMchtPaytypeId())) {
				continue;
			}
			registeRequest = new TradeMchtRegisteRequest();

			mchtBankCard = mchtBankCardMap.get(chanRegiste.getMchtCode());
			mchtInfo = mchtMap.get(chanRegiste.getMchtCode());

			mchtProductFormInfo.setMchtId(chanRegiste.getMchtCode());
			mchtProducts = mchtProductAdminService.getProductListByMchtId(mchtProductFormInfo);

			// 根据mchtId查询Product信息
			PlatProduct product = null;
			for (MchtProduct mprod : mchtProducts) {
				product = productService.queryByKey(mprod.getProductId());
				if (PayTypeEnum.QUICK_TX.getCode().equals(product.getPayType())) {
					break;
				}
			}

			if (mchtBankCard == null || mchtInfo == null || product == null) {
				continue;
			}

			//查找最新费率
			platFeerate = platFeerateService.getLastFee(FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getCode(),
					chanRegiste.getMchtCode() + "&" + product.getId());

			head = new TradeReqHead();
			head.setMchtId(ConfigUtil.getValue("ykzlMchtId"));
			head.setVersion("20");
			head.setBiz("11");
			registeRequest.setHead(head);

			registeRequestBody = new MchtRegisteRequestBody();
			registeRequestBody.setOrderId("RE" + chanRegiste.getId());
			registeRequestBody.setName(mchtInfo.getName());
			registeRequestBody.setAddress(mchtInfo.getCompanyAdr());
			registeRequestBody.setMchtType(mchtInfo.getMchtType());
			registeRequestBody.setLegalName(mchtBankCard.getBankAccountName());
			registeRequestBody.setLegalCertType(mchtBankCard.getCertType());
			registeRequestBody.setLegalCertNo(mchtBankCard.getCertNo());
			registeRequestBody.setSettleBankNo(mchtBankCard.getPlatBankCode());
			registeRequestBody.setSettleCardType(mchtBankCard.getBankCardType());
			registeRequestBody.setSettleCardCvv(mchtBankCard.getCreditCvv());
			registeRequestBody.setSettleCardExpDate(mchtBankCard.getCreditExpDate());
			registeRequestBody.setBankAccountMobile(mchtBankCard.getBankAccountMobile());
			registeRequestBody.setSettleBankAccountNo(mchtBankCard.getBankCardNo());
			registeRequestBody.setSettleAccountName(mchtBankCard.getBankAccountName());
			registeRequestBody.setSettleBankName(platBankMap.get(mchtBankCard.getPlatBankCode()));
			registeRequestBody.setSettleBankAcctType(mchtBankCard.getAccountType());

			registeRequestBody.setBankRateType(platFeerate.getFeeType());
			registeRequestBody.setBankSettleCycle(platFeerate.getSettleCycle());
			registeRequestBody.setBankDmType("1"); //有无积分
			registeRequestBody.setBankRate(platFeerate.getFeeRate().doubleValue() + "");
			registeRequestBody.setBankFee(platFeerate.getFeeAmount().doubleValue() + "");

			registeRequestBody.setOpType("1");//1-申请；2-变更
			registeRequestBody.setUserId(chanRegiste.getMchtCode());

			registeRequest.setBody(registeRequestBody);

			result = tradeMchtRegiste4ExistingMchtHandler.process(registeRequest, "admin后台");
		}

		return result.getRespMsg();
	}

	@RequestMapping(value = {"registeIndex"})
	public String registerIndex(Model model) {
		initListPage(model);
		return "modules/merchant/mchtRegisteList";
	}

	@RequestMapping(value = {"registeOrderIndex"})
	public String registeOrderIndex(Model model) {
		initListPage(model);
		return "modules/merchant/mchtRegisteOrderList";
	}

	private void initListPage(Model model) {
		//查询商户列表
		List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
		//通道商户支付方式列表
		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

		model.addAttribute("chanInfoList", chanInfoList);
		model.addAttribute("mchtInfos", mchtList);
		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);
		model.addAttribute("paramMap", new HashMap<String, String>());
		model.addAttribute("page", new Page(1, new PageInfo().getPageSize(), 0, true));
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
	public String mchtRegisteOrderDetail(String id, Model model) {
		MchtChanRegisteOrder registeOrder = mchtChanRegisteOrderService.queryByKey(id);
		ChanMchtPaytype chanMchtPaytype = chanMchtPaytypeService.queryByKey(registeOrder.getChanMchtPaytypeId());
		model.addAttribute("registeOrder", registeOrder);
		model.addAttribute("chanMchtPayType", chanMchtPaytype != null ? chanMchtPaytype.getName() : "");
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
