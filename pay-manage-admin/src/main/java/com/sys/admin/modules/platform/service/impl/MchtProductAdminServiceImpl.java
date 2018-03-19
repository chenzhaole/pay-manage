package com.sys.admin.modules.platform.service.impl;

import com.sys.admin.modules.platform.bo.MchtProductFormInfo;
import com.sys.admin.modules.platform.service.MchtProductAdminService;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.IdUtil;
import com.sys.common.util.RandomNumberUtil;

import com.sys.core.dao.dmo.*;
import com.sys.core.service.MchtProductService;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.service.ProductService;
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

		MchtProduct mchtProduct = new MchtProduct();
		BeanUtils.copyProperties(productFormInfo, mchtProduct);

		List<MchtProduct> mchtProducts = mchtProductService.list(mchtProduct);

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
