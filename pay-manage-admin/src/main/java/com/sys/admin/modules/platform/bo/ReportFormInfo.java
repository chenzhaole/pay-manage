package com.sys.admin.modules.platform.bo;

public class ReportFormInfo {
    private String reportType;
    private String queryDay;
    
    private String payMchtId;
    private String chanId;
    private String serviceMchtId;
    private String declareMchtId;
    private String payType;
    
    private String agentMchtId;
    
    
    public String getReportType() {
        return reportType;
    }
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    public String getQueryDay() {
        return queryDay;
    }
    public void setQueryDay(String queryDay) {
        this.queryDay = queryDay;
    }
    public String getPayMchtId() {
        return payMchtId;
    }
    public void setPayMchtId(String payMchtId) {
        this.payMchtId = payMchtId;
    }
    public String getChanId() {
        return chanId;
    }
    public void setChanId(String chanId) {
        this.chanId = chanId;
    }
    public String getServiceMchtId() {
        return serviceMchtId;
    }
    public void setServiceMchtId(String serviceMchtId) {
        this.serviceMchtId = serviceMchtId;
    }
    public String getDeclareMchtId() {
        return declareMchtId;
    }
    public void setDeclareMchtId(String declareMchtId) {
        this.declareMchtId = declareMchtId;
    }
    public String getPayType() {
        return payType;
    }
    public void setPayType(String payType) {
        this.payType = payType;
    }
    public String getAgentMchtId() {
        return agentMchtId;
    }
    public void setAgentMchtId(String agentMchtId) {
        this.agentMchtId = agentMchtId;
    }
    
}
