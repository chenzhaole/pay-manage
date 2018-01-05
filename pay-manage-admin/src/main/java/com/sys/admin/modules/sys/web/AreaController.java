//package com.sys.admin.modules.sys.web;
//
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletResponse;
//
//import com.sys.admin.common.config.GlobalConfig;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.apache.shiro.authz.annotation.RequiresUser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.sys.admin.common.web.BaseController;
//import com.sys.admin.modules.sys.entity.Area;
//import com.sys.admin.modules.sys.service.AreaService;
//import com.sys.admin.modules.sys.utils.UserUtils;
//
///**
// * 区域Controller
// */
//@SuppressWarnings("MVCPathVariableInspection")
//@Controller
//@RequestMapping(value = "${adminPath}/sys/area")
//public class AreaController extends BaseController {
//
//    @Autowired
//    private AreaService areaService;
//
//    @ModelAttribute("area")
//    public Area get(@RequestParam(required = false) Long id) {
//        if (id != null) {
//            return areaService.get(id);
//        } else {
//            return new Area();
//        }
//    }
//
//    @RequiresPermissions("sys:area:view")
//    @RequestMapping(value = {"list", ""})
//    public String list(Area area, Model model) {
//        area.setId(1L);
//        model.addAttribute("area", area);
//        List<Area> list = Lists.newArrayList();
//        List<Area> sourcelist = areaService.findAll();
//        Area.sortList(list, sourcelist, area.getId());
//        model.addAttribute("list", list);
//        return "modules/sys/areaList";
//    }
//
//    @RequiresPermissions("sys:area:view")
//    @RequestMapping(value = "form")
//    public String form(Area area, Model model) {
//        if (area.getParent() == null || area.getParent().getId() == null) {
//            area.setParent(UserUtils.getUser().getOffice().getArea());
//        }
//        area.setParent(areaService.get(area.getParent().getId()));
//        model.addAttribute("area", area);
//        return "modules/sys/areaForm";
//    }
//
//    @RequiresPermissions("sys:area:edit")
//    @RequestMapping(value = "save")
//    public String save(Area area, Model model, RedirectAttributes redirectAttributes) {
//        if (!beanValidator(model, area)) {
//            return form(area, model);
//        }
//        areaService.save(area);
//        addMessage(redirectAttributes, "保存区域'" + area.getName() + "'成功");
//        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/area/";
//    }
//
//    @RequiresPermissions("sys:area:edit")
//    @RequestMapping(value = "delete")
//    public String delete(Long id, RedirectAttributes redirectAttributes) {
//        if (Area.isAdmin(id)) {
//            addMessage(redirectAttributes, "删除区域失败, 不允许删除顶级区域或编号为空");
//        } else {
//            areaService.delete(id);
//            addMessage(redirectAttributes, "删除区域成功");
//        }
//        return "redirect:" + GlobalConfig.getAdminPath() + "/sys/area/";
//    }
//
//    @RequiresUser
//    @ResponseBody
//    @RequestMapping(value = "treeData")
//    public List<Map<String, Object>> treeData(@RequestParam(required = false) Long extId, HttpServletResponse response) {
//        response.setContentType("application/json; charset=UTF-8");
//        List<Map<String, Object>> mapList = Lists.newArrayList();
//        List<Area> list = areaService.findAll();
//        for (Area e : list) {
//            if (extId == null || (!extId.equals(e.getId()) && !e.getParentIds().contains("," + extId + ","))) {
//                Map<String, Object> map = Maps.newHashMap();
//                map.put("id", e.getId());
//                map.put("pId", e.getParent() != null ? e.getParent().getId() : 0);
//                map.put("name", e.getName());
//                mapList.add(map);
//            }
//        }
//        return mapList;
//    }
//}
