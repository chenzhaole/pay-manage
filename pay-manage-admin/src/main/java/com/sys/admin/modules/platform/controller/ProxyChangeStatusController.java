package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.boss.api.service.trade.handler.ITradeDFBatchHandler;
import com.sys.common.db.JedisConnPool;
import com.sys.common.enums.AuditEnum;
import com.sys.common.enums.MchtAccountTypeEnum;
import com.sys.common.enums.ProxyPayBatchStatusEnum;
import com.sys.common.enums.ProxyPayDetailStatusEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/proxy")
public class ProxyChangeStatusController extends BaseController {

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
	private ProxyDetailAuditService proxyDetailAuditService;

	@Autowired
	private AccountAdminService accountAdminService;

	@Autowired
	private ITradeDFBatchHandler tradeDFBatchHandler;

	@Autowired
	private ChanMchtPaytypeService chanMchtPaytypeService;


	@Value("${sms_send}")
	private String sms_send;


	/**
	 * 代付状态修改申请编辑列表页
	 */
	@RequestMapping(value = {"changeProxyStatus", ""})
	public String changeProxyStatus(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		PlatProxyDetail proxyDetail = new PlatProxyDetail();
		proxyDetail.setChanId(paramMap.get("chanId"));
		proxyDetail.setMchtId(paramMap.get("mchtId"));
		proxyDetail.setId(paramMap.get("detailId"));
		proxyDetail.setPayStatus(paramMap.get("payStatus"));
		proxyDetail.setPlatBatchId(paramMap.get("batchId"));

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		proxyDetail.setPageInfo(pageInfo);


		//查询商户列表
		List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
		//审批列表
		List<PlatProxyDetailAudit> proxyDetailAudits = proxyDetailAuditService.list(new PlatProxyDetailAudit());
		// 代付审批
		List<PlatProxyDetailAudit> PlatProxyDetailAuditList = proxyDetailAuditService.list(new PlatProxyDetailAudit());

		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");
		Map<String, String> auditMap = Collections3.extractToMap(proxyDetailAudits, "platDetailId", "platDetailId");
		Map<String, String> detailAuditMap = Collections3.extractToMap(PlatProxyDetailAuditList, "platDetailId", "auditStatus");

		model.addAttribute("chanInfos", chanInfoList);
		model.addAttribute("mchtInfos", mchtInfos);
//		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);

		int proxyCount = proxyDetailService.count(proxyDetail);

		List<PlatProxyDetail> proxyInfoList = proxyDetailService.list(proxyDetail);

		for (PlatProxyDetail detail : proxyInfoList) {
			detail.setMchtId(mchtMap.get(detail.getMchtId()));
			detail.setChanId(channelMap.get(detail.getChanId()));
			detail.setExtend1(detailAuditMap.get(detail.getId()));
			//判断是否已发起审批
			/*if (StringUtils.isNotBlank(auditMap.get(detail.getId()))) {
				detail.setExtend3("777");
			}*/
		}

		Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/proxy/changeProxyStatus";
	}

