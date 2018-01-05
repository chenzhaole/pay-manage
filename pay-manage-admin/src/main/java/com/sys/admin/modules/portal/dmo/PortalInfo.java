package com.sys.admin.modules.portal.dmo;

import java.io.Serializable;

public class PortalInfo implements Serializable {
    /**
     * 门户所属机构ID
     */
    private Long officeId;

    /**
     * 类型  0-车站 1-运输公司 2-省集团 3-交运集团
     */
    private String type;

    /**
     * 状态 0-正常 1-冻结
     */
    private String status;

    /**
     * 企业LOGO URL地址
     */
    private String companyLogo;

    /**
     * 企业ICON URL地址
     */
    private String companyIcon;

    /**
     * 域名地址
     */
    private String domainAddress;

    /**
     * 客服电话
     */
    private String customerServicePhone;

    /**
     * 客服email邮箱
     */
    private String customerServiceEmail;

    /**
     * 短信模板编号
     */
    private String smsTemplateId;

    /**
     * 联合登录账号
     */
    private String jointAccountLogin;

    /**
     * 财务账户信息
     */
    private String financialAccountInfo;

    /**
     * 业务招商信息
     */
    private String businessInvestmentInfo;

    /**
     * 微信公众号
     */
    private String wechatAccount;

    /**
     * 微信公众号二维码图片
     */
    private String wechatBarcode;

    /**
     * 星级
     */
    private String starLevel;

    /**
     * 公司简介
     */
    private String description;

    /**
     * 关于我们
     */
    private String about;

    /**
     * 备注
     */
    private String remarks;

