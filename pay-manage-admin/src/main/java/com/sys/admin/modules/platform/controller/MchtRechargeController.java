package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.utils.IpUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.service.order.IRechargeService;
import com.sys.boss.api.service.trade.handler.ITradeApiRechargePayHandler;
import com.sys.common.enums.*;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
    private ChanMchtPaytypeService chanMchtPaytypeService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private MchtProductService mchtProductService;

    @Autowired
    ProductService productService;

    //商户账户详情信息接口地址
    @Value("${mchtAccountDetailData.url}")
    private String mchtAccountDetailUrl;


    public static final String IMAGES_PATH = "/images/";

    protected  final static Logger logger = LoggerFactory.getLogger(MchtRechargeController.class);



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
            PlatFeerate czPlatFeerate = tradeApiRechargePayHandler.queryMchtFeerateByMchtIdAndCode(mchtId, PayTypeEnum.ZHIFU_WG.getCode());
            PlatFeerate hkPlatFeerate = tradeApiRechargePayHandler.queryMchtFeerateByMchtIdAndCode(mchtId, PayTypeEnum.HUIKUANG_WG.getCode());

            modelAndView.addObject("mchtName", mcht.getName());
            modelAndView.addObject("mchtId", mchtId);
            modelAndView.addObject("rechargeConfig", rechargeConfig);
            modelAndView.addObject("czPlatFeerate", czPlatFeerate);
            modelAndView.addObject("hkPlatFeerate", hkPlatFeerate);
            //查询是否有支付通道
            PlatProduct product = queryProduct(mchtId, PayTypeEnum.ZHIFU_WG.getCode());
            if(product == null || StringUtils.isEmpty(product.getId())){
                modelAndView.addObject("payFalg", "false");
            }else{
                modelAndView.addObject("payFalg", "true");
            }

        }
        //查询余额
        BigDecimal balance = queryPlatBalance(mchtId);
        modelAndView.addObject("balance", balance);
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
    public String commitMchtRechargeInfo(HttpServletRequest request, @RequestParam(value = "proofImage", required = false) String proofImage){
        logger.info("商户发起充值信息提交-汇款凭证:"+(proofImage.length()>=22?proofImage.substring(0,22):""));
        String imgUrl = convertImageFromBase64(proofImage,request);//saveRechargeImage(proofImage, request);

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
            if(StringUtils.isEmpty(rechargeAmount)){
                logger.error("商户发起充值信息提交,订单金额不正确,rechargeAmount:"+rechargeAmount +",rechargeType:" +rechargeType + ",mchtId:" +mchtId);
                return "redirect:" + GlobalConfig.getAdminPath() + "/mchtRecharge/queryRechargePayOrders";
            }
            amount = rechargeAmount;
            logger.info("商户发起充值信息提交,rechargeAmount:"+rechargeAmount +",rechargeType:" +rechargeType + ",mchtId:" +mchtId);
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
        CommonResult commonResult = tradeApiRechargePayHandler.process(mchtId, PayTypeEnum.ZHIFU_WG.getCode(), amount, ip, mchtMessage);

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
            String servletPath = IMAGES_PATH  + tempFileDir + "/" + mchtId +"-"+ fileName;

            return servletPath;
        } catch (Exception e) {
            logger.error("base64编码转图片失败",e);
        }
        return null;
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
        this.logger.info("请求参数:"+JSONObject.toJSONString(paramMap));
        ModelAndView modelAndView = new ModelAndView();
        MchtGatewayRechargeOrder rechargeOrder = new MchtGatewayRechargeOrder();
        //获取参数
        rechargeOrder.setMchtName(paramMap.get("mchtName"));
        rechargeOrder.setPlatOrderId(paramMap.get("platOrderId"));
        rechargeOrder.setStatus(paramMap.get("status"));
        rechargeOrder.setAuditStatus(paramMap.get("auditStatus"));
        rechargeOrder.setCreateStartTime(paramMap.get("beginDate"));
        rechargeOrder.setCreateEndTime(paramMap.get("endDate"));
        rechargeOrder.setRechargeType(paramMap.get("rechargeType"));
        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        rechargeOrder.setPageInfo(pageInfo);
        //设置查询当前用户的
        rechargeOrder.setMchtId(UserUtils.getUser().getLoginName());

        //获得总条数
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


        MchtGatewayRechargeOrder statisticsRechargeOrder = new MchtGatewayRechargeOrder();
        BeanUtils.copyProperties(rechargeOrder, statisticsRechargeOrder);

        //总金额
        statisticsRechargeOrder.setStatus(null);
        Long totalAmount = rechargeService.amount(statisticsRechargeOrder);
        //总笔数
        Integer totalTotal = rechargeService.orderCount(statisticsRechargeOrder);
        //成功金额
        statisticsRechargeOrder.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
        Long successAmount = rechargeService.amount(statisticsRechargeOrder);
        //成功笔数
        Integer successTotal = rechargeService.orderCount(statisticsRechargeOrder);


        modelAndView.addObject("totalAmount", totalAmount);
        modelAndView.addObject("totalTotal", totalTotal);
        modelAndView.addObject("successAmount", successAmount);
        modelAndView.addObject("successTotal", successTotal);


        rechargeOrders = buildChanName(rechargeOrders);
        Page page = new Page(pageNo, pageInfo.getPageSize(), orderCount, rechargeOrders, true);
        modelAndView.addObject("page", page);
        modelAndView.addObject("orderCount", orderCount);
        modelAndView.addObject("paramMap",paramMap);


        modelAndView.setViewName("modules/recharge/queryRechargePayOrders");
        return modelAndView;
    }


    /**
     * 客服充值审批
     */
    @RequestMapping("adjustRechargeOrder")
    public ModelAndView adjustRechargeOrder(HttpServletRequest request) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/recharge/adjustRechargeOrder");
        String platOrderId = request.getParameter("platOrderId");
        String queryFlag = request.getParameter("queryFlag");
        MchtGatewayRechargeOrder auditRechargeOrder = rechargeService.findRechargeOrderByPlatOrderId(platOrderId);
        if(auditRechargeOrder != null){
            MchtInfo mchtInfo = merchantService.queryByKey(auditRechargeOrder.getMchtId());
            MchtRechargeConfig rechargeConfig = mchtRechargeConfigService.findByRechargeConfigMchtId(auditRechargeOrder.getMchtId());
            andView.addObject("rechargeConfig", rechargeConfig);
            andView.addObject("mchtInfo",mchtInfo);
        }
        andView.addObject("queryFlag",queryFlag);
        andView.addObject("auditRechargeOrder", auditRechargeOrder);
        return andView;
    }


    /**
     * 运营充值审批
     */
    @RequestMapping("adjustOperateRechargeOrder")
    public ModelAndView adjustOperateRechargeOrder(HttpServletRequest request) {
        ModelAndView andView = new ModelAndView();
        andView.setViewName("modules/recharge/adjustOperateRechargeOrder");
        String platOrderId = request.getParameter("platOrderId");
        String queryFlag = request.getParameter("queryFlag");
        MchtGatewayRechargeOrder auditRechargeOrder = rechargeService.findRechargeOrderByPlatOrderId(platOrderId);
        if(auditRechargeOrder != null){
            MchtInfo mchtInfo = merchantService.queryByKey(auditRechargeOrder.getMchtId());
            MchtRechargeConfig rechargeConfig = mchtRechargeConfigService.findByRechargeConfigMchtId(auditRechargeOrder.getMchtId());
            andView.addObject("rechargeConfig", rechargeConfig);
            andView.addObject("mchtInfo",mchtInfo);
        }
        andView.addObject("queryFlag",queryFlag);
        andView.addObject("auditRechargeOrder", auditRechargeOrder);
        return andView;
    }


    /**
     * 客服提交审批结果
     * @return
     */
    @RequestMapping("commitAdjustRechargeOrder")
    public String commitAdjustRechargeOrder(HttpServletRequest request){
        //customer
        //operate
        //审批类型
        String auditType = request.getParameter("auditType");
        //审批状态
        String auditStatus = request.getParameter("auditStatus");
        //平台订单号
        String platOrderId = request.getParameter("platOrderId");
        //商户留言
        String customerMessage = request.getParameter("customerMessage");
        //运营留言
        String operateMessage = request.getParameter("operateMessage");

        MchtGatewayRechargeOrder rechargeOrder = new MchtGatewayRechargeOrder();
        if("customer".equalsIgnoreCase(auditType)){
            rechargeOrder.setCustomerAuditTime(new Date());
            rechargeOrder.setCustomerAuditUserId(UserUtils.getUser().getLoginName());
            rechargeOrder.setCustomerAuditUserName(UserUtils.getUser().getName());
            rechargeOrder.setExtend1(customerMessage);
            if("pass".equals(auditStatus)){
                rechargeOrder.setAuditStatus(RechargeAuditEnum.CUSTOMER_PASS.getCode());
            }else if("refuse".equals(auditStatus)){
                rechargeOrder.setUpdateTime(new Date());
                rechargeOrder.setAuditStatus(RechargeAuditEnum.CUSTOMER_REFUSE.getCode());
            }
        }else if("operate".equals(auditType)){
            rechargeOrder.setOperateAuditTime(new Date());
            rechargeOrder.setOperateAuditUserId(UserUtils.getUser().getLoginName());
            rechargeOrder.setOperateAuditUserName(UserUtils.getUser().getName());
            rechargeOrder.setExtend2(operateMessage);
            if("pass".equals(auditStatus)){
                rechargeOrder.setAuditStatus(RechargeAuditEnum.OPERATE_PASS.getCode());
                rechargeOrder.setUpdateTime(new Date());
            }else if("refuse".equals(auditStatus)){
                rechargeOrder.setUpdateTime(new Date());
                rechargeOrder.setAuditStatus(RechargeAuditEnum.OPERATE_REFUSE.getCode());
            }
        }
        rechargeOrder.setPlatOrderId(platOrderId);
        logger.info("提交审批结果:"+ JSONObject.toJSONString(rechargeOrder));
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
        tradeApiRechargePayHandler.processQuery(platOrderId, PayTypeEnum.ZHIFU_WG.getCode());
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
        this.logger.info("请求参数:"+JSONObject.toJSONString(paramMap));
        ModelAndView modelAndView = new ModelAndView();
        MchtGatewayRechargeOrder rechargeOrder = new MchtGatewayRechargeOrder();
        //获取参数
        rechargeOrder.setMchtName(paramMap.get("mchtName"));
        rechargeOrder.setPlatOrderId(paramMap.get("platOrderId"));
        rechargeOrder.setStatus(paramMap.get("status"));
        rechargeOrder.setAuditStatus(paramMap.get("auditStatus"));
        rechargeOrder.setCreateStartTime(paramMap.get("beginDate"));
        rechargeOrder.setCreateEndTime(paramMap.get("endDate"));
        rechargeOrder.setRechargeType(paramMap.get("rechargeType"));
        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        rechargeOrder.setPageInfo(pageInfo);

        List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
        modelAndView.addObject("mchtList", mchtList);

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


        MchtGatewayRechargeOrder statisticsRechargeOrder = new MchtGatewayRechargeOrder();
        BeanUtils.copyProperties(rechargeOrder, statisticsRechargeOrder);

        //总金额
        statisticsRechargeOrder.setStatus(null);
        Long totalAmount = rechargeService.amount(statisticsRechargeOrder);
        //总笔数
        Integer totalTotal = rechargeService.orderCount(statisticsRechargeOrder);
        //成功金额
        statisticsRechargeOrder.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
        Long successAmount = rechargeService.amount(statisticsRechargeOrder);
        //成功笔数
        Integer successTotal = rechargeService.orderCount(statisticsRechargeOrder);


        modelAndView.addObject("totalAmount", totalAmount);
        modelAndView.addObject("totalTotal", totalTotal);
        modelAndView.addObject("successAmount", successAmount);
        modelAndView.addObject("successTotal", successTotal);


        rechargeOrders = buildChanName(rechargeOrders);
        Page page = new Page(pageNo, pageInfo.getPageSize(), orderCount, rechargeOrders, true);
        modelAndView.addObject("page", page);
        modelAndView.addObject("orderCount", orderCount);
        modelAndView.addObject("paramMap",paramMap);

        modelAndView.setViewName("modules/recharge/queryOperateRechargePayOrders");
        return modelAndView;
    }


    public List<MchtGatewayRechargeOrder> buildChanName(List<MchtGatewayRechargeOrder> rechargeOrders){
        if(rechargeOrders == null || rechargeOrders.size() ==0){
            return rechargeOrders;
        }
        List<String> mchtIds = new ArrayList<>();
        for(MchtGatewayRechargeOrder rechargeOrder: rechargeOrders){
            mchtIds.add(rechargeOrder.getMchtId());
        }

        Map<String, MchtInfo> mchtInfoMap = buildMchtInfoMap(mchtIds);
        Map<String, MchtRechargeConfig> rechargeConfigMap = buildMchtRechargeConfigMap(mchtIds);


        for(MchtGatewayRechargeOrder rechargeOrder: rechargeOrders){
            MchtInfo mchtInfo = mchtInfoMap.get(rechargeOrder.getMchtId());
            MchtRechargeConfig rechargeConfig = rechargeConfigMap.get(rechargeOrder.getMchtId());
            if (null != mchtInfo) {
                rechargeOrder.setMchtCode(mchtInfo.getName());
                if("1".equals(rechargeOrder.getRechargeType())){
                    rechargeOrder.setPayType(PayTypeEnum.HUIKUANG_WG.getDesc());
                }else if("2".equals(rechargeOrder.getRechargeType())){
                    rechargeOrder.setPayType(PayTypeEnum.ZHIFU_WG.getDesc());
                }
            }
            rechargeOrder.setMchtCode(mchtInfo.getName());
            rechargeOrder.setRechargeConfig(rechargeConfig);
            ChanMchtPaytype chanMchtPaytype = chanMchtPaytypeService.queryByKey(rechargeOrder.getChanMchtPaytypeId());
            if(chanMchtPaytype == null || chanMchtPaytype.getChanCode() == null){
                continue;
            }
            ChanInfo chanInfo =  channelService.queryByKey(chanMchtPaytype.getChanCode());
            if(chanInfo == null){
                continue;
            }
            rechargeOrder.setChanName(chanInfo.getName());
        }
        return rechargeOrders;
    }

    public Map<String, MchtInfo> buildMchtInfoMap(List<String>  mchtIds){
        Map<String, MchtInfo> mchtInfoMap = new HashMap<>();

        MchtInfo mchtInfoQuery = new MchtInfo();
        mchtInfoQuery.setMchtIds(mchtIds);
        List<MchtInfo> mchtInfos = merchantService.list(mchtInfoQuery);
        if(mchtInfos== null || mchtInfos.size() == 0){
            return mchtInfoMap;
        }
        for(MchtInfo mcht: mchtInfos){
            mchtInfoMap.put(mcht.getId(), mcht);
        }
        return mchtInfoMap;
    }

    public Map<String, MchtRechargeConfig> buildMchtRechargeConfigMap(List<String>  mchtIds){
        Map<String, MchtRechargeConfig> rechargeConfigMap = new HashMap<>();

        MchtRechargeConfig rechargeConfigQuery = new MchtRechargeConfig();
        rechargeConfigQuery.setMchtIds(mchtIds);
        List<MchtRechargeConfig> rechargeConfigs = mchtRechargeConfigService.queryRechargeConfigs(rechargeConfigQuery);

        if(rechargeConfigs == null || rechargeConfigs.size() == 0){
            return rechargeConfigMap;
        }
        for(MchtRechargeConfig rechargeConfig: rechargeConfigs){
            rechargeConfigMap.put(rechargeConfig.getMchtId(), rechargeConfig);
        }
        return rechargeConfigMap;
    }


    /**
     * 查询指定商户的平台余额
     *
     * @param mchtId
     * @return
     */
    private BigDecimal queryPlatBalance(String mchtId) {
        BigDecimal balance = BigDecimal.ZERO;
        try {
            String topUrl = ConfigUtil.getValue("gateway.url");
            if (topUrl.endsWith("/")) {
                topUrl = topUrl.substring(0, topUrl.length() - 1);
            }
            String gatewayUrl = topUrl + "/df/gateway/balanceForAdmin";
            Map<String, String> params = new HashMap<>();
            params.put("mchtId", mchtId);
            logger.info(mchtId + " 查询mchtAccountInfo表商户余额,请求URL: " + gatewayUrl + " 请求参数: " + JSON.toJSONString(params));
            String balanceString = null;
            balanceString = HttpUtil.postConnManager(gatewayUrl, params, true);
            if (StringUtils.isNotBlank(balanceString)) {
                balance = new BigDecimal(balanceString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(mchtId + " 查询mchtAccountInfo表商户余额,返回值(平台余额): " + balance);
        return balance;
    }



    /**
     * @param mchtId
     * @param payType
     * @return
     */
    protected PlatProduct queryProduct(String mchtId, String payType) {
        // 查询该支付商户下的所有商户产品
        MchtProduct mchtProduct = new MchtProduct();
        mchtProduct.setMchtId(mchtId);
        mchtProduct.setIsValid(1); // 是否生效： 1-有效；0-失效
//		mchtProduct.setIsDelete(0); // 删除标识：1-删除；0-有效
        List<MchtProduct> list = mchtProductService.list(mchtProduct);
        // 遍历商户产品信息，取出对应的平台支付产品id，找到对应的支付类型的支付产品
        PlatProduct product = null;
        if(null != list && list.size() > 0){
            for (MchtProduct mprod : list) {
                //TODO 这一块后期会改造，根据平台产品id跟支付类型两个条件来查询，避免每次循环都查询一次库，影响性能
                product = productService.queryByKey(mprod.getProductId());
                //			状态：1-有效，2-无效，3-待审核
                if (null != product) {
                    if(StatusEnum.VALID.getCode().equals(product.getStatus()) && payType.equals(product.getPayType())){
                        break;
                    }else{
                        logger.info("，遍历MchtProduct列表信息，根据productId="+mprod.getProductId()+"，查出的PlatProduct为：" + JSONArray.toJSONString(product)+"，产品停用或类型不符，所以过滤掉此产品");
                        product = null;
                    }
                } else {
                    logger.info("，遍历MchtProduct列表信息，根据productId="+mprod.getProductId()+"，查出的PlatProduct为null");
                }
            }
        }else{
            logger.info("，查询的MchtProduct列表信息为null");
        }
        logger.info("，返回的PlatProduct信息为：" + JSONObject.toJSONString(product));
        return product;
    }

    @RequestMapping(value = "/export")
    public String export(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                         @RequestParam Map<String, String> paramMap) throws IOException {

        this.logger.info("请求参数:"+JSONObject.toJSONString(paramMap));
        MchtGatewayRechargeOrder rechargeOrder = new MchtGatewayRechargeOrder();
        //获取参数
        rechargeOrder.setMchtName(paramMap.get("mchtName"));
        rechargeOrder.setPlatOrderId(paramMap.get("platOrderId"));
        rechargeOrder.setStatus(paramMap.get("status"));
        rechargeOrder.setAuditStatus(paramMap.get("auditStatus"));
        rechargeOrder.setCreateStartTime(paramMap.get("beginDate"));
        rechargeOrder.setCreateEndTime(paramMap.get("endDate"));
        rechargeOrder.setRechargeType(paramMap.get("rechargeType"));

        //设置查询当前用户的
        rechargeOrder.setMchtId(UserUtils.getUser().getLoginName());

        //获得总条数
        int orderCount = rechargeService.countMchtGatewayRechargeOrders(rechargeOrder);

        //计算条数 上限五万条
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:modules/recharge/queryOperateRechargePayOrders";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:modules/recharge/queryOperateRechargePayOrders";
        }

        //获得充值订单信息
        List<MchtGatewayRechargeOrder> rechargeOrders  = rechargeService.queryMchtGatewayRechargeOrders(rechargeOrder);

        if (rechargeOrders == null || rechargeOrders.size() ==0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:modules/recharge/queryOperateRechargePayOrders";
        }
        //获取当前日期，为文件名
        String fileName = DateUtils.formatDate(new Date()) + ".xls";

        String[] headers = {"商户名称", "订单号","上游通道" , "充值方式",
                "订单金额","手续费金额", "订单时间", "订单完成时间"
                ,"订单状态","审核状态","客服审批人","运营审批人"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("充值订单流水表");
        sheet.setColumnWidth(0, 20 * 1256);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow((int) 0);

        int j = 0;
        for (String header : headers) {
            HSSFCell cell = row.createCell((short) j);
            cell.setCellValue(header);
            sheet.autoSizeColumn(j);
            j++;
        }
        if (!Collections3.isEmpty(rechargeOrders)) {
            int rowIndex = 1;//行号
            for (MchtGatewayRechargeOrder orderTemp : rechargeOrders) {
                int cellIndex = 0;
                row = sheet.createRow(rowIndex);
                HSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getMchtCode());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getPlatOrderId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getChanName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getPayType());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(NumberUtils.changeF2Y(String.valueOf(orderTemp.getAmount())));
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(NumberUtils.changeF2Y(String.valueOf(orderTemp.getMchtFeeAmount())));
                cellIndex++;


                cell = row.createCell(cellIndex);
                if (orderTemp.getCreateTime() != null) {
                    cell.setCellValue(DateUtils.formatDate(orderTemp.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (orderTemp.getUpdateTime() != null) {
                    cell.setCellValue(DateUtils.formatDate(orderTemp.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
                }
				cellIndex++;

				cell =row.createCell(cellIndex);
				cell.setCellValue(PayStatusEnum.toEnum(orderTemp.getStatus()).getDesc());
				cellIndex++;

				cell =row.createCell(cellIndex);
				cell.setCellValue(RechargeAuditEnum.toEnum(orderTemp.getAuditStatus()).getDesc());
				cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getCustomerAuditUserName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getOperateAuditUserName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getExtend1());
                cellIndex++;

            }
        }
        wb.write(out);
        out.flush();
        out.close();

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "导出完毕");
        response.setCharacterEncoding("UTF-8");
        return "redirect:modules/recharge/queryOperateRechargePayOrders";
    }

}
