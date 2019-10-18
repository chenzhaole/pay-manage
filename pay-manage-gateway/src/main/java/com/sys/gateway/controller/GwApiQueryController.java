package com.sys.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.apipay.TradeApiQueryRequest;
import com.sys.boss.api.entry.trade.request.apipay.TradeQueryFaceRequest;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderCreateResponse;
import com.sys.boss.api.entry.trade.response.apipay.ApiPayOrderQueryResponse;
import com.sys.boss.api.entry.trade.response.apipay.QueryFaceResponse;
import com.sys.boss.api.service.trade.handler.ITradeApiQueryHandler;
import com.sys.boss.api.service.trade.handler.ITradeQueryFaceHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.DateUtils;
import com.sys.common.util.DateUtils2;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.service.TaskLogService;
import com.sys.gateway.common.IpUtil;
import com.sys.gateway.service.GwApiPayService;
import com.sys.gateway.service.GwApiQueryService;
import com.sys.gateway.service.GwQueryFaceService;
import com.sys.trans.api.entry.Order;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询支付订单接口
 */
@Controller
@RequestMapping(value = "")
public class GwApiQueryController {

    protected final Logger logger = LoggerFactory.getLogger(GwApiQueryController.class);

    @Autowired
    GwApiQueryService gwApiQueryService;

    @Autowired
    GwQueryFaceService gwQueryFaceService;

    @Autowired
    private TaskLogService taskLogService;


    @Autowired
    private ITradeQueryFaceHandler tradeQueryFaceHandler;


    /**
     * api支付查询支付订单(带翻页的数据集)
     */
    @RequestMapping(value = "/gateway/api/queryList")
    @ResponseBody
    public String queryList(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        List<MchtGatewayOrder> list = new ArrayList<>();
        try {
            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info("queryList查询支付订单获取到客户端请求ip：" + ip);
//            data = URLDecoder.decode(data, "utf-8");
//            logger.info("queryList查询支付订单收到客户端请求参数后做url解码后的data值为：" + data);

//            JSONObject json = JSON.parseObject(data);
//            String mchtId = json.getString("mchtId");
//            String timeYYYYMM = json.getString("timeYYYYMM");
//            String pageNo = json.getString("pageNo");
//            String pageSize = json.getString("pageSize");

            String mchtId = request.getParameter("mchtId");
            String timeYYYYMM = request.getParameter("timeYYYYMM");
            String pageNo = request.getParameter("pageNo");
            String pageSize = request.getParameter("pageSize");

            logger.info("queryList查询支付订单收到客户端请求参数：mchtId=" + mchtId + ",pageNo=" + pageNo + ",pageSize=" + pageSize + ",timeYYYYMM=" + timeYYYYMM + ",");

            if (StringUtils.isBlank(mchtId)) {
                mchtId = "";
            }
            if (StringUtils.isBlank(timeYYYYMM) || timeYYYYMM.length() != 6) {
                timeYYYYMM = DateUtils2.getNowTimeStr("yyyyMM");
            }
            if (StringUtils.isBlank(pageNo) || !NumberUtils.isNumber(pageNo)) {
                pageNo = "1";
            }
            if (StringUtils.isBlank(mchtId) || !NumberUtils.isNumber(pageSize)) {
                pageSize = "10";
            }


            PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNo(Integer.parseInt(pageNo));
            pageInfo.setPageSize(Integer.parseInt(pageSize));

            MchtGatewayOrder mchtGatewayOrder = new MchtGatewayOrder();
            mchtGatewayOrder.setPageInfo(pageInfo);
            mchtGatewayOrder.setMchtId(mchtId);
            mchtGatewayOrder.setSuffix(DateUtils2.getNowTimeStr("yyyyMM"));

            //queryPay查询支付订单接口
            logger.info("queryList调用查询支付订单接口，传入的mchtGatewayOrder查询条件：" + JSONObject.toJSONString(mchtGatewayOrder));
            list = gwApiQueryService.list(mchtGatewayOrder);
            if(CollectionUtils.isEmpty(list)){
                logger.info("queryList查询支付订单接口，返回下游list为空");
            }
            logger.info("queryList调用q查询支付订单接口，返回的CommOrderQueryResponse信息：" + JSONObject.toJSONString(list));


        } catch (Exception e) {
            logger.error("queryList查询支付订单接口抛异常" + e);
        }
        return JSON.toJSONString(list);
    }


