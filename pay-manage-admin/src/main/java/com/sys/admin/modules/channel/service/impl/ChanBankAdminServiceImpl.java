package com.sys.admin.modules.channel.service.impl;

import com.sys.admin.common.service.BaseService;
import com.sys.admin.modules.channel.bo.ChanBankFormInfo;
import com.sys.admin.modules.channel.service.ChanBankAdminService;
import com.sys.core.service.ChanPaytypeBankService;
import com.sys.core.service.ChannelService;
import com.sys.core.service.ConfigSysService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.dao.dmo.ChanPaytypeBank;
import com.sys.core.dao.dmo.ChanPaytypeBankSearch;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.RandomNumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ChanMchtAdminServiceImpl
 * @Description: 商户支付通道service
 * @author: ALI
 * @date: 2017年8月29日
 */
@Transactional
@Service
public class ChanBankAdminServiceImpl extends BaseService implements ChanBankAdminService {

	@Autowired
	ChanPaytypeBankService chanPaytypeBankService;

	@Autowired
	ChannelService channelService;

	@Autowired
	MerchantService merchantService;

	@Autowired
	ConfigSysService configSysService;

	@Autowired
	PlatFeerateService platFeerateService;

	@Override
	public List<ChanBankFormInfo> search(ChanBankFormInfo chanBankFormInfo) {
		ChanPaytypeBankSearch chanBank = new ChanPaytypeBankSearch();
		BeanUtils.copyProperties(chanBankFormInfo, chanBank);
		List<ChanPaytypeBank> chanMchtPaytypes = chanPaytypeBankService.search(chanBank);

		List<ChanBankFormInfo> result = new ArrayList<>();
		ChanBankFormInfo chanBankFormInfoTemp;

		for (ChanPaytypeBank mchtPaytype : chanMchtPaytypes) {

			chanBankFormInfoTemp = new ChanBankFormInfo();
			BeanUtils.copyProperties(mchtPaytype, chanBankFormInfoTemp);

			result.add(chanBankFormInfoTemp);
		}

		return result;
	}


	@Override
	public int addChanPaytypeBank(ChanBankFormInfo chanBankFormInfo) throws Exception {
		ChanPaytypeBank chanBank = new ChanPaytypeBank();
		BeanUtils.copyProperties(chanBankFormInfo, chanBank);
		chanBank.setCreateDate(new Date());
		chanBank.setUpdateDate(new Date());
		//chanMchtID，“CM”+yyyyMMdd+四位 随机数
		String chanMchtID = "CB"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
		chanBank.setId(chanMchtID);

		return chanPaytypeBankService.create(chanBank);
	}

	@Override
	public int updateChanPaytypeBank(ChanBankFormInfo chanBankFormInfo) throws Exception {
		ChanPaytypeBank chanBank = new ChanPaytypeBank();
		BeanUtils.copyProperties(chanBankFormInfo, chanBank);
		chanBank.setUpdateDate(new Date());

		return chanPaytypeBankService.saveByKey(chanBank);
	}

	@Override
	public int deleteChanPaytypeBank(ChanBankFormInfo chanBankFormInfo) throws Exception {
		return chanPaytypeBankService.deleteByKey(chanBankFormInfo.getId());
	}

	@Override
	public ChanBankFormInfo getChanPaytypeBankById(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		ChanBankFormInfo chanBankFormInfo = new ChanBankFormInfo();
		ChanPaytypeBank chanBank = chanPaytypeBankService.queryByKey(id);
		BeanUtils.copyProperties(chanBank, chanBankFormInfo);

		return chanBankFormInfo;
	}

	@Override
	public int chanBankCount(ChanBankFormInfo chanBankFormInfo) {
		ChanPaytypeBankSearch chanBank = new ChanPaytypeBankSearch();
		BeanUtils.copyProperties(chanBankFormInfo, chanBank);

		return chanPaytypeBankService.chanBankCount(chanBank);
	}
}
