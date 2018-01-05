package com.sys.admin.modules.sys.service;

import com.sys.admin.common.service.BaseService;
import com.sys.admin.modules.sys.dao.AreaDao;
import com.sys.admin.modules.sys.entity.Area;
import com.sys.admin.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 区域Service
 */
@Transactional(readOnly = true)
public class AreaService extends BaseService {

	@Autowired
	private AreaDao areaDao;
	
	public Area get(Long id) {
		return areaDao.findOne(id);
	}
	
	/**
     * 获得父id字符串中的最后,
     */
    public List<Long> getParentIds(Long id, String parentIds) {
        List<Long> list = new ArrayList<Long>();
        list.add(id);
        if (StringUtils.isNotBlank(parentIds)) {
            String[] arr = parentIds.split(",");
            for (String str : arr) {
                if (StringUtils.isNotBlank(str)) {
                    list.add(Long.valueOf(str));
                }
            }
        }
        return list;
    }
	
    public Area getParentByType(Area area, String type) {
        List<Area> list = areaDao.findAllParent(getParentIds(area.getId(), area.getParentIds()));
        for (Area areal : list) {
            if (type.equals(areal.getType())) {
                return areal;
            }
        }
        return null;
    }
	
	public List<Area> findAll(){
		return UserUtils.getAreaList();
	}

	@Transactional(readOnly = false)
	public void save(Area area) {
		area.setParent(this.get(area.getParent().getId()));
		String oldParentIds = area.getParentIds(); // 获取修改前的parentIds，用于更新子节点的parentIds
		area.setParentIds(area.getParent().getParentIds()+area.getParent().getId()+",");
		areaDao.clear();
		areaDao.save(area);
		// 更新子节点 parentIds
		List<Area> list = areaDao.findByParentIdsLike("%,"+area.getId()+",%");
		for (Area e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, area.getParentIds()));
		}
		areaDao.save(list);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Long id) {
		areaDao.deleteById(id, "%,"+id+",%");
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
}
