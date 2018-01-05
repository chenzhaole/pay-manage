package com.sys.admin.modules.sys.service;

import com.sys.admin.modules.sys.dmo.ProvCity;
import com.sys.admin.modules.sys.dmo.ProvCityExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 省-市管理业务操作接口
 */
public interface ProvCityService {
    /**
     * 查询满足条件的数据条数
     * @param example 条件对象
     * @return 查询结果
     */
    int countByExample(ProvCityExample example);

    /**
     * 根据条件删除数据
     * @param example 条件对象
     * @return 删除的条数
     */
    int deleteByExample(ProvCityExample example);

    /**
     * 根据主键删除数据
     * @param id 主键ID
     * @return 删除的条数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入新数据
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insert(ProvCity record);

    /**
     * 插入新数据 -- 只保存不为空的字段
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insertSelective(ProvCity record);

    /**
     * 根据条件查询数据
     * @param example 条件对象
     * @return 查询结果
     */
    List<ProvCity> selectByExample(ProvCityExample example);

    /**
     * 根据主键查询数据
     * @param id 主键ID
     * @return 查询结果
     */
    ProvCity selectByPrimaryKey(Integer id);

    /**
     * 更新满足条件的数据 -- 只更新不为空的字段
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExampleSelective(@Param("record") ProvCity record, @Param("example") ProvCityExample example);

    /**
     * 更新满足条件的数据
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExample(@Param("record") ProvCity record, @Param("example") ProvCityExample example);

    /**
     * 根据主键更新满足数据-- 只更新不为空的字段
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKeySelective(ProvCity record);

    /**
     * 根据主键更新满足数据
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKey(ProvCity record);

    /**
     * 查询微门户下出发城市列表
     * @param portalId 微门户ID，即机构ID
     * @return 城市列表
     */
    List<ProvCity> selectByPortalId(Long portalId);

    /**
     * 查询微门户下出发城市列表，根据城市名称模糊匹配
     * @param portalId 微门户ID，即机构ID
     * @param name 城市名称
     * @return 城市列表
     */
    List<ProvCity> selectByPortalId(Long portalId, String name);

    /**
     * 查询微门户下热门出发城市列表
     * @param portalId 微门户ID，即机构ID
     * @return 城市列表
     */
    List<ProvCity> selectHotByPortalId(Long portalId);

    /**
     * 根据出发城市查询到达站点列表
     * @param startId 出发地ID
     * @param portalId 微门户ID
     * @param companyName 运输公司名称
     * @return 到达站点列表
     */
    List<Map> selectToCitiesByFrom(Integer startId, Long portalId, String companyName);

    /**
     * 根据区县ID获取区县信息
     * @param countyId 区县ID
     * @return 城市信息
     */
    ProvCity getByCountyId(Integer countyId);
    
    List<ProvCity> getHotStartCityList(Long portalId, String provinceId);
    
    /**
     * 获取全部省
     * @date 2016年3月7日 下午6:06:35
     * @return
     */
    List<ProvCity> getProvinceList();
}