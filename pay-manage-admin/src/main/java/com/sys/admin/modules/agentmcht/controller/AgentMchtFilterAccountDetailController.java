package com.sys.admin.modules.agentmcht.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.AccAccountTypeEnum;
import com.sys.common.enums.AccOpTypeEnum;
import com.sys.common.enums.AccTradeTypeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils;
import com.sys.common.util.NumberUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MchtAccountDetailService;
import com.sys.core.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 账务明细controller--过滤某个代理商
 */
@Controller
@RequestMapping("${adminPath}/agentMchtAccountDetail")
public class AgentMchtFilterAccountDetailController extends BaseController {
    @Autowired
    private MchtAccountDetailService mchtAccountDetailService;
    @Autowired
    private AccountAdminService accountAdminService;
    @Autowired
    private MerchantService merchantService;

    /**
     * 账务明细列表
     */
    @RequestMapping(value = {"list", ""})
//    @RequiresPermissions("merchant:accountDetail:list")
    public String list(MchtAccountDetail mchtAccountDetail, HttpServletRequest request, Model model, @RequestParam Map<String, String> paramMap) {

        User user = UserUtils.getUser();
        //代理商id
        String loginName = user.getLoginName();
        MchtInfo agentMchtInfo = merchantService.queryByKey(loginName);
        /**
         COMMON_MCHT("1","支付商户"),
         SIGN_MCHT("2","申报商户"),
         SERVER_MCHT("3","服务商"),
         CLIENT_MCHT("4","代理商"),
         */
        if (null == agentMchtInfo || !SignTypeEnum.CLIENT_MCHT.getCode().equals(agentMchtInfo.getSignType())) {
            String msg = "代理商才能查看该数据";
            logger.info(msg+"："+ JSONObject.toJSONString(agentMchtInfo));
            model.addAttribute("message", msg);
            model.addAttribute("messageType", "error");
            return "modules/agentmcht/agentMchtAccountDetailList";
        }
        //根据代理商id,查出所有的下级商户
        String parentId = loginName;
        MchtInfo selectAgentMchtInfo = new MchtInfo();
        selectAgentMchtInfo.setParentId(parentId);
        List<MchtInfo> agentSubMchtInfolist = merchantService.list(selectAgentMchtInfo);
        logger.info("根据代理商商户号："+parentId+"，查出的下级商户集合为："+JSONObject.toJSONString(agentSubMchtInfolist));
        if (CollectionUtils.isEmpty(agentSubMchtInfolist)) {
            String msg = "未查到该代理商的下级商户";
            logger.info(msg+"："+JSONObject.toJSONString(agentSubMchtInfolist));
            model.addAttribute("message", msg);
            model.addAttribute("messageType", "error");
            return "modules/agentmcht/agentMchtAccountDetailList";
        }
        Map<String, String> agentSubMchtInfoMap = Collections3.extractToMap(agentSubMchtInfolist, "id", "name");
        model.addAttribute("agentSubMchtInfoMap",agentSubMchtInfoMap);

        String createTimeStr = request.getParameter("createTime");
        if(StringUtils.isBlank(createTimeStr)){
            model.addAttribute("createTime", DateUtils.getDate("yyyy-MM-dd"));
        }else{
            model.addAttribute("createTime", createTimeStr);
        }

        String isSelectInfo = request.getParameter("isSelectInfo");
        PageInfo pageInfo = new PageInfo();
        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
            pageNo = Integer.parseInt(pageNoString);
        }
        pageInfo.setPageNo(pageNo);
        mchtAccountDetail.setPageInfo(pageInfo);

        //过滤商户的流水
        StringBuilder sb = new StringBuilder();
        for(MchtInfo info : agentSubMchtInfolist){
            sb.append(info.getMchtCode()).append("&");
        }
        String selectMchtId = sb.toString();
        String subMchtId = paramMap.get("subMchtId");
        if(StringUtils.isNotBlank(subMchtId)){
            //如果查询条件指定了下级商户，则只查出下级商户的流水
            selectMchtId = subMchtId;
        }else if(selectMchtId.endsWith("&")){
            selectMchtId = selectMchtId.substring(0, selectMchtId.length()-1);
        }

        mchtAccountDetail.setMchtId(selectMchtId);

        if (StringUtils.isNotBlank(request.getParameter("pageNo")))
            pageInfo.setPageNo(Integer.parseInt(request.getParameter("pageNo")));

        if (StringUtils.isNotBlank(request.getParameter("pageSize")))
            pageInfo.setPageSize(Integer.parseInt(request.getParameter("pageSize")));

        if (StringUtils.isNotBlank(request.getParameter("createTime"))) {
            mchtAccountDetail.setSuffix(request.getParameter("createTime").replace("-", "").substring(0, 6));
            mchtAccountDetail.setCreateTime(DateUtils.parseDate(request.getParameter("createTime")));
        } else {
            mchtAccountDetail.setSuffix(DateUtils.formatDate(new Date(), "yyyyMM"));
        }
        List<MchtAccountDetail> list = null;
        int count = 0;
        if (StringUtils.isNotBlank(createTimeStr) && checkCreateTime(createTimeStr)) {
            //2018年6月之前的日志 不提供查询，因为没用月表

        } else {
            if (StringUtils.isNotBlank(isSelectInfo)) {
                count = mchtAccountDetailService.count(mchtAccountDetail);
                list = accountAdminService.list(mchtAccountDetail);
                //初始化商户名称
                Map<String, String> mchtMap = Collections3.extractToMap(
                        merchantService.list(new MchtInfo()), "id", "name");
                if(list != null){
                    for (MchtAccountDetail detail : list) {
                        detail.setMchtName(mchtMap.get(detail.getMchtId()));
                        detail.setTradeType(AccTradeTypeEnum.toEnum(detail.getTradeType()).getDesc());
                        detail.setOpType(AccOpTypeEnum.toEnum(detail.getOpType()).getDesc());
                    }
                }
            }
        }

