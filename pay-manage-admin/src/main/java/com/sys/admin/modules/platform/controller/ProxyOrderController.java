package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.common.enums.*;
import com.sys.common.util.*;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
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
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
    private MchtAccountDetailService mchtAccountDetailService;
    @Autowired
    private PlatBankService platBankService;
    @Autowired
    private AccountAdminService accountAdminService;

    @Value("${sms_send}")
    private String sms_send;


    /**
     * 代付批次列表
     */
    @RequestMapping(value = {"proxyBatchList", ""})
    public String proxyBatchList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        PlatProxyBatch proxyBatch = new PlatProxyBatch();
        proxyBatch.setId(paramMap.get("batchId"));
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

        model.addAttribute("mchtInfos", mchtInfos);

        //查询商户列表
        Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");

        int proxyCount = proxyBatchService.count(proxyBatch);

        List<PlatProxyBatch> proxyInfoList = proxyBatchService.list(proxyBatch);
        if (!CollectionUtils.isEmpty(proxyInfoList)) {
            for (PlatProxyBatch platProxyBatch : proxyInfoList) {
                platProxyBatch.setMchtId(mchtMap.get(platProxyBatch.getMchtId()));
                platProxyBatch.setPayStatus(ProxyPayBatchStatusEnum.toEnum(platProxyBatch.getPayStatus()).getDesc());
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
        proxyDetail.setPlatBatchId(paramMap.get("batchId"));

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

        List<PlatProxyDetail> newList = new ArrayList<>();
        if (proxyInfoList != null && proxyInfoList.size() != 0) {
            for (PlatProxyDetail info : proxyInfoList) {
                info.setExtend2(mchtMap.get(info.getMchtId()));
                info.setExtend3(channelMap.get(info.getChanId()));
                newList.add(info);
            }
        }

        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, newList, true);
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
     * 发起代付页面
     */
    @RequestMapping("toCommitBatch")
    @RequiresPermissions("mcht:proxy:commit")
    public String toCommitBatch(Model model) {
        String mchtId = UserUtils.getUser().getLoginName();
        MchtInfo mcht = merchantService.queryByKey(mchtId);
        if (null != mcht) {
            model.addAttribute("mchtName", mcht.getName());
        }

        MchtAccountDetail detailQuery = new MchtAccountDetail();
        detailQuery.setMchtId(mchtId);
        detailQuery.setSuffix(DateUtils.formatDate(new Date(), "yyyyMM"));

        //查询余额
        BigDecimal balance = queryPlatBalance(mchtId);
        model.addAttribute("balance", balance);

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

                MchtAccountDetail detailQuery = new MchtAccountDetail();
                MchtAccountDetail mchtAccountDetail;
                detailQuery.setMchtId(mchtId);
                detailQuery.setSuffix(DateUtils.formatDate(new Date(), "yyyyMM"));

                //校验余额是否充足
                BigDecimal balance = queryPlatBalance(mchtId);

                BigDecimal proxyAmount = batch.getTotalAmount().add(batch.getTotalFee());//所需总金额=代付金额+代付手续费
                logger.info(mchtId + "【提交代付】商户ID={} 余额={} 手续费={} 代付金额={}",
                        mchtId, balance, batch.getTotalFee().stripTrailingZeros().toPlainString(), proxyAmount.stripTrailingZeros().toPlainString());
                //余额是否充足校验
                if (balance.compareTo(proxyAmount) >= 0) {
                    JedisUtil.set(IdUtil.REDIS_PROXYPAY_BATCH + batch.getId(), JSON.toJSONString(batch), 2 * 3600);
                    JedisUtil.set(IdUtil.REDIS_PROXYPAY_DETAILS + batch.getId(), JSON.toJSONString(details), 2 * 3600);

                    MchtInfo mcht = merchantService.queryByKey(mchtId);
                    String financeMobile = mcht.getFinanceMobile();
                    if (StringUtils.isBlank(financeMobile)) {
                        redirectAttributes.addFlashAttribute("messageType", "error");
                        redirectAttributes.addFlashAttribute("message", "代付用手机号为空");
                        return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/toCommitBatch";
                    }
                    String mobile = DesUtil32.decode(mcht.getFinanceMobile(), mchtId);
                    logger.info(mchtId + "【提交代付】商户ID=" + mchtId + " 数据库加密代付手机号码=" + mcht + " 解密后手机号码=" + mobile + " 页面显示手机号码隐藏中间7位数字");
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
                message = "代付失败，商户未配置代付费率！";
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
     * 确认代付信息
     */
    @RequestMapping("confirmCommitBatch")
    @RequiresPermissions("mcht:proxy:commit")
    public void confirmCommitBatch(String platBatchId, String smsCode, HttpServletResponse response) throws IOException {
        logger.info("【确认代付】接受参数 代付批次ID={} 验证码={}", platBatchId, smsCode);
        String mchtId = UserUtils.getUser().getLoginName();
        String contentType = "text/plain";
        String respMsg = "fail";
        try {
            //校验代付批次
            if (JedisUtil.get(IdUtil.REDIS_PROXYPAY_BATCH + platBatchId) != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("version", "1.0");
                paramsMap.put("mchtId", mchtId);
                paramsMap.put("biz", PayTypeEnum.SINGLE_DF.getCode());
                paramsMap.put("orderId", platBatchId);
                paramsMap.put("verifyCode", smsCode);
                paramsMap.put("opType", "2");
                String log_moid = mchtId + "-->" + platBatchId;
                String sign = SignUtil.md5Sign(paramsMap, mchtId, log_moid);
                paramsMap.put("sign", sign);

                String url = sms_send + "/gateway/sms/verify";
                logger.info("商户代付校验短信验证码  url=" + url + " 参数=" + JSON.toJSONString(paramsMap));
                String postResp = PostUtil.postForm(url, paramsMap);
                logger.info("商户代付校验短信验证码  url=" + url + " 参数=" + JSON.toJSONString(paramsMap) + " 响应=" + postResp);

                //校验验证码
                if (StringUtils.equals(postResp, "0000")) {
                    logger.info("商户代付校验短信验证码,代付批次ID=" + platBatchId + " 回填校验成功");
                    PlatProxyBatch batch = proxyBatchService.queryByKey(platBatchId);
                    //判断数据库是否存在该批次
                    if (batch == null) {
                        batch = JSON.parseObject(JedisUtil.get(IdUtil.REDIS_PROXYPAY_BATCH + platBatchId), PlatProxyBatch.class);
                        List<PlatProxyDetail> details = JSON.parseArray(JedisUtil.get(IdUtil.REDIS_PROXYPAY_DETAILS + platBatchId), PlatProxyDetail.class);
                        for (PlatProxyDetail detail : details) {
                            // 账户缓存数据
                            CacheMcht cacheMcht = accountAdminService.queryCacheMcht(mchtId);
                            CacheMchtAccount cacheMchtAccount = new CacheMchtAccount();
                            cacheMchtAccount.setType(Integer.valueOf(MchtAccountTypeEnum.PROXYPAY_ACCOUNT.getCode()));
                            cacheMchtAccount.setCacheMcht(cacheMcht);
                            cacheMchtAccount.setPlatProxyDetail(detail);
                            logger.info("代付的入账功能（插入CacheMchtAccount）信息为：" + JSONObject.toJSONString(cacheMchtAccount));
                            int rs = accountAdminService.insert2redisAccTask(cacheMchtAccount);
                            logger.info("代付的入账功能返回结果 rs=" + rs);
                        }
                        int rs = proxyBatchService.saveBatchAndDetails(batch, details);
                        logger.info("代付批次和代付明细入库返回结果 rs=" + rs);
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
    public void sendMsg(String platBatchId, HttpServletResponse response) throws IOException {
        String mchtId = UserUtils.getUser().getLoginName();
        String contentType = "text/plain";
        String respMsg = "fail";
        try {

            if (JedisUtil.get(IdUtil.REDIS_PROXYPAY_BATCH + platBatchId) != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("version", "1.0");
                paramsMap.put("mchtId", mchtId);
                paramsMap.put("biz", "df01");
                paramsMap.put("orderId", platBatchId);
                paramsMap.put("opType", "1");
                String log_moid = mchtId + "-->" + platBatchId;
                String sign = SignUtil.md5Sign(paramsMap, mchtId, log_moid);
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
        batch.setPlatOrderId(batchId);
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
                PlatProxyDetail detail = buildProxyDetail(row, i, batch, fee, platBankMap, seqSet);
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
    private PlatProxyDetail buildProxyDetail(Row row, int i, PlatProxyBatch proxyBatch, BigDecimal fee, Map<String, String> platBankMap, Set<String> seqSet) {
        int k = i + 1;
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
            throw new RuntimeException("第" + k + "行使用的银行不在平台支持的银行列表中!");
        }

        //附言
        Cell cell7 = row.getCell(7);
        remark = getStringData(cell7).trim();

        //创建明细对象
        PlatProxyDetail detail = new PlatProxyDetail();
        String detailId = batchId + "W" + i;
        detail.setId(detailId);
        detail.setPlatBatchId(batchId);
        detail.setMchtSeq(seq);
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

    @RequestMapping(value = "/export")
    public String export(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                         @RequestParam Map<String, String> paramMap) throws IOException {
        PlatProxyDetail proxyDetail = new PlatProxyDetail();
        assemblySearch(paramMap, proxyDetail);

        int orderCount = proxyDetailService.count(proxyDetail);
        //计算条数 上限五万条
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
        }
        //获取数据List
        List<PlatProxyDetail> list = proxyDetailService.list(proxyDetail);
        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
        }

        //查询商户列表
        List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());
        //  上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());


        Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
        Map<String, String> mchtMap = Collections3.extractToMap(mchtInfos, "id", "name");

        if (list != null && list.size() != 0) {
            for (PlatProxyDetail info : list) {
                info.setExtend2(mchtMap.get(info.getMchtId()));
                info.setExtend3(channelMap.get(info.getChanId()));
                info.setPayStatus(ProxyPayDetailStatusEnum.toEnum(info.getPayStatus()).getDesc());
            }
        }

        //获取当前日期，为文件名
        String fileName = DateUtils.formatDate(new Date()) + ".xls";

        String[] headers = {"商户名称", "平台批次订单号", "平台明细订单号", "商户订单号",
                "批次内序号", "收款户名", "平台银行名称", "平台银行编码", "收款账号", "金额(元)", "手续费(元)", "状态", "通道名称", "上游响应", "创建时间", "更新时间"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("代付明细表");
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

        if (!Collections3.isEmpty(list)) {
            int rowIndex = 1;//行号
            for (PlatProxyDetail info : list) {
                int cellIndex = 0;
                row = sheet.createRow(rowIndex);
                HSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(info.getExtend2());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getPlatBatchId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getMchtBatchId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getMchtSeq());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getBankCardName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getBankName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getBankCode());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getBankCardNo());
                cellIndex++;


                cell = row.createCell(cellIndex);
                if (info.getAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), info.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (info.getMchtFee() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), info.getMchtFee()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getPayStatus());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getExtend3());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getReturnMessage2());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (info.getCreateDate() != null) {
                    cell.setCellValue(DateUtils.formatDate(info.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (info.getUpdateDate() != null) {
                    cell.setCellValue(DateUtils.formatDate(info.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
                }
                cellIndex++;

                rowIndex++;
            }
        }
        wb.write(out);
        out.flush();
        out.close();

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "导出完毕");
        response.setCharacterEncoding("UTF-8");
        return "redirect:" + GlobalConfig.getAdminPath() + "/proxy/proxyDetailList";
    }

    private void assemblySearch(Map<String, String> paramMap, PlatProxyDetail proxyDetail) {

        if (StringUtils.isNotBlank(paramMap.get("chanId"))) {
            proxyDetail.setChanId(paramMap.get("chanId"));
        }
        if (StringUtils.isNotBlank(paramMap.get("mchtId"))) {
            proxyDetail.setMchtId(paramMap.get("mchtId"));
        }
        if (StringUtils.isNotBlank(paramMap.get("detailId"))) {
            proxyDetail.setId(paramMap.get("detailId"));
        }
        if (StringUtils.isNotBlank(paramMap.get("payStatus"))) {
            proxyDetail.setPayStatus(paramMap.get("payStatus"));
        }
        if (StringUtils.isNotBlank(paramMap.get("checkStatus"))) {
            proxyDetail.setCheckStatus(paramMap.get("checkStatus"));
        }
        if (StringUtils.isNotBlank(paramMap.get("batchId"))) {
            proxyDetail.setPlatBatchId(paramMap.get("batchId"));
        }
    }
}
