package com.sys.gateway.service;

import javax.servlet.http.HttpServletRequest;

import com.sys.boss.api.entry.trade.response.ownership.OwnershipResponse;

/**
 * @Description:网关接口业务处理接口
 * 
 * @author: ChenZL
 * @time: 2017年7月30日
 */
public interface BankCardOwnershipGwService {
	
	OwnershipResponse queryBankCardOwnership(String paramStr);
	


}
