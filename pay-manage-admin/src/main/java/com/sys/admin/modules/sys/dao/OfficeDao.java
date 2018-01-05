package com.sys.admin.modules.sys.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sys.admin.common.persistence.BaseDao;
import com.sys.admin.common.persistence.BaseDaoImpl;
import com.sys.admin.modules.sys.entity.Office;

/**
 * 机构DAO接口
 */
public interface OfficeDao extends OfficeDaoCustom, CrudRepository<Office, Long> {

	@Modifying
	@Query("update Office set delFlag='" + Office.DEL_FLAG_DELETE + "' where id = ?1 or parentIds like ?2")
	int deleteById(Long id, String likeParentIds);
	
	List<Office> findByParentIdsLike(String parentIds);

	@Query("from Office where delFlag='" + Office.DEL_FLAG_NORMAL + "' and id <> ?1 and code = ?2 order by code")
	List<Office> findCodeExist(Long id, String code);

    @Query("from Office where delFlag='" + Office.DEL_FLAG_NORMAL + "' and code = ?1 order by code")
	List<Office> findCodeExist(String code);

}

/**
 * DAO自定义接口
 */
interface OfficeDaoCustom extends BaseDao<Office> {

}

/**
 * DAO自定义接口实现
 */
@Repository
class OfficeDaoImpl extends BaseDaoImpl<Office> implements OfficeDaoCustom {

}
