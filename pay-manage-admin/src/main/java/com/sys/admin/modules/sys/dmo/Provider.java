package com.sys.admin.modules.sys.dmo;

import java.io.Serializable;
import java.util.Date;

/**
 * 分平台/提供商信息Model类
 */
public class Provider implements Serializable {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 编码
     */
    private Integer providerId;

    /**
     * 名称
     */
    private String providerName;

    /**
     * 对外显示名称
     */
    private String providerDisplayName;

    /**
     * 订单生成ID
     */
    private String tckIdent;

    /**
     * 接口地址
     */
    private String interfaceUrl;

    /**
     * 是否开启
     */
    private String isOpen;

    /**
     * 开通日期
     */
    private Date openDate;

    /**
     * 加密密钥
     */
    private String privateKey;

    /**
     * 支付方式
     */
    private String payType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName == null ? null : providerName.trim();
    }

    public String getProviderDisplayName() {
        return providerDisplayName;
    }

    public void setProviderDisplayName(String providerDisplayName) {
        this.providerDisplayName = providerDisplayName == null ? null : providerDisplayName.trim();
    }

    public String getTckIdent() {
        return tckIdent;
    }

    public void setTckIdent(String tckIdent) {
        this.tckIdent = tckIdent == null ? null : tckIdent.trim();
    }

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl == null ? null : interfaceUrl.trim();
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen == null ? null : isOpen.trim();
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey == null ? null : privateKey.trim();
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }
}