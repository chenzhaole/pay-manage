package com.sys.admin.modules.channel.bo;

import com.sys.common.enums.StatusEnum;
import com.sys.common.util.DateUtils;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.PlatFeerate;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ChanMchtFormInfo
 * @Description: 商戶支付通道页面form表单业务实体
 * @author: ALI
 * @date: 2017年8月29日 下午16:40:14
 */
public class ChanMchtFormInfo {
	private String id;

	private String name;

	private String chanId;

	private String chanName;

	private String chanCode;

	private String mchtId;

	private String mchtName;

	private String mchtCode;

	private String payType;

	private String bizCode;

	private String chanMchtNo;

	private String terminalNo;

	private String chanMchtPassword;

	private String opAccount;

	private String opPassword;

	private String status;

	private String feeStatus;

	private Integer settleCategory;

	private Integer settleMode;

	private String settleCycle;

	private String feeType;

	private Double feeRate;

	private Double feeAmount;

	private Double lowestFee;

	private Double highestFee;

	private String payUrl;

	private String cancelUrl;

	private String synNotifyUrl;

	private String asynNotifyUrl;

	private String refundUrl;

	private String synRefundNotifyUrl;

	private String asynRefundNotifyUrl;

	private String queryUrl;

	private String queryBalanceUrl;

	private String queryRefundUrl;

	private String checkUrl;

	private String checkRefundUrl;

	private String tranUrl;

	private Integer sdkType;

	private BigDecimal depositLimit;

	private BigDecimal debitcardMinMoney;

	private BigDecimal debitcardMaxMoney;

	private BigDecimal creditcardMinMoney;

	private BigDecimal creditcardMaxMoney;

	private BigDecimal bankbookMinMoney;

	private BigDecimal bankbookMaxMoney;

	private String certPath1;

	private String certContent1;

	private String certPath2;

	private String certContent2;

	private String certPath3;

	private String certContent3;

	private Long operatorUserId;

	private Date createDate;

	private Date updateDate;

	private String activeTime;

	private String extend1;

	private String extend2;

	private String extend3;

	private String contractType;

	private String parentId;

	private PageInfo pageInfo;

	private Integer perdayPayMaxAmount;
	
	private Integer smsSendType;//短信发送方式：0-不发短信，1-平台发短信，2-通道发短信
    private String smsContentTemplet;//短信内容模板

	private Integer combType; //组合类型： 1-（绑卡+支付）组合接口；2-标准接口'

	private String deviceType; //调用收银台页面的设备类型：1-手机端，2-PC端，3-微信内，4-支付宝内，5-APP。多选项，各值用半角逗号分隔

	private BigDecimal tradeMinMoney;

	private BigDecimal tradeMaxMoney;

	private String tradeRangeMoney;

	private String tradeStartTime;

	private String tradeEndTime;

	private String tradeStartTimeH;
	private String tradeStartTimeS;
	private String tradeEndTimeH;
	private String tradeEndTimeS;

	//账务类型	0:默认保留，1:支付，2:代付，3:调账，4:退款，5:清分 6 充值
	private String accType;

	private static final long serialVersionUID = 1L;

	public String getTradeRangeMoney() {
		return tradeRangeMoney;
	}

	public void setTradeRangeMoney(String tradeRangeMoney) {
		this.tradeRangeMoney = tradeRangeMoney;
	}

	public String getTradeStartTimeH() {
		return tradeStartTimeH;
	}

	public void setTradeStartTimeH(String tradeStartTimeH) {
		this.tradeStartTimeH = tradeStartTimeH;
	}

	public String getTradeStartTimeS() {
		return tradeStartTimeS;
	}

	public void setTradeStartTimeS(String tradeStartTimeS) {
		this.tradeStartTimeS = tradeStartTimeS;
	}

	public String getTradeEndTimeH() {
		return tradeEndTimeH;
	}

	public void setTradeEndTimeH(String tradeEndTimeH) {
		this.tradeEndTimeH = tradeEndTimeH;
	}

	public String getTradeEndTimeS() {
		return tradeEndTimeS;
	}

	public void setTradeEndTimeS(String tradeEndTimeS) {
		this.tradeEndTimeS = tradeEndTimeS;
	}

	public BigDecimal getTradeMinMoney() {
		return tradeMinMoney;
	}

	public void setTradeMinMoney(BigDecimal tradeMinMoney) {
		this.tradeMinMoney = tradeMinMoney;
	}

	public BigDecimal getTradeMaxMoney() {
		return tradeMaxMoney;
	}

