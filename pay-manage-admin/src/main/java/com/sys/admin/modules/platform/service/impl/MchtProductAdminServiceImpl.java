package com.sys.admin.modules.platform.service.impl;

import com.sys.admin.modules.platform.bo.MchtProductFormInfo;
import com.sys.admin.modules.platform.service.MchtProductAdminService;
import com.sys.core.service.MchtProductService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.service.ProductService;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.MchtProduct;
import com.sys.core.dao.dmo.MchtProductKey;
import com.sys.core.dao.dmo.PlatFeerate;
import com.sys.core.dao.dmo.PlatProduct;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.IdUtil;
import com.sys.common.util.RandomNumberUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 商户产品
 *
 * @author ALI
 * at 2017/9/7 10:37
 */
//@Transactional(rollbackFor=Exception.class)
@Service
public class MchtProductAdminServiceImpl implements MchtProductAdminService {
	private static final Logger log = LoggerFactory.getLogger(MchtProductAdminServiceImpl.class);

	@Autowired
	private MchtProductService mchtProductService;

	@Autowired
	private PlatFeerateService platFeerateService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private ProductService productService;

	@Override
	public List<MchtProductFormInfo> getProductList(MchtProductFormInfo productFormInfo) {

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

		//查找产品
		PlatProduct platProductQuery = new PlatProduct();
		List<PlatProduct> platProductResults = null;
		boolean productQuery = false;
		if (StringUtils.isNotBlank(productFormInfo.getProductName()) ||
				StringUtils.isNotBlank(productFormInfo.getProductCode())) {
			productQuery = true;
			platProductQuery.setName(productFormInfo.getProductName());
			platProductQuery.setCode(productFormInfo.getProductCode());
			platProductResults = productService.list(platProductQuery);
			if (CollectionUtils.isEmpty(platProductResults)) {
				return null;
			}
		}

		//查找费率
		PlatFeerate platFeerateQuery = new PlatFeerate();
		boolean feeQuery = false;
		List<PlatFeerate> platFeerateResults = null;
		if (StringUtils.isNotBlank(productFormInfo.getSettleCycle()) ||
				StringUtils.isNotBlank(productFormInfo.getSettleMode()) ||
				StringUtils.isNotBlank(productFormInfo.getSettleType())) {
			if (	!"0".equals(productFormInfo.getSettleCycle()) ||
					!"0".equals(productFormInfo.getSettleMode()) ||
					!"0".equals(productFormInfo.getSettleType())) {
			feeQuery = true;
			platFeerateQuery.setBizType(FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getCode());
			platFeerateQuery.setSettleCycle(productFormInfo.getSettleCycle());
			platFeerateQuery.setSettleMode(productFormInfo.getSettleMode());
			platFeerateQuery.setSettleType(productFormInfo.getSettleType());
			platFeerateResults = platFeerateService.list(platFeerateQuery);
			if (CollectionUtils.isEmpty(platFeerateResults)) {
				return null;
			}
			}
		}


		MchtProduct mchtProduct = new MchtProduct();
		BeanUtils.copyProperties(productFormInfo, mchtProduct);

		List<MchtProduct> mchtProductsTemp;
		//根据商户查找商户产品
		List<MchtProduct> mchtProductsByMcht = new ArrayList<>();
		Set<String> keyByMcht = new HashSet<>();
		if (mchtQuery) {
			for (MchtInfo mchtInfo : mchtResult) {
				mchtProduct.setMchtId(mchtInfo.getId());
				mchtProductsTemp = mchtProductService.list(mchtProduct);
				if (!CollectionUtils.isEmpty(mchtProductsTemp)) {
					mchtProductsByMcht.addAll(mchtProductsTemp);
				}
			}
			if (mchtProductsByMcht.isEmpty()) {
				return null;
			}
			keyByMcht = getUnDuplicatedId(mchtProductsByMcht);
		}

		//根据产品查找商户产品
		List<MchtProduct> mchtProductsByProduct = new ArrayList<>();
		Set<String> keyByProduct = new HashSet<>();
		if (productQuery) {
			for (PlatProduct product : platProductResults) {
				mchtProduct.setProductId(product.getId());
				mchtProductsTemp = mchtProductService.list(mchtProduct);
				if (!CollectionUtils.isEmpty(mchtProductsTemp)) {
					mchtProductsByProduct.addAll(mchtProductsTemp);
				}
			}
			if (mchtProductsByProduct.isEmpty()) {
				return null;
			}
			keyByProduct = getUnDuplicatedId(mchtProductsByProduct);
		}

		//根据费率查找商户产品
		List<MchtProduct> mchtProductsByFee = new ArrayList<>();
		Set<String> keyByFee = new HashSet<>();
		MchtProduct mchtProductTemp;
		MchtProductKey mchtProductKey;
		if (feeQuery) {
			for (PlatFeerate fee : platFeerateResults) {
				if (!fee.getBizRefId().contains("&")) {
					continue;
				}
				String[] key = fee.getBizRefId().split("&");
				mchtProductKey = new MchtProduct();
				mchtProductKey.setMchtId(key[0]);
				mchtProductKey.setProductId(key[1]);
				mchtProductTemp = mchtProductService.queryByKey(mchtProductKey);
				if (mchtProductTemp != null) {
					mchtProductsByFee.add(mchtProductTemp);
				}
			}
			if (mchtProductsByFee.isEmpty()) {
				return null;
			}
			keyByFee = getUnDuplicatedId(mchtProductsByFee);
		}


		List<MchtProduct> mchtProducts = new ArrayList<>();
		List<String> mchtProductIDs = new ArrayList<>();

		if (mchtQuery) {
			mchtProductIDs.addAll(keyByMcht);
			if (productQuery) {
				mchtProductIDs.retainAll(keyByProduct);
			}
			if (feeQuery) {
				mchtProductIDs.retainAll(keyByFee);
			}
			for (MchtProduct product : mchtProductsByMcht) {
				for (String mchtProductID : mchtProductIDs) {
					String[] key = mchtProductID.split("&");
					if (product.getMchtId().equals(key[0]) &&
							product.getProductId().equals(key[1])) {
						mchtProducts.add(product);
					}
				}

			}

		} else if (productQuery) {
			mchtProductIDs.addAll(keyByProduct);
			if (feeQuery) {
				mchtProductIDs.retainAll(keyByFee);
			}
			for (MchtProduct product : mchtProductsByProduct) {
				for (String mchtProductID : mchtProductIDs) {
					String[] key = mchtProductID.split("&");
					if (product.getMchtId().equals(key[0]) &&
							product.getProductId().equals(key[1])) {
						mchtProducts.add(product);
					}
				}

			}
		} else if (feeQuery) {
			mchtProducts.addAll(mchtProductsByFee);
		} else {
			mchtProducts = mchtProductService.list(new MchtProduct());
		}

		if (CollectionUtils.isEmpty(mchtProducts)) {
			return null;
		}

		List<MchtProductFormInfo> mchtProductFormInfos = new ArrayList<>();
		MchtProductFormInfo mchtProductFormInfo;
		for (MchtProduct product : mchtProducts) {
			mchtProductFormInfo = new MchtProductFormInfo();
			BeanUtils.copyProperties(product, mchtProductFormInfo);

			//商户信息
			if (StringUtils.isNotBlank(product.getMchtId())) {
				MchtInfo mchtInfo = merchantService.queryByKey(product.getMchtId());
				if (mchtInfo != null) {
					mchtProductFormInfo.setMchtName(mchtInfo.getName());
					mchtProductFormInfo.setMchtCode(mchtInfo.getMchtCode());
				}

			}

			//产品信息
			if (StringUtils.isNotBlank(product.getProductId())) {
				PlatProduct platProduct = productService.queryByKey(product.getProductId());
				if (platProduct != null) {
					mchtProductFormInfo.setProductName(platProduct.getName());
					mchtProductFormInfo.setProductCode(platProduct.getCode());
				}

			}

			//查找最新费率
			PlatFeerate platFeerate = platFeerateService.getLastFee(FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getCode(),
					product.getMchtId() + "&" + product.getProductId());
			mchtProductFormInfo.setFee(platFeerate);

			mchtProductFormInfos.add(mchtProductFormInfo);
		}

		return mchtProductFormInfos;
	}

