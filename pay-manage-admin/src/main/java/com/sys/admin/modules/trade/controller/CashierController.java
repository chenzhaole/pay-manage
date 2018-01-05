package com.sys.admin.modules.trade.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.admin.modules.trade.service.CashierService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/cashier")
public class CashierController extends BaseController {
	
	@Autowired
	private CashierService cashierService;

	@RequestMapping(value = {"cashier", ""})
	public String list(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
        return "modules/cashier/cashier";
	}
	

	@RequestMapping(value = {"createOrder", ""})
	public String createOrder(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam Map<String, String> paramMap) {
       
		try {
			
			String mchtNo = UserUtils.getUser().getNo();
			
			String biz = request.getParameter("biz");
			String goods = request.getParameter("goods");
			String amount = request.getParameter("amount");
			String desc = request.getParameter("desc");
			
			String res = cashierService.createOrder(biz, mchtNo, goods, Long.valueOf(amount), desc);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "modules/cashier/cashier";
	}
	
	
    
}
