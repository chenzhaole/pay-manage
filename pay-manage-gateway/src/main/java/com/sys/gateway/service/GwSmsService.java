package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResult;

import java.util.Map;

/**
 * Created by chenzhaole on 2018/3/31.
 */
public interface GwSmsService {

    /** 发送验证码 **/
    CommonResult smsSend(Map data) throws Exception;

    /** 校验验证码 **/
    CommonResult smsVerify(Map data) throws Exception;

}
