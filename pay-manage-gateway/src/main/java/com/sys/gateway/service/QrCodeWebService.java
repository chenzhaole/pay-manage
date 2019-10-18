package com.sys.gateway.service;

import com.sys.core.dao.dmo.MchtInfo;

import java.util.Map;

/**
 * Created by chenzhaole on 2019/7/20.
 */
public interface QrCodeWebService {

    public String convertKey(String mchtCode);

    public boolean checkQrcodeUrl(String mchtCode,String data);

    public boolean checkQrcodeConfirm(String mchtCode,String data);

    public Map prepareOrder(String mchtCode,String payType);

    public Map confirmOrder(Map dataMap, String ip);

//    public String getConfirmtoken(String mchtCode,String payType);

    public MchtInfo findMchtInfo(String mchtCode);

}
