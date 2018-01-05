package com.sys.admin.modules.sys.utils;

import com.sys.admin.common.service.BaseService;
import com.sys.admin.common.utils.SpringContextHolder;
import com.sys.admin.modules.sys.dao.OfficeDao;
import com.sys.admin.modules.sys.entity.Office;

public class OfficeUtils extends BaseService {
    private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);

    public static String getOfficeName(Long id){
        if (id == null){
            return null;
        }
        Office office = officeDao.findOne(id);
        if (office != null) {
            return office.getName();
        }
        return null;
    }
}
