package com.sys.manage.notify.service;

import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TranException;

public interface IChannelQuickPayService extends IChannelService {

    /**
     *快捷支付--鉴权绑卡
     * @param trade
     * @return
     * @throws TranException
     */
     Result createQuickAuthCardOrder(Trade trade)throws TranException;

    /**
     *快捷支付--绑卡短信校验
     * @param trade
     * @return
     * @throws TranException
     */
     Result createQuickOnCardCheckSms(Trade trade)throws TranException;

    /**
     *快捷支付--绑卡支付
     * @param trade
     * @return
     * @throws TranException
     */
     Result createQuickOnCardPay(Trade trade)throws TranException;

    /**
     *快捷支付--绑卡支付短信校验
     * @param trade
     * @return
     * @throws TranException
     */
     Result createQuickCardPayCheckSms(Trade trade)throws TranException;

    /**
     *快捷支付--绑卡支付通知
     * @param trade
     * @return
     * @throws TranException
     */
     Result notifyQuickCardPay(Trade trade)throws TranException;

    /**
     *快捷支付--解绑卡
     * @param trade
     * @return
     * @throws TranException
     */
     Result createQuickUnBundCardOrder(Trade trade)throws TranException;


    /**
     *快捷支付--绑卡信息列表查询
     * @param trade
     * @return
     * @throws TranException
     */
     Result selectQuickCardListInfo(Trade trade)throws TranException;

    /**
     *快捷支付--查询支付结果
     * @param trade
     * @return
     * @throws TranException
     */
     Result selectQuickPayInfo(Trade trade)throws TranException;
    /**
     * 四要素验证
     * @param trade
     * @return
     * @throws TranException
     */
     Result Auth4ElementOrder(Trade trade)throws TranException;

}
