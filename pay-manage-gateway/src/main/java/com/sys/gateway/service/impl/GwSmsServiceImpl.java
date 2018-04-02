package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.common.db.JedisConnPool;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.*;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.MerchantService;
import com.sys.gateway.service.GwSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 短信服务
 * <p>
 * Created by chenzhaole on 2018/3/31.
 */
@Service
public class GwSmsServiceImpl implements GwSmsService {

    protected final Logger logger = LoggerFactory.getLogger(GwSmsServiceImpl.class);

    //TODO
    public final String SMS_KEY = "ZhrtZhrt";


    @Autowired
    MerchantService merchantService;

    /**
     * 发送验证码
     *
     * @param data
     **/
    @Override
    public CommonResult smsSend(Map data) throws Exception {

        String BIZ_NAME = "短信接口(发送验证码)-";
        CommonResult smsRes = new CommonResult();

        JSONObject json = (JSONObject) JSONObject.toJSON(data);


        String version = (String) data.get("version");
        String mchtId = (String) data.get("mchtId");
        String biz = (String) data.get("biz");
        String orderId = (String) data.get("orderId");
        String opType = (String) data.get("opType");
        String mchtSign = (String) data.get("sign");
        String midoid = mchtId + "-" + orderId;
        logger.info(BIZ_NAME + midoid + " 客户端入参=" + JSON.toJSONString(data));

        if (!"1".equals(opType)) {
            logger.info(BIZ_NAME + midoid + " 发送验证码的opType参数错误");
            smsRes.setRespCode(ErrorCodeEnum.E1003.getCode());
            smsRes.setRespMsg("opType参数错误");
            return smsRes;
        }

        data.remove("sign");
        if (!SignUtil.checkSign(data, mchtSign, SMS_KEY)) {
            logger.info(BIZ_NAME + midoid + " 验签失败");
            smsRes.setRespCode(ErrorCodeEnum.E1009.getCode());
            smsRes.setRespMsg(ErrorCodeEnum.E1009.getDesc());
            return smsRes;
        }

        logger.info(BIZ_NAME + midoid + " 查询商户信息[start] mchtId=" + mchtId);
        MchtInfo merchant = merchantService.queryByKey(mchtId);
        String dbMobile = merchant.getFinanceMobile();//财务联系手机
        String mobile = DesUtil32.decode(dbMobile, SMS_KEY);
        logger.info(BIZ_NAME + midoid + " 查询商户信息[end] 财务代付手机号码=" + dbMobile + " 解密后手机号码=" + mobile);

        String verifyCode = String.valueOf(NumberUtils.buildRandom(6));
        json.put("verifyCode", verifyCode);
        json.put("createUTC", DateUtils2.getNowUTC());
        String contents = "尊敬的客户，您本次代付操作验证码为：" + verifyCode + "，请在5分钟内完成验证。如非本人操作，请无视此短信";

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            String redisKey = IdUtil.REDIS_PROXYPAY_SMS_CODE + mchtId;
            logger.info(BIZ_NAME + midoid + " 请求数据存入Redis[start] redisKey=" + redisKey + " 数据=" + json.toJSONString());
            pool = JedisConnPool.getPool("insertData商户网关订单");
            jedis = pool.getResource();
            String rt = jedis.set(redisKey, json.toJSONString());
            long rt2 = jedis.expire(redisKey, 60 * 5);//五分钟
            logger.info(BIZ_NAME + midoid + " 请求数据存入Redis[end] 存储rt=" + rt + " 有效期rt2=" + rt2);
        } catch (JedisConnectionException je) {
            je.printStackTrace();
            logger.error(BIZ_NAME + midoid + "Redis Jedis连接异常：" + je.getMessage());
        } catch (Exception e) {
            logger.error(BIZ_NAME + midoid + "Redis Jedis操作异常:" + e.getMessage());
            e.printStackTrace();
        } finally {
            JedisConnPool.returnResource(pool, jedis, "");
        }

        CommonResult sendResult = sendPlatSms(midoid, mobile, contents);
        if (ErrorCodeEnum.SUCCESS.getCode().equals(sendResult.getRespCode())) {
            logger.info("短信发送成功 mobile=" + mobile + " contents=" + contents);
            smsRes.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
            smsRes.setRespMsg("发送短信成功");
        } else {
            logger.info("短信发送失败 mobile=" + mobile + " contents=" + contents);
        }

