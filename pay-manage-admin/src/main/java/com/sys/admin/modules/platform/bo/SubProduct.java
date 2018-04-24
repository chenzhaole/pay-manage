package com.sys.admin.modules.platform.bo;

/**
 * 子产品
 * @author ALI
 * at 2017/9/4 16:54
 */
public class SubProduct {

	private String subProductId;

	private Integer sort;

	private static final long serialVersionUID = 1L;

	public String getSubProductId() {
		return subProductId;
	}

	public void setSubProductId(String subProductId) {
		this.subProductId = subProductId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public SubProduct() {
	}
}
