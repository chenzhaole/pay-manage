package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.common.util.DateUtils;
import com.sys.common.util.ExcelUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.AccountAmount;
import com.sys.core.dao.dmo.PublicAccountInfo;
import com.sys.core.service.AccountAmountService;
import com.sys.core.service.PublicAccountInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公户账务数据
 */
@Controller
@RequestMapping(value = "${adminPath}/publicaccount")
public class PublicAccountController extends BaseController {

	@Autowired
	private PublicAccountInfoService publicAccountInfoService;
	@Autowired
	private AccountAmountService accountAmountService;

	/**
	 * 公户账务数据列表
	 */
	@RequestMapping(value = {"publicAccountList", ""})
	public String publicAccountList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		AccountAmount accountAmount = new AccountAmount();
		//公户号
		accountAmount.setPublicAccountCode(paramMap.get("publicAccountCode"));
		if(StringUtils.isNotBlank(paramMap.get("addAmount"))){
			//贷方发生额
			accountAmount.setAddAmount(new BigDecimal(paramMap.get("addAmount")));
		}
		if(StringUtils.isNotBlank(paramMap.get("reductAmount"))){
			//借方发生额
			accountAmount.setReduceAmount(new BigDecimal(paramMap.get("reductAmount")));
		}
		if(StringUtils.isNotBlank(paramMap.get("accountName"))){
			//对方账户名
			accountAmount.setAccountName(paramMap.get("accountName"));
		}
		if(StringUtils.isNotBlank(paramMap.get("summary"))){
			//摘要
			accountAmount.setSummary(paramMap.get("summary"));
		}
		if(StringUtils.isNotBlank(paramMap.get("desc"))){
			//描述
			accountAmount.setSummary(paramMap.get("desc"));
		}

