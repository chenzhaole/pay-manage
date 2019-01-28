package com.sys.admin.modules.merchant.controller;

import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.enums.AdminPayTypeEnum;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.merchant.bo.MerchantFee;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.common.enums.FeeRateBizTypeEnum;
import com.sys.common.enums.SignTypeEnum;
import com.sys.common.enums.StatusEnum;
import com.sys.common.util.DateUtils;
import com.sys.common.util.DateUtils2;
import com.sys.common.util.RandomNumberUtil;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatFeerate;
import com.sys.core.service.MerchantService;
import com.sys.core.service.PlatFeerateService;
import com.sys.core.service.ProductService;
import org.apache.avro.generic.GenericData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping(value = "${adminPath}/merchant")
public class MchtPaytypeFeeController extends BaseController {

	@Autowired
	private MerchantAdminService merchantAdminService;

	@Autowired
	private ChanMchtAdminService chanMchtAdminService;

	@Autowired
	private PlatFeerateService platFeerateService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private ProductService productService;

	@Autowired
	private JedisPool jedisPool;


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
		AdminPayTypeEnum[] payTypeList = AdminPayTypeEnum.values();

		//需要在商户首页展示的支付类型费率
		MchtInfo mchtInfo = merchantService.queryByKey(mchtId);
		String showMchtFeeRateStr = mchtInfo.getExtend2();
		logger.info("商户："+mchtId+"，查出的要显示在商户首页费率的支付类型为："+showMchtFeeRateStr);
		List<String> listShowMchtFeeRateVal = null;
		if(StringUtils.isNotBlank(showMchtFeeRateStr)){
			JSONObject showMchtFeeRateJSON = JSONObject.parseObject(showMchtFeeRateStr);
			if(showMchtFeeRateJSON.containsKey("showPayTypeFeeRate") && StringUtils.isNotBlank(showMchtFeeRateJSON.getString("showPayTypeFeeRate"))){
				listShowMchtFeeRateVal = Arrays.asList(showMchtFeeRateJSON.getString("showPayTypeFeeRate").split("&"));
			}
		}
		MerchantFee merchantFee;
		String bizId;
		for (AdminPayTypeEnum payTypeEnum : payTypeList) {
			bizId = mchtId + "&" + payTypeEnum.getCode();
			merchantFee = new MerchantFee();
			merchantFee.setPaytypeCode(payTypeEnum.getCode());
			merchantFee.setPaytypeName(payTypeEnum.getDesc());
			for (PlatFeerate platFeerate : platFeerates) {
				if (bizId.equals(platFeerate.getBizRefId())){
					merchantFee.setFeeType(platFeerate.getFeeType());
					//该支付类型费率是否显示在商户首页,1:显示，2：不显示
					String showMchtFeeRate = "2";
					if(null != listShowMchtFeeRateVal && listShowMchtFeeRateVal.contains(payTypeEnum.getCode())){
						showMchtFeeRate = "1";
						merchantFee.setShowMchtFeeRate(showMchtFeeRate);
					}
					merchantFee.setFeeRate(platFeerate.getFeeRate());
					merchantFee.setFeeAmount(platFeerate.getFeeAmount());
					merchantFee.setRequestNum(platFeerate.getRequestNum());
					merchantFee.setRequestTime(platFeerate.getRequestTime());
				}
			}

			merchantFees.add(merchantFee);
		}
		if(StringUtils.isNotBlank(mchtInfo.getExtend2()) ){
			JSONObject jsonObject = JSONObject.parseObject(mchtInfo.getExtend2());
			model.addAttribute("agentFeeRateType", jsonObject.get("agentFeeRateType"));
			if(StringUtils.isNotBlank((String)jsonObject.get("agentFeeRateType"))){
				model.addAttribute("rateType", "update");
			}else{
				model.addAttribute("rateType", "add");
			}
		}
		model.addAttribute("mchtInfo", mchtInfo);
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
					//是否展示在商户首页,1:显示
					merchantFee.setShowMchtFeeRate(paramMap.get("showMchtFeeRate" + number));
					merchantFee.setMchtId(mchtId);
					merchantFee.setFeeType(StringUtils.isNotBlank(paramMap.get("feeType" + number)) ? paramMap.get("feeType" + number) : "0");
					merchantFee.setFeeRate(new BigDecimal(StringUtils.isNotBlank(paramMap.get("feeRate" + number)) ? paramMap.get("feeRate" + number) : "0"));
					merchantFee.setFeeAmount(new BigDecimal(StringUtils.isNotBlank(paramMap.get("feeAmount" + number)) ? paramMap.get("feeAmount" + number) : "0"));
					merchantFee.setRequestTime(paramMap.get("mchtFee.requestTime" + number));
					merchantFee.setRequestNum(paramMap.get("mchtFee.requestNum" + number));
					merchantFees.add(merchantFee);
				}

			}

			List<PlatFeerate> feerates = new ArrayList<>();
			PlatFeerate feerate;
			//保存要新增或修改showMchtFeeRate的信息
			StringBuilder stringBuilder = new StringBuilder();
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
				feerate.setRequestNum(fee.getRequestNum());
				feerate.setRequestTime(fee.getRequestTime());
				if (StringUtils.isBlank(activeTime) || StatusEnum.VALID.getCode().equals(feeStatus)) {
					feerate.setActiveTime(new Date());
				} else {
					feerate.setActiveTime(DateUtils.parseDate(activeTime));
				}
				feerate.setStatus(feeStatus);
				feerates.add(feerate);
				//将showMchtFeeRate的值存入stringBuilder中
				stringBuilder.append(fee.getPaytypeCode().trim()).append("=").append(fee.getShowMchtFeeRate()).append("&");
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

					if(FeeRateBizTypeEnum.MCHT_PAYTYPE_BIZTYPE.getCode().equals(platFeerate.getBizType()) && !StringUtils.isEmpty(platFeerate.getRequestNum()) && !StringUtils.isEmpty(platFeerate.getRequestNum())){
						try{
							if(StringUtils.isEmpty(platFeerate.getBizRefId())){
								continue;
							}
							String [] payTypeArray = platFeerate.getBizRefId().split("&");
							if(payTypeArray.length!= 2){
								continue;
							}
							resetPayLimit(mchtId, payTypeArray[0]);
						}catch (Exception e){
							logger.error(e.getMessage(), e);
						}
					}
				}
			}

			//保存要展示的支付方式费率至商户基本信息mcht_info
			String strShowMchtFeeRate = stringBuilder.toString();

			MchtInfo mchtInfo = merchantService.queryByKey(mchtId);
			if(null != mchtInfo){
				JSONObject extend2Json = null;
				if(StringUtils.isNotBlank(mchtInfo.getExtend2())){
					//商户基本信息是否已经保存过要展示的支付类型费率
					String extent2DB = mchtInfo.getExtend2();
					extend2Json = JSONObject.parseObject(extent2DB);
					if(extend2Json.containsKey("showPayTypeFeeRate")){
						String showPayTypeFeeRateDB = extend2Json.getString("showPayTypeFeeRate");
						strShowMchtFeeRate = this.combNewExtend2(strShowMchtFeeRate, showPayTypeFeeRateDB);
						extend2Json.remove("showPayTypeFeeRate");
					}else if (StringUtils.isNotBlank(strShowMchtFeeRate) && strShowMchtFeeRate.contains("&")) {
						strShowMchtFeeRate = this.geneShowMchtFeeRate(strShowMchtFeeRate);
					}
					extend2Json.put("showPayTypeFeeRate", strShowMchtFeeRate);
				}else{
					//extend2字段原本为空
					if (StringUtils.isNotBlank(strShowMchtFeeRate) && strShowMchtFeeRate.contains("&")) {
						strShowMchtFeeRate = this.geneShowMchtFeeRate(strShowMchtFeeRate);
						extend2Json = new JSONObject();
						extend2Json.put("showPayTypeFeeRate", strShowMchtFeeRate);
					}
				}
				if(extend2Json != null){
					mchtInfo.setExtend2(extend2Json.toJSONString());
				}
				MchtInfo selectMchtInfo = new MchtInfo();
				selectMchtInfo.setMchtCode(mchtId);
				logger.info("商户基本信息，Extend2字段增加可展示的支付类型，mchtInfo=" + JSONObject.toJSONString(mchtInfo) + ",选中的selectMchtInfo=" + JSONObject.toJSONString(selectMchtInfo));

				//当商户是代理商户时，判断代理商户费率类型
				if(SignTypeEnum.CLIENT_MCHT.getCode().equals(mchtInfo.getSignType())){
					String agentRateType = paramMap.get("agentFeeRateType");
					JSONObject jsonObject = null;
					if(StringUtils.isBlank(mchtInfo.getExtend2())){
						jsonObject = new JSONObject();
						jsonObject.put("agentFeeRateType",agentRateType);
					}else{
						jsonObject = JSONObject.parseObject(mchtInfo.getExtend2());
						if(StringUtils.isNotBlank(agentRateType)){
							jsonObject.put("agentFeeRateType" , agentRateType);
						}
					}
					mchtInfo.setExtend2(JSONObject.toJSONString(jsonObject));
					logger.info("代理商费率类型(Extend2字段)为："+JSONObject.toJSONString(jsonObject));
				}
				logger.info("商户基本信息，Extend2字段增加可展示的支付类型，mchtInfo=" + JSONObject.toJSONString(mchtInfo) + ",选中的selectMchtInfo=" + JSONObject.toJSONString(selectMchtInfo));
				int i = merchantService.updateBySelective(mchtInfo, selectMchtInfo);
				logger.info("商户基本信息，Extend2字段增加可展示的支付类型，mchtInfo=" + JSONObject.toJSONString(mchtInfo) + ",选中的selectMchtInfo=" + JSONObject.toJSONString(selectMchtInfo) + ",更新数据库的结果为：" + i);

			}

			message = "操作完成";
			messageType = "success";
		} catch (Exception e) {
			e.printStackTrace();
			message = "操作失败"+e.getMessage();
			messageType = "error";
		}
		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + GlobalConfig.getAdminPath() + "/merchant/list";
	}

	/**
	 * 拼接成最终的showMchtFeeRate
	 * @param strShowMchtFeeRate
	 * @return
	 */
	private String geneShowMchtFeeRate(String strShowMchtFeeRate) {
		StringBuilder sb = new StringBuilder();
		String[] showMchtFeeRateArr = strShowMchtFeeRate.split("&");
		for (int i = 0; i < showMchtFeeRateArr.length; i++) {
			sb.append(showMchtFeeRateArr[i].split("=")[0]).append("&");
		}
		return sb.toString();
	}

	/**
	 * 重新组合extend2的值
	 * @param strShowMchtFeeRate 页面提交过来的值 wx501=null&wx502=1&wx503=null&
	 * @param showPayTypeFeeRateDB 数据库原本已经存在的值 wx101&wx201&wx401&
	 * @return
	 */
	private String combNewExtend2(String strShowMchtFeeRate, String showPayTypeFeeRateDB) {
		if(StringUtils.isBlank(strShowMchtFeeRate)){
			return showPayTypeFeeRateDB;
		}
		String[] showMchtFeeRateArr =strShowMchtFeeRate.split("&");
		String[] extend2Arr = showPayTypeFeeRateDB.split("&");
		Set<String> extent2Set = new HashSet<>();
		for (int i = 0; i < extend2Arr.length; i++) {
			extent2Set.add(extend2Arr[i]);
		}

		for (int i = 0; i < showMchtFeeRateArr.length; i++) {
			if(showMchtFeeRateArr[i].contains("=1")){
				extent2Set.add(showMchtFeeRateArr[i].split("=")[0]);
			}else{
				if(extent2Set.contains(showMchtFeeRateArr[i].split("=")[0])){
					extent2Set.remove(showMchtFeeRateArr[i].split("=")[0]);
				}
			}
		}
		StringBuilder sb  = new StringBuilder();
		if(null != extent2Set && extent2Set.size() > 0){
			for (String name:extent2Set) {
				sb.append(name).append("&");
			}
			return sb.toString();
		}
		return null;
	}


	/**
	 * 修改支付限频后重新计数
	 * 2018-12-15 14:13:06
	 * @param mchtId
	 * @param payType
	 * @return
	 */
	public boolean resetPayLimit(String mchtId, String payType){
		Jedis jedis = null;
		String key = "REQUEST_MCHT_PAYTYPE_CREATED_TIME:"+mchtId + "_" + payType;
		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
			logger.info("设置校验商户支付方式请求频率,商户号:"+ mchtId +" ,支付方式:"+ payType );
		} catch (Exception e) {
			logger.error(key+" is redis exists error: {}", e.getMessage(),e);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return true;
	}
}
