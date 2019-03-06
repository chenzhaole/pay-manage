package com.sys.admin.modules.channel.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.admin.common.service.BaseService;
import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.util.Collections3;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.RandomNumberUtil;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ChanMchtAdminServiceImpl
 * @Description: 商户支付通道service
 * @author: ALI
 * @date: 2017年8月29日
 */
@Transactional
@Service
public class ChanMchtAdminServiceImpl extends BaseService implements ChanMchtAdminService {

	@Autowired
	ChanMchtPaytypeService chanMchtPaytypeService;

	@Autowired
	ChannelService channelService;

	@Autowired
	MerchantService merchantService;

//	@Autowired
//	ConfigSysService configSysService;

	@Autowired
	PlatFeerateService platFeerateService;

	@Autowired
	private ChanMchtPaytypeAccConfigService chanMchtPaytypeAccConfigService;
	@Autowired
	private ChanLimitService chanLimitService;

	@Override
	public List<ChanMchtFormInfo> getChannelList(ChanMchtFormInfo chanMchtFormInfo) {
		ChanMchtPaytypeSearch chanMchtPaytype = new ChanMchtPaytypeSearch();
		BeanUtils.copyProperties(chanMchtFormInfo, chanMchtPaytype);

		List<ChanMchtPaytype> chanMchtPaytypes = chanMchtPaytypeService.search(chanMchtPaytype);

		List<ChanMchtFormInfo> result = new ArrayList<>();
		ChanMchtFormInfo chanMchtFormInfoTemp;

		//查询商户列表
		List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
		//上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
		//通道支付方式待结算金额
		List<ChanMchtPaytypeSettleAmount> chanMchtPaytypeSettleAmountList =chanLimitService.list(new ChanMchtPaytypeSettleAmount());
		//所有支付方式
		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtList, "id", "name");
		Map<String, String> mchtCodeMap = Collections3.extractToMap(mchtList, "id", "mchtCode");
		Map<String,BigDecimal> settleAmountMap =Collections3.extractToMap(chanMchtPaytypeSettleAmountList==null?new ArrayList(1):chanMchtPaytypeSettleAmountList,"code","amount");

		for (ChanMchtPaytype mchtPaytype : chanMchtPaytypes) {

			chanMchtFormInfoTemp = new ChanMchtFormInfo();
			BeanUtils.copyProperties(mchtPaytype, chanMchtFormInfoTemp);

			//通道名称
			chanMchtFormInfoTemp.setChanName(channelMap.get(chanMchtFormInfoTemp.getChanId()));

			//商户名称
			chanMchtFormInfoTemp.setMchtName(mchtMap.get(mchtPaytype.getMchtId()));
			chanMchtFormInfoTemp.setMchtCode(mchtCodeMap.get(mchtPaytype.getMchtId()));
			chanMchtFormInfoTemp.setLimitAmount(settleAmountMap.get(mchtPaytype.getId()));

//			//支付类型
//			if (StringUtils.isNotBlank(mchtPaytype.getPayType())) {
//				PaymentTypeInfo paymentType = configSysService.findPaymentType(mchtPaytype.getPayType());
//				if (paymentType != null) {
//					chanMchtFormInfoTemp.setPayType(paymentType.getPaymentName());
//				}
//			}

			//查找最新费率
			PlatFeerate platFeerate = platFeerateService.getLastFee(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getCode(), mchtPaytype.getId());
			chanMchtFormInfoTemp.setFee(platFeerate);

			result.add(chanMchtFormInfoTemp);
		}

