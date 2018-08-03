/**
 * Copyright &copy; 2012-2013  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sys.admin.modules.agentmcht.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.platform.bo.ProductFormInfo;
import com.sys.admin.modules.platform.service.ProductAdminService;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.admin.modules.trade.service.OrderAdminService;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.util.*;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatProduct;
import com.sys.core.service.*;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.sys.boss.api.service.order.OrderProxypay4ManageService;

/**
 * 处理代理商流水的Controller
 */
@SuppressWarnings("MVCPathVariableInspection")
@Controller
@RequestMapping(value = "${adminPath}/agentMchtOrder")
public class AgentMchtOrderController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(AgentMchtOrderController.class);

    @Autowired
    private MchtGwOrderService mchtGwOrderService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MchtProductService mchtProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ChanMchtPaytypeService chanMchtPaytypeService;
    @Autowired
    private ChannelService channelService;

    @Autowired
    private ProductAdminService productAdminService;

    @Autowired
    private ChanMchtAdminService chanMchtAdminService;

    @Autowired
    private OrderAdminService orderAdminService;

//    @RequiresPermissions("process:question:view")
    @RequestMapping(value = {"list", ""})
    public String list(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                       Model model, @RequestParam Map<String, String> paramMap) {

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
                String msg = "代理商才能查看该数据";
                logger.info(msg+"："+JSONObject.toJSONString(agentMchtInfo));
                model.addAttribute("message", msg);
                model.addAttribute("messageType", "error");
                model.addAttribute("paramMap", paramMap);
                return "modules/order/agentMchtOrderList";
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
            model.addAttribute("paramMap", paramMap);
            return "modules/order/agentMchtOrderList";
        }
        Map<String, String> agentSubMchtInfoMap = Collections3.extractToMap(agentSubMchtInfolist, "id", "name");
        model.addAttribute("agentSubMchtInfoMap",agentSubMchtInfoMap);
        String isSelectInfo = paramMap.get("isSelectInfo");
        //开始时间
        String beginDate = paramMap.get("beginDate");
        //结束时间
        String endDate = paramMap.get("endDate");
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            String msg = this.checkDate(beginDate, endDate);
            if (!"ok".equals(msg)) {
                logger.info(msg);
                model.addAttribute("message", msg);
                model.addAttribute("messageType", "error");
                model.addAttribute("paramMap", paramMap);
                return "modules/order/agentMchtOrderList";
            }
        }

        //创建查询实体
        MchtGatewayOrder order = new MchtGatewayOrder();
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
        order.setMchtCode(selectMchtId);
        order.setMchtId(selectMchtId);
        logger.info("开始时间：" + beginDate + ",结束时间：" + endDate);
        //初始化查询订单
        assemblySearch(paramMap, order);

        //获取当前第几页
        String pageNoString = paramMap.get("pageNo");
        int pageNo = 1;
        if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
            pageNo = Integer.parseInt(pageNoString);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNo(pageNo);
        order.setPageInfo(pageInfo);

        int orderCount = 0;
        if (StringUtils.isNotBlank(isSelectInfo)) {
            orderCount = orderAdminService.ordeCount(order);
            if (orderCount == 0) {
                model.addAttribute("paramMap", paramMap);
                return "modules/order/agentMchtOrderList";
            }
        }
        model.addAttribute("paramMap", paramMap);
        logger.info("查询订单信息：" + JSON.toJSONString(order));
        List<MchtGatewayOrder> orderList = null;
        if (StringUtils.isNotBlank(isSelectInfo)) {
            orderList = orderAdminService.list(order);
            if (CollectionUtils.isEmpty(orderList)) {
                model.addAttribute("paramMap", paramMap);
                return "modules/order/agentMchtOrderList";
            }

            if (!CollectionUtils.isEmpty(agentSubMchtInfoMap)) {
                for (MchtGatewayOrder gwOrder : orderList) {
                    String id = gwOrder.getMchtCode();
                    gwOrder.setMchtCode(agentSubMchtInfoMap.get(id));
                    if (gwOrder.getPayType() != null) {
                        String payType = getPayTypeByCode(gwOrder.getPayType());
                        gwOrder.setPayType(payType);
                    }
                }
            }

            //金额总数
            BigDecimal amount = new BigDecimal(orderAdminService.amount(order)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

            //支付成功
            order.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
            //支付成功总数
            long successCount = orderAdminService.ordeCount(order);
            //支付成功金额总数
            BigDecimal successAmount = new BigDecimal(orderAdminService.amount(order)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            model.addAttribute("successCount", successCount);
            model.addAttribute("amount", amount.toString());
            model.addAttribute("successAmount", successAmount.toString());
        }

        Page page = new Page(pageNo, pageInfo.getPageSize(), orderCount, orderList, true);
        model.addAttribute("page", page);


        model.addAttribute("orderCount", orderCount);


        return "modules/order/agentMchtOrderList";
    }

    private String getPayTypeByCode(String payType) {

        String strPayType = "";

        if(payType.startsWith("wx")){
            strPayType = "微信支付";
        }else if(payType.startsWith("ca")){
            strPayType = "收银台支付";
        }else if(payType.startsWith("al")){
            strPayType = "支付宝支付";
        }else if(payType.startsWith("sn")){
            strPayType = "苏宁支付";
        }else if(payType.startsWith("qq")){
            strPayType =  "QQ支付";
        }else if(payType.startsWith("jd")){
            strPayType =  "京东支付";
        }else if(payType.startsWith("yl")){
            strPayType =  "银联支付";
        }else if(payType.startsWith("qj")){
            strPayType = "快捷支付";
        }else if(payType.startsWith("df")){
            strPayType = "单笔代付";
        }else if(payType.startsWith("dk")){
            strPayType = "代扣";
        }else{
            strPayType = "其他";
        }
        return strPayType;
    }

    /**
     * 开始时间不能大于结束时间，
     * 不支持跨年查询
     * 不支持跨月查询
     *
     * @param beginDateStr
     * @param endDateStr
     * @return
     */
    private String checkDate(String beginDateStr, String endDateStr) {
        Date beginDate = DateUtils.parseDate(beginDateStr, "yyyy-MM-dd HH:mm:ss");
        String beginYearStr = DateUtils.formatDate(beginDate, "yyyy");
        String beginMonthStr = DateUtils.formatDate(beginDate, "MM");
        Date endDate = DateUtils.parseDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
        String endYearStr = DateUtils.formatDate(endDate, "yyyy");
        String endMonthStr = DateUtils.formatDate(endDate, "MM");
        if (!beginYearStr.equals(endYearStr)) {
            return "查询时间不能跨年";
        }
        if (!beginMonthStr.equals(endMonthStr)) {
            return "暂不支持跨月查询";
        }
        return "ok";
    }


//    @RequestMapping(value = {"select", ""})
//    public String select(HttpServletRequest request, HttpServletResponse response, HttpSession session,
//                         Model model, @RequestParam Map<String, String> paramMap) {
//        try {
//            User user = UserUtils.getUser();
//            String mchtNo = user.getNo();
//            //如果是内部管理员则显示所有商户的数据
//            if ("1001".equals(mchtNo)) {
//                mchtNo = "";
//            }
//            String pageNum = request.getParameter("pageNum");
//            String pageSize = request.getParameter("pageSize");
//            String id = request.getParameter("id");
//            String mchtSeq = request.getParameter("customerSeq");
//            String platSeq = request.getParameter("platformSeq");
//            String status = request.getParameter("status");
//            String beginDate = request.getParameter("beginDate");
//            String endDate = request.getParameter("endDate");
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("pageNum", pageNum);
//            params.put("pageSize", pageSize);
//            params.put("id", id);
//            params.put("mchtNo", mchtNo);
//            params.put("mchtSeq", mchtSeq);
//            params.put("platSeq", platSeq);
//            params.put("status", status);
//            params.put("beginDate", beginDate);
//            params.put("endDate", endDate);
//
//            List<JSONObject> tmpList = new ArrayList<JSONObject>();
//            List<Map> list = new ArrayList<Map>();
//            PageInfo page = null;
//
////            String url = GlobalConfig.getConfig("boss.url")+"platform/listGatewayOrder";
////            String resp = HttpUtil.post(url, params);
////            DataResponse dataResponse = JSONObject.parseObject(resp, DataResponse.class);
////            if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(dataResponse.getCode())){
////            	String data = JSONObject.toJSONString(dataResponse.getData());
////            	page = JSONObject.parseObject(data, PageInfo.class);
////            }
//
//            model.addAttribute("page", page);
//            model.addAttribute("paramMap", paramMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.getMessage(), e);
//        }
//
//        return "modules/order/mchtOrderList";
//    }

    /**
     * 订单详情
     *
     * @param request
     * @param response
     * @param session
     * @param model
     * @param paramMap
     * @return
     */
//    @RequiresPermissions("process:question:view")
//    @RequestMapping(value = {"detail", ""})
//    public String detail(HttpServletRequest request, HttpServletResponse response, HttpSession session,
//                         Model model, @RequestParam Map<String, String> paramMap) {
//        try {
//            //读取订单基本信息
//            MchtGatewayOrder searchBo = new MchtGatewayOrder();
//            assemblySearch(paramMap, searchBo);
//
//            //根据订单id和时间查询订单，时间用来2定位分表
//            List<MchtGatewayOrder> orderList = orderAdminService.list(searchBo);
//            if (orderList != null && orderList.size() > 0) {
//                MchtGatewayOrder order = orderList.get(0);
//                //根据商户id查询商户信息
//                MchtInfo mchtInfo = merchantService.queryByKey(order.getMchtId());
//                model.addAttribute("mchtInfo", mchtInfo);
//                ChanInfo chanInfo = channelService.queryByKey(order.getChanCode());
//                model.addAttribute("chanInfo", chanInfo);
//                ChanMchtFormInfo chanMchtPaytype = chanMchtAdminService.getChanMchtPaytypeById(order.getChanMchtPaytypeId());
//                model.addAttribute("chanMchtPaytype", chanMchtPaytype);
//                ProductFormInfo platProduct = productAdminService.getPlatProductById(order.getPlatProductId());
//                model.addAttribute("platProduct", platProduct);
//
//                model.addAttribute("orderInfo", orderList.get(0));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.getMessage(), e);
//        }
//
//        return "modules/order/orderDetail";
//    }

//    @RequestMapping(value = {"balance", ""})
//    public String balance(HttpServletRequest request, HttpServletResponse response, HttpSession session,
//                          Model model, @RequestParam Map<String, String> paramMap) {
//        try {
//            String mchtId = StringUtils.isNotBlank(request.getParameter("mchtId")) ? request.getParameter("mchtId") : "";
//            String queryDay = StringUtils.isNotBlank(request.getParameter("queryDay")) ? request.getParameter("queryDay") : "";
////			List<CpInfo> cpInfoList = new ArrayList<CpInfo>();
////			Map<String, BigDecimal> balanceMap = new HashMap<String, BigDecimal>();
////
////			if (StringUtils.isNotBlank(mchtId) || StringUtils.isNotBlank(queryDay)) {
////				cpInfoList = configSysService.listAllCpInfo();
////				balanceMap = orderProxypay4ManageService.listMerchantsBalance(mchtId, "1", queryDay);
////				for (Iterator<CpInfo> it = cpInfoList.iterator(); it.hasNext(); ) {
////					String cpId = it.next().getCpId().toString();
////					if (balanceMap.get(cpId) == null) {
////						it.remove();
////					} else {
////						balanceMap.put(cpId, balanceMap.get(cpId)
////								.divide(BigDecimal.valueOf(100)).setScale(4, BigDecimal.ROUND_DOWN));
////					}
////				}
////			}
////
////			model.addAttribute("cpInfoList", cpInfoList);
////			model.addAttribute("balanceMap", balanceMap);
//            model.addAttribute("cpInfoList", new ArrayList());
//            model.addAttribute("balanceMap", new HashMap());
//            model.addAttribute("mchtId", mchtId);
//            model.addAttribute("queryDay", queryDay);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.getMessage(), e);
//        }
//
//        return "modules/order/balanceList";
//    }

//    @RequestMapping(value = {"payQRCode", ""})
//    public String payQRCode(HttpServletRequest request, HttpServletResponse response, HttpSession session,
//                            Model model, @RequestParam Map<String, String> paramMap) {
//        try {
//            User user = UserUtils.getUser();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.getMessage(), e);
//        }
//
//        return "modules/order/payQRCode";
//    }

    /**
     * 查补单
     *
     * @param request
     * @param response
     * @param paramMap
     * @return
     */
//    @RequestMapping("querySupply")
//    public String querySupply(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes,
//                              Model model, @RequestParam Map<String, String> paramMap) {
//        String message = "查单失败";
//        try {
//            String gatewayUrl = ConfigUtil.getValue("gateway.url");
//            String queryUrl = gatewayUrl + "/gateway/queryOrder";
//            MchtGatewayOrder searchBo = new MchtGatewayOrder();
//            searchBo.setId(paramMap.get("orderId"));
//            String suffix = "20" + searchBo.getId().substring(1, 5);
//            searchBo.setSuffix(suffix);
//            List<MchtGatewayOrder> orderList = orderAdminService.list(searchBo);
//            MchtGatewayOrder order;
//            if (orderList != null && orderList.size() > 0) {
//                order = orderList.get(0);
//            } else {
//                redirectAttributes.addFlashAttribute("message", "查无此单！");
//                redirectAttributes.addFlashAttribute("messageType", "error");
//                redirectAttributes.addAllAttributes(paramMap);
//                return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//            }
//            MchtInfo mchtInfo = merchantService.queryByKey(order.getMchtCode());
//            if (mchtInfo == null) {
//                redirectAttributes.addFlashAttribute("message", "查无此商户！");
//                redirectAttributes.addFlashAttribute("messageType", "error");
//                redirectAttributes.addAllAttributes(paramMap);
//                return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//            }
//            String key = mchtInfo.getMchtKey();
//
//            JSONObject data = new JSONObject();
//            JSONObject head = new JSONObject();
//            JSONObject body = new JSONObject();
//            head.put("mchtId", mchtInfo.getId());
//            head.put("version", "20");
//            head.put("biz", order.getPayType());
//            data.put("head", head);
//            body.put("tradeId", order.getId());
//            body.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(order.getCreateTime()));
//            Map<String, String> params = JSONObject.parseObject(
//                    JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
//                    });
//            String log_moid = mchtInfo.getId() + "-->" + order.getId();
//            String sign = SignUtil.md5Sign(params, key, log_moid);
//            data.put("sign", sign);
//            data.put("body", body);
//            String respStr = HttpUtil.post(queryUrl, data.toJSONString());
//            logger.info("gateway查单返回：" + respStr);
//            JSONObject result = JSON.parseObject(respStr);
//            JSONObject resultHead = result.getJSONObject("head");
//            JSONObject resultBody = result.getJSONObject("body");
//            if (resultBody != null && ErrorCodeEnum.SUCCESS.getCode().equals(resultHead.getString("respCode"))) {
//                String resultStatus = resultBody.getString("status");
//                if (Result.STATUS_SUCCESS.equals(resultStatus)) {
//
//                    //补发通知
//                    String supplyUrl = gatewayUrl + "/renotify";
//                    Map<String, String> redata = new HashMap<>();
//                    redata.put("orderId", order.getId());
//                    redata.put("suffix", suffix);
//                    String reNoStr = HttpUtil.post(supplyUrl, redata);
//                    logger.info("gateway补发通知返回：" + reNoStr);
//
//                    if ("SUCCESS".equalsIgnoreCase(reNoStr)) {
//                        order.setSupplyStatus("0");
//                        message = "补发成功";
//                    } else {
//                        order.setSupplyStatus("1");
//                        message = "已补发，商户响应：" + reNoStr;
//                    }
//
//                    redirectAttributes.addFlashAttribute("message", "查单成功," + message);
//                    redirectAttributes.addFlashAttribute("messageType", "success");
//                    redirectAttributes.addAllAttributes(paramMap);
//                    return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//                } else if (Result.STATUS_FAIL.equals(resultStatus)) {
//                    redirectAttributes.addFlashAttribute("message", "查单成功, 支付状态为失败");
//                    redirectAttributes.addFlashAttribute("messageType", "success");
//                    redirectAttributes.addAllAttributes(paramMap);
//                    return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//                }
//                {
//                    redirectAttributes.addFlashAttribute("message", "查单成功, 支付状态未知");
//                    redirectAttributes.addFlashAttribute("messageType", "success");
//                    redirectAttributes.addAllAttributes(paramMap);
//                    return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("查补单失败，" + e.getMessage());
//            message = "查补单失败, 系统错误";
//            redirectAttributes.addFlashAttribute("message", message);
//            redirectAttributes.addFlashAttribute("messageType", "error");
//            redirectAttributes.addAllAttributes(paramMap);
//            return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//        } finally {
//
//        }
//        redirectAttributes.addFlashAttribute("message", message);
//        redirectAttributes.addFlashAttribute("messageType", "error");
//        redirectAttributes.addAllAttributes(paramMap);
//        return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//    }

//    @RequestMapping("supplyNotify")
//    public String supplyNotify(String orderId, String suffix, RedirectAttributes redirectAttributes, HttpServletResponse response) {
//        String message = "补发通知失败";
//        try {
//            String gatewayUrl = ConfigUtil.getValue("gateway.url");
//            String supplyUrl = gatewayUrl + "/gateway/renotify";
//            Map<String, String> data = new HashMap<>();
//            data.put("orderId", orderId);
//            data.put("suffix", suffix);
//            String respStr = HttpUtil.post(supplyUrl, data);
//            logger.info("gateway补发通知返回：" + respStr);
//            if ("SUCCESS".equalsIgnoreCase(respStr)) {
//                message = "补发成功";
//            } else {
//                message = "已补发，商户响应：" + respStr;
//            }
//            redirectAttributes.addFlashAttribute("message", message);
//            redirectAttributes.addFlashAttribute("messageType", "success");
//            return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("补发失败，" + e.getMessage());
//            message = "补发失败，" + e.getMessage();
//            redirectAttributes.addFlashAttribute("message", message);
//            redirectAttributes.addFlashAttribute("messageType", "error");
//
//        } finally {
//            logger.info(message);
//            return "redirect:" + GlobalConfig.getAdminPath() + "/mchtOrder/list";
//        }
//    }

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
        Map<String, String> agentSubMchtInfoMap = Collections3.extractToMap(agentSubMchtInfolist, "id", "name");

        //创建查询实体
        MchtGatewayOrder order = new MchtGatewayOrder();
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
        order.setMchtCode(selectMchtId);
        order.setMchtId(selectMchtId);
        assemblySearch(paramMap, order);
        redirectAttributes.addAllAttributes(paramMap);
        //计算条数 上限五万条
        int orderCount = orderAdminService.ordeCount(order);
        if (orderCount <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtOrder/list";
        }
        if (orderCount > 50000) {
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
            response.setCharacterEncoding("UTF-8");
            return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtOrder/list";
        }

        // 访问数据库，得到数据集
        List<MchtGatewayOrder> deitelVOList = orderAdminService.list(order);

        List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
        //支付产品列表
        List<PlatProduct> productList = productService.list(new PlatProduct());
        //上游通道列表
        List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
        //查询商户列表
        Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
        Map<String, String> mchtMap = Collections3.extractToMap(mchtList, "id", "name");
        Map<String, String> productMap = Collections3.extractToMap(productList, "id", "name");

        for (MchtGatewayOrder gwOrder : deitelVOList) {
            if (PayStatusEnum.toEnum(gwOrder.getStatus()) != null) {
                if (PayStatusEnum.SUBMIT_SUCCESS.getCode().equals(gwOrder.getStatus())) {
                    gwOrder.setStatus("提交支付");
                } else {
                    gwOrder.setStatus(PayStatusEnum.toEnum(gwOrder.getStatus()).getDesc());
                }
            }
            if (gwOrder.getPayType() != null) {

                String payType = getPayTypeByCode(gwOrder.getPayType());
                gwOrder.setPayType(payType);
            }
            gwOrder.setMchtId(mchtMap.get(gwOrder.getMchtCode()));
            gwOrder.setPlatProductId(productMap.get(gwOrder.getPlatProductId()));
            gwOrder.setChanId(channelMap.get(gwOrder.getChanId()));
        }

        String[] headers = {"商户名称", "支付方式", "商户订单号",
                "平台订单号", "官方订单号", "交易金额(元)", "订单状态", /*"补单状态",*/ "创建时间"};

        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(loginName +  (StringUtils.isNotBlank(subMchtId)? "_"+agentSubMchtInfoMap.get(subMchtId):"")+"_" + DateUtils.getNoSpSysDateString() + ".xls", "UTF-8"));
        OutputStream out = response.getOutputStream();

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("订单流水表");
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
        if (!Collections3.isEmpty(deitelVOList)) {
            int rowIndex = 1;//行号
            for (MchtGatewayOrder orderTemp : deitelVOList) {
                int cellIndex = 0;
                row = sheet.createRow(rowIndex);
                HSSFCell cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getMchtId());
                cellIndex++;

//				cell = row.createCell(cellIndex);
//				cell.setCellValue(orderTemp.getChanId());
//				cellIndex++;

			/*	cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getGoodsName());
				cellIndex++;*/

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getPayType());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getMchtOrderId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getPlatOrderId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getOfficialOrderId());
                cellIndex++;

                cell = row.createCell(cellIndex);
                if (orderTemp.getAmount() != null) {
                    BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), new BigDecimal(orderTemp.getAmount()));
                    cell.setCellValue(bigDecimal.doubleValue());
                }
                cellIndex++;

                cell = row.createCell(cellIndex);
                cell.setCellValue(orderTemp.getStatus());
                cellIndex++;

