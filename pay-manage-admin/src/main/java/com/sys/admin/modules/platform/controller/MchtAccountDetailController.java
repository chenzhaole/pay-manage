package com.sys.admin.modules.platform.controller;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.common.enums.AccAccountTypeEnum;
import com.sys.common.enums.AccOpTypeEnum;
import com.sys.common.enums.AccTradeTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 账务明细controller
 */
@Controller
@RequestMapping("${adminPath}/platform/accountDetail")
public class MchtAccountDetailController extends BaseController {
    @Autowired
    private MchtAccountDetailService mchtAccountDetailService;
    @Autowired
    private AccountAdminService accountAdminService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChanMchtPaytypeService chanMchtPaytypeService;

    /**
     * 账务明细列表
     */
    @RequestMapping(value = {"list", ""})
    @RequiresPermissions("platform:accountDetail:list")
    public String list(MchtAccountDetail mchtAccountDetail, HttpServletRequest request, Model model) {
        PageInfo pageInfo = new PageInfo();
        mchtAccountDetail.setPageInfo(pageInfo);

        if (StringUtils.isNotBlank(request.getParameter("pageNo")))
            pageInfo.setPageNo(Integer.parseInt(request.getParameter("pageNo")));

        if (StringUtils.isNotBlank(request.getParameter("pageSize")))
            pageInfo.setPageSize(Integer.parseInt(request.getParameter("pageSize")));

        String createTimeStr = "";
        if (StringUtils.isNotBlank(request.getParameter("createTime"))) {
            mchtAccountDetail.setSuffix(request.getParameter("createTime").replace("-", "").substring(0, 6));
            mchtAccountDetail.setCreateTime(DateUtils.parseDate(request.getParameter("createTime")));
            createTimeStr = request.getParameter("createTime");
        } else {
            mchtAccountDetail.setSuffix(DateUtils.formatDate(new Date(), "yyyyMM"));
            createTimeStr = DateUtils.getDate();
            mchtAccountDetail.setCreateTime(DateUtils.parseDate(createTimeStr));
        }
        //获取商户列表
        List<MchtInfo> mchtInfos = merchantService.list(new MchtInfo());

        List<MchtAccountDetail> list = null;
        int count = 0;


        if (StringUtils.isNotBlank(createTimeStr) && checkCreateTime(createTimeStr)) {
            //2018年6月之前的日志 不提供查询，因为没用月表

        } else {

            list = accountAdminService.list(mchtAccountDetail);
            count = mchtAccountDetailService.count(mchtAccountDetail);

            //初始化商户名称
            Map<String, String> mchtMap = com.sys.common.util.Collections3.extractToMap(
                    mchtInfos, "id", "name");
            if(list != null){
                for (MchtAccountDetail detail : list) {
                    detail.setMchtName(mchtMap.get(detail.getMchtId()));
                    if(StringUtils.isNotEmpty(detail.getTradeType())){
                        detail.setTradeType(AccTradeTypeEnum.toEnum(detail.getTradeType()).getDesc());
                    }
                    if(StringUtils.isNotEmpty(detail.getOpType())){
                        detail.setOpType(AccOpTypeEnum.toEnum(detail.getOpType()).getDesc());
                    }
                }
            }else{
                logger.info("查询list列表为空");
            }


        }
        Page page = new Page(pageInfo.getPageNo(), pageInfo.getPageSize(), count, true);
        if(mchtAccountDetail!= null){
            model.addAttribute("mchtId", mchtAccountDetail.getMchtId());
        }

        model.addAttribute("mchtInfos", mchtInfos);
        model.addAttribute("list", list);
        model.addAttribute("page", page);
        logger.info("createTimeStr="+createTimeStr);
        model.addAttribute("createTime", createTimeStr);
        return "modules/platform/mchtAccountDetailList";
    }

    /**
     * 2018年6月之前的日志 不提供查询，因为没用月表
     *
     * @return
     */
    private boolean checkCreateTime(String createTimeStr) {
        Date createTime = DateUtils.parseDate(createTimeStr, "yyyy-MM-dd");
        String year = new SimpleDateFormat("yyyy").format(createTime);
        String month = new SimpleDateFormat("MM").format(createTime);
        int yearInt = Integer.parseInt(year);
        if (yearInt < 2018) {
            return true;
        }
        int monthInt = Integer.parseInt(month);
        if (yearInt == 2018 && monthInt < 6) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/export")
    public String export(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
                         @RequestParam Map<String, String> paramMap) throws IOException {
        MchtAccountDetail mchtAccountDetail = new MchtAccountDetail();
        assemblySearch(paramMap, mchtAccountDetail);

        int orderCount = mchtAccountDetailService.count(mchtAccountDetail);
        //计算条数 上限五万条
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/accountDetail/list";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/accountDetail/list";
        }
        //获取数据List
        List<MchtAccountDetail> list = accountAdminService.list(mchtAccountDetail);
        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/platform/accountDetail/list";
        }

