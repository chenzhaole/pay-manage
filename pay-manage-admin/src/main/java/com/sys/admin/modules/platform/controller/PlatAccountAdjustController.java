package com.sys.admin.modules.platform.controller;


import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.AuditEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatAccountAdjust;
import com.sys.core.service.MchtAccountInfoService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatAccountAdjustService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
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
            int count = platAccountAdjustService.count(platAccountAdjust);

            Page page = new Page(pageInfo.getPageNo(),pageInfo.getPageSize(),count,true);
            model.addAttribute("list",list);
            model.addAttribute("page",page);

            //初始化商户名称
            Map<String,String> mchtMap = Collections3.extractToMap(
                    merchantService.list(new MchtInfo()),"id","name");
            for(PlatAccountAdjust adjust : list){
                adjust.setMchtName(mchtMap.get(adjust.getMchtId()));
            }

            model.addAttribute("list",list);
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
    public String save(PlatAccountAdjust platAccountAdjust){
        Long operatorId =  UserUtils.getUser().getId();
        String operatorName = UserUtils.getUser().getName();
        platAccountAdjust.setCreatorId(operatorId.toString());
        platAccountAdjust.setCreatorName(operatorName);

        platAccountAdjust.setAuditStatus(AuditEnum.AUDITING.getCode());
        platAccountAdjust.setAdjustAmount(platAccountAdjust.getAdjustAmount().multiply(BigDecimal.valueOf(100)));
        platAccountAdjust.setId(IdUtil.createCommonId());
        platAccountAdjust.setUpdateTime(new Date());
        platAccountAdjust.setCreateTime(new Date());
        platAccountAdjustService.create(platAccountAdjust);

        return "redirect:"+ GlobalConfig.getAdminPath()+"/platform/adjust/list";
    }

    /**
     * 调账审批
     */
    @RequestMapping("audit")
    @RequiresPermissions("platform:adjust:audit")
    public String audit(PlatAccountAdjust platAccountAdjust){
        Long operatorId =  UserUtils.getUser().getId();
        String operatorName = UserUtils.getUser().getName();

        platAccountAdjust.setAuditorId(operatorId.toString());
        platAccountAdjust.setAuditorName(operatorName);
        platAccountAdjust.setAuditTime(new Date());
        platAccountAdjust.setUpdateTime(new Date());
        platAccountAdjustService.saveByKey(platAccountAdjust);
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

}
