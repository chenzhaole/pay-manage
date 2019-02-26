package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.common.enums.PayStatusEnum;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private MchtGwOrderService mchtGwOrderService;
    @Autowired
    private ProxyDetailService proxyDetailService;
    @Autowired
    private ChanMchtPaytypeService chanMchtPaytypeService;
    @Autowired
    private

    /**
     * 查询上游对账审批详情
     * 2019-02-21 11:01:51
     * @return
     */
    @RequestMapping("/findCaAccountAuditDetail")
    public ModelAndView findCaAccountAuditDetail(String keyId){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/upstreamaudit/auditOperateDzOrder");

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
    public String insertCaAccountAudit(HttpServletRequest request,  @RequestParam Map<String, String> paramMap){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("");

        if(StringUtils.isEmpty(paramMap.get("type"))){
            logger.info("添加审批信息类型为空,参数为:" + JSONObject.toJSONString(paramMap));
            return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
        }
        CaAccountAudit caAccountAudit  = new CaAccountAudit();
        caAccountAudit.setAccountId(paramMap.get("accountId"));
        caAccountAudit.setType(paramMap.get("type"));
        caAccountAudit.setAdjustType(paramMap.get("adjustType"));
        caAccountAudit.setSourceDataId(paramMap.get("sourceDataId"));
        caAccountAudit.setNewDataId(paramMap.get("newDataId"));
        caAccountAudit.setSourceChanDataId(paramMap.get("sourceChanDataId"));
        caAccountAudit.setSourceChanRepeatDataId(paramMap.get("sourceChanRepeatDataId"));
        caAccountAudit.setAmount(new BigDecimal(paramMap.get("amount")));
        caAccountAudit.setCustomerMsg(paramMap.get("customerMsg"));
        caAccountAudit.setAccountType(paramMap.get("accountType"));
        boolean backFlag = caAccountAuditService.insertAccountAudit(caAccountAudit);

        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
    }


    /**
     * 修改上游对账审批信息
     * 2019-02-21 11:44:08
     * @return
     */
    @RequestMapping("/updateCaAccountAuditById")
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


    /**
     * 跳转代付调账页面
     * 2019-02-22 17:10:25
     * @return
     */
    @RequestMapping("/toPayForAnotherAdjustment")
    public ModelAndView toAddAccountAudit(){
        ModelAndView andView = new ModelAndView();

        List<CaElectronicAccount>  electronicAccounts = caAccountAuditService.queryCaElectronicAccountByExample(new CaElectronicAccount());
        andView.addObject("electronicAccounts", electronicAccounts);

        andView.setViewName("modules/upstreamaudit/toPayForAnotherAdjustment");
        return andView;
    }

    /**
     * 查询投诉订单list
     * 2019-02-21 11:09:08
     * @param caAccountAudit
     * @return
     */
    @RequestMapping("/queryRepeatAudits")
    public ModelAndView queryRepeatAudits(CaAccountAudit caAccountAudit){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainList.jsp");
        int count =caAccountAuditService.count(caAccountAudit);
        if(count ==0){
            return andView;
        }
        if(caAccountAudit.getPageInfo()==null){
            PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNo(1);
            caAccountAudit.setPageInfo(pageInfo);
        }
        List <CaAccountAuditEx>  caAccountAudits =  caAccountAuditService.queryCaAccountAuditEx(caAccountAudit);

        Page page = new Page(caAccountAudit.getPageInfo().getPageNo(),caAccountAudit.getPageInfo().getPageSize(),count,caAccountAudits,true);
        andView.addObject("caAccountAudits", caAccountAudits);
        andView.addObject("page",page);
        return andView;
    }

    /**
     * 新增投诉订单
     * 2019-02-21 11:09:08
     * @param caAccountAudit
     * @return
     */
    @RequestMapping("/toAddRepeatAudits")
    public ModelAndView toAddRepeatAudits(CaAccountAudit caAccountAudit){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainAdd.jsp");
        return andView;
    }


    /**
     * 新增投诉订单
     * 2019-02-21 11:09:08
     * @param caAccountAuditEx
     * @return
     */
    @RequestMapping("/doAddRepeatAudits")
    public String doAddRepeatAudits(CaAccountAuditEx caAccountAuditEx){
        ModelAndView andView = new ModelAndView();
        if("P".equals(caAccountAuditEx.getComplainType())){
            MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
            mchtGatewayOrder.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
            mchtGatewayOrder.setPlatOrderId(caAccountAuditEx.getSourceDataId());
            mchtGatewayOrder.setSuffix("20"+caAccountAuditEx.getSourceDataId().substring(1,5));
            List<MchtGatewayOrder> mchtGatewayOrderList =mchtGwOrderService.list(mchtGatewayOrder);
            if(mchtGatewayOrderList==null || mchtGatewayOrderList.size()==0){
                return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
            }
            //
            MchtGatewayOrder mchtGatewayOrder1 =mchtGatewayOrderList.get(0);
            //查询通道商户支付方式
            ChanMchtPaytype chanMchtPaytype =chanMchtPaytypeService.queryByKey(mchtGatewayOrder1.getChanMchtPaytypeId());

            CaAccountAudit caAccountAudit =new CaAccountAudit();
            //caAccountAudit.


        }else{

        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
    }
}