//                cell = row.createCell(cellIndex);
//                if ("0".equals(orderTemp.getSupplyStatus())) {
//                    cell.setCellValue("成功");
//                }

//                cellIndex++;

                cell = row.createCell(cellIndex);
                if (orderTemp.getCreateTime() != null) {
                    cell.setCellValue(DateUtils.formatDate(orderTemp.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
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
        return "redirect:" + GlobalConfig.getAdminPath() + "/agentMchtOrder/list";
    }

    /**
     * 组装搜索参数
     *
     * @param paramMap
     * @return
     */
    public void assemblySearch(Map<String, String> paramMap, MchtGatewayOrder order) {

        String id = paramMap.get("id");
        if (StringUtils.isNotBlank(id)) {
            order.setId(id);
        }
        //设置商户订单号
        if (StringUtils.isNotBlank(paramMap.get("customerSeq"))) {
            order.setMchtOrderId(paramMap.get("customerSeq").trim());
        }

        //设置平台订单号
        if (StringUtils.isNotBlank(paramMap.get("platformSeq"))) {
            order.setPlatOrderId(paramMap.get("platformSeq").trim());
        }

        //官方订单号
        if (StringUtils.isNotBlank(paramMap.get("officialSeq"))) {
            order.setOfficialOrderId(paramMap.get("officialSeq").trim());
        }

        //订单状态
        if (StringUtils.isNotBlank(paramMap.get("status"))) {
            order.setStatus(paramMap.get("status"));
        } else {
            //不显示下单失败的流水,使用!做个标记
            order.setStatus("!" + PayStatusEnum.SUBMIT_FAIL.getCode());
        }

        //补单状态
        if (StringUtils.isNotBlank(paramMap.get("supplyStatus"))) {
            order.setSupplyStatus(paramMap.get("supplyStatus"));
        }

        //支付方式--需要特殊处理下， 例如：支付宝扫码和扫码转h5，统一为支付宝扫码支付
        if (StringUtils.isNotBlank(paramMap.get("payType"))) {
            StringBuffer sb = new StringBuffer();
            if("wx".equals(paramMap.get("payType"))){
                sb.append(PayTypeEnum.WX_APP.getCode()).append("&").append(PayTypeEnum.WX_BARCODE_H5.getCode()).append("&")
                    .append(PayTypeEnum.WX_BARCODE.getCode()).append("&").append(PayTypeEnum.WX_BARCODE_PC.getCode()).append("&")
                    .append(PayTypeEnum.WX_GROUP.getCode()).append("&").append(PayTypeEnum.WX_WAP.getCode()).append("&")
                    .append(PayTypeEnum.WX_PUBLIC_NATIVE.getCode()).append("&").append(PayTypeEnum.WX_PUBLIC_NOT_NATIVE.getCode()).append("&")
                    .append(PayTypeEnum.WX_QRCODE.getCode()).append("&").append(PayTypeEnum.WX_BARCODE_PC.getCode());

            }else if("al".equals(paramMap.get("payType"))){
                sb.append(PayTypeEnum.ALIPAY_GROUP.getCode()).append("&").append(PayTypeEnum.ALIPAY_H5.getCode()).append("&")
                        .append(PayTypeEnum.ALIPAY_ONLINE_SCAN2WAP.getCode()).append("&").append(PayTypeEnum.ALIPAY_PC.getCode()).append("&")
                        .append(PayTypeEnum.ALIPAY_APP.getCode()).append("&").append(PayTypeEnum.ALIPAY_ONLINE_QRCODE.getCode()).append("&")
                        .append(PayTypeEnum.ALIPAY_BARCODE.getCode()).append("&").append(PayTypeEnum.ALIPAY_BARCODE_PC.getCode()).append("&")
                        .append(PayTypeEnum.ALIPAY_BARCODE_H5.getCode()).append("&").append(PayTypeEnum.ALIPAY_SERVICE_WINDOW.getCode());
            }else if("sn".equals(paramMap.get("payType"))){
                sb.append(PayTypeEnum.SUNING_GROUP.getCode()).append("&").append(PayTypeEnum.SUNING_H5.getCode()).append("&")
                        .append(PayTypeEnum.SUNING_SCAN2WAP.getCode()).append("&").append(PayTypeEnum.SUNING_PC.getCode()).append("&")
                        .append(PayTypeEnum.SUNING_QRCODE.getCode()).append("&").append(PayTypeEnum.SUNING_BARCODE.getCode()).append("&")
                        .append(PayTypeEnum.SUNING_BARCODE_PC.getCode()).append("&").append(PayTypeEnum.SUNING_BARCODE_H5.getCode());

            }else if("qq".equals(paramMap.get("payType"))){
                sb.append(PayTypeEnum.QQ_GROUP.getCode()).append("&").append(PayTypeEnum.QQ_WAP.getCode()).append("&")
                        .append(PayTypeEnum.QQ_SCAN2WAP.getCode()).append("&").append(PayTypeEnum.QQ_PC.getCode()).append("&")
                        .append(PayTypeEnum.QQ_QRCODE.getCode()).append("&").append(PayTypeEnum.QQ_BARCODE.getCode()).append("&")
                        .append(PayTypeEnum.QQ_BARCODE_PC.getCode()).append("&").append(PayTypeEnum.QQ_BARCODE_H5.getCode());

            }else if("jd".equals(paramMap.get("payType"))){
                sb.append(PayTypeEnum.JD_GROUP.getCode()).append("&").append(PayTypeEnum.JD_WAP.getCode()).append("&")
                        .append(PayTypeEnum.JD_SCAN2WAP.getCode()).append("&").append(PayTypeEnum.JD_PC.getCode()).append("&")
                        .append(PayTypeEnum.JD_SCAN.getCode()).append("&").append(PayTypeEnum.JD_BARCODE.getCode()).append("&")
                        .append(PayTypeEnum.JD_BARCODE_PC.getCode()).append("&").append(PayTypeEnum.JD_BARCODE_H5.getCode());

            }else if("yl".equals(paramMap.get("payType"))){
                sb.append(PayTypeEnum.UNIONPAY_GROUP.getCode()).append("&").append(PayTypeEnum.UNIONPAY_H5.getCode()).append("&")
                        .append(PayTypeEnum.UNIONPAY_QRCODE.getCode()).append("&").append(PayTypeEnum.UNIONPAY_SCAN2WAP.getCode()).append("&")
                        .append(PayTypeEnum.UNIONPAY_BARCODE.getCode()).append("&").append(PayTypeEnum.UNIONPAY_BARCODE_PC.getCode()).append("&")
                        .append(PayTypeEnum.UNIONPAY_BARCODE_h5.getCode());

            }else if("qj".equals(paramMap.get("payType"))){
                sb.append(PayTypeEnum.QUICK_GROUP.getCode()).append("&").append(PayTypeEnum.QUICK_PAY.getCode()).append("&")
                        .append(PayTypeEnum.QUICK_COMB_DK.getCode());

            }else if("df101".equals(paramMap.get("payType"))){
                sb.append(PayTypeEnum.SINGLE_DF.getCode());
            }

            order.setPayType(sb.toString());
        }

        //初始化页面开始时间
        String beginDate = paramMap.get("beginDate");
        if (StringUtils.isBlank(beginDate)) {
            order.setCreateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
            paramMap.put("beginDate", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
        } else {
            paramMap.put("beginDate", beginDate);
            order.setCreateTime(DateUtils.parseDate(beginDate));
        }
        String endDate = paramMap.get("endDate");
        //初始化页面结束时间
        if (StringUtils.isBlank(endDate)) {
            order.setUpdateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 23:59:59"));
            paramMap.put("endDate", DateUtils.getDate("yyyy-MM-dd") + " 23:59:59");
        } else {
            paramMap.put("endDate", endDate);
            order.setUpdateTime(DateUtils.parseDate(endDate));
        }

        order.setSuffix(DateUtils.formatDate(order.getCreateTime(), "yyyyMM"));

    }
}
