package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheOrder;
import com.sys.boss.api.entry.cache.CacheTrade;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.boss.api.entry.trade.request.cashier.TradeCashierRequest;
import com.sys.boss.api.entry.trade.request.quickpay.TXQuickPrePayRequest;
import com.sys.boss.api.entry.trade.response.TradeDFNotifyResponse;
import com.sys.boss.api.entry.trade.response.TradeNotifyResponse;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.ProxyPayDetailStatusEnum;
import com.sys.common.util.BeanUtils;
import com.sys.common.util.DateUtils;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatProxyDetail;
import com.sys.core.service.MchtGwOrderService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.ProxyDetailService;
import com.sys.gateway.service.GwDFSendNotifyService;
import com.sys.gateway.service.GwRecNotifyService;
import com.sys.gateway.service.GwSendNotifyService;
import com.sys.trans.api.entry.Config;
import com.sys.trans.api.entry.Order;
import com.sys.trans.api.entry.QuickPay;
import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description:代付业务处理实现类
 * @author: duanjintang
 * @time: 2018年11月1日
 */
@Service
public class GwDFSendNotifyServiceImpl implements GwDFSendNotifyService {

    protected final Logger logger = LoggerFactory.getLogger(GwDFSendNotifyServiceImpl.class);

    @Autowired
    private ProxyDetailService proxyDetailService;

    @Autowired
    private MerchantService merchantService;

    private final String BIZ = "给下游商户代付异步通知流水信息GwDFSendNotifyServiceImpl->";

    @Override
    public CommonResult sendNotify(PlatProxyDetail detail, String batchStatus, String notifyUrl,String log_tag) {
        CommonResult commonResult = new CommonResult();
        commonResult.setRespCode(ErrorCodeEnum.FAILURE.getCode());
        //商户信息
        MchtInfo mchtInfo = merchantService.queryByKey(detail.getMchtId());
        TradeNotify tradeNotify = this.buildTradeNotifyInfo(detail,batchStatus,notifyUrl,mchtInfo,log_tag);
        if(null == tradeNotify){
            commonResult.setRespCode(ErrorCodeEnum.E9001.getCode());
            commonResult.setRespMsg("操作失败");
            logger.info(BIZ+"异步通知商户信息TradeNotify为Null，请求参数源PlatProxyDetail="+JSONObject.toJSONString(detail)+",batchStatus="+batchStatus+",notifyUrl="+notifyUrl);
            return commonResult;
        }

        String url = tradeNotify.getUrl();
        String contentType = "application/json";
        String content = JSON.toJSONString(tradeNotify.getResponse());
        try {
            //HTTP异步通知商户交易结果
            logger.info(log_tag+",异步通知商户信息为："+content);
            String mchtRes = HttpUtil.postConnManager(url, content, contentType, "UTF-8", "UTF-8");
            logger.info(log_tag+",异步通知商户信息为："+content+",商户返回的结果为："+mchtRes);

            //补发通知成功后，修改补发状态
            PlatProxyDetail proxyDetail = new PlatProxyDetail();
            proxyDetail.setId(detail.getId());
            if ("SUCCESS".equals(mchtRes)) {
                commonResult.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                //补单是否成功:0：成功，1：失败
                proxyDetail.setSupplyStatus("0");
            }else{
                //补单是否成功:0：成功，1：失败
                proxyDetail.setSupplyStatus("1");
            }
            PlatProxyDetail selectProxyDetail = new PlatProxyDetail();
            selectProxyDetail.setId(detail.getId());
            proxyDetailService.updateBySelective(proxyDetail,selectProxyDetail);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ+"异步通知商户系统异常,e.msg:" + e.getMessage());
            commonResult.setRespMsg("操作失败");
        }
        logger.info(BIZ+"异步通知商户信息后返回给上层的commonResult："+content+",请求参数源PlatProxyDetail为："+JSONObject.toJSONString(detail));
        return commonResult;
    }

    /**
     * 封装TradeNotify信息
     * @return
     */
    private TradeNotify buildTradeNotifyInfo(PlatProxyDetail detail, String batchStatus, String notifyUrl,MchtInfo mchtInfo,String log_tag) {
        TradeNotify tradeNotify = new TradeNotify();
        String respCode = ErrorCodeEnum.SUCCESS.getCode();
        String respMsg = ErrorCodeEnum.SUCCESS.getDesc();
        try {
            //商户异步通知url
            tradeNotify.setUrl(notifyUrl);
            TradeDFNotifyResponse tradeDFNotifyResponse = new TradeDFNotifyResponse();
            TradeDFNotifyResponse.TradeDFNotifyResponseHead head = new TradeDFNotifyResponse.TradeDFNotifyResponseHead();
            head.setRespCode(respCode);
            head.setRespMsg(respMsg);
            TradeDFNotifyResponse.TradeDFNotifyBody body = new TradeDFNotifyResponse.TradeDFNotifyBody();
            body.setMchtId(detail.getMchtId());             //商户ID
            body.setBatchOrderNo(detail.getMchtBatchId());  //商户代付批次号
            body.setTradeId(detail.getPlatBatchId());       //平台批次号
            body.setDetailId(detail.getId());               //代付明细id
            body.setSeq(detail.getMchtSeq());               //序号
            body.setBatchStatus(batchStatus);               //批次状态  DONE 代付结束 DOING 代付处理中
            //代付结果
            String detailStatus = "UNKNOWN";//未知
            if(ProxyPayDetailStatusEnum.DF_SUCCESS.getCode().equals(detail.getPayStatus())){
                detailStatus = "SUCCESS";   //代付成功
            }else if(ProxyPayDetailStatusEnum.DF_FAIL.getCode().equals(detail.getPayStatus())){
                detailStatus = "FAIL";      //代付失败
            }else if(ProxyPayDetailStatusEnum.DF_DOING.getCode().equals(detail.getPayStatus())){
                detailStatus = "DOING";     //代付处理中
            }
            body.setStatus(detailStatus);
            body.setAmount(detail.getAmount().toString());  //金额

            tradeDFNotifyResponse.setHead(head);
            tradeDFNotifyResponse.setBody(body);

            TreeMap<String, String> treeMap = BeanUtils.bean2TreeMap(body);
            String sign = SignUtil.md5Sign(new HashMap<String, String>(treeMap), mchtInfo.getMchtKey(), log_tag);
            tradeDFNotifyResponse.setSign(sign);

            tradeNotify.setResponse(tradeDFNotifyResponse);
            logger.info(log_tag+",tradeNotify："+JSONObject.toJSONString(tradeNotify));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ+"组合商户异步通知对象异常 msg：" + e.getMessage());
        }
        return tradeNotify;
    }
}