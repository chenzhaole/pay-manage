package com.sys.admin.modules.sys.service;

public interface RedisVerifyBySimService extends RedisKeyConst {
    public void insertData(String shiftNumber,String mobile,String stationId,String sendDate,String businessType);
    public int validateOrderBySimOperateDay(String mobile,int canSellNumber);
    public int validateOrderbBySimSendDay(String mobile,String stationId,String sendDate,int canSellNumber);
}
