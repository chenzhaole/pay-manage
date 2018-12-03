package com.sys.admin.modules.platform.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.merchant.bo.MerchantForm;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.admin.modules.platform.bo.PlatAccountAdjustBO;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.boss.api.entry.cache.CacheOrder;
import com.sys.common.db.JedisConnPool;
import com.sys.common.enums.*;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatAccountAdjust;
import com.sys.core.service.MchtAccountDetailService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatAccountAdjustService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 调账controller
 */
@Controller
@RequestMapping("${adminPath}/platform/adjust")
public class PlatAccountAdjustController extends BaseController {
	@Autowired
	private PlatAccountAdjustService platAccountAdjustService;
	@Autowired
	private MerchantService merchantService;
	@Autowired
	private MchtAccountDetailService mchtAccountDetailService;
	@Autowired
	private AccountAdminService accountAdminService;
	@Autowired
	MerchantAdminService merchantAdminService;

	@ModelAttribute
	public PlatAccountAdjust get(@RequestParam(required = false) String id) {
		PlatAccountAdjust platAccountAdjust;
		if (StringUtils.isNotBlank(id)) {
			platAccountAdjust = platAccountAdjustService.queryByKey(id);
		} else {
			platAccountAdjust = new PlatAccountAdjust();
		}
		return platAccountAdjust;
	}

