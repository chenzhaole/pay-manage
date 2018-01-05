package com.sys.admin.modules.sys.web;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.utils.ConstUtils;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.dmo.Provider;
import com.sys.admin.modules.sys.dmo.ProviderExample;
import com.sys.admin.modules.sys.service.ProviderService;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * 分平台/提供商管理Controller
 */
@SuppressWarnings("MVCPathVariableInspection")
@Controller
@RequestMapping(value = "${adminPath}/sys/provider")
public class ProviderController extends BaseController {

	@Autowired
	private ProviderService providerService;
	
	@ModelAttribute("provider")
	public Provider get(@RequestParam(required=false) Long id) {
		if (id != null){
			return providerService.selectByPrimaryKey(id);
		}else{
			return new Provider();
		}
	}

	/**
	 * 查询分平台/提供商数据列表
	 * @param model 页面传参
	 * @param request 请求
	 * @param response 响应
	 * @param paramMap 查询条件
	 * @return 页面
	 */
	@RequiresPermissions("sys:provider:view")
	@RequestMapping(value = {"list", ""})
	public String list(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> paramMap) {
		try {
			Page<Provider> page = new Page<Provider>(request, response);
			ProviderExample example = new ProviderExample();
			ProviderExample.Criteria criteria = example.createCriteria();
			if (StringUtils.isNotBlank(paramMap.get("providerName"))) {
				criteria.andProviderNameLike("%" + paramMap.get("providerName") + "%");
			}
			example.setOrderByClause("open_date desc");
			List<Provider> list = providerService.selectByExample(example);
			page.setList(list);
			model.addAttribute("page", page);
			model.addAttribute("paramMap", paramMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "modules/sys/providerList";
	}

	/**
	 * 打开分平台/提供商数据编辑/查看页面
	 * @param provider 分平台/提供商数据
	 * @param model 页面传参
	 * @return 页面
	 */
	@RequiresPermissions("sys:provider:view")
	@RequestMapping(value = "form")
	public String form(Provider provider, Model model) {
		if (provider.getProviderId() != null) {
			provider = providerService.selectByPrimaryKey(provider.getId());
		}
		if (provider == null) {
			provider = new Provider();
		}
		if (StringUtils.isBlank(provider.getIsOpen())) {
			provider.setIsOpen(ConstUtils.PROVIDER_CONST.FLAG_OPEN);
		}
		model.addAttribute("provider", provider);
		return "modules/sys/providerForm";
	}

	/**
	 * 校验编号是否已存在
	 * @param oldProviderId 现有编号
	 * @param providerId 新编号
	 * @return 不存在返回true 已存在返回false
	 */
	@ResponseBody
	@RequiresPermissions("sys:provider:view")
	@RequestMapping(value = "checkProviderId")
	public String checkProviderId(Integer oldProviderId, Integer providerId) {
		if (providerId == null) {
			return "true";
		}
		if (providerId.equals(oldProviderId)) {
			return "true";
		} else if (providerService.selectByProviderId(providerId) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 保存或修改分平台/提供商信息
	 * @param provider 分平台/提供商数据
	 * @param redirectAttributes 重定向页面传参
	 * @return 页面
	 */
	@RequiresPermissions("sys:provider:edit")
	@RequestMapping(value = "save")
	public String save(Provider provider, RedirectAttributes redirectAttributes) {
		if (provider.getId() != null) {//如果ID为空，则新增；不为空，则修改原数据
			Provider oldProvider = providerService.selectByPrimaryKey(provider.getId());
			oldProvider.setProviderId(provider.getProviderId());
			oldProvider.setProviderName(provider.getProviderName());
			oldProvider.setProviderDisplayName(provider.getProviderDisplayName());
			oldProvider.setInterfaceUrl(provider.getInterfaceUrl());
			oldProvider.setIsOpen(provider.getIsOpen());
			oldProvider.setPayType(provider.getPayType());
			oldProvider.setPrivateKey(provider.getPrivateKey());
			oldProvider.setTckIdent(provider.getTckIdent());
			oldProvider.setOpenDate(provider.getOpenDate());
			providerService.updateByPrimaryKey(oldProvider);
			addMessage(redirectAttributes, "保存分平台/提供商'" + provider.getProviderName() + "'成功");
		} else {
			// 设置创建人和修改人信息
			providerService.insert(provider);
			addMessage(redirectAttributes, "保存分平台/提供商'" + provider.getProviderName() + "'成功");
		}
		return "redirect:"+ GlobalConfig.getAdminPath()+"/sys/provider/list";
	}

	/**
	 * 删除分平台/提供商信息
	 * @param id 主键ID
	 * @param redirectAttributes 重定向页面传参
	 * @return 页面
	 */
	@RequiresPermissions("sys:provider:edit")
	@RequestMapping(value = "delete")
	public String delete(Long id, RedirectAttributes redirectAttributes) {
		if (id != null) {
			if (providerService.deleteByPrimaryKey(id) > 0) {
				addMessage(redirectAttributes, "删除成功");
			} else {
				addMessage(redirectAttributes, "删除失败，没有找到对应的分平台/提供商信息");
			}
		} else {
			addMessage(redirectAttributes, "删除失败，请检查参数");
		}
		return "redirect:"+ GlobalConfig.getAdminPath()+"/sys/provider/list";
	}

}
