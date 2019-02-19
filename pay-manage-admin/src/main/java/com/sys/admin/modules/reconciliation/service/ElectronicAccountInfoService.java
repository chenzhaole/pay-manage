package com.sys.admin.modules.reconciliation.service;

import com.sys.core.vo.ElectronicAccountVo;

import java.util.List;

public interface ElectronicAccountInfoService {
    //查询list数据
    List<ElectronicAccountVo> list(ElectronicAccountVo electronicAccountVo);
    //新增数据
    boolean add(ElectronicAccountVo electronicAccountVo);
    //更新数据
    boolean update(ElectronicAccountVo electronicAccountVo);
    //删除数据(逻辑删除)
    boolean delete(ElectronicAccountVo electronicAccountVo);
    //查询单个信息对象
    ElectronicAccountVo queryBykey(ElectronicAccountVo electronicAccountVo);
}
