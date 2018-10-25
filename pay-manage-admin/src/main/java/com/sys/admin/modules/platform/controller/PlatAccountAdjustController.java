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
import com.sys.common.enums.AuditEnum;
import com.sys.common.enums.MchtAccountTypeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
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
import java.math.BigDecimal;
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

}