	/**
	 * 调账记录列表
	 */
	@RequestMapping(value = {"list", ""})
	@RequiresPermissions("platform:adjust:list")
	public String list(PlatAccountAdjust platAccountAdjust, HttpServletRequest request, Model model, String logo) {
		try {
			PageInfo pageInfo = new PageInfo();
			platAccountAdjust.setPageInfo(pageInfo);

			if (StringUtils.isNotBlank(request.getParameter("pageNo"))){
				pageInfo.setPageNo(Integer.parseInt(request.getParameter("pageNo")));
			}

			if (StringUtils.isNotBlank(request.getParameter("pageSize"))){
				pageInfo.setPageSize(Integer.parseInt(request.getParameter("pageSize")));
			}

			if (StringUtils.isNotBlank(request.getParameter("createTime"))){
				platAccountAdjust.setCreateTime(DateUtils.parseDate(request.getParameter("createTime")));
			}

			if (StringUtils.isNotBlank(request.getParameter("auditStartTime"))){
				platAccountAdjust.setAuditStartTime(DateUtils.parseDate(request.getParameter("auditStartTime")));
			}
			if (StringUtils.isNotBlank(request.getParameter("auditEndTime"))){
				platAccountAdjust.setAuditEndTime(DateUtils.parseDate(request.getParameter("auditEndTime")));
			}

			List<PlatAccountAdjust> list = platAccountAdjustService.list(platAccountAdjust);
			List<PlatAccountAdjustBO> showList = new ArrayList<>(list.size());
			int count = platAccountAdjustService.count(platAccountAdjust);

			Page page = new Page(pageInfo.getPageNo(), pageInfo.getPageSize(), count, true);
			model.addAttribute("list", list);
			model.addAttribute("page", page);
			model.addAttribute("auditStartTime", request.getParameter("auditStartTime"));
			model.addAttribute("auditEndTime", request.getParameter("auditEndTime"));

			//初始化商户名称
			List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());

			Map<String, String> mchtMap = Collections3.extractToMap(
					mchtInfos, "id", "name");
			ConvertUtils.register(new DateConverter(null), java.util.Date.class);
			ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
			for (PlatAccountAdjust adjust : list) {
				PlatAccountAdjustBO bo = new PlatAccountAdjustBO();
				BeanUtils.copyProperties(bo, adjust);
				bo.setMchtName(mchtMap.get(adjust.getMchtId()));
				showList.add(bo);
			}
			//标记区别 调账申请和调账审批
			model.addAttribute("logo", logo);
			model.addAttribute("mchtInfos", mchtInfos);
			model.addAttribute("adjustInfo", platAccountAdjust);
			model.addAttribute("list", showList);
			model.addAttribute("page", page);
			model.addAttribute("createTime", request.getParameter("createTime"));
			model.addAttribute("auditTime", request.getParameter("auditTime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/platform/platAccountAdjustList";
	}

	/**
	 * 调账申请页面
	 */
	@RequestMapping(value = "form")
	@RequiresPermissions("platform:adjust:apply")
	public String form(PlatAccountAdjust platAccountAdjust, HttpServletRequest request, Model model) {
		//所有可配商户
		List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(new MchtInfo());
		List<MerchantForm> mchtInfosResult = new ArrayList<>();
		for (MerchantForm mchtInfo : mchtInfos) {
			if (StringUtils.isBlank(mchtInfo.getSignType())) {
				continue;
			}
			if (mchtInfo.getSignType().contains(SignTypeEnum.COMMON_MCHT.getCode())
					|| mchtInfo.getSignType().contains(SignTypeEnum.CLIENT_MCHT.getCode())) {
				mchtInfosResult.add(mchtInfo);
			}
		}

		//根据名称排序
		Collections3.sortByName(mchtInfosResult, "name");
		model.addAttribute("mchtInfos", mchtInfosResult);
		return "modules/platform/platAccountAdjustForm";
	}


	/**
	 * 保存调账申请
	 */
	@RequestMapping("save")
	@RequiresPermissions("platform:adjust:apply")
	public String save(PlatAccountAdjust platAccountAdjust, RedirectAttributes redirectAttributes) {
		Long operatorId = UserUtils.getUser().getId();
		String operatorName = UserUtils.getUser().getName();
		platAccountAdjust.setCreatorId(operatorId.toString());
		platAccountAdjust.setCreatorName(operatorName);
		//元转分
		if (platAccountAdjust.getAdjustAmount() != null) {
			platAccountAdjust.setAdjustAmount(platAccountAdjust.getAdjustAmount().multiply(BigDecimal.valueOf(100)));
		}
		if (platAccountAdjust.getFeeAmount() != null) {
			platAccountAdjust.setFeeAmount(platAccountAdjust.getFeeAmount().multiply(BigDecimal.valueOf(100)));
		}
		platAccountAdjust.setAuditStatus(AuditEnum.AUDITING.getCode());
		platAccountAdjust.setId(IdUtil.createCommonId());
		platAccountAdjust.setUpdateTime(new Date());
		platAccountAdjust.setCreateTime(new Date());
		int result = platAccountAdjustService.create(platAccountAdjust);

		String message, messageType;
		if (result == 1) {
			message = "保存成功";
			messageType = "success";
		} else {
			message = "保存失败";
			messageType = "error";
		}

		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/adjust/list";
	}

	/**
	 * 调账审批
	 */
	@RequestMapping("audit")
	@RequiresPermissions("platform:adjust:audit")
	public String audit(PlatAccountAdjust platAccountAdjust, RedirectAttributes redirectAttributes) {
		String message, messageType;

		//校验数据库状态
		PlatAccountAdjust platAccountAdjustOri = platAccountAdjustService.queryByKey(platAccountAdjust.getId());
		if (AuditEnum.AUDITED.getCode().equals(platAccountAdjustOri.getAuditStatus())
				|| AuditEnum.UNAUDITED.getCode().equals(platAccountAdjustOri.getAuditStatus())) {
			message = "已处理";
			messageType = "error";
		} else {
			Long operatorId = UserUtils.getUser().getId();
			String operatorName = UserUtils.getUser().getName();

			platAccountAdjust.setAuditorId(operatorId.toString());
			platAccountAdjust.setAuditorName(operatorName);
			platAccountAdjust.setAuditTime(new Date());
			platAccountAdjust.setUpdateTime(new Date());
			int result = platAccountAdjustService.saveByKey(platAccountAdjust);

			if (result == 1) {
				message = "保存成功";
				messageType = "success";

				if (AuditEnum.AUDITED.getCode().equals(platAccountAdjust.getAuditStatus())) {

					CacheMchtAccount cacheMchtAccount = new CacheMchtAccount();
					CacheMcht cacheMcht = new CacheMcht();
					cacheMcht.setMchtId(platAccountAdjust.getMchtId());
					cacheMchtAccount.setCacheMcht(cacheMcht);

					cacheMchtAccount.setType(Integer.valueOf(MchtAccountTypeEnum.ADJUSTMENT_ACCOUNT.getCode()));
					cacheMchtAccount.setPlatAccountAdjust(platAccountAdjust);

					//校验账户是否已处理
					if (queryCachePayOrderForAccount(platAccountAdjust.getId())) {
						logger.info("调账ID：" + platAccountAdjust.getId() + "已入账");

					} else {
						logger.info("调账ID：" + platAccountAdjust.getId() + "，调账功能（插入CacheMchtAccount）信息为：" + JSONObject.toJSONString(cacheMchtAccount));
						insertMchtAccountInfo2redis(cacheMchtAccount);
					}
				}
			} else {
				message = "保存失败";
				messageType = "error";
			}
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/adjust/list";
	}

	/**
	 * 查询余额
	 */
	@RequestMapping("balance")
	@RequiresPermissions("platform:adjust:apply")
	public void balance(String mchtId, HttpServletResponse response) throws IOException {
		PageInfo pageInfo = new PageInfo();
		BigDecimal platBalance = null;
		MchtAccountDetail detailQuery = new MchtAccountDetail();
		detailQuery.setMchtId(mchtId);
		detailQuery.setSuffix(DateUtils.formatDate(new Date(), "yyyyMM"));

		pageInfo.setPageNo(1);
		pageInfo.setPageSize(1);
		detailQuery.setPageInfo(pageInfo);

		List<MchtAccountDetail> list = accountAdminService.list(detailQuery);

		if (!CollectionUtils.isEmpty(list)) {
			logger.info("账务信息：" + JSON.toJSONString(list.get(0)));
			platBalance = list.get(0).getCashTotalAmount();
			//余额 = 现金金额 - 冻结金额
			if (list.get(0).getFreezeTotalAmount() != null) {
				platBalance = platBalance.subtract(list.get(0).getFreezeTotalAmount());
			}
		}

		if (platBalance != null) {
			//分转元
			platBalance = platBalance.divide(BigDecimal.valueOf(100));
		} else {
			platBalance = BigDecimal.ZERO;
		}

		String contentType = "text/plain";
		response.reset();
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(platBalance.stripTrailingZeros().toPlainString());
	}

    /**
     * 缓存中,未入账及已入账队列是否存在某条订单记录
     */
    boolean queryCachePayOrderForAccount(String accountId) {

        String taskKey = IdUtil.REDIS_ACCT_MCHT_ACCOUNT_ADJUST_TASK_LIST;
        String doneKey = IdUtil.REDIS_ACCT_MCHT_ACCOUNT_ADJUST_DONE_LIST;
        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = JedisConnPool.getPool();
            jedis = pool.getResource();

            //未入账
            List<String> payList = jedis.lrange(taskKey, 0, -1);
            if (!CollectionUtils.isEmpty(payList)) {
                for (String value : payList) {
                    CacheMchtAccount cacheMchtAccount = JSON.parseObject(value, CacheMchtAccount.class);
                    PlatAccountAdjust cacheOrder = cacheMchtAccount.getPlatAccountAdjust();
                    if (cacheOrder != null && accountId.equals(cacheOrder.getId())) {
                        return true;
                    }
                }
            }

            //已入账
            List<String> doneList = jedis.lrange(doneKey, 0, -1);
            if (!CollectionUtils.isEmpty(doneList)) {
                for (String value : doneList) {
                    CacheMchtAccount cacheMchtAccount = JSON.parseObject(value, CacheMchtAccount.class);
                    PlatAccountAdjust cacheOrder = cacheMchtAccount.getPlatAccountAdjust();
                    if (cacheOrder != null && accountId.equals(cacheOrder.getId())) {
                        return true;
                    }
                }
            }

        } catch (JedisConnectionException var8) {
            var8.printStackTrace();
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            JedisConnPool.returnResource(pool, jedis, "");
        }
        return false;
    }

	protected void insertMchtAccountInfo2redis(CacheMchtAccount cacheMchtAccount) {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = JedisConnPool.getPool("缓存插入cacheMchtAccount信息");
			jedis = pool.getResource();
			long rsPay = jedis.lpush(IdUtil.REDIS_ACCT_MCHT_ACCOUNT_ADJUST_TASK_LIST, JSON.toJSONString(cacheMchtAccount));
			System.out.println("插入了一个新的任务： rsPay = " + rsPay);
		} catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常：" + je.getMessage());
		} catch (Exception e) {
			logger.info("<insertData-error>error[" + e.getMessage() + "]</insertData-error>");
			e.printStackTrace();
		} finally {
			JedisConnPool.returnResource(pool, jedis, "");
		}
	}

