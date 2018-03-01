package com.sys.admin.modules.platform.service.impl;

import com.sys.admin.modules.platform.bo.ProductFormInfo;
import com.sys.admin.modules.platform.bo.ProductRelaFormInfo;
import com.sys.admin.modules.platform.service.ProductAdminService;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.RandomNumberUtil;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.ChanMchtPaytypeService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.service.ProductRelaService;
import com.sys.core.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 支付产品后台接口
 *
 * @author ALI
 * at 2017/9/4 18:20
 */
@Transactional
@Service
public class ProductAdminServiceImpl implements ProductAdminService {
	private static final Logger log = LoggerFactory.getLogger(ProductAdminServiceImpl.class);

	@Autowired
	private ProductService productService;

	@Autowired
	private PlatFeerateService platFeerateService;

	@Autowired
	private ProductRelaService productRelaService;

	@Autowired
	private ChanMchtPaytypeService chanMchtPaytypeService;

	@Override
	public List<ProductFormInfo> getProductList(ProductFormInfo productFormInfo) {

		PlatProductSearch productInfo = new PlatProductSearch();
		BeanUtils.copyProperties(productFormInfo, productInfo);

		List<PlatProduct> platProducts = productService.search(productInfo);
		if (CollectionUtils.isEmpty(platProducts)) {
			return null;
		}
		List<ProductFormInfo> productFormInfos = new ArrayList<>();
		ProductFormInfo productFormInfoTemp;

		for (PlatProduct platProduct : platProducts) {
			productFormInfoTemp = new ProductFormInfo();
			BeanUtils.copyProperties(platProduct, productFormInfoTemp);

			PlatProductRela platProductRelaQuery = new PlatProductRela();
			platProductRelaQuery.setProductId(platProduct.getId());
			List<PlatProductRela> platProductRelas = productRelaService.list(platProductRelaQuery);
			if (CollectionUtils.isEmpty(platProductRelas)) {
				productFormInfos.add(productFormInfoTemp);
				continue;
			}

			List<ProductRelaFormInfo> productRelaFormInfos = new ArrayList<>();
			ProductRelaFormInfo productRelaFormInfo;
			for (PlatProductRela platProductRela : platProductRelas) {
				productRelaFormInfo = new ProductRelaFormInfo();
				BeanUtils.copyProperties(platProductRela, productRelaFormInfo);
				productRelaFormInfos.add(productRelaFormInfo);
			}

			productFormInfoTemp.setProductRelas(productRelaFormInfos);

			int invaildCount = 0;
			for (ProductRelaFormInfo relaFormInfo : productRelaFormInfos) {
				ChanMchtPaytype chanMchtPaytype = chanMchtPaytypeService.queryByKey(relaFormInfo.getChanMchtPaytypeId());
				if (chanMchtPaytype == null) {
					continue;
				}
				if (StatusEnum.INVALID.getCode().equals(chanMchtPaytype.getStatus())){
					invaildCount ++ ;
				}
			}

			productFormInfoTemp.setProductRelasSize(productRelaFormInfos.size());
			productFormInfoTemp.setDisableCount(invaildCount);
			productFormInfos.add(productFormInfoTemp);
		}

		return productFormInfos;
	}

	@Override
	public int addPlatProduct(ProductFormInfo productFormInfo)  throws Exception{
		PlatProduct productInfo = new PlatProduct();
		BeanUtils.copyProperties(productFormInfo, productInfo);
		productInfo.setCreateTime(new Date());
		productInfo.setUpdateTime(new Date());

		//保存最新费率
		PlatFeerate platFeerate = new PlatFeerate();
		productFormInfo.getFee(platFeerate);
		//系统生成feeID，“F”+yyyyMMdd+四位随机数
		String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
		platFeerate.setId(feeID);
		platFeerate.setBizName(FeeRateBizTypeEnum.PLAT_PRODUCT_BIZTYPE.getdesc());
		platFeerate.setBizType(FeeRateBizTypeEnum.PLAT_PRODUCT_BIZTYPE.getCode());
		platFeerate.setBizRefId(productInfo.getId());
		platFeerate.setStatus(productFormInfo.getFeeStatus());
		platFeerate.setCreateTime(new Date());

		//保存产品与通道商户支付方式对应关系
		List<ProductRelaFormInfo> productRelaFormInfos = productFormInfo.getProductRelas();
		if (CollectionUtils.isEmpty(productRelaFormInfos)) {
			return 0;
		}
		List<PlatProductRela> platProductRelaList = new ArrayList<>();
		PlatProductRela platProductRela;
		for (ProductRelaFormInfo productRelaFormInfo : productRelaFormInfos) {
			platProductRela = new PlatProductRela();
			BeanUtils.copyProperties(productRelaFormInfo, platProductRela);
			platProductRela.setIsValid(1);//默认生效状态
			platProductRela.setCreateTime(new Date());
			platProductRela.setUpdateTime(new Date());
			platProductRelaList.add(platProductRela);
		}
		
		return productService.create(productInfo,platFeerate,platProductRelaList);
	}

