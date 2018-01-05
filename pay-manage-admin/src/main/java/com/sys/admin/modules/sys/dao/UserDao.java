package com.sys.admin.modules.sys.dao;

import com.sys.admin.common.persistence.BaseDao;
import com.sys.admin.common.persistence.BaseDaoImpl;
import com.sys.admin.modules.sys.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 用户DAO接口
 */
public interface UserDao extends UserDaoCustom, CrudRepository<User, Long> {

    @Query("from User where id = ?1")
    User findById(long id);

	@Query("from User where loginName = ?1 and delFlag = '" + User.DEL_FLAG_NORMAL + "'")
	User findByLoginName(String loginName);

	@Modifying
	@Query("update User set delFlag='" + User.DEL_FLAG_DELETE + "' where id = ?1")
	int deleteById(Long id);
	
	@Modifying
	@Query("update User set password=?1 where id = ?2")
	int updatePasswordById(String newPassword, Long id);
	
	@Modifying
	@Query("update User set loginIp=?1, loginDate=?2 where id = ?3")
	int updateLoginInfo(String loginIp, Date loginDate, Long id);
}

/**
 * DAO自定义接口
 */
interface UserDaoCustom extends BaseDao<User> {

}

/**
 * DAO自定义接口实现
 */
@Repository
class UserDaoImpl extends BaseDaoImpl<User> implements UserDaoCustom {

}
