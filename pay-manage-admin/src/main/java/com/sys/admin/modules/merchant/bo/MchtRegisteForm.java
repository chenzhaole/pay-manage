package com.sys.admin.modules.merchant.bo;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MerchantForm
 * @Description: 商户入住信息表单实体类
 * @author: ALI
 * @date: 2018年1月10日 上午11:28:31
 */
public class MchtRegisteForm {

	private String id;

	private String mchtCode;

	private String mchtBankCardId;

	private String chanMchtPaytypeId;

	private String status;

	private Integer isDelete;

	private Date createTime;

	private Date updateTime;

	private String operatorId;

	private String extend1;

	private String extend2;

	private String extend3;

	private String mchtName;

	private String phone;

	private String chanName;

	private String paytype;

	private String mchtOrderId;
	private String platOrderId;
	private String chanOrderId;
	private String chan2PlatResCode;
	private String chan2PlatResMsg;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMchtCode() {
		return mchtCode;
	}

	public void setMchtCode(String mchtCode) {
		this.mchtCode = mchtCode;
	}

	public String getMchtName() {
		return mchtName;
	}

	public void setMchtName(String mchtName) {
		this.mchtName = mchtName;
	}

	public String getMchtBankCardId() {
		return mchtBankCardId;
	}

	public void setMchtBankCardId(String mchtBankCardId) {
		this.mchtBankCardId = mchtBankCardId;
	}

	public String getChanMchtPaytypeId() {
		return chanMchtPaytypeId;
	}

	public void setChanMchtPaytypeId(String chanMchtPaytypeId) {
		this.chanMchtPaytypeId = chanMchtPaytypeId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getChanName() {
		return chanName;
	}

	public void setChanName(String chanName) {
		this.chanName = chanName;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public MchtRegisteForm() {
	}

	//根据页面表单请求转换成bo实体
	public MchtRegisteForm(HttpServletRequest request) {
		Enumeration<String> requestKeys = request.getParameterNames();
		//定义请求表单map
		Map<String, String> requestMap = new HashMap<String, String>();
		//遍历请求参数列表，封装map对象
		while (requestKeys.hasMoreElements()) {
			String requestKey = requestKeys.nextElement();
			requestMap.put(requestKey, request.getParameter(requestKey));
		}
		this.id = requestMap.get("id");

	}

	public String getMchtOrderId() {
		return mchtOrderId;
	}

	public void setMchtOrderId(String mchtOrderId) {
		this.mchtOrderId = mchtOrderId;
	}

	public String getPlatOrderId() {
		return platOrderId;
	}

	public void setPlatOrderId(String platOrderId) {
		this.platOrderId = platOrderId;
	}

	public String getChan2PlatResCode() {
		return chan2PlatResCode;
	}

	public void setChan2PlatResCode(String chan2PlatResCode) {
		this.chan2PlatResCode = chan2PlatResCode;
	}

	public String getChan2PlatResMsg() {
		return chan2PlatResMsg;
	}

	public void setChan2PlatResMsg(String chan2PlatResMsg) {
		this.chan2PlatResMsg = chan2PlatResMsg;
	}

	public String getChanOrderId() {
		return chanOrderId;
	}

	public void setChanOrderId(String chanOrderId) {
		this.chanOrderId = chanOrderId;
	}
}
