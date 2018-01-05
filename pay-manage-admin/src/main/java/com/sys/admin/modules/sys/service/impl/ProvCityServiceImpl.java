package com.sys.admin.modules.sys.service.impl;

import com.sys.admin.common.utils.ConstUtils;
import com.sys.admin.modules.portal.service.AgencyService;
import com.sys.admin.modules.sys.dmo.ProvCity;
import com.sys.admin.modules.sys.dmo.ProvCityExample;
import com.sys.admin.modules.sys.mapper.ProvCityMapper;
import com.sys.admin.modules.sys.service.ProvCityService;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 省-市数据接口实现类
 */
@Service
public class ProvCityServiceImpl implements ProvCityService {
    @Autowired
    private ProvCityMapper provCityMapper;

    @Autowired
    private AgencyService agencyService;

    
    @Override
	public int countByExample(ProvCityExample example) {
        return provCityMapper.countByExample(example);
    }

    
    @Override
	public int deleteByExample(ProvCityExample example) {
        return provCityMapper.deleteByExample(example);
    }

    
    @Override
	public int deleteByPrimaryKey(Integer id) {
        return provCityMapper.deleteByPrimaryKey(id);
    }

    
    @Override
	public int insert(ProvCity record) {
        return provCityMapper.insert(record);
    }

    
    @Override
	public int insertSelective(ProvCity record) {
        return provCityMapper.insertSelective(record);
    }

    
    @Override
	public List<ProvCity> selectByExample(ProvCityExample example) {
        if (example == null) {
            example = new ProvCityExample();
        }
        return provCityMapper.selectByExample(example);
    }

    
    @Override
	public ProvCity selectByPrimaryKey(Integer id) {
        return provCityMapper.selectByPrimaryKey(id);
    }

    
    @Override
	public int updateByExampleSelective(@Param("record") ProvCity record, @Param("example") ProvCityExample example) {
        return provCityMapper.updateByExampleSelective(record, example);
    }

    
    @Override
	public int updateByExample(@Param("record") ProvCity record, @Param("example") ProvCityExample example) {
        return provCityMapper.updateByExample(record, example);
    }

    
    @Override
	public int updateByPrimaryKeySelective(ProvCity record) {
        return provCityMapper.updateByPrimaryKeySelective(record);
    }

    
    @Override
	public int updateByPrimaryKey(ProvCity record) {
        return provCityMapper.updateByPrimaryKey(record);
    }

    
    @Override
	public List<ProvCity> selectByPortalId(Long portalId) {
        return provCityMapper.selectByPortalId(portalId, null, null);
    }

    
    @Override
	public List<ProvCity> selectByPortalId(Long portalId, String name) {
        return provCityMapper.selectByPortalId(portalId, name, null);
    }

    
    @Override
	public List<ProvCity> selectHotByPortalId(Long portalId) {
        return provCityMapper.selectByPortalId(portalId, null, ConstUtils.YES);
    }

    
    @Override
	public List<Map> selectToCitiesByFrom(Integer startId, Long portalId, String companyName) {
        ProvCity provCity = this.getByCountyId(startId);
        if (provCity == null || provCity.getCityId() == null) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cityId", provCity.getCityId());
        paramMap.put("startId", startId);
        paramMap.put("companyName", companyName);
        //如果微门户ID不为空，则获取微门户关联的车站
        if (portalId != null) {
            List<Long> stationIdList = agencyService.getRelStationIds(portalId);
            paramMap.put("stationIdList", stationIdList);
        }
        return provCityMapper.selectToCitiesByFrom(paramMap);
    }

    
    @Override
	public ProvCity getByCountyId(Integer countyId) {
        ProvCityExample example = new ProvCityExample();
        example.createCriteria().andCountyIdEqualTo(countyId);
        List<ProvCity> list = provCityMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

	/**
	 * 获取热门始发城市
	 */
	@Override
	public List<ProvCity> getHotStartCityList(Long ttsId, String provinceId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("portalId", ttsId);
		paramMap.put("provinceId", provinceId);
		return provCityMapper.getHotStartCityList(paramMap);
	}

	/**
     * 获取全部省
     */
	@Override
	public List<ProvCity> getProvinceList() {
		return provCityMapper.getProvinceList();
	}
}