        smsRes.setData(midoid);
        logger.info(BIZ_NAME + midoid + " 返回调用者 smsRes=" + JSON.toJSONString(smsRes));
        return smsRes;
    }

    /**
     * 校验验证码
     *
     * @param data
     **/
    @Override
    public CommonResult smsVerify(Map data) throws Exception {

        String BIZ_NAME = "短信接口(校验验证码)-";
        CommonResult smsRes = new CommonResult();
        smsRes.setRespCode(ErrorCodeEnum.E1013.getCode());
        smsRes.setRespMsg(ErrorCodeEnum.E1013.getDesc());

        String version = (String) data.get("version");
        String mchtId = (String) data.get("mchtId");
        String biz = (String) data.get("biz");
        String orderId = (String) data.get("orderId");
        String opType = (String) data.get("opType");
        String mchtSign = (String) data.get("sign");
        String mchtVerifyCode = (String) data.get("verifyCode");
        String midoid = mchtId + "-" + orderId;
        logger.info(BIZ_NAME + midoid + " 客户端入参=" + JSON.toJSONString(data));

        if (!"2".equals(opType)) {
            logger.info(BIZ_NAME + midoid + " 检验验证码的opType参数错误");
            smsRes.setRespCode(ErrorCodeEnum.E1003.getCode());
            smsRes.setRespMsg("opType参数错误");
            return smsRes;
        }

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            String redisKey = IdUtil.REDIS_PROXYPAY_SMS_CODE + mchtId;
            logger.info(BIZ_NAME + midoid + " 查询Redis数据[start] redisKey=" + redisKey);
            pool = JedisConnPool.getPool();
            jedis = pool.getResource();
            if (jedis.exists(redisKey)) {
                String redisData = jedis.get(redisKey);
                logger.info(BIZ_NAME + midoid + " 查询Redis数据[end] redisKey=" + redisKey + "redis数据=" + redisData);
                Map map = JSON.parseObject(redisData, Map.class);
                if (mchtVerifyCode.equals(map.get("verifyCode")) && orderId.equals(map.get("orderId"))) {
                    smsRes.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                    smsRes.setRespMsg("交易验证码成功");
                    long rt = jedis.del(redisKey);
                    logger.info(BIZ_NAME + midoid + " 短信验证码校验成功 redisKey=" + redisKey + "删除数据rt=" + rt);
                } else {
                    logger.info(BIZ_NAME + midoid + " 短信验证码校验失败");
                }
            }

        } catch (JedisConnectionException je) {
            je.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisConnPool.returnResource(pool, jedis, "");
        }

        smsRes.setData(midoid);
        logger.info(BIZ_NAME + midoid + " 返回调用者 smsRes=" + JSON.toJSONString(smsRes));
        return smsRes;
    }


    /**
     * 发短信
     **/
    private CommonResult sendPlatSms(String midoid, String mobile, String contents) {
        String BIZ_NAME = "短信接口(调用短信服务)-";
        logger.info(BIZ_NAME + midoid + " [start] mobile=" + mobile + " contents=" + contents);
        CommonResult result = new CommonResult();

        String sendTime = "";//String	可空	发送时间,为空表示立即发送	yyyyMMddHHmmss 格式
        String appendID = "12";//String	必填	附加号码
        try {
            contents = URLEncoder.encode(URLEncoder.encode(contents, "UTF-8"), "UTF-8");

            String contentType = "15";//String	必填	消息类型取值有15和8	15：以普通短信形式下发，8：以长短信形式下发
            String spid = "1";//String	必填	用来标识短信端口号 	固定值 1
            String num = "1000";//String	必填	短信有效期，单位 秒	例如：1000 秒
            String pvData = appendID + contents + contentType + mobile + num + sendTime + spid;
            String key = "248125f5e61b41f39d9609d952eeed64";
            String pvalidate = "";//String	必填	签名信息	根据签名工具和双方事先约定好的的秘钥对参数进行加密，加密工具类，及其秘钥参见后文 ：加密秘钥来源 和 加密工具类。	签名生成规则见后文中的：签名生成规则

            pvalidate = MD5Util.MD5Encode(pvData + key);

            String url = "http://115.28.179.55:9000/gdsms/sendSms";
            String reqData = "?sendTime=" + sendTime + "&appendID=" + appendID + "&desMobile=" + mobile//
                    + "&contents=" + contents + "&contentType=" + contentType + "&spid=" + spid + "&num=" + num + "&pvalidate=" + pvalidate;
            url = url + reqData;
            logger.info(BIZ_NAME + midoid + " 发送短信请求url：" + url);

            String retData = HttpUtil.get(url);
            retData = URLDecoder.decode(retData, "UTF-8");
            logger.info(BIZ_NAME + midoid + " 发送短信返回值retData：" + retData);

            //{"msg":"请求成功","status":"0"}
            String status = JSON.parseObject(retData).getString("status");
            result.setRespCode(status);
            if ("0".equals(status)) {
                result.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
                result.setRespMsg("获取验证码成功");
            } else {
                result.setRespCode(ErrorCodeEnum.FAILURE.getCode());
                result.setRespMsg("获取验证码失败,短信网管返回status:" + status);
            }

            logger.info(BIZ_NAME + midoid + " [end] 调用结果(成功=0) status=" + status);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BIZ_NAME + midoid + " [error] e.msg：" + e.getMessage());
        }
        return result;
    }


}
