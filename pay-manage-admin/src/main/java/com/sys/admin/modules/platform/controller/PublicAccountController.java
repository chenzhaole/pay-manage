package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.utils.ExcelUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.boss.api.service.trade.service.IDfProducerService;
import com.sys.common.db.JedisConnPool;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.enums.MchtAccountTypeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.ProxyPayBatchStatusEnum;
import com.sys.common.enums.ProxyPayDetailStatusEnum;
import com.sys.common.enums.ProxyPayRequestEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.DesUtil32;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.common.util.JedisUtil;
import com.sys.common.util.NumberUtils;
import com.sys.common.util.PostUtil;
import com.sys.common.util.QueueUtil;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.MchtProduct;
import com.sys.core.dao.dmo.PlatBank;
import com.sys.core.dao.dmo.PlatFeerate;
import com.sys.core.dao.dmo.PlatProduct;
import com.sys.core.dao.dmo.PlatProxyBatch;
import com.sys.core.dao.dmo.PlatProxyDetail;
import com.sys.core.service.ChannelService;
import com.sys.core.service.MchtAccountDetailService;
import com.sys.core.service.MchtAccountInfoService;
import com.sys.core.service.MchtProductService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatBankService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.service.ProductService;
import com.sys.core.service.ProxyBatchService;
import com.sys.core.service.ProxyDetailService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 公户账户数据
 */
@Controller
@RequestMapping(value = "${adminPath}/publicaccount")
public class PublicAccountController extends BaseController {

	@Autowired
	private ProxyBatchService proxyBatchService;

	@Autowired
	private ProxyDetailService proxyDetailService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private ProductService productService;

	@Autowired
	private MchtProductService mchtProductService;

	@Autowired
	private PlatFeerateService feerateService;
	@Autowired
	private MchtAccountInfoService mchtAccountInfoService;
	@Autowired
	private MchtAccountDetailService mchtAccountDetailService;
	@Autowired
	private PlatBankService platBankService;
	@Autowired
	private AccountAdminService accountAdminService;

	private static final int    maxProxyBatchDetailNum   =  Integer.parseInt(ConfigUtil.getValue("maxProxyBatchDetailNum"));             //最大代付批次明细数量

	private static final BigDecimal maxProxyDetailAmount     =  new BigDecimal(ConfigUtil.getValue("maxProxyDetailAmount"));   //最大代付明细金额

	/**
	 * 代付批次列表
	 *//*
	@RequestMapping(value = {"proxyBatchList", ""})
	public String proxyBatchList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		PlatProxyBatch proxyBatch = new PlatProxyBatch();
		proxyBatch.setId(paramMap.get("batchId"));
		proxyBatch.setMchtId(paramMap.get("mchtId"));
		proxyBatch.setMchtOrderId(paramMap.get("mchtOrderId"));
		proxyBatch.setPayStatus(paramMap.get("payStatus"));
		proxyBatch.setCheckStatus(paramMap.get("checkStatus"));

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		proxyBatch.setPageInfo(pageInfo);

		//查询商户列表
		List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());

		model.addAttribute("mchtInfos", mchtInfos);

		//查询商户列表
		Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");

		int proxyCount = proxyBatchService.count(proxyBatch);

		List<PlatProxyBatch> proxyInfoList = proxyBatchService.list(proxyBatch);
		if (!CollectionUtils.isEmpty(proxyInfoList)) {
			for (PlatProxyBatch platProxyBatch : proxyInfoList) {
				platProxyBatch.setMchtId(mchtMap.get(platProxyBatch.getMchtId()));
				platProxyBatch.setPayStatus(ProxyPayBatchStatusEnum.toEnum(platProxyBatch.getPayStatus()).getDesc());
			}
		}
		Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/proxy/proxyBatchList";
	}


	*//**
	 * 代付明细列表
	 *//*
	@RequestMapping(value = {"proxyDetailList", ""})
	public String proxyDetailList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		PlatProxyDetail proxyDetail = new PlatProxyDetail();
		assemblySearch(paramMap, proxyDetail);

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		proxyDetail.setPageInfo(pageInfo);

		PlatProxyBatch proxyBatch = null;
		//批次信息
		if (StringUtils.isNotBlank(paramMap.get("batchId"))){
			proxyBatch = proxyBatchService.queryByKey(paramMap.get("batchId"));
		}

		//查询商户列表
		List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
		//  产品
		List<PlatProduct> platProducts = productService.list(new PlatProduct());


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

		List<PlatProxyDetail> proxyInfoList = proxyDetailService.list(proxyDetail);

		List<PlatProxyDetail> newList = new ArrayList<>();
		if (proxyInfoList != null && proxyInfoList.size() != 0) {
			for (PlatProxyDetail info : proxyInfoList) {
				info.setExtend2(mchtMap.get(info.getMchtId()));
				info.setExtend3(channelMap.get(info.getChanId()));
				newList.add(info);
			}
		}

		Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, newList, true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/proxy/proxyDetailList";
	}

	*//**
	 * 代付详情
	 *//*
	@RequestMapping(value = {"proxyDetail", ""})
	public String proxyDetail(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
//        PlatProxyDetail proxyDetail = new PlatProxyDetail();
		String detailId = paramMap.get("detailId");
		String batchId = paramMap.get("batchId");

		PlatProxyDetail proxyDetail = proxyDetailService.queryByKey(detailId);

		//批次信息
		PlatProxyBatch proxyBatch = proxyBatchService.queryByKey(batchId);

		//查询商户列表
		List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");

		if (proxyDetail != null) {
			proxyDetail.setExtend2(mchtMap.get(proxyDetail.getMchtId()));
			proxyDetail.setExtend3(channelMap.get(proxyDetail.getChanId()));
		}
		model.addAttribute("chanInfos", chanInfoList);
		model.addAttribute("mchtInfos", mchtInfos);
		model.addAttribute("proxyBatch", proxyBatch);
		model.addAttribute("proxyDetail", proxyDetail);

		model.addAttribute("paramMap", paramMap);
		return "modules/proxy/proxyDetail";
	}
*/
	/**
	 * 跳转到提交公户账务数据页面
	 */
	@RequestMapping("toCommitPublicAccount")
	public String toCommitPublicAccount(Model model) {
		//加载所有公户
		//model.addAttribute("balance", balance);
		return "modules/publicaccount/add";
	}

