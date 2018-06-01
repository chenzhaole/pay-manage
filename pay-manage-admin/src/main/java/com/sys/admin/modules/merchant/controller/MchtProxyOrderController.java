package com.sys.admin.modules.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.platform.service.AccountAdminService;
import com.sys.admin.modules.sys.entity.User;
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

        PlatProxyBatch proxyBatch = new PlatProxyBatch();
        //初始化页面开始时间
        String beginDate = paramMap.get("beginDate");
        if (StringUtils.isBlank(beginDate)) {
            proxyBatch.setCreateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
            paramMap.put("beginDate", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
        } else {
            paramMap.put("beginDate", beginDate);
            proxyBatch.setCreateTime(DateUtils.parseDate(beginDate));
        }
        String endDate = paramMap.get("endDate");
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
        if(StringUtils.isNotBlank(isSearch) && "1".equals(isSearch)){
            proxyCount = proxyBatchService.count(proxyBatch);
            proxyInfoList = proxyBatchService.list(proxyBatch);
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
            }
        }
        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyInfoList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/mchtProxyBatchList";
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

        PlatProxyDetail proxyDetail = new PlatProxyDetail();
        //初始化页面开始时间
        String beginDate = paramMap.get("beginDate");
        if (StringUtils.isBlank(beginDate)) {
            proxyDetail.setCreateDate(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
            paramMap.put("beginDate", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
        } else {
            paramMap.put("beginDate", beginDate);
            proxyDetail.setCreateDate(DateUtils.parseDate(beginDate));
        }
        String endDate = paramMap.get("endDate");
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
        //商户代付详情流水号
        proxyDetail.setId(paramMap.get("detailId"));
        //代付状态
        proxyDetail.setPayStatus(paramMap.get("payStatus"));
        //商户代付批次流水号
        proxyDetail.setMchtBatchId(paramMap.get("mchtBatchId"));

        //如果是从批次页面，查询明细信息
        String platBatchId = paramMap.get("platBatchId");
        if(StringUtils.isNotBlank(platBatchId)){
            proxyDetail.setPlatBatchId(platBatchId);
            //查出批次信息
            PlatProxyBatch platProxyBatch = proxyBatchService.queryByKey(platBatchId);
            if(null != platProxyBatch && loginName.equals(platProxyBatch.getMchtId())){
                //失败金额，保留4为小数
                BigDecimal divice = new BigDecimal(100);
                BigDecimal successAmount = platProxyBatch.getSuccessAmount();
                if(null != successAmount){
                    platProxyBatch.setSuccessAmount(successAmount.divide(successAmount, 4, BigDecimal.ROUND_HALF_UP));
                }
                BigDecimal failAmount = platProxyBatch.getFailAmount();
                if(null != failAmount){
                    platProxyBatch.setFailAmount(failAmount.divide(failAmount, 4, BigDecimal.ROUND_HALF_UP));
                }
                MchtInfo mchtInfo = merchantService.queryByKey(loginName);
                if(null != mchtInfo){
                    platProxyBatch.setExtend3(mchtInfo.getName());
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
        //页面带过来isSearch值为1
        if(StringUtils.isNotBlank(isSearch) && "1".equals(isSearch)){
            proxyCount = proxyDetailService.count(proxyDetail);
            proxyDetailInfoList =  proxyDetailService.list(proxyDetail);
        }
        BigDecimal divide = new BigDecimal(100);
        if(!CollectionUtils.isEmpty(proxyDetailInfoList)){
            for(PlatProxyDetail platProxyDetail : proxyDetailInfoList){
                platProxyDetail.setPayStatus(ProxyPayBatchStatusEnum.toEnum(platProxyDetail.getPayStatus()).getDesc());
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
            }
        }

        Page page = new Page(pageNo, pageInfo.getPageSize(), proxyCount, proxyDetailInfoList, true);
        model.addAttribute("page", page);
        model.addAttribute("paramMap", paramMap);
        return "modules/proxy/mchtProxyDetailList";
    }
}
