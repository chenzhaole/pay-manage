package com.sys.admin.modules.sys.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sys.admin.common.persistence.BaseDao;
import com.sys.admin.common.persistence.BaseDaoImpl;
import com.sys.admin.modules.sys.entity.Role;

/**
 * 角色DAO接口
 */
public interface RoleDao extends RoleDaoCustom, CrudRepository<Role, Long> {
	
	@Query("from Role where name = ?1 and delFlag = '" + Role.DEL_FLAG_NORMAL + "'")
	Role findByName(String name);

	@Modifying
	@Query("update Role set delFlag='" + Role.DEL_FLAG_DELETE + "' where id = ?1")
	int deleteById(Long id);

}

/**
 * DAO自定义接口
 */
interface RoleDaoCustom extends BaseDao<Role> {
	

}

/**
 * DAO自定义接口实现
 * @author 
 */
@Repository
class RoleDaoImpl extends BaseDaoImpl<Role> implements RoleDaoCustom {

}