	/**
	 * 调账审批
	 */
	@RequestMapping("viewAudit")
	@RequiresPermissions("platform:adjust:audit")
	public String viewAudit(PlatAccountAdjust platAccountAdjust, Model model) {

		PlatAccountAdjust platAccountAdjustOri = platAccountAdjustService.queryByKey(platAccountAdjust.getId());
		MchtInfo mchtInfo = merchantService.queryByKey(platAccountAdjust.getMchtId());
		if(mchtInfo!= null){
			platAccountAdjustOri.setMchtName(mchtInfo.getName());
		}
		model.addAttribute("platAccountAdjustOri", platAccountAdjustOri);
		return "modules/platform/platAccountViewAdjustForm";
	}

	@RequestMapping(value = "/export")
	public String export(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
						 @RequestParam Map<String, String> paramMap) throws IOException {
		PlatAccountAdjust platAccountAdjust = new PlatAccountAdjust();
		assemblySearch(paramMap, platAccountAdjust);

		int orderCount = platAccountAdjustService.count(platAccountAdjust);
		//计算条数 上限五万条
		if (orderCount <= 0) {
			redirectAttributes.addFlashAttribute("messageType", "fail");
			redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
			response.setCharacterEncoding("UTF-8");
			return "redirect:" + GlobalConfig.getAdminPath() + "/platform/adjust/list";
		}
		if (orderCount > 50000) {
			redirectAttributes.addFlashAttribute("messageType", "fail");
			redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
			response.setCharacterEncoding("UTF-8");
			return "redirect:" + GlobalConfig.getAdminPath() + "/platform/adjust/list";
		}
		//获取数据List
		List<PlatAccountAdjust> list = platAccountAdjustService.list(platAccountAdjust);
		if (list == null || list.size() == 0) {
			redirectAttributes.addFlashAttribute("messageType", "fail");
			redirectAttributes.addFlashAttribute("message", "导出条数为0条");
			response.setCharacterEncoding("UTF-8");
			return "redirect:" + GlobalConfig.getAdminPath() + "/platform/adjust/list";
		}

		//获取商户列表
		List<MchtInfo> mchtList = merchantService.list(new MchtInfo());

		Map<String, String> mchtMap = Collections3.extractToMap(mchtList, "id", "name");

		for (PlatAccountAdjust adjust : list) {
			adjust.setMchtName(mchtMap.get(adjust.getMchtId()));
			adjust.setAccountType(AccAccountTypeEnum.toEnum(adjust.getAccountType()).getDesc());
		}

		//获取当前日期，为文件名
		String fileName = DateUtils.formatDate(new Date()) + ".xls";

		String[] headers = {"调账订单号", "商户名称", "商户号", "账户类型","调账方向", "申请调账金额(元)", "申请调账日期", "申请人", "审批日期", "审批人", "审批状态"};

		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
		OutputStream out = response.getOutputStream();

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("调账申请表");
		sheet.setColumnWidth(0, 20 * 1256);
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);

