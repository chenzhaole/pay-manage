package com.sys.admin.modules.platform.service.impl;

import com.sys.admin.modules.platform.bo.MchtChanFormInfo;
import com.sys.admin.modules.platform.service.MchtChanAdminService;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.Collections3;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtChan;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商户通道
 *
 * @author ALI
 * at 2017/9/8 14:59
 */
@Transactional
@Service
public class MchtChanAdminServiceImpl implements MchtChanAdminService {
	private static final Logger log = LoggerFactory.getLogger(MchtChanAdminServiceImpl.class);

	@Autowired
	MerchantService merchantService;

	@Autowired
	MchtChanService mchtChanService;

	@Autowired
	ChannelService channelService;

	@Autowired
	MchtProductService mchtProductService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRelaService productRelaService;

	@Autowired
	ChanMchtPaytypeService chanMchtPaytypeService;

	@Override
	public List<MchtChanFormInfo> getMchtList(MchtChanFormInfo productFormInfo) {

		//查找商户
		MchtInfo mchtSearch = new MchtInfo();
		List<MchtInfo> mchtResult = null;
		boolean mchtQuery = false;
		if (StringUtils.isNotBlank(productFormInfo.getMchtName()) ||
				StringUtils.isNotBlank(productFormInfo.getMchtCode())) {
			mchtQuery = true;
			mchtSearch.setMchtCode(productFormInfo.getMchtCode());
			mchtSearch.setName(productFormInfo.getMchtName());
			mchtResult = merchantService.list(mchtSearch);
			if (CollectionUtils.isEmpty(mchtResult)) {
				return null;
			}
		}

		MchtChan mchtProductKey = new MchtChan();
		BeanUtils.copyProperties(productFormInfo, mchtProductKey);
		List<MchtChan> mchtInfos = mchtChanService.list(new MchtChan());
		if (CollectionUtils.isEmpty(mchtInfos)) {
			return null;
		}

		List<MchtChanFormInfo> mchtChanFormInfos = new ArrayList<>();
		MchtChanFormInfo mchtChanFormInfo;
		List<MchtChan> mchtChans;
		MchtChan mchtChan;
		MchtInfo mchtInfo;
		List<String> mchtIds = new ArrayList<>();

		List<MchtInfo> mchts = merchantService.list(new MchtInfo());
		Map<String, MchtInfo> mchtMap = Collections3.extractToMap(mchts, "id");

		for (MchtChan mchtChanTemp : mchtInfos) {

			boolean hasMcht = false;
			if (mchtQuery && !CollectionUtils.isEmpty(mchtResult)) {
				for (MchtInfo info : mchtResult) {
					if (info.getId().equals(mchtChanTemp.getMchtId())) {
						hasMcht = true;
						break;
					}
				}
			}
			if (mchtQuery && !hasMcht) {
				continue;
			}

			if (mchtIds.contains(mchtChanTemp.getMchtId())) {
				continue;
			}
			mchtIds.add(mchtChanTemp.getMchtId());

			mchtInfo = mchtMap.get(mchtChanTemp.getMchtId());
			if (mchtInfo == null) {
				continue;
			}

			mchtChanFormInfo = new MchtChanFormInfo();
			mchtChanFormInfo.setMchtId(mchtInfo.getId());
			mchtChanFormInfo.setMchtName(mchtInfo.getName());
			mchtChanFormInfo.setMchtCode(mchtInfo.getMchtCode());
			mchtChanFormInfo.setMchtStatus(mchtInfo.getStatus());

			mchtChan = new MchtChan();
			mchtChan.setMchtId(mchtInfo.getId());
			mchtChans = mchtChanService.list(mchtChan);
			if (CollectionUtils.isEmpty(mchtChans)) {
				continue;
			}

			int disableCount = 0;
			String isValid = "";
			for (MchtChan chan : mchtChans) {
				isValid = chan.getIsValid()+"";
				if (!StatusEnum.VALID.getCode().equals(isValid)) {
					disableCount++;
				}
			}
			mchtChanFormInfo.setChanCount(mchtChans.size());
			mchtChanFormInfo.setDisableCount(disableCount);
			mchtChanFormInfos.add(mchtChanFormInfo);
		}

		return mchtChanFormInfos;
	}

	@Override
	public MchtChanFormInfo getChanByMcht(MchtChanFormInfo productFormInfo) {

		MchtChan mchtChanQuery = new MchtChan();
		mchtChanQuery.setMchtId(productFormInfo.getMchtId());
		List<MchtChan> mchtInfos = mchtChanService.list(mchtChanQuery);
		if (CollectionUtils.isEmpty(mchtInfos)) {
			return null;
		}

		List<MchtChanFormInfo.Channel> channels = new ArrayList<>();
		MchtChanFormInfo.Channel channel;

		MchtInfo mchtInfo = merchantService.queryByKey(mchtChanQuery.getMchtId());
		MchtChanFormInfo mchtChanFormInfo = new MchtChanFormInfo();
		mchtChanFormInfo.setMchtId(mchtInfo.getId());
		mchtChanFormInfo.setMchtName(mchtInfo.getName());
		mchtChanFormInfo.setMchtCode(mchtInfo.getMchtCode());
		mchtChanFormInfo.setMchtStatus(mchtInfo.getStatus());
		mchtChanFormInfo.setMchtDesc(mchtInfo.getDescription());

		ChanInfo chanInfo;
		for (MchtChan info : mchtInfos) {

			chanInfo = channelService.queryByKey(info.getChanId());
			if (chanInfo == null) {
				continue;
			}

			channel = mchtChanFormInfo.getChannel();
			channel.setChanId(info.getChanId());
			channel.setChanName(chanInfo.getName());
			channel.setIsValid(info.getIsValid());
			channel.setRate(info.getRate());
			channel.setSort(info.getSort());
			channels.add(channel);
		}

		mchtChanFormInfo.setChannels(channels);
		return mchtChanFormInfo;
	}

	@Override
	public int updateStatus(MchtChanFormInfo productFormInfo) {

		MchtChan mchtChan;
		int count = 0;
		int result;
		for (MchtChanFormInfo.Channel channel : productFormInfo.getChannels()) {
			mchtChan = new MchtChan();
			mchtChan.setMchtId(productFormInfo.getMchtId());
			mchtChan.setChanId(channel.getChanId());
			mchtChan.setCreateTime(new Date());
			mchtChan.setIsValid(channel.getIsValid());
			result = mchtChanService.saveByKey(mchtChan);
			if (result == 1) {
				count++;
			}
		}
		return count;
	}

	@Override
	public int refresh(List<String> mchtIds) {
		return mchtChanService.refresh(mchtIds);
	}

	@Override
	public int mchtChanCount(MchtChanFormInfo mchtChanFormInfo) {
		MchtChan mchtProductKey = new MchtChan();
		BeanUtils.copyProperties(mchtChanFormInfo, mchtProductKey);
		return mchtChanService.list(mchtProductKey).size();
	}
}
