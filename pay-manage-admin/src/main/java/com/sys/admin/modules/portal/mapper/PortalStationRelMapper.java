package com.sys.admin.modules.portal.mapper;

import com.sys.admin.modules.portal.dmo.PortalStationRel;
import com.sys.admin.modules.portal.dmo.PortalStationRelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalStationRelMapper {
    /**
     * 查询满足条件的数据条数
     * @param example 条件对象
     * @return 条数
     */
    int countByExample(PortalStationRelExample example);

    /**
     * 根据条件删除数据
     * @param example 条件对象
     * @return 删除的条数
     */
    int deleteByExample(PortalStationRelExample example);

    /**
     * 根据主键删除数据
     * @param id 主键ID
     * @return 删除的条数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入新数据
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insert(PortalStationRel record);

    /**
     * 插入新数据 -- 只保存不为空的字段
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insertSelective(PortalStationRel record);

    /**
     * 根据条件分页查询数据
     * @param example 条件对象
     * @return 查询结果
     */
    List<PortalStationRel> selectByExampleWithRowbounds(PortalStationRelExample example, RowBounds rowBounds);

    /**
     * 根据条件查询数据
     * @param example 条件对象
     * @return 查询结果
     */
    List<PortalStationRel> selectByExample(PortalStationRelExample example);

    /**
     * 根据主键查询数据
     * @param id 主键ID
     * @return 查询结果
     */
    PortalStationRel selectByPrimaryKey(Long id);

    /**
     * 更新满足条件的数据 -- 只更新不为空的字段
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExampleSelective(@Param("record") PortalStationRel record, @Param("example") PortalStationRelExample example);

    /**
     * 更新满足条件的数据
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExample(@Param("record") PortalStationRel record, @Param("example") PortalStationRelExample example);

    /**
     * 根据主键更新满足数据-- 只更新不为空的字段
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKeySelective(PortalStationRel record);

    /**
     * 根据主键更新数据
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKey(PortalStationRel record);
}