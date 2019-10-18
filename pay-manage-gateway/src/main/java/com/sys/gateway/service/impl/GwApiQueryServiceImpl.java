package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.IntegerCodec;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.apipay.ApiPayRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.ApiQueryRequestBody;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiPayRequest;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderQueryResponse;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.boss.api.service.trade.handler.ITradeApiQueryHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.gateway.common.ConfigUtil;
import com.sys.gateway.service.GwApiPayService;
import com.sys.gateway.service.GwApiQueryService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API查询支付订单实现类
 */
@Service
public class GwApiQueryServiceImpl implements GwApiQueryService {

    protected final Logger logger = LoggerFactory.getLogger(GwApiQueryService.class);

    private static final String ORDER_URL = ConfigUtil.getValue("order.url");

    @Autowired
    private ITradeApiQueryHandler tradeApiQueryHandler;


    /**
     * API支付查单检验参数
     **/
    @Override
    public CommonResponse checkParam(String paramStr) {
        CommonResponse checkResp = new CommonResponse();
        try {
            if (paramStr.endsWith("=")) {
                paramStr = paramStr.substring(0, paramStr.length() - 1);
            }
            //解析请求参数
            TradeApiQueryRequest tradeRequest = JSON.parseObject(paramStr, TradeApiQueryRequest.class);
            if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
                logger.info("[head],[body],[sign]请求参数值不能为空，即TradeCommRequest=：" + JSONObject.toJSONString(tradeRequest));
                return checkResp;
            }

            TradeReqHead head = tradeRequest.getHead();
            if (head.getMchtId() == null || head.getVersion() == null || head.getBiz() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
                logger.info("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=：" + JSONObject.toJSONString(head));
                return checkResp;
            }

            ApiQueryRequestBody body = tradeRequest.getBody();
            if (StringUtils.isBlank(body.getOrderId())
                    || StringUtils.isBlank(body.getOrderTime())) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[orderId],[orderTime]请求参数值不能为空");
                logger.info("[orderId],[orderTime]请求参数值不能为空，即CommRequestBody=：" + JSONObject.toJSONString(body));
                return checkResp;
            }


            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(tradeRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("API支付查单接口校验参数异常：" + e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }


    @Override
    public int amount(MchtGatewayOrder order) {

        String url = ORDER_URL + "orderAmount";
        Map<String, String> params = new HashMap<>();
        params.put("order", JSON.toJSONString(order));

        String result;
        int resultInt = 0;

        try {
            logger.info("调用order模块 url:" + url + "  参数params:" + JSON.toJSONString(params));
            result = HttpUtil.postConnManager(url, params);
            if (StringUtils.isNotBlank(result)) {
                resultInt = Integer.parseInt(result);
            }
        } catch (Exception e) {
            logger.error("查询 Order 模块出错：", e);
            return resultInt;
        }
        return resultInt;
    }


    /**
     * API支付查单接口(单笔)
     */
    @Override
    public ApiPayOrderQueryResponse query(TradeBaseRequest tradeRequest, String ip) {

        ApiPayOrderQueryResponse.ApiPayOrderQueryResponseHead head = new ApiPayOrderQueryResponse.ApiPayOrderQueryResponseHead();
        ApiPayOrderQueryResponse.ApiPayOrderQueryResponseBody body = new ApiPayOrderQueryResponse.ApiPayOrderQueryResponseBody();
        String sign = "";
        String moid = tradeRequest.getHead().getMchtId();
        try {
            logger.info("调用boss-trade查询支付订单，参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
            CommonResult commonResult = tradeApiQueryHandler.process(tradeRequest, ip);
            logger.info("调用boss-trade查询支付订单，返回值commonResult：" + JSON.toJSONString(commonResult));


            if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                Map<String, String> retData = (Map<String, String>) commonResult.getData();
                String data = retData.get("data");
                String signKey = retData.get("signKey");

                if(StringUtils.isBlank(data)){
                    head.setRespCode(ErrorCodeEnum.E6111.getCode());
                    head.setRespMsg("没有找到该订单");
                    return new ApiPayOrderQueryResponse(head, body, sign);
                }

                // 签名
                Map<String, String> params = JSON.parseObject(data, Map.class);
                moid = params.get("mchtId") + "-" + params.get("orderId");
                sign = SignUtil.md5Sign(params, signKey, moid);
                head.setRespCode(commonResult.getRespCode());
                head.setRespMsg(commonResult.getRespMsg());
                body = JSON.parseObject(data, ApiPayOrderQueryResponse.ApiPayOrderQueryResponseBody.class);
            } else {
                String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode() : commonResult.getRespCode();
                String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc() : commonResult.getRespMsg();
                head.setRespCode(respCode);
                head.setRespMsg(respMsg);
            }
        } catch (Exception e) {
            head.setRespCode(ErrorCodeEnum.E6111.getCode());
            head.setRespMsg(ErrorCodeEnum.E6111.getDesc());
            e.printStackTrace();
            logger.error(moid + "查询支付API支付订单异常 e=" + e.getMessage());
        }
        ApiPayOrderQueryResponse apiOrderQueryResponse = new ApiPayOrderQueryResponse(head, body, sign);
        logger.info(moid + "ApiPayOrderQueryResponse=" + JSON.toJSONString(apiOrderQueryResponse));
        return apiOrderQueryResponse;
    }

    /**
     * API支付查单接口(列表)
     */
    @Override
    public List<MchtGatewayOrder> list(MchtGatewayOrder mchtGatewayOrder) {
        try {
            String url = ORDER_URL + "orderQuery";
            Map<String, String> params = new HashMap<>();
            params.put("order", JSON.toJSONString(mchtGatewayOrder));

            logger.info("调用order模块 url:" + url + "  参数params:" + JSON.toJSONString(params));
            String result = HttpUtil.postConnManager(url, params);
//            logger.info("调用order模块查询订单list,返回result: " + result);
            List<MchtGatewayOrder> mchtGatewayOrders = JSON.parseArray(result, MchtGatewayOrder.class);

            if (!CollectionUtils.isEmpty(mchtGatewayOrders)) {
                return mchtGatewayOrders;
            } else {
                logger.info("调用order模块查询订单list,返回result=空");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询 Order 模块出错：", e);
        }
        return null;

    }

    /**
     * API支付查单接口(列表)2
     */
    @Override
    public List<MchtGatewayOrder> list(Map param) {
        try {

            String mchtId = (String) param.get("mchtId");
            String timeYYYYMM = (String) param.get("timeYYYYMM");
            String pageNo = (String) param.get("pageNo");
            String pageSize = (String) param.get("pageSize");
            String status = (String) param.get("status");

            logger.info("ServiceImpl查询支付订单收到客户端请求参数：mchtId=" + mchtId + ",pageNo=" + pageNo + ",pageSize=" + pageSize + ",timeYYYYMM=" + timeYYYYMM + ",");

            if (org.apache.commons.lang.StringUtils.isBlank(mchtId)) {
                mchtId = "";
            }
            if (org.apache.commons.lang.StringUtils.isBlank(timeYYYYMM) || timeYYYYMM.length() != 6) {
                timeYYYYMM = DateUtils2.getNowTimeStr("yyyyMM");
                logger.info("ServiceImpl查询支付订单收到客户端请求参数：yyyyMM=空,赋予默认值:" + timeYYYYMM);
            }
            if (org.apache.commons.lang.StringUtils.isBlank(pageNo)) {
                pageNo = "1";
                logger.info("ServiceImpl查询支付订单收到客户端请求参数：pageNo=空,赋予默认值:" + pageNo);
            }
            if (org.apache.commons.lang.StringUtils.isBlank(pageSize)) {
                pageSize = "10";
                logger.info("ServiceImpl查询支付订单收到客户端请求参数：pageSize=空,赋予默认值:" + pageSize);
            }


            PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNo(Integer.parseInt(pageNo));
            pageInfo.setPageSize(Integer.parseInt(pageSize));

            MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
            mchtGatewayOrder.setPageInfo(pageInfo);
            mchtGatewayOrder.setMchtId(mchtId);
            mchtGatewayOrder.setSuffix(DateUtils2.getNowTimeStr("yyyyMM"));
            mchtGatewayOrder.setStatus(status);

            //queryPay查询支付订单接口
            logger.info("ServiceImpl调用查询支付订单接口，传入的mchtGatewayOrder查询条件：" + JSONObject.toJSONString(mchtGatewayOrder));
            return list(mchtGatewayOrder);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ServiceImpl查询订单列表异常：", e);
        }
        return null;

    }


}