	@Override
	public List<MchtProduct> getProductListByMchtId(MchtProductFormInfo productFormInfo) {
		MchtProduct mchtProduct = new MchtProduct();
		mchtProduct.setMchtId(productFormInfo.getMchtId());
		return mchtProductService.list(mchtProduct);
	}

	@Override
	public int addMchtProduct(MchtProductFormInfo productFormInfo)  throws Exception{
		MchtProduct mchtProduct = new MchtProduct();
		BeanUtils.copyProperties(productFormInfo, mchtProduct);
		mchtProduct.setCreateTime(new Date());
		mchtProduct.setUpdateTime(new Date());

        PlatFeerate platFeerate = new PlatFeerate();
        productFormInfo.getFee(platFeerate);
		//系统生成feeID，“F”+yyyyMMdd+四位随机数
//		String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
		platFeerate.setId(IdUtil.getUUID());
        platFeerate.setBizName(FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getdesc());
        platFeerate.setBizType(FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getCode());
        platFeerate.setBizRefId(mchtProduct.getMchtId() + "&" + mchtProduct.getProductId());
        platFeerate.setCreateTime(new Date());
        platFeerate.setStatus(productFormInfo.getFeeStatus());
		
		return mchtProductService.create(mchtProduct,platFeerate);
	}

