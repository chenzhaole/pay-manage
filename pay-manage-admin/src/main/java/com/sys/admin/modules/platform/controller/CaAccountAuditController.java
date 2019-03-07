package com.sys.admin.modules.platform.controller;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
<<<<<<< HEAD
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.util.DateUtils;
import com.sys.common.util.IdUtil;
=======
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.AdjustTypeEnum;
import com.sys.common.enums.CaAuditEnum;
import com.sys.common.enums.CaAuditTypeEnum;
>>>>>>> 1ff1edf15700f0b7895a7101d9afd5f298e695a1
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import com.sys.core.vo.ElectronicAccountVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
<<<<<<< HEAD
import java.text.SimpleDateFormat;
import java.util.*;
=======
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
>>>>>>> 1ff1edf15700f0b7895a7101d9afd5f298e695a1

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
<<<<<<< HEAD
    private JedisPool jedisPool;
=======
    private ElectronicAccountInfoService electronicAccountInfoService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ChannelService channelService;
>>>>>>> 1ff1edf15700f0b7895a7101d9afd5f298e695a1

    /**
     * 查询上游对账审批详情
     * 2019-02-21 11:01:51
     * @return
     */
    @RequestMapping("/findCaAccountAuditDetail")
    public ModelAndView findCaAccountAuditDetail(String id){
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/upstreamaudit/auditOperateElectronicAccountDzOrder");

        CaAccountAudit accountAudit = null;
        logger.info("查询上游对账审批详情, 请求参数keyId为:" + id);
        if(StringUtils.isEmpty(id)){
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
        caAccountAudit.setType(paramMap.get("type"));
        caAccountAudit.setAuditStatus(paramMap.get("auditStatus"));
        caAccountAudit.setApprovalCreateTime(paramMap.get("approvalCreateTime"));
        caAccountAudit.setApprovalEndTime(paramMap.get("approvalEndTime"));
        caAccountAudit.setAccountId(paramMap.get("accountId"));
        caAccountAudit.setAuditStatus(paramMap.get("auditStatus"));
        if(StringUtils.isEmpty(paramMap.get("applyCreateTime")) || StringUtils.isEmpty(paramMap.get("applyEndTime"))){
            String currentDateStr = DateUtils.formatDate(new Date());
            caAccountAudit.setApplyCreateTime(currentDateStr + " 00:00:00");
            caAccountAudit.setApplyEndTime(currentDateStr + " 23:59:59");
            paramMap.put("applyCreateTime", caAccountAudit.getApplyCreateTime());
            paramMap.put("applyEndTime", caAccountAudit.getApplyEndTime());
        }else{
            caAccountAudit.setApplyCreateTime(paramMap.get("applyCreateTime"));
            caAccountAudit.setApplyEndTime(paramMap.get("applyEndTime"));
        }
        //电子账户信息
        List<CaElectronicAccount>  electronicAccounts = caAccountAuditService.queryCaElectronicAccountByExample(new CaElectronicAccount());
        andView.addObject("electronicAccounts", electronicAccounts);

        int count =caAccountAuditService.count(caAccountAudit);
        if(count ==0){
            andView.addObject("paramMap",paramMap);
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

        caAccountAudits =  caAccountAuditService.queryCaAccountAudit(caAccountAudit);



        andView.addObject("caAccountAudits", caAccountAudits);
        Page page = new Page(pageNo, pageInfo.getPageSize(), count, electronicAccounts, true);
        andView.addObject("page", page);
        andView.addObject("orderCount", count);
        andView.addObject("paramMap",paramMap);
        return andView;
    }


    /**
     * 添加上游对账审批信息
     * 2019-02-21 11:31:14
     * @return
     */
    @RequestMapping("/insertCaAccountAudit")
    public String insertCaAccountAudit(HttpServletRequest request, @RequestParam Map<String, String> paramMap,
                                       @RequestParam(value = "proofImage", required = false) String proofImage){
        if(StringUtils.isEmpty(paramMap.get("type"))){
            logger.info("添加审批信息类型为空,参数为:" + JSONObject.toJSONString(paramMap));
            return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
        }
        String imgUrl = convertImageFromBase64(proofImage,request);
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
        caAccountAudit.setPicUrl(imgUrl);

        caAccountAudit.setCustomerAuditUserid(UserUtils.getUser().getId().toString());

        boolean backFlag = caAccountAuditService.insertAccountAudit(caAccountAudit);

        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
    }


    /**
     * 修改上游对账审批信息
     * 2019-02-21 11:44:08
     * @return
     */
    @RequestMapping("/updateCaAccountAuditById")
    public String updateCaAccountAuditById(@RequestParam Map<String, String> paramMap){
        if(StringUtils.isEmpty(paramMap.get("type")) || StringUtils.isEmpty(paramMap.get("id"))){
            logger.info("添加审批信息类型为空,参数为:" + JSONObject.toJSONString(paramMap));
            return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
        }
        CaAccountAudit caAccountAudit  = new CaAccountAudit();
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
        if(backFlag){
            caAccountAuditService.updateAccountAudit(caAccountAudit);
        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryCaAccountAudits?type=" + paramMap.get("type");
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
        andView.setViewName("/modules/upstreamaudit/repeatOrderComplainList");
        caAccountAudit.setType(CaAuditTypeEnum.COMPLAINT_MANAGER.getCode());
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
    public String doAddRepeatAudits(CaAccountAuditEx caAccountAuditEx){
        ModelAndView andView = new ModelAndView();
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

<<<<<<< HEAD
            CaAccountAudit caAccountAudit = new CaAccountAudit();
            //caAccountAudit.

=======
        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
    }
>>>>>>> 1ff1edf15700f0b7895a7101d9afd5f298e695a1

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
        MchtGatewayOrder mchtGatewayOrderReq = new MchtGatewayOrder();
        mchtGatewayOrderReq.setSuffix("20"+caAccountAudit1.getSourceDataId().substring(1,5));
        mchtGatewayOrderReq.setPlatOrderId(caAccountAudit1.getSourceDataId());
        //查询对应订单信息
        MchtGatewayOrder mchtGatewayOrder = mchtGwOrderService.list(mchtGatewayOrderReq).get(0);
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

<<<<<<< HEAD


        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
    }


    /**
     * 获取redis的乐观锁
     * @param redisKey
     * @return
     */
    public boolean haveGetRedisLock(String redisKey){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 判断key在缓存中是否存在
            if(jedis.exists(redisKey)) {
                logger.info("redisKey存在:" + redisKey);
                return true;
            }
            logger.info("redisKey:不存在" + redisKey);
            return false;
        } catch (Exception e) {
            logger.error(redisKey+" is redis exists error: {}", e.getMessage(),e);
            return false;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }




    /**
     * 获取redis的乐观锁
     * @param redisKey
     * @param seconds
     * @return
     */
    public boolean setGetRedisLock(String redisKey, int seconds){
        boolean backFlag = false;
        logger.info("设置key :" + redisKey + ",时间:" + seconds);
        Jedis jedis = jedisPool.getResource();
        try{
            if(jedis.setnx(redisKey, redisKey)== 1){
                jedis.expire(redisKey, seconds);
                backFlag = true;
                logger.info("设置key :" + redisKey + ",时间:" + seconds + ", 设置成功");
            }else{
                logger.info("设置key :" + redisKey + ",时间:" + seconds + ", 设置失败");
                backFlag = false;
            }
        } catch (Exception e) {
            logger.error("删除缓异常！",e.getMessage(), e);
            backFlag = false;
        }finally {
            jedisPool.returnResource(jedis);
            return backFlag;
        }
    }



    /**
     * base64编码转为图片
     * @param base64Image
     * @return
     */
    public String convertImageFromBase64(String base64Image, HttpServletRequest request){
        if (base64Image == null||StringUtils.isBlank(base64Image)) return null;
        String base64Image1 = base64Image.substring(base64Image.indexOf("base64,")+"base64,".length());
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
            String ext = base64Image.substring(base64Image.indexOf("data:image/")+"data:image/".length(),base64Image.indexOf(";"));
            String fileName = fileFormatter.format(new Date()) + new Random().nextInt(1000) + "." + ext;
            String tempFileDir = fileDir.format(new Date());
            String dir = getImageFileStorePath(request);
            String path = dir  + tempFileDir ;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            OutputStream out = new FileOutputStream(path+ "/" + mchtId +"-"+ fileName);
            out.write(b);
            out.flush();
            out.close();
            String servletPath = CA_IMAGES_PATH  + tempFileDir + "/" + mchtId +"-"+ fileName;

            return servletPath;
        } catch (Exception e) {
            logger.error("base64编码转图片失败",e);
        }
        return null;
    }



    /**
     * 跳转公户充值
     * 2019-02-22 17:10:25
     * @return
     */
    @RequestMapping("/toPubAccRechargeAdd")
    public ModelAndView toPubAccRechargeAdd(){
        ModelAndView andView = new ModelAndView();

        List<CaElectronicAccount>  electronicAccounts = caAccountAuditService.queryCaElectronicAccountByExample(new CaElectronicAccount());
        andView.addObject("electronicAccounts", electronicAccounts);

        andView.setViewName("modules/upstreamaudit/toPubAccRechargeAdd");
        return andView;
    }
=======
    /**
     * 审批通过/拒绝
     * 2019-02-21 11:09:08
     * @param caAccountAudit
     * @return
     */
    @RequestMapping("/doApproveRepeatAudits")
    public String doApproveRepeatAudits(CaAccountAudit caAccountAudit){
        //查询对应审批信息
        CaAccountAudit caAccountAudit1=caAccountAuditService.findAccountAudit(caAccountAudit.getId());
        MchtGatewayOrder mchtGatewayOrderReq = new MchtGatewayOrder();
        mchtGatewayOrderReq.setSuffix("20"+caAccountAudit1.getSourceDataId().substring(1,5));
        mchtGatewayOrderReq.setPlatOrderId(caAccountAudit1.getSourceDataId());
        //查询对应订单信息
        MchtGatewayOrder mchtGatewayOrder = mchtGwOrderService.list(mchtGatewayOrderReq).get(0);
        //查询商户名称
        MchtInfo mchtInfo = merchantService.queryByKey(mchtGatewayOrder.getMchtCode());
        //查询通道名称
        ChanInfo chanInfo =channelService.queryByKey(mchtGatewayOrder.getChanCode());
        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
    }

>>>>>>> 1ff1edf15700f0b7895a7101d9afd5f298e695a1
}
