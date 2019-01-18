package com.sys.admin.modules.platform.controller;

import com.sys.admin.common.enums.AdminPayTypeEnum;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.channel.service.ChannelAdminService;
import com.sys.boss.api.service.order.ReportService;
import com.sys.common.util.DateUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.ChanMchtReportStatistics;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("${adminPath}/platform/original/statistice")
public class OriginalReportController extends BaseController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ChannelAdminService channelAdminService;

    @Autowired
    ChanMchtAdminService chanMchtAdminService;

    @RequestMapping(value = "/chanMchtStatistice")
    public String mchtPayTypeOrderQuery(@RequestParam Map<String, String> paramMap, Model model){

        //通道
        List<ChanInfo> chanInfos = channelAdminService.getChannelList(new ChanInfo());
        model.addAttribute("chanInfos", chanInfos);

        //支付方式
        AdminPayTypeEnum[] payTypeList = AdminPayTypeEnum.values();
        model.addAttribute("paymentTypeInfos", payTypeList);

        //商户
        List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
        model.addAttribute("mchtList", mchtList);
        //通道支付方式
        List<ChanMchtFormInfo> chanInfoList = chanMchtAdminService.getChannelListSimple(new ChanMchtFormInfo());

        model.addAttribute("chanInfoList",chanInfoList);

        model.addAttribute("paramMap",paramMap);

        String beginTime =paramMap.get("beginTime");
        String endTime =paramMap.get("endTime");
        if(StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)){
            return "modules/platform/chanMchtStatistice";
        }

        ChanMchtReportStatistics chanMchtReportStatistics = new ChanMchtReportStatistics();
        chanMchtReportStatistics.setBeginTime(beginTime);
        chanMchtReportStatistics.setEndTime(endTime);
        chanMchtReportStatistics.setChanId(paramMap.get("chanCode"));
        chanMchtReportStatistics.setMchtId(paramMap.get("mchtCode"));
        chanMchtReportStatistics.setPayType(StringUtils.isBlank(paramMap.get("payType"))?"":paramMap.get("payType").split(",")[0]);
        chanMchtReportStatistics.setChanMchtPayTypeId(paramMap.get("chanMchtPayTypeId"));
        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        chanMchtReportStatistics.setPageInfo(pageInfo);

        int count =reportService.count(chanMchtReportStatistics);
        if(count == 0){
            return "modules/platform/chanMchtStatistice";
        }

        List<ChanMchtReportStatistics> chanMchtReportStatisticsList =reportService.list(chanMchtReportStatistics);

        if (CollectionUtils.isEmpty(chanMchtReportStatisticsList)) {
            return "modules/platform/chanMchtStatistice";
        }

        Page page = new Page(pageNo, pageInfo.getPageSize(), count, chanMchtReportStatisticsList, true);
        model.addAttribute("page", page);
        model.addAttribute("list",chanMchtReportStatisticsList);
        return "modules/platform/chanMchtStatistice";

    }
}
