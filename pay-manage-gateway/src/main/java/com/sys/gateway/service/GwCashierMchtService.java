package com.sys.gateway.service;

import com.sys.boss.api.entry.CommonResult;

import javax.servlet.http.HttpServletRequest;

public interface GwCashierMchtService {
    CommonResult resolveAndcheckParam(HttpServletRequest request);
}