	/**
	 * 代付状态修改申请编辑页面
	 */
	@RequestMapping(value = {"changeProxyStatusEdit", ""})
	public String changeProxyStatusEdit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
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
		return "modules/proxy/changeProxyStatusEdit";
	}


	/**
	 * 代付状态修改申请保存
	 */
	@RequestMapping(value = {"changeProxyStatusSave", ""})
	@RequiresPermissions("mcht:proxy:changeSave")
	public String changeProxyStatusSave(HttpServletRequest request, HttpServletResponse response, Model model,
										@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		String mchtId = UserUtils.getUser().getId() + "";
		int result = 0;
		int deleteByKey = 0;
		String message, messageType;
		try {

			String detailId = paramMap.get("detailId");
			String changeStatus = paramMap.get("status");
			PlatProxyDetail proxyDetail = proxyDetailService.queryByKey(detailId);
			if (!changeStatus.equals(proxyDetail.getPayStatus())) {

				PlatProxyDetailAudit proxyDetailAudit = new PlatProxyDetailAudit();
				BeanUtils.copyProperties(proxyDetail, proxyDetailAudit);
				proxyDetailAudit.setPlatDetailId(detailId);
				proxyDetailAudit.setOperatorId(mchtId);
				proxyDetailAudit.setNewPayStatus(changeStatus);
				proxyDetailAudit.setAuditStatus(AuditEnum.AUDITING.getCode());
				proxyDetailAudit.setExtend2(paramMap.get("notes"));
				proxyDetailAudit.setCheckTime(new Date());

				deleteByKey = proxyDetailAuditService.deleteByKey(proxyDetailAudit.getId());
				if(deleteByKey == 1){
					logger.info("删除旧的代付状态修改审核请求，创建新的记录");
				}else{
					logger.info("不存在旧的代付状态修改审核请求，创建新的记录");
				}
				result = proxyDetailAuditService.create(proxyDetailAudit);

				if (result == 1) {
					message = "保存成功";
					messageType = "success";
				} else {
					message = "保存失败，该订单可能已申请修改";
					messageType = "error";
				}
			} else {
				message = "代付订单状态与修改状态一致";
				messageType = "error";
			}

		} catch (Exception e) {
			e.printStackTrace();
			message = "保存失败";
			messageType = "error";
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/changeProxyStatus";
	}

	/**
	 * 代付状态修改审批列表页
	 */
	@RequestMapping(value = {"changeProxyStatusAudit", ""})
	public String changeProxyStatusAudit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
		PlatProxyDetailAudit proxyDetail = new PlatProxyDetailAudit();
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

		//查询商户列表
		List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

		List<User> userList = UserUtils.getAllUserList();


		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");

		model.addAttribute("chanInfos", chanInfoList);
		model.addAttribute("mchtInfos", mchtInfos);
		model.addAttribute("users", userList);
//		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);

		int proxyCount = proxyDetailAuditService.count(proxyDetail);

		List<PlatProxyDetailAudit> proxyInfoList = proxyDetailAuditService.list(proxyDetail);
		for (PlatProxyDetailAudit platProxyDetailAudit : proxyInfoList) {
			platProxyDetailAudit.setAuditStatus(AuditEnum.getMessage(platProxyDetailAudit.getAuditStatus()));
			platProxyDetailAudit.setMchtId(mchtMap.get(platProxyDetailAudit.getMchtId()));
			platProxyDetailAudit.setChanId(channelMap.get(platProxyDetailAudit.getChanId()));
			if (StringUtils.isNotBlank(platProxyDetailAudit.getOperatorId()))
				platProxyDetailAudit.setOperatorId(UserUtils.getUserName(Long.parseLong(platProxyDetailAudit.getOperatorId())));
			if (StringUtils.isNotBlank(platProxyDetailAudit.getAuditorId()))
				platProxyDetailAudit.setAuditorName(UserUtils.getUserName(Long.parseLong(platProxyDetailAudit.getAuditorId())));

		}

		Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/proxy/changeProxyStatusAudit";
	}

	/**
	 * 代付状态修改审批编辑页面
	 */
	@RequestMapping(value = {"changeProxyStatusAuditEdit", ""})
	public String changeProxyStatusAuditEdit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
