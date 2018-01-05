package com.sys.admin.modules.sys.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sys.admin.common.persistence.Page;
import com.sys.admin.common.security.Digests;
import com.sys.admin.common.service.BaseService;
import com.sys.admin.common.utils.ConstUtils;
import com.sys.admin.modules.sys.dao.MenuDao;
import com.sys.admin.modules.sys.dao.RoleDao;
import com.sys.admin.modules.sys.dao.UserDao;
import com.sys.admin.modules.sys.entity.Menu;
import com.sys.admin.modules.sys.entity.Role;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.security.SystemAuthorizingRealm;
import com.sys.admin.modules.sys.utils.UserUtils;
import com.sys.common.util.Encodes;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private SystemAuthorizingRealm systemRealm;
	
	//-- User Service --//

	/**
	 * 根据用户ID获取用户详情
	 * @param id 唯一标识
	 * @return 用户对象
	 */
	public User getUser(Long id) {
		return userDao.findOne(id);
	}

	/**
	 * 后台用户查询
	 * @param page 分页对象
	 * @param user 查询条件
	 * @return 用户列表
	 */
	public Page<User> findUser(Page<User> page, User user) {
		DetachedCriteria dc = userDao.createDetachedCriteria();
		User currentUser = UserUtils.getUser();
		dc.createAlias("company", "company");
		if (user.getCompany()!=null && user.getCompany().getId()!=null){
			dc.add(Restrictions.or(
					Restrictions.eq("company.id", user.getCompany().getId()),
					Restrictions.like("company.parentIds", "%,"+user.getCompany().getId()+",%")
					));
		}
		dc.createAlias("office", "office");
		if (user.getOffice()!=null && user.getOffice().getId()!=null){
			dc.add(Restrictions.or(
					Restrictions.eq("office.id", user.getOffice().getId()),
					Restrictions.like("office.parentIds", "%,"+user.getOffice().getId()+",%")
					));
		}
		// 如果不是超级管理员，则不显示超级管理员用户
		if (!currentUser.isAdmin()){
			dc.add(Restrictions.ne("id", ConstUtils.ROOT_ADMIN_ID));
		}
//		dc.add(dataScopeFilter(currentUser, "office", ""));
//		System.out.println(dataScopeFilterString(currentUser, "office", ""));
		if (UserUtils.isCurrentAgency()) {
			dc.add(Restrictions.eq("office.id", currentUser.getOffice().getId()));
		}
		if (StringUtils.isNotEmpty(user.getLoginName())){
			dc.add(Restrictions.like("loginName", "%"+user.getLoginName()+"%"));
		}
		if (StringUtils.isNotEmpty(user.getName())){
			dc.add(Restrictions.like("name", "%"+user.getName()+"%"));
		}
		dc.add(Restrictions.eq(User.DEL_FLAG, User.DEL_FLAG_NORMAL));
		if (!StringUtils.isNotEmpty(page.getOrderBy())){
			dc.addOrder(Order.asc("company.code")).addOrder(Order.asc("office.code")).addOrder(Order.desc("id"));
		}
		return userDao.find(page, dc);
	}

    /**
     * 获取机构下的管理员列表
     * @param officeIds 机构ID列表
     * @return 管理员列表
     */
    public List<User> findUserByOfficeIds(String officeIds) {
        DetachedCriteria dc = userDao.createDetachedCriteria();
        dc.createAlias("office", "office");
        if (StringUtils.isNotBlank(officeIds)){
            String[] arr = officeIds.split(",");
            List<Long> officeIdList = new ArrayList<Long>();
            for (String officeId : arr) {
                if (StringUtils.isNumeric(officeId)) {
                    officeIdList.add(Long.parseLong(officeId));
                }
            }

            dc.add(Restrictions.in("office.id", officeIdList));
        }
        //不显示超级管理员用户
        dc.add(Restrictions.ne("id", ConstUtils.ROOT_ADMIN_ID));
        dc.addOrder(Order.asc("office.id"));
        dc.addOrder(Order.asc("loginName"));
        return userDao.find(dc);
    }

	public User getUserByLoginName(String loginName) {
		return userDao.findByLoginName(loginName);
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		userDao.clear();
		userDao.save(user);
		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void deleteUser(Long id) {
		userDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public void updatePasswordById(Long id, String loginName, String newPassword) {
		userDao.updatePasswordById(entryptPassword(newPassword), id);
		systemRealm.clearCachedAuthorizationInfo(loginName);
	}
	
	@Transactional(readOnly = false)
	public void updateUserLoginInfo(Long id, String ip) {
		userDao.updateLoginInfo(ip, new Date(), id);
	}

	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		byte[] salt = Digests.generateSalt(Digests.SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, Digests.HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}
	
	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, Digests.HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}
	
	//-- Role Service --//
	
	public Role getRole(Long id) {
		return roleDao.findOne(id);
	}

	public Role findRoleByName(String name) {
		return roleDao.findByName(name);
	}
	
	public List<Role> findAllRole(){
		User user = UserUtils.getUser();
		DetachedCriteria dc = roleDao.createDetachedCriteria();
		dc.createAlias("office", "office");
		dc.createAlias("userList", "userList", JoinType.LEFT_OUTER_JOIN);
		dc.add(dataScopeFilter(user, "office", "userList"));
		dc.add(Restrictions.eq(Role.DEL_FLAG, Role.DEL_FLAG_NORMAL));
		dc.addOrder(Order.asc("office.code")).addOrder(Order.asc("name"));
		return roleDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void saveRole(Role role) {
		roleDao.clear();
		roleDao.save(role);
		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void deleteRole(Long id) {
		roleDao.deleteById(id);
		systemRealm.clearAllCachedAuthorizationInfo();
	}
	
	@Transactional(readOnly = false)
	public Boolean outUserInRole(Role role, Long userId) {
		User user = userDao.findOne(userId);
		List<Long> roleIds = user.getRoleIdList();
		List<Role> roles = user.getRoleList();
		// 
		if (roleIds.contains(role.getId())) {
			roles.remove(role);
			saveUser(user);
			return true;
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public User assignUserToRole(Role role, Long userId) {
		User user = userDao.findOne(userId);
		List<Long> roleIds = user.getRoleIdList();
		if (roleIds.contains(role.getId())) {
			return null;
		}
		user.getRoleList().add(role);
		saveUser(user);		
		return user;
	}

	//-- Menu Service --//
	
	public Menu getMenu(Long id) {
		return menuDao.findOne(id);
	}

	public List<Menu> findAllMenu(){
		return UserUtils.getMenuList();
	}
	
	@Transactional(readOnly = false)
	public void saveMenu(Menu menu) {
		menu.setParent(this.getMenu(menu.getParent().getId()));
		String oldParentIds = menu.getParentIds(); // 获取修改前的parentIds，用于更新子节点的parentIds
		menu.setParentIds(menu.getParent().getParentIds()+menu.getParent().getId()+",");
		menuDao.clear();
		menuDao.save(menu);
		// 更新子节点 parentIds
		List<Menu> list = menuDao.findByParentIdsLike("%,"+menu.getId()+",%");
		for (Menu e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
		}
		menuDao.save(list);
		systemRealm.clearAllCachedAuthorizationInfo();
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
	}

	@Transactional(readOnly = false)
	public void deleteMenu(Long id) {
		menuDao.deleteById(id, "%,"+id+",%");
		systemRealm.clearAllCachedAuthorizationInfo();
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
	}
	
}
