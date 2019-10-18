package com.sys.admin.modules.wap.order.service;

import java.util.List;
import java.util.Map;

/**
 * Created by chenzhaole on 2019/8/17.
 */
public interface QrWebOrderService {


    int amount(String mchtId, String pageNo, String status, String beginTime, String endTime);

    List list(String mchtId, String pageNo, String status, String mchtOrderId, String platOrderId, String beginTime, String endTime, String yyyyMM);

    Map queryByPlatOrderId(String platOrderId);

}
