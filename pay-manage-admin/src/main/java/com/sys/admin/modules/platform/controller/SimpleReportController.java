package com.sys.admin.modules.platform.controller;

import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.boss.api.service.order.SimpleReportService;
import com.sys.common.enums.PayTypeEnum;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtOrderStatisticsMinutes;
import com.sys.core.dao.dmo.MchtPaytypeOrderStatisticsMinutes;
import com.sys.core.dao.dmo.MchtPlatOrderStatisticsMinutes;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("${adminPath}/platform/statistice")
public class SimpleReportController extends BaseController {
    @Autowired
    private SimpleReportService simpleReportService;

    @RequestMapping(value = "/mchtOrderStatistice")
    public String mchtOrderQuery(@RequestParam Map<String, String> paramMap, Model model){
        MchtOrderStatisticsMinutes mchtOrderStatisticsMinutes = new MchtOrderStatisticsMinutes();
        mchtOrderStatisticsMinutes.setStatisticeTime(paramMap.get("statisticeTime"));

        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        mchtOrderStatisticsMinutes.setPageInfo(pageInfo);
        int count =simpleReportService.count(mchtOrderStatisticsMinutes);
        if(count == 0){
            return "modules/platform/mchtOrderStatistice";
        }

        List<MchtOrderStatisticsMinutes> mchtOrderStatisticsMinutesList =simpleReportService.list(mchtOrderStatisticsMinutes);

        if (CollectionUtils.isEmpty(mchtOrderStatisticsMinutesList)) {
            return "modules/platform/mchtOrderStatistice";
        }

        List<MchtOrderStatisticsMinutes> mchtOrderStatisticsMinutesList1= new ArrayList<>();
        for(MchtOrderStatisticsMinutes mchtOrderStatisticsMinutess:mchtOrderStatisticsMinutesList){
            mchtOrderStatisticsMinutess.setMchtRequestAmount(mchtOrderStatisticsMinutess.getMchtRequestAmount().divide(new BigDecimal("100").setScale(2,BigDecimal.ROUND_HALF_UP)));
            mchtOrderStatisticsMinutess.setMchtSuccessAmount(mchtOrderStatisticsMinutess.getMchtSuccessAmount().divide(new BigDecimal("100").setScale(2,BigDecimal.ROUND_HALF_UP)));
            mchtOrderStatisticsMinutesList1.add(mchtOrderStatisticsMinutess);

        }
        Page page = new Page(pageNo, pageInfo.getPageSize(), count, mchtOrderStatisticsMinutesList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap",paramMap);
        model.addAttribute("list",mchtOrderStatisticsMinutesList1);
        return "modules/platform/mchtOrderStatistice";

    }

    @RequestMapping(value = "/mchtPayTypeOrderStatistice")
    public String mchtPayTypeOrderQuery(@RequestParam Map<String, String> paramMap, Model model){
        MchtPaytypeOrderStatisticsMinutes mchtPaytypeOrderStatisticsMinutes = new MchtPaytypeOrderStatisticsMinutes();
        mchtPaytypeOrderStatisticsMinutes.setStatisticeTime(paramMap.get("statisticeTime"));

        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        mchtPaytypeOrderStatisticsMinutes.setPageInfo(pageInfo);

        int count =simpleReportService.count(mchtPaytypeOrderStatisticsMinutes);
        if(count == 0){
            return "modules/platform/mchtPayTypeOrderStatistice";
        }

        List<MchtPaytypeOrderStatisticsMinutes> mchtOrderStatisticsMinutesList =simpleReportService.list(mchtPaytypeOrderStatisticsMinutes);

        if (CollectionUtils.isEmpty(mchtOrderStatisticsMinutesList)) {
            return "modules/platform/mchtPayTypeOrderStatistice";
        }
        List<MchtPaytypeOrderStatisticsMinutes> mchtPaytypeOrderStatisticsMinutess =new ArrayList<>();
        for(MchtPaytypeOrderStatisticsMinutes mchtPaytypeOrderStatisticsMinute:mchtOrderStatisticsMinutesList){
            mchtPaytypeOrderStatisticsMinute.setPayTypeName(PayTypeEnum.toEnum(mchtPaytypeOrderStatisticsMinute.getPayType()).getDesc());
            mchtPaytypeOrderStatisticsMinute.setMchtRequestAmount(mchtPaytypeOrderStatisticsMinute.getMchtRequestAmount().divide(new BigDecimal("100").setScale(2,BigDecimal.ROUND_HALF_UP)));
            mchtPaytypeOrderStatisticsMinute.setMchtSuccessAmount(mchtPaytypeOrderStatisticsMinute.getMchtSuccessAmount().divide(new BigDecimal("100").setScale(2,BigDecimal.ROUND_HALF_UP)));
            mchtPaytypeOrderStatisticsMinutess.add(mchtPaytypeOrderStatisticsMinute);

        }
        Page page = new Page(pageNo, pageInfo.getPageSize(), count, mchtOrderStatisticsMinutesList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap",paramMap);
        model.addAttribute("list",mchtPaytypeOrderStatisticsMinutess);
        return "modules/platform/mchtPayTypeOrderStatistice";

    }

    @RequestMapping(value = "/mchtPlatStatistice")
    public String orderQuery(@RequestParam Map<String, String> paramMap, Model model){
        MchtPlatOrderStatisticsMinutes mchtPlatOrderStatisticsMinutes = new MchtPlatOrderStatisticsMinutes();
        mchtPlatOrderStatisticsMinutes.setStatisticeTime(paramMap.get("statisticeTime"));

        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        mchtPlatOrderStatisticsMinutes.setPageInfo(pageInfo);

        int count =simpleReportService.count(mchtPlatOrderStatisticsMinutes);
        if(count == 0){
            return "modules/platform/mchtPlatStatistice";
        }

        List<MchtPlatOrderStatisticsMinutes> mchtOrderStatisticsMinutesList =simpleReportService.list(mchtPlatOrderStatisticsMinutes);

        if (CollectionUtils.isEmpty(mchtOrderStatisticsMinutesList)) {
            return "modules/platform/mchtPlatStatistice";
        }
        List<MchtPlatOrderStatisticsMinutes> mchtPlatOrderStatisticsMinutess =new ArrayList<>();
        for(MchtPlatOrderStatisticsMinutes mchtPlatOrderStatisticsMinutes1 :mchtOrderStatisticsMinutesList){
            mchtPlatOrderStatisticsMinutes1.setMchtRequestAmount(mchtPlatOrderStatisticsMinutes1.getMchtRequestAmount().divide(new BigDecimal("100").setScale(2,BigDecimal.ROUND_HALF_UP)));
            mchtPlatOrderStatisticsMinutes1.setMchtSuccessAmount(mchtPlatOrderStatisticsMinutes1.getMchtSuccessAmount().divide(new BigDecimal("100").setScale(2,BigDecimal.ROUND_HALF_UP)));
            mchtPlatOrderStatisticsMinutess.add(mchtPlatOrderStatisticsMinutes1);
        }
        Page page = new Page(pageNo, pageInfo.getPageSize(), count, mchtOrderStatisticsMinutesList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap",paramMap);
        model.addAttribute("list",mchtPlatOrderStatisticsMinutess);
        return "modules/platform/mchtPlatStatistice";

    }
}
