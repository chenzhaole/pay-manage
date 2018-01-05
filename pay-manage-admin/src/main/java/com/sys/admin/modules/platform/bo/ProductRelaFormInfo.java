package com.sys.admin.modules.platform.bo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author ALI
 * at 2017/9/4 16:54
 */
public class ProductRelaFormInfo {

	private String productId;

	private String chanMchtPaytypeId;

	private Integer isValid;

	private Integer isDelete;

	private BigDecimal rate;

	private Integer sort;

	private static final long serialVersionUID = 1L;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getChanMchtPaytypeId() {
		return chanMchtPaytypeId;
	}

	public void setChanMchtPaytypeId(String chanMchtPaytypeId) {
		this.chanMchtPaytypeId = chanMchtPaytypeId;
	}

	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public ProductRelaFormInfo() {
	}

	public ProductRelaFormInfo(Map<String, String> param) {

	}
}
