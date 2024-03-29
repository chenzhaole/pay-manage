package com.sys.admin.modules.sys.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sys.admin.common.persistence.BaseDao;
import com.sys.admin.common.persistence.BaseDaoImpl;
import com.sys.admin.modules.sys.entity.Dict;

/**
 * 字典DAO接口
 */
public interface DictDao extends DictDaoCustom, CrudRepository<Dict, Long> {
	
	@Modifying
	@Query("update Dict set delFlag='" + Dict.DEL_FLAG_DELETE + "' where id = ?1")
	int deleteById(Long id);

	@Query("from Dict where delFlag='" + Dict.DEL_FLAG_NORMAL + "' order by sort")
	List<Dict> findAllList();

	@Query("select type from Dict where delFlag='" + Dict.DEL_FLAG_NORMAL + "' group by type")
	List<String> findTypeList();
	
	
}

/**
 * DAO自定义接口
 */
interface DictDaoCustom extends BaseDao<Dict> {

}

/**
 * DAO自定义接口实现
 */
@Repository
class DictDaoImpl extends BaseDaoImpl<Dict> implements DictDaoCustom {

}
