package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.PostUtil;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.dao.dmo.PlatProxyBatch;
import com.sys.core.dao.dmo.PlatProxyDetail;
import com.sys.core.service.MchtGwOrderService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.ProxyBatchService;
import com.sys.core.service.ProxyDetailService;
import com.sys.gateway.service.GwDFSendNotifyService;
import com.sys.gateway.service.GwRecNotifyService;
import com.sys.gateway.service.GwSendNotifyService;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TransException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * 向商户发送异步通知
 * 后台页面补发通知触发
 *
 * Created by chenzhaole on 2018/3/15.
 */
@Controller
@RequestMapping(value = "")
public class GwSendNotifyController {
    protected final Logger logger = LoggerFactory.getLogger(GwSendNotifyController.class);


    @Autowired
    private GwSendNotifyService gwSendNotifyService;

    @Autowired
    private GwDFSendNotifyService gwDFSendNotifyService;

    @Autowired
    private ProxyDetailService proxyDetailService;

    @Autowired
    private ProxyBatchService proxyBatchService;


    private final String BIZ = "接收异步通知-";

    @RequestMapping(value = "/gateway/renotify")
    @ResponseBody
    public String renotify(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {

        String platOrderId = request.getParameter("orderId");
        String suffix = request.getParameter("suffix");

        CommonResult commonResult = gwSendNotifyService.sendNotifyAgain(platOrderId, suffix);
        String resultStr = commonResult.getRespCode();
        return resultStr;
    }

    @RequestMapping(value = "/gateway/dfrenotify")
    @ResponseBody
    public String dfrenotify(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        String detailId = request.getParameter("detailId");
        PlatProxyDetail proxyDetail =  proxyDetailService.queryByKey(detailId);
        PlatProxyBatch  proxyBatch  = proxyBatchService.queryByKey(proxyDetail.getPlatBatchId());
        String log_tag = "代付明细补发通知,商户代付批次号："+proxyDetail.getMchtBatchId()+"，平台批次号："+proxyDetail.getPlatBatchId()+",代付明细id:"+proxyDetail.getId()+",batchStatus="+proxyBatch.getPayStatus()+",notifyUrl="+proxyBatch.getNotifyUrl();
        CommonResult commonResult = gwDFSendNotifyService.sendNotify(proxyDetail,proxyBatch.getPayStatus(),proxyBatch.getNotifyUrl(),log_tag);
        String resultStr = commonResult.getRespCode();
        return resultStr;
    }

}
