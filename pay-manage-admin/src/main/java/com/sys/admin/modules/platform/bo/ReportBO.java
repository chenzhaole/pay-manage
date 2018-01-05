package com.sys.admin.modules.platform.bo;

import java.math.BigDecimal;

public class ReportBO {
    private String tradeDay; //交易日期
    private String payMchtId;   //支付商户ID
    private String declareMchtId; //申报商户ID
    private String serviceMchtId; //服务商户ID
    private String agentMchtId; //代理商户ID
    private String chanId; //通道ID
    private String payType; //支付类型
    private Long paySuccessNum; //支付成功笔数
    private BigDecimal paySuccessAmount; //支付成功金额
    private BigDecimal payFeerate; //支付商户费率
    private BigDecimal payFee; //支付商户手续费
    private String payFeeType;//支付商户手续费类型

    private BigDecimal declareFeerate; //申报商户费率
    private BigDecimal declareFee; //申报商户手续费
    private String declareFeeType;

    private BigDecimal serviceFeerate; //服务商户费率
    private BigDecimal serviceFee; //服务商户手续费
    private String serviceFeeType;//服务商户手续费类型

    private BigDecimal agentFeerate; //代理商户费率
    private BigDecimal agentFee; //代理商户手续费
    private String agentFeeType;//代理商户手续费类型

    private BigDecimal payClearAmount;//实际支付商户结算金额
    private BigDecimal declareClearAmount;//申报商户结算金额
    private BigDecimal agentClearAmount;//代理商分成金额

    private BigDecimal profit; //收益
    
    

    public String getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(String tradeDay) {
        this.tradeDay = tradeDay;
    }

    public String getPayMchtId() {
        return payMchtId;
    }

    public void setPayMchtId(String payMchtId) {
        this.payMchtId = payMchtId;
    }

    public String getDeclareMchtId() {
        return declareMchtId;
    }

    public void setDeclareMchtId(String declareMchtId) {
        this.declareMchtId = declareMchtId;
    }

    public String getServiceMchtId() {
        return serviceMchtId;
    }

    public void setServiceMchtId(String serviceMchtId) {
        this.serviceMchtId = serviceMchtId;
    }

    public String getAgentMchtId() {
        return agentMchtId;
    }

    public void setAgentMchtId(String agentMchtId) {
        this.agentMchtId = agentMchtId;
    }

    public String getChanId() {
        return chanId;
    }

    public void setChanId(String chanId) {
        this.chanId = chanId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Long getPaySuccessNum() {
        return paySuccessNum;
    }

    public void setPaySuccessNum(Long paySuccessNum) {
        this.paySuccessNum = paySuccessNum;
    }

    public BigDecimal getPaySuccessAmount() {
        return paySuccessAmount;
    }

    public void setPaySuccessAmount(BigDecimal paySuccessAmount) {
        this.paySuccessAmount = paySuccessAmount;
    }

    public BigDecimal getPayFeerate() {
        return payFeerate;
    }

    public void setPayFeerate(BigDecimal payFeerate) {
        this.payFeerate = payFeerate;
    }

    public BigDecimal getPayFee() {
        return payFee;
    }

    public void setPayFee(BigDecimal payFee) {
        this.payFee = payFee;
    }

    public BigDecimal getDeclareFeerate() {
        return declareFeerate;
    }

    public void setDeclareFeerate(BigDecimal declareFeerate) {
        this.declareFeerate = declareFeerate;
    }

    public BigDecimal getDeclareFee() {
        return declareFee;
    }

    public void setDeclareFee(BigDecimal declareFee) {
        this.declareFee = declareFee;
    }

    public BigDecimal getServiceFeerate() {
        return serviceFeerate;
    }

    public void setServiceFeerate(BigDecimal serviceFeerate) {
        this.serviceFeerate = serviceFeerate;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getAgentFeerate() {
        return agentFeerate;
    }

    public void setAgentFeerate(BigDecimal agentFeerate) {
        this.agentFeerate = agentFeerate;
    }

    public BigDecimal getAgentFee() {
        return agentFee;
    }

    public void setAgentFee(BigDecimal agentFee) {
        this.agentFee = agentFee;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getPayClearAmount() {
        return payClearAmount;
    }

    public void setPayClearAmount(BigDecimal payClearAmount) {
        this.payClearAmount = payClearAmount;
    }

    public BigDecimal getDeclareClearAmount() {
        return declareClearAmount;
    }

    public void setDeclareClearAmount(BigDecimal declareClearAmount) {
        this.declareClearAmount = declareClearAmount;
    }

    public BigDecimal getAgentClearAmount() {
        return agentClearAmount;
    }

    public void setAgentClearAmount(BigDecimal agentClearAmount) {
        this.agentClearAmount = agentClearAmount;
    }

    public String getPayFeeType() {
        return payFeeType;
    }

    public void setPayFeeType(String payFeeType) {
        this.payFeeType = payFeeType;
    }

    public String getDeclareFeeType() {
        return declareFeeType;
    }

    public void setDeclareFeeType(String declareFeeType) {
        this.declareFeeType = declareFeeType;
    }

    public String getServiceFeeType() {
        return serviceFeeType;
    }

    public void setServiceFeeType(String serviceFeeType) {
        this.serviceFeeType = serviceFeeType;
    }

    public String getAgentFeeType() {
        return agentFeeType;
    }

    public void setAgentFeeType(String agentFeeType) {
        this.agentFeeType = agentFeeType;
    }
}
