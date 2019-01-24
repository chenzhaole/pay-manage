package com.sys.admin.modules.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.PictureUtils;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.merchant.bo.MerchantForm;
import com.sys.admin.modules.merchant.service.MerchantAdminService;
import com.sys.admin.modules.platform.bo.MchtProductFormInfo;
import com.sys.admin.modules.platform.service.MchtProductAdminService;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.service.SysAreaService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.enums.*;
import com.sys.common.util.HttpUtil;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.MchtAccountDetail;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatFeerate;
import com.sys.core.service.PlatFeerateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

@Controller
@RequestMapping(value = "${adminPath}/merchant")
public class MerchantController extends BaseController {

	@Autowired
	private SysAreaService areaService;

	@Autowired
	private MerchantAdminService merchantAdminService;

	@Autowired
	private ChanMchtAdminService chanMchtAdminService;

	@Autowired
	private MchtProductAdminService mchtProductAdminService;

	@Autowired
	private PlatFeerateService platFeerateService;

	//商户基本信息接口地址
	@Value("${mchtInfoData.url}")
	private String mchtInfoDataUrl;

	//交易相关数据接口地址
	@Value("${payData.url}")
	private String payDataUrl;

	//商户账户详情信息接口地址
	@Value("${mchtAccountDetailData.url}")
	private String mchtAccountDetailUrl;

	//商户费率信息接口地址
	@Value("${mchtFeerateInfoData.url}")
	private String mchtFeerateInfoDataUrl;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 商户首页
	 */
	@RequestMapping(value = {"mchtWelcome", ""})
	public String  mchtWelcome(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
		//当前登陆商户
		User user = UserUtils.getUser();
		String mchtCode = user.getLoginName();

		//1.商户基本信息
		MchtInfo mchtInfoData = queryMchtInfoByHttp(mchtCode);
		if(null == mchtInfoData){
			return "modules/merchant/mchtWelcome";
		}
		model.addAttribute("mchtInfoData",  mchtInfoData);

		//2.交易相关数据
		Map payData = queryPayDataByHttp(mchtCode);
		model.addAttribute("payData", payData);

		//3.商户账户详情信息
		MchtAccountDetail mchtAccountDetailData = queryMchtAccountDetailByHttp(mchtCode);
		if(null != mchtAccountDetailData){
			//冻结金额
			BigDecimal freezeTotalAmount = mchtAccountDetailData.getFreezeTotalAmount();
			//可提现金额 = 现金总金额 - 冻结总金额;
			//现金总金额
			BigDecimal cashTotalAmount = mchtAccountDetailData.getCashTotalAmount();
			//待结算金额
			BigDecimal settleTotalAmount =mchtAccountDetailData.getSettleTotalAmount().divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
			//可提现金额
			BigDecimal presentedAmount = cashTotalAmount.subtract(freezeTotalAmount);
			presentedAmount = presentedAmount.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
			//冻结金额
			freezeTotalAmount = freezeTotalAmount.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
			mchtAccountDetailData.setFreezeTotalAmount(freezeTotalAmount);
			mchtAccountDetailData.setSettleTotalAmount(settleTotalAmount);
			mchtAccountDetailData.setAvailableBalance(presentedAmount);
		}

		model.addAttribute("mchtAccountDetailData", mchtAccountDetailData);
		//4.商户费率信息
		List<PlatFeerate> mchtFeerateInfoData = queryMchtFeerateInfoByHttp(mchtCode);
		//将费率转成map
		Map<String, String> mchtFeerateInfoMap = null;
		if(null != mchtFeerateInfoData && mchtFeerateInfoData.size() > 0){
			//过滤掉不显示的商户费率
			String extend2 = mchtInfoData.getExtend2();
            JSONObject extend2Json = JSONObject.parseObject(extend2);
            //只显示的支付方式
            List<String> showPayTypeFeeRateList = null;
            if(StringUtils.isNotBlank(extend2) && extend2Json.containsKey("showPayTypeFeeRate")
					&& !"null".equals(extend2Json.getString("showPayTypeFeeRate"))
					&& !"".equals(extend2Json.getString("showPayTypeFeeRate"))){
                String showPayTypeFeeRate = extend2Json.getString("showPayTypeFeeRate");
                showPayTypeFeeRateList = showPayTypeFeeRate.contains("&") ? Arrays.asList(showPayTypeFeeRate.split("&")):null;
				mchtFeerateInfoMap = mchtFeerateInfoDataToMap(mchtFeerateInfoData, showPayTypeFeeRateList);
            }
		}
		model.addAttribute("mchtFeerateInfoMap", mchtFeerateInfoMap);

		return "modules/merchant/mchtWelcome";
	}

