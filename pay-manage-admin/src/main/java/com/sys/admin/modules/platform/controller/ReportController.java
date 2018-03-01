package com.sys.admin.modules.platform.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sys.admin.common.utils.Collections3;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.bo.ReportBO;
import com.sys.admin.modules.platform.bo.ReportFormInfo;
import com.sys.admin.modules.sys.entity.Dict;
import com.sys.boss.api.service.stat.StatReportService;
import com.sys.common.enums.FeeTypeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.ReportTypeEnum;
import com.sys.common.util.DateUtils;
import com.sys.core.dao.dmo.AccAcqFlow;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.ChannelService;
import com.sys.core.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("${adminPath}/platform/report")
public class ReportController extends BaseController {
    @Autowired
    private StatReportService statReportService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ChannelService channelService;
    
    
    @RequestMapping("list")
    public String list(ReportFormInfo reportFormInfo,HttpServletRequest request,  Model model) {
        statReport(reportFormInfo, model);
        return "modules/platform/reportList";
    }
    
    @RequestMapping(value = "/export")
    public void export(ReportFormInfo reportFormInfo,HttpServletResponse response) throws IOException{
        List<ReportBO> reportList = statReport(reportFormInfo, null);
        
        if(StringUtils.equals(reportFormInfo.getReportType(), ReportTypeEnum.REPORT_FENRUN.getCode())){
            String []headers={"交易日期","实际支付商户","申报商户","所属通道","所属服务商","支付类型","支付成功笔数","支付成功金额"
                    ,"申报商户费率","申报商户手续费","上游通道费率","上游通道手续费","服务商清算收益"};
            exportFenRunFile(headers,reportList, response);
        }
        
        else if(StringUtils.equals(reportFormInfo.getReportType(), ReportTypeEnum.REPORT_SHOUDANXIAYOU.getCode())){
            String []headers={"交易日期","实际支付商户","申报商户","所属上游通道","支付类型","支付成功笔数","支付成功金额"
                    ,"实际支付商户费率","实际支付商户手续费","申报商户费率","申报商户手续费","实际支付商户结算金额","费率差额清算收益"};
            exportShouDanXiaYouFile(headers,reportList, response);
        }
        
        else if(StringUtils.equals(reportFormInfo.getReportType(), ReportTypeEnum.REPORT_SHOUDANDUIGONG.getCode())){
            String []headers={"交易日期","实际支付商户","申报商户","所属上游通道","支付类型","支付成功笔数","支付成功金额","实际支付商户费率"
                    ,"实际支付商户手续费","申报商户费率","申报商户手续费","申报商户结算金额","实际支付商户结算金额","清算费率差额收益"};
            exportShouDanDuiGongFile(headers,reportList, response);
        }
        
        else if(StringUtils.equals(reportFormInfo.getReportType(), ReportTypeEnum.REPORT_DAILISHANGFENCHENG.getCode())){
            String []headers={"交易日期","实际支付商户","申报商户","所属代理商","支付类型","支付成功金额","实际支付商户费率"
                    ,"实际支付商户清算金额","平台收益代理商分成占比","代理商分成金额"};
            exportDaiLiShangFenChengFile(headers,reportList, response);
        }
    }
    @SuppressWarnings("unchecked")
    private List<ReportBO> statReport(ReportFormInfo reportFormInfo,  Model model){
        List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
        List<ChanInfo> chanList = channelService.list(new ChanInfo());
        List<Dict> payTypeList = assemblePayTypeList();
        
        List<ReportBO> reportList = Lists.newArrayList();
        if(StringUtils.isNotBlank(reportFormInfo.getQueryDay())){
            String report = statReportService.statReport(reportFormInfo.getReportType(), assembleQueryCondition(reportFormInfo));
            reportList = JSON.parseArray(report, ReportBO.class);
            
            if(!Collections3.isEmpty(reportList)){
                Map<String,String> mchtMap = Collections3.extractToMap(mchtList, "id", "name");
                Map<String,String> chanMap = Collections3.extractToMap(chanList, "id", "name");
                Map<String,String> payTypeMap = Collections3.extractToMap(payTypeList, "value", "label");
                for(ReportBO reportBO : reportList){
                    reportBO.setAgentMchtId(StringUtils.isNotBlank(reportBO.getAgentMchtId())?mchtMap.get(reportBO.getAgentMchtId()):"");
                    reportBO.setPayMchtId(StringUtils.isNotBlank(reportBO.getPayMchtId())?mchtMap.get(reportBO.getPayMchtId()):"");
                    reportBO.setServiceMchtId(StringUtils.isNotBlank(reportBO.getServiceMchtId())?mchtMap.get(reportBO.getServiceMchtId()):"");
                    reportBO.setDeclareMchtId(StringUtils.isNotBlank(reportBO.getDeclareMchtId())?mchtMap.get(reportBO.getDeclareMchtId()):"");
                    reportBO.setChanId(StringUtils.isNotBlank(reportBO.getChanId())?chanMap.get(reportBO.getChanId()):"");
                    reportBO.setPayType(StringUtils.isNotBlank(reportBO.getPayType())?payTypeMap.get(reportBO.getPayType()):"");
                }
            }
        }
        
        if(model!=null){
            model.addAttribute("reportList", reportList);
            model.addAttribute("reportFormInfo", reportFormInfo);
            model.addAttribute("payMchtList", assembleMchtList(mchtList, SignTypeEnum.COMMON_MCHT.getCode()));
            model.addAttribute("declareMchtList",assembleMchtList(mchtList, SignTypeEnum.SIGN_MCHT.getCode()));
            model.addAttribute("serviceMchtList",assembleMchtList(mchtList, SignTypeEnum.SERVER_MCHT.getCode()));
            model.addAttribute("agentMchtList",assembleMchtList(mchtList, SignTypeEnum.CLIENT_MCHT.getCode()));
            model.addAttribute("chanList", chanList);
            model.addAttribute("payTypeList", payTypeList);
        }
        return reportList;
    }
    /**
     * 创建表格样式
     * @param wb 工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(titleFont);
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);
        
        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_LEFT);
        styles.put("data1", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("data2", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        styles.put("data3", style);
        
        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
//      style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);
        
        return styles;
    }
    
    @SuppressWarnings("deprecation")
    private void exportFenRunFile(String[] headers,List<ReportBO> reportList,HttpServletResponse response) throws IOException{
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode("分润报表.xls","UTF-8"));
        OutputStream out = response.getOutputStream();
        
        // 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        Map<String,CellStyle> styles = createStyles(wb);
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("分润报表");  
        sheet.setColumnWidth(0, 20 * 256);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  

        
        int j = 0;
        for(String header : headers){
            HSSFCell cell = row.createCell((short) j);  
            cell.setCellValue(header);  
            cell.setCellStyle(styles.get("header"));  
            sheet.autoSizeColumn(j);
            j++;
        }
        
        if(!Collections3.isEmpty(reportList)){
            int rowIndex = 1;//行号
            for(ReportBO report : reportList){
                row = sheet.createRow(rowIndex);  
                HSSFCell cell = row.createCell(0);  
                cell.setCellValue(report.getTradeDay());
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(1);
                cell.setCellValue(report.getPayMchtId());
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(2);
                cell.setCellValue(report.getDeclareMchtId());
                cell.setCellStyle(styles.get("data1"));
                
                cell =  row.createCell(3);
                cell.setCellValue(report.getChanId());
                cell.setCellStyle(styles.get("data1"));
                
                cell =  row.createCell(4);
                cell.setCellValue(report.getServiceMchtId());
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(5);
                cell.setCellValue(report.getPayType());
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(6);
                cell.setCellValue(report.getPaySuccessNum());
                cell.setCellStyle(styles.get("data3"));
                
                cell =  row.createCell(7);
                cell.setCellValue(report.getPaySuccessAmount().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(8);
                cell.setCellValue(report.getDeclareFeerate().toString()+(StringUtils.equals(report.getDeclareFeeType(), FeeTypeEnum.FIXED.getCode())?"分":"‰"));
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(9);
                cell.setCellValue(report.getDeclareFee().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(10);
                cell.setCellValue(report.getServiceFeerate().toString()+(StringUtils.equals(report.getServiceFeeType(), FeeTypeEnum.FIXED.getCode())?"分":"‰"));
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(11);
                cell.setCellValue(report.getServiceFee().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                cell =  row.createCell(12);
                cell.setCellValue(report.getProfit().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                rowIndex++;
            }
        }
        wb.write(out);  
        out.flush();
        out.close();
    }
    
    @SuppressWarnings("deprecation")
    private void exportShouDanXiaYouFile(String[] headers,List<ReportBO> reportList,HttpServletResponse response) throws IOException{
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode("收单下游报表.xls","UTF-8"));
        OutputStream out = response.getOutputStream();
        
        // 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        Map<String,CellStyle> styles = createStyles(wb);
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("收单下游报表");  
        sheet.setColumnWidth(0, 20 * 256);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        
        int j = 0;
        for(String header : headers){
            HSSFCell cell = row.createCell((short) j);  
            cell.setCellValue(header);  
            cell.setCellStyle(styles.get("header"));  
            sheet.autoSizeColumn(j);
            j++;
        }
        
        if(!Collections3.isEmpty(reportList)){
            int rowIndex = 1;//行号
            for(ReportBO report : reportList){
                row = sheet.createRow(rowIndex); 
                
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(report.getTradeDay());  
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(1);
                cell.setCellValue(report.getPayMchtId());  
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(2);
                cell.setCellValue(report.getDeclareMchtId());
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(3);
                cell.setCellValue(report.getChanId());
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(4);
                cell.setCellValue(report.getPayType());
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(5);
                cell.setCellValue(report.getPaySuccessNum());
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(6);
                cell.setCellValue(report.getPaySuccessAmount().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(7);
                cell.setCellValue(report.getPayFeerate().toString()+(StringUtils.equals(report.getPayFeeType(), FeeTypeEnum.FIXED.getCode())?"分":"‰"));
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(8);
                cell.setCellValue(report.getPayFee().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(9);
                cell.setCellValue(report.getDeclareFeerate().toString()+(StringUtils.equals(report.getDeclareFeeType(), FeeTypeEnum.FIXED.getCode())?"分":"‰"));
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(10);
                cell.setCellValue(report.getDeclareFee().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(11);
                cell.setCellValue(report.getPayClearAmount().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(12);
                cell.setCellValue(report.getProfit().doubleValue());
                cell.setCellStyle(styles.get("data3"));
                
                rowIndex++;
            }
        }
        wb.write(out);  
        out.flush();
        out.close();
    }
    
    @SuppressWarnings("deprecation")
    private void exportShouDanDuiGongFile(String[] headers,List<ReportBO> reportList,HttpServletResponse response) throws IOException{
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode("收单对公报表.xls","UTF-8"));
        OutputStream out = response.getOutputStream();
        
        // 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        Map<String,CellStyle> styles = createStyles(wb);
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("收单对公报表");  
        sheet.setColumnWidth(0, 20 * 256);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        
        int j = 0;
        for(String header : headers){
            HSSFCell cell = row.createCell((short) j);  
            cell.setCellValue(header);  
            cell.setCellStyle(styles.get("header"));  
            sheet.autoSizeColumn(j);
            j++;
        }        
        if(!Collections3.isEmpty(reportList)){
            int rowIndex = 1;//行号
            for(ReportBO report : reportList){
                row = sheet.createRow(rowIndex);  
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(report.getTradeDay());    
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(1);
                cell.setCellValue(report.getPayMchtId());   
                cell.setCellStyle(styles.get("data1"));
                 
                cell = row.createCell(2);
                cell.setCellValue(report.getDeclareMchtId());  
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(3);
                cell.setCellValue(report.getChanId());  
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(4);
                cell.setCellValue(report.getPayType());  
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(5);
                cell.setCellValue(report.getPaySuccessNum());  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(6);
                cell.setCellValue(report.getPaySuccessAmount().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(7);
                cell.setCellValue(report.getPayFeerate().toString()+(StringUtils.equals(report.getPayFeeType(), FeeTypeEnum.FIXED.getCode())?"分":"‰"));  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(8);
                cell.setCellValue(report.getPayFee().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(9);
                cell.setCellValue(report.getDeclareFeerate().toString()+(StringUtils.equals(report.getDeclareFeeType(), FeeTypeEnum.FIXED.getCode())?"分":"‰"));  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(10);
                cell.setCellValue(report.getDeclareFee().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(11);
                cell.setCellValue(report.getDeclareClearAmount().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(12);
                cell.setCellValue(report.getPayClearAmount().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(13);
                cell.setCellValue(report.getProfit().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                rowIndex++;
            }
        }
        wb.write(out);  
        out.flush();
        out.close();
    }
    
    @SuppressWarnings("deprecation")
    private void exportDaiLiShangFenChengFile(String[] headers,List<ReportBO> reportList,HttpServletResponse response) throws IOException{
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode("代理商分成报表.xls","UTF-8"));
        OutputStream out = response.getOutputStream();
        
        // 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook(); 
        Map<String,CellStyle> styles = createStyles(wb);
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("代理商分成报表");  
        sheet.setColumnWidth(0, 20 * 256);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        int j = 0;
        for(String header : headers){
            HSSFCell cell = row.createCell((short) j);  
            cell.setCellValue(header);  
            cell.setCellStyle(styles.get("header"));  
            sheet.autoSizeColumn(j);
            j++;
        }   
        if(!Collections3.isEmpty(reportList)){
            int rowIndex = 1;//行号
            for(ReportBO report : reportList){
                row = sheet.createRow(rowIndex);  
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(report.getTradeDay());    
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(1);
                cell.setCellValue(report.getPayMchtId());   
                cell.setCellStyle(styles.get("data1"));
                 
                cell =  row.createCell(2);
                cell.setCellValue(report.getDeclareMchtId());  
                cell.setCellStyle(styles.get("data1"));
                
                cell =  row.createCell(3);
                cell.setCellValue(report.getAgentMchtId());  
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(4);
                cell.setCellValue(report.getPayType());  
                cell.setCellStyle(styles.get("data1"));
                
                cell = row.createCell(5);
                cell.setCellValue(report.getPaySuccessAmount().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(6);
                cell.setCellValue(report.getPayFeerate().toString()+(StringUtils.equals(report.getPayFeeType(), FeeTypeEnum.FIXED.getCode())?"分":"‰"));  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(7);
                cell.setCellValue(report.getPayClearAmount().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(8);
                cell.setCellValue(report.getAgentFeerate().toString()+(StringUtils.equals(report.getAgentFeeType(), FeeTypeEnum.FIXED.getCode())?"分":"‰"));  
                cell.setCellStyle(styles.get("data3"));
                
                cell = row.createCell(9);
                cell.setCellValue(report.getAgentClearAmount().doubleValue());  
                cell.setCellStyle(styles.get("data3"));
                
                rowIndex++;
            }
        }
        wb.write(out);  
        out.flush();
        out.close();
    }
    
    private AccAcqFlow assembleQueryCondition(ReportFormInfo reportFormInfo){
        if(reportFormInfo == null) {
			return null;
		}
        AccAcqFlow flow = new AccAcqFlow();
        if(StringUtils.isNotBlank(reportFormInfo.getQueryDay())) {
			flow.setTradeDoneTime(DateUtils.parseDate(reportFormInfo.getQueryDay(), "yyyy-MM-dd"));
		}
        if(StringUtils.isNotBlank(reportFormInfo.getPayMchtId())) {
			flow.setPayMchtId(reportFormInfo.getPayMchtId());
		}
        if(StringUtils.isNotBlank(reportFormInfo.getServiceMchtId())) {
			flow.setServiceMchtId(reportFormInfo.getServiceMchtId());
		}
        if(StringUtils.isNotBlank(reportFormInfo.getDeclareMchtId())) {
			flow.setDeclareMchtId(reportFormInfo.getDeclareMchtId());
		}
        if(StringUtils.isNotBlank(reportFormInfo.getAgentMchtId())) {
			flow.setAgentMchtId(reportFormInfo.getAgentMchtId());
		}
        if(StringUtils.isNotBlank(reportFormInfo.getChanId())) {
			flow.setChanId(reportFormInfo.getChanId());
		}
        if(StringUtils.isNotBlank(reportFormInfo.getPayType())) {
			flow.setPayType(reportFormInfo.getPayType());
		}
        return flow;
    }
    
    private List<MchtInfo> assembleMchtList(List<MchtInfo> mchtInfoList,String merchantType){
        List<MchtInfo> payMchtList = Lists.newArrayList();
        for(MchtInfo mcht : mchtInfoList){
            if(StringUtils.contains(mcht.getSignType(),merchantType)){
                payMchtList.add(mcht);
            }
        }
        return payMchtList;
    }
    
    private List<Dict> assemblePayTypeList(){
        List<Dict> dictList = Lists.newArrayList();
        for(PayTypeEnum payType : PayTypeEnum.values()){
            Dict dict = new Dict();
            dict.setLabel(payType.getDesc());
            dict.setValue(payType.getCode());
            dictList.add(dict);
        }
        return dictList;
    }
}
