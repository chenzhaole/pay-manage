package com.sys.admin.modules.sys.service;

import com.sys.admin.modules.sys.dmo.Provider;
import com.sys.admin.modules.sys.dmo.ProviderExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分平台/提供商业务操作接口
 */
public interface ProviderService {
    /**
     * 查询满足条件的数据条数
     * @param example 条件对象
     * @return 查询结果
     */
    int countByExample(ProviderExample example);

    /**
     * 根据条件删除数据
     * @param example 条件对象
     * @return 删除的条数
     */
    int deleteByExample(ProviderExample example);

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
    int insert(Provider record);

    /**
     * 插入新数据 -- 只保存不为空的字段
     * @param record 新数据对象
     * @return 插入的条数
     */
    int insertSelective(Provider record);

    /**
     * 根据条件查询数据
     * @param example 条件对象
     * @return 查询结果
     */
    List<Provider> selectByExample(ProviderExample example);

    /**
     * 根据主键查询数据
     * @param id 主键ID
     * @return 查询结果
     */
    Provider selectByPrimaryKey(Long id);

    /**
     * 根据提供商编码查询数据
     * @param providerId 提供商编码
     * @return 查询结果
     */
    Provider selectByProviderId(Integer providerId);

    /**
     * 更新满足条件的数据 -- 只更新不为空的字段
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExampleSelective(@Param("record") Provider record, @Param("example") ProviderExample example);

    /**
     * 更新满足条件的数据
     * @param record 要更新的数据
     * @param example 检索条件
     * @return 更新的条数
     */
    int updateByExample(@Param("record") Provider record, @Param("example") ProviderExample example);

    /**
     * 根据主键更新满足数据-- 只更新不为空的字段
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKeySelective(Provider record);

    /**
     * 根据主键更新满足数据
     * @param record 要更新的数据
     * @return 更新的条数
     */
    int updateByPrimaryKey(Provider record);
}