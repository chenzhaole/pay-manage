package com.sys.admin.modules.merchant.controller;

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
import com.sys.admin.modules.sys.service.SysAreaService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.CertTypeEnum;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
				if (mchtInfo.getSignType().contains(SignTypeEnum.CLIENT_MCHT.getCode())){
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
			String mchtNo = IdUtil.createCode();
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