    /**
     * api支付查询支付订单(单条数据)
     */
    @RequestMapping(value = "/gateway/api/queryPay")
    @ResponseBody
    public String queryPay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        ApiPayOrderQueryResponse apiPayResp = new ApiPayOrderQueryResponse();
        ApiPayOrderQueryResponse.ApiPayOrderQueryResponseHead head = new ApiPayOrderQueryResponse.ApiPayOrderQueryResponseHead();

        try {
            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info("queryPay查询支付订单获取到客户端请求ip：" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("queryPay查询支付订单收到客户端请求参数后做url解码后的值为：" + data);
            //校验请求参数
            CommonResponse checkResp = gwApiQueryService.checkParam(data);
            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            logger.info("queryPay查询支付订单校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));
            if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                head.setRespCode(checkResp.getRespCode());
                head.setRespMsg(checkResp.getRespMsg());
                apiPayResp.setHead(head);
            } else {
                //queryPay查询支付订单接口
                TradeApiQueryRequest tradeRequest = (TradeApiQueryRequest) checkResp.getData();
                logger.info("调用queryPay查询支付订单接口，传入的TradeCommRequest信息：" + JSONObject.toJSONString(tradeRequest));
                apiPayResp = (ApiPayOrderQueryResponse) gwApiQueryService.query(tradeRequest, ip);
                logger.info("调用queryPay查询支付订单接口，返回的CommOrderQueryResponse信息：" + JSONObject.toJSONString(apiPayResp));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("queryPay查询支付订单接口抛异常" + e.getMessage());
            head.setRespCode(ErrorCodeEnum.FAILURE.getCode());
            head.setRespMsg("支付网关错误：" + e.getMessage());
        }
        logger.info("queryPay查询支付订单接口，返回下游商户值：" + JSON.toJSONString(apiPayResp));
        return JSON.toJSONString(apiPayResp);
    }

    /**
     * 面值库存查询
     */
    @RequestMapping(value = "/gateway/api/queryFace")
    @ResponseBody
    public String queryFace(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws java.io.IOException {
        QueryFaceResponse queryFaceResponse = new QueryFaceResponse();
        queryFaceResponse.setCode(ErrorCodeEnum.SUCCESS.getCode());
        try {
            //请求ip
            String ip = IpUtil.getRemoteHost(request);
            logger.info("queryFace面值库存查询获取到客户端请求ip：" + ip);
            data = URLDecoder.decode(data, "utf-8");
            logger.info("queryFace面值库存查询收到客户端请求参数后做url解码后的值为：" + data);
            //校验请求参数
            CommonResponse checkResp = gwQueryFaceService.checkParam(data);
            logger.info("queryFace面值库存查询校验请求参数的结果为：" + JSONObject.toJSONString(checkResp));
            if (!ErrorCodeEnum.SUCCESS.getCode().equals(checkResp.getRespCode())) {
                queryFaceResponse.setCode(checkResp.getRespCode());
                queryFaceResponse.setMsg(checkResp.getRespMsg());
            } else {
                //queryFace面值库存接口
                TradeQueryFaceRequest tradeRequest = (TradeQueryFaceRequest) checkResp.getData();
                logger.info("调用queryFace面值库存查询接口，传入的TradeCommRequest信息：" + JSONObject.toJSONString(tradeRequest));
                queryFaceResponse = gwQueryFaceService.query(tradeRequest, ip);
                logger.info("调用queryFace面值库存查询接口，返回的TradeQueryFaceRequest信息：" + JSONObject.toJSONString(queryFaceResponse));
            }
        } catch (Exception e) {
            logger.error("queryFace面值库存查询接口抛异常", e);
            queryFaceResponse.setCode(ErrorCodeEnum.FAILURE.getCode());
            queryFaceResponse.setMsg("面值库存查询错误：" + e.getMessage());
        }
        logger.info("queryFace面值库存查询接口，返回下游商户值：" + JSON.toJSONString(queryFaceResponse));
        return JSON.toJSONString(queryFaceResponse);
    }

    /**
     * 检查库存信息
     * 2019-01-30 12:00:01
     *
     * @param request
     * @return
     */
    @RequestMapping("/gateway/api/checkStockAndFace")
    @ResponseBody
    public String checkStockAndFace(@RequestParam(required = false, value = "id") Integer logId, HttpServletRequest request) {
        boolean checkFalg = tradeQueryFaceHandler.checkStockAndFace();
        CommonResult result = new CommonResult();
        result.setRespMsg("SUCCESS");
        result.setRespCode("000000");
        taskLogService.recordLog(logId, result);
        logger.info("代付API，【定时任务代付查单】任务执行logId结束：" + logId + " " + JSON.toJSONString(result));
        if (checkFalg) {
            return "库存请求数正常";
        } else {
            return "库存请求数不正常";
        }
    }
}
