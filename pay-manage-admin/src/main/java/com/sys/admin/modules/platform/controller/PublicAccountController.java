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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Date;
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
		accountAmount.setPublicAccountCode(paramMap.get("publicAccountCode"));
		accountAmount
		String beginTime =paramMap.get("beginTime");
		String endTime =paramMap.get("endTime");
		if(StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)){
			accountAmount.setTradeTime(DateUtils.parseDate(beginTime+" 00:00:00","yyyy-MM-dd HH:mm:ss"));
			accountAmount.setCreateTime(DateUtils.parseDate(endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
		}else{
			Date date = new Date();
			String dateString =DateUtils.formatDate(date,"yyyy-MM-dd");
			beginTime =dateString+"00:00:00";
			endTime =dateString+" 23:59:59";
			accountAmount.setTradeTime(DateUtils.parseDate(beginTime,"yyyy-MM-dd HH:mm:ss"));
			accountAmount.setCreateTime(DateUtils.parseDate(endTime,"yyyy-MM-dd HH:mm:ss"));
			paramMap.put("beginTime",beginTime);
			paramMap.put("endTime",endTime);

		}
		model.addAttribute("paramMap",paramMap);

		List<PublicAccountInfo> pais = publicAccountInfoService.list(new PublicAccountInfo());
		model.addAttribute("pais", pais);

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
	 * @param mchtId
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


}
