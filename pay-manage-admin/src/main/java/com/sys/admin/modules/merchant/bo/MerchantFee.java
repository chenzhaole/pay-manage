package com.sys.admin.modules.merchant.bo;

import java.math.BigDecimal;

/**
 * 
 * @ClassName: MerchantForm 
 * @Description: 商户费率
 * @author: ALI
 * @date: 2018年04月27日 上午11:28:31
 */
public class MerchantFee {

	private String mchtId;

	private String paytypeCode;

	private String paytypeName;

	private String feeType;

	private BigDecimal feeRate;

	private BigDecimal feeAmount;

	private String save;

	public String getMchtId() {
		return mchtId;
	}

	public void setMchtId(String mchtId) {
		this.mchtId = mchtId;
	}

	public String getPaytypeCode() {
		return paytypeCode;
	}

	public void setPaytypeCode(String paytypeCode) {
		this.paytypeCode = paytypeCode;
	}

	public String getPaytypeName() {
		return paytypeName;
	}

	public void setPaytypeName(String paytypeName) {
		this.paytypeName = paytypeName;
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

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}
}
