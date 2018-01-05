package com.sys.admin.modules.sys.web;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.web.BaseController;
import com.sys.admin.modules.sys.dmo.ProvCity;
import com.sys.admin.modules.sys.dmo.ProvCityExample;
import com.sys.admin.modules.sys.dmo.Provider;
import com.sys.admin.modules.sys.dmo.ProviderExample;
import com.sys.admin.modules.sys.service.ProvCityService;
import com.sys.admin.modules.sys.service.ProviderService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 省-市管理Controller
 */
@SuppressWarnings("MVCPathVariableInspection")
@Controller
@RequestMapping(value = "${adminPath}/sys/provCity")
public class ProvCityController extends BaseController {

	@Autowired
	private ProvCityService provCityService;

	@Autowired
	private ProviderService providerService;
	
	@ModelAttribute("provCity")
	public ProvCity get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return provCityService.selectByPrimaryKey(id);
		}else{
			return new ProvCity();
		}
	}

	/**
	 * 查询省市数据列表
	 * @param model 页面传参
	 * @param request 请求
	 * @param response 响应
	 * @param paramMap 查询条件
	 * @return 页面
	 */
	@RequiresPermissions("sys:provCity:view")
	@RequestMapping(value = {"list", ""})
	public String list(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> paramMap) {
		try {
			Page<ProvCity> page = new Page<ProvCity>(request, response);
			ProvCityExample example = new ProvCityExample();
			ProvCityExample.Criteria criteria = example.createCriteria();
			if (StringUtils.isNotBlank(paramMap.get("provinceName"))) {
				criteria.andProvinceNameLike("%" + paramMap.get("provinceName") + "%");
			}
			if (StringUtils.isNotBlank(paramMap.get("cityName"))) {
				criteria.andCityNameLike("%" + paramMap.get("cityName") + "%");
			}
			if (StringUtils.isNotBlank(paramMap.get("countyName"))) {
				criteria.andCountyNameLike("%" + paramMap.get("countyName") + "%");
			}
			if (StringUtils.isNotBlank(paramMap.get("isRecommend"))) {
				criteria.andIsRecommendEqualTo(paramMap.get("isRecommend"));
			}
			example.setOrderByClause("province_id, city_id");
			List<ProvCity> list = provCityService.selectByExample(example);
			page.setList(list);
			model.addAttribute("page", page);
			model.addAttribute("paramMap", paramMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "modules/sys/provCityList";
	}

	/**
	 * 打开省市数据编辑/查看页面；克隆功能会把省市数据展现在页面上，保存是当做一条新数据
	 * @param provCity 省市数据
	 * @param model 页面传参
	 * @param clone 是否克隆  true-是 其他-否
	 * @return 页面
	 */
	@RequiresPermissions("sys:provCity:view")
	@RequestMapping(value = "form")
	public String form(ProvCity provCity, Model model, Boolean clone) {
		if (provCity == null) {
			provCity = new ProvCity();
		}
		if (provCity.getId() != null) {
			provCity = provCityService.selectByPrimaryKey(provCity.getId());
			if (clone != null && clone && provCity != null) {
				provCity.setId(null);
				provCity.setCountyId(null);
				provCity.setCountyName(null);
				provCity.setPinyinPrefix(null);
				provCity.setPinyin(null);
			}
		}

		ProviderExample example = new ProviderExample();
		example.setOrderByClause("provider_id");
		List<Provider> providerList = providerService.selectByExample(example);

		model.addAttribute("providerList", providerList);
		model.addAttribute("provCity", provCity);
		return "modules/sys/provCityForm";
	}

	/**
	 * 保存或修改省市信息
	 * @param provCity 省市数据
	 * @param redirectAttributes 重定向页面传参
	 * @return 页面
	 */
	@RequiresPermissions("sys:provCity:edit")
	@RequestMapping(value = "save")
	public String save(ProvCity provCity, RedirectAttributes redirectAttributes) {
		if (provCity.getId() != null) {//如果ID为空，则新增；不为空，则修改原数据
			ProvCity oldProvCity = provCityService.selectByPrimaryKey(provCity.getId());
			oldProvCity.setProvinceId(provCity.getProvinceId());
			oldProvCity.setProvinceName(provCity.getProvinceName());
			oldProvCity.setCityId(provCity.getCityId());
			oldProvCity.setCityName(provCity.getCityName());
			oldProvCity.setCountyId(provCity.getCountyId());
			oldProvCity.setCountyName(provCity.getCountyName());
			oldProvCity.setProviderId(provCity.getProviderId());
			oldProvCity.setPinyin(provCity.getPinyin());
			oldProvCity.setPinyinPrefix(provCity.getPinyinPrefix());
			oldProvCity.setProviderId(provCity.getProviderId());
			oldProvCity.setIsRecommend(provCity.getIsRecommend());
			oldProvCity.setLat(provCity.getLat());
			oldProvCity.setLon(provCity.getLon());
			provCityService.updateByPrimaryKey(oldProvCity);
			addMessage(redirectAttributes, "保存区域'" + provCity.getProvinceName() + provCity.getCityName() + "'成功");
		} else {
			// 设置创建人和修改人信息
			provCityService.insert(provCity);
			addMessage(redirectAttributes, "保存区域'" + provCity.getProvinceName() + provCity.getCityName() + "'成功");
		}
		return "redirect:"+ GlobalConfig.getAdminPath()+"/sys/provCity/list";
	}

	/**
	 * 校验区县编号是否已存在
	 * @param oldCountyId 现有编号
	 * @param countyId 新编号
	 * @return 不存在返回true 已存在返回false
	 */
	@ResponseBody
	@RequiresPermissions("sys:provCity:view")
	@RequestMapping(value = "checkCountyId")
	public String checkCountyId(Integer oldCountyId, Integer countyId) {
		if (countyId == null) {
			return "true";
		}
		if (countyId.equals(oldCountyId)) {
			return "true";
		} else if (provCityService.getByCountyId(countyId) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 删除省市信息
	 * @param id 主键ID
	 * @param redirectAttributes 重定向页面传参
	 * @return 页面
	 */
	@RequiresPermissions("sys:provCity:edit")
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		if (id != null) {
			if (provCityService.deleteByPrimaryKey(id) > 0) {
				addMessage(redirectAttributes, "删除成功");
			} else {
				addMessage(redirectAttributes, "删除失败，没有找到对应的省市信息");
			}
		} else {
			addMessage(redirectAttributes, "删除失败，请检查参数");
		}
		return "redirect:"+ GlobalConfig.getAdminPath()+"/sys/provCity/list";
	}

	/**
	 * 异步获取省市树
	 * @param response 响应
	 * @return 返回推荐栏目树json对象
	 */
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "/treeData")
	public List<Map<String, Object>> treeData(HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		ProvCityExample example = new ProvCityExample();
		List<ProvCity> list = provCityService.selectByExample(example);
		Map<Integer, String> provCityMap = new HashMap<Integer, String>();
		for (ProvCity provCity : list) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", provCity.getProvinceId() + "_" + provCity.getCountyId());
			map.put("pId", provCity.getCityId() != null ? provCity.getCityId() : 0);
			map.put("name", StringEscapeUtils.unescapeHtml4(provCity.getCountyName()));
			mapList.add(map);
			if (provCityMap.get(provCity.getProvinceId()) == null) {
				map = Maps.newHashMap();
				map.put("id", provCity.getProvinceId());
				map.put("pId", 0);
				map.put("name", StringEscapeUtils.unescapeHtml4(provCity.getProvinceName()));
				mapList.add(map);
				provCityMap.put(provCity.getProvinceId(), provCity.getProvinceName());
			}
			if (provCityMap.get(provCity.getCityId()) == null) {
				map = Maps.newHashMap();
				map.put("id", provCity.getCityId());
				map.put("pId", provCity.getProvinceId());
				map.put("name", StringEscapeUtils.unescapeHtml4(provCity.getCityName()));
				mapList.add(map);
				provCityMap.put(provCity.getCityId(), provCity.getCityName());
			}
		}
		return mapList;
	}

}
