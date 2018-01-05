package com.sys.admin.modules.sys.dmo;

/**
 * 系统参数分类
 */
public enum SysConfigCategoryEnum {
    NO_GENERATE("no_generate", "编号生成"),
    TIMER("timer", "定时器");


    /**
     * 值
     */
    private String value;

    /**
     * 描述
     */
    private String description;


    SysConfigCategoryEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }
}
