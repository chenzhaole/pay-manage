package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.service.trade.handler.ITradeApiPayHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MerchantService;
import com.sys.gateway.service.GwSdkConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GwSdkConfigServiceImpl implements GwSdkConfigService {

    protected final Logger logger = LoggerFactory.getLogger(GwSdkConfigServiceImpl.class);
    private final String BIZ_NAME = "支付SDK-获取配置信息-";

    @Autowired
    private ITradeApiPayHandler tradeApiPayHandler;

    @Autowired
    private MerchantService merchantService;

    /**
     * 校验参数
     **/
    @Override
    public CommonResponse checkParam(String paramStr) {
        CommonResponse checkResp = new CommonResponse();
        try {
            if (paramStr.endsWith("=")) {
                paramStr = paramStr.substring(0, paramStr.length() - 1);
            }
            //解析请求参数
            Map map = JSON.parseObject(paramStr, Map.class);
            String version = map.containsKey("version") ? (String) map.get("version") : "";
            String mchtId = map.containsKey("mchtId") ? (String) map.get("mchtId") : "";
            String userId = map.containsKey("userId") ? (String) map.get("userId") : "";
            String sim = map.containsKey("sim") ? (String) map.get("sim") : "";
            String imsi = map.containsKey("imsi") ? (String) map.get("imsi") : "";
            String imei = map.containsKey("imei") ? (String) map.get("imei") : "";
            String lon = map.containsKey("lon") ? (String) map.get("lon") : "";
            String lat = map.containsKey("lat") ? (String) map.get("lat") : "";
            String ip = map.containsKey("ip") ? (String) map.get("ip") : "";
            String param = map.containsKey("param") ? (String) map.get("param") : "";
            String sign = map.containsKey("sign") ? (String) map.get("sign") : "";

            if (StringUtils.isBlank(version) || StringUtils.isBlank(mchtId) || StringUtils.isBlank(sign)) {
                checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
                checkResp.setRespCode("[version],[mchtId],[sign]参数值不能为空");
                logger.info("[version],[mchtId],[sign]请求参数值不能为空，即客户端请求参数：" + JSON.toJSONString(map));
                return checkResp;
            }

            checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
            checkResp.setData(map);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ_NAME + "参数异常e：" + e.getMessage());
            checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
            checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
        }
        return checkResp;
    }

    /**
     * 获取配置信息
     **/
    @Override
    public Map config(Map map, String ip) {
        Map rtnMap = new HashMap();
        try {
            Map defaultPaySdkMap = new HashMap();
            defaultPaySdkMap.put("serviceQQ", "");
            defaultPaySdkMap.put("serviceTel", "");
            defaultPaySdkMap.put("version", "200");
            defaultPaySdkMap.put("isShowPayResultPage", "1");
            defaultPaySdkMap.put("isShowPayPage", "1");
            defaultPaySdkMap.put("payUrl", "http://106.2.6.41:12080/gateway/sdk/commPay/");
            defaultPaySdkMap.put("al", "0");
            defaultPaySdkMap.put("wx", "1");
            defaultPaySdkMap.put("sn", "0");
            defaultPaySdkMap.put("qq", "0");
            defaultPaySdkMap.put("jd", "0");
            defaultPaySdkMap.put("yl", "0");
            defaultPaySdkMap.put("qj", "0");

            String mchtId = (String) map.get("mchtId");
            MchtInfo merchant = merchantService.queryByKey(mchtId);

            if (merchant == null) {//判断商户是否存在
                rtnMap.put("code", ErrorCodeEnum.E1113.getCode());
                rtnMap.put("msg", ErrorCodeEnum.E1113.getDesc());
                logger.info(BIZ_NAME + "判断商户是否存在,查询商户信息为null,mchtId:" + mchtId + ",返回客户端:" + JSON.toJSONString(rtnMap));
                return rtnMap;
            }

            if (!"1".equals(merchant.getStatus())) {//判断商户关停
                rtnMap.put("code", ErrorCodeEnum.E1002.getCode());
                rtnMap.put("msg", ErrorCodeEnum.E1002.getDesc());
                logger.info(BIZ_NAME + "判断商户关停,状态为关停,商户status:" + merchant.getStatus() + ",返回客户端:" + JSON.toJSONString(rtnMap));
                return rtnMap;
            }

            String mchtKey = merchant.getMchtKey();
            String mchtSignStr = (String) map.get("sign");
            map.remove("sign");//参数sign不参与签名
            String platSignStr = SignUtil.md5Sign(map, mchtKey, mchtId);// 签名
            logger.info(BIZ_NAME + "平台签名原始字符串: " + JSON.toJSONString(map));
            logger.info(BIZ_NAME + "平台签名结果字符串: " + platSignStr.toUpperCase());
            logger.info(BIZ_NAME + "商户签名参数字符串: " + mchtSignStr);
            if (!mchtSignStr.equalsIgnoreCase(platSignStr)) {//支付SDK下单接口验签
                rtnMap.put("code", ErrorCodeEnum.E1119.getCode());
                rtnMap.put("msg", ErrorCodeEnum.E1119.getDesc());
                logger.info(BIZ_NAME + "签名错误,返回客户端:" + JSON.toJSONString(rtnMap));
                return rtnMap;
            }

            if (merchant == null) {
                return defaultPaySdkMap;
            }

            String extend2 = merchant.getExtend2();
            if (StringUtils.isBlank(extend2)) {
                logger.info(BIZ_NAME + "商户基本信息extend2的值为空,返回默认配置信息:" + JSON.toJSONString(defaultPaySdkMap));
                return defaultPaySdkMap;
            }

            Map extend2Map = JSON.parseObject(extend2, Map.class);
            if (extend2Map.containsKey("paySdk")) {
                String configStr = (String) extend2Map.get("paySdk");
                rtnMap = JSON.parseObject(configStr, Map.class);
                logger.info(BIZ_NAME + "mchtInfo基本信息extend2的JSON数据已包含paySdk属性,返回配置信息:" + JSON.toJSONString(rtnMap));

            } else {
//				extend2Map.put("paySdk",JSON.toJSONString(defaultPaySdkMap));
//				merchant.setExtend2(JSON.toJSONString(extend2Map));
//				int rs = merchantService.saveByKey(merchant);
//				logger.info(BIZ_NAME+"mchtInfo基本信息extend2的JSON数据不包含paySdk属性,更新rs:"+rs+",extend2字段追加paySdk属性,最新extend2数据:"+JSON.toJSONString(extend2Map));
                rtnMap = defaultPaySdkMap;
                logger.info(BIZ_NAME + "mchtInfo基本信息extend2的JSON数据不包含paySdk属性,返回默认配置信息:" + JSON.toJSONString(rtnMap));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ_NAME + "异常e：" + e.getMessage());
        }
        return rtnMap;
    }

}