	/**
	 * 提交公户账务数据
	 */
	@RequestMapping("commitPublicAccount")
	public String commitPublicAccount(MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
		String messageType = null;
		String message = null;
		try {
			String fileName = file.getOriginalFilename();
			InputStream is  = file.getInputStream();
			List<String[]> data = ExcelUtil.readexcel(is,fileName);

		} catch (Exception e) {
			messageType = "error";
			message = e.getMessage();
			logger.error("提交公户账务数据异常",e);
		}
		if (StringUtils.equals("error", messageType)) {
			redirectAttributes.addFlashAttribute("messageType", messageType);
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/toCommitBatch";
		}
		return "modules/proxy/confirmCommitBatch";
	}

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
/**
	 * 校验excel
	 *//*

	private Sheet checkFile(String mchtId, MultipartFile file) throws Exception {
		String fileName = file.getOriginalFilename();
		InputStream is = file.getInputStream();
		Workbook wb;

		if (StringUtils.isBlank(fileName)) {
			throw new RuntimeException("导入文档为空!");
		} else if (fileName.toLowerCase().endsWith("xls") || fileName.toLowerCase().endsWith("xlsx")) {
			wb = new HSSFWorkbook(is);
		} else {
			throw new RuntimeException("文档格式不正确!");
		}
		if (wb.getNumberOfSheets() < 0) {
			throw new RuntimeException("文档中没有工作表!");
		}

		Sheet sheet = wb.getSheetAt(0);

		int rowCount = sheet.getLastRowNum();
		logger.info("商户ID: {} 代付笔数: {}", mchtId, rowCount);
		if (rowCount > maxProxyBatchDetailNum) {
			throw new RuntimeException("总笔数大于"+maxProxyBatchDetailNum+"条，如果空行较多，为避免提示笔数超限，请在EXCEL文件中选择多行进行整行删除！");
		}
		if (rowCount == 0) {
			throw new RuntimeException("EXCEL文件中无代付信息！");
		}

		return sheet;
	}

	*/
/**
	 * 读取数据
	 *//*

	private void readExcel(String mchtId, Sheet sheet, BigDecimal fee, PlatProxyBatch batch, List<PlatProxyDetail> details) {
		Map<String, String> platBankMap = getPlatBankMap();
		BigDecimal totalAmount = BigDecimal.valueOf(0);// 累计交易金额
		int totalCount = 0;// 累计交易条数

		String batchId = IdUtil.createProxBatchId("0");//代付批次ID
		batch.setRequesetType(ProxyPayRequestEnum.PLATFORM.getCode());
		batch.setId(batchId);
		batch.setPlatOrderId(batchId);
		batch.setMchtId(mchtId);
		batch.setUserId(UserUtils.getUser().getId().toString());
		batch.setPayType(PayTypeEnum.BATCH_DF.getCode());
		batch.setRequesetType(ProxyPayRequestEnum.PLATFORM.getCode());
		batch.setPayStatus(ProxyPayBatchStatusEnum.AUDIT_DOING.getCode());
		batch.setCreateTime(new Date());
		batch.setUpdateTime(new Date());

		Set<String> seqSet = new HashSet<>();
		//创建代付批次
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			int k = i + 1;//用于提示错误行号
			Row row = sheet.getRow(i);
			if (row != null) {
				PlatProxyDetail detail = buildProxyDetail(row, i, batch, fee, platBankMap, seqSet);
				details.add(detail);
				totalAmount = totalAmount.add(detail.getAmount());
				totalCount++;
			} else {
				throw new RuntimeException("第" + k + "行为空，如果空行较多，为避免提示空行，请在EXCEL文件中选择多行进行整行删除!");
			}
		}

		batch.setTotalAmount(totalAmount);
		batch.setTotalNum(totalCount);
		BigDecimal proxyFee = fee.multiply(BigDecimal.valueOf(batch.getTotalNum()));//代付手续费=单笔手续费*代付笔数
		batch.setTotalFee(proxyFee);
	}
*/

}
