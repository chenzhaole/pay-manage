package com.sys.admin.modules.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.merchant.bo.MerchantForm;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.admin.modules.platform.bo.MchtProductFormInfo;
import com.sys.admin.modules.platform.service.MchtProductAdminService;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.service.SysAreaService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.CertTypeEnum;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatFeerate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/merchant")
public class MerchantController extends BaseController {

	@Autowired
	private SysAreaService areaService;

	@Autowired
	private MerchantAdminService merchantAdminService;

	@Autowired
	private ChanMchtAdminService chanMchtAdminService;

	@Autowired
	private MchtProductAdminService mchtProductAdminService;

	//商户基本信息接口地址
	@Value("${mchtInfoData.url}")
	private String mchtInfoDataUrl;

	//交易相关数据接口地址
	@Value("${payData.url}")
	private String payDataUrl;

	//商户账户详情信息接口地址
	@Value("${mchtAccountDetailData.url}")
	private String mchtAccountDetailUrl;

	//商户费率信息接口地址
	@Value("${mchtFeerateInfoData.url}")
	private String mchtFeerateInfoDataUrl;

	/**
	 * 商户首页
	 */
	@RequestMapping(value = {"mchtWelcome", ""})
	public String  mchtWelcome(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
		//当前登陆商户
		User user = UserUtils.getUser();
		String mchtCode = user.getLoginName();

		//1.商户基本信息
		MchtInfo mchtInfoData = queryMchtInfoByHttp(mchtCode);
		if(null == mchtInfoData){
			return "modules/merchant/mchtWelcome";
		}
		model.addAttribute("mchtInfoData",  mchtInfoData);

		//2.交易相关数据
		Map payData = queryPayDataByHttp(mchtCode);
		model.addAttribute("payData", payData);

		//3.商户账户详情信息
		MchtAccountDetail mchtAccountDetailData = queryMchtAccountDetailByHttp(mchtCode);
		if(null != mchtAccountDetailData){
			//冻结金额
			BigDecimal freezeTotalAmount = mchtAccountDetailData.getFreezeTotalAmount();
			freezeTotalAmount = freezeTotalAmount.divide(new BigDecimal(100));
			mchtAccountDetailData.setFreezeTotalAmount(freezeTotalAmount);

			//可提现金额 = 现金总金额 - 冻结总金额;
			//现金总金额
			BigDecimal cashTotalAmount = mchtAccountDetailData.getCashTotalAmount();
			//可提现金额
			BigDecimal presentedAmount = cashTotalAmount.subtract(freezeTotalAmount);
			presentedAmount = presentedAmount.divide(new BigDecimal(100));
			mchtAccountDetailData.setSettleTotalAmount(presentedAmount);
		}

		model.addAttribute("mchtAccountDetailData", mchtAccountDetailData);
		//4.商户费率信息
		List<PlatFeerate> mchtFeerateInfoData = queryMchtFeerateInfoByHttp(mchtCode);
		//将费率转成map
		Map<String, String> mchtFeerateInfoMap = null;
		if(null != mchtFeerateInfoData && mchtFeerateInfoData.size() > 0){
			mchtFeerateInfoMap = mchtFeerateInfoDataToMap(mchtFeerateInfoData);
		}
		model.addAttribute("mchtFeerateInfoMap", mchtFeerateInfoMap);

		return "modules/merchant/mchtWelcome";
	}

	//将可用的费率封装进map中
	private Map<String, String> mchtFeerateInfoDataToMap(List<PlatFeerate> mchtFeerateInfoData) {
		if(null != mchtFeerateInfoData && mchtFeerateInfoData.size() > 0){
			Map<String, String> data = new HashMap<>();
			for (PlatFeerate platFeerate : mchtFeerateInfoData){
				String biz = (StringUtils.isNotBlank(platFeerate.getBizRefId()) && platFeerate.getBizRefId().contains("&")) ? platFeerate.getBizRefId().split("&")[1]:"";
				BigDecimal feeRate = platFeerate.getFeeRate();
				if(StringUtils.isNotBlank(biz) && null != feeRate && filterPayType(biz)){
					data.put(PayTypeEnum.toEnum(biz).getDesc(),feeRate.toString());
				}
			}
			return data;
		}else{
			return null;
		}
	}

