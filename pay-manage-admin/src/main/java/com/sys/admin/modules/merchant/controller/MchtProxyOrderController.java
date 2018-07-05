package com.sys.admin.modules.merchant.controller;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.ProxyPayBatchStatusEnum;
import com.sys.common.enums.ProxyPayDetailStatusEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatProxyBatch;
import com.sys.core.dao.dmo.PlatProxyDetail;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/mchtProxy")
public class MchtProxyOrderController extends BaseController {

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

    /**
     * 代付批次列表
     */
    @RequestMapping(value = {"proxyBatchList", ""})
    public String proxyBatchList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        String isSearch = paramMap.get("isSearch");
        //登陆用户
        User user = UserUtils.getUser();
        String loginName = user.getLoginName();

        //开始时间
        String beginDate = paramMap.get("beginDate");
        //结束时间
        String endDate = paramMap.get("endDate");
        if(StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)){
//            String msg = this.checkDate(beginDate, endDate);
//            if(!"ok".equals(msg)){
//                logger.info(msg);
//                model.addAttribute("message", msg);
//                model.addAttribute("messageType", "error");
//                model.addAttribute("paramMap",paramMap );
//                return "modules/proxy/mchtProxyBatchList";
//            }
        }

        PlatProxyBatch proxyBatch = new PlatProxyBatch();
        //初始化页面开始时间
        if (StringUtils.isBlank(beginDate)) {
            proxyBatch.setCreateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
            paramMap.put("beginDate", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
        } else {
            paramMap.put("beginDate", beginDate);
            proxyBatch.setCreateTime(DateUtils.parseDate(beginDate));
        }
        //初始化页面结束时间
        if (StringUtils.isBlank(endDate)) {
            proxyBatch.setUpdateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 23:59:59"));
            paramMap.put("endDate", DateUtils.getDate("yyyy-MM-dd") + " 23:59:59");
        } else {
            paramMap.put("endDate", endDate);
            proxyBatch.setUpdateTime(DateUtils.parseDate(endDate));
        }

        //指定商户id
        proxyBatch.setMchtId(loginName);
        //商户代付批次订单号
        proxyBatch.setMchtOrderId(paramMap.get("mchtOrderId"));
        //代付批次状态
        proxyBatch.setPayStatus(paramMap.get("payStatus"));

        //分页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        proxyBatch.setPageInfo(pageInfo);
        //页面带过来isSearch值为1
        int proxyCount = 0;
        List<PlatProxyBatch> proxyInfoList = null;
        MchtInfo mchtInfo = null;
        if(StringUtils.isNotBlank(isSearch) && "1".equals(isSearch)){
            proxyCount = proxyBatchService.count(proxyBatch);
            proxyInfoList = proxyBatchService.list(proxyBatch);
            mchtInfo = merchantService.queryByKey(loginName);
        }
        BigDecimal divide = new BigDecimal(100);
        if (!CollectionUtils.isEmpty(proxyInfoList)) {
            for (PlatProxyBatch platProxyBatch : proxyInfoList) {
                platProxyBatch.setPayStatus(ProxyPayBatchStatusEnum.toEnum(platProxyBatch.getPayStatus()).getDesc());
                //总金额和手续费，转成元,保留四位小数点
                BigDecimal totalAmount = platProxyBatch.getTotalAmount();
                if(null != totalAmount){
                    totalAmount = totalAmount.divide(divide, 4, BigDecimal.ROUND_HALF_UP);
                    platProxyBatch.setTotalAmount(totalAmount);
                }
                BigDecimal totalFee = platProxyBatch.getTotalFee();
                if(null != totalFee){
                    totalFee = totalFee.divide(divide, 4, BigDecimal.ROUND_HALF_UP);
                    platProxyBatch.setTotalFee(totalFee);
                }

                //商户名称
                if(null != mchtInfo){
                    platProxyBatch.setMchtId(mchtInfo.getName());
                }
            }
        }
        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/mchtProxyBatchList";
    }

    /**
     * 开始时间不能大于结束时间，
     * 不支持跨年查询
     * 不支持跨月查询
     * @param beginDateStr
     * @param endDateStr
     * @return
     */
    private String checkDate(String beginDateStr, String endDateStr) {
        Date beginDate = DateUtils.parseDate( beginDateStr,"yyyy-MM-dd HH:mm:ss");
        String beginYearStr = DateUtils.formatDate(beginDate, "yyyy");
        String beginMonthStr = DateUtils.formatDate(beginDate, "MM");
        Date endDate = DateUtils.parseDate( endDateStr,"yyyy-MM-dd HH:mm:ss");
        String endYearStr = DateUtils.formatDate(endDate, "yyyy");
        String endMonthStr = DateUtils.formatDate(endDate, "MM");
        if(!beginYearStr.equals(endYearStr)){
            return "查询时间不能跨年";
        }
        if(!beginMonthStr.equals(endMonthStr)){
            return "暂不支持跨月查询";
        }
        return "ok";
    }

    /**
     * 代付明细列表
     */
    @RequestMapping(value = {"proxyDetailList", ""})
    public String proxyDetailList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        String isSearch = paramMap.get("isSearch");
        //登陆用户
        User user = UserUtils.getUser();
        String loginName = user.getLoginName();

        //开始时间
        String beginDate = paramMap.get("beginDate");
        //结束时间
        String endDate = paramMap.get("endDate");
        if(StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)){
//            String msg = this.checkDate(beginDate, endDate);
//            if(!"ok".equals(msg)){
//                logger.info(msg);
//                model.addAttribute("message", msg);
//                model.addAttribute("messageType", "error");
//                model.addAttribute("paramMap",paramMap );
//                return "modules/proxy/mchtProxyDetailList";
//            }
        }

        PlatProxyDetail proxyDetail = new PlatProxyDetail();
        //初始化页面开始时间
        if (StringUtils.isBlank(beginDate)) {
            proxyDetail.setCreateDate(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
            paramMap.put("beginDate", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
        } else {
            paramMap.put("beginDate", beginDate);
            proxyDetail.setCreateDate(DateUtils.parseDate(beginDate));
        }
        //初始化页面结束时间
        if (StringUtils.isBlank(endDate)) {
            proxyDetail.setUpdateDate(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 23:59:59"));
            paramMap.put("endDate", DateUtils.getDate("yyyy-MM-dd") + " 23:59:59");
        } else {
            paramMap.put("endDate", endDate);
            proxyDetail.setUpdateDate(DateUtils.parseDate(endDate));
        }
        //商户号
        proxyDetail.setMchtId(loginName);
        //商户代付详情流水号M
        proxyDetail.setMchtSeq(paramMap.get("mchtSeq"));
        //代付状态
        proxyDetail.setPayStatus(paramMap.get("payStatus"));
        //商户代付批次流水号
        proxyDetail.setMchtBatchId(paramMap.get("mchtBatchId"));
        //平台明细订单号
        proxyDetail.setId(paramMap.get("id").trim());
        //如果是从批次页面，查询明细信息
        String platBatchId = paramMap.get("platBatchId");
        proxyDetail.setPlatBatchId(platBatchId);
        paramMap.put("platBatchId", platBatchId);
        if(StringUtils.isNotBlank(platBatchId)){
            //查出批次信息
            PlatProxyBatch platProxyBatch = proxyBatchService.queryByKey(platBatchId);
            if(null != platProxyBatch && loginName.equals(platProxyBatch.getMchtId())){
                //金额，保留4为小数
                BigDecimal divice = new BigDecimal(100);

                BigDecimal totalAmount = platProxyBatch.getTotalAmount();
                if(null != totalAmount){
                    totalAmount = totalAmount.divide(divice, 4, BigDecimal.ROUND_HALF_UP);
                    platProxyBatch.setTotalAmount(totalAmount);
                }

                BigDecimal successAmount = platProxyBatch.getSuccessAmount();
                if(null != successAmount){
                    successAmount = successAmount.divide(divice, 4, BigDecimal.ROUND_HALF_UP);
                    platProxyBatch.setSuccessAmount(successAmount);
                }

                BigDecimal failAmount = platProxyBatch.getFailAmount();
                if(null != failAmount){
                    failAmount = failAmount.divide(divice, 4, BigDecimal.ROUND_HALF_UP);
                    platProxyBatch.setFailAmount(failAmount);
                }
                model.addAttribute("proxyBatch", platProxyBatch);
            }
        }

        //分页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString)) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        proxyDetail.setPageInfo(pageInfo);
        int proxyCount = 0;
        List<PlatProxyDetail> proxyDetailInfoList = null;
        MchtInfo mchtInfo = null;
        //页面带过来isSearch值为1
        if(StringUtils.isNotBlank(isSearch) && "1".equals(isSearch)){
            proxyCount = proxyDetailService.count(proxyDetail);
            proxyDetailInfoList =  proxyDetailService.list(proxyDetail);
            mchtInfo = merchantService.queryByKey(loginName);
        }
        BigDecimal divide = new BigDecimal(100);
        if(!CollectionUtils.isEmpty(proxyDetailInfoList)){
            for(PlatProxyDetail platProxyDetail : proxyDetailInfoList){
//                platProxyDetail.setPayStatus(ProxyPayBatchStatusEnum.toEnum(platProxyDetail.getPayStatus()).getDesc());
                //金额和手续费，转成元,保留四位小数点
                BigDecimal amount = platProxyDetail.getAmount();
                if(null != amount){
                    amount = amount.divide(divide, 4, BigDecimal.ROUND_HALF_UP);
                    platProxyDetail.setAmount(amount);
                }
                BigDecimal totalFee = platProxyDetail.getMchtFee();
                if(null != totalFee){
                    totalFee = totalFee.divide(divide, 4, BigDecimal.ROUND_HALF_UP);
                    platProxyDetail.setMchtFee(totalFee);
                }

                platProxyDetail.setExtend1(mchtInfo.getName());
            }
        }

        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyDetailInfoList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/mchtProxyDetailList";
    }

    /**
     * 代付详情
     */
    @RequestMapping(value = {"proxyDetail", ""})
    public String proxyDetail(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap) {
        String detailId = paramMap.get("detailId");
        PlatProxyDetail proxyDetail = proxyDetailService.queryByKey(detailId);
        //批次信息
        PlatProxyBatch proxyBatch = null;
        if (proxyDetail != null) {
            //登陆用户
            User user = UserUtils.getUser();
            String loginName = user.getLoginName();
            MchtInfo mchtInfo = merchantService.queryByKey(loginName);
            if(null != mchtInfo){
                proxyDetail.setExtend2(mchtInfo.getName());
            }
            proxyBatch = proxyBatchService.queryByKey(proxyDetail.getPlatBatchId());
        }
        model.addAttribute("proxyBatch", proxyBatch);
        model.addAttribute("proxyDetail", proxyDetail);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/mchtProxyDetail";
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
            return "redirect:" + GlobalConfig.getAdminPath() + "/mchtProxy/proxyDetailList";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/mchtProxy/proxyDetailList";
        }
        //获取数据List
        List<PlatProxyDetail> list = proxyDetailService.list(proxyDetail);
        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/mchtProxy/proxyDetailList";
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

        String[] headers = { "平台明细订单号", "商户代付批次号",
                "商户名称", "收款户名",  "收款人账号", "金额(元)", "手续费(元)", "代付明细状态", "创建时间", "更新时间"};

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
                cell.setCellValue(info.getId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getMchtBatchId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getExtend2());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(info.getBankCardName());
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
        return "redirect:" + GlobalConfig.getAdminPath() + "/mchtProxy/proxyDetailList";
    }

    private void assemblySearch(Map<String, String> paramMap, PlatProxyDetail proxyDetail) {
        if (StringUtils.isNotBlank(paramMap.get("id"))) {
            proxyDetail.setId(paramMap.get("id").trim());
        }
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
        //开始时间
        String beginDate = paramMap.get("beginDate");
        //结束时间
        String endDate = paramMap.get("endDate");
        //初始化页面开始时间
        if (StringUtils.isBlank(beginDate)) {
            proxyDetail.setCreateDate(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
            paramMap.put("beginDate", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
        } else {
            paramMap.put("beginDate", beginDate);
            proxyDetail.setCreateDate(DateUtils.parseDate(beginDate));
        }
        //初始化页面结束时间
        if (StringUtils.isBlank(endDate)) {
            proxyDetail.setUpdateDate(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 23:59:59"));
            paramMap.put("endDate", DateUtils.getDate("yyyy-MM-dd") + " 23:59:59");
        } else {
            paramMap.put("endDate", endDate);
            proxyDetail.setUpdateDate(DateUtils.parseDate(endDate));
        }
        User user = UserUtils.getUser();
        if (user != null) {
            String loginName = user.getLoginName();
            proxyDetail.setMchtId(loginName);
        }

    }

}
