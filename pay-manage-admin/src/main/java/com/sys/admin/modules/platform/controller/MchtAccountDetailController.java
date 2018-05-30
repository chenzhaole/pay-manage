package com.sys.admin.modules.platform.controller;

import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.Collections3;
import com.sys.admin.common.web.BaseController;
import com.sys.common.enums.AccOpTypeEnum;
import com.sys.common.enums.AccTradeTypeEnum;
import com.sys.common.util.DateUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatAccountAdjust;
import com.sys.core.service.MchtAccountDetailService;
import com.sys.core.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 账务明细controller
 */
@Controller
@RequestMapping("${adminPath}/platform/accountDetail")
public class MchtAccountDetailController extends BaseController{
    @Autowired
    private MchtAccountDetailService mchtAccountDetailService;
    @Autowired
    private MerchantService merchantService;

    /**
     * 账务明细列表
     */
    @RequestMapping(value={"list",""})
    @RequiresPermissions("platform:accountDetail:list")
    public String list(MchtAccountDetail mchtAccountDetail, HttpServletRequest request, Model model){
        PageInfo pageInfo = new PageInfo();
        mchtAccountDetail.setPageInfo(pageInfo);

        if(StringUtils.isNotBlank(request.getParameter("pageNo")))
            pageInfo.setPageNo(Integer.parseInt(request.getParameter("pageNo")));

        if(StringUtils.isNotBlank(request.getParameter("pageSize")))
            pageInfo.setPageSize(Integer.parseInt(request.getParameter("pageSize")));

        if(StringUtils.isNotBlank(request.getParameter("createTime"))){
            mchtAccountDetail.setSuffix(request.getParameter("createTime").replace("-","").substring(0,6));
            mchtAccountDetail.setCreateTime(DateUtils.parseDate(request.getParameter("createTime")));
        }else{
            mchtAccountDetail.setSuffix(DateUtils.formatDate(new Date(),"yyyyMM"));
        }

        List<MchtAccountDetail> list = mchtAccountDetailService.list(mchtAccountDetail);
        int count = mchtAccountDetailService.count(mchtAccountDetail);

        //初始化商户名称
        Map<String,String> mchtMap = com.sys.common.util.Collections3.extractToMap(
                merchantService.list(new MchtInfo()),"id","name");
        for(MchtAccountDetail detail : list){
            detail.setMchtName(mchtMap.get(detail.getMchtId()));
            detail.setTradeType(AccTradeTypeEnum.toEnum(detail.getTradeType()).getDesc());
            detail.setOpType(AccOpTypeEnum.toEnum(detail.getOpType()).getDesc());
        }

        Page page = new Page(pageInfo.getPageNo(),pageInfo.getPageSize(),count,true);


        model.addAttribute("list",list);
        model.addAttribute("page",page);
        model.addAttribute("createTime",request.getParameter("createTime"));
        return "modules/platform/mchtAccountDetailList";
    }
}
