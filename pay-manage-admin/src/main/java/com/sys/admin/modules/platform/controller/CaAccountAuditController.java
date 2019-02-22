package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.web.BaseController;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.CaAccountAudit;
import com.sys.core.service.CaAccountAuditService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("${adminPath}/caAccountAudit")
public class CaAccountAuditController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(CaAccountAuditController.class);

    @Autowired
    private CaAccountAuditService caAccountAuditService;

    /**
     * 查询上游对账审批详情
     * 2019-02-21 11:01:51
     * @return
     */
    @RequestMapping("/findCaAuditEnum")
    public ModelAndView findCaAccountAuditDetail(String keyId){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("");

        CaAccountAudit accountAudit = null;
        logger.info("查询上游对账审批详情, 请求参数keyId为:" + keyId);
        if(StringUtils.isEmpty(keyId)){
            logger.info("查询上游对账审批详情, 请求参数keyId为空.");
             return andView;
        }
        accountAudit = caAccountAuditService.findAccountAudit(keyId);
        andView.addObject("accountAudit", accountAudit);
        return andView;
    }


    /**
     * 查询上游对账集合信息按类型
     * 2019-02-21 11:09:08
     * @param paramMap
     * @return
     */
    @RequestMapping("/queryCaAccountAudits")
    public ModelAndView queryCaAccountAudits(@RequestParam Map<String, String> paramMap){
        List<CaAccountAudit> caAccountAudits = new ArrayList<>();
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/upstreamaudit/payForAnotherAdjustmentAccount");


        if(StringUtils.isEmpty(paramMap.get("type"))){
            logger.info("查询上游对账集合信息按类型,类型信息为空.");
            andView.addObject("caAccountAudits", caAccountAudits);
            return andView;
        }
        CaAccountAudit caAccountAudit  = new CaAccountAudit();
        //分页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        caAccountAudit.setPageInfo(pageInfo);


        caAccountAudit.setType(paramMap.get("type"));
        caAccountAudit.setCustomerStartAuditTime(paramMap.get("customerStartAuditTime"));
        caAccountAudit.setCustomerStartAuditTime(paramMap.get("customerEndAuditTime"));

        caAccountAudits =  caAccountAuditService.queryCaAccountAudit(caAccountAudit);
        andView.addObject("caAccountAudits", caAccountAudits);
        return andView;
    }


    /**
     * 添加上游对账审批信息
     * 2019-02-21 11:31:14
     * @return
     */
    @RequestMapping("/insertCaAccountAudit")
    public ModelAndView insertCaAccountAudit(Map<String, String> paramMap){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("");

        if(StringUtils.isEmpty(paramMap.get("type"))){
            logger.info("添加审批信息类型为空,参数为:" + JSONObject.toJSONString(paramMap));
            return andView;
        }
        CaAccountAudit caAccountAudit  = new CaAccountAudit();
        caAccountAudit.setType(paramMap.get("type"));
        caAccountAudit.setSourceDataId(paramMap.get("sourceDataId"));
        caAccountAudit.setNewDataId(paramMap.get("newDataId"));
        caAccountAudit.setSourceChanDataId(paramMap.get("sourceChanDataId("));
        caAccountAudit.setSourceChanRepeatDataId(paramMap.get("sourceChanRepeatDataId"));
        caAccountAudit.setAmount(new BigDecimal(paramMap.get("amount")));
        boolean backFlag = caAccountAuditService.insertAccountAudit(caAccountAudit);

        return andView;
    }


    /**
     * 修改上游对账审批信息
     * 2019-02-21 11:44:08
     * @return
     */
    public ModelAndView updateCaAccountAuditById(Map<String, String> paramMap){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("");

        if(StringUtils.isEmpty(paramMap.get("type"))){
            logger.info("添加审批信息类型为空,参数为:" + JSONObject.toJSONString(paramMap));
            return andView;
        }
        CaAccountAudit caAccountAudit  = new CaAccountAudit();
        caAccountAudit.setType(paramMap.get("type"));
        caAccountAudit.setId(paramMap.get("id"));
        caAccountAudit.setAuditStatus(paramMap.get("auditStatus"));

        boolean backFlag = caAccountAuditService.updateAccountAudit(caAccountAudit);
        return andView;
    }



}