		int j = 0;
		for (String header : headers) {
			HSSFCell cell = row.createCell((short) j);
			cell.setCellValue(header);
			sheet.autoSizeColumn(j);
			j++;
		}

		if (!Collections3.isEmpty(list)) {
			int rowIndex = 1;//行号
			String adjustTypeName = "";
			String auditStatus = "";
			for (PlatAccountAdjust accountAdjust : list) {
				int cellIndex = 0;
				row = sheet.createRow(rowIndex);
				HSSFCell cell = row.createCell(cellIndex);
				cell.setCellValue(accountAdjust.getId());
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(accountAdjust.getMchtName());
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(accountAdjust.getMchtId());
				cellIndex++;


				cell = row.createCell(cellIndex);
				cell.setCellValue(accountAdjust.getAccountType());
				cellIndex++;


				if (accountAdjust.getAdjustType().equals(AdjustTypeEnum.ADJUST_ADD.getCode())){
					adjustTypeName = AdjustTypeEnum.ADJUST_ADD.getMessage();
				}else if (accountAdjust.getAdjustType().equals(AdjustTypeEnum.ADJUST_REDUCE.getCode())){
					adjustTypeName = AdjustTypeEnum.ADJUST_REDUCE.getMessage();
				}else if (accountAdjust.getAdjustType().equals(AdjustTypeEnum.ADJUST_FREEZE.getCode())){
					adjustTypeName = AdjustTypeEnum.ADJUST_FREEZE.getMessage();
				}else if (accountAdjust.getAdjustType().equals(AdjustTypeEnum.ADJUST_UNFREEZE.getCode())){
					adjustTypeName = AdjustTypeEnum.ADJUST_UNFREEZE.getMessage();
				}
				cell = row.createCell(cellIndex);
				cell.setCellValue(adjustTypeName);
				cellIndex++;


				cell = row.createCell(cellIndex);
				if (accountAdjust.getAdjustAmount() != null) {
					BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountAdjust.getAdjustAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
					cell.setCellValue(bigDecimal.doubleValue());
				}
				cellIndex++;

				cell = row.createCell(cellIndex);
				if (accountAdjust.getCreateTime() != null) {
					cell.setCellValue(DateUtils.formatDate(accountAdjust.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
				}
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(accountAdjust.getCreatorName());
				cellIndex++;


				cell = row.createCell(cellIndex);
				if (accountAdjust.getAuditTime() != null) {
					cell.setCellValue(DateUtils.formatDate(accountAdjust.getAuditTime(), "yyyy-MM-dd HH:mm:ss"));
				}
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(accountAdjust.getAuditorName());
				cellIndex++;

				if (accountAdjust.getAuditStatus().equals(AuditEnum.VALID.getCode())){
					auditStatus = AuditEnum.VALID.getMessage();
				}else if (accountAdjust.getAuditStatus().equals(AuditEnum.INVALID.getCode())){
					auditStatus = AuditEnum.INVALID.getMessage();
				}else if (accountAdjust.getAuditStatus().equals(AuditEnum.AUDITING.getCode())){
					auditStatus = AuditEnum.AUDITING.getMessage();
				}else if (accountAdjust.getAuditStatus().equals(AuditEnum.AUDITED.getCode())){
					auditStatus = AuditEnum.AUDITED.getMessage();
				}else if (accountAdjust.getAuditStatus().equals(AuditEnum.UNAUDITED.getCode())){
					auditStatus = AuditEnum.UNAUDITED.getMessage();
				}else if (accountAdjust.getAuditStatus().equals(AuditEnum.FROZEN.getCode())){
					auditStatus = AuditEnum.FROZEN.getMessage();
				}
				cell = row.createCell(cellIndex);
				cell.setCellValue(auditStatus);
				cellIndex++;

				rowIndex++;
			}
		}
		wb.write(out);
		out.flush();
		out.close();

		redirectAttributes.addFlashAttribute("messageType", "success");
		redirectAttributes.addFlashAttribute("message", "导出完毕");
		response.setCharacterEncoding("UTF-8");
		return "redirect:" + GlobalConfig.getAdminPath() + "/platform/adjust/list";
	}

	private void assemblySearch(Map<String, String> paramMap, PlatAccountAdjust platAccountAdjust) {
		if (StringUtils.isNotBlank(paramMap.get("id"))) {
			platAccountAdjust.setId(paramMap.get("id"));
		}
		if (StringUtils.isNotBlank(paramMap.get("mchtId"))) {
			platAccountAdjust.setMchtId(paramMap.get("mchtId"));
		}
		if (StringUtils.isNotBlank(paramMap.get("auditStatus"))) {
			platAccountAdjust.setAccountType(paramMap.get("auditStatus"));
		}

		String auditStartTime = paramMap.get("auditStartTime");
		String auditStartTimeStr = "";
		if (StringUtils.isNotBlank(auditStartTime)){
			platAccountAdjust.setSuffix(auditStartTime.replace("-", "").substring(0, 6));
			auditStartTimeStr = paramMap.get("auditStartTime");
			platAccountAdjust.setAuditStartTime(DateUtils.parseDate(auditStartTimeStr));
		}

		String auditEndTime = paramMap.get("auditEndTime");
		String auditEndTimeStr = "";
		if (StringUtils.isNotBlank(auditEndTime)){
			platAccountAdjust.setSuffix(auditEndTime.replace("-", "").substring(0, 6));
			auditEndTimeStr = paramMap.get("auditEndTime");
			platAccountAdjust.setAuditEndTime(DateUtils.parseDate(auditEndTimeStr));
		}

		String createTime = paramMap.get("createTime");
		String createTimeStr = "";
		if (StringUtils.isBlank(createTime)) {
			platAccountAdjust.setSuffix(DateUtils.formatDate(new Date(), "yyyyMM"));
			createTimeStr = DateUtils.getDate();
			platAccountAdjust.setCreateTime(DateUtils.parseDate(createTimeStr));
		} else {
			platAccountAdjust.setSuffix(createTime.replace("-", "").substring(0, 6));
			createTimeStr = paramMap.get("createTime");
			platAccountAdjust.setCreateTime(DateUtils.parseDate(createTimeStr));
		}
	}


}