		String beginTime =paramMap.get("beginTime");
		String endTime =paramMap.get("endTime");
		if(StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)){
			accountAmount.setTradeBeginTime(DateUtils.parseDate(beginTime,"yyyy-MM-dd HH:mm:ss"));
			accountAmount.setTradeEndTime(DateUtils.parseDate(endTime,"yyyy-MM-dd HH:mm:ss"));
		}else{
			Date date = new Date();
			String dateString =DateUtils.formatDate(date,"yyyy-MM-dd");
			beginTime =dateString+" 00:00:00";
			endTime =dateString+" 23:59:59";
			accountAmount.setTradeBeginTime(DateUtils.parseDate(beginTime,"yyyy-MM-dd HH:mm:ss"));
			accountAmount.setTradeEndTime(DateUtils.parseDate(endTime,"yyyy-MM-dd HH:mm:ss"));
			paramMap.put("beginTime",beginTime);
			paramMap.put("endTime",endTime);
		}
		logger.info("公户账务数据列表,查询条件"+JSON.toJSON(accountAmount));
		model.addAttribute("paramMap",paramMap);

		List<PublicAccountInfo> pais = publicAccountInfoService.list(new PublicAccountInfo());
		Map<String,PublicAccountInfo> paisMap = new HashMap<>();
		if(pais!=null){
			for(PublicAccountInfo pai:pais){
				paisMap.put(pai.getPublicAccountCode(),pai);
			}
		}
		model.addAttribute("pais", pais);
		model.addAttribute("paisMap", paisMap);

		int count =accountAmountService.accountAmountCount(accountAmount);

		if(count ==0){
			return "modules/publicaccount/list";
		}
		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		accountAmount.setPageInfo(pageInfo);

		List<AccountAmount> accountAmounts =accountAmountService.list(accountAmount);

		Page page = new Page(pageNo, pageInfo.getPageSize(), count, accountAmounts, true);
		model.addAttribute("page", page);
		model.addAttribute("list",accountAmounts);

		return "modules/publicaccount/list";
	}

	/**
	 * 跳转到公户账务编辑页面
	 */
	@RequestMapping(value = {"toEdit", ""})
	public String toEdit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		String id = paramMap.get("id");
		logger.info("跳转到公户账务编辑页面,id="+id);
		List<PublicAccountInfo> pais = publicAccountInfoService.list(new PublicAccountInfo());
		Map<String,PublicAccountInfo> paisMap = new HashMap<>();
		if(pais!=null){
			for(PublicAccountInfo pai:pais){
				paisMap.put(pai.getPublicAccountCode(),pai);
			}
		}
		AccountAmount accountAmount = accountAmountService.queryByKey(id);
		model.addAttribute("pais", pais);
		model.addAttribute("paisMap", paisMap);
		model.addAttribute("accountAmount",accountAmount);
		return "modules/publicaccount/edit";
	}

	@RequestMapping(value = {"edit", ""})
	public String edit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes) {
		logger.info("公户账务编辑,paramMap为"+paramMap);
		String id   = paramMap.get("id");
		String desc = paramMap.get("desc");
		String message = "保存成功";
		String messageType = "success";
		AccountAmount accountAmount = accountAmountService.queryByKey(id);
		if(StringUtils.isNotBlank(desc)){
			accountAmount.setDesc(desc);
			try{
				accountAmountService.saveByKey(accountAmount);
			}catch (Exception e){
				logger.error("公户账务编辑异常",e);
				message = "保存失败";
				messageType = "error";
			}
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:"+ GlobalConfig.getAdminPath()+"/publicaccount/publicAccountList";
	}

	/**
	 * 跳转到提交公户账务数据页面
	 */
	@RequestMapping("toCommitPublicAccount")
	@RequiresPermissions("publicaccount:commit")
	public String toCommitPublicAccount(Model model) {
		//加载所有公户
		List<PublicAccountInfo> pais = publicAccountInfoService.list(new PublicAccountInfo());
		model.addAttribute("pais", pais);
		return "modules/publicaccount/add";
	}

	/**
	 * 提交公户账务数据
	 */
	@RequestMapping("commitPublicAccount")
	@RequiresPermissions("publicaccount:commit")
	public String commitPublicAccount(MultipartFile file, Model model, RedirectAttributes redirectAttributes,@RequestParam Map<String, String> paramMap) {
		String tag = "提交公户账务数据";
		String messageType = null;
		try {
			String publicAccountCode = paramMap.get("publicAccountCode");	//公户编号
			String fileName = file.getOriginalFilename();
			java.io.File f =java.io.File.createTempFile("tmp", null);
			file.transferTo(f);
			List<String[]> data = ExcelUtil.readexcel(f, fileName);
			f.deleteOnExit();
			//获取公户信息
			PublicAccountInfo pai = new PublicAccountInfo();
			pai.setPublicAccountCode(publicAccountCode);
			List<PublicAccountInfo> pais = publicAccountInfoService.list(pai);
			pai = new PublicAccountInfo();
			if(pais!=null&&pais.size()>0){
				pai = pais.get(0);
			}
			logger.info(tag+",publicAccountCode="+publicAccountCode+",fileName="+fileName+",excel中数据的条数为"+(data==null?0:data.size())+",选择的公户信息为"+ JSON.toJSON(pai));
			//解析excel数据到标准模型
			Map resultMap = accountAmountService.convertExcelDataToAccountAmount(publicAccountCode,pai.getModelName(),data);
			String errMsg = resultMap.get("errMsg")+"";
			List<Map> aas = (List<Map>)resultMap.get("accountAmounts");
			//批量入库
			resultMap = accountAmountService.batchAccountAmount(aas);
			errMsg = errMsg+resultMap.get("errMsg");
			redirectAttributes.addFlashAttribute("message", "提交成功"+(errMsg.length()==0?"":",错误信息为"+errMsg.toString()));
		} catch (Exception e) {
			messageType = "error";
			logger.error("提交公户账务数据异常",e);
			redirectAttributes.addFlashAttribute("messageType", messageType);
			redirectAttributes.addFlashAttribute("message", "提交失败");
		}
		return "redirect:" + GlobalConfig.getAdminPath() + "/publicaccount/toCommitPublicAccount";
	}

	/**
	 * 将页面查询条件转换为实体类
	 */
	/*private AccountAmount getAccountAmount(Map<String,String> paramMap,AccountAmount accountAmount){
		//公户编号
		if (StringUtils.isNotBlank(paramMap.get("publicAccountCode"))) {
			accountAmount.setPublicAccountCode(paramMap.get("publicAccountCode"));
		}
		//初始化页面开始时间
		String startTime = paramMap.get("startTime");
		if (StringUtils.isBlank(startTime)) {
			proxyDetail.setStartTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
			paramMap.put("startTime", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
		} else {
			paramMap.put("startTime", startTime);
			proxyDetail.setStartTime(DateUtils.parseDate(startTime));
		}
		String endTime = paramMap.get("endTime");
		//初始化页面结束时间
		if (StringUtils.isBlank(endTime)) {
			proxyDetail.setEndTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 23:59:59"));
			paramMap.put("endTime", DateUtils.getDate("yyyy-MM-dd") + " 23:59:59");
		} else {
			paramMap.put("endTime", endTime);
			proxyDetail.setEndTime(DateUtils.parseDate(endTime));
		}
	}*/

	/**
	 * 查询指定商户的平台余额
	 *
	 * @return
	 *//*

	private BigDecimal queryPlatBalance(String mchtId) {
		BigDecimal balance = BigDecimal.ZERO;
		try {
			String topUrl = ConfigUtil.getValue("gateway.url");
			if (topUrl.endsWith("/")) {
				topUrl = topUrl.substring(0, topUrl.length() - 1);
			}
			String gatewayUrl = topUrl + "/df/gateway/balanceForAdmin";
			Map<String, String> params = new HashMap<>();
			params.put("mchtId", mchtId);
			logger.info(mchtId + " 查询mchtAccountInfo表商户余额,请求URL: " + gatewayUrl + " 请求参数: " + JSON.toJSONString(params));
			String balanceString = null;
			balanceString = HttpUtil.postConnManager(gatewayUrl, params, true);
			if (StringUtils.isNotBlank(balanceString)) {
				balance = new BigDecimal(balanceString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(mchtId + " 查询mchtAccountInfo表商户余额,返回值(平台余额): " + balance);
		return balance;
	}
	*/

	@ResponseBody
	@RequestMapping(value = {"savePublicAccountInfo", ""})
	public String savePublicAccountInfo(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
		PublicAccountInfo accountInfo = new PublicAccountInfo();
		try{
			String publicAccountCode = paramMap.get("publicAccountCode");
			String publicAccountName = URLDecoder.decode(paramMap.get("publicAccountName"),"utf-8");
			String modelName		 = paramMap.get("modelName");
			String op = paramMap.get("op");
			logger.info("公户信息保存,publicAccountCode="+publicAccountCode+",publicAccountName="+publicAccountName+",modelName="+modelName+",op="+op);
			accountInfo.setPublicAccountCode(publicAccountCode);
			accountInfo.setPublicAccountName(publicAccountName);
			accountInfo.setModelName(modelName);
			if("add".equals(op)){
				publicAccountInfoService.create(accountInfo);
			}else{
				PublicAccountInfo oldAccountInfo = publicAccountInfoService.queryByKey(publicAccountCode);
				publicAccountInfoService.updateBySelective(accountInfo,oldAccountInfo);
			}
		}catch (Exception e){
			logger.error("公户数据保存异常",e);
		}
		return JSON.toJSON(accountInfo).toString();
	}

	/**
	 * 公户账务数据删除
	 */
	@RequestMapping(value = {"deleteAccountAmount", ""})
	public String deleteAccountAmount(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
		PublicAccountInfo accountInfo = new PublicAccountInfo();
		try{
			String idstr  = paramMap.get("ids");
			logger.info("公户账务数据删除,ids="+idstr);
			if(StringUtils.isNotBlank(idstr)){
				String[] ids = idstr.split(",");
				for(String id:ids){
                    accountAmountService.deleteByKey(id);
                }
			}
            model.addAttribute("paramMap",paramMap);
		}catch (Exception e){
			logger.error("公户数据删除异常",e);
		}
        return "redirect:"+ GlobalConfig.getAdminPath()+"/publicaccount/publicAccountList";
	}
}
