/**
 * Copyright &copy; 2012-2013  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sys.admin.modules.sys.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sys.admin.common.service.BaseService;
import com.sys.admin.modules.sys.dao.OfficeDao;
import com.sys.admin.modules.sys.entity.Office;
import com.sys.admin.modules.sys.utils.UserUtils;

/**
 * 机构Service
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends BaseService {

	@Autowired
	private OfficeDao officeDao;
	
	public Office get(Long id) {
        if (id == 0) {
            Office office = new Office();
            office.setId(0L);
            office.setParentIds("");
            return office;
        }
		return officeDao.findOne(id);
	}
	
	public List<Office> findAll(){
		return UserUtils.getOfficeList();
	}

    public List<Office> selectOffice() {
        DetachedCriteria dc = officeDao.createDetachedCriteria();
        dc.add(Restrictions.eq("delFlag", Office.DEL_FLAG_NORMAL));
        dc.addOrder(Order.asc("code"));
        return officeDao.find(dc);
    }
	
	@Transactional(readOnly = false)
	public void save(Office office) {
		office.setParent(this.get(office.getParent() == null ? 0 : office.getParent().getId()));
		String oldParentIds = office.getParentIds(); // 获取修改前的parentIds，用于更新子节点的parentIds
		office.setParentIds(office.getParent().getParentIds()+office.getParent().getId()+",");
		officeDao.clear();
		officeDao.save(office);
		// 更新子节点 parentIds
		List<Office> list = officeDao.findByParentIdsLike("%,"+office.getId()+",%");
		for (Office e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, office.getParentIds()));
		}
		officeDao.save(list);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Long id) {
		officeDao.deleteById(id, "%,"+id+",%");
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	public List<Office> findCodeExist(Long id, String code){
        if (id == null) {
            return officeDao.findCodeExist(code);
        }
		return officeDao.findCodeExist(id, code);
	}
}
