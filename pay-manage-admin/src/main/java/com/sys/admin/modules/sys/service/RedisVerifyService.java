package com.sys.admin.modules.sys.service;

public interface RedisVerifyService extends  RedisKeyConst{
    public void insertData(String shiftNumber,String idno,String stationId,String sendDate,String businessType);
    public int validateOrderbByShift(String shiftNumber,String idno,String stationId,String sendDate,int canSellNumber);
    public int validateOrderbByStation(String idno,String stationId,String sendDate,int canSellNumber);
    public String getMaxNo(String businessType);
    public String getMaxRefundNo(String businessType);
    public Long getMaxNoLong(String businessType);
    public void insertData(String key, String value, int timeout, String businessType);
    public String queryData(String key, String businessType);
	void updateData(String key, String value);
   
}
