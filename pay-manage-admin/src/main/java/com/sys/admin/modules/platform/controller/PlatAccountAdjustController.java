package com.sys.admin.modules.platform.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.bo.PlatAccountAdjustBO;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.common.db.JedisConnPool;
import com.sys.common.enums.AuditEnum;
import com.sys.common.enums.FeeTypeEnum;
import com.sys.common.enums.MchtAccountTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatAccountAdjust;
import com.sys.core.service.MchtAccountInfoService;
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
    private MchtAccountInfoService mchtAccountInfoService;

    @ModelAttribute
    public PlatAccountAdjust get(@RequestParam(required = false) String id){
        PlatAccountAdjust platAccountAdjust;
        if(StringUtils.isNotBlank(id)){
            platAccountAdjust = platAccountAdjustService.queryByKey(id);
        }else{
            platAccountAdjust = new PlatAccountAdjust();
        }
        return platAccountAdjust;
    }

    /**
     * 调账记录列表
     */
    @RequestMapping(value={"list",""})
    @RequiresPermissions("platform:adjust:list")
    public String list(PlatAccountAdjust platAccountAdjust, HttpServletRequest request, Model model){
        try {
            PageInfo pageInfo = new PageInfo();
            platAccountAdjust.setPageInfo(pageInfo);

            if(StringUtils.isNotBlank(request.getParameter("pageNo")))
                pageInfo.setPageNo(Integer.parseInt(request.getParameter("pageNo")));

            if(StringUtils.isNotBlank(request.getParameter("pageSize")))
                pageInfo.setPageSize(Integer.parseInt(request.getParameter("pageSize")));

            if(StringUtils.isNotBlank(request.getParameter("createTime")))
                platAccountAdjust.setCreateTime(DateUtils.parseDate(request.getParameter("createTime")));

            if(StringUtils.isNotBlank(request.getParameter("auditTime")))
                platAccountAdjust.setAuditTime(DateUtils.parseDate(request.getParameter("auditTime")));

            List<PlatAccountAdjust> list = platAccountAdjustService.list(platAccountAdjust);
            List<PlatAccountAdjustBO> showList = new ArrayList<>(list.size());
            int count = platAccountAdjustService.count(platAccountAdjust);

            Page page = new Page(pageInfo.getPageNo(),pageInfo.getPageSize(),count,true);
            model.addAttribute("list",list);
            model.addAttribute("page",page);

            //初始化商户名称
            Map<String,String> mchtMap = Collections3.extractToMap(
                    merchantService.list(new MchtInfo()),"id","name");
            ConvertUtils.register(new DateConverter(null), java.util.Date.class);
            ConvertUtils.register(new BigDecimalConverter(null),BigDecimal.class);
            for(PlatAccountAdjust adjust : list){
                PlatAccountAdjustBO bo = new PlatAccountAdjustBO();
                BeanUtils.copyProperties(bo,adjust);


                bo.setMchtName(mchtMap.get(adjust.getMchtId()));
                showList.add(bo);
            }

            model.addAttribute("list",showList);
            model.addAttribute("page",page);
            model.addAttribute("createTime",request.getParameter("createTime"));
            model.addAttribute("auditTime",request.getParameter("auditTime"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "modules/platform/platAccountAdjustList";
    }

    /**
     * 调账申请页面
     */
    @RequestMapping(value="form")
    @RequiresPermissions("platform:adjust:apply")
    public String form(PlatAccountAdjust platAccountAdjust, HttpServletRequest request, Model model){
        return "modules/platform/platAccountAdjustForm";
    }


    /**
     * 保存调账申请
     */
    @RequestMapping("save")
    @RequiresPermissions("platform:adjust:apply")
    public String save(PlatAccountAdjust platAccountAdjust, RedirectAttributes redirectAttributes){
        Long operatorId =  UserUtils.getUser().getId();
        String operatorName = UserUtils.getUser().getName();
        platAccountAdjust.setCreatorId(operatorId.toString());
        platAccountAdjust.setCreatorName(operatorName);

        platAccountAdjust.setAuditStatus(AuditEnum.AUDITING.getCode());


        if(StringUtils.equals(FeeTypeEnum.FIXED.getCode(),platAccountAdjust.getFeeType())){//固定值
            platAccountAdjust.setAdjustAmount(platAccountAdjust.getAdjustAmount().multiply(BigDecimal.valueOf(100)));
            platAccountAdjust.setFeeAmount(platAccountAdjust.getAdjustAmount());
        }else{
            platAccountAdjust.setAdjustAmount(BigDecimal.valueOf(0));
        }

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
        return "redirect:"+ GlobalConfig.getAdminPath()+"/platform/adjust/list";
    }

    /**
     * 调账审批
     */
    @RequestMapping("audit")
    @RequiresPermissions("platform:adjust:audit")
    public String audit(PlatAccountAdjust platAccountAdjust, RedirectAttributes redirectAttributes){
        Long operatorId =  UserUtils.getUser().getId();
        String operatorName = UserUtils.getUser().getName();

        platAccountAdjust.setAuditorId(operatorId.toString());
        platAccountAdjust.setAuditorName(operatorName);
        platAccountAdjust.setAuditTime(new Date());
        platAccountAdjust.setUpdateTime(new Date());
        int result = platAccountAdjustService.saveByKey(platAccountAdjust);

        String message, messageType;
        if (result == 1) {
            message = "保存成功";
            messageType = "success";

            if (AuditEnum.AUDITED.getCode().equals(platAccountAdjust.getAuditStatus())){

                CacheMchtAccount cacheMchtAccount =  new CacheMchtAccount();

                CacheMcht cacheMcht = new CacheMcht();
                cacheMcht.setMchtId(platAccountAdjust.getMchtId());
                cacheMchtAccount.setCacheMcht(cacheMcht);

                cacheMchtAccount.setType(Integer.valueOf(MchtAccountTypeEnum.ADJUSTMENT_ACCOUNT.getCode()));
                cacheMchtAccount.setPlatAccountAdjust(platAccountAdjust);


                logger.info("调账ID："+platAccountAdjust.getId()+"，调账功能（插入CacheMchtAccount）信息为："+JSONObject.toJSONString(cacheMchtAccount));
                insertMchtAccountInfo2redis(cacheMchtAccount);
            }
        } else {
            message = "保存失败";
            messageType = "error";
        }

        redirectAttributes.addFlashAttribute("messageType", messageType);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:"+ GlobalConfig.getAdminPath()+"/platform/adjust/list";
    }

    /**
     * 查询余额
     */
    @RequestMapping("balance")
    @RequiresPermissions("platform:adjust:apply")
    public void balance(String mchtId,HttpServletResponse response) throws IOException {
        BigDecimal balance = mchtAccountInfoService.queryBalance(mchtId,null);
        balance = balance.divide(BigDecimal.valueOf(100));

        String contentType = "text/plain";
        response.reset();
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(balance.stripTrailingZeros().toPlainString());
    }

    protected void insertMchtAccountInfo2redis(CacheMchtAccount cacheMchtAccount) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = JedisConnPool.getPool("缓存插入cacheMchtAccount信息");
            jedis = pool.getResource();
            long rsPay = jedis.lpush(IdUtil.REDIS_ACCT_MCHT_ACCOUNT_TASK_LIST, JSON.toJSONString(cacheMchtAccount));
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

}
