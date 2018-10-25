package com.sys.admin.modules.merchant.service.impl;

import com.sys.admin.common.service.BaseService;
import com.sys.admin.modules.merchant.bo.MerchantForm;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.MchtRechargeConfig;
import com.sys.core.service.MchtRechargeConfigService;
import com.sys.core.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 
 * @ClassName: MerchantAdminService 
 * @Description: 商户service
 * @author: cheng_fei
 * @date: 2017年8月24日 下午1:41:44
 */
@Service
public class MerchantAdminServiceImpl extends BaseService implements MerchantAdminService{
   
	@Autowired
	MerchantService merchantService;

	@Autowired
	MchtRechargeConfigService rechargeConfigService;

	@Override
	public String addMerchantService(MerchantForm merchantForm){
		if(merchantForm != null){
			MchtInfo mchtInfo = new MchtInfo();
			BeanUtils.copyProperties(merchantForm, mchtInfo);
			/*String id = new SimpleDateFormat("yyyyMMddHHmmssSSS")
					.format(new Date());*/
//	20171102日修改：这里之前取的是当前时间戳，但是为了更好的处理业务，这里约定将 主键和商户code的值存为一样
			mchtInfo.setId(merchantForm.getMchtCode());
			mchtInfo.setCreateDate(new Date());
			mchtInfo.setUpdateDate(new Date());
			mchtInfo.setMchtKey(IdUtil.getUUID());
			try{
				merchantService.create(mchtInfo);

				merchantForm.setId(merchantForm.getMchtCode());
				insertMchtRechargeConfig(merchantForm);
				return "success";
			}catch(Exception e){
				return "fail";
			}
		}
		return "fail";
	}
	@Override
	public List<MerchantForm> getMchtInfoList(MchtInfo mchtInfo ){
		List<MchtInfo> mchtInfos = merchantService.list(mchtInfo);
		List<MerchantForm> merchantForms = new ArrayList<>();
		for (MchtInfo info : mchtInfos) {
			MerchantForm merchantForm = new MerchantForm();
			BeanUtils.copyProperties(info, merchantForm);
			merchantForm.setOperatorName(UserUtils.getUserName(info.getOperatorId()));
			merchantForms.add(merchantForm);
		}
		return merchantForms;
	}
	@Override
	public String updateMerchantService(MerchantForm merchantForm) {

		if(merchantForm != null){
			MchtInfo mchtInfo = new MchtInfo();
			BeanUtils.copyProperties(merchantForm, mchtInfo);
			mchtInfo.setUpdateDate(new Date());
			try{
				merchantService.saveByKey(mchtInfo);
				updateMchtRechargeConfig(merchantForm);
				return "success";
			}catch(Exception e){
				return "fail";
			}
		}
		return "fail";
	
	}
	@Override
	public MerchantForm getMerchantById(String id) {
		MchtInfo mchtInfo = merchantService.queryByKey(id);
		if(mchtInfo != null){
			MerchantForm merchantForm = new MerchantForm();
			BeanUtils.copyProperties(mchtInfo, merchantForm);
			merchantForm = setRechargeConfig(merchantForm);
			return merchantForm;
		}
		return null;
	}

	@Override
	public Integer deleteMerchantById(String id) {
		return merchantService.deleteMerchantById(id);
	}

	@Override
	public int mchtCount(MchtInfo mchtInfo) {
		return merchantService.mchtCount(mchtInfo);
	}


	/**
	 * 添加商户充值配置信息
	 * 2018-09-26 10:31:02
	 * @param merchantForm
	 * @return
	 */
	public Integer insertMchtRechargeConfig(MerchantForm merchantForm){
		MchtRechargeConfig rechargeConfig = getRechargeConfig(merchantForm);
		return rechargeConfigService.insertMchtRechargeConfig(rechargeConfig);
	}


	/**
	 * 更新商户充值配置信息
	 * 2018-09-26 10:34:35
	 * @param merchantForm
	 * @return
	 */
	public Integer updateMchtRechargeConfig(MerchantForm merchantForm){

		MchtRechargeConfig dbRechargeConfig = rechargeConfigService.findByRechargeConfigMchtId(merchantForm.getId());
		if(dbRechargeConfig == null){
			return insertMchtRechargeConfig(merchantForm);
		}else{
			MchtRechargeConfig rechargeConfig = getRechargeConfig(merchantForm);
			return rechargeConfigService.updateMchtRechargeConfig(rechargeConfig);
		}
	}

	public MchtRechargeConfig getRechargeConfig(MerchantForm merchantForm){
		MchtRechargeConfig rechargeConfig = new MchtRechargeConfig();
		rechargeConfig.setMchtId(merchantForm.getId());
		rechargeConfig.setExemptReviewStartTime(merchantForm.getExemptReviewStartTime());
		rechargeConfig.setExemptReviewEndTime(merchantForm.getExemptReviewEndTime());
		if(merchantForm.getExemptReviewAmount()== null){
			rechargeConfig.setExemptReviewAmount(new BigDecimal(0));
		}else{
			rechargeConfig.setExemptReviewAmount(merchantForm.getExemptReviewAmount());
		}
		if(merchantForm.getTradeFeeAmount() == null){
			rechargeConfig.setTradeFeeAmount(new BigDecimal(0));
		}else{
			rechargeConfig.setTradeFeeAmount(merchantForm.getTradeFeeAmount());
		}
		rechargeConfig.setCompReceiptAcctName(merchantForm.getCompReceiptAcctName());
		rechargeConfig.setCompReceiptAcctNo(merchantForm.getCompReceiptAcctNo());
		rechargeConfig.setMchtRemittanceAmountSuffix(merchantForm.getMchtRemittanceAmountSuffix());
		return rechargeConfig;
	}


	public MerchantForm setRechargeConfig(MerchantForm merchantForm){
		MchtRechargeConfig rechargeConfig = rechargeConfigService.findByRechargeConfigMchtId(merchantForm.getMchtCode());
		if(rechargeConfig == null){
			return merchantForm;
		}
		merchantForm.setExemptReviewStartTime(rechargeConfig.getExemptReviewStartTime());
		merchantForm.setExemptReviewEndTime(rechargeConfig.getExemptReviewEndTime());
		merchantForm.setExemptReviewAmount(rechargeConfig.getExemptReviewAmount());
		merchantForm.setTradeFeeAmount(rechargeConfig.getTradeFeeAmount());
		merchantForm.setCompReceiptAcctName(rechargeConfig.getCompReceiptAcctName());
		merchantForm.setCompReceiptAcctNo(rechargeConfig.getCompReceiptAcctNo());
		merchantForm.setMchtRemittanceAmountSuffix(rechargeConfig.getMchtRemittanceAmountSuffix());
		return merchantForm;
	}
}