        Page page = new Page(pageInfo.getPageNo(), pageInfo.getPageSize(), count, list, true);

        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/agentmcht/agentMchtAccountDetailList";
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
        User user = UserUtils.getUser();
        //代理商id
        String loginName = user.getLoginName();
        MchtInfo agentMchtInfo = merchantService.queryByKey(loginName);
        /**
         *   COMMON_MCHT("1","支付商户"),
         SIGN_MCHT("2","申报商户"),
         SERVER_MCHT("3","服务商"),
         CLIENT_MCHT("4","代理商"),
         */
        if (null == agentMchtInfo || !SignTypeEnum.CLIENT_MCHT.getCode().equals(agentMchtInfo.getSignType())) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "代理商才能导出该数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtOrder/list";
        }
        //根据代理商id,查出所有的下级商户
        String parentId = loginName;
        MchtInfo selectAgentMchtInfo = new MchtInfo();
        selectAgentMchtInfo.setParentId(parentId);
        List<MchtInfo> agentSubMchtInfolist = merchantService.list(selectAgentMchtInfo);
        logger.info("根据代理商商户号："+parentId+"，查出的下级商户集合为："+JSONObject.toJSONString(agentSubMchtInfolist));
        if (CollectionUtils.isEmpty(agentSubMchtInfolist)) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "未查到该代理商的下级商户");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtOrder/list";
        }


        MchtAccountDetail mchtAccountDetail = new MchtAccountDetail();
        assemblySearch(paramMap, mchtAccountDetail, agentSubMchtInfolist);

        int orderCount = mchtAccountDetailService.count(mchtAccountDetail);
        //计算条数 上限五万条
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtAccountDetail/list";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtAccountDetail/list";
        }
        //获取数据List
        List<MchtAccountDetail> list = accountAdminService.list(mchtAccountDetail);
        if (list == null || list.size() == 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数为0条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtAccountDetail/list";
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
                "平台订单号", /*"账户类型", "记账类型", */ "交易类型", "交易金额(元)", "增加(元)", "减少(元)", "手续费(元)", "可提现金额(元)", "记账时间"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        System.out.println((StringUtils.isNotBlank(paramMap.get("subMchtId"))? mchtMap.get(paramMap.get("subMchtId"))+"_" :"")  + URLEncoder.encode(fileName, "UTF-8"));
        response.setHeader("Content-Disposition", "attachment; filename=" + (StringUtils.isNotBlank(paramMap.get("subMchtId"))? URLEncoder.encode(mchtMap.get(paramMap.get("subMchtId")), "utf-8")+"_" :"")  + URLEncoder.encode(fileName, "UTF-8"));
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

//                cell = row.createCell(cellIndex);
//                cell.setCellValue(accountDetail.getAccountType());
//                cellIndex++;
//
//                cell = row.createCell(cellIndex);
//                cell.setCellValue(accountDetail.getOpType());
//                cellIndex++;

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
                    cell.setCellValue(bigDecimal.setScale(2 , BigDecimal.ROUND_HALF_UP).doubleValue());
                } else if ("代付".equals(accountDetail.getTradeType())) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getTradeFeeAmount());
                    cell.setCellValue(bigDecimal.setScale(2 , BigDecimal.ROUND_HALF_UP).doubleValue());
                } else if ("调账".equals(accountDetail.getTradeType())) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), accountDetail.getTradeFeeAmount());
                    if(bigDecimal !=null ){
                        cell.setCellValue(bigDecimal.setScale(2 , BigDecimal.ROUND_HALF_UP).doubleValue());
                    }else{
                        cell.setCellValue(0);
                    }
                } else {
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
        return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtAccountDetail/list";
    }

    private void assemblySearch(Map<String, String> paramMap, MchtAccountDetail detail, List<MchtInfo> agentSubMchtInfolist) {
        if (StringUtils.isNotBlank(paramMap.get("id"))) {
            detail.setId(paramMap.get("id"));
        }

        //过滤商户的流水
        StringBuilder sb = new StringBuilder();
        for(MchtInfo info : agentSubMchtInfolist){
            sb.append(info.getMchtCode()).append("&");
        }
        String selectMchtId = sb.toString();

        String subMchtId = paramMap.get("subMchtId");
        if(StringUtils.isNotBlank(subMchtId)){
            //如果查询条件指定了下级商户，则只查出下级商户的流水
            selectMchtId = subMchtId;
        }else if(selectMchtId.endsWith("&")){
            selectMchtId = selectMchtId.substring(0, selectMchtId.length()-1);
        }

        if (StringUtils.isNotBlank(selectMchtId)) {
            detail.setMchtId(selectMchtId);
        }
        if (StringUtils.isNotBlank(paramMap.get("mchtOrderId"))) {
            detail.setMchtOrderId(paramMap.get("mchtOrderId"));
        }
        if (StringUtils.isNotBlank(paramMap.get("tradeType"))) {
            detail.setTradeType(paramMap.get("tradeType"));
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
        //初始化页面开始时间
        String createTime = paramMap.get("createTime");
        if (StringUtils.isBlank(createTime)) {
            detail.setSuffix(DateUtils.formatDate(new Date(), "yyyyMM"));
        } else {
            detail.setSuffix(createTime.replace("-", "").substring(0, 6));
            detail.setCreateTime(DateUtils.parseDate(createTime));
        }
    }
}
