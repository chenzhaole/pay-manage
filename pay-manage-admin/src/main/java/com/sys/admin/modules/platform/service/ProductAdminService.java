package com.sys.admin.modules.platform.service;

import com.sys.admin.modules.platform.bo.ProductFormInfo;
import com.sys.admin.modules.platform.bo.ProductRelaFormInfo;
import com.sys.core.dao.dmo.PlatProductSearch;

import java.util.List;

/**
 * 支付产品后台接口
 * @author ALI
 * at 2017/9/4 18:19
 */
public interface ProductAdminService {
	/**
	 *
	 * @Title: getProductList
	 * @Description: 获取支付产品list
	 * @param productFormInfo
	 * @return
	 * @return: List<PlatProduct>
	 */
	List<ProductFormInfo> getProductList(ProductFormInfo productFormInfo);
	/**
	 *
	 * @Title: addPlatProduct
	 * @Description: 新增支付产品
	 * @param productFormInfo
	 * @return
	 * @return: String
	 */
	int addPlatProduct(ProductFormInfo productFormInfo) throws Exception;
	/**
	 *
	 * @Title: updatePlatProduct
	 * @Description: 更新支付产品
	 * @param productFormInfo
	 * @return
	 * @return: String
	 */
	int updatePlatProduct(ProductFormInfo productFormInfo) throws Exception;
	/**
	 *
	 * @Title: deletePlatProduct
	 * @Description: 删除支付产品
	 * @param productFormInfo
	 * @return
	 * @return: String
	 */
	int deletePlatProduct(ProductFormInfo productFormInfo);
	/**
	 *
	 * @Title: getPlatProductByid
	 * @Description: 根据id获取支付产品信息
	 * @param id
	 * @return
	 * @return: ProductFormInfo
	 */
	ProductFormInfo getPlatProductById(String id);
	/**
	 *
	 * @Title: getProductList
	 * @Description: 根据关系获取支付产品list
	 * @param productRelaFormInfo
	 * @return
	 * @return: List<ProductFormInfo>
	 */
	List<String> getProductIdByRela(ProductRelaFormInfo productRelaFormInfo);

	int productCount(ProductFormInfo productFormInfo);

	int searchCount(PlatProductSearch productSearch);
}