	//将可用的费率封装进map中
	private Map<String, String> mchtFeerateInfoDataToMap(List<PlatFeerate> mchtFeerateInfoData, List<String> showPayTypeFeeRateList) {
		if(null != mchtFeerateInfoData && mchtFeerateInfoData.size() > 0){
			Map<String, String> data = new HashMap<>();
			for (PlatFeerate platFeerate : mchtFeerateInfoData){
				//支付类型
				String biz = (StringUtils.isNotBlank(platFeerate.getBizRefId()) && platFeerate.getBizRefId().contains("&")) ? platFeerate.getBizRefId().split("&")[1]:"";
				if(null != showPayTypeFeeRateList && showPayTypeFeeRateList.size() > 0 && !showPayTypeFeeRateList.contains(biz)){
					continue;
				}
				//值
				String value = "";
				//收费类型：1:按笔、2:按比例.
				if(PayTypeEnum.SINGLE_DF.getCode().equals(biz)){
					BigDecimal feeAmount = platFeerate.getFeeAmount();
					if(null != feeAmount && !feeAmount.toString().equals("0.00")){
                        feeAmount = feeAmount.divide(new BigDecimal(100));
						value = feeAmount.toString()+"元/笔";
					}
				}else{
					BigDecimal feeRate = platFeerate.getFeeRate();
					if(null != feeRate && !feeRate.toString().equals("0.00")){
						value = feeRate.toString()+"‰";
					}
				}

				if(StringUtils.isNotBlank(biz) && StringUtils.isNotBlank(value) && filterPayType(biz)){
					data.put(PayTypeEnum.toEnum(biz).getDesc(),value);
				}
			}
			return data;
		}else{
			return null;
		}
	}

	/**
	 * 过滤掉不能显示出来的支付类型
	 * @param biz
	 * @return
	 */
	private boolean filterPayType(String biz) {
//		List types = new ArrayList();
//		types.add("ca001");
//		types.add("ca002");
//		types.add("jh001");
//		types.add("hf001");
//		types.add("wx000");
//		types.add("wx502");
//		types.add("wx503");
//		types.add("al000");
//		types.add("al102");
//		types.add("al502");
//		types.add("al503");
//		types.add("sn000");
//		types.add("sn502");
//		types.add("sn503");
//		types.add("qq000");
//		types.add("qq102");
//		types.add("qq502");
//		types.add("qq503");
//		types.add("jd000");
//		types.add("jd102");
//		types.add("jd502");
//		types.add("jd503");
//		types.add("yl000");
//		types.add("yl402");
//		types.add("yl502");
//		types.add("yl503");
//		if(types.contains(biz)){
//			return false;
//		}

		return true;
	}

