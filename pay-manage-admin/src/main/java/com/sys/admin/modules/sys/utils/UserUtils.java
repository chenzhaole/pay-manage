package com.sys.admin.modules.sys.utils;

import com.google.common.collect.Maps;
import com.sys.admin.common.service.BaseService;
import com.sys.admin.common.utils.CacheUtils;
import com.sys.admin.common.utils.ConstUtils;
import com.sys.admin.common.utils.SpringContextHolder;
import com.sys.admin.modules.sys.dao.MenuDao;
import com.sys.admin.modules.sys.dao.OfficeDao;
import com.sys.admin.modules.sys.dao.UserDao;
import com.sys.admin.modules.sys.entity.Area;
import com.sys.admin.modules.sys.entity.Menu;
import com.sys.admin.modules.sys.entity.Office;
import com.sys.admin.modules.sys.entity.User;
import com.sys.admin.modules.sys.security.SystemAuthorizingRealm.Principal;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.subject.Subject;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户工具类
 */
public class UserUtils extends BaseService {
	
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);


	public static final String CACHE_USER = "user";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_AREA_LIST = "areaList";
	public static final String CACHE_OFFICE_LIST = "officeList";
    public static final String CACHE_ALL_OFFICE_LIST = "officeListAll";

    public static User getRootUser(){
        return userDao.findOne(ConstUtils.ROOT_ADMIN_ID);
    }
	
	public static User getUser(){
		User user = (User)getCache(CACHE_USER);
		if (user == null){
			Principal principal = (Principal)SecurityUtils.getSubject().getPrincipal();
			if (principal!=null){
				user = userDao.findOne(principal.getId());
				putCache(CACHE_USER, user);
			}
		}
		if (user == null){
			user = new User();
			SecurityUtils.getSubject().logout();
		}
		return user;
	}

	
	public static User getUser(boolean isRefresh){
		if (isRefresh){
			removeCache(CACHE_USER);
		}
		return getUser();
	}

	public static List<Menu> getMenuList(){
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			User user = getUser();
			if (user.isAdmin()){
				menuList = menuDao.findAllList();
			}else{
				menuList = menuDao.findByUserId(user.getId());
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}
	
	public static List<Area> getAreaList(){
		@SuppressWarnings("unchecked")
		List<Area> areaList = (List<Area>)getCache(CACHE_AREA_LIST);
		if (areaList == null){
			putCache(CACHE_AREA_LIST, areaList);
		}
		return areaList;
	}

    public static String getUserName(Long id) {
    	if (id != null){
			User user = userDao.findOne(id);
			if (user != null) {
				return user.getName();
			}
		}
        return null;
    }

	/**
	 * 是否为平台管理员
	 */
	public static boolean isPlatform(Long id) {
		User user = userDao.findOne(id);
		return user != null && user.getOffice().isRoot();
	}

	/**
	 * 判断当前用户是否为平台管理员
	 */
	public static boolean isCurrentPlatform() {
		return isPlatform(getUser().getId());
	}

	/**
	 * 是否为机构管理员
	 */
	public static boolean isAgency(Long id) {
		User user = userDao.findOne(id);
		return user != null && !user.getOffice().isRoot();
	}

	/**
	 * 判断当前用户是否为机构管理员
	 */
	public static boolean isCurrentAgency() {
		return isAgency(getUser().getId());
	}

	/**
	 * 是否为顶级机构管理员
	 */
	public static boolean isTopAgency(Long id) {
		User user = userDao.findOne(id);
		return user != null && !user.getOffice().isRoot() && user.getOffice().getParent().isRoot();
	}

	/**
	 * 判断当前用户是否为顶级机构管理员
	 */
	public static boolean isCurrentTopAgency() {
		return isTopAgency(getUser().getId());
	}
	
	public static List<Office> getOfficeList(){
		return getOfficeList(true);
	}

    @SuppressWarnings("unchecked")
	public static List<Office> getOfficeList(boolean isDataScopeFilter){
        List<Office> officeList;
        if (isDataScopeFilter) {
            officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
            if (officeList == null || officeList.size() == 0){
                DetachedCriteria dc = officeDao.createDetachedCriteria();
                dc.add(dataScopeFilter(getUser(), dc.getAlias(), ""));
                dc.add(Restrictions.eq("delFlag", Office.DEL_FLAG_NORMAL));
                dc.addOrder(Order.asc("code"));
                officeList = officeDao.find(dc);
                putCache(CACHE_OFFICE_LIST, officeList);
            }
        } else {
            DetachedCriteria dc = officeDao.createDetachedCriteria();
            dc.add(Restrictions.eq("delFlag", Office.DEL_FLAG_NORMAL));
            dc.addOrder(Order.asc("code"));
            officeList = officeDao.find(dc);
        }

		return officeList;
	}

	/**
	 * 获取当前用户可维护的机构ID列表
	 * @return 机构ID列表
	 */
	public static List<Long> getOfficeIdList() {
		List<Office> list = getOfficeList();
		List<Long> idList = null;
		if (list != null) {
			idList = new ArrayList<Long>();
			for (Office office : list) {
				idList.add(office.getId());
			}
		}
		return idList;
	}
	
	// ============== User Cache ==============
	
	public static Object getCache(String key) {
		return getCache(key, null);
	}
	
	public static Object getCache(String key, Object defaultValue) {
		Object obj = getCacheMap().get(key);
		return obj==null?defaultValue:obj;
	}

	public static void putCache(String key, Object value) {
		getCacheMap().put(key, value);
	}

	public static void removeCache(String key) {
		getCacheMap().remove(key);
	}
	
	public static Map<String, Object> getCacheMap(){
		Map<String, Object> map = Maps.newHashMap();
		try{
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			return principal!=null?principal.getCacheMap():map;
		}catch (UnavailableSecurityManagerException e) {
			return map;
		}
	}
	
	/**
	 * 是否是验证码登录
	 * @param useruame 用户名
	 * @param isFail 计数加1
	 * @param clean 计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean){
		Map<String, Integer> loginFailMap = (Map<String, Integer>)CacheUtils.getSysCache("loginFailMap");
		if (loginFailMap==null){
			loginFailMap = Maps.newHashMap();
			CacheUtils.putSysCache("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum==null){
			loginFailNum = 0;
		}
		if (isFail){
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean){
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 3;
	}
}
