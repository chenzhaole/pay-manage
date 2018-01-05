package com.sys.admin.modules.platform.service;

import com.sys.admin.modules.platform.bo.MchtChanFormInfo;

import java.util.List;

/**
 * 商户通道
 * @author ALI
 * at 2017/9/8 14:37
 */
public interface MchtChanAdminService {

	/**
	 *
	 * @Title: getMchtList
	 * @Description: 获取商户通道列表页list
	 * @param productFormInfo
	 * @return
	 * @return: List<MchtProduct>
	 */
	List<MchtChanFormInfo> getMchtList(MchtChanFormInfo productFormInfo);

	/**
	 *
	 * @Title: getChanByMcht
	 * @Description: 获取商户通道的list
	 * @param productFormInfo
	 * @return
	 * @return: MchtProductFormInfo
	 */
	MchtChanFormInfo getChanByMcht(MchtChanFormInfo productFormInfo);

	/**
	 *
	 * @Title: updateStatus
	 * @Description: 修改商户通道状态
	 * @param productFormInfo
	 * @return
	 * @return: MchtProductFormInfo
	 */
	int updateStatus(MchtChanFormInfo productFormInfo);

	/**
	 *
	 * @Title: updateStatus
	 * @Description: 刷新商户通道
	 * @return
	 * @return: MchtProductFormInfo
	 */
	int refresh(List<String> mchtId);

	int mchtChanCount(MchtChanFormInfo mchtChanFormInfo);
}
