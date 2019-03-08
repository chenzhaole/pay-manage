package com.sys.admin.modules.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.sys.admin.modules.platform.service.CaAccountAuditAdminService;
import com.sys.boss.api.entry.cache.CacheChanAccount;
import com.sys.boss.api.entry.cache.CacheMcht;
import com.sys.boss.api.entry.cache.CacheMchtAccount;
import com.sys.boss.api.entry.cache.CacheOrder;
import com.sys.common.db.JedisConnPool;
import com.sys.common.enums.*;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.math.BigDecimal;

@Service
public class CaAccountAuditAdminServiceImpl implements CaAccountAuditAdminService {
    private static final Logger logger =LoggerFactory.getLogger(CaAccountAuditAdminServiceImpl.class);
    @Override
    public CacheMchtAccount bulidRedisPayTaskObject(MchtGatewayOrder mchtGatewayOrder, MchtInfo mchtInfo, CaAccountAudit caAccountAudit) {
        CacheMchtAccount cacheMchtAccount = new CacheMchtAccount();
        cacheMchtAccount.setType(Integer.valueOf(MchtAccountTypeEnum.ADJUSTMENT_ACCOUNT.getCode()));
        //商户信息
        CacheMcht cacheMcht = new CacheMcht();
        cacheMcht.setMchtId(mchtInfo.getMchtCode());
        cacheMchtAccount.setCacheMcht(cacheMcht);
        PlatAccountAdjust platAccountAdjust = new PlatAccountAdjust();
        platAccountAdjust.setId(caAccountAudit.getId());
        platAccountAdjust.setMchtId(mchtInfo.getMchtCode());
        platAccountAdjust.setAdjustType(AdjustTypeEnum.ADJUST_ADD.getCode());
        platAccountAdjust.setAccountType(mchtGatewayOrder.getAccountType());
        platAccountAdjust.setAdjustAmount(BigDecimal.valueOf(mchtGatewayOrder.getAmount()));
        if(mchtGatewayOrder.getMchtFeeRate()!=null && mchtGatewayOrder.getMchtFeeRate().compareTo(BigDecimal.ZERO)>0){
            platAccountAdjust.setFeeType(FeeTypeEnum.RATIO.getCode());
        }else if(mchtGatewayOrder.getMchtFeeAmount()!=null){
            platAccountAdjust.setFeeType(FeeTypeEnum.FIXED.getCode());
        }
        platAccountAdjust.setFeeRate(mchtGatewayOrder.getMchtFeeRate());
        platAccountAdjust.setFeeAmount(mchtGatewayOrder.getMchtFeeAmount());
        cacheMchtAccount.setPlatAccountAdjust(platAccountAdjust);
        return cacheMchtAccount;
    }

    @Override
    public CacheMchtAccount bulidRedisProxyTaskObject(PlatProxyDetail platProxyDetail,MchtInfo mchtInfo, CaAccountAudit caAccountAudit) {
        CacheMchtAccount cacheMchtAccount = new CacheMchtAccount();
        cacheMchtAccount.setType(Integer.valueOf(MchtAccountTypeEnum.ADJUSTMENT_ACCOUNT.getCode()));
        CacheMcht cacheMcht = new CacheMcht();
        cacheMcht.setMchtId(mchtInfo.getMchtCode());
        PlatAccountAdjust platAccountAdjust = new PlatAccountAdjust();
        platAccountAdjust.setId(caAccountAudit.getId());
        platAccountAdjust.setMchtId(platProxyDetail.getMchtId());
        platAccountAdjust.setAdjustType(AdjustTypeEnum.ADJUST_ADD.getCode());
        platAccountAdjust.setAccountType(AccAccountTypeEnum.CASH.getCode());
        platAccountAdjust.setAdjustAmount(platProxyDetail.getAmount().add(platProxyDetail.getMchtFee()));
        return cacheMchtAccount;
    }

    @Override
    public boolean insert2redisAccTask(CacheMchtAccount cacheMchtAccount) {
        JedisPool pool = null;
        Jedis jedis = null;
        long rs = 0;
        try {
            pool = JedisConnPool.getPool("缓存插入cacheMchtAccount信息");
            jedis = pool.getResource();
            rs = jedis.lpush(IdUtil.REDIS_ACCT_MCHT_ACCOUNT_ADJUST_TASK_LIST, JSON.toJSONString(cacheMchtAccount));
            logger.info("插入了一个新的任务： rsPay = " + rs);
            return true;
        } catch (JedisConnectionException je) {
            logger.error("Redis Jedis连接异常：" + je.getMessage());
            je.printStackTrace();
            return false;
        } catch (Exception e) {
            logger.error("<insertData-error>error[" + e.getMessage() + "]</insertData-error>");
            e.printStackTrace();
            return false;
        } finally {
            JedisConnPool.returnResource(pool, jedis, "");
        }
    }

    @Override
    public CacheChanAccount bulidMqPayTaskObject(MchtGatewayOrder mchtGatewayOrder, MchtInfo mchtInfo, CaAccountAudit caAccountAudit) {
        CacheChanAccount cacheChanAccount = new CacheChanAccount();
        PlatAccountAdjust platAccountAdjust = new PlatAccountAdjust();
        platAccountAdjust.setId(caAccountAudit.getId());
        cacheChanAccount.setType(ChanStatAccountTypeEnum.REPEAT_PAY_ADJUST.getCode());
        cacheChanAccount.setPlatAccountAdjust(platAccountAdjust);
        CacheOrder cacheOrder = new CacheOrder();
        cacheOrder.setPlatOrderId(mchtGatewayOrder.getPlatOrderId());
        cacheOrder.setMchtOrderId(mchtGatewayOrder.getMchtOrderId());
        cacheOrder.setChanOrderId(mchtGatewayOrder.getChanOrderId());
        cacheOrder.setChanRealFeeRate(mchtGatewayOrder.getChanRealFeeRate());
        cacheOrder.setChanRealFeeAmount(mchtGatewayOrder.getChanRealFeeAmount());
        cacheChanAccount.setCacheOrder(cacheOrder);
        return cacheChanAccount;
    }

    @Override
    public CacheChanAccount bulidMqProxyObject(PlatProxyDetail platProxyDetail, MchtInfo mchtInfo, CaAccountAudit caAccountAudit) {
        CacheChanAccount cacheChanAccount = new CacheChanAccount();
        PlatAccountAdjust platAccountAdjust = new PlatAccountAdjust();
        platAccountAdjust.setId(caAccountAudit.getId());
        cacheChanAccount.setType(ChanStatAccountTypeEnum.REPEAT_PROXY_ADJUST.getCode());
        cacheChanAccount.setPlatProxyDetail(platProxyDetail);
        return cacheChanAccount;
    }
}
