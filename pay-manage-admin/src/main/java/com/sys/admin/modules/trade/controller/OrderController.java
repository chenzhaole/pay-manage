/**
 * Copyright &copy; 2012-2013  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sys.admin.modules.trade.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.enums.AdminPayTypeEnum;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConfigUtil;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.channel.bo.ChanMchtFormInfo;
import com.sys.admin.modules.channel.service.ChanMchtAdminService;
import com.sys.admin.modules.platform.bo.ProductFormInfo;
import com.sys.admin.modules.platform.service.ProductAdminService;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.admin.modules.trade.service.OrderAdminService;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.PayStatusEnum;
import com.sys.common.enums.PayTypeEnum;
import com.sys.common.util.*;
import com.sys.core.dao.common.PageInfo;
import com.sys.core.dao.dmo.*;
import com.sys.core.service.*;
import com.sys.trans.api.entry.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.sys.boss.api.service.order.OrderProxypay4ManageService;
//import com.sys.core.dao.dmo.CpInfo;
//import com.sys.core.service.ConfigSysService;

@SuppressWarnings("MVCPathVariableInspection")
@Controller
@RequestMapping(value = "${adminPath}/order")
public class OrderController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private MchtGwOrderService mchtGwOrderService;
	@Autowired
	private MerchantService merchantService;
//	@Autowired
//	private OrderProxypay4ManageService orderProxypay4ManageService;
//	@Autowired
//	private ConfigSysService configSysService;
	@Autowired
	private MchtProductService mchtProductService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ChanMchtPaytypeService chanMchtPaytypeService;
	@Autowired
	private ChannelService channelService;

	@Autowired
	private ProductAdminService productAdminService;

	@Autowired
	private ChanMchtAdminService chanMchtAdminService;

	@Autowired
	private OrderAdminService orderAdminService;

	@Autowired
	private MchtAccountDetailService mchtAccountDetailService;

    //商户账户详情信息接口地址
    @Value("${oneMchtAccountDetail.url}")
    private String mchtAccountDetailUrl;

    //查询所有商户账户详情信息接口地址
    @Value("${mchtAllAccountDetailData.url}")
    private String mchtAllAccountDetailUrl;

	//汇总商户总金额接口地址
	@Value("${mchtCashTotalAmountUrl.url}")
    private String mchtCashTotalAmountUrl;

	//汇总商户可用余额接口地址
	@Value("${mchtSettleTotalAmountUrl.url}")
	private String mchtSettleTotalAmountUrl;



	@RequiresPermissions("process:question:view")
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response, HttpSession session,
					   Model model, @RequestParam Map<String, String> paramMap) {

		List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
		//支付产品列表
		List<PlatProduct> productList = productService.list(new PlatProduct());
		//通道商户支付方式列表
		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());
		//  上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
		//查询商户列表
		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> chanMPMap = Collections3.extractToMap(chanMchtPaytypeList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtList, "id", "name");
		Map<String, String> productMap = Collections3.extractToMap(productList, "id", "name");


		//支付方式
		AdminPayTypeEnum[] payTypeList = AdminPayTypeEnum.values();
		model.addAttribute("paymentTypeInfos", payTypeList);
		model.addAttribute("chanInfoList", chanInfoList);
		model.addAttribute("mchtList", mchtList);
		model.addAttribute("productList", productList);
		model.addAttribute("chanMchtPaytypeList", chanMchtPaytypeList);

		if ("no".equals(paramMap.get("query"))) {
			return "modules/order/orderList";
		}

		//创建查询实体
		MchtGatewayOrder order = new MchtGatewayOrder();
		assemblySearch(paramMap, order);

		//获取当前第几页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		order.setPageInfo(pageInfo);

		int orderCount = orderAdminService.ordeCount(order);

		model.addAttribute("paramMap", paramMap);

		if (orderCount == 0) {
			return "modules/order/orderList";
		}

		List<MchtGatewayOrder> orderList = orderAdminService.list(order);

		if (CollectionUtils.isEmpty(orderList)) {
			return "modules/order/orderList";
		}

		for (MchtGatewayOrder gwOrder : orderList) {
			gwOrder.setMchtCode(mchtMap.get(gwOrder.getMchtCode()));
			gwOrder.setPlatProductId(productMap.get(gwOrder.getPlatProductId()));
			gwOrder.setChanId(channelMap.get(gwOrder.getChanId()));
			gwOrder.setChanMchtPaytypeId(chanMPMap.get(gwOrder.getChanMchtPaytypeId()));
		}
		Page page = new Page(pageNo, pageInfo.getPageSize(), orderCount, orderList, true);
		model.addAttribute("page", page);

		//金额总数
		BigDecimal amount = new BigDecimal(orderAdminService.amount(order)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

		//支付成功
		order.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
		//支付成功总数
		long successCount = orderAdminService.ordeCount(order);
		//支付成功金额总数
		BigDecimal successAmount = new BigDecimal(orderAdminService.amount(order)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

		model.addAttribute("orderCount", orderCount);
		model.addAttribute("successCount", successCount);
		model.addAttribute("amount", amount.toString());
		model.addAttribute("successAmount", successAmount.toString());

		return "modules/order/orderList";
	}


	@RequestMapping(value = {"select", ""})
	public String select(HttpServletRequest request, HttpServletResponse response, HttpSession session,
						 Model model, @RequestParam Map<String, String> paramMap) {
		try {
			User user = UserUtils.getUser();
			String mchtNo = user.getNo();
			//如果是内部管理员则显示所有商户的数据
			if ("1001".equals(mchtNo)) {
				mchtNo = "";
			}
			String pageNum = request.getParameter("pageNum");
			String pageSize = request.getParameter("pageSize");
			String id = request.getParameter("id");
			String mchtSeq = request.getParameter("customerSeq");
			String platSeq = request.getParameter("platformSeq");
			String status = request.getParameter("status");
			String beginDate = request.getParameter("beginDate");
			String endDate = request.getParameter("endDate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("pageNum", pageNum);
			params.put("pageSize", pageSize);
			params.put("id", id);
			params.put("mchtNo", mchtNo);
			params.put("mchtSeq", mchtSeq);
			params.put("platSeq", platSeq);
			params.put("status", status);
			params.put("beginDate", beginDate);
			params.put("endDate", endDate);

			List<JSONObject> tmpList = new ArrayList<JSONObject>();
			List<Map> list = new ArrayList<Map>();
			PageInfo page = null;

//            String url = GlobalConfig.getConfig("boss.url")+"platform/listGatewayOrder";
//            String resp = HttpUtil.post(url, params);
//            DataResponse dataResponse = JSONObject.parseObject(resp, DataResponse.class);
//            if(ErrorCodeEnum.SUCCESS.getCode().equalsIgnoreCase(dataResponse.getCode())){
//            	String data = JSONObject.toJSONString(dataResponse.getData());
//            	page = JSONObject.parseObject(data, PageInfo.class);
//            }

			model.addAttribute("page", page);
			model.addAttribute("paramMap", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		return "modules/order/orderList";
	}

	/**
	 * 订单详情
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @param paramMap
	 * @return
	 */
	@RequiresPermissions("process:question:view")
	@RequestMapping(value = {"detail", ""})
	public String detail(HttpServletRequest request, HttpServletResponse response, HttpSession session,
						 Model model, @RequestParam Map<String, String> paramMap) {
		try {
			//读取订单基本信息
			MchtGatewayOrder searchBo = new MchtGatewayOrder();
			assemblySearch(paramMap, searchBo);

			//根据订单id和时间查询订单，时间用来2定位分表
			List<MchtGatewayOrder> orderList = orderAdminService.list(searchBo);
			if (orderList != null && orderList.size() > 0) {
				MchtGatewayOrder order = orderList.get(0);
				//根据商户id查询商户信息
				MchtInfo mchtInfo = merchantService.queryByKey(order.getMchtCode());
				model.addAttribute("mchtInfo", mchtInfo);
				ChanInfo chanInfo = channelService.queryByKey(order.getChanCode());
				model.addAttribute("chanInfo", chanInfo);
				ChanMchtFormInfo chanMchtPaytype = chanMchtAdminService.getChanMchtPaytypeById(order.getChanMchtPaytypeId());
				model.addAttribute("chanMchtPaytype", chanMchtPaytype);
				ProductFormInfo platProduct = productAdminService.getPlatProductById(order.getPlatProductId());
				model.addAttribute("platProduct", platProduct);

				model.addAttribute("orderInfo", orderList.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		return "modules/order/orderDetail";
	}

	@RequestMapping(value = {"balance", ""})
	public String balance(HttpServletRequest request, HttpServletResponse response, HttpSession session,
						  Model model, @RequestParam Map<String, String> paramMap) {
		try {
			//查出所有商户信息
			List<MchtInfo> mchtInfoList = merchantService.list(new MchtInfo());

			Map<String, String> mchtMap = Collections3.extractToMap(mchtInfoList, "id", "name");

			MchtAccountDetail selectMchtAccountDetail = new MchtAccountDetail();
			//获取当前第几页
			String pageNoString = paramMap.get("pageNo");
			int pageNo = 1;
			if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
				pageNo = Integer.parseInt(pageNoString);
			}
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageNo(pageNo);
			selectMchtAccountDetail.setPageInfo(pageInfo);
			String mchtId = StringUtils.isNotBlank(request.getParameter("mchtId")) ? request.getParameter("mchtId") : "";
			//截至到某个时间的余额
			String queryDate = StringUtils.isNotBlank(request.getParameter("queryDate")) ? request.getParameter("queryDate") : DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
            List<MchtAccountDetail> listMchtAccountDetail = new ArrayList<>();
            //通过左侧菜单栏进入，不查数据
            String isSelectInfo = request.getParameter("isSelectInfo");
            long count = 0;
            if(StringUtils.isNotBlank(isSelectInfo) && "0".equals(isSelectInfo)){
				if(StringUtils.isNotBlank(mchtId)){
					selectMchtAccountDetail.setMchtId(mchtId);
					listMchtAccountDetail = queryMchtAccountDetailByHttp(selectMchtAccountDetail);
					if(!CollectionUtils.isEmpty(listMchtAccountDetail)){
						count = listMchtAccountDetail.size();
					}
				}else{
					selectMchtAccountDetail.setCreateTime(DateUtils.parseDate(queryDate, "yyyy-MM-dd HH:mm:ss"));
					listMchtAccountDetail = queryListLastOneMchtAccountDetailDataByHttp(selectMchtAccountDetail);
					count = queryListLastOneMchtAccountDetailCount(selectMchtAccountDetail);
				}
				if(!CollectionUtils.isEmpty(listMchtAccountDetail)){
					for(MchtAccountDetail mchtAccountDetail : listMchtAccountDetail){
						//商户名称
						mchtAccountDetail.setMchtName(mchtMap.get(mchtAccountDetail.getMchtId()));
						//商户总金额
						BigDecimal cashTotalAmount = mchtAccountDetail.getCashTotalAmount();
						cashTotalAmount = cashTotalAmount.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
						mchtAccountDetail.setCashTotalAmount(cashTotalAmount);
						//商户冻结金额
						BigDecimal freezeTotalAmount = mchtAccountDetail.getFreezeTotalAmount();
						freezeTotalAmount = freezeTotalAmount.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
						mchtAccountDetail.setFreezeTotalAmount(freezeTotalAmount);
						//商户可用余额,可用余额=总金额-冻结金额
						BigDecimal settleTotalAmount = cashTotalAmount.subtract(freezeTotalAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
						mchtAccountDetail.setSettleTotalAmount(settleTotalAmount);
					}
				}
			}

			Page page = new Page(pageNo, pageInfo.getPageSize(), count, listMchtAccountDetail, true);
			model.addAttribute("page", page);
			BigDecimal mchtTotalBalance = null;
			BigDecimal mchtAvailTotalBalance = null;
			BigDecimal mchtFreezeTotalAmountBalance = null;
			if(!CollectionUtils.isEmpty(listMchtAccountDetail) && listMchtAccountDetail.size() > 1){
                // 商户总金额合计（元）
                mchtTotalBalance = this.statisticsMchtTotalBalance(selectMchtAccountDetail);
                mchtTotalBalance = null!=mchtTotalBalance ? mchtTotalBalance.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
                // 商户可用余额合计（元）
                mchtAvailTotalBalance = this.statisticsMchtAvailTotalBalance(selectMchtAccountDetail);
				mchtAvailTotalBalance = null!=mchtAvailTotalBalance ? mchtAvailTotalBalance.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
				//商户冻结金额合计
				mchtFreezeTotalAmountBalance = mchtTotalBalance.subtract(mchtAvailTotalBalance);

			}else if(!CollectionUtils.isEmpty(listMchtAccountDetail) && 1 == listMchtAccountDetail.size()){
				MchtAccountDetail oneMchtAccountDetail = listMchtAccountDetail.get(0);
				//单个商户的总金额汇总，可用总金额汇总
				// 商户总金额合计（元）
				mchtTotalBalance = oneMchtAccountDetail.getCashTotalAmount();
				// 商户可用余额合计（元）
				mchtAvailTotalBalance = oneMchtAccountDetail.getCashTotalAmount().subtract(oneMchtAccountDetail.getFreezeTotalAmount());
				//商户冻结金额合计
				mchtFreezeTotalAmountBalance = mchtTotalBalance.subtract(mchtAvailTotalBalance);
			}
			model.addAttribute("mchtInfoList", mchtInfoList);
			model.addAttribute("mchtId", mchtId);
			model.addAttribute("queryDate", queryDate);
			model.addAttribute("mchtTotalBalance", mchtTotalBalance);
			model.addAttribute("mchtAvailTotalBalance", mchtAvailTotalBalance);
			model.addAttribute("mchtFreezeTotalAmountBalance", mchtFreezeTotalAmountBalance);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return "modules/order/balanceList";
	}

    //商户总金额合计（元）
    private BigDecimal statisticsMchtTotalBalance(MchtAccountDetail selectMchtAccountDetail) {
		String retData = "";
		try {
			String url = mchtCashTotalAmountUrl;
			logger.info("汇总商户总金额合计（元），请求地址："+url);
			Map paramsMap = new HashMap();
			paramsMap.put("params", URLEncoder.encode(JSONObject.toJSONString(selectMchtAccountDetail), "utf-8"));
			logger.info("汇总商户总金额合计（元），请求参数："+paramsMap);
			retData = HttpUtil.postConnManager(url, paramsMap);
			logger.info("汇总商户总金额合计（元），接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return new BigDecimal(retData).setScale(4, BigDecimal.ROUND_HALF_UP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }

    //商户可用余额合计（元）
    private BigDecimal statisticsMchtAvailTotalBalance(MchtAccountDetail selectMchtAccountDetail) {
		String retData = "";
		try {
			String url = mchtSettleTotalAmountUrl;
			logger.info("汇总商户可用余额合计（元），请求地址："+url);
			Map paramsMap = new HashMap();
			paramsMap.put("params", URLEncoder.encode(JSONObject.toJSONString(selectMchtAccountDetail), "utf-8"));
			logger.info("汇总商户可用余额合计（元），请求参数："+paramsMap);
			retData = HttpUtil.postConnManager(url, paramsMap);
			logger.info("汇总商户可用余额合计（元），接口返回的数据为："+JSONObject.toJSONString(retData));
			if(StringUtils.isNotBlank(retData)){
				return new BigDecimal(retData).setScale(4, BigDecimal.ROUND_HALF_UP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }

    /**
	 *  查询所有商户账户详情信息的个数
	 * @return
	 */
	private long queryListLastOneMchtAccountDetailCount(MchtAccountDetail selectMchtAccountDetail) {
		return mchtAccountDetailService.queryLastOneMchtAccountDetailCount(selectMchtAccountDetail);
	}

	/**
     *  查询所有商户账户详情信息
     * @return
     */
    private List<MchtAccountDetail> queryListLastOneMchtAccountDetailDataByHttp(MchtAccountDetail selectMchtAccountDetail) {
        String retData = "";
        try {
	        String url = mchtAllAccountDetailUrl;
	        logger.info("查询所有商户账户详情信息，请求地址："+url);
	        Map paramsMap = new HashMap();
	        paramsMap.put("params", URLEncoder.encode(JSONObject.toJSONString(selectMchtAccountDetail), "utf-8"));
	        logger.info("查询所有商户账户详情信息，请求参数："+paramsMap);
            retData = HttpUtil.postConnManager(url, paramsMap);
            logger.info("查询所有商户账户详情信息，接口返回的数据为："+JSONObject.toJSONString(retData));
            if(StringUtils.isNotBlank(retData)){
                return JSONArray.parseArray(retData, MchtAccountDetail.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 商户账户详情信息
     */
    private List<MchtAccountDetail> queryMchtAccountDetailByHttp(MchtAccountDetail selectMchtAccountDetail) {
        String url = mchtAccountDetailUrl+"/"+selectMchtAccountDetail.getMchtId();
        logger.info("商户首页，查询商户账户详情信息，请求地址："+url);
        String retData = "";
        try {
            retData = HttpUtil.postConnManager(url, null);
            logger.info("商户首页，查询商户账户详情信息，接口返回的数据为："+JSONObject.toJSONString(retData));
            if(StringUtils.isNotBlank(retData) && !"null".equals(retData)){
                MchtAccountDetail mchtAccountDetail = JSONObject.parseObject(retData, MchtAccountDetail.class);
                List<MchtAccountDetail> list = new ArrayList<>();
                list.add(mchtAccountDetail);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	@RequestMapping(value = {"payQRCode", ""})
	public String payQRCode(HttpServletRequest request, HttpServletResponse response, HttpSession session,
							Model model, @RequestParam Map<String, String> paramMap) {
		try {
			User user = UserUtils.getUser();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		return "modules/order/payQRCode";
	}

	/**
	 * 查补单
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @param paramMap
	 * @return
	 */
	@RequestMapping("querySupply")
	public String querySupply(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes,
							  Model model, @RequestParam Map<String, String> paramMap) {
		String message = "查单失败";
		try {
			String gatewayUrl = ConfigUtil.getValue("gateway.url");
			String queryUrl = gatewayUrl + "/gateway/queryOrder";
			MchtGatewayOrder searchBo = new MchtGatewayOrder();
			searchBo.setId(paramMap.get("orderId"));
			String suffix = "20" + searchBo.getId().substring(1, 5);
			searchBo.setSuffix(suffix);
			List<MchtGatewayOrder> orderList = orderAdminService.list(searchBo);
			MchtGatewayOrder order;
			if (orderList != null && orderList.size() > 0) {
				order = orderList.get(0);
			} else {
				redirectAttributes.addFlashAttribute("message", "查无此单！");
				redirectAttributes.addFlashAttribute("messageType", "error");
				return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
			}
			MchtInfo mchtInfo = merchantService.queryByKey(order.getMchtCode());
			if (mchtInfo == null) {
				redirectAttributes.addFlashAttribute("message", "查无此商户！");
				redirectAttributes.addFlashAttribute("messageType", "error");
				return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
			}
			String key = mchtInfo.getMchtKey();

			JSONObject data = new JSONObject();
			JSONObject head = new JSONObject();
			JSONObject body = new JSONObject();
			head.put("mchtId", mchtInfo.getId());
			head.put("version", "20");
			head.put("biz", order.getPayType());
			data.put("head", head);
			body.put("tradeId", order.getId());
			body.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(order.getCreateTime()));
			Map<String, String> params = JSONObject.parseObject(
					JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
					});
            String log_moid = mchtInfo.getId()+"-->"+order.getId();
            String sign = SignUtil.md5Sign(params, key, log_moid);
			data.put("sign", sign);
			data.put("body", body);
			String respStr = HttpUtil.post(queryUrl, data.toJSONString());
			logger.info("gateway查单返回：" + respStr);
			JSONObject result = JSON.parseObject(respStr);
			JSONObject resultHead = result.getJSONObject("head");
			JSONObject resultBody = result.getJSONObject("body");
			if (resultBody != null && ErrorCodeEnum.SUCCESS.getCode().equals(resultHead.getString("respCode"))) {
				String resultStatus = resultBody.getString("status");
				if (Result.STATUS_SUCCESS.equals(resultStatus)) {

					//补发通知
					String supplyUrl = gatewayUrl + "/renotify";
					Map<String, String> redata = new HashMap<>();
					redata.put("orderId", order.getId());
					redata.put("suffix", suffix);
					String reNoStr = HttpUtil.post(supplyUrl, redata);
					logger.info("gateway补发通知返回：" + reNoStr);

					if ("SUCCESS".equalsIgnoreCase(reNoStr)) {
						order.setSupplyStatus("0");
						message = "补发成功";
					} else {
						order.setSupplyStatus("1");
						message = "已补发，商户响应：" + reNoStr;
					}

					redirectAttributes.addFlashAttribute("message", "查单成功," + message);
					redirectAttributes.addFlashAttribute("messageType", "success");
					return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
				} else if (Result.STATUS_FAIL.equals(resultStatus)) {
					redirectAttributes.addFlashAttribute("message", "查单成功, 支付状态为失败");
					redirectAttributes.addFlashAttribute("messageType", "success");
					return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
				} {
					redirectAttributes.addFlashAttribute("message", "查单成功, 支付状态未知");
					redirectAttributes.addFlashAttribute("messageType", "success");
					return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查补单失败，" + e.getMessage());
			message = "查补单失败, 系统错误";
			redirectAttributes.addFlashAttribute("message", message);
			redirectAttributes.addFlashAttribute("messageType", "error");
			return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
		} finally {

		}
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addFlashAttribute("messageType", "error");
		return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
	}

	@RequestMapping("supplyNotify")
	public String supplyNotify(String orderId, String suffix, RedirectAttributes redirectAttributes, HttpServletResponse response) {
		String message = "补发通知失败";
		try {
			String gatewayUrl = ConfigUtil.getValue("gateway.url");
			String supplyUrl = gatewayUrl + "/gateway/renotify";
			Map<String, String> data = new HashMap<>();
			data.put("orderId", orderId);
			data.put("suffix", suffix);
			String respStr = HttpUtil.post(supplyUrl, data);
			logger.info("gateway补发通知返回：" + respStr);
			if ("SUCCESS".equalsIgnoreCase(respStr)) {
				message = "补发成功";
			} else {
				message = "已补发，商户响应：" + respStr;
			}
			redirectAttributes.addFlashAttribute("message", message);
			redirectAttributes.addFlashAttribute("messageType", "success");
			return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("补发失败，" + e.getMessage());
			message = "补发失败，" + e.getMessage();
			redirectAttributes.addFlashAttribute("message", message);
			redirectAttributes.addFlashAttribute("messageType", "error");

		} finally {
			logger.info(message);
			return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
		}
	}

	@RequestMapping(value = "/export")
	public String export(HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes,
						 @RequestParam Map<String, String> paramMap) throws IOException {

		//创建查询实体
		MchtGatewayOrder order = new MchtGatewayOrder();
		assemblySearch(paramMap, order);

		//计算条数 上限五万条
		int orderCount = orderAdminService.ordeCount(order);
		if (orderCount <= 0) {
			redirectAttributes.addFlashAttribute("messageType", "fail");
			redirectAttributes.addFlashAttribute("message", "暂无可导出数据");
			response.setCharacterEncoding("UTF-8");
			return "redirect:" + GlobalConfig.getAdminPath() + "/order/list";
		}
		if (orderCount > 50000) {
			redirectAttributes.addFlashAttribute("messageType", "fail");
			redirectAttributes.addFlashAttribute("message", "导出条数不可超过 50000 条");
			response.setCharacterEncoding("UTF-8");
			return "redirect:" + GlobalConfig.getAdminPath() + "/order/list";
		}

		// 访问数据库，得到数据集 导出修改为查询关联表
		//List<MchtGatewayOrder> deitelVOList = orderAdminService.list(order);
		List<MchtGatewayOrder> deitelVOList = orderAdminService.listCurr(order);

		if (deitelVOList == null || deitelVOList.size() ==0) {
			redirectAttributes.addFlashAttribute("messageType", "fail");
			redirectAttributes.addFlashAttribute("message", "导出条数为0条");
			response.setCharacterEncoding("UTF-8");
			return "redirect:" + GlobalConfig.getAdminPath() + "/order/list";
		}
		List<MchtInfo> mchtList = merchantService.list(new MchtInfo());
		//支付产品列表
		List<PlatProduct> productList = productService.list(new PlatProduct());
		//上游通道列表
		List<ChanInfo> chanInfoList = channelService.list(new ChanInfo());
		//查询商户列表
		//通道商户支付方式列表
		List<ChanMchtPaytype> chanMchtPaytypeList = chanMchtPaytypeService.list(new ChanMchtPaytype());

		Map<String, String> chanMPMap = Collections3.extractToMap(chanMchtPaytypeList, "id", "name");
		Map<String, String> channelMap = Collections3.extractToMap(chanInfoList, "id", "name");
		Map<String, String> mchtMap = Collections3.extractToMap(mchtList, "id", "name");
		Map<String, String> productMap = Collections3.extractToMap(productList, "id", "name");

		for (MchtGatewayOrder gwOrder : deitelVOList) {
			if (PayStatusEnum.toEnum(gwOrder.getStatus()) != null) {
				if(PayStatusEnum.SUBMIT_SUCCESS.getCode().equals(gwOrder.getStatus())){
					gwOrder.setStatus("提交支付");
				}else{
					gwOrder.setStatus(PayStatusEnum.toEnum(gwOrder.getStatus()).getDesc());
				}
			}
			if (PayTypeEnum.toEnum(gwOrder.getPayType()) != null) {
				gwOrder.setPayType(PayTypeEnum.toEnum(gwOrder.getPayType()).getDesc());
			}
			gwOrder.setMchtId(mchtMap.get(gwOrder.getMchtId()));
			gwOrder.setPlatProductId(productMap.get(gwOrder.getPlatProductId()));
			gwOrder.setChanId(channelMap.get(gwOrder.getChanId()));
		}
		//获取当前日期，为文件名
		String fileName = DateUtils.formatDate(new Date()) + ".xls";

		String[] headers = {"商户名称", "通道商户支付方式","产品名称" , "商户订单号",
				"平台订单号","上游通道订单号", "交易金额(元)", "订单状态", "创建时间", "支付时间","外币币种","外币汇率","外币金额"};

		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
		OutputStream out = response.getOutputStream();

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("订单流水表");
		sheet.setColumnWidth(0, 20 * 1256);
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);

		int j = 0;
		for (String header : headers) {
			HSSFCell cell = row.createCell((short) j);
			cell.setCellValue(header);
			sheet.autoSizeColumn(j);
			j++;
		}
		if (!Collections3.isEmpty(deitelVOList)) {
			int rowIndex = 1;//行号
			for (MchtGatewayOrder orderTemp : deitelVOList) {
				int cellIndex = 0;
				row = sheet.createRow(rowIndex);
				HSSFCell cell = row.createCell(cellIndex);
				cell.setCellValue(mchtMap.get(orderTemp.getMchtCode()));
				cellIndex++;

//				cell = row.createCell(cellIndex);
//				cell.setCellValue(orderTemp.getChanId());
//				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(chanMPMap.get(orderTemp.getChanMchtPaytypeId()));
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(orderTemp.getPlatProductId());
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(orderTemp.getMchtOrderId());
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(orderTemp.getPlatOrderId());
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(orderTemp.getChanOrderId());
				cellIndex++;

				cell = row.createCell(cellIndex);
				if (orderTemp.getAmount() != null) {
					BigDecimal bigDecimal = NumberUtils.multiplyHundred(new BigDecimal(0.01), new BigDecimal(orderTemp.getAmount()));
					cell.setCellValue(bigDecimal.doubleValue());
				}
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(orderTemp.getStatus());
				cellIndex++;

//				cell = row.createCell(cellIndex);
//				cell.setCellValue(orderTemp.getSupplyStatus());
//				cellIndex++;

				cell = row.createCell(cellIndex);
				if (orderTemp.getCreateTime() != null) {
					cell.setCellValue(DateUtils.formatDate(orderTemp.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
				}
				cellIndex++;

				cell = row.createCell(cellIndex);
				if (orderTemp.getUpdateTime() != null) {
					cell.setCellValue(DateUtils.formatDate(orderTemp.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
				}
				cellIndex++;

				cell =row.createCell(cellIndex);
				cell.setCellValue(orderTemp.getRate().getCurrency());
				cellIndex++;

				cell =row.createCell(cellIndex);
				cell.setCellValue(orderTemp.getRate().getExchangeRate());
				cellIndex++;

				cell =row.createCell(cellIndex);
				String foreignAmout =String.valueOf(orderTemp.getRate().getAmountForeignCurrency());
				cell.setCellValue(foreignAmout);

				rowIndex++;
			}
		}
		wb.write(out);
		out.flush();
		out.close();

		redirectAttributes.addFlashAttribute("messageType", "success");
		redirectAttributes.addFlashAttribute("message", "导出完毕");
		response.setCharacterEncoding("UTF-8");
		return "redirect:" + GlobalConfig.getAdminPath() + "/order/list";
	}

	/**
	 * 组装搜索参数
	 *
	 * @param paramMap
	 * @return
	 */
	public void assemblySearch(Map<String, String> paramMap, MchtGatewayOrder order) {

		String id = paramMap.get("id");
		if (StringUtils.isNotBlank(id)) {
			order.setId(id);
		}

		//设置平台订单号
		if (StringUtils.isNotBlank(paramMap.get("platformSeq"))) {
			order.setPlatOrderId(paramMap.get("platformSeq").trim());
		}

		//设置商户订单号
		if (StringUtils.isNotBlank(paramMap.get("customerSeq"))) {
			order.setMchtOrderId(paramMap.get("customerSeq").trim());
		}

		//登陆用户名称=商户ID
//		User user = UserUtils.getUser();
//		String loginName = user.getLoginName();
//		if ("administrator".equals(loginName) || "root".equals(loginName) || "yunying".equals(loginName)) {
//			order.setMchtId("");
//		} else {
//			order.setMchtId(loginName);
//		}

		//初始化页面开始时间
		String beginDate = paramMap.get("beginDate");
		if (StringUtils.isBlank(beginDate)) {
			order.setCreateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 00:00:00"));
			paramMap.put("beginDate", DateUtils.getDate("yyyy-MM-dd") + " 00:00:00");
		} else {
			paramMap.put("beginDate", beginDate);
			order.setCreateTime(DateUtils.parseDate(beginDate));
		}
		String endDate = paramMap.get("endDate");
		//初始化页面结束时间
		if (StringUtils.isBlank(endDate)) {
			order.setUpdateTime(DateUtils.parseDate(DateUtils.getDate("yyyy-MM-dd") + " 23:59:59"));
			paramMap.put("endDate", DateUtils.getDate("yyyy-MM-dd") + " 23:59:59");
		} else {
			paramMap.put("endDate", endDate);
			order.setUpdateTime(DateUtils.parseDate(endDate));
		}
		//初始化商户ID
		if (StringUtils.isNotBlank(paramMap.get("mchtId"))) {
			order.setMchtId(paramMap.get("mchtId"));
		}
		//初始化支付产品ID
		if (StringUtils.isNotBlank(paramMap.get("platProductId"))) {
			order.setPlatProductId(paramMap.get("platProductId"));
		}
		//初始化通道商户支付方式ID
		if (StringUtils.isNotBlank(paramMap.get("chanMchtPaytypeId"))) {
			order.setChanMchtPaytypeId(paramMap.get("chanMchtPaytypeId"));
		}
		//补单状态
		if (StringUtils.isNotBlank(paramMap.get("supplyStatus"))) {
			order.setSupplyStatus(paramMap.get("supplyStatus"));
		}
		//支付方式
		if (StringUtils.isNotBlank(paramMap.get("payType"))) {
			order.setPayType(paramMap.get("payType"));
		}
		//交易状态
		if (StringUtils.isNotBlank(paramMap.get("status"))) {
			order.setStatus(paramMap.get("status"));
		}
		//官方订单号
		if (StringUtils.isNotBlank(paramMap.get("officialSeq"))) {
			order.setOfficialOrderId(paramMap.get("officialSeq").trim());
		}
		//上游订单号
		if (StringUtils.isNotBlank(paramMap.get("chanSeq"))) {
			order.setChanOrderId(paramMap.get("chanSeq").trim());
		}
		if (StringUtils.isNotBlank(paramMap.get("chanId"))) {
			order.setChanId(paramMap.get("chanId"));
		}

		order.setSuffix(DateUtils.formatDate(order.getCreateTime(), "yyyyMM"));

	}

	/**
	 * 根据平台订单ID清结算成功支付订单流水
	 *
	 * @param orderId
	 * @param redirectAttributes
	 * @param response
	 * @return
	 */
	@RequestMapping("statPayOrderById")
	public String statPayOrderById(String orderId, RedirectAttributes redirectAttributes, HttpServletResponse response) {
		String message = "补入账";
		try {
			String gatewayUrl = ConfigUtil.getValue("stat.url");
			String supplyUrl = gatewayUrl + "/settle/statPayOrderById";
			Map<String, String> data = new HashMap<>();
			data.put("platOrderId", orderId);
			String respStr = HttpUtil.post(supplyUrl, data);
			logger.info("stat补入账返回：" + respStr);
			message = respStr;
			redirectAttributes.addFlashAttribute("message", message);
			redirectAttributes.addFlashAttribute("messageType", "success");
			return "redirect:" + GlobalConfig.getAdminPath() + "/order/list";

		} catch (Exception e) {
			logger.error("补入账失败", e);
			message = "补入账失败，" + e.getMessage();
			redirectAttributes.addFlashAttribute("message", message);
			redirectAttributes.addFlashAttribute("messageType", "error");

		} finally {
			logger.info(message);
			return "redirect:" + GlobalConfig.getAdminPath() + "/order/list";
		}
	}
}
