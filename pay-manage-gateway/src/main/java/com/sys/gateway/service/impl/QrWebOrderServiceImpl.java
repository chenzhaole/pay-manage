//package com.sys.gateway.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sys.common.enums.PayStatusEnum;
//import com.sys.common.util.*;
//import com.sys.core.dao.common.PageInfo;
//import com.sys.core.dao.dmo.MchtGatewayOrder;
//import com.sys.gateway.common.PlatBeanUtil;
//import com.sys.gateway.service.GwApiQueryService;
//import com.sys.gateway.service.QrWebOrderService;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.poi.hssf.usermodel.HSSFHeader;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by chenzhaole on 2019/8/17.
// */
//@Service
//public class QrWebOrderServiceImpl implements QrWebOrderService {
//
//    protected final Logger logger = LoggerFactory.getLogger(QrWebOrderServiceImpl.class);
//
//    @Autowired
//    GwApiQueryService gwApiQueryService;
//
//    @Override
//    public int amount(String mchtId, String pageNo, String status, String beginTime, String endTime) {
//
//        try {
//            Map<String, String> paramMap = new HashMap<>();
//            paramMap.put("mchtId", mchtId);
//            paramMap.put("status", status);
//
//            MchtGatewayOrder order = PlatBeanUtil.buildMchtGatewayOrder4Search(paramMap);
//            logger.info("调用gwApiQueryService  请求参数order: " + JSON.toJSONString(order));
//            int amount = gwApiQueryService.amount(order);
//            logger.info("调用gwApiQueryService  返回值 amount: " + amount);
//            return amount;
//        } catch (Exception e) {
//            logger.error("查询 Order 模块出错：", e);
//            return -1;
//        }
//    }
//
//    /**
//     * 查询接口-列表
//     */
//    @Override
//    public List list(String mchtId, String pageNo, String status, String mchtOrderId, String platOrderId, String beginTime, String endTime, String yyyyMM) {
//        logger.info("ServiceImpl查询支付订单收到客户端请求参数：mchtId=" + mchtId + ", pageNo=" + pageNo);
//
//        try {
//
//            if (org.apache.commons.lang.StringUtils.isBlank(mchtId)) {
//                mchtId = "";
//            }
//            if (org.apache.commons.lang.StringUtils.isBlank(beginTime)) {
//                beginTime = DateUtils2.getNowTimeStr("yyyy-MM") + "-01 00:00:00";
//                logger.info("ServiceImpl查询支付订单收到客户端请求参数：beginTime=空,赋予默认值:" + beginTime);
//            }
//            if (org.apache.commons.lang.StringUtils.isBlank(pageNo)) {
//                pageNo = "1";
//                logger.info("ServiceImpl查询支付订单收到客户端请求参数：pageNo=空,赋予默认值:" + pageNo);
//            }
//            String pageSize = "10";
//            logger.info("ServiceImpl查询支付订单收到客户端请求参数：pageSize=空,赋予默认值:" + pageSize);
//
//            Map<String, String> paramMap = new HashMap<>();
//            paramMap.put("mchtId", mchtId);
//            paramMap.put("status", status);
//            paramMap.put("pageNo", pageNo);
//            paramMap.put("pageSize", pageSize);
//            paramMap.put("beginDate", beginTime);
//            paramMap.put("mchtOrderId", mchtOrderId);
//            paramMap.put("platOrderId", platOrderId);
//            paramMap.put("yyyyMM", yyyyMM);
//
//
//            MchtGatewayOrder mchtGatewayOrder = PlatBeanUtil.buildMchtGatewayOrder4Search(paramMap);
//
//            //queryPay查询支付订单接口
//            logger.info("ServiceImpl调用查询支付订单接口，传入的mchtGatewayOrder查询条件：" + JSONObject.toJSONString(mchtGatewayOrder));
//            List list = gwApiQueryService.list(mchtGatewayOrder);
//
//            if (CollectionUtils.isEmpty(list)) {
//                return null;
//            }
//
//            List rtnList = new ArrayList<>();
//            for (int i = 0; i < list.size(); i++) {
//                MchtGatewayOrder order = (MchtGatewayOrder) list.get(i);
//                Map map = BeanUtils.bean2Map(order);
//
//                String orderId = order.getPlatOrderId();
//                map.put("orderId", orderId);
//                String orderTime = DateUtils.formatDate(order.getUpdateTime(), "yyyyMMdd HH:mm:ss");
//                map.put("orderTime", orderTime);
//                if (StringUtils.isBlank(yyyyMM)) {
//                    yyyyMM = DateUtils.formatDate(order.getCreateTime(), "yyyyMM");
//                    map.put("yyyyMM", yyyyMM);
//                }
//                long fenLong = order.getAmount();
//                String fenStr = String.valueOf(fenLong);
//                map.put("amount", NumberUtils.changeF2Y(fenStr));
//                map.put("verifyCode", orderId.substring(orderId.length() - 5, orderId.length() - 1));
//                String payType = order.getPayType();
//                if (payType.startsWith("wx")) {
//                    map.put("payType", "wx");
//                }
//                if (payType.startsWith("al")) {
//                    map.put("payType", "al");
//                }
//                rtnList.add(map);
//            }
//            logger.info("ServiceImpl调用查询支付订单接口，返回list数据 ：" + JSONObject.toJSONString(rtnList));
//            return rtnList;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("ServiceImpl查询订单列表异常：", e);
//        }
//
//        return null;
//    }
//
//    @Override
//    public Map queryByPlatOrderId(String platOrderId) {
//        Map map = new HashMap<>();
//        try {
//            List list = list("", "", "", "", platOrderId, "", "", "");
//            if (CollectionUtils.isEmpty(list)) {
//                return null;
//            }
//            map = (Map) list.get(0);
//            map.put("amount",(String) map.get("amount"));
//            map.put("verifyCode", platOrderId.substring(platOrderId.length() - 5, platOrderId.length() - 1));
//            if (PayStatusEnum.PAY_SUCCESS.getCode().equals(map.get("status"))) {
//                map.put("status", "成功");
//            } else {
//                map.put("status", "未成功,确认中");
//            }
////            map.put("updateTime",DateUtils2.);
////            map.put("",xxxx);
////            map.put("",xxxx);
////            map.put("",xxxx);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return map;
//    }
//
//
//}
