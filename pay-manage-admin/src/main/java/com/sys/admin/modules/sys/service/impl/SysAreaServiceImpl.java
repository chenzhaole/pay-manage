package com.sys.admin.modules.sys.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sys.admin.modules.sys.service.SysAreaService;
import com.sys.core.service.AreaService;
import com.sys.core.dao.dmo.SysArea;
import com.sys.common.enums.AreaEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地区
 *
 * @author ALI
 * at 2017/10/10 14:20
 */
@Service
public class SysAreaServiceImpl implements SysAreaService {
	private static final Logger log = LoggerFactory.getLogger(SysAreaServiceImpl.class);

	@Autowired
	private AreaService areaService;

	@Override
	public Object getAllJson() {
		List<SysArea> sysAreas = areaService.getAll();

		JSONArray jsonAra = new JSONArray();
		JSONObject region, province, city, district;
		JSONArray citys, districts;
		for (SysArea sysAreaPro : sysAreas) {

			if (AreaEnum.PROVINCE.getCode().equals(sysAreaPro.getType())){
				citys = new JSONArray(); //城市列表
				for (SysArea sysAreaCity : sysAreas) {
					if (sysAreaPro.getId().equals(sysAreaCity.getParentId())){

						districts = new JSONArray(); //地区列表
						for (SysArea sysAreaDist : sysAreas) {
							if (sysAreaCity.getId().equals(sysAreaDist.getParentId())){
								district = new JSONObject();
								district.put("name", sysAreaDist.getName());
								district.put("code", sysAreaDist.getId());
								districts.add(district);
							}

						}

						city = new JSONObject();
						city.put("name", sysAreaCity.getName());
						city.put("code", sysAreaCity.getId());
						city.put("city", districts);
						citys.add(city);
					}

				}
				province = new JSONObject();
				region = new JSONObject();
				province.put("name", sysAreaPro.getName());
				province.put("code", sysAreaPro.getId());
				province.put("state", citys);
				region.put("region", province);
				jsonAra.add(region);
			}
		}

		return jsonAra;
	}
}
