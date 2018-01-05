package com.sys.admin.modules.portal.service.impl;

import com.sys.admin.common.service.BaseService;
import com.sys.admin.common.utils.ConstUtils;
import com.sys.admin.modules.portal.dmo.*;
import com.sys.admin.modules.portal.mapper.PortalInfoMapper;
import com.sys.admin.modules.portal.mapper.PortalStationRelMapper;
import com.sys.admin.modules.portal.service.AgencyService;
import com.sys.admin.modules.sys.entity.Office;
import com.sys.admin.modules.sys.service.OfficeService;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 机构接口实现类
 */
@Service
public class AgencyServiceImpl extends BaseService implements AgencyService {
    @Autowired
    private OfficeService officeService;

    @Autowired
    private PortalInfoMapper portalInfoMapper;

    @Autowired
    private PortalStationRelMapper portalStationRelMapper;

//    @Autowired
//    private CustomerMapper customerMapper;

    @Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
    public void save(Office office, PortalInfo portalInfo) {
        officeService.save(office);
        portalInfo.setOfficeId(office.getId());
        save(portalInfo);
    }

    @Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
    public void save(Office office, PortalInfo portalInfo, Long[] ids) {
        officeService.save(office);
        portalInfo.setOfficeId(office.getId());
        save(portalInfo);

        PortalStationRelExample example = new PortalStationRelExample();
        example.createCriteria().andOfficeIdEqualTo(office.getId());
        portalStationRelMapper.deleteByExample(example);

        for (Long id : ids) {
            PortalStationRel rel = new PortalStationRel();
            rel.setOfficeId(office.getId());
            rel.setStationId(id);
            portalStationRelMapper.insertSelective(rel);
        }
    }

    @Override
	public void save(PortalInfo portalInfo) {
        if (portalInfoMapper.selectByPrimaryKey(portalInfo.getOfficeId()) == null) {
            portalInfoMapper.insertSelective(portalInfo);
        } else {
            portalInfoMapper.updateByPrimaryKeySelective(portalInfo);
        }
    }

    @Override
	public Office getOfficeById(Long id) {
        return officeService.get(id);
    }

    @Override
	public PortalInfo getInfoById(Long id) {
        return portalInfoMapper.selectByPrimaryKey(id);
    }

    @Override
	public PortalInfo getInfoByName(String name) {
        return portalInfoMapper.getInfoByName(name);
    }

    @Override
	public boolean isPortalInfoNormal(Long id) {
        PortalInfo portalInfo = portalInfoMapper.selectByPrimaryKey(id);
        return portalInfo != null && (ConstUtils.PORTAL_CONST.STATUS_NORMAL.equals(portalInfo.getStatus())
                                || ConstUtils.PORTAL_CONST.STATUS_CHECK.equals(portalInfo.getStatus()));
    }

    @Override
	public List<Office> findAllOffice() {
        return officeService.findAll();
    }

    @Override
	public List<Office> findAllOfficeWithoutUser() {
        return UserUtils.getOfficeList(false);
    }


    @Override
	@Transactional(readOnly = false)
    public void delete(Long id) {
        officeService.delete(id);
    }

    @Override
	public List<Office> findCodeExist(Long id, String code) {
        return officeService.findCodeExist(id, code);
    }

    @Override
	public boolean isOfficeNormal(Long id) {
        if (id == null) {
            return false;
        }
        if (ConstUtils.ROOT_OFFICE_ID.equals(id)) {
            return true;
        }
        boolean res = true;
        Office office = getOfficeById(id);
        PortalInfo portalInfo = getInfoById(id);
        if (office == null || portalInfo == null) {
            res = false;
        }
        while (office != null && portalInfo != null && res) {
            if (ConstUtils.BLOCK.equals(office.getDelFlag()) || ConstUtils.BLOCK.equals(portalInfo.getStatus())) {
                res = false;
            }
            office = office.getParent();
            if (office != null) {
                portalInfo = getInfoById(office.getId());
            }
        }

        return res;
    }

    @Override
	@Transactional(readOnly = false)
    public void changeStatus(Long id) {
        if (id == null) {
            return;
        }
        PortalInfo portalInfo = getInfoById(id);
        if (portalInfo == null) {
            return;
        }
        if (ConstUtils.PORTAL_CONST.STATUS_NORMAL.equals(portalInfo.getStatus())) {
            portalInfo.setStatus(ConstUtils.PORTAL_CONST.STATUS_BLOCK);
        } else if (ConstUtils.PORTAL_CONST.STATUS_BLOCK.equals(portalInfo.getStatus())) {
            portalInfo.setStatus(ConstUtils.PORTAL_CONST.STATUS_NORMAL);
        }
        save(portalInfo);
    }

    @Override
	public Office getTopAgency(Long id) {
        if (ConstUtils.ROOT_OFFICE_ID.equals(id)) {
            return null;
        }
        Office office = getOfficeById(id);
        while (office != null && !office.getParent().isRoot()) {
            office = office.getParent();
        }
        if (office != null) {
            office.setPortalInfo(getInfoById(office.getId()));
        }
        return office;
    }

    @Override
	public List<Office> getAllChildren(Long id) {
        if (id == null) {
			return Lists.newArrayList();
		}
        Office office = getOfficeById(id);
        if (office == null) {
			return Lists.newArrayList();
		}
        return getAllChildren(office);
    }

    @Override
	public Map selectFromCityList(Long officeId) {
        return null;
    }

    public List<Office> getAllChildren(Office office) {
        if (office == null) {
			return Lists.newArrayList();
		}
        List<Office> list = office.getChildList();
        if (list == null || list.size() <= 0) {
            return Lists.newArrayList();
        }
        for (int i = 0, len = list.size(); i < len; i ++) {
            list.addAll(getAllChildren(list.get(i)));
        }
        return list;
    }


    @Override
	public List<Long> getRelStationIds(Long officeId) {
        if (officeId == null) {
            return new ArrayList(0);
        }
        PortalStationRelExample example = new PortalStationRelExample();
        example.createCriteria().andOfficeIdEqualTo(officeId);
        List<PortalStationRel> list = portalStationRelMapper.selectByExample(example);
        List<Long> ids = new ArrayList<Long>(list.size());
        for(PortalStationRel rel : list) {
            ids.add(rel.getStationId());
        }
        return ids;
    }
}
