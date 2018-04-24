package com.sys.admin.modules.platform.bo;

import com.sys.common.enums.StatusEnum;
import com.sys.common.util.DateUtils;
import com.sys.common.util.DateUtils2;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.PlatFeerate;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支付产品
 * @author ALI
 * at 2017/9/4 16:51
 */
public class ProductFormInfo {

	private String id;

	private String subId;

	private String code;

	private String name;

	private String chanMchtPaytypeId; //搜索用

	private String payType;

	private Integer routeType;

	private Integer isNameControlled;

	private String goodId;

	private String settleType;

	private String settleMode;

	private String settleCycle;

	private String feeType;

	private BigDecimal feeRate;

	private BigDecimal feeAmount;

	private BigDecimal settleLowestFee;

	private BigDecimal settleHighestFee;

	private String status;

	private String appId;

	private String appKey;

	private Long operatorId;

	private Date createTime;

	private Date updateTime;

	private String extend1;

	private String extend2;

	private String extend3;

	private String feeStatus;

	private String activeTime;

	private List<ProductRelaFormInfo>  productRelas;

	private Integer productRelasSize;

	private Integer disableCount;

	private List<SubProduct>  subProducts;

	private PageInfo pageInfo;

	private static final long serialVersionUID = 1L;

	public Integer getDisableCount() {
		return disableCount;
	}