	public void setTradeMaxMoney(BigDecimal tradeMaxMoney) {
		this.tradeMaxMoney = tradeMaxMoney;
	}

	public String getTradeStartTime() {
		return tradeStartTime;
	}

	public void setTradeStartTime(String tradeStartTime) {
		this.tradeStartTime = tradeStartTime;
	}

	public String getTradeEndTime() {
		return tradeEndTime;
	}

	public void setTradeEndTime(String tradeEndTime) {
		this.tradeEndTime = tradeEndTime;
	}

	public Integer getPerdayPayMaxAmount() {
		return perdayPayMaxAmount;
	}

	public void setPerdayPayMaxAmount(Integer perdayPayMaxAmount) {
		this.perdayPayMaxAmount = perdayPayMaxAmount;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChanId() {
		return chanId;
	}

	public void setChanId(String chanId) {
		this.chanId = chanId;
	}

	public String getChanCode() {
		return chanCode;
	}

	public void setChanCode(String chanCode) {
		this.chanCode = chanCode;
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

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public String getChanMchtNo() {
		return chanMchtNo;
	}

	public void setChanMchtNo(String chanMchtNo) {
		this.chanMchtNo = chanMchtNo;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getChanMchtPassword() {
		return chanMchtPassword;
	}

	public void setChanMchtPassword(String chanMchtPassword) {
		this.chanMchtPassword = chanMchtPassword;
	}

	public String getOpAccount() {
		return opAccount;
	}

	public void setOpAccount(String opAccount) {
		this.opAccount = opAccount;
	}

	public String getOpPassword() {
		return opPassword;
	}

	public void setOpPassword(String opPassword) {
		this.opPassword = opPassword;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFeeStatus() {
		return feeStatus;
	}

	public void setFeeStatus(String feeStatus) {
		this.feeStatus = feeStatus;
	}

	public Integer getSettleCategory() {
		return settleCategory;
	}

	public void setSettleCategory(Integer settleCategory) {
		this.settleCategory = settleCategory;
	}

	public Integer getSettleMode() {
		return settleMode;
	}

	public void setSettleMode(Integer settleMode) {
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

	public Double getFeeRate() {
		return feeRate;
	}

	public Double getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(String activeTime) {
		this.activeTime = activeTime;
	}

	public void setFeeRate(Double feeRate) {
		this.feeRate = feeRate;
	}

	public Double getLowestFee() {
		return lowestFee;
	}

	public void setLowestFee(Double lowestFee) {
		this.lowestFee = lowestFee;
	}

	public Double getHighestFee() {
		return highestFee;
	}

	public void setHighestFee(Double highestFee) {
		this.highestFee = highestFee;
	}

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	public String getSynNotifyUrl() {
		return synNotifyUrl;
	}

	public void setSynNotifyUrl(String synNotifyUrl) {
		this.synNotifyUrl = synNotifyUrl;
	}

	public String getAsynNotifyUrl() {
		return asynNotifyUrl;
	}

	public void setAsynNotifyUrl(String asynNotifyUrl) {
		this.asynNotifyUrl = asynNotifyUrl;
	}

	public String getRefundUrl() {
		return refundUrl;
	}

	public void setRefundUrl(String refundUrl) {
		this.refundUrl = refundUrl;
	}

	public String getSynRefundNotifyUrl() {
		return synRefundNotifyUrl;
	}

	public void setSynRefundNotifyUrl(String synRefundNotifyUrl) {
		this.synRefundNotifyUrl = synRefundNotifyUrl;
	}

	public String getAsynRefundNotifyUrl() {
		return asynRefundNotifyUrl;
	}

	public void setAsynRefundNotifyUrl(String asynRefundNotifyUrl) {
		this.asynRefundNotifyUrl = asynRefundNotifyUrl;
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	public String getQueryBalanceUrl() {
		return queryBalanceUrl;
	}

	public void setQueryBalanceUrl(String queryBalanceUrl) {
		this.queryBalanceUrl = queryBalanceUrl;
	}

	public String getQueryRefundUrl() {
		return queryRefundUrl;
	}

	public void setQueryRefundUrl(String queryRefundUrl) {
		this.queryRefundUrl = queryRefundUrl;
	}

	public String getCheckUrl() {
		return checkUrl;
	}

	public void setCheckUrl(String checkUrl) {
		this.checkUrl = checkUrl;
	}

	public String getCheckRefundUrl() {
		return checkRefundUrl;
	}

	public void setCheckRefundUrl(String checkRefundUrl) {
		this.checkRefundUrl = checkRefundUrl;
	}

	public String getTranUrl() {
		return tranUrl;
	}

	public void setTranUrl(String tranUrl) {
		this.tranUrl = tranUrl;
	}

	public Integer getSdkType() {
		return sdkType;
	}

	public void setSdkType(Integer sdkType) {
		this.sdkType = sdkType;
	}

	public BigDecimal getDepositLimit() {
		return depositLimit;
	}

	public void setDepositLimit(BigDecimal depositLimit) {
		this.depositLimit = depositLimit;
	}

	public BigDecimal getDebitcardMinMoney() {
		return debitcardMinMoney;
	}

	public void setDebitcardMinMoney(BigDecimal debitcardMinMoney) {
		this.debitcardMinMoney = debitcardMinMoney;
	}

	public BigDecimal getDebitcardMaxMoney() {
		return debitcardMaxMoney;
	}

	public void setDebitcardMaxMoney(BigDecimal debitcardMaxMoney) {
		this.debitcardMaxMoney = debitcardMaxMoney;
	}

	public BigDecimal getCreditcardMinMoney() {
		return creditcardMinMoney;
	}

	public void setCreditcardMinMoney(BigDecimal creditcardMinMoney) {
		this.creditcardMinMoney = creditcardMinMoney;
	}

	public BigDecimal getCreditcardMaxMoney() {
		return creditcardMaxMoney;
	}

	public void setCreditcardMaxMoney(BigDecimal creditcardMaxMoney) {
		this.creditcardMaxMoney = creditcardMaxMoney;
	}

	public BigDecimal getBankbookMinMoney() {
		return bankbookMinMoney;
	}

	public void setBankbookMinMoney(BigDecimal bankbookMinMoney) {
		this.bankbookMinMoney = bankbookMinMoney;
	}

	public BigDecimal getBankbookMaxMoney() {
		return bankbookMaxMoney;
	}

	public void setBankbookMaxMoney(BigDecimal bankbookMaxMoney) {
		this.bankbookMaxMoney = bankbookMaxMoney;
	}

	public String getCertPath1() {
		return certPath1;
	}

	public void setCertPath1(String certPath1) {
		this.certPath1 = certPath1;
	}

	public String getCertContent1() {
		return certContent1;
	}

	public void setCertContent1(String certContent1) {
		this.certContent1 = certContent1;
	}

	public String getCertPath2() {
		return certPath2;
	}

	public void setCertPath2(String certPath2) {
		this.certPath2 = certPath2;
	}

	public String getCertContent2() {
		return certContent2;
	}

	public void setCertContent2(String certContent2) {
		this.certContent2 = certContent2;
	}

	public String getCertPath3() {
		return certPath3;
	}

	public void setCertPath3(String certPath3) {
		this.certPath3 = certPath3;
	}

	public String getCertContent3() {
		return certContent3;
	}

	public void setCertContent3(String certContent3) {
		this.certContent3 = certContent3;
	}

	public Long getOperatorUserId() {
		return operatorUserId;
	}

	public void setOperatorUserId(Long operatorUserId) {
		this.operatorUserId = operatorUserId;
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

	public String getChanName() {
		return chanName;
	}

	public void setChanName(String chanName) {
		this.chanName = chanName;
	}

	public String getMchtName() {
		return mchtName;
	}

	public void setMchtName(String mchtName) {
		this.mchtName = mchtName;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public Integer getSmsSendType() {
        return smsSendType;
    }
    public void setSmsSendType(Integer smsSendType) {
        this.smsSendType = smsSendType;
    }
    public String getSmsContentTemplet() {
        return smsContentTemplet;
    }
    public void setSmsContentTemplet(String smsContentTemplet) {
        this.smsContentTemplet = smsContentTemplet;
    }

	public Integer getCombType() {
		return combType;
	}

	public void setCombType(Integer combType) {
		this.combType = combType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	//空的构造方法
	public ChanMchtFormInfo() {

	}

	//将页面请求参数转化成实体构造方法
	public ChanMchtFormInfo(HttpServletRequest request) {
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
			this.name = requestMap.get("name");
			this.chanId = requestMap.get("chanId");
			this.chanName = requestMap.get("chanName");
			this.chanCode = requestMap.get("chanCode");
			this.mchtId = requestMap.get("mchtId");
			this.mchtName = requestMap.get("mchtName");
			this.mchtCode = requestMap.get("mchtCode");
			this.chanMchtNo = requestMap.get("chanMchtNo");
			this.payType = requestMap.get("payType");
			this.bizCode = requestMap.get("bizCode");
			this.chanMchtPassword = requestMap.get("chanMchtPassword");
			this.opAccount = requestMap.get("opAccount");
			this.opPassword = requestMap.get("opPassword");
			this.terminalNo = requestMap.get("terminalNo");
			this.tranUrl = requestMap.get("tranUrl");
			this.status = requestMap.get("status");
			this.feeStatus = requestMap.get("feeStatus");
			this.settleCategory = Integer.parseInt(StringUtils.isBlank(requestMap.get("settleCategory")) ? "0" : requestMap.get("settleCategory"));
			this.settleMode = Integer.parseInt(StringUtils.isBlank(requestMap.get("settleMode")) ? "0" : requestMap.get("settleMode"));
			this.settleCycle = requestMap.get("settleCycle");
			this.feeType = requestMap.get("feeType");
			this.feeRate = Double.parseDouble(StringUtils.isBlank(requestMap.get("feeRate")) ? "0" : requestMap.get("feeRate"));
			this.feeAmount = Double.parseDouble(StringUtils.isBlank(requestMap.get("feeAmount")) ? "0" : requestMap.get("feeAmount"));
			this.activeTime = requestMap.get("activeTime");
			this.lowestFee = Double.parseDouble(StringUtils.isBlank(requestMap.get("lowestFee")) ? "0" : requestMap.get("lowestFee"));
			this.highestFee = Double.parseDouble(StringUtils.isBlank(requestMap.get("highestFee")) ? "0" : requestMap.get("highestFee"));
			this.payUrl = requestMap.get("payUrl");
			this.cancelUrl = requestMap.get("cancelUrl");
			this.synNotifyUrl = requestMap.get("synNotifyUrl");
			this.asynNotifyUrl = requestMap.get("asynNotifyUrl");
			this.refundUrl = requestMap.get("refundUrl");
			this.synRefundNotifyUrl = requestMap.get("synRefundNotifyUrl");
			this.asynRefundNotifyUrl = requestMap.get("asynRefundNotifyUrl");
			this.queryUrl = requestMap.get("queryUrl");
			this.queryBalanceUrl = requestMap.get("queryBalanceUrl");
			this.queryRefundUrl = requestMap.get("queryRefundUrl");
			this.checkUrl = requestMap.get("checkUrl");
			this.checkRefundUrl = requestMap.get("checkRefundUrl");
			this.depositLimit = new BigDecimal(StringUtils.isBlank(requestMap.get("depositLimit")) ? "0" : requestMap.get("depositLimit"));
			this.debitcardMinMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("debitcardMinMoney")) ? "0" : requestMap.get("debitcardMinMoney"));
			this.debitcardMaxMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("debitcardMaxMoney")) ? "0" : requestMap.get("debitcardMaxMoney"));
			this.creditcardMinMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("creditcardMinMoney")) ? "0" : requestMap.get("creditcardMinMoney"));
			this.creditcardMaxMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("creditcardMaxMoney")) ? "0" : requestMap.get("creditcardMaxMoney"));
			this.bankbookMinMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("bankbookMinMoney")) ? "0" : requestMap.get("bankbookMinMoney"));
			this.bankbookMaxMoney = new BigDecimal(StringUtils.isBlank(requestMap.get("bankbookMaxMoney")) ? "0" : requestMap.get("bankbookMaxMoney"));
			this.certPath1 = requestMap.get("certPath1");
			this.certContent1 = requestMap.get("certContent1");
			this.certPath2 = requestMap.get("certPath2");
			this.certContent2 = requestMap.get("certContent2");
			this.certPath3 = requestMap.get("certPath3");
			this.certContent3 = requestMap.get("certContent3");
			if (!StringUtils.isBlank(requestMap.get("operatorUserId"))) {
				this.operatorUserId = Long.parseLong(requestMap.get("operatorUserId"));
			}
			this.extend1 = requestMap.get("extend1");
			this.extend2 = requestMap.get("extend2");
			this.extend3 = requestMap.get("extend3");
			this.sdkType = Integer.parseInt(StringUtils.isBlank(requestMap.get("sdkType")) ? "0" : requestMap.get("sdkType"));

