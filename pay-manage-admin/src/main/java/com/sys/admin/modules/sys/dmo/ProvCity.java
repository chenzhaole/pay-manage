package com.sys.admin.modules.sys.dmo;

/**
 * 省-市信息Model类
 */
public class ProvCity {
    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 省编码
     */
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市编码
     */
    private Integer cityId;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区编码
     */
    private Integer countyId;

    /**
     * 区名称
     */
    private String countyName;

    /**
     * 市名称简拼
     */
    private String pinyinPrefix;

    /**
     * 市名称全拼
     */
    private String pinyin;

    /**
     * 分平台/提供商标识
     */
    private Integer providerId;

    /**
     * 是否推荐/热门
     */
    private String isRecommend;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 经度
     */
    private String lon;
    /**
     * 高德经度
     */
    private String lonGd;
    /**
     * 高德纬度
     */
    private String latGd;
    
    
    
    public String getLonGd() {
		return lonGd;
	}

	public void setLonGd(String lonGd) {
		this.lonGd = lonGd;
	}

	public String getLatGd() {
		return latGd;
	}

	public void setLatGd(String latGd) {
		this.latGd = latGd;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName == null ? null : provinceName.trim();
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getPinyinPrefix() {
        return pinyinPrefix;
    }

    public void setPinyinPrefix(String pinyinPrefix) {
        this.pinyinPrefix = pinyinPrefix == null ? null : pinyinPrefix.trim();
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}