	@Override
	public int updateMchtProduct(MchtProductFormInfo productFormInfo)  throws Exception{
		MchtProduct mchtProduct = new MchtProduct();
		BeanUtils.copyProperties(productFormInfo, mchtProduct);
        mchtProduct.setUpdateTime(new Date());

		PlatFeerate platFeerate = new PlatFeerate();
		productFormInfo.getFee(platFeerate);
		//系统生成feeID，“F”+yyyyMMdd+四位随机数
		String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
		platFeerate.setId(feeID);
		platFeerate.setBizName(FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getdesc());
		platFeerate.setBizType(FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getCode());
		platFeerate.setBizRefId(mchtProduct.getMchtId() + "&" + mchtProduct.getProductId());
		platFeerate.setStatus(productFormInfo.getFeeStatus());
		platFeerate.setCreateTime(new Date());
		
		return mchtProductService.saveByKey(mchtProduct,platFeerate);
	}

	@Override
	public int deleteMchtProduct(MchtProductFormInfo productFormInfo) {
		MchtProduct mchtProductKey = new MchtProduct();
		BeanUtils.copyProperties(productFormInfo, mchtProductKey);
		return mchtProductService.delete(mchtProductKey);
	}

	@Override
	public MchtProductFormInfo getMchtProductById(MchtProductFormInfo productFormInfo) {
		MchtProductKey mchtProductKey = new MchtProduct();
		BeanUtils.copyProperties(productFormInfo, mchtProductKey);
		MchtProduct mchtProduct = mchtProductService.queryByKey(mchtProductKey);
		if (mchtProduct == null) {
			return null;
		}
		BeanUtils.copyProperties(mchtProduct, productFormInfo);

		//商户信息
		if (StringUtils.isNotBlank(productFormInfo.getMchtId())) {
			MchtInfo mchtInfo = merchantService.queryByKey(productFormInfo.getMchtId());
			if (mchtInfo != null) {
				productFormInfo.setMchtName(mchtInfo.getName());
				productFormInfo.setMchtCode(mchtInfo.getMchtCode());
			}

		}

		//产品信息
		if (StringUtils.isNotBlank(productFormInfo.getProductId())) {
			PlatProduct platProduct = productService.queryByKey(productFormInfo.getProductId());
			if (platProduct != null) {
				productFormInfo.setProductName(platProduct.getName());
				productFormInfo.setProductCode(platProduct.getCode());
			}

		}

		//查找最新费率
		PlatFeerate platFeerate = platFeerateService.getLastFee(FeeRateBizTypeEnum.MCHT_PRODUCT_BIZTYPE.getCode(),
				mchtProduct.getMchtId() + "&" + mchtProduct.getProductId());
		productFormInfo.setFee(platFeerate);
		return productFormInfo;
	}

	@Override
	public int mchtProductCount(MchtProductFormInfo mchtProductFormInfo) {
		MchtProduct mchtProductKey = new MchtProduct();
		BeanUtils.copyProperties(mchtProductFormInfo, mchtProductKey);
		return mchtProductService.productCount(mchtProductKey);
	}

	/**
	 * 获取去重的ID
	 *
	 * @param mchtProductFormInfos
	 * @return
	 */
	private Set<String> getUnDuplicatedId(List<MchtProduct> mchtProductFormInfos) {
		if (CollectionUtils.isEmpty(mchtProductFormInfos)) {
			return null;
		}
		Set<String> result = new HashSet<>();
		for (MchtProduct mchtProductFormInfo : mchtProductFormInfos) {
			result.add(mchtProductFormInfo.getMchtId() + "&" + mchtProductFormInfo.getProductId());
		}
		return result;
	}
}
