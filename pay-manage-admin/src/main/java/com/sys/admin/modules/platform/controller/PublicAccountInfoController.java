package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.util.DateUtils;
import com.sys.common.util.ExcelUtil;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.AccountAmount;
import com.sys.core.dao.dmo.PublicAccountInfo;
import com.sys.core.service.AccountAmountService;
import com.sys.core.service.PublicAccountInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公户信息管理
 */
@Controller
@RequestMapping(value = "${adminPath}/publicaccountinfo")
public class PublicAccountInfoController extends BaseController {

	@Autowired
	private PublicAccountInfoService publicAccountInfoService;

	/**
	 * 公户数据列表
	 */
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		PublicAccountInfo publicAccountInfo = new PublicAccountInfo();
		//公户编号
		if(StringUtils.isNotBlank(paramMap.get("publicAccountCode"))){
			publicAccountInfo.setPublicAccountCode(paramMap.get("publicAccountCode"));
		}
		//公户账号
		if(StringUtils.isNotBlank(paramMap.get("publicAccountNo"))){
			publicAccountInfo.setPublicAccountNo(paramMap.get("publicAccountNo"));
		}
		//公户名称
		if(StringUtils.isNotBlank(paramMap.get("publicAccountName"))){
			publicAccountInfo.setPublicAccountName(paramMap.get("publicAccountName"));
		}
		logger.info("公户信息数据列表,查询条件"+JSON.toJSON(publicAccountInfo));
		model.addAttribute("paramMap",paramMap);

		int count =publicAccountInfoService.publicAccountInfoCount(publicAccountInfo);

		if(count ==0){
			return "modules/publicaccountinfo/list";
		}
		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		if(count<=20&&pageNo!=1){
			pageNo = 1;
		}
		pageInfo.setPageNo(pageNo);
		publicAccountInfo.setPageInfo(pageInfo);

		List<PublicAccountInfo> publicAccountInfos =publicAccountInfoService.list(publicAccountInfo);
		if(publicAccountInfos!=null){
			for(PublicAccountInfo pai:publicAccountInfos){
				pai.setCreateOperatorName(UserUtils.getUserName(pai.getCreateOperatorId()));
			}
		}

		Page page = new Page(pageNo, pageInfo.getPageSize(), count, publicAccountInfos, true);
		model.addAttribute("page", page);
		model.addAttribute("list",publicAccountInfos);

		return "modules/publicaccountinfo/list";
	}

	/**
	 * 跳转到公户信息编辑页面
	 */
	@RequestMapping(value = {"toEdit", ""})
	public String toEdit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		String publicAccountCode = paramMap.get("publicAccountCode");
		logger.info("跳转到公户信息编辑页面,id="+publicAccountCode);
		PublicAccountInfo publicAccountInfo = null;
		if(StringUtils.isNotBlank(publicAccountCode)){
			publicAccountInfo = publicAccountInfoService.queryByKey(publicAccountCode);
		}
		model.addAttribute("publicAccountInfo",publicAccountInfo);
		return "modules/publicaccountinfo/edit";
	}

	@RequestMapping(value = {"edit", ""})
	public String edit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes) {
		PublicAccountInfo pai = getPublicAccountInfo(request);
		logger.info("公户信息编辑,publicAccountInfo为"+pai);
		String message = "保存成功";
		String messageType = "success";

		try{
			if(StringUtils.isNotBlank(request.getParameter("publicAccountCode"))){
				PublicAccountInfo publicAccountInfo = publicAccountInfoService.queryByKey(request.getParameter("publicAccountCode"));
				BeanUtils.copyProperties(pai, publicAccountInfo);
				pai.setUpdateTime(new Date());
				pai.setUpdateOperatorId(UserUtils.getUser().getId());
				publicAccountInfoService.saveByKey(publicAccountInfo);
			}else{
				pai.setPublicAccountCode(IdUtil.createCaCommonId("0"));
				pai.setCreateTime(new Date());
				pai.setCreateOperatorId(UserUtils.getUser().getId());
				publicAccountInfoService.create(pai);
			}

		}catch (Exception e){
			logger.error("公户信息编辑异常",e);
			message = "保存失败";
			messageType = "error";
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:"+ GlobalConfig.getAdminPath()+"/publicaccountinfo/publicAccountList";
	}

	private PublicAccountInfo getPublicAccountInfo(HttpServletRequest request){
		PublicAccountInfo pai = new PublicAccountInfo();
		pai.setPublicAccountCode(request.getParameter("publicAccountCode"));
		pai.setPublicAccountName(request.getParameter("publicAccountName"));
		pai.setPublicAccountNo(request.getParameter("publicAccountNo"));
		pai.setPublicOpenAccountBankName(request.getParameter("publicOpenAccountBankName"));
		pai.setModelName(request.getParameter("modelName"));
		pai.setRemark(request.getParameter("remark"));
		pai.setStatus(request.getParameter("status"));
		pai.setBindPhones(request.getParameter("bindPhones"));
		return pai;
	}
}
