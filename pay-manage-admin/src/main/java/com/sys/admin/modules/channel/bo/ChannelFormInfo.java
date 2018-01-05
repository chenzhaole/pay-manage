package com.sys.admin.modules.channel.bo;

import com.sys.common.enums.ErrorCodeEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: ChannelFormInfo 
 * @Description: 支付通道页面form表单业务实体
 * @author: cheng_fei
 * @date: 2017年8月29日 上午11:40:14
 */
public class ChannelFormInfo {
	private String id;
	private String chanCode;//渠道编号
	private String name;//渠道名称
	private String corpAddr;//公司地址
	private String busiContacts;//业务联系人
	private String techContacts;//技术联系人
	private String busiPhone;//业务电话
	private String techPhone;//技术电话
	private String busiMobile;//业务手机
	private String techMobile;//技术手机
	private String busiEmail;//业务邮箱
	private String techEmail;//技术邮箱
	private String code;//实体转换是否成功
	private Long operatorId;
	private String channelCooType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getChannelCooType() {
		return channelCooType;
	}

	public void setChannelCooType(String channelCooType) {
		this.channelCooType = channelCooType;
	}

	//空的构造方法
	public ChannelFormInfo(){
		
	}
	//将页面请求参数转化成实体构造方法
	public ChannelFormInfo(HttpServletRequest request){
		if(request != null) {
			Enumeration<String> requestKeys =  request.getParameterNames();
			//请求页面表单map
			Map<String,String> requestMap = new HashMap<String,String>();
			//遍历请求参数，封装请求对象
			while(requestKeys.hasMoreElements()){
				String requestKey = requestKeys.nextElement();
				requestMap.put(requestKey, request.getParameter(requestKey));
			}
			this.code =  ErrorCodeEnum.SUCCESS.getCode();
			this.id = requestMap.get("id");
			this.busiContacts = requestMap.get("busiContacts");
			this.busiEmail = requestMap.get("busiEmail");
			this.busiMobile = requestMap.get("busiMobile");
			this.busiPhone = requestMap.get("busiPhone");
			this.chanCode = requestMap.get("chanCode");
			this.corpAddr = requestMap.get("corpAddr");
			this.name = requestMap.get("name");
			this.techContacts = requestMap.get("techContacts");
			this.techEmail = requestMap.get("techEmail");
			this.techMobile = requestMap.get("techMobile");
			this.techPhone = requestMap.get("techPhone");
			this.channelCooType = requestMap.get("channelCooType");
		}
	}

	public String getChanCode() {
		return chanCode;
	}

	public void setChanCode(String chanCode) {
		this.chanCode = chanCode;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCorpAddr() {
		return corpAddr;
	}
	public void setCorpAddr(String corpAddr) {
		this.corpAddr = corpAddr;
	}
	public String getBusiContacts() {
		return busiContacts;
	}
	public void setBusiContacts(String busiContacts) {
		this.busiContacts = busiContacts;
	}
	public String getTechContacts() {
		return techContacts;
	}
	public void setTechContacts(String techContacts) {
		this.techContacts = techContacts;
	}
	public String getBusiPhone() {
		return busiPhone;
	}
	public void setBusiPhone(String busiPhone) {
		this.busiPhone = busiPhone;
	}
	public String getTechPhone() {
		return techPhone;
	}
	public void setTechPhone(String techPhone) {
		this.techPhone = techPhone;
	}
	public String getBusiMobile() {
		return busiMobile;
	}
	public void setBusiMobile(String busiMobile) {
		this.busiMobile = busiMobile;
	}
	public String getTechMobile() {
		return techMobile;
	}
	public void setTechMobile(String techMobile) {
		this.techMobile = techMobile;
	}
	public String getBusiEmail() {
		return busiEmail;
	}
	public void setBusiEmail(String busiEmail) {
		this.busiEmail = busiEmail;
	}
	public String getTechEmail() {
		return techEmail;
	}
	public void setTechEmail(String techEmail) {
		this.techEmail = techEmail;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}
