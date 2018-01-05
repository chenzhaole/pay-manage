package com.sys.admin.modules.platform.service;

import com.sys.admin.modules.platform.bo.MchtProductFormInfo;
import com.sys.core.dao.dmo.MchtProduct;

import java.util.List;

/**
 * 商户产品
 * @author ALI
 * at 2017/9/7 10:37
 */
public interface MchtProductAdminService {

	/**
	 *
	 * @Title: getProductList
	 * @Description: 获取支付产品list
	 * @param productFormInfo
	 * @return
	 * @return: List<MchtProduct>
	 */
	List<MchtProductFormInfo> getProductList(MchtProductFormInfo productFormInfo);
	/**
	 *
	 * @Title: getProductList
	 * @Description: 获取支付产品list 根据商户id
	 * @param productFormInfo
	 * @return
	 * @return: List<MchtProduct>
	 */
	List<MchtProduct> getProductListByMchtId(MchtProductFormInfo productFormInfo);
	/**
	 *
	 * @Title: addMchtProduct
	 * @Description: 新增支付产品
	 * @param productFormInfo
	 * @return
	 * @return: String
	 */
	int addMchtProduct(MchtProductFormInfo productFormInfo) throws Exception;
	/**
	 *
	 * @Title: updateMchtProduct
	 * @Description: 更新支付产品
	 * @param productFormInfo
	 * @return
	 * @return: String
	 */
	int updateMchtProduct(MchtProductFormInfo productFormInfo) throws Exception;
	/**
	 *
	 * @Title: deleteMchtProduct
	 * @Description: 删除支付产品
	 * @param productFormInfo
	 * @return
	 * @return: String
	 */
	int deleteMchtProduct(MchtProductFormInfo productFormInfo);
	/**
	 *
	 * @Title: getMchtProductById
	 * @Description: 根据id获取支付产品信息
	 * @param productFormInfo
	 * @return
	 * @return: MchtProductFormInfo
	 */
	MchtProductFormInfo getMchtProductById(MchtProductFormInfo productFormInfo);

	int mchtProductCount(MchtProductFormInfo mchtProductFormInfo);
}
