package com.sys.admin.modules.warning;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.ChanMchtPaytype;
import com.sys.core.dao.dmo.ChanMchtPaytypeSettleAmount;
import com.sys.core.dao.dmo.ChanMchtPaytypeSettleRecord;
import com.sys.core.service.ChanLimitService;
import com.sys.core.service.ChanMchtPaytypeService;
import com.sys.core.service.ChanRecordService;
import com.sys.core.service.ChannelService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping(value = "${adminPath}/warning")
public class Warning{
    @Autowired
    private ChanMchtPaytypeService chanMchtPaytypeService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChanRecordService chanRecordService;
    @Autowired
    private ChanLimitService chanLimitService;

    private static final Logger logger = LoggerFactory.getLogger(Warning.class);

    @RequestMapping(value = "list")
    public String list(ChanMchtPaytypeSettleRecord chanMchtPaytypeSettleRecord,Model model){
        chanMchtPaytypeSettleRecord.setCode(StringUtils.isNotBlank(chanMchtPaytypeSettleRecord.getCode_0())
                ?chanMchtPaytypeSettleRecord.getCode_0()
                : chanMchtPaytypeSettleRecord.getCode_1());
        //通道商户支付方式列表
        List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
        //上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
        model.addAttribute("chanInfoList", chanInfoList);
        model.addAttribute("chanMchtPaytypeList", chanMchtPaytypeList);
        if(chanMchtPaytypeSettleRecord.getPageInfo()==null){
            PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNo(1);
            chanMchtPaytypeSettleRecord.setPageInfo(pageInfo);
        }
        int count = chanRecordService.count(chanMchtPaytypeSettleRecord);
        List<ChanMchtPaytypeSettleRecord> list =chanRecordService.list(chanMchtPaytypeSettleRecord);
        Page page = new Page(chanMchtPaytypeSettleRecord.getPageInfo().getPageNo(),chanMchtPaytypeSettleRecord.getPageInfo().getPageSize(),count,list,true);
        model.addAttribute("page",page);
        model.addAttribute("vo",chanMchtPaytypeSettleRecord);

        return "modules/warning/warning-list";
    }

    @RequestMapping(value = "toAdd")
    public String toAdd(ChanMchtPaytypeSettleRecord chanMchtPaytypeSettleRecord,Model model){
        //通道商户支付方式列表
        List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
        //上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
        model.addAttribute("chanInfoList", chanInfoList);
        model.addAttribute("chanMchtPaytypeList", chanMchtPaytypeList);
        return "modules/warning/warning-add";
    }

    @RequestMapping(value = "doAdd")
    public String doAdd(ChanMchtPaytypeSettleRecord chanMchtPaytypeSettleRecord,Model model){
        logger.info("请求参数:"+JSON.toJSONString(chanMchtPaytypeSettleRecord));
        String name =UserUtils.getUser().getLoginName();
        chanMchtPaytypeSettleRecord.setAmount(chanMchtPaytypeSettleRecord.getAmount().multiply(new BigDecimal("100")));
        chanMchtPaytypeSettleRecord.setOperateAuditUserid(name);
        chanMchtPaytypeSettleRecord.setCreateTime(new Date());
        chanRecordService.insert(chanMchtPaytypeSettleRecord);
        return "redirect:" + GlobalConfig.getAdminPath() + "/warning/list";
    }

    @RequestMapping(value = "settleList")
    public String settleList(ChanMchtPaytypeSettleAmount chanMchtPaytypeSettleAmount, Model model){
        chanMchtPaytypeSettleAmount.setCode(StringUtils.isNotBlank(chanMchtPaytypeSettleAmount.getCode_0())
                ?chanMchtPaytypeSettleAmount.getCode_0()
                : chanMchtPaytypeSettleAmount.getCode_1());
        //通道商户支付方式列表
        List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
        //上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
        model.addAttribute("chanInfoList", chanInfoList);
        model.addAttribute("chanMchtPaytypeList", chanMchtPaytypeList);
        if(chanMchtPaytypeSettleAmount.getPageInfo()==null){
            PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNo(1);
            chanMchtPaytypeSettleAmount.setPageInfo(pageInfo);
        }
        int count = chanLimitService.count(chanMchtPaytypeSettleAmount);
        List<ChanMchtPaytypeSettleAmount> list =chanLimitService.list(chanMchtPaytypeSettleAmount);
        Page page = new Page(chanMchtPaytypeSettleAmount.getPageInfo().getPageNo(),chanMchtPaytypeSettleAmount.getPageInfo().getPageSize(),count,list,true);
        model.addAttribute("page",page);
        model.addAttribute("vo",chanMchtPaytypeSettleAmount);

        return "modules/warning/warning-settle";
    }

}
