package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.*;
import com.sys.common.util.*;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLDecoder;
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


    /**
     * 代付批次列表
     */
    @RequestMapping(value = {"proxyBatchList", ""})
    public String proxyBatchList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        PlatProxyBatch proxyBatch = new PlatProxyBatch();
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

        int proxyCount = proxyBatchService.count(proxyBatch);
        if (proxyCount == 0) {
            return "modules/proxy/proxyBatchList";
        }

        List<PlatProxyBatch> proxyInfoList = proxyBatchService.list(proxyBatch);

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
    public String toCommitBatch(Model model){
        String mchtId = null;//todo 商户ID,待补充
        BigDecimal balance = null;//todo 商户余额,待补充

        balance = BigDecimal.valueOf(20000);//todo 测试
        mchtId = "17359b78";//todo 测试

        MchtInfo mcht = merchantService.queryByKey(mchtId);
        model.addAttribute("balance",balance);
        model.addAttribute("mcht",mcht);
        return "modules/proxy/commitBatch";
    }

    /**
     * 提交代付
     */
    @RequestMapping("commitBatch")
    public String commitBatch(String mchtId,Double balance,MultipartFile file,Model model,RedirectAttributes redirectAttributes){
        logger.info("【提交代付】接受参数 商户ID={} 余额={}",mchtId,balance);
        String messageType = null;
        String message = null;
        try {
            //检验excel文件
            Sheet sheet = checkFile(mchtId,file);
            //查询代付手续费
            BigDecimal fee = queryFee(mchtId);
            if(fee != null){
                PlatProxyBatch batch = new PlatProxyBatch();
                List<PlatProxyDetail> details = new ArrayList<>();
                //读取数据
                readExcel(mchtId,sheet,fee,batch,details);

                BigDecimal proxyAmount = batch.getTotalAmount().add(batch.getTotalFee());//所需总金额=代付金额+代付手续费
                logger.info("【提交代付】商户ID={} 余额={} 手续费={} 代付金额={}",mchtId,balance,batch.getTotalFee().doubleValue(),proxyAmount.doubleValue());
                //余额是否充足校验
                if(balance.doubleValue()-proxyAmount.doubleValue()>=0){
                    JedisUtil.set(IdUtil.REDIS_PROXYPAY_BATCH+batch.getId(),JSON.toJSONString(batch),2*3600);
                    JedisUtil.set(IdUtil.REDIS_PROXYPAY_DETAILS+batch.getId(),JSON.toJSONString(details),2*3600);

                    MchtInfo mchtInfo = merchantService.queryByKey(mchtId);
                    model.addAttribute("batch",batch);
                    model.addAttribute("details",details);
                    model.addAttribute("proxyFee",batch.getTotalFee().doubleValue());
                    model.addAttribute("proxyAmount",proxyAmount.doubleValue());
                    model.addAttribute("phone",mchtInfo.getPhone());
                    model.addAttribute("mchtId",mchtInfo.getId());
                }else{
                    messageType = "error";
                    message = "代付失败，商户余额不足！";
                }
            }else{
                messageType = "error";
                message = "代付失败，商户未配置代付产品！";
            }
        } catch (Exception e) {
            messageType = "error";
            message = e.getMessage();
        }
        if(StringUtils.equals("error",messageType)){
            redirectAttributes.addFlashAttribute("messageType",messageType);
            redirectAttributes.addFlashAttribute("message",message);
            return "redirect:"+GlobalConfig.getAdminPath()+"/proxy/toCommitBatch";
        }
        return "modules/proxy/confirmCommitBatch";
    }

    /**
     * 确认代付信息
     */
    @RequestMapping("confirmCommitBatch")
    public void confirmCommitBatch(String batchId,String smsCode,HttpServletResponse response)throws IOException {
        logger.info("【确认代付】接受参数 代付批次ID={} 验证码={}",batchId,smsCode);
        String contentType = "text/plain";
        String respMsg = "fail";
        try {
            String cacheCode = JedisUtil.get(IdUtil.REDIS_PROXYPAY_SMS_CODE+batchId);
            //校验验证码
            if(StringUtils.equals(cacheCode,smsCode)){
                PlatProxyBatch batch = proxyBatchService.queryByKey(batchId);
                //判断数据库是否存在该批次
                if(batch == null){
                    batch = JSON.parseObject(JedisUtil.get(IdUtil.REDIS_PROXYPAY_BATCH+batchId),PlatProxyBatch.class);
                    List<PlatProxyDetail> details = JSON.parseArray(
                            JedisUtil.get(IdUtil.REDIS_PROXYPAY_DETAILS+batchId),PlatProxyDetail.class);
                    proxyBatchService.create(batch);
                    for(PlatProxyDetail detail : details){
                        proxyDetailService.create(detail);
                    }
                    respMsg = "ok";
                }else{
                    respMsg = "batch exsit";
                }
            }else{
                respMsg = "smscode error";
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
    public void sendMsg(String batchId,String phone,String mchtId,HttpServletResponse response) throws IOException {
        String contentType = "text/plain";
        String respMsg = "fail";
        try {
            //生成验证码
            int smsCode = NumberUtils.buildRandom(6);
            logger.info("【代付验证码】发送短信 批次号={} 商户号={} 手机号={} 验证码={}",batchId,mchtId,phone,smsCode);
            //发送短信
            boolean status = sendPlatSms(phone,mchtId,smsCode);
            if(status){
                JedisUtil.set(IdUtil.REDIS_PROXYPAY_SMS_CODE+batchId,smsCode+"",300);
                respMsg = "ok";
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
     * 发短信
     **/
    private boolean sendPlatSms(String mobile, String midoid,int smsCode) {
        boolean result = false;

        String sendTime = "";//String	可空	发送时间,为空表示立即发送	yyyyMMddHHmmss 格式
        String appendID = "12";//String	必填	附加号码，例如：固定值 01，表示：展鸿软通 	短信内容中会显示 【展鸿软通】字样，若后期需要扩展新的附加号码，需向我方申请
        // String desMobile = clientRequest.getBody().getMobilePhone();//String	必填	手机号	发送单条消息时，此字段填写11位的手机号码 。群发消息时，此字段为使用逗号分隔的手机号码串，每批发送的手机号数量不得超过500个
        String contents = "尊敬的客户，您的验证码为：" + smsCode + "，请在5分钟内完成验证。如非本人操作，请无视此短信";//String	必填	发送消息内容最长不超过500字	需URL编码，见 备注1
        try {
            contents = URLEncoder.encode(URLEncoder.encode(contents, "UTF-8"), "UTF-8");

            String contentType = "15";//String	必填	消息类型取值有15和8	15：以普通短信形式下发，8：以长短信形式下发
            String spid = "1";//String	必填	用来标识短信端口号 	固定值 1
            String num = "1000";//String	必填	短信有效期，单位 秒	例如：1000 秒
            String pvData = appendID + contents + contentType + mobile + num + sendTime + spid;
            String key = "248125f5e61b41f39d9609d952eeed64";
            String pvalidate = "";//String	必填	签名信息	根据签名工具和双方事先约定好的的秘钥对参数进行加密，加密工具类，及其秘钥参见后文 ：加密秘钥来源 和 加密工具类。	签名生成规则见后文中的：签名生成规则

            pvalidate = MD5Util.MD5Encode(pvData + key);

            String url = "http://115.28.179.55:9000/gdsms/sendSms";
            String reqData = "?sendTime=" + sendTime + "&appendID=" + appendID + "&desMobile=" + mobile//
                    + "&contents=" + contents + "&contentType=" + contentType + "&spid=" + spid + "&num=" + num//
                    + "&pvalidate=" + pvalidate;
            url = url + reqData;
            logger.info( midoid + " 发送短信请求url：" + url);

            String retData = "";
            retData = HttpUtil.get(url);
            retData = URLDecoder.decode(retData, "UTF-8");
            logger.info( midoid + " 发送短信返回值retData：" + retData);

            //{"msg":"请求成功","status":"0"}
            String status = JSON.parseObject(retData).getString("status");
            result = "0".equals(status);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error( midoid + " 发送短信【error】异常，e.msg：" + e.getMessage());
        }
        return result;
    }

    /**
     * 校验excel
     */
    private Sheet checkFile(String mchtId,MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        InputStream is = file.getInputStream();
        Workbook wb;

        if (StringUtils.isBlank(fileName)){
            throw new RuntimeException("导入文档为空!");
        }else if(fileName.toLowerCase().endsWith("xls") || fileName.toLowerCase().endsWith("xlsx")){
            wb = new HSSFWorkbook(is);
        }else{
            throw new RuntimeException("文档格式不正确!");
        }
        if (wb.getNumberOfSheets()< 0){
            throw new RuntimeException("文档中没有工作表!");
        }

        Sheet sheet = wb.getSheetAt(0);

        int rowCount = sheet.getLastRowNum();
        logger.info("商户ID: {} 代付笔数: {}",mchtId,rowCount);
        if(rowCount>100){
            throw new RuntimeException("总笔数大于100条，如果空行较多，为避免提示笔数超限，请在EXCEL文件中选择多行进行整行删除！");
        }
        if(rowCount==0){
            throw new RuntimeException("EXCEL文件中无代付信息！");
        }

        return sheet;
    }

    /**
     * 读取数据
     */
    private void readExcel(String mchtId,Sheet sheet,BigDecimal fee,PlatProxyBatch batch,List<PlatProxyDetail> details){
        double amount = 0.00;// 交易金额
        double totalAmount = 0.00;// 累计交易金额
        int totalCount = 0;// 累计交易条数
        Map<String,String> accountNoMap = new TreeMap<String,String>();//存放收款人卡号
        List<String> accountNoAmountList = new ArrayList<String>();//存放收款人卡号和金额格式为:"收款人卡号-交易金额"

        String bankCity = "";//开户行所在市
        String destAccountName = "";// 收款人账户名
        String destAccountNo = "";// 收款人卡号
        String bankNameTmp = "";//临时银行名称包括银行编号
        String batchId= IdUtil.createProxBatchId("0");//代付批次ID
        batch.setRequesetType(ProxyPayRequestEnum.PLATFORM.getCode());
        batch.setId(batchId);
        batch.setMchtId(mchtId);
        batch.setUserId(null);//todo
        batch.setPayType(PayTypeEnum.SINGLE_DF.getCode());
        batch.setTotalNum(null);//todo
        batch.setPayStatus(ProxyPayBatchStatusEnum.AUDIT_SUCCESS.getCode());
        batch.setCreateTime(new Date());
        batch.setUpdateTime(new Date());
        //创建代付批次
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            int k=i+1;//用于提示错误行号
            Row row = sheet.getRow(i);
            if(row!=null){
                //商户订单号
                //收款人卡号
                Cell cell0 = row.getCell(0);
                if(cell0==null){
                    throw new RuntimeException("第"+k+"行的收款人卡号为空!");
                }
                //收款人户名
                Cell cell1 = row.getCell(1);
                if(cell1==null){
                    throw new RuntimeException("第"+k+"行的收款人户名为空!");
                }
                //银行名称
                Cell cell2 = row.getCell(2);
                if(cell2==null){
                    throw new RuntimeException("第"+k+"行的银行名称为空!");
                }
                //金额
                Cell cell3 = row.getCell(3);
                if(cell3==null){
                    throw new RuntimeException("第"+k+"行的代付金额为空!");
                }else{
                    if(Double.valueOf(getStringData(cell3).trim())<30){
                        throw new RuntimeException("第"+k+"行的代付金额不能小于30元!");
                    }
                    if(Double.valueOf(getStringData(cell3).trim())>50000){
                        throw new RuntimeException("第"+k+"行的代付金额大于50000元!");
                    }
                }
                /**
                 * 6月27日，军哥要求暂时去掉城市校验，用户体验不好
                 */
                Cell cell4 = row.getCell(4);
                bankCity = getStringData(cell4);

                destAccountNo = getStringData(cell0).trim();
                destAccountName = getStringData(cell1).trim();
                bankNameTmp = getStringData(cell2).trim();
                amount = Double.valueOf(getStringData(cell3).trim());
                bankCity = bankCity.trim();
                //如果destAccountNo不在MAP中则将其存入MAP中用于后面统计收款人卡号是否大于20万
                if(accountNoMap.get(destAccountNo)==null){
                    accountNoMap.put(destAccountNo, destAccountNo);
                }
                accountNoAmountList.add(destAccountNo+"-"+amount);//"收款人卡号-交易金额"存入LIST中
                //创建批次明细对象
                PlatProxyDetail detail = new PlatProxyDetail();
                detail.setId(IdUtil.createProxDetailId("0"));
                detail.setBatchId(batchId);
                detail.setMchtId(mchtId);
                detail.setPayType(PayTypeEnum.SINGLE_DF.getCode());
                detail.setChannelTradeId(detail.getId());
                detail.setBankCardNo(destAccountNo);
                detail.setBankCardName(destAccountName);
                detail.setBankName(bankNameTmp);
                detail.setBankLineCode(null);//todo 联行号
                detail.setBankCode(null);//todo 银行编码
                detail.setCity(bankCity);
                detail.setAmount(BigDecimal.valueOf(amount!=0?amount*100:0));//将交易金额转换为"分",保存到对象中
                detail.setPayStatus(ProxyPayDetailStatusEnum.AUDIT_SUCCESS.getCode());
                detail.setMchtFee(fee);
                detail.setCreateDate(new Date());
                detail.setUpdateDate(new Date());


                details.add(detail);
                totalAmount+=amount;
                totalCount++;
            }else{
                throw new RuntimeException("第"+k+"行为空，如果空行较多，为避免提示空行，请在EXCEL文件中选择多行进行整行删除!");
            }
        }

        batch.setTotalAmount(BigDecimal.valueOf(totalAmount).multiply(BigDecimal.valueOf(100)));
        batch.setTotalNum(totalCount);
        BigDecimal proxyFee = fee.multiply(BigDecimal.valueOf(batch.getTotalNum()));//代付手续费=单笔手续费*代付笔数
        batch.setTotalFee(proxyFee);
    }

    /**
     * 查询代付手续费
     */
    private BigDecimal queryFee(String mchtId){
        MchtProduct queryBO = new MchtProduct();
        queryBO.setMchtId(mchtId);
        queryBO.setIsValid(Integer.parseInt(StatusEnum.VALID.getCode()));
        List<MchtProduct> mchtProductList = mchtProductService.list(queryBO);

        if(!Collections3.isEmpty(mchtProductList)){
            for(MchtProduct mchtProduct : mchtProductList){
                PlatProduct product = productService.queryByKey(mchtProduct.getProductId());
                if(StringUtils.equals(product.getPayType(),PayTypeEnum.SINGLE_DF.getCode())){
                    String bizType = FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getCode();
                    String bizRefId = mchtId + "&" + product.getId();
                    PlatFeerate feerate = feerateService.getValidFeerate(bizType, bizRefId);
                    return feerate.getFeeAmount()==null?BigDecimal.valueOf(0):feerate.getFeeAmount();
                }
            }
        }
        return null;
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
}
