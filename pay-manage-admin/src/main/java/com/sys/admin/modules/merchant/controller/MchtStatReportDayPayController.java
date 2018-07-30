package com.sys.admin.modules.merchant.controller;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.boss.api.service.stat.StatReportDayPayDetailService;
import com.sys.boss.api.service.stat.StatReportDayPayService;
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
    public String list(String startDate , String endDate , HttpServletRequest request,  Model model) {
        if(StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)){
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
    public String detailList(String startDate , String endDate , HttpServletRequest request,  Model model) {
        if(StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)){
            return "modules/merchant/statReportDayPayList";
        }

        StatReportDayPay info = new StatReportDayPay();
        List<StatReportDayPay> list = reportDayPayService.list(info, startDate, endDate);

        model.addAttribute("list", list);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "modules/merchant/statReportDayPayList";

    }
    
    @RequestMapping("reStat")
    public String reStat( String tradeDate , HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            if(StringUtils.isBlank(tradeDate)){
                String message = "重新统计失败";
                String messageType = "fail";
                redirectAttributes.addFlashAttribute("messageType", messageType);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
            }
            //重新统计日报详情信息；
            StatReportDayPayDetail statReportDayPayDetail = new StatReportDayPayDetail();
            statReportDayPayDetail.setTradeDate(tradeDate);
            reportDayPayDetailService.delStatReportPayList(statReportDayPayDetail);

            statReportDayPayDetailService.reStatReportPayList(tradeDate);

            //重新统计日报汇总信息
            StatReportDayPay statReportDayPay = new StatReportDayPay();
            statReportDayPay.setTradeDate(tradeDate);
            reportDayPayService.delStatReportPayList(statReportDayPay);

            statReportDayPayService.reStatReportList(tradeDate);

            String message = "重新统计成功";
            String messageType = "success";
            redirectAttributes.addFlashAttribute("messageType", messageType);
            redirectAttributes.addFlashAttribute("message", message);
            String s = "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            String message = "重新统计失败";
            String messageType = "fail";
            redirectAttributes.addFlashAttribute("messageType", messageType);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
        }

    }

    @RequestMapping(value = "/export")
    public String exportProxy(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                              @RequestParam Map<String, String> paramMap) throws IOException {

        if(StringUtils.isBlank(paramMap.get("startDate")) || StringUtils.isBlank(paramMap.get("endDate"))){
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
        int orderCount = reportDayPayService.ordeCount(info ,paramMap.get("startDate"),paramMap.get("endDate"));
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
        List<StatReportDayPay> list = reportDayPayService.list(info ,paramMap.get("startDate"),paramMap.get("endDate"));

        if (list == null || list.size() ==0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
        }

        String startDate = paramMap.get("startDate").replaceAll("-","").substring(4);
        String endDate = paramMap.get("endDate").replaceAll("-","").substring(4);
        //获取当前日期，为文件名
        String fileName = "REPORT"+startDate+"-"+endDate+ ".xls";

        String[] headers = {"日期","交易金额(元)" ,"手续费(元)","结算金额(元)" ,"代付利润(元)","代付手续费(元)","操作"};

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
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getPayBizProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getPayBizProfitAmount());
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getPayBizProfitAmount() != null && detail.getPayBizTradeAmount() != null) {
                    BigDecimal subtract = detail.getPayBizTradeAmount().subtract(detail.getPayBizProfitAmount());
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), subtract);
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getDfBizTradeAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getDfBizTradeAmount());
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (detail.getDfProfitAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), detail.getDfProfitAmount());
                    cell.setCellValue(bigDecimal.doubleValue());
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
        return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/statReportDayPay/list";
    }


}