	public void setDisableCount(Integer disableCount) {
		this.disableCount = disableCount;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public String getSubId() {
		return subId;
	}

	public void setSubId(String subId) {
		this.subId = subId;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Integer getRouteType() {
		return routeType;
	}

	public void setRouteType(Integer routeType) {
		this.routeType = routeType;
	}

	public Integer getIsNameControlled() {
		return isNameControlled;
	}

	public void setIsNameControlled(Integer isNameControlled) {
		this.isNameControlled = isNameControlled;
	}

	public String getGoodId() {
		return goodId;
	}

	public void setGoodId(String goodId) {
		this.goodId = goodId;
	}

	public String getSettleType() {
		return settleType;
	}

	public void setSettleType(String settleType) {
		this.settleType = settleType;
	}

	public String getSettleMode() {
		return settleMode;
	}

	public void setSettleMode(String settleMode) {
		this.settleMode = settleMode;
	}

	public String getSettleCycle() {
		return settleCycle;
	}

	public void setSettleCycle(String settleCycle) {
		this.settleCycle = settleCycle;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public BigDecimal getSettleLowestFee() {
		return settleLowestFee;
	}

	public void setSettleLowestFee(BigDecimal settleLowestFee) {
		this.settleLowestFee = settleLowestFee;
	}

	public BigDecimal getSettleHighestFee() {
		return settleHighestFee;
	}

	public void setSettleHighestFee(BigDecimal settleHighestFee) {
		this.settleHighestFee = settleHighestFee;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}

	public String getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(String activeTime) {
		this.activeTime = activeTime;
	}

	public String getFeeStatus() {
		return feeStatus;
	}

	public void setFeeStatus(String feeStatus) {
		this.feeStatus = feeStatus;
	}

	public List<ProductRelaFormInfo> getProductRelas() {
		return productRelas;
	}

	public void setProductRelas(List<ProductRelaFormInfo> productRelas) {
		this.productRelas = productRelas;
	}

	public Integer getProductRelasSize() {
		return productRelasSize;
	}

	public void setProductRelasSize(Integer productRelasSize) {
		this.productRelasSize = productRelasSize;
	}

	public String getChanMchtPaytypeId() {
		return chanMchtPaytypeId;
	}

	public void setChanMchtPaytypeId(String chanMchtPaytypeId) {
		this.chanMchtPaytypeId = chanMchtPaytypeId;
	}

	public List<SubProduct> getSubProducts() {
		return subProducts;
	}

	public void setSubProducts(List<SubProduct> subProducts) {
		this.subProducts = subProducts;
	}

	public ProductFormInfo() {
	}

	public ProductFormInfo(Map<String, String> paramMap) {
		this.id = paramMap.get("id");

		if (StringUtils.isBlank(id)) {
			String productId = DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS");
			this.id = productId;
		}

		this.routeType = Integer.parseInt(StringUtils.isNotBlank(paramMap.get("routeType")) ? paramMap.get("routeType") : "1");
		this.code = paramMap.get("code");
		this.chanMchtPaytypeId = paramMap.get("chanMchtPaytypeId");
		this.name = paramMap.get("productName");
		this.payType = paramMap.get("paymentType");
		this.isNameControlled = Integer.parseInt(StringUtils.isNotBlank(paramMap.get("isNameControlled")) ? paramMap.get("isNameControlled") : "0");
		this.goodId = paramMap.get("goodId");
		this.settleType = StringUtils.isNotBlank(paramMap.get("settleType")) ? paramMap.get("settleType") : "0";
		this.settleMode = StringUtils.isNotBlank(paramMap.get("settleMode")) ? paramMap.get("settleMode") : "0";
		this.settleCycle = paramMap.get("settleCycle");
		this.feeType = StringUtils.isNotBlank(paramMap.get("feeType")) ? paramMap.get("feeType") : "0";
		this.feeRate = new BigDecimal(StringUtils.isNotBlank(paramMap.get("feeRate")) ? paramMap.get("feeRate") : "0");
		this.feeAmount = new BigDecimal(StringUtils.isNotBlank(paramMap.get("feeAmount")) ? paramMap.get("feeAmount") : "0");
		this.settleLowestFee = new BigDecimal(StringUtils.isNotBlank(paramMap.get("settleLowestFee")) ? paramMap.get("settleLowestFee") : "0");
		this.settleHighestFee = new BigDecimal(StringUtils.isNotBlank(paramMap.get("settleHighestFee")) ? paramMap.get("settleHighestFee") : "0");
		this.status = paramMap.get("status");
		this.appId = paramMap.get("appId");
		this.appKey = paramMap.get("appKey");
		this.operatorId = Long.parseLong(StringUtils.isNotBlank(paramMap.get("operatorId")) ? paramMap.get("operatorId") : "0");
		this.extend1 = paramMap.get("extend1");
		this.extend2 = paramMap.get("extend2");
		this.extend3 = paramMap.get("extend3");
		this.activeTime = paramMap.get("activeTime");
		this.feeStatus = paramMap.get("feeStatus") != null ? paramMap.get("feeStatus") : StatusEnum.VALID.getCode();

		List<ProductRelaFormInfo> productRelaFormInfos = new ArrayList<>();
		//获取通道商户支付方式ID
		List<String> keys = new ArrayList<>();
		for (String key : paramMap.keySet()) {
			if (key.contains("payType")) {
				keys.add(key);
			}
		}

		ProductRelaFormInfo productRelaFormInfo;
		//根据通道商户支付方式ID获取别的参数
		for (String key : keys) {
			productRelaFormInfo = new ProductRelaFormInfo();
			String number = key.substring(7, key.length());
			if (StringUtils.isNotBlank(paramMap.get("rate" + number))){
				productRelaFormInfo.setRate(new BigDecimal(paramMap.get("rate" + number)));
			}
			if (paramMap.get("isValid" + number) != null && "on".equals(paramMap.get("isValid" + number))){
				productRelaFormInfo.setIsValid(Integer.parseInt(StatusEnum.VALID.getCode()));
			}else {
				productRelaFormInfo.setIsValid(Integer.parseInt(StatusEnum.TOBEVALID.getCode()));
			}
			productRelaFormInfo.setIsDelete(Integer.parseInt(StatusEnum.TOBEVALID.getCode()));
			productRelaFormInfo.setProductId(this.id);
			productRelaFormInfo.setChanMchtPaytypeId(paramMap.get(key));
			productRelaFormInfo.setSort(Integer.parseInt(paramMap.get("sort" + number)));
			productRelaFormInfos.add(productRelaFormInfo);
		}

		this.productRelas = productRelaFormInfos;


		List<SubProduct> subProducts = new ArrayList<>();
		//获取子产品ID
		List<String> productkeys = new ArrayList<>();
		for (String productkey : paramMap.keySet()) {
			if (productkey.contains("subProductId")) {
				productkeys.add(productkey);
			}
		}

		SubProduct subProduct;
		//根据通道商户支付方式ID获取别的参数
		for (String productkey : productkeys) {
			subProduct = new SubProduct();
			String number = productkey.substring(12, productkey.length());
			subProduct.setSubProductId(paramMap.get("subProductId" + number));
			subProduct.setSort(Integer.parseInt(paramMap.get("sort" + number)));
			subProducts.add(subProduct);
		}
		this.subProducts = subProducts;
	}

	//获取费率对象
	public void getFee(PlatFeerate platFeerate) {
		platFeerate.setFeeType(this.feeType);
		platFeerate.setFeeRate(this.feeRate);
		platFeerate.setFeeAmount(this.feeAmount);
		platFeerate.setSettleType(this.settleType);
		platFeerate.setSettleMode(this.settleMode);
		platFeerate.setSettleCycle(this.settleCycle);
		platFeerate.setSettleLowestFee(this.settleLowestFee);

		if (StringUtils.isBlank(this.activeTime) || StatusEnum.VALID.getCode().equals(this.feeStatus)) {
			platFeerate.setActiveTime(new Date());
		} else {
			platFeerate.setActiveTime(DateUtils.parseDate(this.activeTime));
		}
		platFeerate.setStatus(this.feeStatus);
	}

	//设置费率对象
	public void setFee(PlatFeerate platFeerate) {
		if (platFeerate == null) {
			return;
		}
		this.feeType = platFeerate.getFeeType();
		this.feeRate = platFeerate.getFeeRate();
		this.feeAmount = platFeerate.getFeeAmount();
		this.settleType = platFeerate.getSettleType();
		this.settleMode = platFeerate.getSettleMode();
		this.settleCycle = platFeerate.getSettleCycle();
		this.settleLowestFee = platFeerate.getSettleLowestFee();
		this.activeTime = DateUtils.formatDate(platFeerate.getActiveTime(), "yyyy-MM-dd HH:mm:ss");

		if (platFeerate.getActiveTime().getTime() < System.currentTimeMillis()) {
			this.feeStatus = StatusEnum.VALID.getCode();
		} else {
			this.feeStatus = StatusEnum.TOBEVALID.getCode();
		}
	}
}
