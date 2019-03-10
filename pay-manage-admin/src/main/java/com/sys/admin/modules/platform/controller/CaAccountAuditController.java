package com.sys.admin.modules.platform.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.*;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
import com.sys.admin.modules.platform.service.CaAccountAuditAdminService;
import com.sys.admin.modules.reconciliation.service.ElectronicAdminAccountInfoService;
import com.sys.boss.api.entry.cache.CacheChanAccount;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.boss.api.service.trade.service.IDfProducerService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sun.misc.BASE64Decoder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private JedisPool jedisPool;
    @Autowired
    private ElectronicAccountInfoService electronicAccountInfoService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private PublicAccountInfoService publicAccountInfoService;
    @Autowired
    private CaAccountAuditAdminService caAccountAuditAdminService;
    @Autowired
    private ElectronicAdminAccountInfoService electronicAdminAccountInfoService;
    @Autowired
    private IDfProducerService iDfProducerService;

    /**
     * 查询上游对账审批详情
     * 2019-02-21 11:01:51
     *
     * @return
     */
    @RequestMapping("/findCaAccountAuditDetail")
    public ModelAndView findCaAccountAuditDetail(String id) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/upstreamaudit/auditOperateElectronicAccountDzOrder");

        CaAccountAudit accountAudit = null;
        logger.info("查询上游对账审批详情, 请求参数keyId为:" + id);
        if (StringUtils.isEmpty(id)) {
            logger.info("查询上游对账审批详情, 请求参数keyId为空.");
            return andView;
        }
        accountAudit = caAccountAuditService.findAccountAudit(id);

        andView.addObject("queryFlag", "audit");
        andView.addObject("accountAudit", accountAudit);
        return andView;
    }


    /**
     * 查询上游对账集合信息按类型
     * 2019-02-21 11:09:08
     *
     * @param paramMap
     * @return
     */
    @RequestMapping("/queryCaAccountAudits")
    public ModelAndView queryCaAccountAudits(@RequestParam Map<String, String> paramMap) {
        List<CaAccountAudit> caAccountAudits = new ArrayList<>();
        ModelAndView andView = new ModelAndView();

        if (StringUtils.isEmpty(paramMap.get("type"))) {
            logger.info("查询上游对账集合信息按类型,类型信息为空.");
            andView.addObject("caAccountAudits", caAccountAudits);
            return andView;
        }

        //电子账户信息
        List<CaElectronicAccount> electronicAccounts = caAccountAuditService.queryCaElectronicAccountByExample(new CaElectronicAccount());
        andView.addObject("electronicAccounts", electronicAccounts);

        //对公账户
        List<PublicAccountInfo> publicAccountInfos = getPublicAccountInfos();
        andView.addObject("publicAccountInfos", publicAccountInfos);


        CaAccountAudit caAccountAudit = new CaAccountAudit();
        //1:投诉管理2:公户充值管理3:上游结算管理4:代付业务手动调账管理5:支付业务手动调账管理
        if (paramMap.get("type").equals("2")) {
            andView.setViewName("modules/upstreamaudit/pubAccList");
            caAccountAudit = buildAdjustDfAndZf(paramMap);
        } else if (paramMap.get("type").equals("3")) {
            andView.setViewName("modules/upstreamaudit/upstreamSettlementList");
            caAccountAudit = buildAdjustDfAndZf(paramMap);
        } else if (paramMap.get("type").equals("4") || paramMap.get("type").equals("5")) {
            andView.setViewName("modules/upstreamaudit/payForAnotherAdjustmentAccount");
            caAccountAudit = buildAdjustPubRecharge(paramMap);
        }

        int count = caAccountAuditService.count(caAccountAudit);
        if (count == 0) {
            andView.addObject("paramMap", paramMap);
            return andView;
        }
        //分页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        caAccountAudit.setPageInfo(pageInfo);

        caAccountAudits = caAccountAuditService.queryCaAccountAudit(caAccountAudit);

        caAccountAudits = buildCaAccountAudits(caAccountAudits);

        andView.addObject("caAccountAudits", caAccountAudits);
        Page page = new Page(pageNo, pageInfo.getPageSize(), count, electronicAccounts, true);
        andView.addObject("page", page);
        andView.addObject("orderCount", count);
        andView.addObject("paramMap", paramMap);


        return andView;
    }


    /**
     * 添加上游对账审批信息
     * 2019-02-21 11:31:14
     *
     * @return
     */
    @RequestMapping("/insertCaAccountAudit")
    public String insertCaAccountAudit(HttpServletRequest request, @RequestParam Map<String, String> paramMap,
                                       @RequestParam(value = "proofImage", required = false) String proofImage) {
        if (StringUtils.isEmpty(paramMap.get("type"))) {
            logger.info("添加审批信息类型为空,参数为:" + JSONObject.toJSONString(paramMap));
            return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
        }
        String imgUrl = convertImageFromBase64(proofImage, request);
        CaAccountAudit caAccountAudit = new CaAccountAudit();
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
        caAccountAudit.setPicUrl(imgUrl);

        caAccountAudit.setCustomerAuditUserid(UserUtils.getUser().getId().toString());
        //如果是公户充值  设置
        if (caAccountAudit.getType() != null && CaAuditTypeEnum.PUB_ACC_RECHARGE_MANAGER.getCode().equals(caAccountAudit.getType())) {
            if (StringUtils.isNotEmpty(paramMap.get("type")) && paramMap.get("type").equals("1")) {
                if (StringUtils.isNotEmpty(paramMap.get("sourceDataId"))) {
                    caAccountAudit.setAccountId(paramMap.get("sourceDataId"));
                }
            } else if (StringUtils.isNotEmpty(paramMap.get("type")) && paramMap.get("type").equals("2")) {
                if (StringUtils.isNotEmpty(paramMap.get("newDataId"))) {
                    caAccountAudit.setAccountId(paramMap.get("newDataId"));
                }
            }
        }
        //如果是上游结算  设置
        if (caAccountAudit.getType() != null && CaAuditTypeEnum.SETTLEMENT_MANAGER.getCode().equals(caAccountAudit.getType())) {
            if (StringUtils.isNotEmpty(paramMap.get("type")) && paramMap.get("type").equals("1")) {
                if (StringUtils.isNotEmpty(paramMap.get("sourceDataId"))) {
                    caAccountAudit.setAccountId(paramMap.get("sourceDataId"));
                }
            } else if (StringUtils.isNotEmpty(paramMap.get("type")) && paramMap.get("type").equals("2")) {
                if (StringUtils.isNotEmpty(paramMap.get("newDataId"))) {
                    caAccountAudit.setAccountId(paramMap.get("newDataId"));
                }
            }
        }
        boolean backFlag = caAccountAuditService.insertAccountAudit(caAccountAudit);
        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
    }


    /**
     * 修改上游对账审批信息
     * 2019-02-21 11:44:08
     *
     * @return
     */
    @RequestMapping("/updateCaAccountAuditById")
    public String updateCaAccountAuditById(@RequestParam Map<String, String> paramMap) {
        if (StringUtils.isEmpty(paramMap.get("type")) || StringUtils.isEmpty(paramMap.get("id"))) {
            logger.info("添加审批信息类型为空,参数为:" + JSONObject.toJSONString(paramMap));
            return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
        }
        CaAccountAudit caAccountAudit = new CaAccountAudit();
        caAccountAudit.setType(paramMap.get("type"));
        caAccountAudit.setId(paramMap.get("id"));
        caAccountAudit.setAuditStatus(paramMap.get("auditStatus"));
        caAccountAudit.setOperateMsg(paramMap.get("operateMsg"));

        String keyLock = IdUtil.ELECTRONIC_ACCOUNT_ADJUST_ORDER + caAccountAudit.getId();

        caAccountAudit.setOperateAuditUserid(UserUtils.getUser().getId().toString());

        //添加redis 分布式 解决代付查单重复入账的问题
        if (haveGetRedisLock(keyLock)) {
            logger.info("已经在处理该订单！" + caAccountAudit.getId() + "添加审批信息类型为空,参数为:" + JSONObject.toJSONString(paramMap));
            return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
        }
        boolean backFlag = setGetRedisLock(keyLock, IdUtil.ELECTRONIC_ACCOUNT_ADJUST_ORDER_TIME);
        if (backFlag) {
            caAccountAuditService.updateAccountAudit(caAccountAudit);
            //入账MQ
            if(caAccountAudit.getAuditStatus().equals(CaAuditEnum.UNAUDITED.getCode())){
                if(//公户充值管理
                CaAuditTypeEnum.PUB_ACC_RECHARGE_MANAGER.getCode().equals(caAccountAudit.getType())||
                    //上游结算管理
                CaAuditTypeEnum.SETTLEMENT_MANAGER.getCode().equals(caAccountAudit.getType())||
                    //代付业务手动调账管理
                CaAuditTypeEnum.PAY_FOR_ANOTHER_ADJUSTMENT_MANAGER.getCode().equals(caAccountAudit.getType())||
                    //支付业务手动调账管理
                CaAuditTypeEnum.PAYMENT_ADJUSTMENT_MANAGER.getCode().equals(caAccountAudit.getType())
                ){
                    CacheChanAccount  cacheChanAccount =caAccountAuditAdminService.bulidMqCaAccountAudit(caAccountAudit);
                    logger.info("投诉订单调账ID:"+caAccountAudit.getId()+"入mq");
                    iDfProducerService.sendInfo(JSON.toJSONString(cacheChanAccount),QueueUtil.CA_QUEUE);
                }

            }

        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
    }


    /**
     * 跳转代付调账页面
     * 2019-02-22 17:10:25
     *
     * @return
     */
    @RequestMapping("/toPayForAnotherAdjustment")
    public ModelAndView toAddAccountAudit() {
        ModelAndView andView = new ModelAndView();

        List<CaElectronicAccount> electronicAccounts = caAccountAuditService.queryCaElectronicAccountByExample(new CaElectronicAccount());
        andView.addObject("electronicAccounts", electronicAccounts);

        andView.setViewName("modules/upstreamaudit/toPayForAnotherAdjustment");
        return andView;
    }

    /**
     * 查询投诉订单list
     * 2019-02-21 11:09:08
     *
     * @param paramMap
     * @return
     */
    @RequestMapping("/queryRepeatAudits")
    public ModelAndView queryRepeatAudits(@RequestParam Map<String, String> paramMap) {
        logger.info("请求参数:" + JSON.toJSONString(paramMap));
        ModelAndView andView = new ModelAndView();
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainList");
        andView.addObject("vo", paramMap);
        List<CaElectronicAccount> caElectronicAccountList = electronicAdminAccountInfoService.list(new ElectronicAccountVo());
        andView.addObject("electronicAccounts", caElectronicAccountList);
        CaAccountAudit caAccountAudit = new CaAccountAudit();
        caAccountAudit.setType(CaAuditTypeEnum.COMPLAINT_MANAGER.getCode());
        caAccountAudit.setAccountId(paramMap.get("accountId"));
        if (StringUtils.isNotBlank(paramMap.get("createTime")) && StringUtils.isNotBlank(paramMap.get("updateTime"))) {
            try {
                caAccountAudit.setCreatedTime(DateUtils.parseDate(paramMap.get("createTime"), "yyyy-MM-dd HH:mm:ss"));
                caAccountAudit.setUpdatedTime(DateUtils.parseDate(paramMap.get("updateTime"), "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                logger.info("日期格式错误");
            }
        }
        int count = caAccountAuditService.count(caAccountAudit);
        if (count == 0) {
            return andView;
        }
        PageInfo pageInfo = new PageInfo();
        if (paramMap.get("pageNo") == null) {
            pageInfo.setPageNo(1);
        } else {
            pageInfo.setPageNo(Integer.valueOf(paramMap.get("pageNo")));
        }
        caAccountAudit.setPageInfo(pageInfo);
        List<CaAccountAuditEx> caAccountAudits = caAccountAuditService.queryCaAccountAuditEx(caAccountAudit);

        Page page = new Page(caAccountAudit.getPageInfo().getPageNo(), caAccountAudit.getPageInfo().getPageSize(), count, caAccountAudits, true);

        andView.addObject("caAccountAudits", caAccountAudits);
        andView.addObject("page", page);
        andView.addObject("messageType", paramMap.get("messageType"));
        andView.addObject("message", paramMap.get("message"));
        return andView;
    }

    /**
     * 新增投诉订单
     * 2019-02-21 11:09:08
     *
     * @param caAccountAudit
     * @return
     */
    @RequestMapping("/toAddRepeatAudits")
    public ModelAndView toAddRepeatAudits(CaAccountAudit caAccountAudit) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainAdd");
        return andView;
    }


    /**
     * 新增投诉订单
     * 2019-02-21 11:09:08
     *
     * @param caAccountAuditEx
     * @return
     */
    @RequestMapping("/doAddRepeatAudits")
    public String doAddRepeatAudits(CaAccountAuditEx caAccountAuditEx, RedirectAttributes redirectAttributes) {
        String message, messageType;
        //组装投诉订单参数
        CaAccountAudit caAccountAudit = new CaAccountAudit();
        //重复支付
        if ("P".equals(caAccountAuditEx.getComplainType())) {
            MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
            mchtGatewayOrder.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
            mchtGatewayOrder.setPlatOrderId(caAccountAuditEx.getSourceDataId());
            mchtGatewayOrder.setSuffix("20" + caAccountAuditEx.getSourceDataId().substring(1, 5));
            List<MchtGatewayOrder> mchtGatewayOrderList = mchtGwOrderService.list(mchtGatewayOrder);
            if (mchtGatewayOrderList == null || mchtGatewayOrderList.size() == 0) {
                message = "原始订单不存在";
                messageType = "error";
                redirectAttributes.addFlashAttribute("messageType", messageType);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
            }
            //
            MchtGatewayOrder mchtGatewayOrder1 = mchtGatewayOrderList.get(0);
            BigDecimal realChanFee = BigDecimal.ZERO;
            if (mchtGatewayOrder1.getChanRealFeeAmount() != null && mchtGatewayOrder1.getChanRealFeeRate() != null) {
                //混合
                realChanFee = BigDecimal.valueOf(mchtGatewayOrder1.getAmount())
                        .multiply(mchtGatewayOrder1.getChanRealFeeRate())
                        .add(mchtGatewayOrder1.getChanRealFeeAmount());
            } else if (mchtGatewayOrder1.getChanRealFeeRate() != null) {
                realChanFee = BigDecimal.valueOf(mchtGatewayOrder1.getAmount())
                        .multiply(mchtGatewayOrder1.getChanRealFeeRate());
            } else if (mchtGatewayOrder1.getChanRealFeeAmount() != null) {
                realChanFee = mchtGatewayOrder1.getChanRealFeeAmount();
            }
            //查询通道商户支付方式
            ChanMchtPaytype chanMchtPaytype = chanMchtPaytypeService.queryByKey(mchtGatewayOrder1.getChanMchtPaytypeId());
            //查询电子账户信息
            CaElectronicAccount caElectronicAccount = new CaElectronicAccount();
            caElectronicAccount.setChanCode(chanMchtPaytype.getChanCode());
            caElectronicAccount.setMchtCode(chanMchtPaytype.getMchtCode());
            ElectronicAccountVo reqVo = new ElectronicAccountVo();
            reqVo.setCaElectronicAccount(caElectronicAccount);
            ElectronicAccountVo vo = electronicAccountInfoService.queryBykey(reqVo);

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
        } else {
            PlatProxyDetail platProxyDetail = proxyDetailService.queryByKey(caAccountAuditEx.getSourceDataId());
            if (platProxyDetail == null) {
                message = "原始订单不存在";
                messageType = "error";
                redirectAttributes.addFlashAttribute("messageType", messageType);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
            }
            BigDecimal realChanFee = BigDecimal.ZERO;
            if (platProxyDetail.getChanRealFeeAmount() != null && platProxyDetail.getChanRealFeeRate() != null) {
                //混合
                realChanFee = platProxyDetail.getAmount()
                        .multiply(platProxyDetail.getChanRealFeeRate())
                        .add(platProxyDetail.getChanRealFeeAmount());
            } else if (platProxyDetail.getChanRealFeeRate() != null) {
                realChanFee = platProxyDetail.getAmount()
                        .multiply(platProxyDetail.getChanRealFeeRate());
            } else if (platProxyDetail.getChanRealFeeAmount() != null) {
                realChanFee = platProxyDetail.getChanRealFeeAmount();
            }
            //查询通道商户支付方式
            ChanMchtPaytype chanMchtPaytype = chanMchtPaytypeService.queryByKey(platProxyDetail.getChanMchtPaytypeId());
            //查询电子账户信息
            CaElectronicAccount caElectronicAccount = new CaElectronicAccount();
            caElectronicAccount.setChanCode(chanMchtPaytype.getChanCode());
            caElectronicAccount.setMchtCode(chanMchtPaytype.getMchtCode());
            ElectronicAccountVo reqVo = new ElectronicAccountVo();
            reqVo.setCaElectronicAccount(caElectronicAccount);
            ElectronicAccountVo vo = electronicAccountInfoService.queryBykey(reqVo);
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
     *
     * @param caAccountAudit
     * @return
     */
    @RequestMapping("/toApproveRepeatAudits")
    public ModelAndView toApproveRepeatAudits(CaAccountAudit caAccountAudit) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainApprove");
        //查询对应审批信息
        CaAccountAudit caAccountAudit1 = caAccountAuditService.findAccountAudit(caAccountAudit.getId());
        MchtGatewayOrder mchtGatewayOrder = null;
        if (caAccountAudit1.getSourceDataId().startsWith("P")) {
            MchtGatewayOrder mchtGatewayOrderReq = new MchtGatewayOrder();
            mchtGatewayOrderReq.setSuffix("20" + caAccountAudit1.getSourceDataId().substring(1, 5));
            mchtGatewayOrderReq.setPlatOrderId(caAccountAudit1.getSourceDataId());
            //查询对应订单信息
            mchtGatewayOrder = mchtGwOrderService.list(mchtGatewayOrderReq).get(0);
        } else {
            PlatProxyDetail platProxyDetail = proxyDetailService.queryByKey(caAccountAudit1.getSourceDataId());
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
        ChanInfo chanInfo = channelService.queryByKey(mchtGatewayOrder.getChanCode());
        andView.addObject("mertName", mchtInfo.getName());
        andView.addObject("chanName", chanInfo.getName());
        andView.addObject("mchtGatewayOrder", mchtGatewayOrder);
        andView.addObject("caAccountAudit", caAccountAudit1);
        return andView;
    }


    /**
     * 获取redis的乐观锁
     *
     * @param redisKey
     * @return
     */
    public boolean haveGetRedisLock(String redisKey) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 判断key在缓存中是否存在
            if (jedis.exists(redisKey)) {
                logger.info("redisKey存在:" + redisKey);
                return true;
            }
            logger.info("redisKey:不存在" + redisKey);
            return false;
        } catch (Exception e) {
            logger.error(redisKey + " is redis exists error: {}", e.getMessage(), e);
            return false;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }


    /**
     * 获取redis的乐观锁
     *
     * @param redisKey
     * @param seconds
     * @return
     */
    public boolean setGetRedisLock(String redisKey, int seconds) {
        boolean backFlag = false;
        logger.info("设置key :" + redisKey + ",时间:" + seconds);
        Jedis jedis = jedisPool.getResource();
        try {
            if (jedis.setnx(redisKey, redisKey) == 1) {
                jedis.expire(redisKey, seconds);
                backFlag = true;
                logger.info("设置key :" + redisKey + ",时间:" + seconds + ", 设置成功");
            } else {
                logger.info("设置key :" + redisKey + ",时间:" + seconds + ", 设置失败");
                backFlag = false;
            }
        } catch (Exception e) {
            logger.error("删除缓异常！", e.getMessage(), e);
            backFlag = false;
        } finally {
            jedisPool.returnResource(jedis);
            return backFlag;
        }
    }


    /**
     * base64编码转为图片
     *
     * @param base64Image
     * @return
     */
    public String convertImageFromBase64(String base64Image, HttpServletRequest request) {
        if (base64Image == null || StringUtils.isBlank(base64Image)) return null;
        String base64Image1 = base64Image.substring(base64Image.indexOf("base64,") + "base64,".length());
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(base64Image1);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            String mchtId = UserUtils.getUser().getLoginName();
            SimpleDateFormat fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            SimpleDateFormat fileDir = new SimpleDateFormat("yyyyMMddHH");
            String ext = base64Image.substring(base64Image.indexOf("data:image/") + "data:image/".length(), base64Image.indexOf(";"));
            String fileName = fileFormatter.format(new Date()) + new Random().nextInt(1000) + "." + ext;
            String tempFileDir = fileDir.format(new Date());
            String dir = getImageFileStorePath(request);
            String path = dir + tempFileDir;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            OutputStream out = new FileOutputStream(path + "/" + mchtId + "-" + fileName);
            out.write(b);
            out.flush();
            out.close();
            String servletPath = CA_IMAGES_PATH + tempFileDir + "/" + mchtId + "-" + fileName;

            return servletPath;
        } catch (Exception e) {
            logger.error("base64编码转图片失败", e);
        }
        return null;
    }


    /**
     * 跳转公户充值
     * 2019-02-22 17:10:25
     *
     * @return
     */
    @RequestMapping("/toPubAccRechargeAdd")
    public ModelAndView toPubAccRechargeAdd() {
        ModelAndView andView = new ModelAndView();
        //电子账户
        List<CaElectronicAccount> electronicAccounts = caAccountAuditService.queryCaElectronicAccountByExample(new CaElectronicAccount());
        andView.addObject("electronicAccounts", electronicAccounts);
        //对公账户
        List<PublicAccountInfo> publicAccountInfos = getPublicAccountInfos();
        andView.addObject("publicAccountInfos", publicAccountInfos);

        andView.setViewName("modules/upstreamaudit/toPubAccRechargeAdd");
        return andView;
    }

    /**
     * 审批通过/拒绝
     * 2019-02-21 11:09:08
     *
     * @param caAccountAudit
     * @return
     */
    @RequestMapping("/doApproveRepeatAudits")
    public String doApproveRepeatAudits(CaAccountAudit caAccountAudit, RedirectAttributes redirectAttributes) {
        caAccountAudit.setOperateAuditUserid(String.valueOf(UserUtils.getUser().getId()));
        caAccountAudit.setOperateAuditTime(new Date());
        boolean result = false;
        String message, messageType;
        //查询对应审批信息及上游入账信息
        CaAccountAudit caAccountAudit1 = caAccountAuditService.findAccountAudit(caAccountAudit.getId());
        if (caAccountAudit1.getSourceDataId().startsWith("P")) {
            MchtGatewayOrder mchtGatewayOrderReq = new MchtGatewayOrder();
            mchtGatewayOrderReq.setSuffix("20" + caAccountAudit1.getSourceDataId().substring(1, 5));
            mchtGatewayOrderReq.setPlatOrderId(caAccountAudit1.getSourceDataId());
            //查询对应订单信息
            MchtGatewayOrder mchtGatewayOrder = mchtGwOrderService.list(mchtGatewayOrderReq).get(0);
            MchtInfo mchtInfo = merchantService.queryByKey(mchtGatewayOrder.getMchtCode());
            CacheMchtAccount cacheMchtAccount = caAccountAuditAdminService.bulidRedisPayTaskObject(mchtGatewayOrder, mchtInfo, caAccountAudit);
            //商户调账队列
            result =caAccountAuditAdminService.insert2redisAccTask(cacheMchtAccount);
            if(result){
                CacheChanAccount  cacheChanAccount =caAccountAuditAdminService.bulidMqPayTaskObject(mchtGatewayOrder,mchtInfo,caAccountAudit1);
                logger.info("投诉订单调账ID:"+caAccountAudit1.getId()+"入mq");
                iDfProducerService.sendInfo(JSON.toJSONString(cacheChanAccount),QueueUtil.CA_QUEUE);
            }

        } else {
            PlatProxyDetail platProxyDetail = proxyDetailService.queryByKey(caAccountAudit1.getSourceDataId());
            MchtInfo mchtInfo = merchantService.queryByKey(platProxyDetail.getMchtId());
            CacheMchtAccount cacheMchtAccount = caAccountAuditAdminService.bulidRedisProxyTaskObject(platProxyDetail, mchtInfo, caAccountAudit);
            //商户调账队列
            result =caAccountAuditAdminService.insert2redisAccTask(cacheMchtAccount);
            if(result){
                CacheChanAccount  cacheChanAccount =caAccountAuditAdminService.bulidMqProxyObject(platProxyDetail,mchtInfo,caAccountAudit1);
                logger.info("投诉订单调账ID:"+caAccountAudit1.getId()+"入mq");
                iDfProducerService.sendInfo(JSON.toJSONString(cacheChanAccount),QueueUtil.CA_QUEUE);
            }
        }
        //上游入账信息
        if (result) {
            result = caAccountAuditService.updateAccountAudit(caAccountAudit);
        }
        if (result) {
            message = "保存成功";
            messageType = "success";
        } else {
            message = "保存失败";
            messageType = "error";
        }
        redirectAttributes.addFlashAttribute("messageType", messageType);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
    }


    public CaAccountAudit buildAdjustDfAndZf(Map<String, String> paramMap) {
        CaAccountAudit caAccountAudit = new CaAccountAudit();
        //公户充值  公户信息
        caAccountAudit.setSourceDataId(paramMap.get("sourceDataId"));
        //公户充值  入账类型 1 电子 2 公户
        caAccountAudit.setNewDataId(paramMap.get("newDataId"));
        caAccountAudit.setType(paramMap.get("type"));
        caAccountAudit.setAuditStatus(paramMap.get("auditStatus"));
        caAccountAudit.setApprovalCreateTime(paramMap.get("approvalCreateTime"));
        caAccountAudit.setApprovalEndTime(paramMap.get("approvalEndTime"));
        caAccountAudit.setAccountId(paramMap.get("accountId"));
        caAccountAudit.setAuditStatus(paramMap.get("auditStatus"));
        if (StringUtils.isEmpty(paramMap.get("applyCreateTime")) || StringUtils.isEmpty(paramMap.get("applyEndTime"))) {
            String currentDateStr = DateUtils.formatDate(new Date());
            caAccountAudit.setApplyCreateTime(currentDateStr + " 00:00:00");
            caAccountAudit.setApplyEndTime(currentDateStr + " 23:59:59");
            paramMap.put("applyCreateTime", caAccountAudit.getApplyCreateTime());
            paramMap.put("applyEndTime", caAccountAudit.getApplyEndTime());
        } else {
            caAccountAudit.setApplyCreateTime(paramMap.get("applyCreateTime"));
            caAccountAudit.setApplyEndTime(paramMap.get("applyEndTime"));
        }
        return caAccountAudit;
    }


    public CaAccountAudit buildAdjustPubRecharge(Map<String, String> paramMap) {
        CaAccountAudit caAccountAudit = new CaAccountAudit();
        //公户充值  公户信息
        caAccountAudit.setSourceDataId(paramMap.get("sourceDataId"));
        //公户充值  入账类型 1 电子 2 公户
        caAccountAudit.setNewDataId(paramMap.get("newDataId"));
        caAccountAudit.setType(paramMap.get("type"));
        caAccountAudit.setAuditStatus(paramMap.get("auditStatus"));
        caAccountAudit.setApprovalCreateTime(paramMap.get("approvalCreateTime"));
        caAccountAudit.setApprovalEndTime(paramMap.get("approvalEndTime"));
        caAccountAudit.setAccountId(paramMap.get("accountId"));
        caAccountAudit.setAuditStatus(paramMap.get("auditStatus"));
        if (StringUtils.isEmpty(paramMap.get("applyCreateTime")) || StringUtils.isEmpty(paramMap.get("applyEndTime"))) {
            String currentDateStr = DateUtils.formatDate(new Date());
            caAccountAudit.setApplyCreateTime(currentDateStr + " 00:00:00");
            caAccountAudit.setApplyEndTime(currentDateStr + " 23:59:59");
            paramMap.put("applyCreateTime", caAccountAudit.getApplyCreateTime());
            paramMap.put("applyEndTime", caAccountAudit.getApplyEndTime());
        } else {
            caAccountAudit.setApplyCreateTime(paramMap.get("applyCreateTime"));
            caAccountAudit.setApplyEndTime(paramMap.get("applyEndTime"));
        }
        return caAccountAudit;
    }


    /**
     * 获得公户信息
     * 2019-03-07 17:16:07
     *
     * @return
     */
    public List<PublicAccountInfo> getPublicAccountInfos() {
        PublicAccountInfo publicAccountInfo = new PublicAccountInfo();
        publicAccountInfo.setStatus("1,2");
        List<PublicAccountInfo> publicAccountInfos = publicAccountInfoService.list(publicAccountInfo);
        return publicAccountInfos;
    }


    public Map<String, PublicAccountInfo> buildPublicAccountInfoMap() {
        Map<String, PublicAccountInfo> accountInfoMap = new HashMap<>();
        List<PublicAccountInfo> accountInfos = getPublicAccountInfos();
        for (PublicAccountInfo info : accountInfos) {
            accountInfoMap.put(info.getPublicAccountCode(), info);
        }
        return accountInfoMap;
    }


    public Map<String, CaElectronicAccount> buildElectronicsAccountInfoMap() {
        Map<String, CaElectronicAccount> electronicAccountHashMap = new HashMap<>();
        List<CaElectronicAccount> electronicAccounts = caAccountAuditService.queryCaElectronicAccountByExample(new CaElectronicAccount());
        if (electronicAccounts == null) {
            return electronicAccountHashMap;
        }
        for (CaElectronicAccount info : electronicAccounts) {
            electronicAccountHashMap.put(info.getId(), info);
        }
        return electronicAccountHashMap;
    }


    /**
     * 调账审批公户充值页面
     * 2019-03-08 10:29:19
     *
     * @param id
     * @return
     */
    @RequestMapping("/auditOperatePubRecharge")
    public ModelAndView auditOperatePubRecharge(String id) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/upstreamaudit/auditOperatePubRecharge");

        CaAccountAudit accountAudit = null;
        logger.info("查询上游对账审批详情, 请求参数keyId为:" + id);
        if (StringUtils.isEmpty(id)) {
            logger.info("查询上游对账审批详情, 请求参数keyId为空.");
            return andView;
        }
        accountAudit = caAccountAuditService.findAccountAudit(id);


        Map<String, PublicAccountInfo> accountInfoMap = buildPublicAccountInfoMap();

        Map<String, CaElectronicAccount> electronicAccountMap = buildElectronicsAccountInfoMap();
        //如果是公户管理
        if (CaAuditTypeEnum.PUB_ACC_RECHARGE_MANAGER.getCode().equals(accountAudit.getType())) {
            if (accountInfoMap.get(accountAudit.getSourceDataId()) != null) {
                accountAudit.setPubAccName(accountInfoMap.get(accountAudit.getSourceDataId()).getPublicAccountName());
            }
            if ("1".equals(accountAudit.getAccountType())) {
                if (electronicAccountMap.get(accountAudit.getNewDataId()) != null) {
                    accountAudit.setReceiptAccName(electronicAccountMap.get(accountAudit.getNewDataId()).getElectronicAccountName());
                }
            } else if ("2".equals(accountAudit.getAccountType())) {
                if (accountInfoMap.get(accountAudit.getNewDataId()) != null) {
                    accountAudit.setReceiptAccName(accountInfoMap.get(accountAudit.getNewDataId()).getPublicAccountName());
                }
            }

        }


        andView.addObject("queryFlag", "audit");
        andView.addObject("accountAudit", accountAudit);
        return andView;
    }

    @InitBinder
    public void initDateFormate(WebDataBinder dataBinder) {
        dataBinder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"), "createTime");
        dataBinder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"), "updateTime");
    }


    public List<CaAccountAudit> buildCaAccountAudits(List<CaAccountAudit> caAccountAuditList){
        if(caAccountAuditList == null || caAccountAuditList.size()== 0){
            return caAccountAuditList;
        }
        Map<String, PublicAccountInfo> accountInfoMap = buildPublicAccountInfoMap();
        Map<String, CaElectronicAccount> electronicAccountMap = buildElectronicsAccountInfoMap();
        for (CaAccountAudit audit : caAccountAuditList) {

            //audit.setCustomerAuditUserName();


            //如果是公户管理
            if (CaAuditTypeEnum.PUB_ACC_RECHARGE_MANAGER.getCode().equals(audit.getType())) {
                if (accountInfoMap.get(audit.getSourceDataId()) != null) {
                    audit.setPubAccName(accountInfoMap.get(audit.getSourceDataId()).getPublicAccountName());
                }
                if ("1".equals(audit.getAccountType())) {
                    if (electronicAccountMap.get(audit.getNewDataId()) != null) {
                        audit.setReceiptAccName(electronicAccountMap.get(audit.getNewDataId()).getElectronicAccountName());
                    }
                } else if ("2".equals(audit.getAccountType())) {
                    if (accountInfoMap.get(audit.getNewDataId()) != null) {
                        audit.setReceiptAccName(accountInfoMap.get(audit.getNewDataId()).getPublicAccountName());
                    }
                }

            }

            //如果是上游结算
            if (CaAuditTypeEnum.SETTLEMENT_MANAGER.getCode().equals(audit.getType())) {
                if (electronicAccountMap.get(audit.getSourceDataId()) != null) {
                    audit.setElectronicAccountName(electronicAccountMap.get(audit.getSourceDataId()).getElectronicAccountName());
                }
                if ("1".equals(audit.getAccountType())) {
                    if (electronicAccountMap.get(audit.getNewDataId()) != null) {
                        audit.setReceiptAccName(electronicAccountMap.get(audit.getNewDataId()).getElectronicAccountName());
                    }
                } else if ("2".equals(audit.getAccountType())) {
                    if (accountInfoMap.get(audit.getNewDataId()) != null) {
                        audit.setReceiptAccName(accountInfoMap.get(audit.getNewDataId()).getPublicAccountName());
                    }
                }

            }
        }
        return caAccountAuditList;




    }

    /**
     * 跳转上游结算
     * 2019-02-22 17:10:25
     *
     * @return
     */
    @RequestMapping("/toUpstreamSettlementAdd")
    public ModelAndView toUpstreamSettlementAdd() {
        ModelAndView andView = new ModelAndView();
        //电子账户
        List<CaElectronicAccount> electronicAccounts = caAccountAuditService.queryCaElectronicAccountByExample(new CaElectronicAccount());
        andView.addObject("electronicAccounts", electronicAccounts);
        //对公账户
        List<PublicAccountInfo> publicAccountInfos = getPublicAccountInfos();
        andView.addObject("publicAccountInfos", publicAccountInfos);

        andView.setViewName("modules/upstreamaudit/toUpstreamSettlementAdd");
        return andView;
    }





    /**
     * 调账审批上游结算页面
     * 2019-03-08 10:29:19
     *
     * @param id
     * @return
     */
    @RequestMapping("/auditOperateUpstreamSettlement")
    public ModelAndView auditOperateUpstreamSettlement(String id) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/upstreamaudit/auditOperateUpstreamSettlement");

        CaAccountAudit accountAudit = null;
        logger.info("查询上游对账审批详情, 请求参数keyId为:" + id);
        if (StringUtils.isEmpty(id)) {
            logger.info("查询上游对账审批详情, 请求参数keyId为空.");
            return andView;
        }
        accountAudit = caAccountAuditService.findAccountAudit(id);


        Map<String, PublicAccountInfo> accountInfoMap = buildPublicAccountInfoMap();

        Map<String, CaElectronicAccount> electronicAccountMap = buildElectronicsAccountInfoMap();
        //如果是上游结算管理
        if (CaAuditTypeEnum.SETTLEMENT_MANAGER.getCode().equals(accountAudit.getType())) {
            if (electronicAccountMap.get(accountAudit.getSourceDataId()) != null) {
                accountAudit.setElectronicAccountName(electronicAccountMap.get(accountAudit.getSourceDataId()).getElectronicAccountName());
            }
            if ("1".equals(accountAudit.getAccountType())) {
                if (electronicAccountMap.get(accountAudit.getNewDataId()) != null) {
                    accountAudit.setReceiptAccName(electronicAccountMap.get(accountAudit.getNewDataId()).getElectronicAccountName());
                }
            } else if ("2".equals(accountAudit.getAccountType())) {
                if (accountInfoMap.get(accountAudit.getNewDataId()) != null) {
                    accountAudit.setReceiptAccName(accountInfoMap.get(accountAudit.getNewDataId()).getPublicAccountName());
                }
            }

        }
        andView.addObject("queryFlag", "audit");
        andView.addObject("accountAudit", accountAudit);
        return andView;
    }
}