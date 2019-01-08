package com.sys.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResponse;
import com.sys.boss.api.entry.CommonResult;
import com.sys.boss.api.entry.trade.request.apipay.TradeQueryFaceRequest;
import com.sys.boss.api.entry.trade.response.apipay.QueryFaceResponse;
import com.sys.boss.api.service.trade.handler.ITradeQueryFaceHandler;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.util.SignUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.AccountAmount;
import com.sys.core.service.AccountAmountService;
import com.sys.gateway.service.GwPublicAccountService;
import com.sys.gateway.service.GwQueryFaceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * 公户余额查询实现类
 */
@Service
public class GwPublicAccountServiceImpl implements GwPublicAccountService {

	protected final Logger logger = LoggerFactory.getLogger(GwPublicAccountServiceImpl.class);

	@Autowired
	private AccountAmountService accountAmountService;

	private String signKey = "7543cb4bf2f340278363adadea91c25d";

	private String BIZ = "公户余额查询";

	/**公户余额查询检验参数**/
	@Override
	public CommonResponse checkParam(Map<String,String> map) {
		CommonResponse checkResp = new CommonResponse();
		try {
			//解析请求参数
			if (map==null||StringUtils.isBlank(map.get("publicAccountCode"))||StringUtils.isBlank(map.get("sign"))) {
				checkResp.setRespCode(ErrorCodeEnum.E1003.getCode());
				checkResp.setRespMsg("[publicAccountCode],[sign]请求参数值不能为空");
				logger.error("[publicAccountCode],[sign]请求参数值不能为空，即："+ (map!=null?JSON.toJSON(map):"为空"));
				return checkResp;
			}
			//校验商户请求参数签名
			TreeMap<String, String> m = new TreeMap<>();
			m.put("publicAccountCode",map.get("publicAccountCode"));
			if (!SignUtil.checkSign(m,map.get("sign"), signKey, BIZ)) {
				checkResp.setRespCode(ErrorCodeEnum.E1009.getCode());
				checkResp.setRespMsg(ErrorCodeEnum.E1009.getDesc());
				logger.info(BIZ+",publicAccountCode="+map.get("publicAccountCode")+"验签，验签结果失败!");
				return checkResp;
			}
			checkResp.setRespCode(ErrorCodeEnum.SUCCESS.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.SUCCESS.getDesc());
			checkResp.setData(map);
		} catch (Exception e) {
			logger.error("公户余额查询校验参数异常",e);
			checkResp.setRespCode(ErrorCodeEnum.E1012.getCode());
			checkResp.setRespMsg(ErrorCodeEnum.E1012.getDesc());
		}
		return checkResp;
	}


	/**公户余额查询查询接口*/
	@Override
	public Map query(Map<String,String> params) {
		Map<String,String> resultMap = new HashMap<>();
		try {
			logger.info("公户余额查询，参数值params："+JSON.toJSONString(params));
			AccountAmount accountAmount = new AccountAmount();
			accountAmount.setPublicAccountCode(params.get("publicAccountCode"));//公户编号
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageNo(1);
			pageInfo.setPageSize(1);
			accountAmount.setPageInfo(pageInfo);
			List<AccountAmount> accountAmounts = accountAmountService.list(accountAmount);
			accountAmount = null;
			if(accountAmounts!=null&&accountAmounts.size()>0){
				accountAmount = accountAmounts.get(0);
			}
			logger.info("公户余额查询，返回值accountAmount：" + JSON.toJSONString(accountAmount));

			resultMap.put("publicAccountCode",params.get("publicAccountCode"));
			if (accountAmount==null) {
				resultMap.put("code","1");
				resultMap.put("msg","无记录");
			}else{
				resultMap.put("code",ErrorCodeEnum.SUCCESS.getCode());
				resultMap.put("msg",ErrorCodeEnum.SUCCESS.getDesc());
				resultMap.put("balance",accountAmount.getBalance().toString());//TODO:余额元转分
			}
		} catch (Exception e) {
			resultMap.put("code","2");
			resultMap.put("msg","查询异常");
			logger.error("公户余额查询异常",e);
		}
		logger.info("公户余额查询，resultMap="+JSON.toJSONString(resultMap));
		return resultMap;
	}

}
