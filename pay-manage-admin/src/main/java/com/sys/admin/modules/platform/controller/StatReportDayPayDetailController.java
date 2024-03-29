package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.web.BaseController;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.StatReportDayPayDetailBizTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.dmo.StatReportDayPayDetail;
import com.sys.core.service.ReportDayPayDetailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("${adminPath}/platform/statReportDayPayDetail")
public class StatReportDayPayDetailController extends BaseController {

    @Autowired
    private ReportDayPayDetailService reportDayPayDetailService;

    @RequiresPermissions("platform:statReportDayPayDetail:list")
    @RequestMapping("list")
    public String list(String startDate,String endDate, HttpServletRequest request, Model model) {
        if (StringUtils.isBlank(startDate)||StringUtils.isBlank(endDate)) {
            return "modules/platform/statReportDayPayDetailList";
        }

        StatReportDayPayDetail info = new StatReportDayPayDetail();
        info.setBizType(StatReportDayPayDetailBizTypeEnum.BUSINESS_PAY.getCode());
        info.setTradeBeginDate(startDate);
        info.setTradeEndDate(endDate);
        List<StatReportDayPayDetail> payList = reportDayPayDetailService.list(info);

        info.setBizType(StatReportDayPayDetailBizTypeEnum.BUSINESS_PROXY.getCode());
        List<StatReportDayPayDetail> proxyList = reportDayPayDetailService.list(info);

        info.setBizType(StatReportDayPayDetailBizTypeEnum.CHONG_ZHI.getCode());
        List<StatReportDayPayDetail> chongzhiList = reportDayPayDetailService.list(info);

        if (payList != null) {
            for (StatReportDayPayDetail detail : payList) {
                PayTypeEnum p = PayTypeEnum.toEnum(detail.getPayType());
                String desc = p == null ? "" : p.getDesc();
                detail.setPayType(desc);
            }
        }

        if(chongzhiList!=null){
            for (StatReportDayPayDetail detail : chongzhiList) {
                PayTypeEnum p = PayTypeEnum.toEnum(detail.getPayType());
                String desc = p == null ? "" : p.getDesc();
                detail.setPayType(desc);
            }
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("payList", payList);
        model.addAttribute("proxyList", proxyList);
        model.addAttribute("chongzhiList",chongzhiList);
        return "modules/platform/statReportDayPayDetailList";
    }

    @RequestMapping(value = "/exportPay")
    public String export(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                         @RequestParam Map<String, String> paramMap) throws IOException {

        if(StringUtils.isBlank(paramMap.get("startDate")) || StringUtils.isBlank(paramMap.get("endDate"))){
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "请选择统计日期");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }
        //创建查询实体
        StatReportDayPayDetail info = new StatReportDayPayDetail();
        assemblySearch(paramMap, info);
        logger.info("导出支付运营日报详情"+ JSON.toJSON(paramMap));

        //计算条数 上限五万条
        int orderCount = reportDayPayDetailService.ordeCount(info);
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }

        // 访问数据库，得到数据集
        List<StatReportDayPayDetail> list = reportDayPayDetailService.list(info);

        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }

        if (list != null) {
            for (StatReportDayPayDetail detail : list) {
                String payType = detail.getPayType();
                PayTypeEnum pt = PayTypeEnum.toEnum(payType);
                detail.setPayType(pt!=null?pt.getDesc():"未知");
                if(pt==null){
                    logger.error("运营日报支付详情导出:"+payType+"的支付方式不存在");
                }
            }
        }

        String startDate = paramMap.get("startDate").replaceAll("-", "").substring(4);
        String endDate = paramMap.get("endDate").replaceAll("-", "").substring(4);
        //获取当前日期，为文件名
        String fileName = "REPORT-ZF" + startDate+"-"+ endDate+ ".xls";

        String[] headers = {"日期", "代理商名称", "商户名称", "支付方式",
                "通道名称", "交易金额(元)", "通道费率(‰)", "商户费率(‰)", "代理商费率(‰)", "代理商分润(元)", "利润(元)", "成功笔数", "交易笔数", "成功率"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("支付业务");
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
            for (StatReportDayPayDetail detail : list) {
                int cellIndex = 0;
                row = sheet.createRow(rowIndex);
                HSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getTradeDate());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getAgentMchtName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getPayMchtName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getPayType());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getCmpName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTradeAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), new BigDecimal(detail.getTradeAmount()));
                    cell.setCellValue(bigDecimal.doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getCmpFeerate() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(1), detail.getCmpFeerate());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getPayMchtFeerate() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(1), detail.getPayMchtFeerate());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getAgenMchtFeerate() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(1), detail.getAgenMchtFeerate());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getAgentMchtProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getAgentMchtProfitAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTotalProfitAmount() != null) {
                    BigDecimal bigDecimal = detail.getTotalProfitAmount().divide(new BigDecimal(100));
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getTradeSuccessCount());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getTradeTotalCount());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTradeSuccessCount() != null && detail.getTradeTotalCount() != null) {
                    double successRate = (double) detail.getTradeSuccessCount() / (double) detail.getTradeTotalCount() * 100;
                    cell.setCellValue(new BigDecimal(successRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "%");
                } else {
                    cell.setCellValue(0);
                }
                rowIndex++;

            }
        }
        wb.write(out);
        out.flush();
        out.close();

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "导出完毕");
        response.setCharacterEncoding("UTF-8");
        return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPayDetail/list";
    }

    @RequestMapping(value = "/exportProxy")
    public String exportProxy(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                              @RequestParam Map<String, String> paramMap) throws IOException {

        if(StringUtils.isBlank(paramMap.get("startDate")) || StringUtils.isBlank(paramMap.get("endDate"))){
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "请选择统计日期");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }
        //创建查询实体
        StatReportDayPayDetail info = new StatReportDayPayDetail();
        assemblySearch(paramMap, info);
        logger.info("导出代付运营日报详情"+ JSON.toJSON(paramMap));

        //计算条数 上限五万条
        int orderCount = reportDayPayDetailService.ordeCount(info);
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }

        // 访问数据库，得到数据集
        List<StatReportDayPayDetail> list = reportDayPayDetailService.list(info);

        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }

        String startDate = paramMap.get("startDate").replaceAll("-", "").substring(4);
        String endDate = paramMap.get("endDate").replaceAll("-", "").substring(4);
        //获取当前日期，为文件名
        String fileName = "REPORT-DF" +startDate+"-"+endDate + ".xls";

        String[] headers = {"日期", "商户名称", "交易金额(元)", "入账金额(元)", "利润(元)"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("代付业务");
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
            for (StatReportDayPayDetail detail : list) {
                int cellIndex = 0;
                row = sheet.createRow(rowIndex);
                HSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getTradeDate());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getPayMchtName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTradeAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), new BigDecimal(detail.getTradeAmount()));
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getAccAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getAccAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTotalProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getTotalProfitAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                rowIndex++;

            }
        }
        wb.write(out);
        out.flush();
        out.close();

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "导出完毕");
        response.setCharacterEncoding("UTF-8");
        return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPayDetail/list";
    }

    @RequestMapping(value = "/exportChongzhi")
    public String exportChongzhi(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                         @RequestParam Map<String, String> paramMap) throws IOException {
        if(StringUtils.isBlank(paramMap.get("startDate")) || StringUtils.isBlank(paramMap.get("endDate"))){
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "请选择统计日期");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }
        //创建查询实体
        StatReportDayPayDetail info = new StatReportDayPayDetail();
        assemblySearch(paramMap, info);
        logger.info("导出充值运营日报详情"+ JSON.toJSON(paramMap));

        //计算条数 上限五万条
        int orderCount = reportDayPayDetailService.ordeCount(info);
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }

        // 访问数据库，得到数据集
        List<StatReportDayPayDetail> list = reportDayPayDetailService.list(info);

        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPay/list";
        }

        if (list != null) {
            for (StatReportDayPayDetail detail : list) {
                String payType = detail.getPayType();
                PayTypeEnum pt = PayTypeEnum.toEnum(payType);
                detail.setPayType(pt!=null?pt.getDesc():"未知");
                if(pt==null){
                    logger.error("运营日报充值详情导出:"+payType+"的支付方式不存在");
                }
            }
        }

        String startDate = paramMap.get("startDate").replaceAll("-", "").substring(4);
        String endDate = paramMap.get("endDate").replaceAll("-", "").substring(4);
        //获取当前日期，为文件名
        String fileName = "REPORT-CZ" +startDate+"-"+endDate+ ".xls";

        String[] headers = {"日期", "代理商名称", "商户名称", "支付方式",
                "通道名称", "交易金额(元)", "通道费率(‰)", "商户费率(‰)", "代理商费率(‰)", "代理商分润(元)", "利润(元)", "成功笔数", "交易笔数", "成功率"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("充值业务");
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
            for (StatReportDayPayDetail detail : list) {
                int cellIndex = 0;
                row = sheet.createRow(rowIndex);
                HSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getTradeDate());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getAgentMchtName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getPayMchtName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getPayType());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getCmpName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTradeAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), new BigDecimal(detail.getTradeAmount()));
                    cell.setCellValue(bigDecimal.doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getCmpFeerate() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(1), detail.getCmpFeerate());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getPayMchtFeerate() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(1), detail.getPayMchtFeerate());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getAgenMchtFeerate() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(1), detail.getAgenMchtFeerate());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getAgentMchtProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getAgentMchtProfitAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTotalProfitAmount() != null) {
                    BigDecimal bigDecimal = detail.getTotalProfitAmount().divide(new BigDecimal(100));
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getTradeSuccessCount());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getTradeTotalCount());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTradeSuccessCount() != null && detail.getTradeTotalCount() != null) {
                    double successRate = (double) detail.getTradeSuccessCount() / (double) detail.getTradeTotalCount() * 100;
                    cell.setCellValue(new BigDecimal(successRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "%");
                } else {
                    cell.setCellValue(0);
                }
                rowIndex++;

            }
        }
        wb.write(out);
        out.flush();
        out.close();

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "导出完毕");
        response.setCharacterEncoding("UTF-8");
        return "redirect:" + GlobalConfig.getAdminPath() + "/platform/statReportDayPayDetail/list";
    }


    private void assemblySearch(Map<String, String> paramMap, StatReportDayPayDetail info) {
        if (StringUtils.isNotBlank(paramMap.get("startDate"))) {
            info.setTradeBeginDate(paramMap.get("startDate"));
        }
        if (StringUtils.isNotBlank(paramMap.get("endDate"))) {
            info.setTradeEndDate(paramMap.get("endDate"));
        }
        if (StringUtils.isNotBlank(paramMap.get("bizType"))) {
            info.setBizType(paramMap.get("bizType"));
        }
    }

}