//        PlatProxyDetail proxyDetail = new PlatProxyDetail();
		String detailId = paramMap.get("detailId");
		String batchId = paramMap.get("batchId");

		PlatProxyDetailAudit proxyDetail = proxyDetailAuditService.queryByKey(detailId);

		//批次信息
		PlatProxyBatch proxyBatch = proxyBatchService.queryByKey(batchId);

		//查询商户列表
		List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");

		if (proxyDetail != null) {
			proxyDetail.setAuditNotes(proxyDetail.getExtend2());
			proxyDetail.setExtend2(mchtMap.get(proxyDetail.getMchtId()));
			proxyDetail.setExtend3(channelMap.get(proxyDetail.getChanId()));
		}
		model.addAttribute("chanInfos", chanInfoList);
		model.addAttribute("mchtInfos", mchtInfos);
		model.addAttribute("proxyBatch", proxyBatch);
		model.addAttribute("proxyDetail", proxyDetail);

		model.addAttribute("paramMap", paramMap);
		return "modules/proxy/changeProxyStatusAuditEdit";
	}


	/**
	 * 代付状态修改审批保存
	 */
	@RequestMapping(value = {"changeProxyStatusAuditSave", ""})
	@RequiresPermissions("mcht:proxy:auditSave")
	public String changeProxyStatusAuditSave(HttpServletRequest request, HttpServletResponse response, Model model,
											 @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {
		String userId = UserUtils.getUser().getId() + "";
		int result = 0;
		int accResult = 0;
		String message, messageType;
		try {
			String detailId = paramMap.get("detailId");
			String auditStatus = paramMap.get("auditStatus");
			PlatProxyDetail proxyDetail = proxyDetailService.queryByKey(detailId);
			String OriStatus = proxyDetail.getPayStatus();
			PlatProxyDetailAudit proxyDetailAudit = proxyDetailAuditService.queryByKey(detailId);
			proxyDetailAudit.setAuditorId(userId);
			proxyDetailAudit.setAuditStatus(auditStatus);
			proxyDetailAudit.setAuditNotes(paramMap.get("auditNotes"));
			proxyDetailAudit.setAuditTime(new Date());
			if (!proxyDetailAudit.getNewPayStatus().equals(OriStatus)) {
				if (AuditEnum.AUDITED.getCode().equals(auditStatus)) {
					//修改明细订单状态
					proxyDetail.setPayStatus(proxyDetailAudit.getNewPayStatus());
					proxyDetail.setUpdateDate(new Date());
					result = proxyDetailService.saveByKey(proxyDetail);
					if(ProxyPayDetailStatusEnum.DF_SUCCESS.getCode().equals(proxyDetailAudit.getNewPayStatus())){
						//如果修改状态为代付成功，则累积交易金额
						ChanMchtPaytype chanMchtPaytype = null;
						if(proxyDetail.getChanMchtPaytypeId()!=null){
							chanMchtPaytype = chanMchtPaytypeService.queryByKey(proxyDetail.getChanMchtPaytypeId());
						}
/*
						tradeDFBatchHandler.accumulateCMPDayTradeAmount(proxyDetail.getAmount()+"",chanMchtPaytype,proxyDetail.getId(),"代付状态修改审批保存");
*/
					}

					//修改批次订单状态
					if (result == 1) {

						int sucCount = 0;
						int failCount = 0;
						BigDecimal sucAmount = BigDecimal.ZERO;
						BigDecimal failAmount = BigDecimal.ZERO;

						PlatProxyBatch proxyBatch = proxyBatchService.queryByKey(proxyDetail.getPlatBatchId());
						PlatProxyDetail detailSearch = new PlatProxyDetail();
						detailSearch.setPlatBatchId(proxyDetail.getPlatBatchId());
						List<PlatProxyDetail> details = proxyDetailService.list(detailSearch);
						boolean done = false;
						for (PlatProxyDetail detail : details) {
							if (ProxyPayDetailStatusEnum.DF_FAIL.getCode().equals(detail.getPayStatus()) ||
									ProxyPayDetailStatusEnum.DF_SUCCESS.getCode().equals(detail.getPayStatus())) {
								done = true;
							} else {
								done = false;
								break;
							}
						}
						for (PlatProxyDetail detail : details) {
							if (StringUtils.equals(detail.getPayStatus(), ProxyPayDetailStatusEnum.DF_SUCCESS.getCode())) {
								sucCount++;
								sucAmount = sucAmount.add(detail.getAmount());
							} else if (StringUtils.equals(detail.getPayStatus(), ProxyPayDetailStatusEnum.DF_FAIL.getCode())) {
								failCount++;
								failAmount = failAmount.add(detail.getAmount());
							}
						}
						proxyBatch.setSuccessNum(sucCount);
						proxyBatch.setSuccessAmount(sucAmount);
						proxyBatch.setFailNum(failCount);
						proxyBatch.setFailAmount(failAmount);
						//所有明细状态为完成
						if (done) {
							proxyBatch.setPayStatus(ProxyPayBatchStatusEnum.DF_DONE.getCode());
							proxyBatchService.saveByKey(proxyBatch);
						}

						//代付明细由未完成变为完成，则操作账户，否则提示人工调账
						if (OriStatus.equals(ProxyPayDetailStatusEnum.DF_FAIL.getCode()) ||
								OriStatus.equals(ProxyPayDetailStatusEnum.DF_SUCCESS.getCode())) {
							accResult = 7777;
						} else {
							// 账户缓存数据
							CacheMcht cacheMcht = accountAdminService.queryCacheMcht(proxyDetail.getMchtId());
							CacheMchtAccount cacheMchtAccount = new CacheMchtAccount();
							cacheMchtAccount.setType(Integer.valueOf(MchtAccountTypeEnum.PROXYPAY_ACCOUNT.getCode()));
							cacheMchtAccount.setCacheMcht(cacheMcht);
							cacheMchtAccount.setPlatProxyDetail(proxyDetail);
							logger.info("代付的入账功能（插入CacheMchtAccount）信息为：" + JSONObject.toJSONString(cacheMchtAccount));
							int rs = accountAdminService.insert2redisAccTask(cacheMchtAccount);
							logger.info("代付的入账功能返回结果 rs=" + rs);
						}
						if(proxyBatch.getNotifyUrl()!=null&&!"".equals(proxyBatch.getNotifyUrl())){
							//将代付成功或失败的代付明细插入代付明细异步通知redis队列
							Map<String,Object> detailMap = new HashMap<>();
							detailMap.put("detail",proxyDetail);
							detailMap.put("batchStatus",proxyBatch.getPayStatus());
							detailMap.put("notifyUrl",proxyBatch.getNotifyUrl());
							insert2redisPlatProxyDetail(detailMap);
						}
					}
				}
				proxyDetailAudit.setUpdateDate(new Date());
				result = proxyDetailAuditService.saveByKey(proxyDetailAudit);

				if (result == 1) {
					if (accResult == 7777) {
						message = "保存成功，请手动调账";
					} else {
						message = "保存成功，稍后请核对账户";
					}
					messageType = "success";
				} else {
					message = "保存失败";
					messageType = "error";
				}


			} else {
				message = "代付订单状态与修改状态一致";
				messageType = "error";
			}

		} catch (Exception e) {
			e.printStackTrace();
			message = "保存失败";
			messageType = "error";
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/changeProxyStatusAudit";
	}

	/**
	 * 代付成功或失败将代付明细插入redis队列中
	 */
	protected int insert2redisPlatProxyDetail(Map detailMap) {
		JedisPool pool = null;
		Jedis jedis = null;
		long rs = 0;
		try {
			pool = JedisConnPool.getPool("缓存插入PlatProxyDetail信息");
			jedis = pool.getResource();
			rs = jedis.lpush(IdUtil.REDIS_PROXY_DETAIL_RESULT_NOTIFY_LIST, JSON.toJSONString(detailMap));

			logger.info("缓存插入PlatProxyDetail信息： rsDf = " + rs+",key="+IdUtil.REDIS_PROXY_DETAIL_RESULT_NOTIFY_LIST+",value="+JSON.toJSONString(detailMap));
		} catch (JedisConnectionException je) {
			logger.error("Redis Jedis连接异常：" + je.getMessage());
			je.printStackTrace();
			rs = -1;
		} catch (Exception e) {
			logger.error("<insertData-error>error[" + e.getMessage() + "]</insertData-error>");
			e.printStackTrace();
			rs = -1;
		} finally {
			JedisConnPool.returnResource(pool, jedis, "");
		}
		return (int) rs;
	}

	private void assemblySearch(Map<String, String> paramMap, PlatProxyDetailAudit proxyDetail) {
		//代付通道
		if (StringUtils.isNotBlank(paramMap.get("chanId"))) {
			proxyDetail.setChanId(paramMap.get("chanId"));
		}
		//代付商户
		if (StringUtils.isNotBlank(paramMap.get("mchtId"))) {
			proxyDetail.setMchtId(paramMap.get("mchtId"));
		}
		//明细订单号
		if (StringUtils.isNotBlank(paramMap.get("detailId"))) {
			proxyDetail.setId(paramMap.get("detailId"));
		}
		//代付状态
		if (StringUtils.isNotBlank(paramMap.get("payStatus"))) {
			proxyDetail.setPayStatus(paramMap.get("payStatus"));
		}
		//平台批次号
		if (StringUtils.isNotBlank(paramMap.get("batchId"))) {
			proxyDetail.setPlatBatchId(paramMap.get("batchId"));
		}
		//申请人
		if (StringUtils.isNotBlank(paramMap.get("operatorId"))) {
			proxyDetail.setOperatorId(paramMap.get("operatorId"));
		}
		//审批人
		if (StringUtils.isNotBlank(paramMap.get("auditorId"))) {
			proxyDetail.setAuditorId(paramMap.get("auditorId"));
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
	}
}
