//package com.sys.gateway.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;
//import com.alipay.api.request.AlipaySystemOauthTokenRequest;
//import com.alipay.api.response.AlipaySystemOauthTokenResponse;
//import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
//import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderQueryResponse;
//import com.sys.common.enums.PayTypeEnum;
//import com.sys.common.util.DateUtils2;
//import com.sys.common.util.HttpUtil;
//import com.sys.common.util.IdUtil;
//import com.sys.common.util.NumberUtils;
//import com.sys.core.dao.dmo.MchtGatewayOrder;
//import com.sys.gateway.common.ClientUtil;
//import com.sys.gateway.common.PlatRequestUtil;
//import com.sys.gateway.service.GwApiQueryService;
//import com.sys.gateway.service.QrCodeWebService;
//import com.sys.gateway.service.QrWebOrderService;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 通用支付接口
// */
//@Controller
//@RequestMapping(value = "")
//public class QrWebOrderController {
//
//    protected final Logger logger = LoggerFactory.getLogger(QrWebOrderController.class);
//
//    @Autowired
//    QrWebOrderService qrWebOrderService;
//
//
//    @RequestMapping("/gwqr/order/preList")
//    public String qrOrderPreList(HttpServletRequest request, HttpServletResponse response, Model model) {
//
//        try {
//
//            String mchtOrderId = request.getParameter("mchtOrderId");
//            String orderTime = DateUtils2.getNowTimeStr(DateUtils2.STR_DATEFORMATE_yyyyMMddHHmmss);
//            logger.info("聚合二维码进入订单列表页面: mchtOrderId=" + mchtOrderId + ",orderTime=" + orderTime);
//
//            String mchtId = "3310000000666666";//TODO:通过sessionId查询缓存中的mchtInfo对象
//            String key = "12345678901234567890123456789012";
//
//            String pageNo = "1";
//            String status = "2";
//            List list = qrWebOrderService.list(mchtId, pageNo, status, "", "", "", "", "");
//            logger.info("聚合二维码查询订单列表数据,返回前端数据: mchtOrderId=" + mchtOrderId + " pageNo=" + pageNo);
//
//            model.addAttribute("mchtId", mchtId);
//            model.addAttribute("pageNo", "2");
//            model.addAttribute("orderList", list);
//
//
//        } catch (Exception e) {
//            logger.error("聚合二维码查询订单异常!", e);
//            return "modules/qrcode/qrCodeWebFail";
//        }
//        return "modules/qrweb/qrWebOrderList";
//    }
//
//
//    @RequestMapping("/gwqr/order/list")
//    @ResponseBody
//    public String qrOrderList(HttpServletRequest request, HttpServletResponse response, Model model) {
//
//        try {
//            String pageNo = request.getParameter("pageNo");
//            if (StringUtils.isBlank(pageNo)) {
//                pageNo = "1";
//            }
//            String yyyyMM = request.getParameter("orderTime");
//
//            String mchtOrderId = request.getParameter("mchtOrderId");
//            String orderTime = DateUtils2.getNowTimeStr(DateUtils2.STR_DATEFORMATE_yyyyMMddHHmmss);
//            logger.info("聚合二维码查询订单列表数据: mchtOrderId=" + mchtOrderId + " pageNo=" + pageNo);
//
//            String mchtId = "3310000000666666";//TODO:通过sessionId查询缓存中的mchtInfo对象
//            String key = "12345678901234567890123456789012";
//
//
//            model.addAttribute("mchtId", mchtId);
//            model.addAttribute("pageNo", pageNo);
//
//
//            Map param = new HashMap();
//            param.put("pageNo", pageNo);
//            param.put("mchtId", mchtId);
//
//            List list = qrWebOrderService.list(mchtId, pageNo, "2", "", "", "", "","");
//
//            logger.info("聚合二维码查询订单列表数据,返回前端数据: mchtOrderId=" + mchtOrderId + " pageNo=" + pageNo);
//            Map rtnMap = new HashMap<>();
//            rtnMap.put("orderList", list);
//            rtnMap.put("pageNo", pageNo);
//            rtnMap.put("mchtId", mchtId);
//
//            return JSON.toJSONString(rtnMap);
//
//        } catch (Exception e) {
//            logger.error("聚合二维码查询订单异常!", e);
//        }
//        return null;
//    }
//
//
//    @RequestMapping("/gwqr/order/detail")
//    public String qrOrderDetail(HttpServletRequest request, HttpServletResponse response, Model model) {
//
//        try {
//            String platOderId = request.getParameter("platOrderId");
//            String yyyyMM = request.getParameter("yyyyMM");
//            logger.info("聚合二维码查询订单详情数据,platOrderId=" + platOderId + ",yyyyMM=" + yyyyMM);
//            Map map = qrWebOrderService.queryByPlatOrderId(platOderId);
//            logger.info("聚合二维码查询订单详情数据,订单数据map=" + JSON.toJSONString(map));
//            model.addAttribute("data", map);
//
//        } catch (Exception e) {
//            logger.error("聚合二维码查询订单异常!", e);
//            return "modules/qrcode/qrCodeWebFail";
//        }
//        return "modules/qrweb/qrWebOrderDetail";
//    }
//
//
//}
