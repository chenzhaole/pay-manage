package com.sys.admin.modules.platform.controller;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.enums.AdminPayTypeEnum;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.channel.service.ChannelAdminService;
import com.sys.admin.modules.merchant.bo.MerchantForm;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.admin.modules.platform.bo.MchtChanFormInfo;
import com.sys.admin.modules.platform.bo.MchtProductFormInfo;
import com.sys.admin.modules.platform.bo.ProductFormInfo;
import com.sys.admin.modules.platform.bo.SubProduct;
import com.sys.admin.modules.platform.service.MchtChanAdminService;
import com.sys.admin.modules.platform.service.MchtProductAdminService;
import com.sys.admin.modules.platform.service.ProductAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanMchtPaytype;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.MchtProduct;
import com.sys.core.dao.dmo.PlatSdkConfig;
import com.sys.core.service.ChanMchtPaytypeService;
import com.sys.core.service.PlatSDKService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.sys.core.service.ConfigSysService;

/**
 * @ClassName: PlatformController
 * @Description: 平台配置controller
 * @author: cheng_fei
 * @date: 2017年8月15日 下午5:59:25
 */
@Controller
@RequestMapping(value = "${adminPath}/platform")
public class PlatformController extends BaseController {

	@Autowired
	PlatSDKService platSDKService;

	@Autowired
	ProductAdminService productAdminService;

	@Autowired
	MchtProductAdminService mchtProductAdminService;

	@Autowired
	ChanMchtAdminService chanMchtAdminService;

	@Autowired
	ChannelAdminService channelAdminService;

	@Autowired
	MerchantAdminService merchantAdminService;

	@Autowired
	MchtChanAdminService mchtChanAdminService;

//	@Autowired
//	ConfigSysService configSysService;

