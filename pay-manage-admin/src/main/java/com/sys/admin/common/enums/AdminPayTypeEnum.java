package com.sys.admin.common.enums;

import com.sys.common.enums.PayTypeEnum;

/**
 * @Description:只用于运营平台显示支付方式枚举类

 * @author: li_hx
 */
public enum AdminPayTypeEnum {

	//微信--以wx开头
	WX_GROUP("wx000", "微信组合支付类型"),
	WX_WAP("wx101", "微信H5支付"),
	WX_APP("wx201", "微信APP支付"),
	WX_PUBLIC_NATIVE("wx301", "微信公众号支付原生方式"),
	WX_PUBLIC_NOT_NATIVE("wx302", "微信公众号支付非原生方式"),
	WX_QRCODE("wx401", "微信扫码支付"),
	WX_BARCODE("wx501", "微信付款码支付"),
	WX_BARCODE_PC("wx502","微信付款码包装成PC支付"),
	WX_BARCODE_H5("wx503","微信付款码包装成H5支付"),
	WX_GM("wxgm001","微信固码"),

	//支付宝--以al开头
	ALIPAY_GROUP("al000","支付宝组合支付类型"),
	ALIPAY_H5("al101","支付宝H5支付"),
	ALIPAY_ONLINE_SCAN2WAP("al102","支付宝扫码转H5"),
	ALIPAY_APP( "al201", "支付宝APP"),
	ALIPAY_ONLINE_QRCODE("al401","支付宝扫码"),
	ALIPAY_BARCODE("al501", "支付宝付款码支付"),


	//苏宁支付--以sn开头
	SUNING_GROUP("sn000","苏宁组合支付类型"),
	SUNING_H5("sn101","苏宁H5支付"),
	SUNING_QRCODE("sn401","苏宁扫码"),
	SUNING_BARCODE("sn501","苏宁付款码支付"),

	//qq支付--以qq开头
	QQ_GROUP("qq000", "QQ组合支付类型"),
	QQ_WAP("qq101", "QQh5支付"),
	QQ_SCAN2WAP("qq102", "QQ扫码转H5支付"),
	QQ_QRCODE("qq403", "QQ扫码支付"),
	QQ_BARCODE("qq501", "QQ付款码支付"),



	//京东支付--以jd开头
	JD_GROUP("jd000", "京东组合支付类型"),
	JD_WAP("jd101", "京东H5支付"),
	JD_SCAN("jd401", "京东扫码支付"),
	JD_BARCODE("jd501", "京东付款码支付"),

	//银联支付--以yl
	UNIONPAY_GROUP("yl000","银联组合支付类型"),
	UNIONPAY_H5("yl101","银联H5支付"),
	UNIONPAY_QRCODE("yl401","银联扫码"),
	UNIONPAY_BARCODE("yl501","银联付款码支付"),

	//代付使用--以df开头
	SINGLE_DF("df101", "单笔代付"),

	//快捷支付--以qj开头
	QUICK_GROUP ("qj000", "银行卡组合支付类型"),
	QUICK_PAY ("qj202", "快捷支付(即银行卡支付)"),
	QUICK_ONLINE_BANK ("qj301", "网银/网银退款支付"),


	SDK_GROUP("sdk000","sdk组合支付类型"),
	CHONGZHI_WG ( "cz001", "充值"),
	HUIKUANG_WG ( "hk001", "汇款"),


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