	/**
	 * 过滤掉不能显示出来的支付类型
	 * @param biz
	 * @return
	 */
	private boolean filterPayType(String biz) {
//		List types = new ArrayList();
//		types.add("ca001");
//		types.add("ca002");
//		types.add("jh001");
//		types.add("hf001");
//		types.add("wx000");
//		types.add("wx502");
//		types.add("wx503");
//		types.add("al000");
//		types.add("al102");
//		types.add("al502");
//		types.add("al503");
//		types.add("sn000");
//		types.add("sn502");
//		types.add("sn503");
//		types.add("qq000");
//		types.add("qq102");
//		types.add("qq502");
//		types.add("qq503");
//		types.add("jd000");
//		types.add("jd102");
//		types.add("jd502");
//		types.add("jd503");
//		types.add("yl000");
//		types.add("yl402");
//		types.add("yl502");
//		types.add("yl503");
//		if(types.contains(biz)){
//			return false;
//		}

		return true;
	}

	/**
	 *  查询商户交易数据
	 * @param mchtCode
	 * @return
	 */
	private Map queryPayDataByHttp(String mchtCode) {
		String url = payDataUrl+"/"+mchtCode;
		logger.info("商户首页，查询商户交易数据信息，请求地址："+url);
		String retData = "";
		try {
			retData = HttpUtil.postConnManager(url, null);
			logger.info("商户首页，查询商户交易数据信息，接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return JSON.parseObject(retData, Map.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询商户基本信息
	 * @param mchtCode
	 * @return
	 */
	private MchtInfo queryMchtInfoByHttp(String mchtCode) {
		String url = mchtInfoDataUrl+"/"+mchtCode;
		logger.info("商户首页，查询商户信息，请求地址："+url);
		String retData = "";
		try {
			retData = HttpUtil.postConnManager(url, null);
			logger.info("商户首页，查询商户信息，接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return JSON.parseObject(retData, MchtInfo.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 商户账户详情信息
	 */
	private MchtAccountDetail queryMchtAccountDetailByHttp(String mchtCode) {
		String url = mchtAccountDetailUrl+"/"+mchtCode;
		logger.info("商户首页，查询商户账户详情信息，请求地址："+url);
		String retData = "";
		try {
			retData = HttpUtil.postConnManager(url, null);
			logger.info("商户首页，查询商户账户详情信息，接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return JSON.parseObject(retData, MchtAccountDetail.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 商户费率信息
	 */
	private List<PlatFeerate> queryMchtFeerateInfoByHttp(String mchtCode) {
		String url = mchtFeerateInfoDataUrl+"/"+mchtCode;
		logger.info("商户首页，查询商户费率详情信息，请求地址："+url);
		String retData = "";
		try {
			retData = HttpUtil.postConnManager(url, null);
			logger.info("商户首页，查询商户费率信息，接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return JSONArray.parseArray(retData, PlatFeerate.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value={"add"})
	public String add(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes) {

		model.addAttribute("op", "add");
		//获取商户类型枚举类
		SignTypeEnum[] merchantTypList =  SignTypeEnum.values();
		model.addAttribute("merchantTypList", merchantTypList);
		//获取证件类型枚举类
		CertTypeEnum[] certTypeList = CertTypeEnum.values();
		model.addAttribute("certTypeList", certTypeList);

		//商户列表 只展示代理商
		List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(new MchtInfo());
		List<MerchantForm> mchtInfosResult = new ArrayList<>();
		for (MerchantForm mchtInfo : mchtInfos) {
			if(StringUtils.isBlank(mchtInfo.getSignType())){
				continue;
			}
			if (mchtInfo.getSignType().contains(SignTypeEnum.CLIENT_MCHT.getCode())){
				mchtInfosResult.add(mchtInfo);
			}
		}
		model.addAttribute("mchts", mchtInfosResult);

		model.addAttribute("areas", areaService.getAllJson()); //获取所有地区
		return "modules/merchant/merchantEdit";
	}
	

	@RequestMapping(value={"edit"})
	public String edit(MerchantForm mcht,Model model) {
		try {
			//获取商户类型枚举类
			SignTypeEnum[] merchantTypList =  SignTypeEnum.values();
			model.addAttribute("merchantTypList", merchantTypList);
			//获取证件类型枚举类
			CertTypeEnum[] certTypeList = CertTypeEnum.values();
			model.addAttribute("certTypeList", certTypeList);
			String id = mcht.getId();
			MerchantForm mchtInfo = merchantAdminService.getMerchantById(id);
			model.addAttribute("merchant", mchtInfo);
			model.addAttribute("op", "edit");

			//商户列表
			List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(new MchtInfo());
			List<MerchantForm> mchtInfoResults = new ArrayList<>();
			for (MerchantForm mchtInfoTemp : mchtInfos) {
				if (mchtInfoTemp.getId().equals(id)) {
					continue;
				}
				if (mchtInfoTemp.getSignType().contains(SignTypeEnum.CLIENT_MCHT.getCode())){
					mchtInfoResults.add(mchtInfoTemp);
				}
			}
			model.addAttribute("mchts", mchtInfoResults);

			model.addAttribute("areas", areaService.getAllJson()); //获取所有地区

			//检查商户配置情况
			ChanMchtFormInfo chanMchtFormInfo = new ChanMchtFormInfo();
			chanMchtFormInfo.setMchtId(mcht.getId());
			List<ChanMchtFormInfo> chanMchtFormInfos = chanMchtAdminService.getChannelListSimple(chanMchtFormInfo);
			int chan = 0, pro  = 0;
			if (!CollectionUtils.isEmpty(chanMchtFormInfos)){
				chan = 1;
			}
			MchtProductFormInfo mchtProductFormInfo = new MchtProductFormInfo();
			mchtProductFormInfo.setMchtCode(mcht.getMchtCode());
			List<MchtProductFormInfo> mchtProductFormInfos = mchtProductAdminService.getProductList(mchtProductFormInfo);

			if (!CollectionUtils.isEmpty(mchtProductFormInfos)){
				pro = 1;
			}

			model.addAttribute("chanCount", chan);
			model.addAttribute("productCount", pro);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		
		return "modules/merchant/merchantEdit";
	}
	/**
	 * 
	 * @Title: addMerchantSave 
	 * @Description: 新增商户
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @return
	 * @return: String
	 */
	@RequestMapping(value = { "addMerchantSave", "" })
	public String addMerchantSave(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes) {
		try {
			//系统生成8位MerchantNo
			String mchtNo = IdUtil.createMchtId();
			//创建者UserId
			Long operatorId =  UserUtils.getUser().getId();
			//将页面请求参数转换成商户实体bo
			MerchantForm merchantForm = new MerchantForm(request);
        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(merchantForm.getCode())){
        		//转换成功,调用商户service执行新增商户操作
        		merchantForm.setMchtCode(mchtNo);
        		merchantForm.setOperatorId(operatorId);

				//校验商户简称重复
				MchtInfo mchtInfo = new MchtInfo();
				mchtInfo.setShortName(merchantForm.getShortName());
				List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(mchtInfo);
				if (!CollectionUtils.isEmpty(mchtInfos)){
					redirectAttributes.addFlashAttribute("message", "商户简称重复！");
					redirectAttributes.addFlashAttribute("messageType", "error");
					return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
				}

        		String result = merchantAdminService.addMerchantService(merchantForm);
        		if("success".equals(result)){
        			redirectAttributes.addFlashAttribute("message", "保存商户信息成功！");
        			redirectAttributes.addFlashAttribute("messageType", "success");
        			return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
        		}
        	}else{
        		redirectAttributes.addFlashAttribute("message", "操作失败！");
				redirectAttributes.addFlashAttribute("messageType", "error");
				return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
        	}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/merchant/merchantEdit";
	}
	
	@RequestMapping(value = { "editMerchantSave", "" })
	public String editSave(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes) {
		try {
			//创建者UserId
			Long operatorId =  UserUtils.getUser().getId();
			//将页面请求参数转换成商户实体bo
			MerchantForm merchantForm = new MerchantForm(request);
        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(merchantForm.getCode())){
        		//转换成功,调用商户service执行更新商户操作
        		merchantForm.setOperatorId(operatorId);
        		String result = merchantAdminService.updateMerchantService(merchantForm);
        		if("success".equals(result)){
        			redirectAttributes.addFlashAttribute("message", "更新商户信息成功！");
        			redirectAttributes.addFlashAttribute("messageType", "success");
        			return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
        		}
        	}else{
        		redirectAttributes.addFlashAttribute("message", "操作失败！");
				redirectAttributes.addFlashAttribute("messageType", "error");
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/merchant/merchantEdit";
	}

//	@RequestMapping(value = {"detailByLoginUser", ""})
//	public String detailByLoginUser(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
//        try {
//        	User user = UserUtils.getUser();
//        	String url = GlobalConfig.getConfig("boss.url")+"merchant/getByMchtNo";
//        	Map<String,String> params = new HashMap<String, String>();
//        	params.put("no", user.getNo());
//        	String resp = HttpUtil.post(url, params);
////        	DataResponse dataResponse = JSONObject.parseObject(resp, DataResponse.class);
////        	MchtInfo merchant = new MchtInfo();
////        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(dataResponse.getCode())){
////        		String data = JSONObject.toJSONString(dataResponse.getData());
////        		merchant = JSONObject.parseObject(data,MchtInfo.class);
////        	}
////            Page<MchtInfo> page = new Page<>(request, response);
////            page.setList(new ArrayList<MchtInfo>());
////            model.addAttribute("page", page);
////            model.addAttribute("merchant", merchant);
////            model.addAttribute("list", new ArrayList<MchtInfo>());
//        } catch (Exception e) {
//        	e.printStackTrace();
//            logger.error(e.getMessage(), e);
//        }
//        return "modules/merchant/merchantDetail";
//	}
//
//	@RequestMapping(value = {"detailByNo", ""})
//	public String detailByNo(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
//        try {
//        	User user = UserUtils.getUser();
//        	String no = request.getParameter("mchtNo");
//        	String url = GlobalConfig.getConfig("boss.url")+"merchant/getByMchtNo";
//        	Map<String,String> params = new HashMap<String, String>();
//        	params.put("no", no);
//        	String resp = HttpUtil.post(url, params);
////        	DataResponse dataResponse = JSONObject.parseObject(resp, DataResponse.class);
////        	MchtInfo merchant = new MchtInfo();
////        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(dataResponse.getCode())){
////        		String data = JSONObject.toJSONString(dataResponse.getData());
////        		merchant = JSONObject.parseObject(data,MchtInfo.class);
////        	}
////            Page<MchtInfo> page = new Page<>(request, response);
////            page.setList(new ArrayList<MchtInfo>());
////            model.addAttribute("page", page);
////            model.addAttribute("merchant", merchant);
////            model.addAttribute("list", new ArrayList<MchtInfo>());
//        } catch (Exception e) {
//        	e.printStackTrace();
//            logger.error(e.getMessage(), e);
//        }
//        return "modules/merchant/merchantDetail";
//	}
	

	/**
	 * 商户列表
	 */
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
		MchtInfo mchtInfo = new MchtInfo();
		mchtInfo.setMchtCode(paramMap.get("mchtCode"));
		mchtInfo.setName(paramMap.get("mchtName"));
		mchtInfo.setServiceMobile(paramMap.get("serviceMobile"));
		mchtInfo.setSignType(paramMap.get("signType"));

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		mchtInfo.setPageInfo(pageInfo);

		List<MerchantForm> mchtInfoList = merchantAdminService.getMchtInfoList(mchtInfo);
//		model.addAttribute("list", mchtInfoList);
		int mchtCount = merchantAdminService.mchtCount(mchtInfo);
		Page page = new Page(pageNo,pageInfo.getPageSize(),mchtCount,mchtInfoList,true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/merchant/merchantList";
	}
	
	/**
	 * 商户简称查询
	 */
	@ResponseBody
	@RequestMapping(value = {"checkShortName", ""})
	public String select(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {

		JSONObject result = new JSONObject();

		MchtInfo mchtInfo = new MchtInfo();
		mchtInfo.setShortName(paramMap.get("shortName"));
		List<MerchantForm> mchtInfoList = merchantAdminService.getMchtInfoList(mchtInfo);
		int count = 0;
		if (mchtInfoList != null){
			count = mchtInfoList.size();
		}

		String op = paramMap.get("op");
		if ("add".equals(op)) {
			result.put("count", count);
		} else {

			if (count == 1){
				MerchantForm merchantForm = merchantAdminService.getMerchantById(paramMap.get("id"));
				if (mchtInfo.getShortName().equals(merchantForm.getShortName())){
					result.put("count", 0);
				}else {
					result.put("count", count);
				}
			}else{
				result.put("count", count);
			}

		}

        return result.toJSONString();
	}

	/**
	 * 商户删除
	 */
	@RequestMapping(value = {"deleteMcht"})
	public String deleteMcht(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes) {

		int result;
		String message = "", messageType = null;

		boolean chan = false, pro = false;

		ChanMchtFormInfo chanMchtFormInfo = new ChanMchtFormInfo();
		chanMchtFormInfo.setMchtId(paramMap.get("id"));
		List<ChanMchtFormInfo> chanMchtFormInfos = chanMchtAdminService.getChannelListSimple(chanMchtFormInfo);

		if (CollectionUtils.isEmpty(chanMchtFormInfos)){
			chan = true;

			MchtProductFormInfo mchtProductFormInfo = new MchtProductFormInfo();
			mchtProductFormInfo.setMchtId(paramMap.get("id"));
			List<MchtProductFormInfo> mchtProductFormInfos = mchtProductAdminService.getProductList(mchtProductFormInfo);

			if (CollectionUtils.isEmpty(mchtProductFormInfos)){
				pro = true;
			}else {
				message = "该商户已配置了产品，无法删除";
				messageType = "error";
			}

		}else {
			message = "该商户已配置通道商户支付方式，无法删除";
			messageType = "error";
		}


		if (chan && pro){
			result = merchantAdminService.deleteMerchantById(paramMap.get("id"));
			if (result == 1){
				message = "删除成功";
				messageType = "success";
			}
		}

		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		response.setCharacterEncoding("UTF-8");
		return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
	}

}