	@Autowired
	ChanMchtPaytypeService chanMchtPaytypeService;

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @return
	 * @Title: platProductList
	 * @Description: 支付产品列表
	 * @return: String
	 */
	@RequestMapping(value = {"platProductList", ""})
	public String platProductList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {

		ProductFormInfo productFormInfo = new ProductFormInfo(paramMap);
		productFormInfo.setId("");

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		productFormInfo.setPageInfo(pageInfo);

		List<ProductFormInfo> productInfos = productAdminService.getProductList(productFormInfo);

		int chanCount = productAdminService.productCount(productFormInfo);
		Page page = new Page(pageNo, pageInfo.getPageSize(), chanCount, productInfos, true);
		model.addAttribute("page", page);

		//所有支付方式
		AdminPayTypeEnum[] payTypeList = AdminPayTypeEnum.values();
		model.addAttribute("paymentTypeInfos", payTypeList);

		//所有通道商户支付方式
		List<ChanMchtPaytype> chanMchtPaytypes = chanMchtPaytypeService.list(new ChanMchtPaytype());
		model.addAttribute("chanMchtPaytypes", chanMchtPaytypes);

		if (StringUtils.isNotBlank(paramMap.get("messageType"))) {
			model.addAttribute("message", paramMap.get("message"));
			model.addAttribute("messageType", paramMap.get("messageType"));
		}
		response.setCharacterEncoding("UTF-8");
		model.addAttribute("paramMap", paramMap);
		return "modules/platform/platProductList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @Title: addPlatProduct
	 * @Description: 支付产品新增或者修改
	 * @return: String
	 */
	@RequestMapping(value = {"addPlatProductPage", ""})
	public String addPlatProductPage(HttpServletRequest request, HttpServletResponse response, Model model,
									 @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		ProductFormInfo productFormInfo = new ProductFormInfo(paramMap);
		ProductFormInfo productFormInfoQuery = null;
		String payType = "";
		if (paramMap.get("op") != null && "add".equals(paramMap.get("op"))) {
			model.addAttribute("op", "add");
			payType = paramMap.get("paymentType");
		} else if (!StringUtils.isBlank(productFormInfo.getId())) {
			productFormInfoQuery = productAdminService.getPlatProductById(productFormInfo.getId());

			model.addAttribute("op", "edit");
			payType = productFormInfoQuery.getPayType();
		}

		List<ChanMchtFormInfo> chanInfoList = chanMchtAdminService.getChannelListSimple(new ChanMchtFormInfo());
		List<ChanMchtFormInfo> chanMchtFormInfos = new ArrayList<>();
		List<ProductFormInfo> productInfos = new ArrayList<>();
		boolean subPro = false;
		//平台收银台产品页面配置子产品
		if (PayTypeEnum.CASHIER_PLAT.getCode().equals(payType)) {
			subPro = true;
			//子产品不可选收银台和组合产品
			List<ProductFormInfo> productTemps = productAdminService.getProductList(new ProductFormInfo());
			if (!CollectionUtils.isEmpty(productTemps)) {
				for (ProductFormInfo productTemp : productTemps) {
					if (PayTypeEnum.CASHIER_PLAT.getCode().equals(productTemp.getPayType())) {
						continue;
					}
					productInfos.add(productTemp);
				}
			}
			//读取子产品
			if (productFormInfoQuery != null && !StringUtils.isBlank(productFormInfoQuery.getSubId())) {
				getSubProducts(productFormInfoQuery);
			}

			//组合支付方式配置
		} else if (payType.endsWith("000")) {
			subPro = true;
			String payChan = payType.split("000")[0];

			//子产品不可选收银台和组合产品，只可选组合产品相关的产品
			List<ProductFormInfo> productTemps = productAdminService.getProductList(new ProductFormInfo());
			if (!CollectionUtils.isEmpty(productTemps)) {
				for (ProductFormInfo productTemp : productTemps) {
					if (PayTypeEnum.CASHIER_PLAT.getCode().equals(productTemp.getPayType()) ||
							productTemp.getPayType().endsWith("000")) {
						continue;
					}
					if (productTemp.getPayType().startsWith(payChan)){
						productInfos.add(productTemp);
					}
				}
			}
			//读取子产品
			if (productFormInfoQuery != null && !StringUtils.isBlank(productFormInfoQuery.getSubId())) {
				getSubProducts(productFormInfoQuery);
			}

		} else {
			chanMchtFormInfos = new ArrayList<>();
			for (ChanMchtFormInfo chanMchtFormInfo : chanInfoList) {
				if (chanMchtFormInfo.getPayType().equals(payType)) {
					chanMchtFormInfos.add(chanMchtFormInfo);
				}
			}

			if (CollectionUtils.isEmpty(chanMchtFormInfos)) {
				redirectAttributes.addFlashAttribute("messageType", "error");
				redirectAttributes.addFlashAttribute("message", "该支付方式未配置");
				response.setCharacterEncoding("UTF-8");
				return "redirect:" + GlobalConfig.getAdminPath() + "/platform/getPayType";
			}
		}

		model.addAttribute("productInfo", productFormInfoQuery);
		model.addAttribute("chanInfoList", chanMchtFormInfos);
		model.addAttribute("subProductLists", productInfos);
		model.addAttribute("paymentType", payType);
		model.addAttribute("subPro", subPro);

		return "modules/platform/platProductEdit";

	}

	private void getSubProducts(ProductFormInfo productFormInfo){
		List<SubProduct> subProducts = new ArrayList<>();
		String[] subId = productFormInfo.getSubId().split(",");
		SubProduct subProduct;
		for (int i = 0; i < subId.length; i++) {
			subProduct = new SubProduct();
			subProduct.setSubProductId(subId[i]);
			subProduct.setSort(i + 1);
			subProducts.add(subProduct);
		}
		productFormInfo.setSubProducts(subProducts);
	}

	/**
	 * @Title: getPayType
	 * @Description: 获取支付方式
	 * @return: String
	 */
	@RequestMapping(value = {"getPayType", ""})
	public String getPayType(Model model) {
		//支付方式
		AdminPayTypeEnum[] payTypeList = AdminPayTypeEnum.values();
		model.addAttribute("paymentTypeInfos", payTypeList);
		return "modules/platform/platProductSelectPayType";
	}


	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @Title: addPlatProduct
	 * @Description: 支付产品新增或者修改
	 * @return: String
	 */
	@RequestMapping(value = {"addPlatProduct", ""})
	public String addPlatProduct(HttpServletRequest request, HttpServletResponse response, Model model,
								 @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result = 0;

		String message;
		String messageType;
		try {
			ProductFormInfo productFormInfo = new ProductFormInfo(paramMap);
			if (!CollectionUtils.isEmpty(productFormInfo.getProductRelas()) ||
					!CollectionUtils.isEmpty(productFormInfo.getSubProducts())) {

				if (StringUtils.isBlank(productFormInfo.getCode())) {
					productFormInfo.setCode(IdUtil.createCode());
				}

				if ("add".equals(paramMap.get("op"))) {
					result = productAdminService.addPlatProduct(productFormInfo);
				}

				if ("edit".equals(paramMap.get("op"))) {
					result = productAdminService.updatePlatProduct(productFormInfo);

					//刷新商户通道
					MchtProductFormInfo mchtProductFormInfo = new MchtProductFormInfo();
					mchtProductFormInfo.setProductId(productFormInfo.getId());
					List<MchtProductFormInfo> mchtProductFormInfos = mchtProductAdminService.getProductList(mchtProductFormInfo);

					if (!CollectionUtils.isEmpty(mchtProductFormInfos)) {

						List<String> mchtIds = new ArrayList<>();
						for (MchtProductFormInfo formInfo : mchtProductFormInfos) {
							mchtIds.add(formInfo.getMchtId());
						}
						mchtChanAdminService.refresh(mchtIds);
					}
				}
			}
			if (result == 1) {
				message = "保存成功";
				messageType = "success";
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
		response.setCharacterEncoding("UTF-8");
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platProductList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @Description: 支付产品新增或者修改
	 * @return: String
	 */
	@RequestMapping(value = {"deletePlatProduct", ""})
	public String deletePlatProduct(HttpServletRequest request, HttpServletResponse response, Model model,
									@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result;

		ProductFormInfo productFormInfo = new ProductFormInfo(paramMap);

		MchtProductFormInfo mchtProductFormInfo = new MchtProductFormInfo();
		mchtProductFormInfo.setProductId(productFormInfo.getId());
		mchtProductFormInfo.setProductCode(productFormInfo.getCode());
		List<MchtProductFormInfo> mchtProductFormInfos = mchtProductAdminService.getProductList(mchtProductFormInfo);

		if (CollectionUtils.isEmpty(mchtProductFormInfos)) {
			result = productAdminService.deletePlatProduct(productFormInfo);
		} else {
			result = 99;
		}

		String message, messageType;
		if (result == 1) {
			message = "删除成功";
			messageType = "success";
		} else if (result == 99) {
			message = "该产品已配置商户产品，无法删除";
			messageType = "error";
		} else {
			message = "删除失败";
			messageType = "error";
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		response.setCharacterEncoding("UTF-8");
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platProductList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @Title: platConfMchtChanList
	 * @Description: 商户通道列表
	 * @return: String
	 */
	@RequestMapping(value = {"platConfMchtChanList", ""})
	public String platConfMchtChanList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {

		MchtChanFormInfo mchtChanFormInfo = new MchtChanFormInfo(paramMap);

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		mchtChanFormInfo.setPageInfo(pageInfo);

		List<MchtChanFormInfo> mchtChanFormInfos = mchtChanAdminService.getMchtList(mchtChanFormInfo);
//		model.addAttribute("mchtChans", mchtChanFormInfos);
//		int orderCount = mchtChanAdminService.mchtChanCount(mchtChanFormInfo);

		Page page;
		if (mchtChanFormInfos != null && mchtChanFormInfos.size() != 0) {
			page = new Page(pageNo, mchtChanFormInfos.size(), mchtChanFormInfos.size(), mchtChanFormInfos, true);
		} else {
			page = new Page();
		}

		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/platform/platConfMchtChanList";
	}

	/**
	 * @Title: editPlatConfMchtChan
	 * @Description: 刷新商户通道
	 * @return: String
	 */
	@RequestMapping(value = {"refreshMchtChan", ""})
	public String refreshMchtChan(HttpServletRequest request, HttpServletResponse response, Model model,
								  @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		Integer result;
		result = mchtChanAdminService.refresh(new ArrayList<String>());

		String message;
		if (result == 1) {
			message = "刷新成功";
		} else {
			message = "刷新失败";
		}
		redirectAttributes.addFlashAttribute("messageType", message);
		redirectAttributes.addFlashAttribute("message", message);
		response.setCharacterEncoding("UTF-8");
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platConfMchtChanList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @return
	 * @Title: editPlatConfMchtChan
	 * @Description: 商户通道编辑
	 * @return: String
	 */
	@RequestMapping(value = {"editPlatConfMchtChanPage", ""})
	public String editPlatConfMchtChanPage(HttpServletRequest request, HttpServletResponse response, Model model,
										   @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		MchtChanFormInfo mchtChanFormInfo = new MchtChanFormInfo(paramMap);
		MchtChanFormInfo mchtChanFormInfos = mchtChanAdminService.getChanByMcht(mchtChanFormInfo);

		model.addAttribute("mchtChanFormInfos", mchtChanFormInfos);
		model.addAttribute("chanInfos", mchtChanFormInfos.getChannels());
		model.addAttribute("op", "edit");
		return "modules/platform/platConfMchtChanEdit";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @return
	 * @Title: editPlatConfMchtChan
	 * @Description: 商户通道编辑
	 * @return: String
	 */
	@RequestMapping(value = {"editPlatConfMchtChan", ""})
	public String editPlatConfMchtChan(HttpServletRequest request, HttpServletResponse response, Model model,
									   @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		Integer result = 0;
		MchtChanFormInfo mchtChanFormInfo = new MchtChanFormInfo(paramMap);
		if (!CollectionUtils.isEmpty(mchtChanFormInfo.getChannels())) {
			result = mchtChanAdminService.updateStatus(mchtChanFormInfo);
		}

		String message = "已经保存" + result + "条";
		redirectAttributes.addFlashAttribute("messageType", "success");
		redirectAttributes.addFlashAttribute("message", message);
		response.setCharacterEncoding("UTF-8");
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platConfMchtChanList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @return
	 * @Title: platConfMchtProductList
	 * @Description: 商户产品List
	 * @return: String
	 */
	@RequestMapping(value = {"platConfMchtProductList", ""})
	public String platConfMchtProductList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {

		MchtProductFormInfo productFormInfo = new MchtProductFormInfo(paramMap);

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		productFormInfo.setPageInfo(pageInfo);

		//所有产品
		List<ProductFormInfo> productFormInfos = productAdminService.getProductList(new ProductFormInfo());
		Map<String, String> productPaytypeMap = Collections3.extractToMap(productFormInfos, "id", "payType");

		List<MchtProductFormInfo> mchtProductFormInfos = mchtProductAdminService.getProductList(productFormInfo);
		if(!CollectionUtils.isEmpty(mchtProductFormInfos)){
			for (MchtProductFormInfo mchtProductFormInfo : mchtProductFormInfos) {
				mchtProductFormInfo.setPayType(productPaytypeMap.get(mchtProductFormInfo.getProductId()));
			}
		}

		int orderCount = mchtProductAdminService.mchtProductCount(productFormInfo);

		Page page;
		if (!CollectionUtils.isEmpty(mchtProductFormInfos)) {
			page = new Page(pageNo, pageInfo.getPageSize(), orderCount, mchtProductFormInfos, true);
		} else {
			page = new Page();
		}

		model.addAttribute("page", page);

		//所有可配商户
		List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(new MchtInfo());
		List<MerchantForm> mchtInfosResult = new ArrayList<>();
		if(!CollectionUtils.isEmpty(mchtInfos)){
			for (MerchantForm mchtInfo : mchtInfos) {
				if (StringUtils.isBlank(mchtInfo.getSignType())) {
					continue;
				}
				if (!mchtInfo.getSignType().contains(SignTypeEnum.SINGLE_MCHT.getCode())) {
					if (mchtInfo.getSignType().contains(SignTypeEnum.COMMON_MCHT.getCode())
							|| mchtInfo.getSignType().contains(SignTypeEnum.CLIENT_MCHT.getCode())) {
						mchtInfosResult.add(mchtInfo);
					}
				}
			}

		}

		//根据名称排序
		Collections3.sortByName(mchtInfosResult, "name");
		model.addAttribute("mchtInfos", mchtInfosResult);

		model.addAttribute("productInfos", productFormInfos);


		model.addAttribute("paramMap", paramMap);
		return "modules/platform/platConfMchtProductList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @return
	 * @Title: addPlatConfMchtProductPage
	 * @Description: 商户产品新增/编辑的页面
	 * @return: String
	 */
	@RequestMapping(value = {"addPlatConfMchtProductPage", ""})
	public String addPlatConfMchtProductPage(HttpServletRequest request, HttpServletResponse response, Model model,
											 @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		MchtProductFormInfo productFormInfo = new MchtProductFormInfo(paramMap);
		if (StringUtils.isNotBlank(productFormInfo.getMchtId())) {
			MchtProductFormInfo mchtProductById = mchtProductAdminService.getMchtProductById(productFormInfo);
			model.addAttribute("productInfo", mchtProductById);
		}

		List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(new MchtInfo());
		List<MerchantForm> mchtInfosResult = new ArrayList<>();
		for (MerchantForm mchtInfo : mchtInfos) {
			if (StringUtils.isBlank(mchtInfo.getSignType())) {
				continue;
			}
			if (!mchtInfo.getSignType().contains(SignTypeEnum.SINGLE_MCHT.getCode())) {
				if (mchtInfo.getSignType().contains(SignTypeEnum.COMMON_MCHT.getCode())
						|| mchtInfo.getSignType().contains(SignTypeEnum.CLIENT_MCHT.getCode())) {
					mchtInfosResult.add(mchtInfo);
				}
			}
		}
		//根据名称排序
		Collections3.sortByName(mchtInfosResult, "name");
		model.addAttribute("mchtInfos", mchtInfosResult);

//		List<PaymentTypeInfo> paymentTypeInfos = configSysService.listAllPaymentTypeInfo();
		List paymentTypeInfos = new ArrayList<>();
		model.addAttribute("paymentTypeInfos", paymentTypeInfos);

		List<ProductFormInfo> productFormInfos = productAdminService.getProductList(new ProductFormInfo());
		model.addAttribute("productFormInfos", productFormInfos);

		if (StringUtils.isNotBlank(productFormInfo.getMchtId()) && StringUtils.isNotBlank(productFormInfo.getProductId())) {
			model.addAttribute("op", "edit");
		} else {
			model.addAttribute("op", "add");
		}

		return "modules/platform/platConfMchtProductEdit";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @return
	 * @Title: addPlatConfMchtProduct
	 * @Description: 商户产品新增/编辑
	 * @return: String
	 */
	@RequestMapping(value = {"addPlatConfMchtProduct", ""})
	public String addPlatConfMchtProduct(HttpServletRequest request, HttpServletResponse response, Model model,
										 @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result = 0;

		String message;
		String messageType;
		try {
			MchtProductFormInfo productFormInfo = new MchtProductFormInfo(paramMap);

			if ("add".equals(paramMap.get("op"))) {

				//校验重复
				MchtProductFormInfo mchtProductById = mchtProductAdminService.getMchtProductById(productFormInfo);
				if (mchtProductById != null && !"".equals(mchtProductById.getMchtId())) {
					redirectAttributes.addFlashAttribute("messageType", "error");
					redirectAttributes.addFlashAttribute("message", "存在相同商户产品");
					response.setCharacterEncoding("UTF-8");
					return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platConfMchtProductList";
				}
				//校验同一种支付类型的支付产品，只能选择一个
				boolean isExist = this.byMchtIdAndPayType(productFormInfo);
				if (isExist) {
					redirectAttributes.addFlashAttribute("messageType", "error");
					redirectAttributes.addFlashAttribute("message", "同一种支付类型的支付产品，商户只能选择一个");
					response.setCharacterEncoding("UTF-8");
					return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platConfMchtProductList";
				}

				result = mchtProductAdminService.addMchtProduct(productFormInfo);
			}

			if ("edit".equals(paramMap.get("op"))) {
				result = mchtProductAdminService.
						updateMchtProduct(productFormInfo);
			}

			//刷新商户通道
			List<String> mchtIds = new ArrayList<String>();
			mchtIds.add(productFormInfo.getMchtId());
			mchtChanAdminService.refresh(mchtIds);

			if (result == 1) {
				message = "保存成功";
				messageType = "success";
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
		response.setCharacterEncoding("UTF-8");
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platConfMchtProductList";
	}

	/**
	 * 校验同一种支付类型的支付产品，只能选择一个
	 */
	private boolean byMchtIdAndPayType(MchtProductFormInfo productFormInfo) {
		//1. 根据支付产品id查出支付类型
		ProductFormInfo selectProductFormInfo = productAdminService.getPlatProductById(productFormInfo.getProductId());
		String payTypeCode = "";
		if (null != selectProductFormInfo) {
			payTypeCode = selectProductFormInfo.getPayType();
		}
		//2. 根据商户id查出该商户下的所有支付产品--
		List<MchtProduct> listMchtProduct = mchtProductAdminService.getProductListByMchtId(productFormInfo);
		String tempProductId = "";
		ProductFormInfo tempProductFormInfo = null;
		if (listMchtProduct != null && listMchtProduct.size() > 0) {
			for (MchtProduct info : listMchtProduct) {
				tempProductId = info.getProductId(); //商户产品下配置的平台支付产品的主键
				tempProductFormInfo = productAdminService.getPlatProductById(tempProductId);
				if (StringUtils.isNotBlank(tempProductId) && StringUtils.isNotBlank(tempProductFormInfo.getPayType()) //
						&& payTypeCode.equals(tempProductFormInfo.getPayType())) {
					System.out.println("页面提交过来的商户id为：" + productFormInfo.getMchtId() + "，支付产品为：" + productFormInfo.getProductId() + "，支付类型为：" + payTypeCode + ", 校验出已经存在该支付类型对应的支付产品，即支付产品id为：" + tempProductId);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @Title: addPlatProduct
	 * @Description: 支付产品新增或者修改
	 * @return: String
	 */
	@RequestMapping(value = {"deleteMchtProduct", ""})
	public String deleteMchtProduct(HttpServletRequest request, HttpServletResponse response, Model model,
									@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result = 0;

		MchtProductFormInfo productFormInfo = new MchtProductFormInfo(paramMap);
		result = mchtProductAdminService.deleteMchtProduct(productFormInfo);

		List<String> mchtIds = new ArrayList<String>();
		mchtIds.add(productFormInfo.getMchtId());
		result = mchtChanAdminService.refresh(mchtIds);

		String message, messageType;
		if (result == 1) {
			message = "删除成功";
			messageType = "success";
		} else {
			message = "删除失败";
			messageType = "error";
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		response.setCharacterEncoding("UTF-8");
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platConfMchtProductList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @return
	 * @Title: platSdkVersionList
	 * @Description: 支付SDK版本list
	 * @return: String
	 */
	@RequestMapping(value = {"platSdkVersionList", ""})
	public String platSdkVersionList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		PlatSdkConfig platSdkConfig = new PlatSdkConfig();
		List<PlatSdkConfig> sdkList = platSDKService.list(platSdkConfig);
		model.addAttribute("sdkList", sdkList);
		return "modules/platform/platSdkVersionList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @Title: editPlatSdkVersion
	 * @Description: 支付SDK version修改
	 * @return: String
	 */
	@RequestMapping(value = {"editPlatSdkVersion", ""})
	public String editPlatSdkVersion(HttpServletRequest request, HttpServletResponse response, Model model,
									 RedirectAttributes redirectAttributes) {
//		PayTypeEnum[] payTypeList = PayTypeEnum.values();
//		model.addAttribute("payTypeList", payTypeList);
		model.addAttribute("op", "add");
		return "modules/platform/platSdkVersionEdit";
	}

	/**
	 * @param model
	 * @return
	 * @Title: editPlatSdkVersion
	 * @Description: 编辑
	 * @return: String
	 */
	@RequestMapping(value = "form")
	public String form(PlatSdkConfig sdk, Model model) {
		PlatSdkConfig platSdkConfig = platSDKService.queryByKey(sdk.getVersion());
//		platSdkConfig.setUpdateTime(DateUtils.parseDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		model.addAttribute("op", "edit");
//		PayTypeEnum[] payTypeList = PayTypeEnum.values();
//		model.addAttribute("payTypeList", payTypeList);
		model.addAttribute("platSdkConfig", platSdkConfig);
		return "modules/platform/platSdkVersionEdit";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @return
	 * @Title: addPlatFormSdkSave
	 * @Description: 新增sdk版本
	 * @return: String
	 */
	@RequestMapping(value = {"addPlatFormSdkSave", ""})
	public String addPlatFormSdkSave(HttpServletRequest request, HttpServletResponse response, Model model,
									 @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		//根据版本号检查当前版本是否存在
		String version = request.getParameter("version");
//		PlatSdkConfig oldConfig = platSDKService.queryByKey(version);
//		if(oldConfig != null){
//			model.addAttribute("message", "当前版本已存在");
//			return "redirect:"+ GlobalConfig.getAdminPath()+"/platform/menu/";
//		}
//				
		PlatSdkConfig platSdkConfig = new PlatSdkConfig();
		//版本号
		platSdkConfig.setVersion(version);
		String updateTime = request.getParameter("updateTime");
		String createTime = request.getParameter("createTime");
		//获取当前是什么操作，新增or修改
		String op = request.getParameter("op");
		//创建时间
		if ("add".equals(op)) {
			platSdkConfig.setCreateTime(DateUtils.parseDate(updateTime, "yyyy-MM-dd HH:mm:ss"));
			platSdkConfig.setUpdateTime(DateUtils.parseDate(updateTime, "yyyy-MM-dd HH:mm:ss"));
		}
		//更新时间
		if ("edit".equals(op)) {
			platSdkConfig.setCreateTime(DateUtils.parseDate(createTime, "yyyy-MM-dd HH:mm:ss"));
			platSdkConfig.setUpdateTime(DateUtils.parseDate(updateTime, "yyyy-MM-dd HH:mm:ss"));
		}
		//是否生效
		platSdkConfig.setIsValid(request.getParameter("isValid"));
		//备注
		platSdkConfig.setDescription(request.getParameter("desc").trim());
		//更新説明
		platSdkConfig.setUpdateDesc(request.getParameter("updateDesc").trim());
		//客服tel
		platSdkConfig.setConsumerTel(request.getParameter("consumerTel").trim());
		//客服qq
		platSdkConfig.setConsumerQq(request.getParameter("consumerQq").trim());
		//是否弹出收银台页面
		platSdkConfig.setIsshowpaypage(request.getParameter("isshowpaypage"));
		//是否弹出支付结果页面
		platSdkConfig.setIsshowpayresultpage(request.getParameter("isshowpayresultpage"));
		String[] values = request.getParameterValues("paymentType");
		for (int i = 0; i < values.length; i++) {
			if (i == 0) {
				platSdkConfig.setPaymentType1(values[i]);
			} else if (i == 1) {
				platSdkConfig.setPaymentType2(values[i]);
			} else if (i == 2) {
				platSdkConfig.setPaymentType3(values[i]);
			} else if (i == 3) {
				platSdkConfig.setPaymentType4(values[i]);
			} else if (i == 4) {
				platSdkConfig.setPaymentType5(values[i]);
			} else if (i == 5) {
				platSdkConfig.setPaymentType6(values[i]);
			} else if (i == 6) {
				platSdkConfig.setPaymentType7(values[i]);
			} else if (i == 7) {
				platSdkConfig.setPaymentType8(values[i]);
			} else if (i == 8) {
				platSdkConfig.setPaymentType9(values[i]);
			} else if (i == 9) {
				platSdkConfig.setPaymentType10(values[i]);
			}
		}
		String[] paymentValues = request.getParameterValues("paymentValue");
		for (int i = 0; i < paymentValues.length; i++) {
			if (i == 0) {
				platSdkConfig.setPaymentValue1(paymentValues[i]);
			} else if (i == 1) {
				platSdkConfig.setPaymentValue2(paymentValues[i]);
			} else if (i == 2) {
				platSdkConfig.setPaymentValue3(paymentValues[i]);
			} else if (i == 3) {
				platSdkConfig.setPaymentValue4(paymentValues[i]);
			} else if (i == 4) {
				platSdkConfig.setPaymentValue5(paymentValues[i]);
			} else if (i == 5) {
				platSdkConfig.setPaymentValue6(paymentValues[i]);
			} else if (i == 6) {
				platSdkConfig.setPaymentValue7(paymentValues[i]);
			} else if (i == 7) {
				platSdkConfig.setPaymentValue8(paymentValues[i]);
			} else if (i == 8) {
				platSdkConfig.setPaymentValue9(paymentValues[i]);
			} else if (i == 9) {
				platSdkConfig.setPaymentValue10(paymentValues[i]);
			}
		}
		String[] domainTypeValues = request.getParameterValues("domainType");
		for (int i = 0; i < domainTypeValues.length; i++) {
			if (i == 0) {
				platSdkConfig.setDomainType1(domainTypeValues[i].trim());
			} else if (i == 1) {
				platSdkConfig.setDomainType2(domainTypeValues[i].trim());
			} else if (i == 2) {
				platSdkConfig.setDomainType3(domainTypeValues[i].trim());
			} else if (i == 3) {
				platSdkConfig.setDomainType4(domainTypeValues[i].trim());
			} else if (i == 4) {
				platSdkConfig.setDomainType5(domainTypeValues[i].trim());
			} else if (i == 5) {
				platSdkConfig.setDomainType6(domainTypeValues[i].trim());
			} else if (i == 6) {
				platSdkConfig.setDomainType7(domainTypeValues[i].trim());
			} else if (i == 7) {
				platSdkConfig.setDomainType8(domainTypeValues[i].trim());
			} else if (i == 8) {
				platSdkConfig.setDomainType9(domainTypeValues[i].trim());
			} else if (i == 9) {
				platSdkConfig.setDomainType10(domainTypeValues[i].trim());
			}
		}

		String[] domainUrlValues = request.getParameterValues("domainUrl");
		for (int i = 0; i < domainUrlValues.length; i++) {
			if (i == 0) {
				platSdkConfig.setDomainUrl1(domainUrlValues[i].trim());
			} else if (i == 1) {
				platSdkConfig.setDomainUrl2(domainUrlValues[i].trim());
			} else if (i == 2) {
				platSdkConfig.setDomainUrl3(domainUrlValues[i].trim());
			} else if (i == 3) {
				platSdkConfig.setDomainUrl4(domainUrlValues[i].trim());
			} else if (i == 4) {
				platSdkConfig.setDomainUrl5(domainUrlValues[i].trim());
			} else if (i == 5) {
				platSdkConfig.setDomainUrl6(domainUrlValues[i].trim());
			} else if (i == 6) {
				platSdkConfig.setDomainUrl7(domainUrlValues[i].trim());
			} else if (i == 7) {
				platSdkConfig.setDomainUrl8(domainUrlValues[i].trim());
			} else if (i == 8) {
				platSdkConfig.setDomainUrl9(domainUrlValues[i].trim());
			} else if (i == 9) {
				platSdkConfig.setDomainUrl10(domainUrlValues[i].trim());
			}
		}
		Long operatorId = UserUtils.getUser().getId();
		platSdkConfig.setOperatorId(operatorId.toString());
		if ("add".equals(op)) {
			platSDKService.create(platSdkConfig);
		} else if ("edit".equals(op)) {
			platSDKService.saveByKey(platSdkConfig);
		}
//		addMessage(redirectAttributes, "保存SDK版本'成功");
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/platSdkVersionList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @return
	 * @Title: platConfProxyBatchSplitList
	 * @Description: 商户批量拆包列表
	 * @return: String
	 */
	@RequestMapping(value = {"platConfProxyBatchSplitList", ""})
	public String platConfProxyBatchSplitList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		return "modules/platform/platConfProxyBatchSplitList";
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @return
	 * @Title: editPlatConfProxyBatchSplit
	 * @Description: 批量拆包编辑
	 * @return: String
	 */
	@RequestMapping(value = {"editPlatConfProxyBatchSplit", ""})
	public String editPlatConfProxyBatchSplit(HttpServletRequest request, HttpServletResponse response, Model model,
											  @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		model.addAttribute("op", "edit");
		chanMchtPaytypeService.list(null);
		return "modules/platform/platConfProxyBatchSplitEdit";
	}
}