    private static final long serialVersionUID = 1L;

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo == null ? null : companyLogo.trim();
    }

    public String getCompanyIcon() {
        return companyIcon;
    }

    public void setCompanyIcon(String companyIcon) {
        this.companyIcon = companyIcon == null ? null : companyIcon.trim();
    }

    public String getDomainAddress() {
        return domainAddress;
    }

    public void setDomainAddress(String domainAddress) {
        this.domainAddress = domainAddress == null ? null : domainAddress.trim();
    }

    public String getCustomerServicePhone() {
        return customerServicePhone;
    }

    public void setCustomerServicePhone(String customerServicePhone) {
        this.customerServicePhone = customerServicePhone == null ? null : customerServicePhone.trim();
    }

    public String getCustomerServiceEmail() {
        return customerServiceEmail;
    }

    public void setCustomerServiceEmail(String customerServiceEmail) {
        this.customerServiceEmail = customerServiceEmail == null ? null : customerServiceEmail.trim();
    }

    public String getSmsTemplateId() {
        return smsTemplateId;
    }

    public void setSmsTemplateId(String smsTemplateId) {
        this.smsTemplateId = smsTemplateId == null ? null : smsTemplateId.trim();
    }

    public String getJointAccountLogin() {
        return jointAccountLogin;
    }

    public void setJointAccountLogin(String jointAccountLogin) {
        this.jointAccountLogin = jointAccountLogin == null ? null : jointAccountLogin.trim();
    }

    public String getFinancialAccountInfo() {
        return financialAccountInfo;
    }

    public void setFinancialAccountInfo(String financialAccountInfo) {
        this.financialAccountInfo = financialAccountInfo == null ? null : financialAccountInfo.trim();
    }

    public String getBusinessInvestmentInfo() {
        return businessInvestmentInfo;
    }

    public void setBusinessInvestmentInfo(String businessInvestmentInfo) {
        this.businessInvestmentInfo = businessInvestmentInfo == null ? null : businessInvestmentInfo.trim();
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount == null ? null : wechatAccount.trim();
    }

    public String getWechatBarcode() {
        return wechatBarcode;
    }

    public void setWechatBarcode(String wechatBarcode) {
        this.wechatBarcode = wechatBarcode == null ? null : wechatBarcode.trim();
    }

    public String getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(String starLevel) {
        this.starLevel = starLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PortalInfo other = (PortalInfo) that;
        return (this.getOfficeId() == null ? other.getOfficeId() == null : this.getOfficeId().equals(other.getOfficeId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCompanyLogo() == null ? other.getCompanyLogo() == null : this.getCompanyLogo().equals(other.getCompanyLogo()))
            && (this.getCompanyIcon() == null ? other.getCompanyIcon() == null : this.getCompanyIcon().equals(other.getCompanyIcon()))
            && (this.getDomainAddress() == null ? other.getDomainAddress() == null : this.getDomainAddress().equals(other.getDomainAddress()))
            && (this.getCustomerServicePhone() == null ? other.getCustomerServicePhone() == null : this.getCustomerServicePhone().equals(other.getCustomerServicePhone()))
            && (this.getCustomerServiceEmail() == null ? other.getCustomerServiceEmail() == null : this.getCustomerServiceEmail().equals(other.getCustomerServiceEmail()))
            && (this.getSmsTemplateId() == null ? other.getSmsTemplateId() == null : this.getSmsTemplateId().equals(other.getSmsTemplateId()))
            && (this.getJointAccountLogin() == null ? other.getJointAccountLogin() == null : this.getJointAccountLogin().equals(other.getJointAccountLogin()))
            && (this.getFinancialAccountInfo() == null ? other.getFinancialAccountInfo() == null : this.getFinancialAccountInfo().equals(other.getFinancialAccountInfo()))
            && (this.getBusinessInvestmentInfo() == null ? other.getBusinessInvestmentInfo() == null : this.getBusinessInvestmentInfo().equals(other.getBusinessInvestmentInfo()))
            && (this.getWechatAccount() == null ? other.getWechatAccount() == null : this.getWechatAccount().equals(other.getWechatAccount()))
            && (this.getWechatBarcode() == null ? other.getWechatBarcode() == null : this.getWechatBarcode().equals(other.getWechatBarcode()))
            && (this.getRemarks() == null ? other.getRemarks() == null : this.getRemarks().equals(other.getRemarks()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOfficeId() == null) ? 0 : getOfficeId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCompanyLogo() == null) ? 0 : getCompanyLogo().hashCode());
        result = prime * result + ((getCompanyIcon() == null) ? 0 : getCompanyIcon().hashCode());
        result = prime * result + ((getDomainAddress() == null) ? 0 : getDomainAddress().hashCode());
        result = prime * result + ((getCustomerServicePhone() == null) ? 0 : getCustomerServicePhone().hashCode());
        result = prime * result + ((getCustomerServiceEmail() == null) ? 0 : getCustomerServiceEmail().hashCode());
        result = prime * result + ((getSmsTemplateId() == null) ? 0 : getSmsTemplateId().hashCode());
        result = prime * result + ((getJointAccountLogin() == null) ? 0 : getJointAccountLogin().hashCode());
        result = prime * result + ((getFinancialAccountInfo() == null) ? 0 : getFinancialAccountInfo().hashCode());
        result = prime * result + ((getBusinessInvestmentInfo() == null) ? 0 : getBusinessInvestmentInfo().hashCode());
        result = prime * result + ((getWechatAccount() == null) ? 0 : getWechatAccount().hashCode());
        result = prime * result + ((getWechatBarcode() == null) ? 0 : getWechatBarcode().hashCode());
        result = prime * result + ((getRemarks() == null) ? 0 : getRemarks().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", officeId=").append(officeId);
        sb.append(", type=").append(type);
        sb.append(", status=").append(status);
        sb.append(", companyLogo=").append(companyLogo);
        sb.append(", companyIcon=").append(companyIcon);
        sb.append(", domainAddress=").append(domainAddress);
        sb.append(", customerServicePhone=").append(customerServicePhone);
        sb.append(", customerServiceEmail=").append(customerServiceEmail);
        sb.append(", smsTemplateId=").append(smsTemplateId);
        sb.append(", jointAccountLogin=").append(jointAccountLogin);
        sb.append(", financialAccountInfo=").append(financialAccountInfo);
        sb.append(", businessInvestmentInfo=").append(businessInvestmentInfo);
        sb.append(", wechatAccount=").append(wechatAccount);
        sb.append(", wechatBarcode=").append(wechatBarcode);
        sb.append(", remarks=").append(remarks);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}