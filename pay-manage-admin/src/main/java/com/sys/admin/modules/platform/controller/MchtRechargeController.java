package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.IpUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.order.IRechargeService;
import com.sys.boss.api.service.trade.handler.ITradeApiRechargePayHandler;
import com.sys.common.enums.*;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.ChannelService;
import com.sys.core.service.MchtRechargeConfigService;
import com.sys.core.service.MerchantService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("${adminPath}/mchtRecharge")
public class MchtRechargeController extends BaseController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MchtRechargeConfigService mchtRechargeConfigService;

    @Autowired
    private ITradeApiRechargePayHandler tradeApiRechargePayHandler;

    @Autowired
    private IRechargeService rechargeService;

    @Autowired
    private ChannelService channelService;


    public static final String IMAGES_PATH = "/images/";



    /**
     * 商户充值发起
     * 2018-09-29 11:36:58
     * @return
     */
    @RequestMapping("/commitMchtRecharge")
    @RequiresPermissions("mcht:proxy:commit")
    public ModelAndView commitMchtRecharge(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        String mchtId = UserUtils.getUser().getLoginName();
        MchtInfo mcht = merchantService.queryByKey(mchtId);
        MchtRechargeConfig rechargeConfig = mchtRechargeConfigService.findByRechargeConfigMchtId(mchtId);
        if (null != mcht) {
            modelAndView.addObject("mchtName", mcht.getName());
            modelAndView.addObject("mchtId", mchtId);
            modelAndView.addObject("rechargeConfig", rechargeConfig);
        }
        modelAndView.setViewName("modules/recharge/commitMchtRecharge");
        return modelAndView;
    }


    /**
     * 商户发起充值信息提交
     * 2018-10-11 15:55:21
     * @param request
     * @param proofImage
     * @return
     */
    @RequestMapping("/commitMchtRechargeInfo")
    @RequiresPermissions("mcht:proxy:commit")
    public String commitMchtRechargeInfo(HttpServletRequest request, @RequestParam(value = "proofImage", required = false) MultipartFile proofImage){
        String imgUrl = saveRechargeImage(proofImage, request);
        String amount = null;
        //充值金额
        String rechargeAmount = request.getParameter("rechargeAmount");
        //充值类型
        String rechargeType = request.getParameter("rechargeType");
        //商户ID
        String mchtId = UserUtils.getUser().getLoginName();
        if(StringUtils.isEmpty(rechargeType)){
            return null;
        }

        //留言
        String mchtMessage = request.getParameter("mchtMessage");

        //支付金额
        String payAmount = request.getParameter("payAmount");
        if("1".equals(rechargeType)){
            amount = rechargeAmount;
            Integer insertFlag = tradeApiRechargePayHandler.insertRechargeOrder(mchtId, amount, rechargeType, imgUrl, mchtMessage);
        }

        return "redirect:" + GlobalConfig.getAdminPath() + "/mchtRecharge/queryRechargePayOrders";
    }


    @RequestMapping("/commitMchtRechargePayInfo")
    @RequiresPermissions("mcht:proxy:commit")
    public ModelAndView commitMchtRechargePayInfo(HttpServletRequest request, Model model){
        ModelAndView modelAndView = new ModelAndView();
        String amount = null;
        //支付金额
        String payAmount = request.getParameter("payAmount");
        //充值类型
        String rechargeType = request.getParameter("rechargeType");
        //留言
        String mchtMessage = request.getParameter("mchtMessage");
        //商户ID
        String mchtId = UserUtils.getUser().getLoginName();
        if(StringUtils.isEmpty(rechargeType)){
            return null;
        }
        amount = payAmount;
        //String mchtId, String payType, String amount, String ip
        //获取请求ip，值必须真实，某些上游通道要求必须是真实ip
        String ip = IpUtil.getRemoteHost(request);//请求ip
        CommonResult commonResult = tradeApiRechargePayHandler.process(mchtId, PayTypeEnum.CHONGZHI_WG.getCode(), amount, ip, mchtMessage);

        String page = null;

        //先判断是否跳转上游收银台
        logger.info("commonResult:"+ commonResult + ",mchtId:"+ mchtId);
        if(isUseChanCashierPage(commonResult, mchtId)){
            //跳转到上游收银台的中转页面
            modelAndView.setViewName("modules/recharge/chanCashier");
            //跳转到上游收银台的中转页面，携带的数据
            this.addChanCashierModelInfo(model, commonResult, mchtId);
            logger.info(mchtId+"调用TradeCashierMchtHandler处理业务逻辑，处理结果为成功，需要使用上游收银台的中转页面，返回的CommonResult="+JSONObject.toJSONString(commonResult)+"跳转的页面为："+page);

        }
        return modelAndView;
    }


    public String saveRechargeImage(MultipartFile companyIconFile, HttpServletRequest request){
        String backStr = null;

        if (companyIconFile != null && StringUtils.isNotBlank(companyIconFile.getOriginalFilename())) {
            try {

                String mchtId = UserUtils.getUser().getLoginName();
                String originalFileName = companyIconFile.getOriginalFilename();
                // 获取文件扩展名
                String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
                //如果文件不是图片，则不上传
                if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)
                        || "png".equalsIgnoreCase(ext) || "gif".equalsIgnoreCase(ext)
                        || "bmp".equalsIgnoreCase(ext)) {
                    SimpleDateFormat fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    SimpleDateFormat fileDir = new SimpleDateFormat("yyyyMMddHH");
                    String fileName = fileFormatter.format(new Date()) + new Random().nextInt(1000) + "." + ext;
                    String dir = getImageFileStorePath(request);

                    String tempFileDir = fileDir.format(new Date());

                    dir = dir + "/" + tempFileDir + "/";

                    File file = new File(dir);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    backStr = IMAGES_PATH  + tempFileDir + "/" + mchtId +"-"+ fileName;

                    File tempFile = new File(dir + mchtId +"-"+ fileName);
                    if (!tempFile.createNewFile()) {
                        return backStr;
                    }
                    companyIconFile.transferTo(tempFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("手机端图标上传失败" + e.getMessage());
            }
        }
        return backStr;
    }




    /**
     * 查询支付订单
     * 2018-10-11 15:55:21
     * @param request
     * @return
     */
    @RequestMapping("/queryRechargePayOrders")
    @RequiresPermissions("mcht:proxy:commit")
    public ModelAndView queryRechargePayOrders(HttpServletRequest request ,
                                            @RequestParam Map<String, String> paramMap){
        ModelAndView modelAndView = new ModelAndView();
        MchtGatewayRechargeOrder rechargeOrder = new MchtGatewayRechargeOrder();
        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        rechargeOrder.setPageInfo(pageInfo);
        //获得总条数

        //查询当前的商户信息
        String mchtId = UserUtils.getUser().getLoginName();
        rechargeOrder.setMchtId(mchtId);

        int orderCount = rechargeService.countMchtGatewayRechargeOrders(rechargeOrder);
        if (orderCount == 0) {
            modelAndView.addObject("paramMap", paramMap);
            modelAndView.setViewName("modules/recharge/queryRechargePayOrders");
            return modelAndView;
        }
        //获得充值订单信息
        List<MchtGatewayRechargeOrder> rechargeOrders  = rechargeService.queryMchtGatewayRechargeOrders(rechargeOrder);
        if (CollectionUtils.isEmpty(rechargeOrders)) {
            modelAndView.addObject("paramMap", paramMap);
            modelAndView.setViewName("modules/recharge/queryRechargePayOrders");
            return modelAndView;
        }

        rechargeOrders = buildChanName(rechargeOrders);
        Page page = new Page(pageNo, pageInfo.getPageSize(), orderCount, rechargeOrders, true);
        modelAndView.addObject("page", page);
        modelAndView.addObject("orderCount", orderCount);
        modelAndView.setViewName("modules/recharge/queryRechargePayOrders");
        return modelAndView;
    }


    /**
     * 客服调账审批
     */
    @RequestMapping("adjustRechargeOrder")
    @RequiresPermissions("mcht:proxy:commit")
    public ModelAndView adjustRechargeOrder(HttpServletRequest request) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/recharge/adjustRechargeOrder");
        String platOrderId = request.getParameter("platOrderId");
        MchtGatewayRechargeOrder auditRechargeOrder = rechargeService.findRechargeOrderByPlatOrderId(platOrderId);
        if(auditRechargeOrder != null){
            MchtInfo mchtInfo = merchantService.queryByKey(auditRechargeOrder.getMchtId());
            MchtRechargeConfig rechargeConfig = mchtRechargeConfigService.findByRechargeConfigMchtId(auditRechargeOrder.getMchtId());
            andView.addObject("rechargeConfig", rechargeConfig);
            andView.addObject("mchtInfo",mchtInfo);
        }
        andView.addObject("auditRechargeOrder", auditRechargeOrder);
        return andView;
    }


    /**
     * 客服调账审批
     */
    @RequestMapping("adjustOperateRechargeOrder")
    @RequiresPermissions("mcht:proxy:commit")
    public ModelAndView adjustOperateRechargeOrder(HttpServletRequest request) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/recharge/adjustOperateRechargeOrder");
        String platOrderId = request.getParameter("platOrderId");
        MchtGatewayRechargeOrder auditRechargeOrder = rechargeService.findRechargeOrderByPlatOrderId(platOrderId);
        if(auditRechargeOrder != null){
            MchtInfo mchtInfo = merchantService.queryByKey(auditRechargeOrder.getMchtId());
            MchtRechargeConfig rechargeConfig = mchtRechargeConfigService.findByRechargeConfigMchtId(auditRechargeOrder.getMchtId());
            andView.addObject("rechargeConfig", rechargeConfig);
            andView.addObject("mchtInfo",mchtInfo);
        }

        andView.addObject("auditRechargeOrder", auditRechargeOrder);
        return andView;
    }


    /**
     * 客服提交审批结果
     * @return
     */
    @RequestMapping("commitAdjustRechargeOrder")
    @RequiresPermissions("mcht:proxy:commit")
    public String commitAdjustRechargeOrder(HttpServletRequest request){
        //customer
        //operate
        //审批类型
        String auditType = request.getParameter("auditType");
        //审批状态
        String auditStatus = request.getParameter("auditStatus");
        //平台订单号
        String platOrderId = request.getParameter("platOrderId");
        MchtGatewayRechargeOrder rechargeOrder = new MchtGatewayRechargeOrder();
        if("customer".equalsIgnoreCase(auditType)){
            rechargeOrder.setCustomerAuditTime(new Date());
            rechargeOrder.setCustomerAuditUserId(UserUtils.getUser().getLoginName());
            rechargeOrder.setCustomerAuditUserName(UserUtils.getUser().getName());
            if("pass".equals(auditStatus)){
                rechargeOrder.setAuditStatus(RechargeAuditEnum.CUSTOMER_PASS.getCode());
            }else if("refuse".equals(auditStatus)){
                rechargeOrder.setAuditStatus(RechargeAuditEnum.CUSTOMER_REFUSE.getCode());
            }
        }else if("operate".equals(auditType)){
            rechargeOrder.setOperateAuditTime(new Date());
            rechargeOrder.setOperateAuditUserId(UserUtils.getUser().getLoginName());
            rechargeOrder.setOperateAuditUserName(UserUtils.getUser().getName());
            if("pass".equals(auditStatus)){
                rechargeOrder.setAuditStatus(RechargeAuditEnum.OPERATE_PASS.getCode());
                rechargeOrder.setUpdateTime(new Date());
            }else if("refuse".equals(auditStatus)){
                rechargeOrder.setAuditStatus(RechargeAuditEnum.OPERATE_REFUSE.getCode());
            }
        }
        rechargeOrder.setPlatOrderId(platOrderId);
        Integer backFlag =  tradeApiRechargePayHandler.modifyRechargeOrder(rechargeOrder);
        if(backFlag == 0){
            logger.error("msg", "审批失败,请联系管理员.");
        }else{
            logger.info("msg", "审批成功");
        }
        return "redirect:" + GlobalConfig.getAdminPath() + "/mchtRecharge/queryOperateRechargePayOrders";
    }
    /**
     * 判断是否使用上游收银台页面
     * @param commonResult
     * @return
     */
    protected boolean isUseChanCashierPage(CommonResult commonResult, String midoid) {
        Result mchtResult = (Result) commonResult.getData();
        String clientPayWay  = mchtResult.getClientPayWay();
        logger.info(midoid+"，根据clientPayWay的值："+clientPayWay+"，判断是否需要使用上游收银台页面");
        if(ClientPayWayEnum.CHAN_CASHIER_URL.getCode().equals(clientPayWay) ||
                ClientPayWayEnum.CHAN_CASHIER_JS.getCode().equals(clientPayWay) ||
                ClientPayWayEnum.CHAN_CASHIER_FORM.getCode().equals(clientPayWay)){
            logger.info(midoid+"，根据clientPayWay的值："+clientPayWay+"，判断出需要使用上游收银台页面");
            return true;
        }
        logger.info(midoid+"，根据clientPayWay的值："+clientPayWay+"，判断出不需要使用上游收银台页面");
        return false;
    }

    /**
     * 跳转上游收银台页面需要的数据
     * @param model
     */
    protected void addChanCashierModelInfo(Model model, CommonResult result, String midoid) {
        Result mchtResult = (Result) result.getData();
        model.addAttribute("clientPayWay", mchtResult.getClientPayWay());
        model.addAttribute("payInfo", mchtResult.getPayInfo());
        logger.info(midoid+"，直接跳转到上游收银台页面需要的数据："+JSONObject.toJSONString(model));
    }

    /**
     * 查询支付订单状态
     * @param request
     * @return
     */
    @RequestMapping("/queryChanOrderStatus")
    @RequiresPermissions("operate:recharge:query")
    public String queryChanOrderStatus(HttpServletRequest request){
        String platOrderId = request.getParameter("platOrderId");
        if(StringUtils.isEmpty(platOrderId)){
            return "redirect:" + GlobalConfig.getAdminPath() + "/mchtRecharge/queryOperateRechargePayOrders";
        }
        tradeApiRechargePayHandler.processQuery(platOrderId, PayTypeEnum.CHONGZHI_WG.getCode());
        return "redirect:" + GlobalConfig.getAdminPath() + "/mchtRecharge/queryOperateRechargePayOrders";
    }



    /**
     * 查询汇款订单
     * 2018-10-11 15:55:21
     * @param request
     * @return
     */
    @RequestMapping("/queryOperateRechargePayOrders")
    @RequiresPermissions("operate:recharge:query")
    public ModelAndView queryOperateRechargePayOrders(HttpServletRequest request ,
                                                      @RequestParam Map<String, String> paramMap){
        ModelAndView modelAndView = new ModelAndView();
        MchtGatewayRechargeOrder rechargeOrder = new MchtGatewayRechargeOrder();
        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        rechargeOrder.setPageInfo(pageInfo);

        //获得总条数
        int orderCount = rechargeService.countMchtGatewayRechargeOrders(rechargeOrder);
        if (orderCount == 0) {
            modelAndView.addObject("paramMap", paramMap);
            modelAndView.setViewName("modules/recharge/queryOperateRechargePayOrders");
            return modelAndView;
        }
        //获得充值订单信息
        List<MchtGatewayRechargeOrder> rechargeOrders  = rechargeService.queryMchtGatewayRechargeOrders(rechargeOrder);
        if (CollectionUtils.isEmpty(rechargeOrders)) {
            modelAndView.addObject("paramMap", paramMap);
            modelAndView.setViewName("modules/recharge/queryOperateRechargePayOrders");
            return modelAndView;
        }

        rechargeOrders = buildChanName(rechargeOrders);
        Page page = new Page(pageNo, pageInfo.getPageSize(), orderCount, rechargeOrders, true);
        modelAndView.addObject("page", page);
        modelAndView.addObject("orderCount", orderCount);
        modelAndView.setViewName("modules/recharge/queryOperateRechargePayOrders");
        return modelAndView;
    }


    public List<MchtGatewayRechargeOrder> buildChanName(List<MchtGatewayRechargeOrder> rechargeOrders){
        if(rechargeOrders == null || rechargeOrders.size() ==0){
            return rechargeOrders;
        }
        for(MchtGatewayRechargeOrder rechargeOrder: rechargeOrders){
            MchtInfo mchtInfo = merchantService.queryByKey(rechargeOrder.getMchtId());
            MchtRechargeConfig rechargeConfig = mchtRechargeConfigService.findByRechargeConfigMchtId(rechargeOrder.getMchtId());
            if (null != mchtInfo) {
                rechargeOrder.setMchtCode(mchtInfo.getName());
                if("1".equals(rechargeOrder.getRechargeType())){
                    rechargeOrder.setPayType(PayTypeEnum.HUIKUANG_WG.getDesc());
                }else if("2".equals(rechargeOrder.getRechargeType())){
                    rechargeOrder.setPayType(PayTypeEnum.CHONGZHI_WG.getDesc());
                }
            }
            rechargeOrder.setMchtCode(mchtInfo.getName());
            rechargeOrder.setRechargeConfig(rechargeConfig);
            ChanInfo chanInfo = channelService.queryByKey(rechargeOrder.getChanMchtPaytypeId());
            if(chanInfo == null){
                continue;
            }
            rechargeOrder.setChanName(chanInfo.getName());
        }
        return rechargeOrders;
    }



}
