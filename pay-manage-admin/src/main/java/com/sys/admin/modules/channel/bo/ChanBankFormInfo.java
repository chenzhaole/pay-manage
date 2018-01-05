package com.sys.admin.modules.channel.bo;

import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.common.PageInfo;

import org.apache.commons.lang3.StringUtils;


import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ChanBankFormInfo
 * @Description: 通道银行页面form表单业务实体
 * @author: ALI
 * @date: 2017年12月11日 下午16:40:14
 */
public class ChanBankFormInfo {
	private String id;

	private String chanName;

	private String chanCode;

	private String payType;

	private String bankName;

	private String platBankCode;

	private String chanBankCode;

	private String chanCheckCode;

	private BigDecimal debitMinMoney;

	private BigDecimal debitMaxMoney;

	private BigDecimal debitTotalMoney;

	private BigDecimal creditMinMoney;

	private BigDecimal creditMaxMoney;

	private BigDecimal creditTotalMoney;

	private BigDecimal passbookMinMoney;

	private BigDecimal passbookMaxMoney;

	private BigDecimal passbookTotalMoney;

	private String status;

	private Date createDate;

	private Date updateDate;

	private Long operatorId;

	private String extend;

	private PageInfo pageInfo;

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

	public String getChanName() {
		return chanName;
	}

	public void setChanName(String chanName) {
		this.chanName = chanName;
	}

	public String getChanCode() {
		return chanCode;
	}

	public void setChanCode(String chanCode) {
		this.chanCode = chanCode;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPlatBankCode() {
		return platBankCode;
	}

	public void setPlatBankCode(String platBankCode) {
		this.platBankCode = platBankCode;
	}

	public BigDecimal getDebitTotalMoney() {
		return debitTotalMoney;
	}

	public void setDebitTotalMoney(BigDecimal debitTotalMoney) {
		this.debitTotalMoney = debitTotalMoney;
	}

	public BigDecimal getCreditTotalMoney() {
		return creditTotalMoney;
	}

	public void setCreditTotalMoney(BigDecimal creditTotalMoney) {
		this.creditTotalMoney = creditTotalMoney;
	}

	public BigDecimal getPassbookTotalMoney() {
		return passbookTotalMoney;
	}

	public void setPassbookTotalMoney(BigDecimal passbookTotalMoney) {
		this.passbookTotalMoney = passbookTotalMoney;
	}

	public String getChanBankCode() {
		return chanBankCode;
	}

	public void setChanBankCode(String chanBankCode) {
		this.chanBankCode = chanBankCode;
	}

	public String getChanCheckCode() {
		return chanCheckCode;
	}

	public void setChanCheckCode(String chanCheckCode) {
		this.chanCheckCode = chanCheckCode;
	}

	public BigDecimal getDebitMinMoney() {
		return debitMinMoney;
	}

	public void setDebitMinMoney(BigDecimal debitMinMoney) {
		this.debitMinMoney = debitMinMoney;
	}

	public BigDecimal getDebitMaxMoney() {
		return debitMaxMoney;
	}

	public void setDebitMaxMoney(BigDecimal debitMaxMoney) {
		this.debitMaxMoney = debitMaxMoney;
	}

	public BigDecimal getCreditMinMoney() {
		return creditMinMoney;
	}

	public void setCreditMinMoney(BigDecimal creditMinMoney) {
		this.creditMinMoney = creditMinMoney;
	}

	public BigDecimal getCreditMaxMoney() {
		return creditMaxMoney;
	}

	public void setCreditMaxMoney(BigDecimal creditMaxMoney) {
		this.creditMaxMoney = creditMaxMoney;
	}

	public BigDecimal getPassbookMinMoney() {
		return passbookMinMoney;
	}

	public void setPassbookMinMoney(BigDecimal passbookMinMoney) {
		this.passbookMinMoney = passbookMinMoney;
	}

	public BigDecimal getPassbookMaxMoney() {
		return passbookMaxMoney;
	}

	public void setPassbookMaxMoney(BigDecimal passbookMaxMoney) {
		this.passbookMaxMoney = passbookMaxMoney;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	//空的构造方法
	public ChanBankFormInfo() {

	}

	//将页面请求参数转化成实体构造方法
	public ChanBankFormInfo(HttpServletRequest request) {
		if (request != null) {
			Enumeration<String> requestKeys = request.getParameterNames();
			//请求页面表单map
			Map<String, String> requestMap = new HashMap<>();
			//遍历请求参数，封装请求对象
			while (requestKeys.hasMoreElements()) {
				String requestKey = requestKeys.nextElement();
				requestMap.put(requestKey, request.getParameter(requestKey));
			}
			this.id = requestMap.get("id");
			this.chanCode = requestMap.get("chanCode");
			this.chanName = requestMap.get("chanName");
			this.bankName = requestMap.get("bankName");
			this.chanBankCode = requestMap.get("chanBankCode");
			this.platBankCode = requestMap.get("platBankCode");
			this.payType = requestMap.get("payType");
			this.creditMaxMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("creditMaxMoney")) ? "0" : requestMap.get("creditMaxMoney"));
			this.creditMinMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("creditMinMoney")) ? "0" : requestMap.get("creditMinMoney"));
			this.creditTotalMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("creditTotalMoney")) ? "0" : requestMap.get("creditTotalMoney"));
			this.debitMaxMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("debitMaxMoney")) ? "0" : requestMap.get("debitMaxMoney"));
			this.debitMinMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("debitMinMoney")) ? "0" : requestMap.get("debitMinMoney"));
			this.debitTotalMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("debitTotalMoney")) ? "0" : requestMap.get("debitTotalMoney"));
			this.passbookMaxMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("passbookMaxMoney")) ? "0" : requestMap.get("passbookMaxMoney"));
			this.passbookMinMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("passbookMinMoney")) ? "0" : requestMap.get("passbookMinMoney"));
			this.passbookTotalMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("passbookTotalMoney")) ? "0" : requestMap.get("passbookTotalMoney"));
			this.extend = requestMap.get("extend");
			this.status = requestMap.get("status");
//			this.settleCategory = Integer.parseInt(StringUtils.isBlank(requestMap.get("settleCategory")) ? "0" : requestMap.get("settleCategory"));
//			this.feeRate = Double.parseDouble(StringUtils.isBlank(requestMap.get("feeRate")) ? "0" : requestMap.get("feeRate"));
//			this.depositLimit = new BigDecimal(StringUtils.isBlank(requestMap.get("depositLimit")) ? "0" : requestMap.get("depositLimit"));
//			if (!StringUtils.isBlank(requestMap.get("operatorUserId"))) {
//				this.operatorUserId = Long.parseLong(requestMap.get("operatorUserId"));
//			}
		}
	}
}