package com.sys.admin.modules.reconciliation.service.impl;

import com.sys.admin.modules.reconciliation.service.ElectronicAccountInfoService;
import com.sys.core.vo.ElectronicAccountVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElectronicAccountInfoServiceImpl implements ElectronicAccountInfoService {
    @Override
    public List<ElectronicAccountVo> list(ElectronicAccountVo electronicAccountVo) {
        return null;
    }

    @Override
    public boolean add(ElectronicAccountVo electronicAccountVo) {
        return false;
    }

    @Override
    public boolean update(ElectronicAccountVo electronicAccountVo) {
        return false;
    }

    @Override
    public boolean delete(ElectronicAccountVo electronicAccountVo) {
        return false;
    }

    @Override
    public ElectronicAccountVo queryBykey(ElectronicAccountVo electronicAccountVo) {
        return null;
    }
}
