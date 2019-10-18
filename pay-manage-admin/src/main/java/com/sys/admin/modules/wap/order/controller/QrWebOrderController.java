package com.sys.admin.modules.wap.order.controller;

import com.alibaba.fastjson.JSON;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.admin.modules.wap.order.service.QrWebOrderService;
import com.sys.common.util.DateUtils2;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用支付接口
 */
@Controller
@RequestMapping(value = "${adminPath}/wap/order")
public class QrWebOrderController {

    protected final Logger logger = LoggerFactory.getLogger(QrWebOrderController.class);

    @Autowired
    QrWebOrderService qrWebOrderService;


    @RequestMapping("/preList")
    public String qrOrderPreList(HttpServletRequest request, HttpServletResponse response, Model model) {

        try {

            User user = UserUtils.getUser();
            String mchtId = user.getLoginName();

            String mchtOrderId = request.getParameter("mchtOrderId");
            String orderTime = DateUtils2.getNowTimeStr(DateUtils2.STR_DATEFORMATE_yyyyMMddHHmmss);
            logger.info("聚合二维码进入订单列表页面: mchtId="+mchtId+",mchtOrderId=" + mchtOrderId + ",orderTime=" + orderTime);

            String pageNo = "1";
            String status = "2";
            List list = qrWebOrderService.list(mchtId, pageNo, status, "", "", "", "", "");
            logger.info("聚合二维码查询订单列表数据,返回前端数据: mchtOrderId=" + mchtOrderId + " pageNo=" + pageNo);

            model.addAttribute("mchtId", mchtId);
            model.addAttribute("pageNo", "2");
            model.addAttribute("orderList", list);


        } catch (Exception e) {
            logger.error("聚合二维码查询订单异常!", e);
            return "modules/qrcode/qrCodeWebFail";
        }
        return "modules/wap/order/qrWebOrderList";
    }


    @RequestMapping("/list")
    @ResponseBody
    public String qrOrderList(HttpServletRequest request, HttpServletResponse response, Model model) {

        try {
            User user = UserUtils.getUser();
            String mchtId = user.getLoginName();

            String pageNo = request.getParameter("pageNo");
            if (StringUtils.isBlank(pageNo)) {
                pageNo = "1";
            }
            String yyyyMM = request.getParameter("orderTime");

            String mchtOrderId = request.getParameter("mchtOrderId");
            String orderTime = DateUtils2.getNowTimeStr(DateUtils2.STR_DATEFORMATE_yyyyMMddHHmmss);
            logger.info("聚合二维码查询订单列表数据: mchtOrderId=" + mchtOrderId + " pageNo=" + pageNo);

            model.addAttribute("mchtId", mchtId);
            model.addAttribute("pageNo", pageNo);

            Map param = new HashMap();
            param.put("pageNo", pageNo);
            param.put("mchtId", mchtId);

            List list = qrWebOrderService.list(mchtId, pageNo, "2", "", "", "", "","");

            logger.info("聚合二维码查询订单列表数据,返回前端数据: mchtOrderId=" + mchtOrderId + " pageNo=" + pageNo);
            Map rtnMap = new HashMap<>();
            rtnMap.put("orderList", list);
            rtnMap.put("pageNo", pageNo);
            rtnMap.put("mchtId", mchtId);

            return JSON.toJSONString(rtnMap);

        } catch (Exception e) {
            logger.error("聚合二维码查询订单异常!", e);
        }
        return null;
    }


    @RequestMapping("/detail")
    public String qrOrderDetail(HttpServletRequest request, HttpServletResponse response, Model model) {

        try {
            String platOderId = request.getParameter("platOrderId");
            String yyyyMM = request.getParameter("yyyyMM");
            logger.info("聚合二维码查询订单详情数据,platOrderId=" + platOderId + ",yyyyMM=" + yyyyMM);
            Map map = qrWebOrderService.queryByPlatOrderId(platOderId);
            logger.info("聚合二维码查询订单详情数据,订单数据map=" + JSON.toJSONString(map));
            model.addAttribute("data", map);

        } catch (Exception e) {
            logger.error("聚合二维码查询订单异常!", e);
            return "modules/qrcode/qrCodeWebFail";
        }
        return "modules/wap/order/qrWebOrderDetail";
    }


}
