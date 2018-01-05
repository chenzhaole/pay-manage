package com.sys.admin.modules.portal.dmo;

import java.util.ArrayList;
import java.util.List;

public class PortalInfoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PortalInfoExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andOfficeIdIsNull() {
            addCriterion("office_id is null");
            return (Criteria) this;
        }

        public Criteria andOfficeIdIsNotNull() {
            addCriterion("office_id is not null");
            return (Criteria) this;
        }

        public Criteria andOfficeIdEqualTo(Long value) {
            addCriterion("office_id =", value, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdNotEqualTo(Long value) {
            addCriterion("office_id <>", value, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdGreaterThan(Long value) {
            addCriterion("office_id >", value, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdGreaterThanOrEqualTo(Long value) {
            addCriterion("office_id >=", value, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdLessThan(Long value) {
            addCriterion("office_id <", value, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdLessThanOrEqualTo(Long value) {
            addCriterion("office_id <=", value, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdIn(List<Long> values) {
            addCriterion("office_id in", values, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdNotIn(List<Long> values) {
            addCriterion("office_id not in", values, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdBetween(Long value1, Long value2) {
            addCriterion("office_id between", value1, value2, "officeId");
            return (Criteria) this;
        }

        public Criteria andOfficeIdNotBetween(Long value1, Long value2) {
            addCriterion("office_id not between", value1, value2, "officeId");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(String value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(String value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("type like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("type not like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(String value1, String value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(String value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(String value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(String value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(String value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(String value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(String value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLike(String value) {
            addCriterion("status like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotLike(String value) {
            addCriterion("status not like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<String> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<String> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(String value1, String value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(String value1, String value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoIsNull() {
            addCriterion("company_logo is null");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoIsNotNull() {
            addCriterion("company_logo is not null");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoEqualTo(String value) {
            addCriterion("company_logo =", value, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoNotEqualTo(String value) {
            addCriterion("company_logo <>", value, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoGreaterThan(String value) {
            addCriterion("company_logo >", value, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoGreaterThanOrEqualTo(String value) {
            addCriterion("company_logo >=", value, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoLessThan(String value) {
            addCriterion("company_logo <", value, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoLessThanOrEqualTo(String value) {
            addCriterion("company_logo <=", value, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoLike(String value) {
            addCriterion("company_logo like", value, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoNotLike(String value) {
            addCriterion("company_logo not like", value, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoIn(List<String> values) {
            addCriterion("company_logo in", values, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoNotIn(List<String> values) {
            addCriterion("company_logo not in", values, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoBetween(String value1, String value2) {
            addCriterion("company_logo between", value1, value2, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyLogoNotBetween(String value1, String value2) {
            addCriterion("company_logo not between", value1, value2, "companyLogo");
            return (Criteria) this;
        }

        public Criteria andCompanyIconIsNull() {
            addCriterion("company_icon is null");
            return (Criteria) this;
        }

        public Criteria andCompanyIconIsNotNull() {
            addCriterion("company_icon is not null");
            return (Criteria) this;
        }

        public Criteria andCompanyIconEqualTo(String value) {
            addCriterion("company_icon =", value, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconNotEqualTo(String value) {
            addCriterion("company_icon <>", value, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconGreaterThan(String value) {
            addCriterion("company_icon >", value, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconGreaterThanOrEqualTo(String value) {
            addCriterion("company_icon >=", value, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconLessThan(String value) {
            addCriterion("company_icon <", value, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconLessThanOrEqualTo(String value) {
            addCriterion("company_icon <=", value, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconLike(String value) {
            addCriterion("company_icon like", value, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconNotLike(String value) {
            addCriterion("company_icon not like", value, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconIn(List<String> values) {
            addCriterion("company_icon in", values, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconNotIn(List<String> values) {
            addCriterion("company_icon not in", values, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconBetween(String value1, String value2) {
            addCriterion("company_icon between", value1, value2, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andCompanyIconNotBetween(String value1, String value2) {
            addCriterion("company_icon not between", value1, value2, "companyIcon");
            return (Criteria) this;
        }

        public Criteria andDomainAddressIsNull() {
            addCriterion("domain_address is null");
            return (Criteria) this;
        }

        public Criteria andDomainAddressIsNotNull() {
            addCriterion("domain_address is not null");
            return (Criteria) this;
        }

        public Criteria andDomainAddressEqualTo(String value) {
            addCriterion("domain_address =", value, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressNotEqualTo(String value) {
            addCriterion("domain_address <>", value, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressGreaterThan(String value) {
            addCriterion("domain_address >", value, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressGreaterThanOrEqualTo(String value) {
            addCriterion("domain_address >=", value, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressLessThan(String value) {
            addCriterion("domain_address <", value, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressLessThanOrEqualTo(String value) {
            addCriterion("domain_address <=", value, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressLike(String value) {
            addCriterion("domain_address like", value, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressNotLike(String value) {
            addCriterion("domain_address not like", value, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressIn(List<String> values) {
            addCriterion("domain_address in", values, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressNotIn(List<String> values) {
            addCriterion("domain_address not in", values, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressBetween(String value1, String value2) {
            addCriterion("domain_address between", value1, value2, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andDomainAddressNotBetween(String value1, String value2) {
            addCriterion("domain_address not between", value1, value2, "domainAddress");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneIsNull() {
            addCriterion("customer_service_phone is null");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneIsNotNull() {
            addCriterion("customer_service_phone is not null");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneEqualTo(String value) {
            addCriterion("customer_service_phone =", value, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneNotEqualTo(String value) {
            addCriterion("customer_service_phone <>", value, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneGreaterThan(String value) {
            addCriterion("customer_service_phone >", value, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneGreaterThanOrEqualTo(String value) {
            addCriterion("customer_service_phone >=", value, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneLessThan(String value) {
            addCriterion("customer_service_phone <", value, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneLessThanOrEqualTo(String value) {
            addCriterion("customer_service_phone <=", value, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneLike(String value) {
            addCriterion("customer_service_phone like", value, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneNotLike(String value) {
            addCriterion("customer_service_phone not like", value, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneIn(List<String> values) {
            addCriterion("customer_service_phone in", values, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneNotIn(List<String> values) {
            addCriterion("customer_service_phone not in", values, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneBetween(String value1, String value2) {
            addCriterion("customer_service_phone between", value1, value2, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServicePhoneNotBetween(String value1, String value2) {
            addCriterion("customer_service_phone not between", value1, value2, "customerServicePhone");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailIsNull() {
            addCriterion("customer_service_email is null");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailIsNotNull() {
            addCriterion("customer_service_email is not null");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailEqualTo(String value) {
            addCriterion("customer_service_email =", value, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailNotEqualTo(String value) {
            addCriterion("customer_service_email <>", value, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailGreaterThan(String value) {
            addCriterion("customer_service_email >", value, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailGreaterThanOrEqualTo(String value) {
            addCriterion("customer_service_email >=", value, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailLessThan(String value) {
            addCriterion("customer_service_email <", value, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailLessThanOrEqualTo(String value) {
            addCriterion("customer_service_email <=", value, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailLike(String value) {
            addCriterion("customer_service_email like", value, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailNotLike(String value) {
            addCriterion("customer_service_email not like", value, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailIn(List<String> values) {
            addCriterion("customer_service_email in", values, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailNotIn(List<String> values) {
            addCriterion("customer_service_email not in", values, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailBetween(String value1, String value2) {
            addCriterion("customer_service_email between", value1, value2, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andCustomerServiceEmailNotBetween(String value1, String value2) {
            addCriterion("customer_service_email not between", value1, value2, "customerServiceEmail");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdIsNull() {
            addCriterion("sms_template_id is null");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdIsNotNull() {
            addCriterion("sms_template_id is not null");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdEqualTo(String value) {
            addCriterion("sms_template_id =", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdNotEqualTo(String value) {
            addCriterion("sms_template_id <>", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdGreaterThan(String value) {
            addCriterion("sms_template_id >", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdGreaterThanOrEqualTo(String value) {
            addCriterion("sms_template_id >=", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdLessThan(String value) {
            addCriterion("sms_template_id <", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdLessThanOrEqualTo(String value) {
            addCriterion("sms_template_id <=", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdLike(String value) {
            addCriterion("sms_template_id like", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdNotLike(String value) {
            addCriterion("sms_template_id not like", value, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdIn(List<String> values) {
            addCriterion("sms_template_id in", values, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdNotIn(List<String> values) {
            addCriterion("sms_template_id not in", values, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdBetween(String value1, String value2) {
            addCriterion("sms_template_id between", value1, value2, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIdNotBetween(String value1, String value2) {
            addCriterion("sms_template_id not between", value1, value2, "smsTemplateId");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginIsNull() {
            addCriterion("joint_account_login is null");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginIsNotNull() {
            addCriterion("joint_account_login is not null");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginEqualTo(String value) {
            addCriterion("joint_account_login =", value, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginNotEqualTo(String value) {
            addCriterion("joint_account_login <>", value, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginGreaterThan(String value) {
            addCriterion("joint_account_login >", value, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginGreaterThanOrEqualTo(String value) {
            addCriterion("joint_account_login >=", value, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginLessThan(String value) {
            addCriterion("joint_account_login <", value, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginLessThanOrEqualTo(String value) {
            addCriterion("joint_account_login <=", value, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginLike(String value) {
            addCriterion("joint_account_login like", value, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginNotLike(String value) {
            addCriterion("joint_account_login not like", value, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginIn(List<String> values) {
            addCriterion("joint_account_login in", values, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginNotIn(List<String> values) {
            addCriterion("joint_account_login not in", values, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginBetween(String value1, String value2) {
            addCriterion("joint_account_login between", value1, value2, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andJointAccountLoginNotBetween(String value1, String value2) {
            addCriterion("joint_account_login not between", value1, value2, "jointAccountLogin");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoIsNull() {
            addCriterion("financial_account_info is null");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoIsNotNull() {
            addCriterion("financial_account_info is not null");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoEqualTo(String value) {
            addCriterion("financial_account_info =", value, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoNotEqualTo(String value) {
            addCriterion("financial_account_info <>", value, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoGreaterThan(String value) {
            addCriterion("financial_account_info >", value, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoGreaterThanOrEqualTo(String value) {
            addCriterion("financial_account_info >=", value, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoLessThan(String value) {
            addCriterion("financial_account_info <", value, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoLessThanOrEqualTo(String value) {
            addCriterion("financial_account_info <=", value, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoLike(String value) {
            addCriterion("financial_account_info like", value, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoNotLike(String value) {
            addCriterion("financial_account_info not like", value, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoIn(List<String> values) {
            addCriterion("financial_account_info in", values, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoNotIn(List<String> values) {
            addCriterion("financial_account_info not in", values, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoBetween(String value1, String value2) {
            addCriterion("financial_account_info between", value1, value2, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andFinancialAccountInfoNotBetween(String value1, String value2) {
            addCriterion("financial_account_info not between", value1, value2, "financialAccountInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoIsNull() {
            addCriterion("business_investment_info is null");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoIsNotNull() {
            addCriterion("business_investment_info is not null");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoEqualTo(String value) {
            addCriterion("business_investment_info =", value, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoNotEqualTo(String value) {
            addCriterion("business_investment_info <>", value, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoGreaterThan(String value) {
            addCriterion("business_investment_info >", value, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoGreaterThanOrEqualTo(String value) {
            addCriterion("business_investment_info >=", value, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoLessThan(String value) {
            addCriterion("business_investment_info <", value, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoLessThanOrEqualTo(String value) {
            addCriterion("business_investment_info <=", value, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoLike(String value) {
            addCriterion("business_investment_info like", value, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoNotLike(String value) {
            addCriterion("business_investment_info not like", value, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoIn(List<String> values) {
            addCriterion("business_investment_info in", values, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoNotIn(List<String> values) {
            addCriterion("business_investment_info not in", values, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoBetween(String value1, String value2) {
            addCriterion("business_investment_info between", value1, value2, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andBusinessInvestmentInfoNotBetween(String value1, String value2) {
            addCriterion("business_investment_info not between", value1, value2, "businessInvestmentInfo");
            return (Criteria) this;
        }

        public Criteria andWechatAccountIsNull() {
            addCriterion("wechat_account is null");
            return (Criteria) this;
        }

        public Criteria andWechatAccountIsNotNull() {
            addCriterion("wechat_account is not null");
            return (Criteria) this;
        }

        public Criteria andWechatAccountEqualTo(String value) {
            addCriterion("wechat_account =", value, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountNotEqualTo(String value) {
            addCriterion("wechat_account <>", value, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountGreaterThan(String value) {
            addCriterion("wechat_account >", value, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountGreaterThanOrEqualTo(String value) {
            addCriterion("wechat_account >=", value, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountLessThan(String value) {
            addCriterion("wechat_account <", value, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountLessThanOrEqualTo(String value) {
            addCriterion("wechat_account <=", value, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountLike(String value) {
            addCriterion("wechat_account like", value, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountNotLike(String value) {
            addCriterion("wechat_account not like", value, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountIn(List<String> values) {
            addCriterion("wechat_account in", values, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountNotIn(List<String> values) {
            addCriterion("wechat_account not in", values, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountBetween(String value1, String value2) {
            addCriterion("wechat_account between", value1, value2, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatAccountNotBetween(String value1, String value2) {
            addCriterion("wechat_account not between", value1, value2, "wechatAccount");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeIsNull() {
            addCriterion("wechat_barcode is null");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeIsNotNull() {
            addCriterion("wechat_barcode is not null");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeEqualTo(String value) {
            addCriterion("wechat_barcode =", value, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeNotEqualTo(String value) {
            addCriterion("wechat_barcode <>", value, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeGreaterThan(String value) {
            addCriterion("wechat_barcode >", value, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeGreaterThanOrEqualTo(String value) {
            addCriterion("wechat_barcode >=", value, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeLessThan(String value) {
            addCriterion("wechat_barcode <", value, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeLessThanOrEqualTo(String value) {
            addCriterion("wechat_barcode <=", value, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeLike(String value) {
            addCriterion("wechat_barcode like", value, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeNotLike(String value) {
            addCriterion("wechat_barcode not like", value, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeIn(List<String> values) {
            addCriterion("wechat_barcode in", values, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeNotIn(List<String> values) {
            addCriterion("wechat_barcode not in", values, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeBetween(String value1, String value2) {
            addCriterion("wechat_barcode between", value1, value2, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andWechatBarcodeNotBetween(String value1, String value2) {
            addCriterion("wechat_barcode not between", value1, value2, "wechatBarcode");
            return (Criteria) this;
        }

        public Criteria andStarLevelIsNull() {
            addCriterion("star_level is null");
            return (Criteria) this;
        }

        public Criteria andStarLevelIsNotNull() {
            addCriterion("star_level is not null");
            return (Criteria) this;
        }

        public Criteria andStarLevelEqualTo(String value) {
            addCriterion("star_level =", value, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelNotEqualTo(String value) {
            addCriterion("star_level <>", value, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelGreaterThan(String value) {
            addCriterion("star_level >", value, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelGreaterThanOrEqualTo(String value) {
            addCriterion("star_level >=", value, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelLessThan(String value) {
            addCriterion("star_level <", value, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelLessThanOrEqualTo(String value) {
            addCriterion("star_level <=", value, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelLike(String value) {
            addCriterion("star_level like", value, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelNotLike(String value) {
            addCriterion("star_level not like", value, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelIn(List<String> values) {
            addCriterion("star_level in", values, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelNotIn(List<String> values) {
            addCriterion("star_level not in", values, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelBetween(String value1, String value2) {
            addCriterion("star_level between", value1, value2, "starLevel");
            return (Criteria) this;
        }

        public Criteria andStarLevelNotBetween(String value1, String value2) {
            addCriterion("star_level not between", value1, value2, "starLevel");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andAboutIsNull() {
            addCriterion("about is null");
            return (Criteria) this;
        }

        public Criteria andAboutIsNotNull() {
            addCriterion("about is not null");
            return (Criteria) this;
        }

        public Criteria andAboutEqualTo(String value) {
            addCriterion("about =", value, "about");
            return (Criteria) this;
        }

        public Criteria andAboutNotEqualTo(String value) {
            addCriterion("about <>", value, "about");
            return (Criteria) this;
        }

        public Criteria andAboutGreaterThan(String value) {
            addCriterion("about >", value, "about");
            return (Criteria) this;
        }

        public Criteria andAboutGreaterThanOrEqualTo(String value) {
            addCriterion("about >=", value, "about");
            return (Criteria) this;
        }

        public Criteria andAboutLessThan(String value) {
            addCriterion("about <", value, "about");
            return (Criteria) this;
        }

        public Criteria andAboutLessThanOrEqualTo(String value) {
            addCriterion("about <=", value, "about");
            return (Criteria) this;
        }

        public Criteria andAboutLike(String value) {
            addCriterion("about like", value, "about");
            return (Criteria) this;
        }

        public Criteria andAboutNotLike(String value) {
            addCriterion("about not like", value, "about");
            return (Criteria) this;
        }

        public Criteria andAboutIn(List<String> values) {
            addCriterion("about in", values, "about");
            return (Criteria) this;
        }

        public Criteria andAboutNotIn(List<String> values) {
            addCriterion("about not in", values, "about");
            return (Criteria) this;
        }

        public Criteria andAboutBetween(String value1, String value2) {
            addCriterion("about between", value1, value2, "about");
            return (Criteria) this;
        }

        public Criteria andAboutNotBetween(String value1, String value2) {
            addCriterion("about not between", value1, value2, "about");
            return (Criteria) this;
        }

        public Criteria andRemarksIsNull() {
            addCriterion("remarks is null");
            return (Criteria) this;
        }

        public Criteria andRemarksIsNotNull() {
            addCriterion("remarks is not null");
            return (Criteria) this;
        }

        public Criteria andRemarksEqualTo(String value) {
            addCriterion("remarks =", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksNotEqualTo(String value) {
            addCriterion("remarks <>", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksGreaterThan(String value) {
            addCriterion("remarks >", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksGreaterThanOrEqualTo(String value) {
            addCriterion("remarks >=", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksLessThan(String value) {
            addCriterion("remarks <", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksLessThanOrEqualTo(String value) {
            addCriterion("remarks <=", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksLike(String value) {
            addCriterion("remarks like", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksNotLike(String value) {
            addCriterion("remarks not like", value, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksIn(List<String> values) {
            addCriterion("remarks in", values, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksNotIn(List<String> values) {
            addCriterion("remarks not in", values, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksBetween(String value1, String value2) {
            addCriterion("remarks between", value1, value2, "remarks");
            return (Criteria) this;
        }

        public Criteria andRemarksNotBetween(String value1, String value2) {
            addCriterion("remarks not between", value1, value2, "remarks");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}