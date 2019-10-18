package com.sys.admin.common.enums;

import com.sys.common.enums.PayTypeEnum;

/**
 * @Description:只用于运营平台显示支付方式枚举类

 * @author: li_hx
 */
public enum AdminPayTypeEnum {

	//微信--以wx开头
	WX_GROUP("wx000", "微信组合(wx000)"),
	WX_WAP("wx101", "微信H5(wx101)"),
//	WX_APP("wx201", "微信APP支付"),
	WX_PUBLIC_NATIVE("wx301", "微信公众号(wx301)"),
//	WX_PUBLIC_NOT_NATIVE("wx302", "微信公众号非原生"),
	WX_QRCODE("wx401", "微信扫码(wx401)"),
	WX_BARCODE("wx501", "微信付款码(wx501)"),
//	WX_BARCODE_PC("wx502","微信付款码包装PC"),
//	WX_BARCODE_H5("wx503","微信付款码包装H5"),
	WX_GM("wxgm001","微信固码(wxgm001)"),


	//支付宝--以al开头
	ALIPAY_GROUP("al000","支付宝组合(al000)"),
	ALIPAY_H5("al101","支付宝H5(al101)"),
	ALIPAY_ONLINE_SCAN2WAP("al102","支付宝扫码转H5(al102)"),
//	ALIPAY_APP( "al201", "支付宝APP"),
	ALIPAY_SERVICE_WINDOW("al301", "支付宝服务窗(al301)"),
	ALIPAY_ONLINE_QRCODE("al401","支付宝扫码(al401)"),
	ALIPAY_BARCODE("al501", "支付宝付款码(al501)"),


	//苏宁支付--以sn开头
	SUNING_GROUP("sn000","苏宁组合(sn000)"),
	SUNING_H5("sn101","苏宁H5支付(sn101)"),
	SUNING_QRCODE("sn401","苏宁扫码(sn401)"),
	SUNING_BARCODE("sn501","苏宁付款码支付(sn501)"),


	//qq支付--以qq开头
	QQ_GROUP("qq000", "QQ组合(qq000)"),
	QQ_WAP("qq101", "QQH5(qq101)"),
//	QQ_SCAN2WAP("qq102", "QQ扫码转H5"),
	QQ_QRCODE("qq403", "QQ扫码(qq403)"),
	QQ_BARCODE("qq501", "QQ付款码(qq501)"),


	//京东支付--以jd开头
	JD_GROUP("jd000", "京东组合(jd000)"),
	JD_WAP("jd101", "京东H5(jd101)"),
	JD_SCAN("jd401", "京东扫码(jd401)"),
	JD_BARCODE("jd501", "京东付款码(jd501)"),

	//银联支付--以yl
	UNIONPAY_GROUP("yl000","银联组合(yl000)"),
	UNIONPAY_H5("yl101","银联H5(yl101)"),
	UNIONPAY_QRCODE("yl401","银联扫码(yl401)"),
	UNIONPAY_BARCODE("yl501","银联付款码(yl501)"),

	//代付使用--以df开头
	SINGLE_DF("df101", "单笔代付(df101)"),

	//快捷支付--以qj开头
	QUICK_GROUP ("qj000", "银行卡组合支付()"),
	QUICK_PAY ("qj202", "快捷支付(即银行卡)(qj202)"),
	QUICK_ONLINE_BANK ("qj301", "网关支付(qj301)"),
	QUICK_REAL_AUTH ("qj501", "鉴权实名认证(qj501)"),
	LOCAL_BANK("qj601","网银支付(qj601)"),

	PAY_REFUND("zf201", "退款(zf201)"),
//	PAY_CANCEL("zf201", "取消(zf201)"),

	//商户入驻--rg开头
	REGISTE_MCHT("rm101", "入驻商户信息(rm101)"),
	REGISTE_QUERY("rm102", "入驻信息查询(rm102)"),
	REGISTE_UPLOAD("rm103", "入驻文件上传(rm103)")

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
