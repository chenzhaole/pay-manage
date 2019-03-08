package com.sys.admin.modules.platform.service;

import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.core.dao.dmo.CaAccountAudit;
import com.sys.core.dao.dmo.MchtGatewayOrder;
import com.sys.core.dao.dmo.MchtInfo;
import com.sys.core.dao.dmo.PlatProxyDetail;

public interface CaAccountAuditAdminService {

    public CacheMchtAccount bulidRedisPayTaskObject(MchtGatewayOrder mchtGatewayOrder, MchtInfo mchtInfo, CaAccountAudit caAccountAudit);
    public CacheMchtAccount bulidRedisProxyTaskObject(PlatProxyDetail platProxyDetail,MchtInfo mchtInfo,CaAccountAudit caAccountAudit);
    public boolean insert2redisAccTask(CacheMchtAccount cacheMchtAccount);

}
