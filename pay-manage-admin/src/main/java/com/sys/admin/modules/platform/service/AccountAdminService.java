package com.sys.admin.modules.platform.service;

import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.core.dao.dmo.MchtAccountDetail;

import java.util.List;

/**
 * 处理后台账户相关
 * @author ALI
 * at 2018/5/30 9:50
 */
public interface AccountAdminService {

	int insert2redisAccTask(CacheMchtAccount cacheMchtAccount);

	CacheMcht queryCacheMcht(String mchtId);

	List<MchtAccountDetail> list ( MchtAccountDetail mchtAccountDetail);
}
