package com.sys.admin.modules.sys.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sys.admin.common.persistence.BaseDao;
import com.sys.admin.common.persistence.BaseDaoImpl;
import com.sys.admin.modules.sys.entity.Area;

/**
 * 区域DAO接口
 */
public interface AreaDao extends AreaDaoCustom, CrudRepository<Area, Long> {

	@Modifying
	@Query("update Area set delFlag='" + Area.DEL_FLAG_DELETE + "' where id = ?1 or parentIds like ?2")
	int deleteById(Long id, String likeParentIds);
	
	List<Area> findByParentIdsLike(String parentIds);

	@Query("from Area where delFlag='" + Area.DEL_FLAG_NORMAL + "' order by code")
	List<Area> findAllList();
	
	@Query("from Area where (id=?1 or parent.id=?1 or parentIds like ?2) and delFlag='" + Area.DEL_FLAG_NORMAL + "' order by code")
	List<Area> findAllChild(Long parentId, String likeParentIds);

    @Query("from Area where id in (?1) and delFlag='" + Area.DEL_FLAG_NORMAL + "' order by code")
	List<Area> findAllParent(List<Long> ids);
}

/**
 * DAO自定义接口
 */
interface AreaDaoCustom extends BaseDao<Area> {

}

/**
 * DAO自定义接口实现
 */
@Repository
class AreaDaoImpl extends BaseDaoImpl<Area> implements AreaDaoCustom {

}