	@Override
	public int updatePlatProduct(ProductFormInfo productFormInfo)  throws Exception{
		PlatProduct productInfo = new PlatProduct();
		BeanUtils.copyProperties(productFormInfo, productInfo);
		productInfo.setUpdateTime(new Date());
		
		PlatFeerate platFeerate = new PlatFeerate();
		productFormInfo.getFee(platFeerate);
		//系统生成feeID，“F”+yyyyMMdd+四位随机数
		String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
		platFeerate.setId(feeID);
		platFeerate.setBizName(FeeRateBizTypeEnum.PLAT_PRODUCT_BIZTYPE.getdesc());
		platFeerate.setBizType(FeeRateBizTypeEnum.PLAT_PRODUCT_BIZTYPE.getCode());
		platFeerate.setBizRefId(productInfo.getId());
		platFeerate.setCreateTime(new Date());
		platFeerate.setStatus(productFormInfo.getFeeStatus());

		//产品与通道商户支付方式对应关系
		List<ProductRelaFormInfo> productRelaFormInfos = productFormInfo.getProductRelas();
		if (CollectionUtils.isEmpty(productRelaFormInfos)) {
			return 0;
		}
		PlatProductRela platProductRela;
		List<PlatProductRela> platProductRelaList = new ArrayList<>();
		for (ProductRelaFormInfo productRelaFormInfo : productRelaFormInfos) {
			platProductRela = new PlatProductRela();
			BeanUtils.copyProperties(productRelaFormInfo, platProductRela);
			platProductRela.setIsValid(1);//默认生效状态
			platProductRela.setUpdateTime(new Date());
			platProductRelaList.add(platProductRela);
		}

		return productService.saveByKey(productInfo,platFeerate,platProductRelaList);
	}

	@Override
	public int deletePlatProduct(ProductFormInfo productFormInfo) {
		PlatProductRela platProductRela = new PlatProductRela();
		platProductRela.setProductId(productFormInfo.getId());
		productRelaService.deleteByExample(platProductRela);
		int result = productService.deleteByKey(productFormInfo.getId());
		if (result == 1) {
			productRelaService.deleteByExample(platProductRela);
		}
		return result;
	}

	@Override
	public ProductFormInfo getPlatProductById(String id) {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		PlatProduct platProduct = productService.queryByKey(id);
		ProductFormInfo productFormInfoTemp = new ProductFormInfo();
		BeanUtils.copyProperties(platProduct, productFormInfoTemp);

		PlatProductRela platProductRelaQuery = new PlatProductRela();
		platProductRelaQuery.setProductId(platProduct.getId());
		List<PlatProductRela> platProductRelas = productRelaService.list(platProductRelaQuery);
		if (CollectionUtils.isEmpty(platProductRelas)) {
			return productFormInfoTemp;
		}

		List<ProductRelaFormInfo> productRelaFormInfos = new ArrayList<>();
		ProductRelaFormInfo productRelaFormInfo;
		for (PlatProductRela platProductRela : platProductRelas) {
			productRelaFormInfo = new ProductRelaFormInfo();
			BeanUtils.copyProperties(platProductRela, productRelaFormInfo);
			if (productRelaFormInfo.getRate() != null) {
				productRelaFormInfo.setRate(productRelaFormInfo.getRate().setScale(0, RoundingMode.HALF_UP));
			}
			productRelaFormInfos.add(productRelaFormInfo);
		}

		Collections.sort(productRelaFormInfos, new Comparator<ProductRelaFormInfo>() {
			@Override
			public int compare(ProductRelaFormInfo o1, ProductRelaFormInfo o2) {
				if (o1.getSort() > o2.getSort()) {
					return 1;
				}
				if (o1.getSort() < o2.getSort()) {
					return -1;
				}
				return 0;
			}
		});

		productFormInfoTemp.setProductRelas(productRelaFormInfos);

		//查找最新费率
		PlatFeerate platFeerate = platFeerateService.getLastFee(FeeRateBizTypeEnum.PLAT_PRODUCT_BIZTYPE.getCode(), platProduct.getId());
		productFormInfoTemp.setFee(platFeerate);

		return productFormInfoTemp;
	}

	@Override
	public List<String> getProductIdByRela(ProductRelaFormInfo productRelaFormInfo) {

		PlatProductRela platProductRela = new PlatProductRela();
		BeanUtils.copyProperties(productRelaFormInfo, platProductRela);
		List<PlatProductRela> platProductRelas = productRelaService.list(platProductRela);
		if (CollectionUtils.isEmpty(platProductRelas)) {
			return null;
		}

		List<String> productIds = new ArrayList<>();
		for (PlatProductRela productRela : platProductRelas) {
			productIds.add(productRela.getProductId());
		}

		return productIds;
	}

	@Override
	public int productCount(ProductFormInfo productFormInfo) {
		PlatProduct platProduct = new PlatProduct();
		BeanUtils.copyProperties(productFormInfo, platProduct);
		return productService.productCount(platProduct);
	}

	@Override
	public int searchCount(PlatProductSearch productSearch) {
		return productService.searchCount(productSearch);
	}

}
