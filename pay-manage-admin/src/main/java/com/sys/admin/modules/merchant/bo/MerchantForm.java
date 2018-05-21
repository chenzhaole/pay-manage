package com.sys.admin.modules.merchant.bo;

import com.sys.common.enums.ErrorCodeEnum;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: MerchantForm 
 * @Description: 商户信息表单实体类
 * @author: ALI
 * @date: 2017年10月10日 上午11:28:31
 */
public class MerchantForm {

	private String unionCastId;

	private String synNotifyUrl;

	private String asynNotifyUrl;

	private String businessLicenseCode;

	private Date blcStartDate;

	private Date blcEndDate;

	private String organizationCode;

	private Date ocStartDate;

	private Date ocEndDate;

	private String taxRegistCode;

	private String financeContractName;

	private String financeMobile;

	private String financePhone;

	private String financeEmail;

	private String icpNo;

	private String contractNo;

	private String contractType;

	private Date contractBeginDate;

	private Date contractEndDate;

	private String fundSettleType;

	private String fundSettleMode;

	private String fundSettleRange;

	private Boolean fundSettleDay;

	private String fundSettleHolidays;

	private BigDecimal fundMinSettleMoney;


	private String feeSettleType;

	private String feeSettleMode;

	private String feeSettleRange;

	private Boolean feeSettleDay;

	private String feeSettleHolidays;

	private BigDecimal feeMinSettleMoney;

	private String feeSettleAccountName;

	private String feeSettleBankCard;

	private String feeSettleBankName;

	private String feeSettleProvince;

	private String feeSettleCity;

	private String description;

	private String registerEmail;

	private String isSendmail;

	private Date createDate;

	private Date updateDate;

	private String extend1;

	private String extend2;

	private String extend3;

	private String id;
	private String parentId; //上级商户
	private String name;
	private String mchtCode;
	private String shortName;
	private String mchtType;
	private String signType;
	private String companyAdr;
	private String companyPhone;
	private String businessScope;
	private String bizType;
	private String salemanName;
	private String companyType;
	private String contractName;
	private String phone;
	private String email;
	private String servicePhone;
	private String serviceQq;
	private String serviceWx;
	private String serviceContractName;
	private String serviceMobile;
	private String website;
	private String legalPerson;
	private String legalCardType;
	private String legalCardNo;
	private String mobile;

	private String clientIp;
	private String mchtKey;

	private String contactName;

	//银行信息
	private String fundSettleBankName;
	private String fundSettleBankCard;
	private String fundSettleAccountName;
	private String fundSettleProvince;
	private String fundSettleCity;
	private String fundSettleSubbranchName; //支行
	private String fundSettleLinkBankNo; //联行号
	private String fundSettleAccountType; //账户类型

	private Integer isProductControl;
	private Long operatorId;
	private String operatorName;
	private String code;//是否转换成功

	private String city;//省市区

	private String blcPath; //营业执照

	private String taxRegisPath; //税务登记表
	private String organizationPath; //组织机构代码证
	private String boardPicPath; //门牌照/其他
	private String bankIdcardPath; //银行账户身份证
	private String openingPermitPath; //开户许可证
	private String bankCardFrontPath; //银行卡正面照
	private String contractFilePath; //商户协议

	private String status;

	
	public MerchantForm() {
	}

