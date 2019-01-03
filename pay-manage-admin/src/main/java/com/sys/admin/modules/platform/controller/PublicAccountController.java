package com.sys.admin.modules.platform.controller;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.utils.ExcelUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.PublicAccountAmountService;
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
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 公户账务数据
 */
@Controller
@RequestMapping(value = "${adminPath}/publicaccount")
public class PublicAccountController extends BaseController {

	@Autowired
	private PublicAccountAmountService publicAccountAmountService;
	@Autowired
	private PublicAccountInfoService publicAccountInfoService;
	@Autowired
	private AccountAmountService accountAmountService;

	/**
	 * 公户账务数据列表
	 */
	/*@RequestMapping(value = {"publicAccountList", ""})
	public String publicAccountList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		AccountAmount accountAmount = new AccountAmount();

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		accountAmount.setPageInfo(pageInfo);

		//查询商户列表
		List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());


		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");
		Map<String, String> productMap = Collections3.extractToMap(platProducts, "id", "name");

		if (proxyBatch != null) {
			proxyBatch.setChanId(channelMap.get(proxyBatch.getChanId()));
			proxyBatch.setProductId(productMap.get(proxyBatch.getProductId()));
			proxyBatch.setExtend3(mchtMap.get(proxyBatch.getMchtId()));
			model.addAttribute("proxyBatch", proxyBatch);
		}
		model.addAttribute("chanInfos", chanInfoList);
		model.addAttribute("mchtInfos", mchtInfos);
//		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);

		int proxyCount = proxyDetailService.count(proxyDetail);

		List<AccountAmount> proxyInfoList = accountAmountService.list(proxyDetail);


		Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, newList, true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/proxy/proxyDetailList";
	}*/

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
		String messageType = null;
		String message = null;
		try {
			String modelName 		 = paramMap.get("modelName");			//模型名称
			String publicAccountCode = paramMap.get("publicAccountCode");	//公户编号
			String fileName = file.getOriginalFilename();
			InputStream is  = file.getInputStream();
			List<String[]> data = ExcelUtil.readexcel(is,fileName);
			//获取公户信息
			PublicAccountInfo pai = new PublicAccountInfo();
			pai.setPublicAccountCode(publicAccountCode);
			List<PublicAccountInfo> pais = publicAccountInfoService.list(pai);
			pai = new PublicAccountInfo();
			if(pais==null&&pais.size()>0){
				pai = pais.get(0);
			}
			//解析excel数据到标准模型
			List<AccountAmount> aas = publicAccountAmountService.convertExcelDataToAccountAmount(publicAccountCode,pai.getModelName(),data);
			//批量入库
			publicAccountAmountService.batchAccountAmount(aas);
		} catch (Exception e) {
			messageType = "error";
			message = e.getMessage();
			logger.error("提交公户账务数据异常",e);
		}
		if (StringUtils.equals("error", messageType)) {
			redirectAttributes.addFlashAttribute("messageType", messageType);
			redirectAttributes.addFlashAttribute("message", "提交失败");
		}else{
			redirectAttributes.addFlashAttribute("message", "提交成功");
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