        //获取商户列表
        List<MchtInfo> mchtList = merchantService.list(new MchtInfo());

        Map<String, String> mchtMap = Collections3.extractToMap(mchtList, "id", "name");

        for (MchtAccountDetail detail : list) {
                detail.setMchtName(mchtMap.get(detail.getMchtId()));
            detail.setTradeType(AccTradeTypeEnum.toEnum(detail.getTradeType()).getDesc());
            detail.setOpType(AccOpTypeEnum.toEnum(detail.getOpType()).getDesc());
            detail.setAccountType(AccAccountTypeEnum.toEnum(detail.getAccountType()).getDesc());
        }

        //获取当前日期，为文件名
        String fileName = DateUtils.formatDate(new Date()) + ".xls";

        String[] headers = {"账务明细号", "商户名称", "商户号", "商户订单号",
                "平台订单号", "账户类型", "记账类型", "交易类型", "交易金额(元)", "增加(元)", "减少(元)", "手续费(元)", "可提现金额(元)", "记账时间"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("账务明细表");
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
            for (MchtAccountDetail accountDetail : list) {
                int cellIndex = 0;
                row = sheet.createRow(rowIndex);
                HSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(accountDetail.getId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(accountDetail.getMchtName());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(accountDetail.getMchtId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(accountDetail.getMchtOrderId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(accountDetail.getPlatOrderId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(accountDetail.getAccountType());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(accountDetail.getOpType());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(accountDetail.getTradeType());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (accountDetail.getTradeAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getTradeAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (accountDetail.getAddAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getAddAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (accountDetail.getReduceAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getReduceAmount());
                    cell.setCellValue(bigDecimal.setScale(2 , BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                cellIndex++;

                //手续费
                cell = row.createCell(cellIndex);
                if ("支付".equals(accountDetail.getTradeType())) {
                    BigDecimal tradeAmount = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getTradeAmount());
                    BigDecimal addAmount = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getAddAmount());
                    BigDecimal bigDecimal = new BigDecimal(NumberUtils.subtract(tradeAmount.doubleValue() + "", addAmount.doubleValue() + "")).setScale(2, BigDecimal.ROUND_HALF_UP);
                    cell.setCellValue(bigDecimal.doubleValue());
                } else if ("代付".equals(accountDetail.getTradeType())) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getTradeFeeAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
                    cell.setCellValue(bigDecimal.doubleValue());
                } else if("调账".equals(accountDetail.getTradeType())){
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getTradeFeeAmount());
                    if(bigDecimal !=null ){
                        cell.setCellValue(bigDecimal.setScale(2 , BigDecimal.ROUND_HALF_UP).doubleValue());
                    }else{
                        cell.setCellValue(0);
                    }
                }else{
                    cell.setCellValue(0);
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (accountDetail.getCashTotalAmount() != null) {
                    BigDecimal totalAmount = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getCashTotalAmount());
                    BigDecimal feeAmount = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getFreezeTotalAmount());
                    BigDecimal bigDecimal = totalAmount.subtract(feeAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (accountDetail.getCreateTime() != null) {
                    cell.setCellValue(DateUtils.formatDate(accountDetail.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
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
        return "redirect:" + GlobalConfig.getAdminPath() + "/platform/accountDetail/list";
    }

    private void assemblySearch(Map<String, String> paramMap, MchtAccountDetail detail) {
        if (StringUtils.isNotBlank(paramMap.get("id"))) {
            detail.setId(paramMap.get("id"));
        }
        if (StringUtils.isNotBlank(paramMap.get("mchtId"))) {
            detail.setMchtId(paramMap.get("mchtId"));
        }
        if (StringUtils.isNotBlank(paramMap.get("mchtOrderId"))) {
            detail.setMchtOrderId(paramMap.get("mchtOrderId"));
        }
        if (StringUtils.isNotBlank(paramMap.get("platOrderId"))) {
            detail.setPlatOrderId(paramMap.get("platOrderId"));
        }
        if (StringUtils.isNotBlank(paramMap.get("accountType"))) {
            detail.setAccountType(paramMap.get("accountType"));
        }
        if (StringUtils.isNotBlank(paramMap.get("opType"))) {
            detail.setOpType(paramMap.get("opType"));
        }
        if(StringUtils.isNotBlank(paramMap.get("tradeType"))){
            detail.setTradeType(paramMap.get("tradeType"));
        }
        //初始化页面开始时间
        String createTime = paramMap.get("createTime");
        String createTimeStr = "";
        if (StringUtils.isBlank(createTime)) {
            detail.setSuffix(DateUtils.formatDate(new Date(), "yyyyMM"));
            createTimeStr = DateUtils.getDate();
            detail.setCreateTime(DateUtils.parseDate(createTimeStr));
        } else {
            detail.setSuffix(createTime.replace("-", "").substring(0, 6));
            createTimeStr = paramMap.get("createTime");
            detail.setCreateTime(DateUtils.parseDate(createTimeStr));
        }
    }
}
