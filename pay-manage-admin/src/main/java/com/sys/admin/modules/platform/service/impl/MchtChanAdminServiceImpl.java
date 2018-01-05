package com.sys.admin.modules.platform.service.impl;

import com.sys.admin.modules.platform.bo.MchtChanFormInfo;
import com.sys.admin.modules.platform.service.MchtChanAdminService;
import com.sys.core.service.ChanMchtPaytypeService;
import com.sys.core.service.ChannelService;
import com.sys.core.service.MchtChanService;
import com.sys.core.service.MchtProductService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.ProductRelaService;
import com.sys.core.service.ProductService;
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.ChanMchtPaytype;
import com.sys.core.dao.dmo.MchtChan;
import com.sys.core.dao.dmo.MchtChanKey;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.MchtProduct;
import com.sys.core.dao.dmo.PlatProductRela;
import com.sys.common.enums.StatusEnum;
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

			mchtInfo = merchantService.queryByKey(mchtChanTemp.getMchtId());
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
			for (MchtChan chan : mchtChans) {
				if (StatusEnum.INVALID.getCode().equals(chan.getIsValid())) {
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
		try {

			if (CollectionUtils.isEmpty(mchtIds)) {
				return 0;
			}

			Set<String> chanIds = new HashSet<>();
			Set<String> chanIdsOld = new HashSet<>();

			//根据商户查找商户产品
			List<MchtProduct> mchtProducts;
			List<MchtChan> mchtInfosOld;
//			List<MchtChan> mchtInfosAdd;
//			List<MchtChan> mchtInfosDelete;
			MchtChan mchtChanTemp;
			MchtChanKey mchtChanKey;
			MchtChanKey mchtChanKeyDel;
			MchtChan mchtChanQuery;
			for (String mchtId : mchtIds) {
				MchtProduct mchtProductQuery = new MchtProduct();
				mchtProductQuery.setMchtId(mchtId);
				mchtProducts = mchtProductService.list(mchtProductQuery);
				if (CollectionUtils.isEmpty(mchtProducts)) {
					//删除所有该商户的商户通道
					mchtChanService.deleteByMchtId(mchtId);
					continue;
				}

				//查找支付产品
				PlatProductRela platProductRela;
				List<PlatProductRela> platProductRelas;
				for (MchtProduct mchtProduct : mchtProducts) {

					platProductRela = new PlatProductRela();
					platProductRela.setProductId(mchtProduct.getProductId());
					platProductRelas = productRelaService.list(platProductRela);
					if (CollectionUtils.isEmpty(platProductRelas)) {
						continue;
					}

					//查找产品的通道
					ChanMchtPaytype chanMchtPaytype;
					for (PlatProductRela productRela : platProductRelas) {
						chanMchtPaytype = chanMchtPaytypeService.queryByKey(productRela.getChanMchtPaytypeId());
						if (chanMchtPaytype == null) {
							continue;
						}
						chanIds.add(chanMchtPaytype.getChanId());
					}
				}

				//对比现有商户通道，多删少增
				mchtChanQuery = new MchtChan();
				mchtChanQuery.setMchtId(mchtId);
				mchtInfosOld = mchtChanService.list(mchtChanQuery);

				if (CollectionUtils.isEmpty(mchtInfosOld)) {
					if (CollectionUtils.isEmpty(chanIds)) {
						return 0;
					}

					for (String chanId : chanIds) {
						mchtChanKey = new MchtChanKey();
						mchtChanKey.setMchtId(mchtId);
						mchtChanKey.setChanId(chanId);
						mchtChanTemp = mchtChanService.queryByKey(mchtChanKey);
						if (mchtChanTemp != null) {
							continue;
						}
						mchtChanTemp = new MchtChan();
						mchtChanTemp.setMchtId(mchtId);
						mchtChanTemp.setChanId(chanId);
						mchtChanTemp.setIsValid(Integer.parseInt(StatusEnum.VALID.getCode()));
						mchtChanService.create(mchtChanTemp);
					}

				} else if (CollectionUtils.isEmpty(chanIds)) {
					//删除所有该商户的商户通道
					mchtChanService.deleteByMchtId(mchtId);
				} else {
					for (MchtChan mchtChan : mchtInfosOld) {
						chanIdsOld.add(mchtChan.getChanId());
					}
					Set<String> chanIdsTemp = new HashSet<>();
					chanIdsTemp.addAll(chanIds);
					chanIdsTemp.removeAll(chanIdsOld);

					for (String chanId : chanIdsTemp) {
						mchtChanKey = new MchtChanKey();
						mchtChanKey.setMchtId(mchtId);
						mchtChanKey.setChanId(chanId);
						mchtChanTemp = mchtChanService.queryByKey(mchtChanKey);
						if (mchtChanTemp != null) {
							continue;
						}
						mchtChanTemp = new MchtChan();
						mchtChanTemp.setMchtId(mchtId);
						mchtChanTemp.setChanId(chanId);
						mchtChanTemp.setIsValid(Integer.parseInt(StatusEnum.VALID.getCode()));
						mchtChanService.create(mchtChanTemp);
					}

					chanIdsOld.removeAll(chanIds);
					for (String chanId : chanIdsOld) {
						mchtChanKeyDel = new MchtChan();
						mchtChanKeyDel.setMchtId(mchtId);
						mchtChanKeyDel.setChanId(chanId);
						mchtChanService.delete(mchtChanKeyDel);
					}
				}

			}
		} catch (Exception e) {
			log.error("刷新失败", e);
			return 0;
		}
		return 1;
	}

	@Override
	public int mchtChanCount(MchtChanFormInfo mchtChanFormInfo) {
		MchtChan mchtProductKey = new MchtChan();
		BeanUtils.copyProperties(mchtChanFormInfo, mchtProductKey);
		return mchtChanService.list(mchtProductKey).size();
	}
}
