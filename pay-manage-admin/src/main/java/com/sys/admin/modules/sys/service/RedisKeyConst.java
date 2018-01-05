package com.sys.admin.modules.sys.service;

import com.sys.admin.common.config.GlobalConfig;

public interface RedisKeyConst {
    static final String REDIS_IP = GlobalConfig.getConfig("redis.ip");
    static final int REDIS_PORT = Integer.parseInt(GlobalConfig.getConfig("redis.port"));
    static final String  REDIS_PWD=GlobalConfig.getConfig("redis.pwd");
    //验证同一天每人每班次只能买N张票key前缀
    public static final String VERIFY_DAY_ID_SHIFT="VERIFY:ORDER:";
    //验证同一天每身份证每班次只能买N张票Hash key前缀
    //买票业务前缀
    public static final String VERIFY_DAY_ID_SHIFT_BUY="BUY:";
    //退票业务前缀
    public  static final String VERIFY_DAY_ID_SHIFT_REFUND="REFUND:";
    //改签业务前缀
    public static final String VERIFY_DAY_ID_SHIFT_ALTER="ALTER:";
    //生成订单号业务前缀
    public static final String MAXNO_ORDERNO="MAXNO:ORDERNO";
    //生成退单号业务前缀 
    public static final String MAXNO_REFUNDNO="MAXNO:REFUNDNO";
    //验证同一天每手机号每班次只能买N张票Hash key前缀
    public static final String VERIFY_DAY_SIM="VERIFY:SIM:";
    //买票业务前缀
    public static final String VERIFY_DAY_SIM_BUY="BUY:";
    //退票业务前缀
    public  static final String VERIFY_DAY_SIM_REFUND="REFUND:";
    //改签业务前缀
    public static final String VERIFY_DAY_SIM_ALTER="ALTER:";
    //通用生成自增序列
    public static final String MAXNO_NORMAL="MAXNO:NORMAL";
    //生成班次自增id
    public static final String MAXNO_SHIFTID="PAGE:SEL:PLANID:MAXNO";
    //存班次id与生成long对照
    public static final String SHIFTID_STR="PAGE:SEL:PLANID:STR:";
    //存生成Long与班次信息对照
    public static final String SHIFTID_LONG="PAGE:SEL:PLANID:LONG:";
    
    public static final String PAGE_SEL_PLAN = "PAGE:SEL:PLAN:";
    
    //余票信息
    public static final String SHIFTID_TICKETLEFT_STR="PAGE:SEL:TICKETINFO:STR:";
    
    
}
