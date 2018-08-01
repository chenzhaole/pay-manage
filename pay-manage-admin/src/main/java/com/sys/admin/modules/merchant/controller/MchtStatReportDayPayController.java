package com.sys.admin.modules.merchant.controller;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.service.stat.StatReportDayPayDetailService;
import com.sys.boss.api.service.stat.StatReportDayPayService;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.StatReportDayPayDetailBizTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.dmo.StatReportDayPay;
import com.sys.core.dao.dmo.StatReportDayPayDetail;
import com.sys.core.service.ReportDayPayDetailService;
import com.sys.core.service.ReportDayPayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
@RequestMapping("${adminPath}/merchant/statReport")
public class MchtStatReportDayPayController extends BaseController {
    @Autowired
    private ReportDayPayService reportDayPayService;

    @Autowired
    private ReportDayPayDetailService reportDayPayDetailService;

    @Autowired
    StatReportDayPayDetailService statReportDayPayDetailService;


    @Autowired
    StatReportDayPayService statReportDayPayService;

    @RequestMapping("list")
    public String list(String startDate, String endDate, HttpServletRequest request, Model model) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return "modules/merchant/statReportDayPayList";
        }
        User user = UserUtils.getUser();
        String loginName = user.getLoginName();

        StatReportDayPay info = new StatReportDayPay();
        info.setMchtCode(loginName);
        List<StatReportDayPay> list = reportDayPayService.list(info, startDate, endDate);

        model.addAttribute("list", list);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "modules/merchant/statReportDayPayList";

    }

    @RequestMapping("detailList")
    public String detailList(String tradeDate, HttpServletRequest request, Model model) {
        if (StringUtils.isBlank(tradeDate)) {
            return "modules/merchant/statReportDayPayList";
        }

        User user = UserUtils.getUser();
        String loginName = user.getLoginName();

        StatReportDayPayDetail info = new StatReportDayPayDetail();
        info.setPayMchtId(loginName);
        info.setBizType(StatReportDayPayDetailBizTypeEnum.MCHT_BUSINESS_PAY.getCode());
        info.setTradeDate(tradeDate);
        List<StatReportDayPayDetail> payList = reportDayPayDetailService.list(info);

        info.setBizType(StatReportDayPayDetailBizTypeEnum.MCHT_BUSINESS_PROXY.getCode());
        List<StatReportDayPayDetail> proxyList = reportDayPayDetailService.list(info);


        if (payList != null) {
            for (StatReportDayPayDetail detail : payList) {
                detail.setPayType(PayTypeEnum.toEnum(detail.getPayType()).getDesc());
            }
        }

        model.addAttribute("tradeDate", tradeDate);
        model.addAttribute("payList", payList);
        model.addAttribute("proxyList", proxyList);
        return "modules/merchant/statReportDayPayDetailList";

    }

    @RequestMapping(value = "/export")
    public String export(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                         @RequestParam Map<String, String> paramMap) throws IOException {

        if (StringUtils.isBlank(paramMap.get("startDate")) || StringUtils.isBlank(paramMap.get("endDate"))) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "请选择统计日期");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
        }
        User user = UserUtils.getUser();
        String loginName = user.getLoginName();
        //创建查询实体
        StatReportDayPay info = new StatReportDayPay();
        info.setMchtCode(loginName);
        //计算条数 上限五万条
        int orderCount = reportDayPayService.ordeCount(info, paramMap.get("startDate"), paramMap.get("endDate"));
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
        }

        // 访问数据库，得到数据集
        List<StatReportDayPay> list = reportDayPayService.list(info, paramMap.get("startDate"), paramMap.get("endDate"));

        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
        }

        String startDate = paramMap.get("startDate").replaceAll("-", "").substring(4);
        String endDate = paramMap.get("endDate").replaceAll("-", "").substring(4);
        //获取当前日期，为文件名
        String fileName = "REPORT" + startDate + "-" + endDate + ".xls";

        String[] headers = {"日期", "交易金额(元)", "手续费(元)", "结算金额(元)", "代付利润(元)", "代付手续费(元)"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("日报统计");
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
            for (StatReportDayPay detail : list) {
                int cellIndex = 0;
                row = sheet.createRow(rowIndex);
                HSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(detail.getTradeDate());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getPayBizTradeAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getPayBizTradeAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getPayBizProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getPayBizProfitAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getPayBizProfitAmount() != null && detail.getPayBizTradeAmount() != null) {
                    BigDecimal subtract = detail.getPayBizTradeAmount().subtract(detail.getPayBizProfitAmount());
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), subtract);
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getDfBizTradeAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getDfBizTradeAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getDfBizProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getDfBizProfitAmount());
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
        return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/list";
    }

    @RequestMapping(value = "/exportPay")
    public String exportPay(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                            @RequestParam Map<String, String> paramMap) throws IOException {

        //创建查询实体
        StatReportDayPayDetail info = new StatReportDayPayDetail();
        assemblySearch(paramMap, info);

        String tradeDate = info.getTradeDate();
        //计算条数 上限五万条
        int orderCount = reportDayPayDetailService.ordeCount(info);
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/detailList?tradeDate="+tradeDate;
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/detailList?tradeDate="+tradeDate;
        }

        // 访问数据库，得到数据集
        List<StatReportDayPayDetail> list = reportDayPayDetailService.list(info);

        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/detailList?tradeDate="+tradeDate;
        }

        if (list != null) {
            for (StatReportDayPayDetail detail : list) {
                detail.setPayType(PayTypeEnum.toEnum(detail.getPayType()).getDesc());
            }
        }

        String date = paramMap.get("tradeDate").replaceAll("-", "").substring(4);
        //获取当前日期，为文件名
        String fileName = "REPORT-ZF" + date + ".xls";

        String[] headers = {"日期", "支付方式", "交易金额(元)", "手续费(元)", "结算金额(元)"};

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
                cell.setCellValue(detail.getPayType());
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
                if (detail.getTotalProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getTotalProfitAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTradeAmount() != null && detail.getTotalProfitAmount() != null) {
                    BigDecimal subtract = new BigDecimal(detail.getTradeAmount()).subtract(detail.getTotalProfitAmount());
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), subtract);
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
        return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/detailList?tradeDate="+tradeDate;
    }

    @RequestMapping(value = "/exportProxy")
    public String exportProxy(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                              @RequestParam Map<String, String> paramMap) throws IOException {

        //创建查询实体
        StatReportDayPayDetail info = new StatReportDayPayDetail();
        assemblySearch(paramMap, info);
        String tradeDate = info.getTradeDate();
        //计算条数 上限五万条
        int orderCount = reportDayPayDetailService.ordeCount(info);
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/detailList?tradeDate="+tradeDate;
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/detailList?tradeDate="+tradeDate;
        }

        // 访问数据库，得到数据集
        List<StatReportDayPayDetail> list = reportDayPayDetailService.list(info);

        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/detailList?tradeDate="+tradeDate;
        }

        String date = paramMap.get("tradeDate").replaceAll("-", "").substring(4);
        //获取当前日期，为文件名
        String fileName = "REPORT-DF" + date + ".xls";

        String[] headers = {"日期", "代付成功笔数", "代付成功(元)", "代付手续费(元)", "代付失败笔数", "代付失败金额(元)"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("代付交易");
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
                if (detail.getTradeSuccessCount() != null) {
                    cell.setCellValue(detail.getTradeSuccessCount());
                } else {
                    cell.setCellValue(0);
                }
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
                if (detail.getTotalProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getTotalProfitAmount());
                    cell.setCellValue(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTradeFailCount() != null) {
                    cell.setCellValue(detail.getTradeFailCount());
                } else {
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getTradeFailAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), new BigDecimal(detail.getTradeFailAmount()));
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
        return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReport/detailList?tradeDate="+tradeDate;
    }


    private void assemblySearch(Map<String, String> paramMap, StatReportDayPayDetail info) {
        if (StringUtils.isNotBlank(paramMap.get("tradeDate"))) {
            info.setTradeDate(paramMap.get("tradeDate"));
        }
        if (StringUtils.isNotBlank(paramMap.get("bizType"))) {
            info.setBizType(paramMap.get("bizType"));
        }
        User user = UserUtils.getUser();
        String loginName = user.getLoginName();
        info.setPayMchtId(loginName);
    }
}
