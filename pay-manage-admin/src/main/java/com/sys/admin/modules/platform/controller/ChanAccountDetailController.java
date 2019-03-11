package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.ChanAccountAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.service.order.CaChanAccountDetailOrderService;
import com.sys.common.util.DateUtils;
import com.sys.common.util.ExcelUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.AccountAmount;
import com.sys.core.dao.dmo.CaChanAccountDetail;
import com.sys.core.dao.dmo.CaElectronicAccount;
import com.sys.core.dao.dmo.PublicAccountInfo;
import com.sys.core.service.AccountAmountService;
import com.sys.core.service.CaChanAccountDetailService;
import com.sys.core.service.ElectronicAccountInfoService;
import com.sys.core.service.PublicAccountInfoService;
import com.sys.core.vo.ElectronicAccountVo;
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
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上游账务明细
 */
@Controller
@RequestMapping(value = "${adminPath}/chanAccountDetail")
public class ChanAccountDetailController extends BaseController {

	@Autowired
	private PublicAccountInfoService publicAccountInfoService;
	@Autowired
	private ChanAccountAdminService chanAccountAdminService;
	@Autowired
	private ElectronicAccountInfoService electronicAccountInfoService;

	/**
	 * 上游账务明细列表
	 */
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		CaChanAccountDetail caChanAccountDetail = new CaChanAccountDetail();
		if(StringUtils.isNotBlank(paramMap.get("accountId"))){
			//电子账户
			caChanAccountDetail.setAccountId(paramMap.get("accountId"));
		}
		if(StringUtils.isNotBlank(paramMap.get("platOrderId"))){
			//平台订单号
			caChanAccountDetail.setPlatOrderId(paramMap.get("platOrderId"));
		}
		if(StringUtils.isNotBlank(paramMap.get("tradeType"))){
			//交易类型
			caChanAccountDetail.setTradeType(paramMap.get("tradeType"));
		}
		if(StringUtils.isNotBlank(paramMap.get("accountType"))){
			//账户类型
			caChanAccountDetail.setAccountType(paramMap.get("accountType"));
		}
		if(StringUtils.isNotBlank(paramMap.get("accountAddType"))){
			//记账类型
			caChanAccountDetail.setAccountId(paramMap.get("accountAddType"));
		}
		if(StringUtils.isNotBlank(paramMap.get("createTime"))){
			//入账时间
			caChanAccountDetail.setCreateTime(DateUtils.parseDate(paramMap.get("createTime")+"","yyyy-MM-dd"));
		}else{
			caChanAccountDetail.setCreateTime(DateUtils.parseDate(DateUtils.formatDate(new Date(),"yyyy-MM-dd")+" 00:00:00","yyyy-MM-dd HH:mm:ss"));
			paramMap.put("createTime",DateUtils.formatDate(new Date(),"yyyy-MM-dd"));
		}

		logger.info("上游账务数据列表,查询条件"+JSON.toJSON(caChanAccountDetail));
		model.addAttribute("paramMap",paramMap);

		//电子账户列表
		List<CaElectronicAccount> electronicAccounts = electronicAccountInfoService.list(new ElectronicAccountVo());
		model.addAttribute("electronicAccounts",electronicAccounts);

		int count =chanAccountAdminService.count(caChanAccountDetail);

		if(count ==0){
			return "modules/platform/chanAccountList";
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
		pageInfo.setPageSize(20);
		caChanAccountDetail.setPageInfo(pageInfo);

		List<CaChanAccountDetail> caChanAccountDetails = chanAccountAdminService.list(caChanAccountDetail);
		Page page = new Page(pageNo, pageInfo.getPageSize(), count, caChanAccountDetails, true);
		model.addAttribute("page", page);
		model.addAttribute("list",caChanAccountDetails);
		return "modules/platform/chanAccountList";
	}

}
