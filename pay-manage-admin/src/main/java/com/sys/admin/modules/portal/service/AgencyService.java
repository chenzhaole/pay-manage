package com.sys.admin.modules.portal.service;

import com.sys.admin.modules.portal.dmo.PortalInfo;
import com.sys.admin.modules.sys.entity.Office;

import java.util.List;
import java.util.Map;

/**
 * 门户接口
 */
public interface AgencyService {
    /**
     * 保存门户
     * @param office 基本信息
     * @param portalInfo 门户特有信息
     */
    void save(Office office, PortalInfo portalInfo);

    /**
     * 保存门户
     * @param office 基本信息
     * @param portalInfo 门户特有信息
     * @param ids 车站ID
     */
    void save(Office office, PortalInfo portalInfo, Long[] ids);

    /**
     * 保存门户特有信息
     * @param portalInfo 门户特有信息
     */
    void save(PortalInfo portalInfo);

    /**
     * 获取门户对应的基础信息
     * @param id 门户ID
     * @return 门户
     */
    Office getOfficeById(Long id);

    /**
     * 获取门户特有信息
     * @param id 门户ID
     * @return 门户
     */
    PortalInfo getInfoById(Long id);

    /**
     * 根据门户名称获取门户特有信息
     * @param name 门户名称
     * @return 门户
     */
    PortalInfo getInfoByName(String name);

    /**
     * 验证门户状态是否正常
     * @param id 门户ID
     * @return true-正常 false-未审核或已被注销
     */
    boolean isPortalInfoNormal(Long id);

    /**
     * 获取所有门户基础信息
     * @return 门户列表
     */
    List<Office> findAllOffice();

    /**
     * 获取所有门户基础信息,后台未登录情况下获取
     * @return 门户列表
     */
    List<Office> findAllOfficeWithoutUser();


    /**
     * 删除门户
     * @param id 门户ID
     */
    void delete(Long id);

    /**
     * 查找非id的门户编号为code的所有门户
     * @param id 门户ID
     * @param code 编码
     * @return 门户列表
     */
    List<Office> findCodeExist(Long id, String code);

    /**
     * 判断门户是否正常
     * @param id 门户ID
     * @return 是否正常
     */
    boolean isOfficeNormal(Long id);

    /**
     * 更改状态为冻结或解冻
     * @param id 门户ID
     */
    void changeStatus(Long id);

    /**
     * 获取当前门户所属的顶级门户
     * @param id 门户ID
     * @return 返回null时没有顶级门户
     */
    Office getTopAgency(Long id);

    /**
     * 获取所有子节点
     * @param id 父节点ID
     * @return 子节点list
     */
    List<Office> getAllChildren(Long id);

    /**
     * 查询微门户下所有的出发城市 key=城市代码 value=城市名称
     * @param officeId 微门户ID
     * @return 城市列表
     */
    Map selectFromCityList(Long officeId);

    /**
     * 获取所有关联车站ID
     * @param officeId 门户ID
     * @return 车站ID
     */
    List<Long> getRelStationIds(Long officeId);

}
