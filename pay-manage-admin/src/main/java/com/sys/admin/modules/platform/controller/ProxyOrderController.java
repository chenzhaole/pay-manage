package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.ProxyPayBatchStatusEnum;
import com.sys.common.enums.ProxyPayDetailStatusEnum;
import com.sys.common.enums.ProxyPayRequestEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.*;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.MchtProduct;
import com.sys.core.dao.dmo.PlatBank;
import com.sys.core.dao.dmo.PlatFeerate;
import com.sys.core.dao.dmo.PlatProduct;
import com.sys.core.dao.dmo.PlatProxyBatch;
import com.sys.core.dao.dmo.PlatProxyDetail;
import com.sys.core.service.ChannelService;
import com.sys.core.service.MchtAccountInfoService;
import com.sys.core.service.MchtProductService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatBankService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.service.ProductService;
import com.sys.core.service.ProxyBatchService;
import com.sys.core.service.ProxyDetailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping(value = "${adminPath}/proxy")
public class ProxyOrderController extends BaseController {

    @Autowired
    private ProxyBatchService proxyBatchService;

    @Autowired
    private ProxyDetailService proxyDetailService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MchtProductService mchtProductService;

    @Autowired
    private PlatFeerateService feerateService;
    @Autowired
    private MchtAccountInfoService mchtAccountInfoService;
    @Autowired
    private PlatBankService platBankService;

    @Value("${sms_send}")
    private String sms_send;


    /**
     * 代付批次列表
     */
    @RequestMapping(value = {"proxyBatchList", ""})
    public String proxyBatchList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        PlatProxyBatch proxyBatch = new PlatProxyBatch();
        proxyBatch.setId(paramMap.get("batchId"));
        proxyBatch.setChanId(paramMap.get("chanId"));
        proxyBatch.setMchtId(paramMap.get("mchtId"));
        proxyBatch.setMchtOrderId(paramMap.get("mchtOrderId"));
        proxyBatch.setPayStatus(paramMap.get("payStatus"));
        proxyBatch.setCheckStatus(paramMap.get("checkStatus"));

        //分页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        proxyBatch.setPageInfo(pageInfo);

        //查询商户列表
        List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
        //通道商户支付方式列表
//		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
        //  上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

        model.addAttribute("chanInfos", chanInfoList);
        model.addAttribute("mchtInfos", mchtInfos);
//		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);

        //查询商户列表
        Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");
        Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");

        int proxyCount = proxyBatchService.count(proxyBatch);

        List<PlatProxyBatch> proxyInfoList = proxyBatchService.list(proxyBatch);

        if (!CollectionUtils.isEmpty(proxyInfoList)) {
            for (PlatProxyBatch platProxyBatch : proxyInfoList) {
                platProxyBatch.setMchtId(mchtMap.get(platProxyBatch.getMchtId()));
                platProxyBatch.setChanId(channelMap.get(platProxyBatch.getChanId()));
            }
        }
        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/proxyBatchList";
    }


    /**
     * 代付明细列表
     */
    @RequestMapping(value = {"proxyDetailList", ""})
    public String proxyDetailList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        PlatProxyDetail proxyDetail = new PlatProxyDetail();
        proxyDetail.setChanId(paramMap.get("chanId"));
        proxyDetail.setMchtId(paramMap.get("mchtId"));
        proxyDetail.setId(paramMap.get("detailId"));
        proxyDetail.setPayStatus(paramMap.get("payStatus"));
        proxyDetail.setCheckStatus(paramMap.get("checkStatus"));
        proxyDetail.setBatchId(paramMap.get("batchId"));

        //分页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        proxyDetail.setPageInfo(pageInfo);

        //批次信息
        PlatProxyBatch proxyBatch = proxyBatchService.queryByKey(paramMap.get("batchId"));

        //查询商户列表
        List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
        //  上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
        //  产品
        List<PlatProduct> platProducts = productService.list(new PlatProduct());


        Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
        Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");
        Map<String, String> productMap = Collections3.extractToMap(platProducts, "id", "name");

        if (proxyBatch != null) {
            proxyBatch.setChanId(channelMap.get(proxyBatch.getChanId()));
            proxyBatch.setProductId(productMap.get(proxyBatch.getProductId()));
            proxyBatch.setExtend3(mchtMap.get(proxyBatch.getMchtId()));
            model.addAttribute("proxyBatch", proxyBatch);
        }
        model.addAttribute("chanInfos", chanInfoList);
        model.addAttribute("mchtInfos", mchtInfos);
