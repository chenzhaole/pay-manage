package com.sys.admin.modules.platform.bo;

import com.sys.common.enums.StatusEnum;
import com.sys.common.util.DateUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.PlatFeerate;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 商户产品
 *
 * @author ALI
 * at 2017/9/6 17:41
 */
public class MchtProductFormInfo {

	private String mchtId;

	private String mchtCode;

	private String mchtName;

	private String productId;

	private String productCode;

	private String productName;

	private Integer isValid;

	private Date createTime;

	private Date updateTime;

	private String operatorId;

	private String settleType;

	private String settleMode;

	private String settleCycle;

	private String feeType;

	private BigDecimal feeRate;

	private BigDecimal feeAmount;

	private BigDecimal settleLowestFee;

	private BigDecimal settleHighestFee;

	private String activeTime;

	private String feeStatus;

	private PageInfo pageInfo;

	private static final long serialVersionUID = 1L;

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public String getMchtId() {
		return mchtId;
	}

	public void setMchtId(String mchtId) {
		this.mchtId = mchtId;
	}

	public String getMchtCode() {
		return mchtCode;
	}

	public void setMchtCode(String mchtCode) {
		this.mchtCode = mchtCode;
	}

	public String getMchtName() {
		return mchtName;
	}

	public void setMchtName(String mchtName) {
		this.mchtName = mchtName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
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

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
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

	public MchtProductFormInfo() {
	}

	public MchtProductFormInfo(Map<String, String> paramMap) {

		this.mchtId = paramMap.get("mchtId");
		this.mchtName = paramMap.get("mchtName");
		this.mchtCode = paramMap.get("mchtCode");
		this.productId = paramMap.get("productId");
		this.productName = paramMap.get("productName");
		this.productCode = paramMap.get("productCode");
		this.isValid = Integer.parseInt(StringUtils.isNotBlank(paramMap.get("isValid")) ? paramMap.get("isValid") : "0");
		this.operatorId = paramMap.get("operatorId");
		this.settleType = StringUtils.isNotBlank(paramMap.get("settleType")) ? paramMap.get("settleType") : "0";
		this.settleMode = StringUtils.isNotBlank(paramMap.get("settleMode")) ? paramMap.get("settleMode") : "0";
		this.settleCycle = StringUtils.isNotBlank(paramMap.get("settleCycle")) ? paramMap.get("settleCycle") : "0";
		this.feeType = StringUtils.isNotBlank(paramMap.get("feeType")) ? paramMap.get("feeType") : "0";
		this.feeRate = new BigDecimal(StringUtils.isNotBlank(paramMap.get("feeRate")) ? paramMap.get("feeRate") : "0");
		this.feeAmount = new BigDecimal(StringUtils.isNotBlank(paramMap.get("feeAmount")) ? paramMap.get("feeAmount") : "0");
		this.settleLowestFee = new BigDecimal(StringUtils.isNotBlank(paramMap.get("settleLowestFee")) ? paramMap.get("settleLowestFee") : "0");
		this.settleHighestFee = new BigDecimal(StringUtils.isNotBlank(paramMap.get("settleHighestFee")) ? paramMap.get("settleHighestFee") : "0");
		this.operatorId = StringUtils.isNotBlank(paramMap.get("operatorId")) ? paramMap.get("operatorId") : "0";
		this.activeTime = paramMap.get("activeTime");
		this.feeStatus = paramMap.get("feeStatus") != null ? paramMap.get("feeStatus") : StatusEnum.VALID.getCode();

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
