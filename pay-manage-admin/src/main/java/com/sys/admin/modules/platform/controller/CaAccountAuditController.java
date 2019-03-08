package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.CaAccountAuditAdminService;
import com.sys.admin.modules.reconciliation.service.ElectronicAdminAccountInfoService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.cache.CacheChanAccount;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.boss.api.entry.cache.CacheOrder;
import com.sys.boss.api.service.trade.service.IDfProducerService;
import com.sys.common.enums.*;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.common.util.QueueUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import com.sys.core.vo.ElectronicAccountVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private ElectronicAccountInfoService electronicAccountInfoService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private CaAccountAuditAdminService caAccountAuditAdminService;
    @Autowired
    private ElectronicAdminAccountInfoService electronicAdminAccountInfoService;
    @Autowired
    private IDfProducerService iDfProducerService;

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
     * @param paramMap
     * @return
     */
    @RequestMapping("/queryRepeatAudits")
    public ModelAndView queryRepeatAudits(@RequestParam Map<String, String> paramMap){
        logger.info("请求参数:"+ JSON.toJSONString(paramMap));
        ModelAndView andView = new ModelAndView();
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainList");
        andView.addObject("vo",paramMap);
        List<CaElectronicAccount> caElectronicAccountList =electronicAdminAccountInfoService.list(new ElectronicAccountVo());
        andView.addObject("electronicAccounts",caElectronicAccountList);
        CaAccountAudit caAccountAudit = new CaAccountAudit();
        caAccountAudit.setType(CaAuditTypeEnum.COMPLAINT_MANAGER.getCode());
        caAccountAudit.setAccountId(paramMap.get("accountId"));
        if(StringUtils.isNotBlank(paramMap.get("createTime")) && StringUtils.isNotBlank(paramMap.get("updateTime"))){
            try {
                caAccountAudit.setCreatedTime(DateUtils.parseDate(paramMap.get("createTime"),"yyyy-MM-dd HH:mm:ss"));
                caAccountAudit.setUpdatedTime(DateUtils.parseDate(paramMap.get("updateTime"),"yyyy-MM-dd HH:mm:ss"));
            }catch (Exception e){
                logger.info("日期格式错误");
            }
        }
        int count =caAccountAuditService.count(caAccountAudit);
        if(count ==0){
            return andView;
        }
        PageInfo pageInfo = new PageInfo();
        if(paramMap.get("pageNo")==null){
            pageInfo.setPageNo(1);
        }else{
            pageInfo.setPageNo(Integer.valueOf(paramMap.get("pageNo")));
        }
        caAccountAudit.setPageInfo(pageInfo);
        List <CaAccountAuditEx>  caAccountAudits =  caAccountAuditService.queryCaAccountAuditEx(caAccountAudit);

        Page page = new Page(caAccountAudit.getPageInfo().getPageNo(),caAccountAudit.getPageInfo().getPageSize(),count,caAccountAudits,true);

        andView.addObject("caAccountAudits", caAccountAudits);
        andView.addObject("page",page);
        andView.addObject("messageType",paramMap.get("messageType"));
        andView.addObject("message",paramMap.get("message"));
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
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainAdd");
        return andView;
    }


    /**
     * 新增投诉订单
     * 2019-02-21 11:09:08
     * @param caAccountAuditEx
     * @return
     */
    @RequestMapping("/doAddRepeatAudits")
    public String doAddRepeatAudits(CaAccountAuditEx caAccountAuditEx,RedirectAttributes redirectAttributes){
        String message, messageType;
        //组装投诉订单参数
        CaAccountAudit caAccountAudit =new CaAccountAudit();
        //重复支付
        if("P".equals(caAccountAuditEx.getComplainType())){
            MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
            mchtGatewayOrder.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
            mchtGatewayOrder.setPlatOrderId(caAccountAuditEx.getSourceDataId());
            mchtGatewayOrder.setSuffix("20"+caAccountAuditEx.getSourceDataId().substring(1,5));
            List<MchtGatewayOrder> mchtGatewayOrderList =mchtGwOrderService.list(mchtGatewayOrder);
            if(mchtGatewayOrderList==null || mchtGatewayOrderList.size()==0){
                message = "原始订单不存在";
                messageType = "error";
                redirectAttributes.addFlashAttribute("messageType", messageType);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
            }
            //
            MchtGatewayOrder mchtGatewayOrder1 =mchtGatewayOrderList.get(0);
            BigDecimal realChanFee =BigDecimal.ZERO ;
            if(mchtGatewayOrder1.getChanRealFeeAmount()!=null && mchtGatewayOrder1.getChanRealFeeRate() !=null){
                //混合
                realChanFee=BigDecimal.valueOf(mchtGatewayOrder1.getAmount())
                        .multiply(mchtGatewayOrder1.getChanRealFeeRate())
                        .add(mchtGatewayOrder1.getChanRealFeeAmount());
            }else if(mchtGatewayOrder1.getChanRealFeeRate()!=null){
                realChanFee =BigDecimal.valueOf(mchtGatewayOrder1.getAmount())
                        .multiply(mchtGatewayOrder1.getChanRealFeeRate());
            }else if(mchtGatewayOrder1.getChanRealFeeAmount()!=null){
                realChanFee =mchtGatewayOrder1.getChanRealFeeAmount();
            }
            //查询通道商户支付方式
            ChanMchtPaytype chanMchtPaytype =chanMchtPaytypeService.queryByKey(mchtGatewayOrder1.getChanMchtPaytypeId());
            //查询电子账户信息
            CaElectronicAccount caElectronicAccount = new CaElectronicAccount();
            caElectronicAccount.setChanCode(chanMchtPaytype.getChanCode());
            caElectronicAccount.setMchtCode(chanMchtPaytype.getMchtCode());
            ElectronicAccountVo reqVo = new ElectronicAccountVo();
            reqVo.setCaElectronicAccount(caElectronicAccount);
            ElectronicAccountVo vo =electronicAccountInfoService.queryBykey(reqVo);

            caAccountAudit.setId(IdUtil.createCaCommonId("0"));
            caAccountAudit.setAccountId(vo.getCaElectronicAccount().getId());
            caAccountAudit.setSourceDataId(caAccountAuditEx.getSourceDataId());
            caAccountAudit.setNewDataId(IdUtil.createPlatOrderId("0"));
            caAccountAudit.setSourceChanDataId(caAccountAuditEx.getSourceChanDataId());
            caAccountAudit.setSourceChanRepeatDataId(caAccountAuditEx.getSourceChanRepeatDataId());
            caAccountAudit.setType(CaAuditTypeEnum.COMPLAINT_MANAGER.getCode());
            caAccountAudit.setAccountType("1");
            caAccountAudit.setAdjustType(AdjustTypeEnum.ADJUST_ADD.getCode());
            caAccountAudit.setAmount(BigDecimal.valueOf(mchtGatewayOrder1.getAmount()));
            caAccountAudit.setFeeAmount(realChanFee);
            caAccountAudit.setAuditStatus(CaAuditEnum.CREATED_NO_AUDIT.getCode());
            caAccountAudit.setCustomerAuditUserid(String.valueOf(UserUtils.getUser().getId()));
            caAccountAudit.setCustomerMsg(caAccountAuditEx.getCustomerMsg());
            caAccountAudit.setComplainTime(new Date());
            caAccountAudit.setCreatedTime(new Date());
            caAccountAuditService.insertAccountAudit(caAccountAudit);
            //代付成功，上游口头通知失败了
        }else{
            PlatProxyDetail platProxyDetail =proxyDetailService.queryByKey(caAccountAuditEx.getSourceDataId());
            if(platProxyDetail==null){
                message = "原始订单不存在";
                messageType = "error";
                redirectAttributes.addFlashAttribute("messageType", messageType);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
            }
            BigDecimal realChanFee =BigDecimal.ZERO ;
            if(platProxyDetail.getChanRealFeeAmount()!=null && platProxyDetail.getChanRealFeeRate() !=null){
                //混合
                realChanFee=platProxyDetail.getAmount()
                        .multiply(platProxyDetail.getChanRealFeeRate())
                        .add(platProxyDetail.getChanRealFeeAmount());
            }else if(platProxyDetail.getChanRealFeeRate()!=null){
                realChanFee =platProxyDetail.getAmount()
                        .multiply(platProxyDetail.getChanRealFeeRate());
            }else if(platProxyDetail.getChanRealFeeAmount()!=null){
                realChanFee =platProxyDetail.getChanRealFeeAmount();
            }
            //查询通道商户支付方式
            ChanMchtPaytype chanMchtPaytype =chanMchtPaytypeService.queryByKey(platProxyDetail.getChanMchtPaytypeId());
            //查询电子账户信息
            CaElectronicAccount caElectronicAccount = new CaElectronicAccount();
            caElectronicAccount.setChanCode(chanMchtPaytype.getChanCode());
            caElectronicAccount.setMchtCode(chanMchtPaytype.getMchtCode());
            ElectronicAccountVo reqVo = new ElectronicAccountVo();
            reqVo.setCaElectronicAccount(caElectronicAccount);
            ElectronicAccountVo vo =electronicAccountInfoService.queryBykey(reqVo);
            //组装投诉订单参数
            caAccountAudit.setId(IdUtil.createCaCommonId("0"));
            caAccountAudit.setAccountId(vo.getCaElectronicAccount().getId());
            caAccountAudit.setSourceDataId(caAccountAuditEx.getSourceDataId());
            caAccountAudit.setSourceChanDataId(caAccountAuditEx.getSourceChanDataId());
            caAccountAudit.setSourceChanRepeatDataId(caAccountAuditEx.getSourceChanRepeatDataId());
            caAccountAudit.setType(CaAuditTypeEnum.COMPLAINT_MANAGER.getCode());
            caAccountAudit.setAccountType("1");
            caAccountAudit.setAdjustType(AdjustTypeEnum.ADJUST_ADD.getCode());
            caAccountAudit.setAmount(platProxyDetail.getAmount());
            caAccountAudit.setFeeAmount(realChanFee);
            caAccountAudit.setAuditStatus(CaAuditEnum.CREATED_NO_AUDIT.getCode());
            caAccountAudit.setCustomerAuditUserid(String.valueOf(UserUtils.getUser().getId()));
            caAccountAudit.setCustomerMsg(caAccountAuditEx.getCustomerMsg());
            caAccountAudit.setComplainTime(new Date());
            caAccountAudit.setCreatedTime(new Date());
            caAccountAuditService.insertAccountAudit(caAccountAudit);
        }
        message = "保存成功";
        messageType = "success";
        redirectAttributes.addFlashAttribute("messageType", messageType);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
    }

    /**
     * 审批详情
     * 2019-02-21 11:09:08
     * @param caAccountAudit
     * @return
     */
    @RequestMapping("/toApproveRepeatAudits")
    public ModelAndView toApproveRepeatAudits(CaAccountAudit caAccountAudit){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainApprove");
        //查询对应审批信息
        CaAccountAudit caAccountAudit1=caAccountAuditService.findAccountAudit(caAccountAudit.getId());
        MchtGatewayOrder mchtGatewayOrder =null;
        if(caAccountAudit1.getSourceDataId().startsWith("P")){
            MchtGatewayOrder mchtGatewayOrderReq = new MchtGatewayOrder();
            mchtGatewayOrderReq.setSuffix("20"+caAccountAudit1.getSourceDataId().substring(1,5));
            mchtGatewayOrderReq.setPlatOrderId(caAccountAudit1.getSourceDataId());
            //查询对应订单信息
            mchtGatewayOrder= mchtGwOrderService.list(mchtGatewayOrderReq).get(0);
        }else{
            PlatProxyDetail platProxyDetail=proxyDetailService.queryByKey(caAccountAudit1.getSourceDataId());
            mchtGatewayOrder = new MchtGatewayOrder();
            mchtGatewayOrder.setPlatOrderId(platProxyDetail.getId());
            mchtGatewayOrder.setMchtCode(platProxyDetail.getMchtId());
            mchtGatewayOrder.setChanOrderId(platProxyDetail.getChannelTradeId());
            mchtGatewayOrder.setChanCode(platProxyDetail.getChanId());
            mchtGatewayOrder.setStatus(platProxyDetail.getPayStatus());
            mchtGatewayOrder.setCreateTime(platProxyDetail.getCreateDate());
            mchtGatewayOrder.setUpdateTime(platProxyDetail.getUpdateDate());
        }

        //查询商户名称
        MchtInfo mchtInfo = merchantService.queryByKey(mchtGatewayOrder.getMchtCode());
        //查询通道名称
        ChanInfo chanInfo =channelService.queryByKey(mchtGatewayOrder.getChanCode());
        andView.addObject("mertName",mchtInfo.getName());
        andView.addObject("chanName",chanInfo.getName());
        andView.addObject("mchtGatewayOrder",mchtGatewayOrder);
        andView.addObject("caAccountAudit",caAccountAudit1);
        return andView;
    }

    /**
     * 审批通过/拒绝
     * 2019-02-21 11:09:08
     * @param caAccountAudit
     * @return
     */
    @RequestMapping("/doApproveRepeatAudits")
    public String doApproveRepeatAudits(CaAccountAudit caAccountAudit,RedirectAttributes redirectAttributes){
        caAccountAudit.setOperateAuditUserid(String.valueOf(UserUtils.getUser().getId()));
        caAccountAudit.setOperateAuditTime(new Date());
        boolean result =false;
        String message, messageType;
        //查询对应审批信息及上游入账信息
        CaAccountAudit caAccountAudit1=caAccountAuditService.findAccountAudit(caAccountAudit.getId());
        if(caAccountAudit1.getSourceDataId().startsWith("P")){
            MchtGatewayOrder mchtGatewayOrderReq = new MchtGatewayOrder();
            mchtGatewayOrderReq.setSuffix("20"+caAccountAudit1.getSourceDataId().substring(1,5));
            mchtGatewayOrderReq.setPlatOrderId(caAccountAudit1.getSourceDataId());
            //查询对应订单信息
            MchtGatewayOrder mchtGatewayOrder = mchtGwOrderService.list(mchtGatewayOrderReq).get(0);
            MchtInfo mchtInfo = merchantService.queryByKey(mchtGatewayOrder.getMchtCode());
            CacheMchtAccount cacheMchtAccount =caAccountAuditAdminService.bulidRedisPayTaskObject(mchtGatewayOrder,mchtInfo,caAccountAudit);
            //商户调账队列
            result =caAccountAuditAdminService.insert2redisAccTask(cacheMchtAccount);
            if(result){
                CacheChanAccount  cacheChanAccount =caAccountAuditAdminService.bulidMqPayTaskObject(mchtGatewayOrder,mchtInfo,caAccountAudit1);
                logger.info("投诉订单调账ID:"+caAccountAudit1.getId()+"入mq");
                iDfProducerService.sendInfo(JSON.toJSONString(cacheChanAccount),QueueUtil.CA_QUEUE);
            }

        }else{
            PlatProxyDetail platProxyDetail=proxyDetailService.queryByKey(caAccountAudit1.getSourceDataId());
            MchtInfo mchtInfo = merchantService.queryByKey(platProxyDetail.getMchtId());
            CacheMchtAccount cacheMchtAccount =caAccountAuditAdminService.bulidRedisProxyTaskObject(platProxyDetail,mchtInfo,caAccountAudit);
            //商户调账队列
            result =caAccountAuditAdminService.insert2redisAccTask(cacheMchtAccount);
            if(result){
                CacheChanAccount  cacheChanAccount =caAccountAuditAdminService.bulidMqProxyObject(platProxyDetail,mchtInfo,caAccountAudit1);
                logger.info("投诉订单调账ID:"+caAccountAudit1.getId()+"入mq");
                iDfProducerService.sendInfo(JSON.toJSONString(cacheChanAccount),QueueUtil.CA_QUEUE);
            }


        }
        //上游入账信息
        if(result){
            result =caAccountAuditService.updateAccountAudit(caAccountAudit);
        }
        if(result){
            message = "保存成功";
            messageType = "success";
        }else{
            message = "保存失败";
            messageType = "error";
        }
        redirectAttributes.addFlashAttribute("messageType", messageType);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
    }

    @InitBinder
    public void initDateFormate(WebDataBinder dataBinder) {
        dataBinder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"),"createTime");
        dataBinder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"),"updateTime");
    }



}
