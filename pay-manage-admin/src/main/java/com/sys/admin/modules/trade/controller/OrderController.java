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
import com.sys.boss.api.service.order.MchtAccAmountService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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

	//汇总商待结算接口地址
	@Value("${mchtWaitSettleTotalAmountUrl.url}")
	private String mchtWaitSettleTotalAmountUrl;

	@Value("${payOrderListExpireSecond}")
	private String payOrderListExpireSecond;

	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private MchtAccAmountService mchtAccAmountService;



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

		BigDecimal amount 		 = new BigDecimal(0);
		long 	   successCount  = 0;
		BigDecimal successAmount = new BigDecimal(0);
		int 	   orderCount = 0;
		model.addAttribute("orderCount", orderCount);
		model.addAttribute("successCount", successCount);
		model.addAttribute("amount", amount.toString());
		model.addAttribute("successAmount", successAmount.toString());
		model.addAttribute("isstat",paramMap.get("isstat"));
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

		JSONObject orderCountSumJson = orderAdminService.orderCountSum(order);

		model.addAttribute("paramMap", paramMap);

		if (orderCountSumJson == null || !orderCountSumJson.containsKey("num")) {
			return "modules/order/orderList";
		}
		//记录总数
		orderCount = orderCountSumJson.getInteger("num");

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
		//是否统计汇总
		if("1".equals(paramMap.get("isstat"))){
			//交易开始时间:交易结束时间:商户:上游通道:支付产品:支付方式:官方订单号:商户订单号:平台订单号:上游订单号:通道商户支付方式:订单状态:补单状态
			String key = "PAYORDER:LIST:"+DateUtils.formatDate(order.getCreateTime(),"yyyy-MM-dd HH:mm:ss") +":"+DateUtils.formatDate(order.getUpdateTime(),"yyyy-MM-dd HH:mm:ss")
					+":"+order.getMchtId()+":"+order.getChanId()+":"+order.getPlatProductId()+":"+order.getPayType()+":"+order.getOfficialOrderId()
					+":"+order.getMchtOrderId()+":"+order.getPlatOrderId()+":"+order.getChanOrderId()+":"+order.getChanMchtPaytypeId()+":"+order.getStatus()+":"+order.getSupplyStatus();
			String value = getFromRedis(key);
			if(value==null||"".equals(value)){
				//金额总数
				if(!orderCountSumJson.containsKey("amount")){
					amount = new BigDecimal("0");
				}else{
					amount = new BigDecimal(orderCountSumJson.getString("amount")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
				}
                //支付成功
                order.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());
				//成功信息
				JSONObject sucOrderCountSumJson = orderAdminService.sucOrderCountSum(order);

				//支付成功总数
				if(sucOrderCountSumJson!= null && sucOrderCountSumJson.containsKey("num")){
					successCount = sucOrderCountSumJson.getLongValue("num");
				}
				//支付成功金额总数
				if(sucOrderCountSumJson!= null && sucOrderCountSumJson.containsKey("amount")){
					successAmount = new BigDecimal(sucOrderCountSumJson.getString("amount")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
				}
				insert2Redis(key,amount.toString()+","+successCount+","+successAmount.toString(),Integer.parseInt(payOrderListExpireSecond));
			}else{
				String[] values = value.split(",");
				if(values.length>=3){
					amount = new BigDecimal(values[0]);
					successCount = Long.parseLong(values[1]);
					successAmount = new BigDecimal(values[2]);
				}
			}

		}

		model.addAttribute("orderCount", orderCount);
		model.addAttribute("successCount", successCount);
		model.addAttribute("amount", amount.toString());
		model.addAttribute("successAmount", successAmount.toString());
		model.addAttribute("isstat",paramMap.get("isstat"));
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
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String sysDate = dateFormat.format(new Date());
			//截至到某个时间的余额
			String queryDate = StringUtils.isNotBlank(request.getParameter("queryDate")) ? request.getParameter("queryDate") : DateUtils.getDate("yyyy-MM-dd");
			if(queryDate.length()>10){
				queryDate = queryDate.substring(0,10);
			}

			//查出所有商户信息
			List<MchtInfo> mchtInfoList = merchantService.list(new MchtInfo());
			Map<String, String> mchtMap = Collections3.extractToMap(mchtInfoList, "id", "name");


			MchtAccountDetail selectMchtAccountDetail = new MchtAccountDetail();
			//查询当前实时余额
			if(StringUtils.isNotEmpty(queryDate) && (DateUtils.parseDate(queryDate,"yyyy-MM-dd").getTime()>=
					DateUtils.parseDate(sysDate,"yyyy-MM-dd").getTime())){

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
				queryDate = StringUtils.isNotBlank(request.getParameter("queryDate")) ? request.getParameter("queryDate") : DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
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
						listMchtAccountDetail = setMchtAccountDetail(listMchtAccountDetail, mchtMap);
					}
				}

				Page page = new Page(pageNo, pageInfo.getPageSize(), count, listMchtAccountDetail, true);
				model.addAttribute("page", page);
				BigDecimal mchtTotalBalance = null;
				BigDecimal mchtAvailTotalBalance = null;
				BigDecimal mchtFreezeTotalAmountBalance = null;
				BigDecimal mchtWaitTotalBalance =null;
				if(!CollectionUtils.isEmpty(listMchtAccountDetail) && listMchtAccountDetail.size() > 1){
					// 商户总金额合计（元）
					mchtTotalBalance = this.statisticsMchtTotalBalance(selectMchtAccountDetail);
					mchtTotalBalance = null!=mchtTotalBalance ? mchtTotalBalance.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
					// 商户待结算金额
					mchtWaitTotalBalance =this.statisticsMchtWaitTotalBalance(selectMchtAccountDetail);
					mchtWaitTotalBalance =null!=mchtWaitTotalBalance ? mchtWaitTotalBalance.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);

					// 商户可用余额合计（元）
					mchtAvailTotalBalance = this.statisticsMchtAvailTotalBalance(selectMchtAccountDetail);
					mchtAvailTotalBalance = null!=mchtAvailTotalBalance ? mchtAvailTotalBalance.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
					// 商户冻结金额合计
					// mchtFreezeTotalAmountBalance = mchtTotalBalance.subtract(mchtAvailTotalBalance);
					mchtFreezeTotalAmountBalance = mchtTotalBalance.subtract(mchtAvailTotalBalance);
					// 商户总金额合计 = 商户金额 + 结算金额  (需放到结算商户冻结金额后面)
					mchtTotalBalance =mchtTotalBalance.add(mchtWaitTotalBalance);
				}else if(!CollectionUtils.isEmpty(listMchtAccountDetail) && 1 == listMchtAccountDetail.size()){
					MchtAccountDetail oneMchtAccountDetail = listMchtAccountDetail.get(0);
					// 商户可用余额合计（元）
					mchtAvailTotalBalance = oneMchtAccountDetail.getCashTotalAmount().subtract(oneMchtAccountDetail.getFreezeTotalAmount());
					// 商户冻结金额合计
					mchtFreezeTotalAmountBalance = oneMchtAccountDetail.getFreezeTotalAmount();//mchtTotalBalance.subtract(mchtAvailTotalBalance);
					// 结算金额
					mchtWaitTotalBalance =oneMchtAccountDetail.getSettleTotalAmount();
					// 单个商户的总金额汇总，可用总金额汇总
					// 商户总金额合计 = 商户金额 + 结算金额  (需放到结算商户冻结金额后面)
					mchtTotalBalance = oneMchtAccountDetail.getCashTotalAmount().add(oneMchtAccountDetail.getSettleTotalAmount());
				}
				logger.info("查询实时余额,商户id="+mchtId+",queryDate="+queryDate+",mchtTotalBalance="+mchtTotalBalance+",mchtAvailTotalBalance="+mchtAvailTotalBalance+",mchtFreezeTotalAmountBalance="+mchtFreezeTotalAmountBalance+",mchtWaitTotalBalance="+mchtWaitTotalBalance);
				model.addAttribute("mchtInfoList", mchtInfoList);
				model.addAttribute("mchtId", mchtId);
				model.addAttribute("queryDate", queryDate);
				model.addAttribute("mchtTotalBalance", mchtTotalBalance);
				model.addAttribute("mchtAvailTotalBalance", mchtAvailTotalBalance);
				model.addAttribute("mchtFreezeTotalAmountBalance", mchtFreezeTotalAmountBalance);
				//商户待结算金额
				model.addAttribute("mchtWaitTotalBalance", mchtWaitTotalBalance);
			}else{	//查询历史

				List<MchtAccountDetail> listMchtAccountDetail = new ArrayList<>();


				Date changeQueryDate = dateFormat.parse(queryDate);
				queryDate = dateFormat.format(changeQueryDate);

				String mchtId = StringUtils.isNotBlank(request.getParameter("mchtId")) ? request.getParameter("mchtId") : "";

				MchtAccountDetailAmount amount = new MchtAccountDetailAmount();
				if(StringUtils.isNotEmpty(mchtId)){
					amount.setMchtId(mchtId);
				}
				amount.setCreatedStartTime(queryDate + " 00:00:00");
				amount.setCreatedEndTime(queryDate + " 23:59:59");
				List<MchtAccountDetailAmount> amountList = mchtAccAmountService.queryCurrentDayAcctAmount(amount);

				//商户总金额合计
				BigDecimal mchtTotalBalanceTotal = new BigDecimal("0");
				//商户可用余额合计	(现金 - 冻结)
				BigDecimal mchtAvailTotalBalanceTotal = new BigDecimal("0");
				//商户冻结金额合计
				BigDecimal mchtFreezeTotalAmountBalanceTotal = new BigDecimal("0");
				//商户待结算金额
				BigDecimal settleTotalAmountTotal = new BigDecimal("0");



				if(amountList!= null && amountList.size()>0){
					for(MchtAccountDetailAmount amountBo :amountList){
						//商户总金额合计
						mchtTotalBalanceTotal = mchtTotalBalanceTotal.add(amountBo.getCashTotalAmount());
						//商户冻结金额合计
						mchtFreezeTotalAmountBalanceTotal = mchtFreezeTotalAmountBalanceTotal.add(amountBo.getFreezeTotalAmount());
						//商户待结算金额
						settleTotalAmountTotal = settleTotalAmountTotal.add(amountBo.getSettleTotalAmount());
						///商户可用余额合计	(现金 - 冻结)
						mchtAvailTotalBalanceTotal = mchtAvailTotalBalanceTotal.add(amountBo.getCashTotalAmount().subtract(amountBo.getFreezeTotalAmount()));

						amountBo.setMchtName(mchtMap.get(amountBo.getMchtId()));

						MchtAccountDetail mchtAccountDetail = new MchtAccountDetail();
						mchtAccountDetail.setOriFreezeTotalAmount(amountBo.getOriFreezeTotalAmount());
						mchtAccountDetail.setFreezeTotalAmount(amountBo.getFreezeTotalAmount());
						mchtAccountDetail.setOriCashTotalAmount(amountBo.getOriCashTotalAmount());
						mchtAccountDetail.setCashTotalAmount(amountBo.getCashTotalAmount());
						mchtAccountDetail.setOriSettleTotalAmount(amountBo.getOriSettleTotalAmount());
						mchtAccountDetail.setSettleTotalAmount(amountBo.getSettleTotalAmount());
						mchtAccountDetail.setMchtId(amountBo.getMchtId());
						listMchtAccountDetail.add(mchtAccountDetail);
					}
				}

				// 商户总金额合计 = 商户金额 + 结算金额  (需放到结算商户冻结金额后面)
				mchtTotalBalanceTotal = mchtTotalBalanceTotal.add(settleTotalAmountTotal);
				if(!CollectionUtils.isEmpty(listMchtAccountDetail)){
					listMchtAccountDetail = setMchtAccountDetail(listMchtAccountDetail, mchtMap);
				}

				Page page = new Page(1, 1, listMchtAccountDetail.size(), listMchtAccountDetail, true);
				model.addAttribute("page", page);

				model.addAttribute("mchtInfoList", mchtInfoList);

				model.addAttribute("mchtAccountDetail", amountList);
				model.addAttribute("mchtId", mchtId);

				queryDate = StringUtils.isNotBlank(request.getParameter("queryDate")) ? request.getParameter("queryDate") : DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
				logger.info("查询历史余额,商户id="+mchtId+",queryDate="+queryDate+",mchtTotalBalance="+mchtTotalBalanceTotal+",mchtAvailTotalBalance="+mchtAvailTotalBalanceTotal+",mchtFreezeTotalAmountBalance="+mchtFreezeTotalAmountBalanceTotal+",mchtWaitTotalBalance="+settleTotalAmountTotal);
				model.addAttribute("queryDate", queryDate);
				//商户总金额合计
				model.addAttribute("mchtTotalBalance", mchtTotalBalanceTotal.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP));
				//商户可用余额合计
				model.addAttribute("mchtAvailTotalBalance", mchtAvailTotalBalanceTotal.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP));
				//商户冻结金额合计
				model.addAttribute("mchtFreezeTotalAmountBalance", mchtFreezeTotalAmountBalanceTotal.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP));
				//商户待结算金额
				model.addAttribute("mchtWaitTotalBalance", settleTotalAmountTotal.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP));
			}



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

	//商户可用余额合计（元）
	private BigDecimal statisticsMchtWaitTotalBalance(MchtAccountDetail selectMchtAccountDetail) {
		String retData = "";
		try {
			String url = mchtWaitSettleTotalAmountUrl;
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
		List<MchtGatewayOrder> deitelVOList = orderAdminService.list(order);
		//List<MchtGatewayOrder> deitelVOList = orderAdminService.listCurr(order);

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
				"平台订单号","上游通道订单号", "交易金额(元)", "订单状态", "创建时间", "支付时间"};//,"外币币种","外币汇率","外币金额"};

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
//				cellIndex++;
//
//				cell =row.createCell(cellIndex);
//				cell.setCellValue(orderTemp.getRate().getCurrency());
//				cellIndex++;
//
//				cell =row.createCell(cellIndex);
//				cell.setCellValue(orderTemp.getRate().getExchangeRate());
//				cellIndex++;
//
//				cell =row.createCell(cellIndex);
//				String foreignAmout =String.valueOf(orderTemp.getRate().getAmountForeignCurrency());
//				cell.setCellValue(foreignAmout);

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

	/**
	 *
	 * @Title: 缓存数据到redis
	 * @throws
	 */
	public void insert2Redis(String key,String value,int expireSecond) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// NX是不存在时才set， XX是存在时才set， EX是秒，PX是毫秒
			jedis.set(key,value,"NX","EX",expireSecond);

		} catch (Exception e) {
			logger.error("redis insert error: {}", e.getMessage(),e);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
	}

	/**
	 * 从redis缓存中获取数据
	 * @param key
	 * @return
	 */
	public String getFromRedis(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			logger.error("redis insert error: {}", e.getMessage(),e);
		} finally {
			if (jedis != null) {
				jedisPool.returnResource(jedis);
			}
		}
		return null;
	}


	/**
	 * 批量补发异步通知  商户订单补单状态为空 订单状态为成功
	 * 2018-12-11 14:36:40
	 * @return
	 */
	@RequestMapping("/batchReissueMchtNotify")
	public String batchReissueMchtNotify(HttpServletRequest request, @RequestParam Map<String, String> paramMap){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("modules/order/orderList");

		MchtGatewayOrder order = new MchtGatewayOrder();
		assemblySearch(paramMap, order);
		order.setStatus(PayStatusEnum.PAY_SUCCESS.getCode());

		if(order.getCreateTime()!=null){
			order.setSuffix(DateUtils.formatDate(order.getCreateTime(), "yyyyMM"));
		}


		//获取当前第几页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		order.setPageInfo(pageInfo);

		List<MchtGatewayOrder> mchtGatewayOrders =  mchtGwOrderService.queryMchtGatewayOrdersNoPage(order, 1);
		if(mchtGatewayOrders == null || mchtGatewayOrders.size() == 0){
			return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
		}
		for(MchtGatewayOrder gatewayOrder: mchtGatewayOrders){
			if(!PayStatusEnum.PAY_SUCCESS.getCode().equals(gatewayOrder.getStatus())){
				continue;
			}
			sendNotifyMsg(gatewayOrder.getPlatOrderId());
			modelAndView.addObject("messageType", "success");
		}
		return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
	}


	/**
	 * 批量查询订单  订单状态为提交支付
	 * 2018-12-11 17:37:09
	 * @return
	 */
	@RequestMapping("/batchReissueMchtQuery")
	public String batchReissueMchtQuery(HttpServletRequest request, @RequestParam Map<String, String> paramMap, RedirectAttributes redirectAttributes) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("modules/order/orderList");

		MchtGatewayOrder queryOrder = new MchtGatewayOrder();
		//初始化页面开始时间
		assemblySearch(paramMap, queryOrder);
		queryOrder.setStatus(PayStatusEnum.SUBMIT_SUCCESS.getCode());

		if(queryOrder.getCreateTime()!=null){
			queryOrder.setSuffix(DateUtils.formatDate(queryOrder.getCreateTime(), "yyyyMM"));
		}

		//获取当前第几页
		String pageNoString = paramMap.get("pageNo");
		int pageNo = 1;
		if (StringUtils.isNotBlank(pageNoString) && "1".equals(paramMap.get("paging"))) {
			pageNo = Integer.parseInt(pageNoString);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(pageNo);
		queryOrder.setPageInfo(pageInfo);

		List<MchtGatewayOrder> mchtGatewayOrders =  mchtGwOrderService.queryMchtGatewayOrdersNoPage(queryOrder, null);
		if(mchtGatewayOrders == null || mchtGatewayOrders.size() == 0){
			return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
		}

		if(mchtGatewayOrders.size() >= 50){
			redirectAttributes.addFlashAttribute("message", "批量异步通知不能大于50条");
			redirectAttributes.addFlashAttribute("messageType", "error");
			return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";
		}


		for(MchtGatewayOrder  gatewayOrder: mchtGatewayOrders){

			JSONObject result  =queryResult(gatewayOrder.getPlatOrderId());
			JSONObject resultHead = result.getJSONObject("head");
			JSONObject resultBody = result.getJSONObject("body");
			if (resultBody != null && ErrorCodeEnum.SUCCESS.getCode().equals(resultHead.getString("respCode"))) {
				String resultStatus = resultBody.getString("status");
				if (Result.STATUS_SUCCESS.equals(resultStatus)) {
					sendNotifyMsg(gatewayOrder.getPlatOrderId());
				} else if (Result.STATUS_FAIL.equals(resultStatus)) {
					logger.info("查单成功, 支付状态为失败,订单号:"+ gatewayOrder.getPlatOrderId());
				} else {
					logger.info("查单成功, 支付状态未知,订单号:"+ gatewayOrder.getPlatOrderId());
				}
			}
		}
		return "redirect:"+ GlobalConfig.getAdminPath()+"/order/list";

	}


	public List<MchtAccountDetail> setMchtAccountDetail(List<MchtAccountDetail> listMchtAccountDetail, Map<String, String> mchtMap){
		for(MchtAccountDetail mchtAccountDetail : listMchtAccountDetail){
			//商户名称
			mchtAccountDetail.setMchtName(mchtMap.get(mchtAccountDetail.getMchtId()));
			//商户现金金额
			BigDecimal cashTotalAmount = mchtAccountDetail.getCashTotalAmount();
			cashTotalAmount = cashTotalAmount.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
			mchtAccountDetail.setCashTotalAmount(cashTotalAmount);
			//商户结算金额
			BigDecimal settleTotalAmount = mchtAccountDetail.getSettleTotalAmount();
			settleTotalAmount =settleTotalAmount.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
			mchtAccountDetail.setSettleTotalAmount(settleTotalAmount);
			//商户冻结金额
			BigDecimal freezeTotalAmount = mchtAccountDetail.getFreezeTotalAmount();
			freezeTotalAmount = freezeTotalAmount.divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
			mchtAccountDetail.setFreezeTotalAmount(freezeTotalAmount);
			//商户可用余额,可用余额=现金金额-冻结金额
			BigDecimal availableBalance = cashTotalAmount.subtract(freezeTotalAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
			mchtAccountDetail.setAvailableBalance(availableBalance);
			//商户总金额
			mchtAccountDetail.setTotalAmount(cashTotalAmount.add(settleTotalAmount));
		}
		return listMchtAccountDetail;
	}

	/**
	 * 批量补发异步通知按照订单号
	 * 2018-12-11 14:36:40
	 * @return
	 */
	@RequestMapping("/batchReissueMchtNotifyByOrderId")
	@ResponseBody
	public String batchReissueMchtNotifyByOrderId(HttpServletRequest request){
		String[] array=request.getParameterValues("platOrderNo");
		if(array ==null || array.length==0){
			return "商户订单号为空";
		}
		logger.info("array:"+array.length);

		Set<String> set = new HashSet<>(Arrays.asList(array));

		for(String mchtOrderNo:set){
			logger.info("开始补发通知"+mchtOrderNo);
			if(StringUtils.isBlank(mchtOrderNo)){
				continue;
			}
			sendNotifyMsg(mchtOrderNo);

		}
		return "success";
	}


	/**
	 * 批量查询订单按照订单号
	 * 2018-12-11 17:37:09
	 * @return
	 */
	@RequestMapping("/batchReissueMchtQueryByOrderId")
	@ResponseBody
	public String batchReissueMchtQueryByOrderId(HttpServletRequest request) throws Exception {
		String[] array=request.getParameterValues("platOrderNo");

		if(array ==null || array.length==0){
			return "平台订单号为空";
		}
		logger.info("array:"+array.length);

		Set<String> set = new HashSet<>(Arrays.asList(array));

		for(String platOrderNo:set){
			if(StringUtils.isBlank(platOrderNo)){
				continue;
			}
			JSONObject resultData =queryResult(platOrderNo);
			if(resultData==null){
				continue;
			}
			JSONObject resultHead = resultData.getJSONObject("head");
			JSONObject resultBody = resultData.getJSONObject("body");
			if (resultBody != null && ErrorCodeEnum.SUCCESS.getCode().equals(resultHead.getString("respCode"))) {
				String resultStatus = resultBody.getString("status");
				if (Result.STATUS_SUCCESS.equals(resultStatus)) {
					sendNotifyMsg(platOrderNo);
				} else if (Result.STATUS_FAIL.equals(resultStatus)) {
					logger.info("查单成功, 支付状态为失败,订单号:"+platOrderNo );
				} else {
					logger.info("查单成功, 支付状态未知,订单号:"+ platOrderNo);
				}
			}
		}
		return "success";
	}

	private void sendNotifyMsg(String mchtOrderNo){
		try{
			MchtGatewayOrder order = new MchtGatewayOrder();
			order.setMchtOrderId(mchtOrderNo);
			String suffix= DateUtils.formatDate(new Date(),"yyyyMM");
			order.setSuffix(suffix);
			logger.info(mchtOrderNo+"从支付流水"+suffix+"表中查询");
			List<MchtGatewayOrder> list=mchtGwOrderService.list(order);
			if(list ==null || list.size()==0){
				logger.info("补发异步通知,商户订单号:"+mchtOrderNo+"在支付流水"+suffix+"表中未找到订单数据");
				//上个月流水中查询
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, -1);
				SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
				suffix = format.format(c.getTime());
				order.setSuffix(suffix);
				logger.info(mchtOrderNo+"从支付流水"+suffix+"表中查询");
				list=mchtGwOrderService.list(order);
			}
			if(list ==null || list.size()==0){
				logger.info("补发异步通知,商户订单号:"+mchtOrderNo+"在支付流水"+suffix+"表中未找到订单数据");
				return;
			}
			MchtGatewayOrder result =list.get(0);

			if(!PayStatusEnum.PAY_SUCCESS.getCode().equals(result.getStatus())){
				return;
			}
			String message  = null;
			String gatewayUrl = ConfigUtil.getValue("gateway.url");
			String supplyUrl = gatewayUrl + "/gateway/renotify";
			Map<String, String> data = new HashMap<>();
			data.put("orderId", result.getPlatOrderId());
			data.put("suffix", suffix);
			String respStr = null;
			respStr = HttpUtil.post(supplyUrl, data);
			logger.info("gateway补发异步通知通知,商户订单号:"+mchtOrderNo+",返回结果：" + respStr);
			if ("SUCCESS".equalsIgnoreCase(respStr)) {
				logger.info("补发异步通知,商户订单号:"+result.getMchtOrderId()+",平台订单号:"+ result.getPlatOrderId() + ",商户响应:"+ respStr);
			} else {
				logger.info("补发异步通知,商户订单号:"+result.getMchtOrderId()+",平台订单号:"+ result.getPlatOrderId() + ",商户响应:"+ respStr);
			}
		}catch (Exception e) {
			logger.info("批量补单异常:"+mchtOrderNo,e);
		}
	}

	private JSONObject queryResult(String platOrderNo){
		JSONObject resultData =null;
		try {
			MchtGatewayOrder order = new MchtGatewayOrder();
			String suffix= "20" + platOrderNo.substring(1, 5);
			order.setPlatOrderId(platOrderNo);
			order.setSuffix(suffix);
			List<MchtGatewayOrder> list=mchtGwOrderService.list(order);

			if(list == null || list.size()==0){
				return null;
			}
			MchtGatewayOrder result =list.get(0);
			String gatewayUrl = ConfigUtil.getValue("gateway.url");
			String queryUrl = gatewayUrl + "/gateway/queryOrder";
			MchtInfo mchtInfo = merchantService.queryByKey(result.getMchtCode());
			if (mchtInfo == null) {
				return null;
			}
			String key = mchtInfo.getMchtKey();

			JSONObject data = new JSONObject();
			JSONObject head = new JSONObject();
			JSONObject body = new JSONObject();
			head.put("mchtId", mchtInfo.getId());
			head.put("version", "20");
			head.put("biz", result.getPayType());
			data.put("head", head);
			body.put("tradeId", result.getId());
			body.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(result.getCreateTime()));
			Map<String, String> params = JSONObject.parseObject(
					JSON.toJSONString(body), new TypeReference<Map<String, String>>() {
					});
			String log_moid = mchtInfo.getId()+"-->"+result.getId();
			String sign = SignUtil.md5Sign(params, key, log_moid);
			data.put("sign", sign);
			data.put("body", body);
			String respStr = HttpUtil.post(queryUrl, data.toJSONString());
			logger.info("gateway查单返回：" + respStr);
			resultData = JSON.parseObject(respStr);
		}catch (Exception e){
			logger.info("批量查单异常:"+platOrderNo,e);
		}

		return resultData;
	}

}
