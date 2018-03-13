package com.sys.manage.notify.service;

import com.sys.trans.api.entry.Result;
import com.sys.trans.api.entry.Trade;
import com.sys.trans.exception.TransException;

public interface IChannelQuickPayService extends IChannelService {

    /**
     *快捷支付--鉴权绑卡
     * @param trade
     * @return
     * @throws TransException
     */
     Result createQuickAuthCardOrder(Trade trade)throws TransException;

    /**
     *快捷支付--绑卡短信校验
     * @param trade
     * @return
     * @throws TransException
     */
     Result createQuickOnCardCheckSms(Trade trade)throws TransException;

    /**
     *快捷支付--绑卡支付
     * @param trade
     * @return
     * @throws TransException
     */
     Result createQuickOnCardPay(Trade trade)throws TransException;

    /**
     *快捷支付--绑卡支付短信校验
     * @param trade
     * @return
     * @throws TransException
     */
     Result createQuickCardPayCheckSms(Trade trade)throws TransException;

    /**
     *快捷支付--绑卡支付通知
     * @param trade
     * @return
     * @throws TransException
     */
     Result notifyQuickCardPay(Trade trade)throws TransException;

    /**
     *快捷支付--解绑卡
     * @param trade
     * @return
     * @throws TransException
     */
     Result createQuickUnBundCardOrder(Trade trade)throws TransException;


    /**
     *快捷支付--绑卡信息列表查询
     * @param trade
     * @return
     * @throws TransException
     */
     Result selectQuickCardListInfo(Trade trade)throws TransException;

    /**
     *快捷支付--查询支付结果
     * @param trade
     * @return
     * @throws TransException
     */
     Result selectQuickPayInfo(Trade trade)throws TransException;
    /**
     * 四要素验证
     * @param trade
     * @return
     * @throws TransException
     */
     Result Auth4ElementOrder(Trade trade)throws TransException;

}
