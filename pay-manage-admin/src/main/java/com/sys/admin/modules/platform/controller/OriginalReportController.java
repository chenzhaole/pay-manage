package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.*;

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

    private static final Logger logger =LoggerFactory.getLogger(OriginalReportController.class);

    @RequestMapping(value = "/chanMchtStatistice")
    public String mchtPayTypeOrderQuery(@RequestParam Map<String, String> paramMap, Model model){

        logger.info("paramMap===="+JSON.toJSONString(paramMap));

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
        //查询所有符合条件的数据
        List<ChanMchtReportStatistics> sumChanMchtReportStatisticsList =reportService.listSum(chanMchtReportStatistics);
        model.addAttribute("sumObject",sumChanMchtReportStatisticsList.get(0));
        Page page = new Page(pageNo, pageInfo.getPageSize(), count, chanMchtReportStatisticsList, true);
        model.addAttribute("page", page);
        model.addAttribute("list",chanMchtReportStatisticsList);
        return "modules/platform/chanMchtStatistice";

    }
    @RequestMapping(value = "/chanMchtEcharts")
    public String mchtEchartQuery(@RequestParam Map<String, String> paramMap, Model model) {
        logger.info("paramMap===="+JSON.toJSONString(paramMap));
        String paramTime=null;
        String precent = null;
        BigDecimal chanCommitNum=null;
        BigDecimal chanCommitSuccNum=null;
        String beginDate =paramMap.get("beginDate");
        String endDate =paramMap.get("endDate");
        String beginTime =paramMap.get("beginTime");
        String endTime =paramMap.get("endTime");
        String echartsType=paramMap.get("echartsType");

        model.addAttribute("paramMap",paramMap);
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

        if(StringUtils.isBlank(echartsType)){
            return "modules/platform/chanMchtEcharts";
        }
        Calendar beginTimeCalendar =DateUtils.toCalendar(DateUtils.parseDate(beginTime,"HHmmss"));
        int m =beginTimeCalendar.get(Calendar.MINUTE);
        if(m>0 && m<15){
            beginTimeCalendar.set(Calendar.MINUTE,15);
        }else if(m>15 && m<30){
            beginTimeCalendar.set(Calendar.MINUTE,30);
        }else if(m>30 && m<45){
            beginTimeCalendar.set(Calendar.MINUTE,45);
        }else if(m>45){
            beginTimeCalendar.add(Calendar.DAY_OF_MONTH,1);
            beginTimeCalendar.set(Calendar.MINUTE,00);
        }
        Calendar endTimeCalendar =DateUtils.toCalendar(DateUtils.parseDate(endTime,"HHmmss"));
        m =endTimeCalendar.get(Calendar.MINUTE);
        if(m>0 && m<15){
            endTimeCalendar.set(Calendar.MINUTE,00);
        }else if(m>15 && m<30){
            endTimeCalendar.set(Calendar.MINUTE,15);
        }else if(m>30 && m<45){
            endTimeCalendar.set(Calendar.MINUTE,30);
        }else if(m>45){
            endTimeCalendar.set(Calendar.MINUTE,45);
        }
        List<String> xAxis =new ArrayList<>();
        while(beginTimeCalendar.getTime().getTime()<=endTimeCalendar.getTime().getTime()){
            paramTime =DateUtils.formatDate(beginTimeCalendar.getTime(),"HHmm");
            xAxis.add(paramTime);
            beginTimeCalendar.add(Calendar.MINUTE,15);
        }

        String[] echartsTypeArray =echartsType.split(",");
        echartsType =echartsTypeArray[0];
        String echartsDesc =echartsTypeArray[1];
        Calendar beginDateCalendar =DateUtils.toCalendar(DateUtils.parseDate(beginDate,"yyyyMMdd"));
        Calendar endDateCalendar =DateUtils.toCalendar(DateUtils.parseDate(endDate,"yyyyMMdd"));
        ChanMchtReportStatistics chanMchtReportStatistics = new ChanMchtReportStatistics();
        chanMchtReportStatistics.setChanId(paramMap.get("chanCode"));
        chanMchtReportStatistics.setMchtId(paramMap.get("mchtCode"));
        chanMchtReportStatistics.setPayType(StringUtils.isBlank(paramMap.get("payType"))?"":paramMap.get("payType").split(",")[0]);
        chanMchtReportStatistics.setChanMchtPayTypeId(paramMap.get("chanMchtPayTypeId"));
        List<ChanMchtReportStatistics> chanMchtReportStatisticsList=null;
        List<List<String>> series = new ArrayList<>();
        List<String> data =new ArrayList<>();
        while(beginDateCalendar.getTime().getTime()<=endDateCalendar.getTime().getTime()){
            paramTime =DateUtils.formatDate(beginDateCalendar.getTime(),"yyyyMMdd");
            chanMchtReportStatistics.setBeginTime(paramTime+beginTime);
            chanMchtReportStatistics.setEndTime(paramTime+endTime);
            chanMchtReportStatisticsList=reportService.list(chanMchtReportStatistics);
            if(chanMchtReportStatisticsList!=null && chanMchtReportStatisticsList.size()>0){
                data.add(paramTime);
            }else{
                beginDateCalendar.add(Calendar.DAY_OF_MONTH,1);
                continue;
            }
            List<String> seriesData =new ArrayList<>();
            int j =0;//纵坐标数据点
            ChanMchtReportStatistics chanMchtReportStatistics1=null;
            for(String x:xAxis){
                try {
                    chanMchtReportStatistics1 =chanMchtReportStatisticsList.get(j);
                }catch (Exception e){
                    seriesData.add("0");
                    continue;
                }
                if(x.equals(chanMchtReportStatistics1.getStatisticsTime().substring(8,12))){
                    j++;
                    if("0".equals(echartsType)){
                        seriesData.add(String.valueOf(chanMchtReportStatistics1.getMchtReqNum()));
                    }else if("1".equals(echartsType)){
                        chanCommitNum =chanMchtReportStatistics1.getMchtReqNum();
                        if(chanCommitNum.compareTo(BigDecimal.ZERO)==0){
                            seriesData.add("0");
                            continue;
                        }
                        precent =String.valueOf(chanMchtReportStatistics1.getPaySuccNum().divide(chanMchtReportStatistics1.getMchtReqNum(),2).setScale(2,BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal("100")));
                        seriesData.add(precent);
                    }else if("2".equals(echartsType)){
                        chanCommitSuccNum =chanMchtReportStatistics1.getChanCommitSuccNum();
                        if(chanCommitSuccNum.compareTo(BigDecimal.ZERO)==0){
                            seriesData.add("0");
                            continue;
                        }
                        precent =String.valueOf(chanMchtReportStatistics1.getPaySuccNum().divide(chanMchtReportStatistics1.getChanCommitSuccNum(),2).setScale(2,BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal("100")));
                        seriesData.add(precent);
                    }
                }else{
                    seriesData.add("0");
                }
            }
            series.add(seriesData);
            beginDateCalendar.add(Calendar.DAY_OF_MONTH,1);
        };
        model.addAttribute("echartsType",echartsType);
        model.addAttribute("echartsDesc",echartsDesc);
        model.addAttribute("xAxis",xAxis.toArray());
        model.addAttribute("data",data.toArray());
        model.addAttribute("series",series.toArray());

        return "modules/platform/chanMchtEcharts";
    }
}