	//根据页面表单请求转换成bo实体
	public MerchantForm(HttpServletRequest request){
		Enumeration<String> requestKeys = request.getParameterNames();
		//定义请求表单map
		Map<String,String> requestMap = new HashMap<String,String>();
		//遍历请求参数列表，封装map对象
		while(requestKeys.hasMoreElements()){
			String requestKey = requestKeys.nextElement();
			requestMap.put(requestKey, request.getParameter(requestKey));
		}
		this.code = ErrorCodeEnum.SUCCESS.getCode();
		this.id = requestMap.get("id");
		this.parentId = requestMap.get("parentId");
		this.bizType = requestMap.get("bizType");
		this.businessScope = requestMap.get("businessScope");
		this.companyAdr = requestMap.get("companyAdr");
		this.companyPhone = requestMap.get("companyPhone");
		this.companyType = requestMap.get("companyType");
		this.contractName = requestMap.get("contractName");
		this.email = requestMap.get("email");

		this.legalCardNo = requestMap.get("legalCardNo");
		this.legalCardType = requestMap.get("legalCardType");
		this.legalPerson = requestMap.get("legalPerson");
		this.mobile = requestMap.get("mobile");
		this.name = requestMap.get("name");
		this.phone = requestMap.get("phone");
		this.salemanName = requestMap.get("salemanName");
		this.serviceContractName = requestMap.get("serviceContractName");
		this.serviceMobile = requestMap.get("serviceMobile");
		this.servicePhone = requestMap.get("servicePhone");
		this.serviceQq = requestMap.get("serviceQq");
		this.serviceWx = requestMap.get("serviceWx");
		this.shortName = requestMap.get("shortName");
		this.website = requestMap.get("website");

		this.contactName = requestMap.get("contactName");

		this.fundSettleAccountName = requestMap.get("fundSettleAccountName");
		this.fundSettleBankCard = requestMap.get("fundSettleBankCard");
		this.fundSettleBankName = requestMap.get("fundSettleBankName");
		this.fundSettleProvince = requestMap.get("fundSettleProvince");
		this.fundSettleCity = requestMap.get("fundSettleCity");
		this.fundSettleSubbranchName = requestMap.get("fundSettleSubbranchName");
		this.fundSettleLinkBankNo = requestMap.get("fundSettleLinkBankNo");
		this.fundSettleAccountType = requestMap.get("fundSettleAccountType");

		this.city = requestMap.get("city");
		this.blcPath = requestMap.get("blcPath");
		this.taxRegisPath = requestMap.get("taxRegisPath");
		this.organizationPath = requestMap.get("organizationPath");
		this.boardPicPath = requestMap.get("boardPicPath");
		this.bankIdcardPath = requestMap.get("bankIdcardPath");
		this.openingPermitPath = requestMap.get("openingPermitPath");
		this.bankCardFrontPath = requestMap.get("bankCardFrontPath");
		this.contractFilePath = requestMap.get("contractFilePath");

		this.status = requestMap.get("status");

		this.mchtType = requestMap.get("mchtType");

		this.clientIp = requestMap.get("clientIp");
		this.mchtKey = requestMap.get("mchtKey");

		this.isProductControl = Integer.valueOf(requestMap.get("isProductControl"));

		String[] signTypes = request.getParameterValues("signType");

		if (signTypes != null && signTypes.length > 0){
			StringBuffer signTypeTemp = new StringBuffer();
			for (String signType : signTypes) {
				signTypeTemp.append(signType);
				signTypeTemp.append(",");
			}
			String signType = signTypeTemp.toString();
			this.signType = signType.substring(0, signType.length() - 1);
		}

	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getMchtType() {
		return mchtType;
	}
	public void setMchtType(String mchtType) {
		this.mchtType = mchtType;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getCompanyAdr() {
		return companyAdr;
	}
	public void setCompanyAdr(String companyAdr) {
		this.companyAdr = companyAdr;
	}
	public String getCompanyPhone() {
		return companyPhone;
	}
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	public String getBusinessScope() {
		return businessScope;
	}
	public void setBusinessScope(String businessScope) {
		this.businessScope = businessScope;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public String getSalemanName() {
		return salemanName;
	}
	public void setSalemanName(String salemanName) {
		this.salemanName = salemanName;
	}
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getServicePhone() {
		return servicePhone;
	}
	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone;
	}
	public String getServiceQq() {
		return serviceQq;
	}
	public void setServiceQq(String serviceQq) {
		this.serviceQq = serviceQq;
	}
	public String getServiceWx() {
		return serviceWx;
	}
	public void setServiceWx(String serviceWx) {
		this.serviceWx = serviceWx;
	}
	public String getServiceContractName() {
		return serviceContractName;
	}
	public void setServiceContractName(String serviceContractName) {
		this.serviceContractName = serviceContractName;
	}
	public String getServiceMobile() {
		return serviceMobile;
	}
	public void setServiceMobile(String serviceMobile) {
		this.serviceMobile = serviceMobile;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getLegalPerson() {
		return legalPerson;
	}
	public void setLegalPerson(String legalPerson) {
		this.legalPerson = legalPerson;
	}
	public String getLegalCardType() {
		return legalCardType;
	}
	public void setLegalCardType(String legalCardType) {
		this.legalCardType = legalCardType;
	}
	public String getLegalCardNo() {
		return legalCardNo;
	}
	public void setLegalCardNo(String legalCardNo) {
		this.legalCardNo = legalCardNo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFundSettleBankName() {
		return fundSettleBankName;
	}
	public void setFundSettleBankName(String fundSettleBankName) {
		this.fundSettleBankName = fundSettleBankName;
	}
	public String getFundSettleBankCard() {
		return fundSettleBankCard;
	}
	public void setFundSettleBankCard(String fundSettleBankCard) {
		this.fundSettleBankCard = fundSettleBankCard;
	}
	public String getFundSettleAccountName() {
		return fundSettleAccountName;
	}
	public void setFundSettleAccountName(String fundSettleAccountName) {
		this.fundSettleAccountName = fundSettleAccountName;
	}
	public Integer getIsProductControl() {
		return isProductControl;
	}
	public void setIsProductControl(Integer isProductControl) {
		this.isProductControl = isProductControl;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getMchtCode() {
		return mchtCode;
	}

	public void setMchtCode(String mchtCode) {
		this.mchtCode = mchtCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBlcPath() {
		return blcPath;
	}

	public void setBlcPath(String blcPath) {
		this.blcPath = blcPath;
	}

	public String getTaxRegisPath() {
		return taxRegisPath;
	}

	public void setTaxRegisPath(String taxRegisPath) {
		this.taxRegisPath = taxRegisPath;
	}

	public String getOrganizationPath() {
		return organizationPath;
	}

	public void setOrganizationPath(String organizationPath) {
		this.organizationPath = organizationPath;
	}

	public String getBoardPicPath() {
		return boardPicPath;
	}

	public void setBoardPicPath(String boardPicPath) {
		this.boardPicPath = boardPicPath;
	}

	public String getBankIdcardPath() {
		return bankIdcardPath;
	}

	public void setBankIdcardPath(String bankIdcardPath) {
		this.bankIdcardPath = bankIdcardPath;
	}

	public String getOpeningPermitPath() {
		return openingPermitPath;
	}

	public void setOpeningPermitPath(String openingPermitPath) {
		this.openingPermitPath = openingPermitPath;
	}

	public String getBankCardFrontPath() {
		return bankCardFrontPath;
	}

	public void setBankCardFrontPath(String bankCardFrontPath) {
		this.bankCardFrontPath = bankCardFrontPath;
	}

	public String getContractFilePath() {
		return contractFilePath;
	}

	public void setContractFilePath(String contractFilePath) {
		this.contractFilePath = contractFilePath;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFundSettleProvince() {
		return fundSettleProvince;
	}

	public void setFundSettleProvince(String fundSettleProvince) {
		this.fundSettleProvince = fundSettleProvince;
	}

	public String getFundSettleCity() {
		return fundSettleCity;
	}

	public void setFundSettleCity(String fundSettleCity) {
		this.fundSettleCity = fundSettleCity;
	}

	public String getFundSettleSubbranchName() {
		return fundSettleSubbranchName;
	}

	public void setFundSettleSubbranchName(String fundSettleSubbranchName) {
		this.fundSettleSubbranchName = fundSettleSubbranchName;
	}

	public String getFundSettleLinkBankNo() {
		return fundSettleLinkBankNo;
	}

	public void setFundSettleLinkBankNo(String fundSettleLinkBankNo) {
		this.fundSettleLinkBankNo = fundSettleLinkBankNo;
	}

	public String getFundSettleAccountType() {
		return fundSettleAccountType;
	}

	public void setFundSettleAccountType(String fundSettleAccountType) {
		this.fundSettleAccountType = fundSettleAccountType;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getMchtKey() {
		return mchtKey;
	}

	public void setMchtKey(String mchtKey) {
		this.mchtKey = mchtKey;
	}

	public String getUnionCastId() {
		return unionCastId;
	}

	public void setUnionCastId(String unionCastId) {
		this.unionCastId = unionCastId;
	}

	public String getSynNotifyUrl() {
		return synNotifyUrl;
	}

	public void setSynNotifyUrl(String synNotifyUrl) {
		this.synNotifyUrl = synNotifyUrl;
	}

	public String getAsynNotifyUrl() {
		return asynNotifyUrl;
	}

	public void setAsynNotifyUrl(String asynNotifyUrl) {
		this.asynNotifyUrl = asynNotifyUrl;
	}

	public String getBusinessLicenseCode() {
		return businessLicenseCode;
	}

	public void setBusinessLicenseCode(String businessLicenseCode) {
		this.businessLicenseCode = businessLicenseCode;
	}

	public Date getBlcStartDate() {
		return blcStartDate;
	}

	public void setBlcStartDate(Date blcStartDate) {
		this.blcStartDate = blcStartDate;
	}

	public Date getBlcEndDate() {
		return blcEndDate;
	}

	public void setBlcEndDate(Date blcEndDate) {
		this.blcEndDate = blcEndDate;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public Date getOcStartDate() {
		return ocStartDate;
	}

	public void setOcStartDate(Date ocStartDate) {
		this.ocStartDate = ocStartDate;
	}

	public Date getOcEndDate() {
		return ocEndDate;
	}

	public void setOcEndDate(Date ocEndDate) {
		this.ocEndDate = ocEndDate;
	}

	public String getTaxRegistCode() {
		return taxRegistCode;
	}

	public void setTaxRegistCode(String taxRegistCode) {
		this.taxRegistCode = taxRegistCode;
	}

	public String getFinanceContractName() {
		return financeContractName;
	}

	public void setFinanceContractName(String financeContractName) {
		this.financeContractName = financeContractName;
	}

	public String getFinanceMobile() {
		return financeMobile;
	}

	public void setFinanceMobile(String financeMobile) {
		this.financeMobile = financeMobile;
	}

	public String getFinancePhone() {
		return financePhone;
	}

	public void setFinancePhone(String financePhone) {
		this.financePhone = financePhone;
	}

	public String getFinanceEmail() {
		return financeEmail;
	}

	public void setFinanceEmail(String financeEmail) {
		this.financeEmail = financeEmail;
	}

	public String getIcpNo() {
		return icpNo;
	}

	public void setIcpNo(String icpNo) {
		this.icpNo = icpNo;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public Date getContractBeginDate() {
		return contractBeginDate;
	}

	public void setContractBeginDate(Date contractBeginDate) {
		this.contractBeginDate = contractBeginDate;
	}

	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	public String getFundSettleType() {
		return fundSettleType;
	}

	public void setFundSettleType(String fundSettleType) {
		this.fundSettleType = fundSettleType;
	}

	public String getFundSettleMode() {
		return fundSettleMode;
	}

	public void setFundSettleMode(String fundSettleMode) {
		this.fundSettleMode = fundSettleMode;
	}

	public String getFundSettleRange() {
		return fundSettleRange;
	}

	public void setFundSettleRange(String fundSettleRange) {
		this.fundSettleRange = fundSettleRange;
	}

	public Boolean getFundSettleDay() {
		return fundSettleDay;
	}

	public void setFundSettleDay(Boolean fundSettleDay) {
		this.fundSettleDay = fundSettleDay;
	}

	public String getFundSettleHolidays() {
		return fundSettleHolidays;
	}

	public void setFundSettleHolidays(String fundSettleHolidays) {
		this.fundSettleHolidays = fundSettleHolidays;
	}

	public BigDecimal getFundMinSettleMoney() {
		return fundMinSettleMoney;
	}

	public void setFundMinSettleMoney(BigDecimal fundMinSettleMoney) {
		this.fundMinSettleMoney = fundMinSettleMoney;
	}

	public String getFeeSettleType() {
		return feeSettleType;
	}

	public void setFeeSettleType(String feeSettleType) {
		this.feeSettleType = feeSettleType;
	}

	public String getFeeSettleMode() {
		return feeSettleMode;
	}

	public void setFeeSettleMode(String feeSettleMode) {
		this.feeSettleMode = feeSettleMode;
	}

	public String getFeeSettleRange() {
		return feeSettleRange;
	}

	public void setFeeSettleRange(String feeSettleRange) {
		this.feeSettleRange = feeSettleRange;
	}

	public Boolean getFeeSettleDay() {
		return feeSettleDay;
	}

	public void setFeeSettleDay(Boolean feeSettleDay) {
		this.feeSettleDay = feeSettleDay;
	}

	public String getFeeSettleHolidays() {
		return feeSettleHolidays;
	}

	public void setFeeSettleHolidays(String feeSettleHolidays) {
		this.feeSettleHolidays = feeSettleHolidays;
	}

	public BigDecimal getFeeMinSettleMoney() {
		return feeMinSettleMoney;
	}

	public void setFeeMinSettleMoney(BigDecimal feeMinSettleMoney) {
		this.feeMinSettleMoney = feeMinSettleMoney;
	}

	public String getFeeSettleAccountName() {
		return feeSettleAccountName;
	}

	public void setFeeSettleAccountName(String feeSettleAccountName) {
		this.feeSettleAccountName = feeSettleAccountName;
	}

	public String getFeeSettleBankCard() {
		return feeSettleBankCard;
	}

	public void setFeeSettleBankCard(String feeSettleBankCard) {
		this.feeSettleBankCard = feeSettleBankCard;
	}

	public String getFeeSettleBankName() {
		return feeSettleBankName;
	}

	public void setFeeSettleBankName(String feeSettleBankName) {
		this.feeSettleBankName = feeSettleBankName;
	}

	public String getFeeSettleProvince() {
		return feeSettleProvince;
	}

	public void setFeeSettleProvince(String feeSettleProvince) {
		this.feeSettleProvince = feeSettleProvince;
	}

	public String getFeeSettleCity() {
		return feeSettleCity;
	}

	public void setFeeSettleCity(String feeSettleCity) {
		this.feeSettleCity = feeSettleCity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRegisterEmail() {
		return registerEmail;
	}

	public void setRegisterEmail(String registerEmail) {
		this.registerEmail = registerEmail;
	}

	public String getIsSendmail() {
		return isSendmail;
	}

	public void setIsSendmail(String isSendmail) {
		this.isSendmail = isSendmail;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
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
}