			this.perdayPayMaxAmount = Integer.parseInt(StringUtils.isBlank(requestMap.get("perdayPayMaxAmount")) ? "0" : requestMap.get("perdayPayMaxAmount"));

			this.contractType = requestMap.get("contractType");
			this.parentId = requestMap.get("parentId");
			this.smsSendType = Integer.parseInt(StringUtils.isBlank(requestMap.get("smsSendType")) ? "0" :requestMap.get("smsSendType"));
            this.smsContentTemplet = requestMap.get("smsContentTemplet");
            this.combType = Integer.parseInt(StringUtils.isBlank(requestMap.get("combType")) ? "0" :requestMap.get("combType"));

			String[] deviceTypes = request.getParameterValues("deviceType");

			if (deviceTypes != null && deviceTypes.length > 0){
				StringBuffer signTypeTemp = new StringBuffer();
				for (String signType : deviceTypes) {
					signTypeTemp.append(signType);
					signTypeTemp.append(",");
				}
				String signType = signTypeTemp.toString();
				this.deviceType = signType.substring(0, signType.length() - 1);
			}

			this.tradeStartTime = requestMap.get("tradeStartTimeH") + ":" + requestMap.get("tradeStartTimeS");
			this.tradeEndTime = requestMap.get("tradeEndTimeH") + ":" + requestMap.get("tradeEndTimeS");

