package com.sys.gateway.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.service.config.PlatSDKService;
import com.sys.boss.dao.dmo.PlatSdkConfig;
import com.sys.common.util.DateUtils;
import com.sys.gateway.service.ConfigGwService;

/**
 * 
 * @Description:
 * 
 * @author: ChenZL
 * @time: 2017年8月23日
 */
@Service
public class ConfigGwServiceImpl implements ConfigGwService {

	@Autowired
	private PlatSDKService platSDKService;
	

	/**
	 * 根据version查询支付SDK配置信息
	 * 
	 * @param sdkConfig
	 * @return
	 */
	@Override
	public String querySdkConfig(String key) {
		
		PlatSdkConfig sdkConfig = platSDKService.queryByKey(key);
		if(sdkConfig == null){
			return null;
		}
		
		JSONObject respObject = new JSONObject();// ******

		String updateTime = DateUtils.formatDate(sdkConfig.getUpdateTime(),
				"yyyyMMddHHmmss");
		respObject.put("payConfigUpdateTime", updateTime);// ******

		JSONObject consumerConf = new JSONObject();
		consumerConf.put("consumerQQ", sdkConfig.getConsumerQq());
		consumerConf.put("consumerTel", sdkConfig.getConsumerTel());
		respObject.put("consumerConf", consumerConf);// ******

		JSONArray domainList = new JSONArray();
		if (StringUtils.isNotBlank(sdkConfig.getDomainType1())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl1())) {
			JSONObject domain1 = new JSONObject();
			domain1.put("type", sdkConfig.getDomainType1());
			domain1.put("url", sdkConfig.getDomainUrl1());
			domainList.add(domain1);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType2())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl2())) {
			JSONObject domain2 = new JSONObject();
			domain2.put("type", sdkConfig.getDomainType2());
			domain2.put("url", sdkConfig.getDomainUrl2());
			domainList.add(domain2);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType3())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl3())) {
			JSONObject domain3 = new JSONObject();
			domain3.put("type", sdkConfig.getDomainType3());
			domain3.put("url", sdkConfig.getDomainUrl3());
			domainList.add(domain3);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType4())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl4())) {
			JSONObject domain4 = new JSONObject();
			domain4.put("type", sdkConfig.getDomainType4());
			domain4.put("url", sdkConfig.getDomainUrl4());
			domainList.add(domain4);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType5())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl5())) {
			JSONObject domain5 = new JSONObject();
			domain5.put("type", sdkConfig.getDomainType5());
			domain5.put("url", sdkConfig.getDomainUrl5());
			domainList.add(domain5);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType6())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl6())) {
			JSONObject domain6 = new JSONObject();
			domain6.put("type", sdkConfig.getDomainType6());
			domain6.put("url", sdkConfig.getDomainUrl6());
			domainList.add(domain6);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType7())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl7())) {
			JSONObject domain7 = new JSONObject();
			domain7.put("type", sdkConfig.getDomainType7());
			domain7.put("url", sdkConfig.getDomainUrl7());
			domainList.add(domain7);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType8())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl8())) {
			JSONObject domain8 = new JSONObject();
			domain8.put("type", sdkConfig.getDomainType8());
			domain8.put("url", sdkConfig.getDomainUrl8());
			domainList.add(domain8);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType9())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl9())) {
			JSONObject domain9 = new JSONObject();
			domain9.put("type", sdkConfig.getDomainType9());
			domain9.put("url", sdkConfig.getDomainUrl9());
			domainList.add(domain9);
		}
		if (StringUtils.isNotBlank(sdkConfig.getDomainType10())
				&& StringUtils.isNotBlank(sdkConfig.getDomainUrl10())) {
			JSONObject domain10 = new JSONObject();
			domain10.put("type", sdkConfig.getDomainType10());
			domain10.put("url", sdkConfig.getDomainUrl10());
			domainList.add(domain10);
		}
		respObject.put("domainList", domainList);// *****

		JSONArray payConfigList = new JSONArray();
		JSONObject payConfigItem = new JSONObject();
		JSONArray paymentTypeList = new JSONArray();
		JSONObject paymentTypeItem = new JSONObject();
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType1())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue1())) {
			paymentTypeItem.put(sdkConfig.getPaymentType1(),
					sdkConfig.getPaymentValue1());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType2())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue2())) {
			paymentTypeItem.put(sdkConfig.getPaymentType2(),
					sdkConfig.getPaymentValue2());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType3())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue3())) {
			paymentTypeItem.put(sdkConfig.getPaymentType3(),
					sdkConfig.getPaymentValue3());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType4())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue4())) {
			paymentTypeItem.put(sdkConfig.getPaymentType4(),
					sdkConfig.getPaymentValue4());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType5())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue5())) {
			paymentTypeItem.put(sdkConfig.getPaymentType5(),
					sdkConfig.getPaymentValue5());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType6())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue6())) {
			paymentTypeItem.put(sdkConfig.getPaymentType6(),
					sdkConfig.getPaymentValue6());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType7())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue7())) {
			paymentTypeItem.put(sdkConfig.getPaymentType7(),
					sdkConfig.getPaymentValue7());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType8())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue8())) {
			paymentTypeItem.put(sdkConfig.getPaymentType8(),
					sdkConfig.getPaymentValue8());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType9())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue9())) {
			paymentTypeItem.put(sdkConfig.getPaymentType9(),
					sdkConfig.getPaymentValue9());
		}
		if (StringUtils.isNotBlank(sdkConfig.getPaymentType10())
				&& StringUtils.isNotBlank(sdkConfig.getPaymentValue10())) {
			paymentTypeItem.put(sdkConfig.getPaymentType10(),
					sdkConfig.getPaymentValue10());
		}
		paymentTypeList.add(paymentTypeItem);
		payConfigItem.put("paymentTypeList", paymentTypeList);
		payConfigItem.put("isShowPayResultPage",
				sdkConfig.getIsshowpayresultpage());
		payConfigItem.put("isShowPayPage", sdkConfig.getIsshowpaypage());
		payConfigList.add(payConfigItem);

		respObject.put("payConfigList", payConfigList);// *****

		return respObject.toJSONString();

	}

}
