package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.request.TradeReqHead;
import com.sys.boss.api.entry.trade.request.authcardelement.AuthCardElementRequestBody;
import com.sys.boss.api.entry.trade.request.authcardelement.TradeAuthCardElementRequest;
import com.sys.boss.api.entry.trade.response.TradeBaseResponse;
import com.sys.boss.api.entry.trade.response.authcardelement.AuthCardeLElementCreateResponse;
import com.sys.boss.api.service.trade.handler.ITradeAuthCardElementHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.PlatBankcardValid;
import com.sys.gateway.service.GwAuthCardElementService;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sys.core.service.ChanBankcardValidService;
import com.sys.core.service.PlatBankcardValidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chenzhaole on 2018/11/7.
 */
@Service
public class GwAuthCardElementServiceImpl implements GwAuthCardElementService
{

    protected final Logger logger = LoggerFactory.getLogger(GwAuthCardElementServiceImpl.class);

    @Autowired
    private PlatBankcardValidService platBankcardValidService;

    @Autowired
    private ChanBankcardValidService chanBankcardValidService;

    @Autowired
    ITradeAuthCardElementHandler tradeAuthCardElementHandler;


    /**查找四要素是否已经验证过*/
    private PlatBankcardValid checkAuthCardElement(AuthCardElementRequestBody authCardElementRequestBody) {
        String idCard = authCardElementRequestBody.getIdCard();//证件号码
        String userName = authCardElementRequestBody.getUserName();//	姓名
        String bankCard = authCardElementRequestBody.getBankCard();//	银行卡号
        String mobile = authCardElementRequestBody.getMobile();//手机号
        PlatBankcardValid platBankcardValid = new PlatBankcardValid();
        platBankcardValid.setPayType(PayTypeEnum.QUICK_REAL_AUTH.getCode());
        platBankcardValid.setCertId(idCard);
        platBankcardValid.setCardName(userName);
        platBankcardValid.setCardNo(bankCard);
        platBankcardValid.setMobile(mobile);
        //鉴权状态 0:初始创建,-1:未知失败,2:支付成功,3:处理中，6:鉴权交易成功，鉴权结果失败,4006:鉴权交易失败，7:组合支付--鉴权成功待支付
        platBankcardValid.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
        List<PlatBankcardValid> bankcardValids = platBankcardValidService.list(platBankcardValid);
        if(null != bankcardValids && bankcardValids.size() > 0){
            return bankcardValids.get(0);
        }
        return null;
    }

    /**
     * 实名认证接口即四要素、六要素认证校验参数
     *
     * @param paramStr
     **/
    @Override
    public CommonResponse checkAuthCardParam(String paramStr) {
        CommonResponse checkResp = new CommonResponse();
        try {
            if(paramStr.endsWith("=")){
                paramStr = paramStr.substring(0,paramStr.length()-1);
            }
            //解析请求参数
            TradeAuthCardElementRequest tradeRequest = JSON.parseObject(paramStr, TradeAuthCardElementRequest.class);
            if (tradeRequest.getHead() == null || tradeRequest.getBody() == null || tradeRequest.getSign() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[head],[body],[sign]请求参数值不能为空");
                logger.error("[head],[body],[sign]请求参数值不能为空，即TradeAuthCardElementRequest=："+ JSONObject.toJSONString(tradeRequest));
                return checkResp;
            }

            TradeReqHead head = tradeRequest.getHead();
            if (head.getMchtId()== null || head.getVersion() == null || head.getBiz() == null) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[mchtId],[version],[biz]请求参数值不能为空");
                logger.error("[mchtId],[version],[biz]请求参数值不能为空，即TradeReqHead=："+JSONObject.toJSONString(head));
                return checkResp;
            }

            AuthCardElementRequestBody body = tradeRequest.getBody();
            if (StringUtils.isBlank(body.getOrderId())
                    || StringUtils.isBlank(body.getIdType())
                    || StringUtils.isBlank(body.getIdCard())
                    || StringUtils.isBlank(body.getUserName())
                    || StringUtils.isBlank(body.getBankCard())
                    || StringUtils.isBlank(body.getMobile())
                    || StringUtils.isBlank(body.getAuthCode())
                    || StringUtils.isBlank(body.getAuthMethod())){
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("实名认证接口缺少必传参数");
                logger.error("实名认证接口缺少必传参数，即AuthCardElementRequestBody=："+JSONObject.toJSONString(body));
                return checkResp;
            }

            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(tradeRequest);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("校验参数异常："+e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }

    /**
     * 实名认证即四要素、六要素认证接口
     *
     * @param tradeRequest
     * @param ip
     */
    @Override
    public TradeBaseResponse authCardElement(TradeBaseRequest tradeRequest, String ip) {
        AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseHead head = new AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseHead();
        AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseBody body = new AuthCardeLElementCreateResponse.AuthCardeLElementCreateResponseBody();
        String sign  ="";
        try {
            //实名认证前，先查下数据库是否已经存在，验证通过的，即根据四要素
            AuthCardElementRequestBody authCardElementRequestBody = ((TradeAuthCardElementRequest) tradeRequest).getBody();
            PlatBankcardValid platBankcardValid = this.checkAuthCardElement(authCardElementRequestBody);
            if(null != platBankcardValid){
                //已经认证过
                logger.info("调用boss-trade创建authCardElement实名认证订单前，从数据看查出改四要素已经认证过，即参数值tradeRequest："+JSON.toJSONString(tradeRequest)+", 数据库查出的数据为PlatBankcardValid："+JSONObject.toJSONString(platBankcardValid));
                head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                head.setRespMsg(ErrorCodeEnum.E1014.getDesc());
            }else {
                logger.info("调用boss-trade创建authCardElement实名认证订单，参数值tradeRequest：" + JSON.toJSONString(tradeRequest));
                CommonResult commonResult = (CommonResult) tradeAuthCardElementHandler.process(tradeRequest, ip);
                logger.info("调用boss-trade创建authCardElement实名认证订单，返回值commonResult：" + JSON.toJSONString(commonResult));
                if (ErrorCodeEnum.SUCCESS.getCode().equals(commonResult.getRespCode())) {
                    Result tranResult = (Result)commonResult.getData();
                    head.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                    head.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
                    body.setMchtId(tranResult.getMchtId());
                    body.setOrderId(tranResult.getMchtOrderNo());//商户订单号
                    body.setPlatId(tranResult.getOrderNo());//平台订单号
                    body.setSeq(UUID.randomUUID().toString().replaceAll("-", ""));
                    // 签名
                    Map<String, String> params = JSONObject.parseObject(
                            JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
                            });
                    sign = SignUtil.md5Sign(params, tranResult.getMchtKey(),authCardElementRequestBody.getOrderId());
                }else{
                    String respCode = StringUtils.isBlank(commonResult.getRespCode()) ? ErrorCodeEnum.FAILURE.getCode():commonResult.getRespCode();
                    String respMsg = StringUtils.isBlank(commonResult.getRespMsg()) ? ErrorCodeEnum.FAILURE.getDesc():commonResult.getRespMsg();
                    head.setRespCode(respCode);
                    head.setRespMsg(respMsg);
                }
            }
        } catch (Exception e) {
            head.setRespCode(ErrorCodeEnum.E8001.getCode());
            head.setRespMsg(ErrorCodeEnum.E8001.getDesc());
            e.printStackTrace();
            logger.error("创建authCardElement实名认证订单异常 e=" + e.getMessage());
        }
        return new AuthCardeLElementCreateResponse(head, body, sign);
    }
}