			this.tradeMaxMoney = StringUtils.isBlank(requestMap.get("tradeMaxMoney")) ? new BigDecimal(0) : new BigDecimal(requestMap.get("tradeMaxMoney"));
			this.tradeMinMoney = StringUtils.isBlank(requestMap.get("tradeMinMoney")) ? new BigDecimal(0) : new BigDecimal(requestMap.get("tradeMinMoney"));
			this.tradeRangeMoney = requestMap.get("tradeRangeMoney") == null ? "" : requestMap.get("tradeRangeMoney");
			this.accType = requestMap.get("accType") == null ? "" : requestMap.get("accType");
		}
	}

	//获取费率对象
	public void getFee(PlatFeerate platFeerate) {
		platFeerate.setFeeType(this.feeType);
		platFeerate.setFeeRate(new BigDecimal(this.feeRate != null ? this.feeRate : 0));
		platFeerate.setFeeAmount(new BigDecimal(this.feeAmount != null ? this.feeAmount : 0));
		platFeerate.setSettleType(this.settleCategory != null ? this.settleCategory + "" : "");
		platFeerate.setSettleMode(this.settleMode != null ? this.settleMode + "" : "");
		platFeerate.setSettleCycle(this.settleCycle);
		platFeerate.setSettleLowestAmount(new BigDecimal(this.lowestFee != null ? this.lowestFee : 0));

		platFeerate.setPerdayPayMaxAmount(new BigDecimal(this.perdayPayMaxAmount != null ? this.perdayPayMaxAmount : 0));

		if (StringUtils.isBlank(this.activeTime) || StatusEnum.VALID.getCode().equals(this.feeStatus)) {
			platFeerate.setActiveTime(new Date());
		}else {
			platFeerate.setActiveTime(DateUtils.parseDate(this.activeTime));
		}
		platFeerate.setStatus(this.feeStatus != null ? this.feeStatus : StatusEnum.VALID.getCode());
	}

	//设置费率对象
	public void setFee(PlatFeerate platFeerate) {
		if (platFeerate == null) {
			return;
		}
		this.feeType = platFeerate.getFeeType() != null ? platFeerate.getFeeType() : "";
		this.feeRate = platFeerate.getFeeRate() != null ? platFeerate.getFeeRate().doubleValue() : null;
		this.feeAmount = platFeerate.getFeeAmount() != null ? platFeerate.getFeeAmount().doubleValue() : null;
		this.settleCategory = Integer.parseInt(platFeerate.getSettleType() != null ? platFeerate.getSettleType() : "0");
		this.settleMode = Integer.parseInt(platFeerate.getSettleMode()  != null ? platFeerate.getSettleMode() : "0");
		this.settleCycle = platFeerate.getSettleCycle();
		this.lowestFee = platFeerate.getSettleLowestAmount() != null ? platFeerate.getSettleLowestAmount().doubleValue() : null;
		this.activeTime = DateUtils.formatDate(platFeerate.getActiveTime(), "yyyy-MM-dd HH:mm:ss");

		this.perdayPayMaxAmount = platFeerate.getSettleLowestAmount() != null ? platFeerate.getSettleLowestAmount().intValue() : null;

		if (platFeerate.getActiveTime().getTime() < System.currentTimeMillis()){
			this.feeStatus = StatusEnum.VALID.getCode();
		}else {
			this.feeStatus = StatusEnum.TOBEVALID.getCode();
		}
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}
}