	/**
	 *  查询商户交易数据
	 * @param mchtCode
	 * @return
	 */
	private Map queryPayDataByHttp(String mchtCode) {
		String url = payDataUrl+"/"+mchtCode;
		logger.info("商户首页，查询商户交易数据信息，请求地址："+url);
		String retData = "";
		try {
			retData = HttpUtil.postConnManager(url, null);
			logger.info("商户首页，查询商户交易数据信息，接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return JSON.parseObject(retData, Map.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询商户基本信息
	 * @param mchtCode
	 * @return
	 */
	private MchtInfo queryMchtInfoByHttp(String mchtCode) {
		String url = mchtInfoDataUrl+"/"+mchtCode;
		logger.info("商户首页，查询商户信息，请求地址："+url);
		String retData = "";
		try {
			retData = HttpUtil.postConnManager(url, null);
			logger.info("商户首页，查询商户信息，接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return JSON.parseObject(retData, MchtInfo.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 商户账户详情信息
	 */
	private MchtAccountDetail queryMchtAccountDetailByHttp(String mchtCode) {
		String url = mchtAccountDetailUrl+"/"+mchtCode;
		logger.info("商户首页，查询商户账户详情信息，请求地址："+url);
		String retData = "";
		try {
			retData = HttpUtil.postConnManager(url, null);
			logger.info("商户首页，查询商户账户详情信息，接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return JSON.parseObject(retData, MchtAccountDetail.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 商户费率信息
	 */
	private List<PlatFeerate> queryMchtFeerateInfoByHttp(String mchtCode) {
		String url = mchtFeerateInfoDataUrl+"/"+mchtCode;
		logger.info("商户首页，查询商户费率详情信息，请求地址："+url);
		String retData = "";
		try {
			retData = HttpUtil.postConnManager(url, null);
			logger.info("商户首页，查询商户费率信息，接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return JSONArray.parseArray(retData, PlatFeerate.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value={"add"})
	public String add(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes) {

		model.addAttribute("op", "add");
		//获取商户类型枚举类
		SignTypeEnum[] merchantTypList =  SignTypeEnum.values();
		model.addAttribute("merchantTypList", merchantTypList);
		//获取证件类型枚举类
		CertTypeEnum[] certTypeList = CertTypeEnum.values();
		model.addAttribute("certTypeList", certTypeList);

		//商户列表 只展示代理商
		List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(new MchtInfo());
		List<MerchantForm> mchtInfosResult = new ArrayList<>();
		for (MerchantForm mchtInfo : mchtInfos) {
			if(StringUtils.isBlank(mchtInfo.getSignType())){
				continue;
			}
			if (mchtInfo.getSignType().contains(SignTypeEnum.CLIENT_MCHT.getCode())){
				mchtInfosResult.add(mchtInfo);
			}
		}
		model.addAttribute("mchts", mchtInfosResult);

		model.addAttribute("areas", areaService.getAllJson()); //获取所有地区
		return "modules/merchant/merchantEdit";
	}
	

	@RequestMapping(value={"edit"})
	public String edit(MerchantForm mcht,Model model) {
		try {
			//获取商户类型枚举类
			SignTypeEnum[] merchantTypList =  SignTypeEnum.values();
			model.addAttribute("merchantTypList", merchantTypList);
			//获取证件类型枚举类
			CertTypeEnum[] certTypeList = CertTypeEnum.values();
			model.addAttribute("certTypeList", certTypeList);
			String id = mcht.getId();
			MerchantForm mchtInfo = merchantAdminService.getMerchantById(id);
			//根据extend2中存的值，来取出 isShowPayResultPage、 mchtPropertyTag的值，以便页面回显
			if(null != mchtInfo && StringUtils.isNotBlank(mchtInfo.getExtend2())){
				String extend2Str = mchtInfo.getExtend2();
				JSONObject extend2Json = JSONObject.parseObject(extend2Str);
				if(extend2Json.containsKey("isShowPayResultPage")){
					mchtInfo.setIsShowPayResultPage(extend2Json.getString("isShowPayResultPage"));
				}
				if(extend2Json.containsKey("isShowPayPage")){
					mchtInfo.setIsShowPayPage(extend2Json.getString("isShowPayPage"));
				}
				if(extend2Json.containsKey("mchtPropertyTag")){
					String mchtPropertyTagStr = extend2Json.getString("mchtPropertyTag");
					if(mchtPropertyTagStr.contains("&")){
						mchtPropertyTagStr = this.converMchtPropertyTag(mchtPropertyTagStr);
					}
					mchtInfo.setMchtPropertyTag(mchtPropertyTagStr);
				}

			}

			logger.info("编辑商户信息,picDomain="+ PictureUtils.PIC_DOMAIN);
			model.addAttribute("picDomain",PictureUtils.PIC_DOMAIN);
			model.addAttribute("merchant", mchtInfo);
			model.addAttribute("op", "edit");

			//商户列表
			List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(new MchtInfo());
			List<MerchantForm> mchtInfoResults = new ArrayList<>();
			for (MerchantForm mchtInfoTemp : mchtInfos) {
				if (mchtInfoTemp.getId().equals(id)) {
					continue;
				}
				if (mchtInfoTemp.getSignType().contains(SignTypeEnum.CLIENT_MCHT.getCode())){
					mchtInfoResults.add(mchtInfoTemp);
				}
			}
			model.addAttribute("mchts", mchtInfoResults);

			model.addAttribute("areas", areaService.getAllJson()); //获取所有地区

			//检查商户配置情况
			ChanMchtFormInfo chanMchtFormInfo = new ChanMchtFormInfo();
			chanMchtFormInfo.setMchtId(mcht.getId());
			List<ChanMchtFormInfo> chanMchtFormInfos = chanMchtAdminService.getChannelListSimple(chanMchtFormInfo);
			int chan = 0, pro  = 0;
			if (!CollectionUtils.isEmpty(chanMchtFormInfos)){
				chan = 1;
			}
			MchtProductFormInfo mchtProductFormInfo = new MchtProductFormInfo();
			mchtProductFormInfo.setMchtCode(mcht.getMchtCode());
			List<MchtProductFormInfo> mchtProductFormInfos = mchtProductAdminService.getProductList(mchtProductFormInfo);

			if (!CollectionUtils.isEmpty(mchtProductFormInfos)){
				pro = 1;
			}

			model.addAttribute("chanCount", chan);
			model.addAttribute("productCount", pro);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		
		return "modules/merchant/merchantEdit";
	}

	/**
	 *  将mchtPropertyTag的格式由&拼接转成由,拼接并显示在页面
	 * @param mchtPropertyTagStr
	 * @return
	 */
	private String converMchtPropertyTag(String mchtPropertyTagStr) {
		if(StringUtils.isNotBlank(mchtPropertyTagStr)){
			String[] mchtPropertyTagArr = mchtPropertyTagStr.split("&");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mchtPropertyTagArr.length; i++) {
				sb.append(mchtPropertyTagArr[i]).append(",");
			}
			String val = sb.toString();
			val =  val.endsWith(",") ? val.substring(0, val.lastIndexOf(",")) : val;
			return val;
		}
		return "";
	}

	/**
	 * 
	 * @Title: addMerchantSave 
	 * @Description: 新增商户
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @param redirectAttributes
	 * @return
	 * @return: String
	 */
	@RequestMapping(value = { "addMerchantSave", "" })
	public String addMerchantSave(HttpServletRequest request, HttpServletResponse response, Model model,
								  @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes
								 /* MultipartFile blcFile, MultipartFile contractFile, MultipartFile boardPicFile, MultipartFile openingPermitFile,
								  MultipartFile bankCardFrontFile, MultipartFile bankIdcardFile*/) {
		try {
			//系统生成8位MerchantNo
			String mchtNo = IdUtil.createMchtId();
			//创建者UserId
			Long operatorId =  UserUtils.getUser().getId();
			//将页面请求参数转换成商户实体bo
			MerchantForm merchantForm = new MerchantForm(request);
        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(merchantForm.getCode())){
        		//转换成功,调用商户service执行新增商户操作
        		merchantForm.setMchtCode(mchtNo);
        		merchantForm.setOperatorId(operatorId);

				//校验商户简称重复
				MchtInfo mchtInfo = new MchtInfo();
				mchtInfo.setShortName(merchantForm.getShortName());
				List<MerchantForm> mchtInfos = merchantAdminService.getMchtInfoList(mchtInfo);
				if (!CollectionUtils.isEmpty(mchtInfos)){
					redirectAttributes.addFlashAttribute("message", "商户简称重复！");
					redirectAttributes.addFlashAttribute("messageType", "error");
					return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
				}
				//将 是否显示支付结果页、商户标签 的信息，封装成json格式的数据。存入extend2字段中
				JSONObject extend2Json = new JSONObject();
				extend2Json.put("isShowPayResultPage", merchantForm.getIsShowPayResultPage());
				extend2Json.put("mchtPropertyTag", this.geneMchtPropertyTagStr(merchantForm.getMchtPropertyTag()));
				extend2Json.put("isShowPayPage",merchantForm.getIsShowPayPage());
				merchantForm.setExtend2(extend2Json.toJSONString());

				String result = merchantAdminService.addMerchantService(merchantForm);
        		if("success".equals(result)){
        			redirectAttributes.addFlashAttribute("message", "保存商户信息成功！");
        			redirectAttributes.addFlashAttribute("messageType", "success");
        			return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
        		}
        	}else{
        		redirectAttributes.addFlashAttribute("message", "操作失败！");
				redirectAttributes.addFlashAttribute("messageType", "error");
				return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
        	}
			
			
		} catch (Exception e) {
			logger.error("商户信息添加",e);
		}
		return "modules/merchant/merchantEdit";
	}
	
	@RequestMapping(value = { "editMerchantSave", "" })
	public String editSave(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes
		    /*MultipartFile blcFile, MultipartFile contractFile, MultipartFile boardPicFile, MultipartFile openingPermitFile,
		    MultipartFile bankCardFrontFile, MultipartFile bankIdcardFile*/
			) {
		try {
			//创建者UserId
			Long operatorId =  UserUtils.getUser().getId();
			//将页面请求参数转换成商户实体bo
			MerchantForm merchantForm = new MerchantForm(request);
			MerchantForm mchtInfo = null;
        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(merchantForm.getCode())){
        		//转换成功,调用商户service执行更新商户操作
        		merchantForm.setOperatorId(operatorId);
        		//查出该商户中的extend2中已经存在的值
                if(null != merchantForm){
                    mchtInfo = merchantAdminService.getMerchantById(merchantForm.getId());
                    if(null != mchtInfo){
						JSONObject extend2Json = null;
						if(StringUtils.isNotBlank(mchtInfo.getExtend2())) {
							extend2Json = JSONObject.parseObject(mchtInfo.getExtend2());
							//设置isShowPayResultPage的值
							if (extend2Json.containsKey("isShowPayResultPage")) {
								extend2Json.remove("isShowPayResultPage");
							}
							extend2Json.put("isShowPayResultPage", merchantForm.getIsShowPayResultPage());

							if (extend2Json.containsKey("isShowPayPage")) {
								extend2Json.remove("isShowPayPage");
							}
							extend2Json.put("isShowPayPage", merchantForm.getIsShowPayPage());

							//设置mchtPropertyTag的值
							if (extend2Json.containsKey("mchtPropertyTag")) {
								extend2Json.remove("mchtPropertyTag");
							}
							String mchtPropertyTagStr = this.geneMchtPropertyTagStr(merchantForm.getMchtPropertyTag());
							extend2Json.put("mchtPropertyTag", mchtPropertyTagStr);
						}else{
							extend2Json = new JSONObject();
							//设置isShowPayResultPage的值
							extend2Json.put("isShowPayResultPage", merchantForm.getIsShowPayResultPage());
							//设置mchtPropertyTag的值
							extend2Json.put("mchtPropertyTag", merchantForm.getMchtPropertyTag());

							extend2Json.put("isShowPayPage", merchantForm.getIsShowPayPage());
						}
						merchantForm.setExtend2(extend2Json.toJSONString());
                    }
                }

				MchtInfo mi = new MchtInfo();
				BeanUtils.copyProperties(merchantForm, mi);
				logger.info("校验商户与代理商费率,商户"+JSON.toJSON(mi));
				String errMsg = platFeerateService.checkMchtAndAgentFee(mi,null,null,null);
        		if(StringUtils.isNotBlank(errMsg)){
					redirectAttributes.addFlashAttribute("message", "更新失败"+errMsg);
					redirectAttributes.addFlashAttribute("messageType", "error");
					return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
				}
				String result = merchantAdminService.updateMerchantService(merchantForm);
        		if("success".equals(result)){
        			redirectAttributes.addFlashAttribute("message", "更新商户信息成功！");
        			redirectAttributes.addFlashAttribute("messageType", "success");
        			return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
        		}
        	}else{
        		redirectAttributes.addFlashAttribute("message", "操作失败！");
				redirectAttributes.addFlashAttribute("messageType", "error");
        	}
			
		} catch (Exception e) {
			logger.error("商户信息修改",e);
		}
		return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
	}

    /**
     * 将mchtPropertyTag的值以&符号拼接
     * @param mchtPropertyTag
     * @return
     */
    private String geneMchtPropertyTagStr(String mchtPropertyTag) {
        if(StringUtils.isNotBlank(mchtPropertyTag)){
            if(mchtPropertyTag.contains(",")){
                String[] isMchtPropertyTagArr = mchtPropertyTag.split(",");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < isMchtPropertyTagArr.length; i++) {
                    sb.append(isMchtPropertyTagArr[i]).append("&");
                }
                String isMchtPropertyTagArrVal = sb.toString();
                if(StringUtils.isNotBlank(isMchtPropertyTagArrVal) && isMchtPropertyTagArrVal.contains("&")){
                    return isMchtPropertyTagArrVal;
                }
            }else{
                return mchtPropertyTag;
            }
        }
        return "";
    }

//	@RequestMapping(value = {"detailByLoginUser", ""})
//	public String detailByLoginUser(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
//        try {
//        	User user = UserUtils.getUser();
//        	String url = GlobalConfig.getConfig("boss.url")+"merchant/getByMchtNo";
//        	Map<String,String> params = new HashMap<String, String>();
//        	params.put("no", user.getNo());
//        	String resp = HttpUtil.post(url, params);
////        	DataResponse dataResponse = JSONObject.parseObject(resp, DataResponse.class);
////        	MchtInfo merchant = new MchtInfo();
////        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(dataResponse.getCode())){
////        		String data = JSONObject.toJSONString(dataResponse.getData());
////        		merchant = JSONObject.parseObject(data,MchtInfo.class);
////        	}
////            Page<MchtInfo> page = new Page<>(request, response);
////            page.setList(new ArrayList<MchtInfo>());
////            model.addAttribute("page", page);
////            model.addAttribute("merchant", merchant);
////            model.addAttribute("list", new ArrayList<MchtInfo>());
//        } catch (Exception e) {
//        	e.printStackTrace();
//            logger.error(e.getMessage(), e);
//        }
//        return "modules/merchant/merchantDetail";
//	}
//
//	@RequestMapping(value = {"detailByNo", ""})
//	public String detailByNo(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
//        try {
//        	User user = UserUtils.getUser();
//        	String no = request.getParameter("mchtNo");
//        	String url = GlobalConfig.getConfig("boss.url")+"merchant/getByMchtNo";
//        	Map<String,String> params = new HashMap<String, String>();
//        	params.put("no", no);
//        	String resp = HttpUtil.post(url, params);
////        	DataResponse dataResponse = JSONObject.parseObject(resp, DataResponse.class);
////        	MchtInfo merchant = new MchtInfo();
////        	if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(dataResponse.getCode())){
////        		String data = JSONObject.toJSONString(dataResponse.getData());
////        		merchant = JSONObject.parseObject(data,MchtInfo.class);
////        	}
////            Page<MchtInfo> page = new Page<>(request, response);
////            page.setList(new ArrayList<MchtInfo>());
////            model.addAttribute("page", page);
////            model.addAttribute("merchant", merchant);
////            model.addAttribute("list", new ArrayList<MchtInfo>());
//        } catch (Exception e) {
//        	e.printStackTrace();
//            logger.error(e.getMessage(), e);
//        }
//        return "modules/merchant/merchantDetail";
//	}
	

	/**
	 * 商户列表
	 */
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
		MchtInfo mchtInfo = new MchtInfo();
		mchtInfo.setMchtCode(paramMap.get("mchtCode"));
		mchtInfo.setName(paramMap.get("mchtName"));
		mchtInfo.setServiceMobile(paramMap.get("serviceMobile"));
		mchtInfo.setSignType(paramMap.get("signType"));

		//分页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString)) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		mchtInfo.setPageInfo(pageInfo);

		List<MerchantForm> mchtInfoList = merchantAdminService.getMchtInfoList(mchtInfo);
//		model.addAttribute("list", mchtInfoList);
		int mchtCount = merchantAdminService.mchtCount(mchtInfo);
		Page page = new Page(pageNo,pageInfo.getPageSize(),mchtCount,mchtInfoList,true);
		model.addAttribute("page", page);
		model.addAttribute("paramMap", paramMap);
		return "modules/merchant/merchantList";
	}
	
	/**
	 * 商户简称查询
	 */
	@ResponseBody
	@RequestMapping(value = {"checkShortName", ""})
	public String select(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {

		JSONObject result = new JSONObject();

		MchtInfo mchtInfo = new MchtInfo();
		mchtInfo.setShortName(paramMap.get("shortName"));
		List<MerchantForm> mchtInfoList = merchantAdminService.getMchtInfoList(mchtInfo);
		int count = 0;
		if (mchtInfoList != null){
			count = mchtInfoList.size();
		}

		String op = paramMap.get("op");
		if ("add".equals(op)) {
			result.put("count", count);
		} else {

			if (count == 1){
				MerchantForm merchantForm = merchantAdminService.getMerchantById(paramMap.get("id"));
				if (mchtInfo.getShortName().equals(merchantForm.getShortName())){
					result.put("count", 0);
				}else {
					result.put("count", count);
				}
			}else{
				result.put("count", count);
			}

		}

        return result.toJSONString();
	}

	/**
	 * 商户删除
	 */
	@RequestMapping(value = {"deleteMcht"})
	public String deleteMcht(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap,RedirectAttributes redirectAttributes) {

		int result;
		String message = "", messageType = null;

		boolean chan = false, pro = false;

		ChanMchtFormInfo chanMchtFormInfo = new ChanMchtFormInfo();
		chanMchtFormInfo.setMchtId(paramMap.get("id"));
		List<ChanMchtFormInfo> chanMchtFormInfos = chanMchtAdminService.getChannelListSimple(chanMchtFormInfo);

		if (CollectionUtils.isEmpty(chanMchtFormInfos)){
			chan = true;

			MchtProductFormInfo mchtProductFormInfo = new MchtProductFormInfo();
			mchtProductFormInfo.setMchtId(paramMap.get("id"));
			List<MchtProductFormInfo> mchtProductFormInfos = mchtProductAdminService.getProductList(mchtProductFormInfo);

			if (CollectionUtils.isEmpty(mchtProductFormInfos)){
				pro = true;
			}else {
				message = "该商户已配置了产品，无法删除";
				messageType = "error";
			}

		}else {
			message = "该商户已配置通道商户支付方式，无法删除";
			messageType = "error";
		}


		if (chan && pro){
			result = merchantAdminService.deleteMerchantById(paramMap.get("id"));
			if (result == 1){
				message = "删除成功";
				messageType = "success";
			}
		}

		redirectAttributes.addFlashAttribute("messageType", messageType);
		redirectAttributes.addFlashAttribute("message", message);
		response.setCharacterEncoding("UTF-8");
		return "redirect:"+ GlobalConfig.getAdminPath()+"/merchant/list";
	}

	/*private String uploadPicture(MultipartFile file, HttpServletRequest request,String filename) throws Exception {
		// 上传文件路径
		String path = picPath+"webupload/mchtpic/";
		// 文件扩展名
		String prefix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
		// 上传文件名
		filename = filename + "." + prefix;
		File filepath = new File(path, filename);
		// 判断路径是否存在，如果不存在就创建一个
		if (!filepath.getParentFile().exists()) {
			filepath.getParentFile().mkdirs();
		}
		// 将上传文件保存到一个目标文件当中
		file.transferTo(new File(path + filename));

		return "/webupload/mchtpic/"+filename;
	}*/

}
