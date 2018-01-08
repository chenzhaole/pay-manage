package com.sys.manage.notify.service;

import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TranException;

public interface IChannelService {
	 /**
     * 创建扫码订单
     */
    Result createOrder(Trade trade) throws TranException ;
    /**
     * 创建付款码订单
     */
    Result createBarcodeOrder(Trade trade) throws TranException ;
    
    /**
     * 查询订单
     */
    Result queryOrder(Trade trade) throws TranException ;
    
    /**
     * 取消订单
     */
    Result cancelOrder(Trade trade) throws TranException ;
    
    /**
     * 退款
     */
    Result refundOrder(Trade trade) throws TranException ;
    /**
     * 查询退款
     */
    Result queryRefundOrder(Trade trade) throws TranException ;

    /**
     * 发送代付
     */
    Result createDF(Trade trade) throws TranException ;
    
    /**
     * 发送代扣
     */
    Result createDK(Trade trade) throws TranException ;

    /**
     * 查询代付订单
     */
    Result queryDF(Trade trade) throws TranException ;
    
    /**
     * 查询代扣订单
     */
    Result queryDK(Trade trade) throws TranException ;
    
    /**
     * 查询信用和余额
     */
    Result queryCreditBalance(Trade trade) throws TranException ;

    /**
     * 更新交易状态并通知下游商户
     */
    Result reciveNotify(Trade trade) throws TranException;
}
