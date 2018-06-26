package com.sys.admin.common.enums;

import com.sys.common.enums.PayTypeEnum;

/**
 * @Description:只用于运营平台显示支付方式枚举类

 * @author: li_hx
 */
public enum AdminPayTypeEnum {

	//微信--以wx开头
	WX_WAP("wx101", "微信H5支付"),
	WX_APP("wx201", "微信APP支付"),
	WX_PUBLIC_NATIVE("wx301", "微信公众号支付原生方式"),
	WX_PUBLIC_NOT_NATIVE("wx302", "微信公众号支付非原生方式"),
	WX_QRCODE("wx401", "微信扫码支付"),
	WX_BARCODE_PC("wx502","微信付款码包装成PC支付"),
	WX_BARCODE_H5("wx503","微信付款码包装成H5支付"),

	//支付宝--以al开头
	ALIPAY_H5("al101","支付宝H5支付"),
	ALIPAY_ONLINE_SCAN2WAP("al102","支付宝扫码转H5"),
	ALIPAY_ONLINE_QRCODE("al401","支付宝扫码"),

	//苏宁支付--以sn开头
	SUNING_H5("sn101","苏宁H5支付"),
	SUNING_QRCODE("sn401","苏宁扫码"),

	//qq支付--以qq开头

	QQ_SCAN2WAP("qq102", "QQ扫码转H5支付"),
	QQ_QRCODE("qq403", "QQ扫码支付"),



	//京东支付--以jd开头
	JD_WAP("jd101", "京东H5支付"),
	JD_SCAN("jd401", "京东扫码支付"),

	//银联支付--以yl
	UNIONPAY_H5("yl101","银联H5支付"),
	UNIONPAY_QRCODE("yl401","银联扫码"),

	//代付使用--以df开头
	SINGLE_DF("df101", "单笔代付"),

	//快捷支付--以qj开头
	QUICK_PAY ("qj202", "快捷支付(即银行卡支付)"),
	QUICK_ONLINE_BANK ("qj301", "网银/网银退款支付"),
	;

	private String code;
	private String desc;

	private AdminPayTypeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static PayTypeEnum toEnum(String code) {
		for (PayTypeEnum category : PayTypeEnum.values()) {
			if (category.getCode().equals(code)) {
				return category;
			}
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}