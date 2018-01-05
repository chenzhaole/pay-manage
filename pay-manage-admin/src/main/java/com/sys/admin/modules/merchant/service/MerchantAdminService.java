package com.sys.admin.modules.merchant.service;

import com.sys.admin.modules.merchant.bo.MerchantForm;
import com.sys.core.dao.dmo.MchtInfo;

import java.util.List;

/**
 * @ClassName: MerchantAdminService
 * @Description: 商户信息service
 * @author: cheng_fei
 * @date: 2017年8月24日 下午3:45:28
 */
public interface MerchantAdminService {
	/**
	 * @param merchantForm
	 * @return
	 * @Title: addMerchantService
	 * @Description: 新增
	 * @return: String
	 */
	String addMerchantService(MerchantForm merchantForm);

	/**
	 * @param mchtInfo
	 * @return
	 * @Title: getMchtInfoList
	 * @Description: 商户list
	 * @return: List<MchtInfo>
	 */
	List<MerchantForm> getMchtInfoList(MchtInfo mchtInfo);

	/**
	 * @param merchantForm
	 * @return
	 * @Title: updateMerchantService
	 * @Description: 更新商户信息
	 * @return: String
	 */
	String updateMerchantService(MerchantForm merchantForm);

	/**
	 * @param id
	 * @return
	 * @Title: getMerchantById
	 * @Description: 根据id查询商户信息
	 * @return: MerchantForm
	 */
	MerchantForm getMerchantById(String id);

	/**
	 * @param id
	 * @return
	 * @Title: deleteMerchantById
	 * @Description: 根据id删除商户信息
	 */
	Integer deleteMerchantById(String id);

	int mchtCount(MchtInfo mchtInfo);
}
