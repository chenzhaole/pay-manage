/**
 * Copyright &copy; 2012-2013  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sys.admin.modules.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sys.admin.common.config.GlobalConfig;
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
import com.sys.core.dao.dmo.ChanInfo;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatProduct;
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

/**
 * 商户快速配置
 */

@Controller
@RequestMapping(value = "${adminPath}/merchant/quickConfig")
public class MchtQuickConfigController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(MchtQuickConfigController.class);

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

    @Value("${payOrderListExpireSecond}")
    private String payOrderListExpireSecond;

    @Autowired
    private JedisPool jedisPool;


    @RequestMapping(value = {"index", ""})
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                       Model model, @RequestParam Map<String, String> paramMap) {

        User user = UserUtils.getUser();

        return "modules/merchant/merchantQuickConfig";
    }

}
