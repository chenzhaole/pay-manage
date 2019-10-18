package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.trade.request.TradeBaseRequest;
import com.sys.boss.api.entry.trade.response.TradeBaseResponse;

/**
 * Created by chenzhaole on 2018/11/7.
 */
public interface GwAuthCardElementService {


    /**实名认证接口即四要素、六要素认证校验参数**/
    CommonResponse checkAuthCardParam(String paramStr);

    /**实名认证即四要素、六要素认证接口*/
    TradeBaseResponse authCardElement(TradeBaseRequest tradeRequest, String ip);
}
