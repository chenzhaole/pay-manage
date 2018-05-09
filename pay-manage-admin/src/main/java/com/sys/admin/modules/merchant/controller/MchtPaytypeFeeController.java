package com.sys.admin.modules.merchant.controller;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.merchant.bo.MerchantFee;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.DateUtils;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.RandomNumberUtil;
import com.sys.core.dao.dmo.PlatFeerate;
import com.sys.core.service.PlatFeerateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/merchant")
public class MchtPaytypeFeeController extends BaseController {

	@Autowired
	private MerchantAdminService merchantAdminService;

	@Autowired
	private ChanMchtAdminService chanMchtAdminService;

	@Autowired
	private PlatFeerateService platFeerateService;


	/**
	 * 商户支付方式费率列表
	 */
	@RequestMapping(value = {"mchtPaytypeFeePage", ""})
	public String mchPaytypelist(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam Map<String, String> paramMap
	) {

		String mchtId = paramMap.get("mchtId");
		//根据商户查费率
		List<PlatFeerate> platFeerates = platFeerateService.getMchtFee(mchtId);
		//商户费率关系
		List<MerchantFee> merchantFees = new ArrayList<>();
		//支付方式
		PayTypeEnum[] payTypeList = PayTypeEnum.values();

		MerchantFee merchantFee;
		String bizId;
		for (PayTypeEnum payTypeEnum : payTypeList) {
			bizId = mchtId + "&" + payTypeEnum.getCode();
			merchantFee = new MerchantFee();
			merchantFee.setPaytypeCode(payTypeEnum.getCode());
			merchantFee.setPaytypeName(payTypeEnum.getDesc());
			for (PlatFeerate platFeerate : platFeerates) {
				if (bizId.equals(platFeerate.getBizRefId())){
					merchantFee.setFeeType(platFeerate.getFeeType());
					merchantFee.setFeeRate(platFeerate.getFeeRate());
					merchantFee.setFeeAmount(platFeerate.getFeeAmount());
				}
			}
			merchantFees.add(merchantFee);
		}

		model.addAttribute("mchtId", mchtId);
		model.addAttribute("mchtFees", merchantFees);
		model.addAttribute("paymentTypeInfos", payTypeList);

		return "modules/merchant/mchtPaytypeFeePage";
	}


	/**
	 * 商户支付方式费率修改
	 */
	@RequestMapping(value = {"updateMchtPaytypeFee"})
	public String updateMchtPaytypeFee(HttpServletRequest request, HttpServletResponse response, Model model,
									@RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) {

		int result = 0;
		String message, messageType;
		try {

			String mchtId = paramMap.get("id");
			String activeTime = paramMap.get("activeTime");
			String feeStatus = paramMap.get("feeStatus");

			//商户费率关系
			List<MerchantFee> merchantFees = new ArrayList<>();

			//获取通道商户支付方式ID
			List<String> keys = new ArrayList<>();
			for (String key : paramMap.keySet()) {
				if (key.contains("paytype")) {
					keys.add(key);
				}
			}

			MerchantFee merchantFee;
			//根据通道商户支付方式ID获取别的参数
			for (String key : keys) {
				merchantFee = new MerchantFee();
				merchantFee.setPaytypeCode(paramMap.get(key));
				String number = key.substring(7, key.length());
				if (StringUtils.isNotBlank(paramMap.get("rate" + number))){
					merchantFee.setFeeRate(new BigDecimal(paramMap.get("rate" + number)));
				}
				if ("1".equals(paramMap.get("save" + number))){
					merchantFee.setMchtId(mchtId);
					merchantFee.setFeeType(StringUtils.isNotBlank(paramMap.get("feeType" + number)) ? paramMap.get("feeType" + number) : "0");
					merchantFee.setFeeRate(new BigDecimal(StringUtils.isNotBlank(paramMap.get("feeRate" + number)) ? paramMap.get("feeRate" + number) : "0"));
					merchantFee.setFeeAmount(new BigDecimal(StringUtils.isNotBlank(paramMap.get("feeAmount" + number)) ? paramMap.get("feeAmount" + number) : "0"));
					merchantFees.add(merchantFee);
				}

			}

			List<PlatFeerate> feerates = new ArrayList<>();
			PlatFeerate feerate;
			for (MerchantFee fee : merchantFees) {
				feerate = new PlatFeerate();
				feerate.setBizType(FeeRateBizTypeEnum.MCHT_PAYTYPE_BIZTYPE.getCode());
				//系统生成feeID，“F”+yyyyMMdd+四位随机数
				String feeID = "F"+ DateUtils2.getNowTimeStr("yyyyMMddHHmmssSSS")+ RandomNumberUtil.getRandNumber(4);
				feerate.setId(feeID);
				feerate.setBizName(FeeRateBizTypeEnum.MCHT_PAYTYPE_BIZTYPE.getdesc());
				feerate.setBizRefId(mchtId + "&" + fee.getPaytypeCode());
				feerate.setCreateTime(new Date());
				feerate.setFeeType(fee.getFeeType());
				feerate.setFeeRate(fee.getFeeRate());
				feerate.setFeeAmount(fee.getFeeAmount());
				if (StringUtils.isBlank(activeTime) || StatusEnum.VALID.getCode().equals(feeStatus)) {
					feerate.setActiveTime(new Date());
				} else {
					feerate.setActiveTime(DateUtils.parseDate(activeTime));
				}
				feerate.setStatus(feeStatus);
				feerates.add(feerate);
			}


			boolean save;
			if (!CollectionUtils.isEmpty(feerates)){
				List<PlatFeerate> platFeerates = platFeerateService.getMchtFee(mchtId);
				for (PlatFeerate platFeerate : feerates) {
					save = false;
					if (!CollectionUtils.isEmpty(platFeerates)){
						for (PlatFeerate mchtFee : platFeerates) {
							if (platFeerate.getBizRefId().equals(mchtFee.getBizRefId())){
								save = true;
							}
						}
					}
					if (save){
						platFeerateService.saveByKey(platFeerate);
					}else {
						platFeerateService.createFirstTime(platFeerate);
					}

				}
			}

			message = "操作完成";
			messageType = "success";
		} catch (Exception e) {
			e.printStackTrace();
			message = "操作失败";
			messageType = "error";
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/list";
	}
}