		return result;
	}

	@Override
	public List<ChanMchtFormInfo> getChannelListSimple(ChanMchtFormInfo chanMchtFormInfo) {
		ChanMchtPaytype chanMchtPaytype = new ChanMchtPaytype();
		BeanUtils.copyProperties(chanMchtFormInfo, chanMchtPaytype);

		List<ChanMchtPaytype> chanMchtPaytypes = chanMchtPaytypeService.list(chanMchtPaytype);
				
		List<ChanMchtFormInfo> result = new ArrayList<>();
		ChanMchtFormInfo chanMchtFormInfoTemp;
		for (ChanMchtPaytype mchtPaytype : chanMchtPaytypes) {
			chanMchtFormInfoTemp = new ChanMchtFormInfo();
			BeanUtils.copyProperties(mchtPaytype, chanMchtFormInfoTemp);
			result.add(chanMchtFormInfoTemp);
		}

		return result;
	}

	/**
	 * 签了服务商合同的 商户通道支付方式
	 * @param chanMchtFormInfo
	 * @return
	 */
	@Override
	public List<ChanMchtFormInfo> getAllChannel(ChanMchtFormInfo chanMchtFormInfo) {

		List<ChanMchtPaytype> chanMchtPaytypes = chanMchtPaytypeService.list(new ChanMchtPaytype());
		List<ChanMchtFormInfo> result = new ArrayList<>();
		ChanMchtFormInfo chanMchtFormInfoTemp;


		for (ChanMchtPaytype mchtPaytype : chanMchtPaytypes) {
			if (!"2".equals(mchtPaytype.getContractType())) {
				continue; //只选取服务商合同的
			}
			if (mchtPaytype.getId().equals(chanMchtFormInfo.getId())) {
				continue; //只选取服务商合同的排除自身
			}

			chanMchtFormInfoTemp = new ChanMchtFormInfo();
			BeanUtils.copyProperties(mchtPaytype, chanMchtFormInfoTemp);
			result.add(chanMchtFormInfoTemp);
		}

		return result;
	}

	@Override
	public int addChanMchtPaytype(ChanMchtFormInfo chanMchtFormInfo) throws Exception {
		ChanMchtPaytype chanMchtPaytype = new ChanMchtPaytype();
		BeanUtils.copyProperties(chanMchtFormInfo, chanMchtPaytype);
		chanMchtPaytype.setCreateDate(new Date());
		chanMchtPaytype.setUpdateDate(new Date());
		//chanMchtID，“CM”+yyyyMMdd+四位 随机数
		String chanMchtID = "CM"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
		chanMchtPaytype.setId(chanMchtID);

		PlatFeerate platFeerate = new PlatFeerate();
		chanMchtFormInfo.getFee(platFeerate);
		//系统生成feeID，“F”+yyyyMMdd+四位随机数
		String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
		platFeerate.setId(feeID);
		platFeerate.setBizName(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getdesc());
		platFeerate.setBizType(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getCode());
		platFeerate.setBizRefId(chanMchtPaytype.getId());
		platFeerate.setCreateTime(new Date());
		platFeerate.setStatus(chanMchtFormInfo.getFeeStatus());

		/** 添加 通道商户支付方式账务类型 xq.w **/
		ChanMchtPaytypeAccConfig accConfig = new ChanMchtPaytypeAccConfig();
		accConfig.setChanMchtPaytypeId(chanMchtPaytype.getId());
		accConfig.setTradeType(chanMchtFormInfo.getAccType());
		chanMchtPaytypeAccConfigService.insertChanMchtPaytypeAccConfig(accConfig);

		return chanMchtPaytypeService.create(chanMchtPaytype,platFeerate);
	}

	@Override
	public int updateChanMchtPaytype(ChanMchtFormInfo chanMchtFormInfo) throws Exception {
		ChanMchtPaytype chanMchtPaytype = new ChanMchtPaytype();
		BeanUtils.copyProperties(chanMchtFormInfo, chanMchtPaytype);
		chanMchtPaytype.setUpdateDate(new Date());
		logger.info("修改后的chanMchtPaytype为"+ JSON.toJSON(chanMchtPaytype));
		PlatFeerate platFeerate = new PlatFeerate();
		chanMchtFormInfo.getFee(platFeerate);
		//系统生成feeID，“F”+yyyyMMdd+四位随机数
		String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
		platFeerate.setId(feeID);
		platFeerate.setBizName(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getdesc());
		platFeerate.setBizType(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getCode());
		platFeerate.setBizRefId(chanMchtPaytype.getId());
		platFeerate.setCreateTime(new Date());
		platFeerate.setStatus(chanMchtFormInfo.getFeeStatus());

		/** 更新 通道商户支付方式账务类型 xq.w **/
		ChanMchtPaytypeAccConfig accConfig = new ChanMchtPaytypeAccConfig();
		accConfig.setChanMchtPaytypeId(chanMchtPaytype.getId());
		accConfig.setTradeType(chanMchtFormInfo.getAccType());
		accConfig.setUpdatedTime(new Date());
		chanMchtPaytypeAccConfigService.updateChanMchtPaytypeAccConfig(accConfig);
		
		return chanMchtPaytypeService.saveByKey(chanMchtPaytype,platFeerate);
	}

	@Override
	public int deleteChanMchtPaytype(ChanMchtFormInfo chanMchtFormInfo) {
		return chanMchtPaytypeService.delete(chanMchtFormInfo.getId() + "");
	}

	@Override
	public ChanMchtFormInfo getChanMchtPaytypeById(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		ChanMchtFormInfo chanMchtFormInfo = new ChanMchtFormInfo();
		ChanMchtPaytype chanMchtPaytype = chanMchtPaytypeService.queryByKey(id);
		if(chanMchtPaytype == null){
			return null;
		}
		BeanUtils.copyProperties(chanMchtPaytype, chanMchtFormInfo);

		if (StringUtils.isNotBlank(chanMchtPaytype.getTradeStartTime())){
			String[] startTime = chanMchtPaytype.getTradeStartTime().split(":");
			chanMchtFormInfo.setTradeStartTimeH(startTime[0]);
			if (startTime.length > 1){
				chanMchtFormInfo.setTradeStartTimeS(startTime[1]);
			}
		}

		if (StringUtils.isNotBlank(chanMchtPaytype.getTradeEndTime())){
			String[] endTime = chanMchtPaytype.getTradeEndTime().split(":");
			chanMchtFormInfo.setTradeEndTimeH(endTime[0]);
			if (endTime.length > 1){
				chanMchtFormInfo.setTradeEndTimeS(endTime[1]);
			}
		}else {
			chanMchtFormInfo.setTradeEndTimeH("23");
			chanMchtFormInfo.setTradeEndTimeS("59");
		}

		//商户名称
		if (StringUtils.isNotBlank(chanMchtPaytype.getMchtId())) {
			MchtInfo mchtInfo = merchantService.queryByKey(chanMchtPaytype.getMchtId());
			if (mchtInfo != null){
				chanMchtFormInfo.setMchtName(mchtInfo.getName());
				chanMchtFormInfo.setMchtCode(mchtInfo.getMchtCode());
			}
		}

		//查找最新费率
		PlatFeerate platFeerate = platFeerateService.getLastFee(FeeRateBizTypeEnum.CHAN_MCHT_PAYTYPE_BIZTYPE.getCode(), id);
		chanMchtFormInfo.setFee(platFeerate);

		//查询通道商户支付方式账务信息
		ChanMchtPaytypeAccConfig accConfig = chanMchtPaytypeAccConfigService.findAccConfigByChanMchtPaytypeId(id);
		if(accConfig!= null){
			chanMchtFormInfo.setAccType(accConfig.getTradeType());
		}

		return chanMchtFormInfo;
	}

	@Override
	public int chanMchtCount(ChanMchtFormInfo chanMchtFormInfo) {
		ChanMchtPaytypeSearch chanMchtPaytype = new ChanMchtPaytypeSearch();
		BeanUtils.copyProperties(chanMchtFormInfo, chanMchtPaytype);

		return chanMchtPaytypeService.cmpCount(chanMchtPaytype);
	}
}
