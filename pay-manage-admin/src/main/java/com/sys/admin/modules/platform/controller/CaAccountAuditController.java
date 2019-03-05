package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
<<<<<<< HEAD
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.util.IdUtil;
=======
import com.sys.common.enums.PayStatusEnum;
>>>>>>> 1410ee77807ef6b52dda3207aabe14f962076e85
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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


    @Autowired
    private JedisPool jedisPool;

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

        boolean backFlag = caAccountAuditService.updateAccountAudit(caAccountAudit);

        setGetRedisLock(keyLock, IdUtil.ELECTRONIC_ACCOUNT_ADJUST_ORDER_TIME);

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

<<<<<<< HEAD

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
            jedis.set(redisKey, redisKey);
            jedis.expire(redisKey, seconds);
            backFlag = true;
        } catch (Exception e) {
            logger.error("删除缓异常！",e.getMessage(), e);
            backFlag = false;
        }finally {
            jedisPool.returnResource(jedis);
            return backFlag;
        }
    }




=======
        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/caAccountAudit/queryRepeatAudits";
    }
>>>>>>> 1410ee77807ef6b52dda3207aabe14f962076e85
}