//		model.addAttribute("chanMchtPaytypes", chanMchtPaytypeList);

        int proxyCount = proxyDetailService.count(proxyDetail);

        List<PlatProxyDetail> proxyInfoList = proxyDetailService.list(proxyDetail);

        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/proxyDetailList";
    }

    /**
     * 代付详情
     */
    @RequestMapping(value = {"proxyDetail", ""})
    public String proxyDetail(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
//        PlatProxyDetail proxyDetail = new PlatProxyDetail();
        String detailId = paramMap.get("detailId");
        String batchId = paramMap.get("batchId");

        PlatProxyDetail proxyDetail = proxyDetailService.queryByKey(detailId);

        //批次信息
        PlatProxyBatch proxyBatch = proxyBatchService.queryByKey(batchId);

        //查询商户列表
        List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
        //  上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());

        Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
        Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");

        if (proxyDetail != null) {
            proxyDetail.setExtend2(mchtMap.get(proxyDetail.getMchtId()));
            proxyDetail.setExtend3(channelMap.get(proxyDetail.getChanId()));
        }
        model.addAttribute("chanInfos", chanInfoList);
        model.addAttribute("mchtInfos", mchtInfos);
        model.addAttribute("proxyBatch", proxyBatch);
        model.addAttribute("proxyDetail", proxyDetail);

        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/proxyDetail";
    }

    /**
     * 代付详情初始化，即在此发起代付
     */
    @RequestMapping(value = {"proxyDetailInit", ""})
    public String proxyDetailInit(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int result;
        String message = "", messageType = null;
        String opLogin = UserUtils.getUser().getLoginName();
        String opName = UserUtils.getUser().getName();
        String operatorUser = opLogin + "-" + opName;

        String oldProxyDetailId = paramMap.get("detailId");
        PlatProxyDetail oldProxyDetail = proxyDetailService.queryByKey(oldProxyDetailId);
        PlatProxyDetail newProxyDetail = (PlatProxyDetail) org.apache.commons.beanutils.BeanUtils.cloneBean(oldProxyDetail);

        if (null == oldProxyDetail) {
            message = "该笔代付数据异常";
            messageType = "error";
            redirectAttributes.addFlashAttribute("messageType", messageType);
            redirectAttributes.addFlashAttribute("message", message);
            logger.info("该笔代付数据异常：" + oldProxyDetail.getId());
            return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
        }

        if (ProxyPayDetailStatusEnum.DF_SUCCESS.getCode().equals(oldProxyDetail.getPayStatus())
                || ProxyPayDetailStatusEnum.RECREATEED.getCode().equals(oldProxyDetail.getPayStatus())) {
            message = "该笔代付已经成功或已重新发起,不可再次重新发起代付";
            messageType = "error";
            redirectAttributes.addFlashAttribute("messageType", messageType);
            redirectAttributes.addFlashAttribute("message", message);
            logger.info("该笔代付已经成功：" + oldProxyDetailId);
            return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
        }

        //代付失败的订单，允许初始化，并且创建一条新的代付订单
        if (ProxyPayDetailStatusEnum.DF_FAIL.getCode().equals(oldProxyDetail.getPayStatus())) {


            //新代付详情ID： "B + yyMMddHHmmss + xxxxxx + mID"
            String newId = "B" + DateUtils2.getNowTimeStr("yyMMddHHmmss") + IdUtil.buildRandom(6) + "2";
            //将该笔代付订单PayStatus值置为 20 即代付未处理
            newProxyDetail.setPayStatus(ProxyPayDetailStatusEnum.DF_INIT.getCode());
            newProxyDetail.setOperatorId(operatorUser);
            newProxyDetail.setId(newId);
            newProxyDetail.setCreateDate(new Date());
            newProxyDetail.setUpdateDate(new Date());
            newProxyDetail.setReturnMessage2("");
            newProxyDetail.setRemark("该笔代付由人工后台重新发起，操作者：" + operatorUser + "，对应旧的代付详情ID：" + oldProxyDetail.getId());

            logger.info("初始化新代付id：" + oldProxyDetailId + "，具体信息为：" + JSON.toJSONString(newProxyDetail));
            int addInt = proxyDetailService.create(newProxyDetail);
            logger.info("初始化新代付：" + newProxyDetail + "，修改数据库结果为：" + addInt);
            if (1 == addInt) {
                oldProxyDetail.setUpdateDate(new Date());
                //将该笔代付订单PayStatus值置为30 即已经重新发起代付
                oldProxyDetail.setPayStatus(ProxyPayDetailStatusEnum.RECREATEED.getCode());
                oldProxyDetail.setRemark("该笔代付已经人工重新发起，操作者：" + operatorUser + "，新创建的代付详情ID：" + newId);
                PlatProxyDetail selected = new PlatProxyDetail();
                selected.setId(oldProxyDetailId);
                int upInt = proxyDetailService.saveByKey(oldProxyDetail);
                message = "该笔代付初始化成功";
                messageType = "success";
                logger.info("该笔代付初始化成功：id=" + oldProxyDetailId);
            } else {
                message = "该笔代付初始化失败";
                messageType = "error";
                logger.info("该笔代付初始化失败：id=" + oldProxyDetailId);
            }
        } else {
            message = "该笔代付数据未失败，不允许初始化id=" + oldProxyDetailId;
            messageType = "error";
            logger.info("该笔代付数据未失败，不允许初始化：id=" + oldProxyDetailId);
        }


        redirectAttributes.addFlashAttribute("messageType", messageType);
        redirectAttributes.addFlashAttribute("message", message);
        response.setCharacterEncoding("UTF-8");
        return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
    }

    /**
     * 发起代付页面
     */
    @RequestMapping("toCommitBatch")
    @RequiresPermissions("mcht:proxy:commit")
    public String toCommitBatch(Model model) {
        String mchtId = UserUtils.getUser().getLoginName();
        BigDecimal balance = mchtAccountInfoService.queryBalance(mchtId, null);
        MchtInfo mcht = merchantService.queryByKey(mchtId);
        model.addAttribute("balance", balance);
        model.addAttribute("mchtName", mcht.getName());
        return "modules/proxy/commitBatch";
    }

    /**
     * 提交代付
     */
    @RequestMapping("commitBatch")
    @RequiresPermissions("mcht:proxy:commit")
    public String commitBatch(MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        String mchtId = UserUtils.getUser().getLoginName();
        String messageType = null;
        String message = null;
        try {
            //检验excel文件
            Sheet sheet = checkFile(mchtId, file);
            //查询代付手续费
            BigDecimal fee = queryFee(mchtId);
            if (fee != null) {
                PlatProxyBatch batch = new PlatProxyBatch();
                List<PlatProxyDetail> details = new ArrayList<>();
                //读取数据
                readExcel(mchtId, sheet, fee, batch, details);

                BigDecimal balance = mchtAccountInfoService.queryBalance(mchtId, null);
                logger.info(mchtId + " 查询mchtAccountInfo表商户余额,返回值=" + balance);
                BigDecimal proxyAmount = batch.getTotalAmount().add(batch.getTotalFee());//所需总金额=代付金额+代付手续费
                logger.info(mchtId + "【提交代付】商户ID={} 余额={} 手续费={} 代付金额={}",
                        mchtId, balance, batch.getTotalFee().stripTrailingZeros().toPlainString(), proxyAmount.stripTrailingZeros().toPlainString());
                //余额是否充足校验
                if (balance.compareTo(proxyAmount) >= 0) {
                    JedisUtil.set(IdUtil.REDIS_PROXYPAY_BATCH + batch.getId(), JSON.toJSONString(batch), 2 * 3600);
                    JedisUtil.set(IdUtil.REDIS_PROXYPAY_DETAILS + batch.getId(), JSON.toJSONString(details), 2 * 3600);

                    MchtInfo mcht = merchantService.queryByKey(mchtId);
                    String mobile = DesUtil32.decode(mcht.getFinanceMobile(), "ZhrtZhrt");
                    logger.info(mchtId + "【提交代付】商户ID="+mchtId+" 数据库加密代付手机号码="+mcht+" 解密后手机号码="+mobile+" 页面显示手机号码隐藏中间7位数字");
                    mobile = mobile.substring(0, 2) + "*****" + mobile.substring(7, mobile.length());

                    model.addAttribute("batch", batch);
                    model.addAttribute("details", details);
                    model.addAttribute("proxyFee", batch.getTotalFee().doubleValue());
                    model.addAttribute("proxyAmount", proxyAmount.doubleValue());
                    model.addAttribute("phone", mobile);

                } else {
                    messageType = "error";
                    message = "代付失败，商户余额不足！";
                }
            } else {
                messageType = "error";
                message = "代付失败，商户未配置代付产品！";
            }
        } catch (Exception e) {
            messageType = "error";
            message = e.getMessage();
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        if (StringUtils.equals("error", messageType)) {
            redirectAttributes.addFlashAttribute("messageType", messageType);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/toCommitBatch";
        }
        return "modules/proxy/confirmCommitBatch";
    }

    /**
     * 确认代付信息
     */
    @RequestMapping("confirmCommitBatch")
    @RequiresPermissions("mcht:proxy:commit")
    public void confirmCommitBatch(String batchId, String smsCode, HttpServletResponse response) throws IOException {
        logger.info("【确认代付】接受参数 代付批次ID={} 验证码={}", batchId, smsCode);
        String mchtId = UserUtils.getUser().getLoginName();
        String contentType = "text/plain";
        String respMsg = "fail";
        try {
            //校验代付批次
            if (JedisUtil.get(IdUtil.REDIS_PROXYPAY_BATCH + batchId) != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("version", "1.0");
                paramsMap.put("mchtId", mchtId);
                paramsMap.put("biz", "df01");
                paramsMap.put("orderId", batchId);
                paramsMap.put("verifyCode", smsCode);
                paramsMap.put("opType", "2");

                String sign = SignUtil.md5Sign(paramsMap, "ZhrtZhrt");
                paramsMap.put("sign", sign);

                String url = sms_send + "/gateway/sms/verify";
                logger.info("商户代付校验短信验证码  url=" + url + " 参数=" + JSON.toJSONString(paramsMap));
                String postResp = PostUtil.postForm(url, paramsMap);
                logger.info("商户代付校验短信验证码  url=" + url + " 参数=" + JSON.toJSONString(paramsMap) + " 响应=" + postResp);

                //校验验证码
                if (StringUtils.equals(postResp, "0000")) {
                    logger.info("商户代付校验短信验证码,代付批次ID="+batchId+" 回填校验成功");
                    PlatProxyBatch batch = proxyBatchService.queryByKey(batchId);
                    //判断数据库是否存在该批次
                    if (batch == null) {
                        batch = JSON.parseObject(JedisUtil.get(IdUtil.REDIS_PROXYPAY_BATCH + batchId), PlatProxyBatch.class);
                        List<PlatProxyDetail> details = JSON.parseArray(JedisUtil.get(IdUtil.REDIS_PROXYPAY_DETAILS + batchId), PlatProxyDetail.class);
                        int rs = proxyBatchService.saveBatchAndDetails(batch, details);
                        logger.info("代付批次和代付明细入库返回结果 rs="+rs);
                        respMsg = "ok";
                    } else {
                        respMsg = "batch exist in db";
                    }
                } else {
                    respMsg = "smscode error";
                }
            } else {
                respMsg = "batch not exist in redis";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.reset();
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(respMsg);
    }




    /**
     * 发送短信验证码
     */
    @RequestMapping("sendMsg")
    @RequiresPermissions("mcht:proxy:commit")
    public void sendMsg(String batchId, HttpServletResponse response) throws IOException {
        String mchtId = UserUtils.getUser().getLoginName();
        String contentType = "text/plain";
        String respMsg = "fail";
        try {

            if (JedisUtil.get(IdUtil.REDIS_PROXYPAY_BATCH + batchId) != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("version", "1.0");
                paramsMap.put("mchtId", mchtId);
                paramsMap.put("biz", "df01");
                paramsMap.put("orderId", batchId);
                paramsMap.put("opType", "1");

                String sign = SignUtil.md5Sign(paramsMap, "ZhrtZhrt");
                paramsMap.put("sign", sign);


                String url = sms_send + "/gateway/sms/send";
                logger.info("商户代付发送短信验证码  url=" + url + " 参数=" + JSON.toJSONString(paramsMap));
                String postResp = PostUtil.postForm(url, paramsMap);
                logger.info("商户代付发送短信验证码  url=" + url + " 参数=" + JSON.toJSONString(paramsMap) + " 响应=" + postResp);

                if (StringUtils.equals(postResp, "0000")) {
                    respMsg = "ok";
                }
            } else {
                respMsg = "batch not exist in redis";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.reset();
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(respMsg);
    }

    /**
     * 校验excel
     */
    private Sheet checkFile(String mchtId, MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        InputStream is = file.getInputStream();
        Workbook wb;

        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("导入文档为空!");
        } else if (fileName.toLowerCase().endsWith("xls") || fileName.toLowerCase().endsWith("xlsx")) {
            wb = new HSSFWorkbook(is);
        } else {
            throw new RuntimeException("文档格式不正确!");
        }
        if (wb.getNumberOfSheets() < 0) {
            throw new RuntimeException("文档中没有工作表!");
        }

        Sheet sheet = wb.getSheetAt(0);

        int rowCount = sheet.getLastRowNum();
        logger.info("商户ID: {} 代付笔数: {}", mchtId, rowCount);
        if (rowCount > 100) {
            throw new RuntimeException("总笔数大于100条，如果空行较多，为避免提示笔数超限，请在EXCEL文件中选择多行进行整行删除！");
        }
        if (rowCount == 0) {
            throw new RuntimeException("EXCEL文件中无代付信息！");
        }

        return sheet;
    }

    /**
     * 读取数据
     */
    private void readExcel(String mchtId, Sheet sheet, BigDecimal fee, PlatProxyBatch batch, List<PlatProxyDetail> details) {
        Map<String, String> platBankMap = getPlatBankMap();
        BigDecimal totalAmount = BigDecimal.valueOf(0);// 累计交易金额
        int totalCount = 0;// 累计交易条数

        String batchId = IdUtil.createProxBatchId("0");//代付批次ID
        batch.setRequesetType(ProxyPayRequestEnum.PLATFORM.getCode());
        batch.setId(batchId);
        batch.setMchtId(mchtId);
        batch.setUserId(UserUtils.getUser().getId().toString());
        batch.setPayType(PayTypeEnum.BATCH_DF.getCode());
        batch.setPayStatus(ProxyPayBatchStatusEnum.AUDIT_SUCCESS.getCode());
        batch.setCreateTime(new Date());
        batch.setUpdateTime(new Date());

        Set<String> seqSet = new HashSet<>();
        //创建代付批次
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            int k = i + 1;//用于提示错误行号
            Row row = sheet.getRow(i);
            if (row != null) {
                PlatProxyDetail detail = buildProxyDetail(row, k, batch, fee, platBankMap, seqSet);
                details.add(detail);
                totalAmount = totalAmount.add(detail.getAmount());
                totalCount++;
            } else {
                throw new RuntimeException("第" + k + "行为空，如果空行较多，为避免提示空行，请在EXCEL文件中选择多行进行整行删除!");
            }
        }

        batch.setTotalAmount(totalAmount);
        batch.setTotalNum(totalCount);
        BigDecimal proxyFee = fee.multiply(BigDecimal.valueOf(batch.getTotalNum()));//代付手续费=单笔手续费*代付笔数
        batch.setTotalFee(proxyFee);
    }

    /**
     * 查询代付手续费
     */
    private BigDecimal queryFee_old(String mchtId) {
        MchtProduct queryBO = new MchtProduct();
        queryBO.setMchtId(mchtId);
        queryBO.setIsValid(Integer.parseInt(StatusEnum.VALID.getCode()));
        List<MchtProduct> mchtProductList = mchtProductService.list(queryBO);

        if (!Collections3.isEmpty(mchtProductList)) {
            for (MchtProduct mchtProduct : mchtProductList) {
                PlatProduct product = productService.queryByKey(mchtProduct.getProductId());
                if (StringUtils.equals(product.getPayType(), PayTypeEnum.BATCH_DF.getCode())) {
                    String bizType = FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getCode();
                    String bizRefId = mchtId + "&" + product.getId();
                    PlatFeerate feerate = feerateService.getValidFeerate(bizType, bizRefId);
                    return feerate.getFeeAmount() == null ? BigDecimal.valueOf(0) : feerate.getFeeAmount();
                }
            }
        }
        return null;
    }

    /**
     * 查询代付手续费
     */
    private BigDecimal queryFee(String mchtId) {
        logger.info(mchtId + " 查询代付手续费[start]");
        BigDecimal rtn = null;
        String bizType = FeeRateBizTypeEnum.MCHT_PAYTYPE_BIZTYPE.getCode();
        String bizRefId = mchtId + "&" + PayTypeEnum.SINGLE_DF.getCode();
        logger.info(mchtId + " 查询代付手续费,查询条件:bizType=" + bizType + ",bizRefId=" + bizRefId);
        PlatFeerate feerate = feerateService.getValidFeerate(bizType, bizRefId);
        logger.info(mchtId + " 查询代付手续费,查询结果feerate:" + JSON.toJSON(feerate));

        if (feerate != null) {
            rtn = feerate.getFeeAmount() == null ? BigDecimal.valueOf(0) : feerate.getFeeAmount();
        }

        logger.info(mchtId + " 查询代付手续费[end] 费率返回值=" + rtn);
        return rtn;
    }


    /**
     * 获取String 的 cell数据
     */

    private String getStringData(Cell cell) {
        String result = "";
        if (cell != null) {
            //为了防止数字被改变,先将单元格设置为字符串类型
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                result = cell.getStringCellValue();
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                Double cellD = cell.getNumericCellValue();
                Integer cellI = cellD.intValue();
                result = String.valueOf(cellI);
            }
        }
        return result;
    }

    /**
     * 构造代付明细对象
     */
    private PlatProxyDetail buildProxyDetail(Row row, int k, PlatProxyBatch proxyBatch, BigDecimal fee, Map<String, String> platBankMap, Set<String> seqSet) {
        String bankCity = null;
        String batchId = proxyBatch.getId();
        String mchtId = proxyBatch.getMchtId();

        String seq = null;
        String bankCardNo = null;
        String certId = null;
        String bankCode = null;
        String bankCardName = null;
        String bankName = null;
        BigDecimal amount = null;
        String remark = null;

        //序号
        Cell cell0 = row.getCell(0);
        seq = getStringData(cell0).trim();
        if (cell0 == null || StringUtils.isBlank(seq)) {
            throw new RuntimeException("第" + k + "行的序号为空!");
        }
        if (seqSet.contains(seq)) {
            throw new RuntimeException("第" + k + "行的序号重复!");
        }
        seqSet.add(seq);

        //收款人卡号
        Cell cell1 = row.getCell(1);
        bankCardNo = getStringData(cell1).trim();
        if (cell1 == null || StringUtils.isBlank(bankCardNo)) {
            throw new RuntimeException("第" + k + "行的收款人卡号为空!");
        }
        //收款人户名
        Cell cell2 = row.getCell(2);
        bankCardName = getStringData(cell2).trim();
        if (cell2 == null || StringUtils.isBlank(bankCardName)) {
            throw new RuntimeException("第" + k + "行的收款人户名为空!");
        }
        //身份证号
        Cell cell3 = row.getCell(3);
        certId = getStringData(cell3).trim();
        if (cell3 == null || StringUtils.isBlank(certId)) {
            throw new RuntimeException("第" + k + "行的收款人身份证号为空!");
        }
        //银行编码
        Cell cell4 = row.getCell(4);
        bankCode = getStringData(cell4).trim();
        if (cell4 == null || StringUtils.isBlank(bankCode)) {
            throw new RuntimeException("第" + k + "行的收款人银行编码为空!");
        } else {
            if (!platBankMap.containsKey(bankCode)) {
                throw new RuntimeException("第" + k + "行的收款人银行编码错误!");
            }
        }

        //金额
        Cell cell5 = row.getCell(5);
        amount = new BigDecimal(getStringData(cell5).trim());
        if (cell5 == null) {
            throw new RuntimeException("第" + k + "行的代付金额为空!");
        } else {
            if (amount.compareTo(BigDecimal.valueOf(30)) == -1) {
                throw new RuntimeException("第" + k + "行的代付金额不能小于30元!");
            }
            if (amount.compareTo(BigDecimal.valueOf(50000)) == 1) {
                throw new RuntimeException("第" + k + "行的代付金额大于50000元!");
            }
        }

        //开户行所在市
        Cell cell6 = row.getCell(6);
        bankCity = getStringData(cell6).trim();

        //银行名称
        bankName = platBankMap.get(bankCode);
        if (StringUtils.isBlank(bankName)) {
            throw new RuntimeException("第" + k + "行的开户行名称错误!");
        }

        //附言
        Cell cell7 = row.getCell(7);
        remark = getStringData(cell7).trim();

        //创建明细对象
        PlatProxyDetail detail = new PlatProxyDetail();
        detail.setId(IdUtil.createProxDetailId("0"));
        detail.setMchtOrderId(seq);
        detail.setBatchId(batchId);
        detail.setMchtId(mchtId);
        detail.setPayType(PayTypeEnum.BATCH_DF.getCode());
        detail.setChannelTradeId(detail.getId());
        detail.setBankCardNo(bankCardNo);
        detail.setBankCardName(bankCardName);
        detail.setBankName(bankName);
        detail.setBankCode(bankCode);
        detail.setCity(bankCity);
        detail.setRemark(remark);
        detail.setCertId(certId);
        detail.setAmount(amount.multiply(BigDecimal.valueOf(100)));//将交易金额转换为"分",保存到对象中
        detail.setPayStatus(ProxyPayDetailStatusEnum.AUDIT_SUCCESS.getCode());
        detail.setMchtFee(fee);
        detail.setCreateDate(new Date());
        detail.setUpdateDate(new Date());
        return detail;
    }

    /**
     * 查询平台银行code
     */
    private Map<String, String> getPlatBankMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (PlatBank bank : platBankService.list(new PlatBank())) {
            map.put(bank.getBankCode(), bank.getBankName());
        }
        return map;
    }
}
