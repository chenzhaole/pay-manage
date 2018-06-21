package com.sys.admin.modules.platform.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class PlatProxyDetailVo implements Serializable {

    private String id;
    private String platBatchId;
    private String mchtBatchId;
    private String mchtSeq;
    private String channelSeq;
    private String mchtId;
    private String mchtName;//商户名称
    private String subMchtId;
    private String chanMchtPaytypeId;
    private String chanId;
    private String chanName;//通道名称
    private String payType;
    private String chanMchtNo;
    private String goodId;
    private String serialNum;
    private String channelTradeId;
    private String custBatchNo;
    private BigDecimal amount;
    private BigDecimal mchtFee;
    private BigDecimal subMchtFee;
    private BigDecimal chanFee;
    private String productName;
    private String productDesc;
    private String accType;
    private String bankCardType;
    private String bankCode;
    private String bankName;
    private String bankLineCode;
    private String bankCardNo;
    private String bankCardName;
    private String certType;
    private String certId;
    private String mobile;
    private String creditValid;
    private String creditCvv;
    private String province;
    private String city;
    private String actId;
    private String actName;
    private String payStatus;
    private Date payTime;
    private String channelReturnSeq;
    private String channelBankSeq;
    private String errCode;
    private String errMessage;
    private String returnCode2;
    private String returnMessage2;
    private Date fundSettleTime;
    private Date feeSettleTime;
    private String checkStatus;
    private String operatorId;
    private String accountDetailId;
    private String settleType;
    private String settleFeezeId;
    private String settleUnfeezeId;
    private String settleId;
    private Date settleTime;
    private Date checkTime;
    private Date createDate;
    private Date updateDate;
    private String remark;
    private String extend1;
    private String extend2;
    private String extend3;
    private static final long serialVersionUID = 1L;

    public PlatProxyDetailVo() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatBatchId() {
        return this.platBatchId;
    }

    public void setPlatBatchId(String platBatchId) {
        this.platBatchId = platBatchId;
    }

    public String getMchtBatchId() {
        return this.mchtBatchId;
    }

    public void setMchtBatchId(String mchtBatchId) {
        this.mchtBatchId = mchtBatchId;
    }

    public String getMchtSeq() {
        return this.mchtSeq;
    }

    public void setMchtSeq(String mchtSeq) {
        this.mchtSeq = mchtSeq;
    }

    public String getChannelSeq() {
        return this.channelSeq;
    }

    public void setChannelSeq(String channelSeq) {
        this.channelSeq = channelSeq;
    }

    public String getMchtId() {
        return this.mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getSubMchtId() {
        return this.subMchtId;
    }

    public void setSubMchtId(String subMchtId) {
        this.subMchtId = subMchtId;
    }

    public String getChanMchtPaytypeId() {
        return this.chanMchtPaytypeId;
    }

    public void setChanMchtPaytypeId(String chanMchtPaytypeId) {
        this.chanMchtPaytypeId = chanMchtPaytypeId;
    }

    public String getChanId() {
        return this.chanId;
    }

    public void setChanId(String chanId) {
        this.chanId = chanId;
    }

    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getChanMchtNo() {
        return this.chanMchtNo;
    }

    public void setChanMchtNo(String chanMchtNo) {
        this.chanMchtNo = chanMchtNo;
    }

    public String getGoodId() {
        return this.goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public String getSerialNum() {
        return this.serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getChannelTradeId() {
        return this.channelTradeId;
    }

    public void setChannelTradeId(String channelTradeId) {
        this.channelTradeId = channelTradeId;
    }

    public String getCustBatchNo() {
        return this.custBatchNo;
    }

    public void setCustBatchNo(String custBatchNo) {
        this.custBatchNo = custBatchNo;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMchtFee() {
        return this.mchtFee;
    }

    public void setMchtFee(BigDecimal mchtFee) {
        this.mchtFee = mchtFee;
    }

    public BigDecimal getSubMchtFee() {
        return this.subMchtFee;
    }

    public void setSubMchtFee(BigDecimal subMchtFee) {
        this.subMchtFee = subMchtFee;
    }

    public BigDecimal getChanFee() {
        return this.chanFee;
    }

    public void setChanFee(BigDecimal chanFee) {
        this.chanFee = chanFee;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return this.productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getAccType() {
        return this.accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getBankCardType() {
        return this.bankCardType;
    }

    public void setBankCardType(String bankCardType) {
        this.bankCardType = bankCardType;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankLineCode() {
        return this.bankLineCode;
    }

    public void setBankLineCode(String bankLineCode) {
        this.bankLineCode = bankLineCode;
    }

    public String getBankCardNo() {
        return this.bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBankCardName() {
        return this.bankCardName;
    }

    public void setBankCardName(String bankCardName) {
        this.bankCardName = bankCardName;
    }

    public String getCertType() {
        return this.certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertId() {
        return this.certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreditValid() {
        return this.creditValid;
    }

    public void setCreditValid(String creditValid) {
        this.creditValid = creditValid;
    }

    public String getCreditCvv() {
        return this.creditCvv;
    }

    public void setCreditCvv(String creditCvv) {
        this.creditCvv = creditCvv;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getActId() {
        return this.actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActName() {
        return this.actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getPayStatus() {
        return this.payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public Date getPayTime() {
        return this.payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getChannelReturnSeq() {
        return this.channelReturnSeq;
    }

    public void setChannelReturnSeq(String channelReturnSeq) {
        this.channelReturnSeq = channelReturnSeq;
    }

    public String getChannelBankSeq() {
        return this.channelBankSeq;
    }

    public void setChannelBankSeq(String channelBankSeq) {
        this.channelBankSeq = channelBankSeq;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return this.errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getReturnCode2() {
        return this.returnCode2;
    }

    public void setReturnCode2(String returnCode2) {
        this.returnCode2 = returnCode2;
    }

    public String getReturnMessage2() {
        return this.returnMessage2;
    }

    public void setReturnMessage2(String returnMessage2) {
        this.returnMessage2 = returnMessage2;
    }

    public Date getFundSettleTime() {
        return this.fundSettleTime;
    }

    public void setFundSettleTime(Date fundSettleTime) {
        this.fundSettleTime = fundSettleTime;
    }

    public Date getFeeSettleTime() {
        return this.feeSettleTime;
    }

    public void setFeeSettleTime(Date feeSettleTime) {
        this.feeSettleTime = feeSettleTime;
    }

    public String getCheckStatus() {
        return this.checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getAccountDetailId() {
        return this.accountDetailId;
    }

    public void setAccountDetailId(String accountDetailId) {
        this.accountDetailId = accountDetailId;
    }

    public String getSettleType() {
        return this.settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    public String getSettleFeezeId() {
        return this.settleFeezeId;
    }

    public void setSettleFeezeId(String settleFeezeId) {
        this.settleFeezeId = settleFeezeId;
    }

    public String getSettleUnfeezeId() {
        return this.settleUnfeezeId;
    }

    public void setSettleUnfeezeId(String settleUnfeezeId) {
        this.settleUnfeezeId = settleUnfeezeId;
    }

    public String getSettleId() {
        return this.settleId;
    }

    public void setSettleId(String settleId) {
        this.settleId = settleId;
    }

    public Date getSettleTime() {
        return this.settleTime;
    }

    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
    }

    public Date getCheckTime() {
        return this.checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getExtend1() {
        return this.extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return this.extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return this.extend3;
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

}