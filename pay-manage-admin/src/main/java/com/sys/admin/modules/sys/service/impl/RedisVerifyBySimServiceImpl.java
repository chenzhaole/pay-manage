package com.sys.admin.modules.sys.service.impl;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.modules.sys.service.RedisKeyConst;
import com.sys.admin.modules.sys.service.RedisVerifyBySimService;
import com.sys.admin.modules.sys.utils.JedisConnPool;
import com.sys.common.util.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Date;
import java.util.Set;

@Service
public class RedisVerifyBySimServiceImpl  implements RedisVerifyBySimService {
    Logger logger = LoggerFactory.getLogger(RedisVerifyServiceImpl.class);
    Jedis jedis=null;
    /*
    *添加购票成功信息
    *
    */
    
    @Override
	public void insertData(String shiftNumber, String mobile, String stationId, String sendDate, String businessType) {
        logger.info("<insertData-start>shiftNumber["+shiftNumber+"],mobile["+mobile+"],stationId["+stationId+"],sendDate["+sendDate+"],businessType["+businessType+"]</insertData-start>");
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = JedisConnPool.getPool("insertData（正常购票）添加购票成功信息");
            jedis = pool.getResource();
            {
                //redis里数据失效时间
                String timeout=GlobalConfig.getConfig("order.redis.timeout.mobile");
                if(timeout==null)
                {
                    //15天的秒数
                    timeout="1296000";
                }
                int intTimeout=Integer.parseInt(timeout);
                Date date=new Date();
                String currDateTime=DateUtils.formatDate(date,"yyyyMMdd");
                String redisKey= RedisKeyConst.VERIFY_DAY_SIM+mobile;
                String hashKey=businessType+stationId+":"+shiftNumber+":"+sendDate+":"+currDateTime;
                jedis.hincrBy(redisKey, hashKey, 1L);
                //设置失效时间
                jedis.expire(redisKey, intTimeout);
            }
        } catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常："+ je.getMessage());
			JedisConnPool.returnBrokenResource(pool, jedis, "insertData（正常购票）添加购票成功信息 JedisConnectionException");
		} catch (Exception e) {
            logger.info("<insertData-error>error["+e.getMessage()+"]</insertData-error>");
            e.printStackTrace();
        } finally {
        	JedisConnPool.returnResource(pool,jedis,"insertData（正常购票）添加购票成功信息 finally");
        }
        logger.info("<insertData-end>shiftNumber["+shiftNumber+"],mobile["+mobile+"],stationId["+stationId+"],sendDate["+sendDate+"],businessType["+businessType+"]</insertData-end>");
    }
    /*
     *校验同一天同一手机号只能买N张票
     *
     */
    
    @Override
	public int validateOrderBySimOperateDay(String mobile, int canSellNumber) {
        int res=0;
        JedisPool pool = null;
        Jedis jedis = null;
        Date date=new Date();
        String currDateTime=DateUtils.formatDate(date,"yyyyMMdd");
        try{
            pool = JedisConnPool.getPool("validateOrderBySimOperateDay（正常购票）校验同一天同一手机号只能买N张票");
            jedis = pool.getResource();
            logger.info("<validateOrderBySimOperateDay-start>,mobile["+mobile+"],canSellNumber["+canSellNumber+"]</validateOrderBySimOperateDay-start>");
            String redisKey= RedisKeyConst.VERIFY_DAY_SIM+mobile;
            Set<String> orders=jedis.hkeys(redisKey);
            if(orders==null) {
                res=1;
            }
            else {
                //买票数   退票数   改签数
                int buyNumber=0;
                int refundNumber=0;
                int alertNumber=0;
                for (String hashKey : orders){
                    String[] operateDates=hashKey.split(":");
                    String operateDate=operateDates[operateDates.length-1];
                    if(hashKey.startsWith(RedisKeyConst.VERIFY_DAY_SIM_BUY)&&operateDate.startsWith(currDateTime)) {
                        buyNumber+=Integer.parseInt(jedis.hget(redisKey,hashKey));
                    }
                    else if (hashKey.startsWith(RedisKeyConst.VERIFY_DAY_SIM_REFUND)&&operateDate.startsWith(currDateTime))  {
                        refundNumber+= Integer.parseInt(jedis.hget(redisKey,hashKey));
                    }
                    else if(hashKey.startsWith(RedisKeyConst.VERIFY_DAY_SIM_ALTER)&&operateDate.startsWith(currDateTime)) {
                        alertNumber+=Integer.parseInt(jedis.hget(redisKey,hashKey));
                    }
                }
                if(buyNumber-refundNumber>canSellNumber) {
                    res= -1;
                }
                else {
                    res= 1;
                }
            }
        } catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常："+ je.getMessage());
			JedisConnPool.returnBrokenResource(pool, jedis, "validateOrderBySimOperateDay（正常购票）校验同一天同一手机号只能买N张票 JedisConnectionException");
		} catch (Exception e) {
            logger.info("<validateOrderbByShift-error>error["+e.getMessage()+"]</validateOrderbByShift-error>");
            res=0;
            e.printStackTrace();
        } finally {
			JedisConnPool.returnResource(pool, jedis, "validateOrderBySimOperateDay（正常购票）校验同一天同一手机号只能买N张票 finally");
        }
        logger.info("<validateOrderbByShift-end>mobile["+mobile+"],canSellNumber["+canSellNumber+"],return["+res+"]</validateOrderbByShift-end>");

        return res;
    }
    /*
     *校验同一天同车站同一发车日期只能买N张票
     *
     */
    
    @Override
	public int validateOrderbBySimSendDay(String mobile, String stationId, String sendDate, int canSellNumber){
        int res=0;
        JedisPool pool = null;
        Jedis jedis = null;
        logger.info("<validateOrderbByStation-start>mobile["+mobile+"],stationId["+stationId+"],sendDate["+sendDate+"],canSellNumber["+canSellNumber+"]</validateOrderbByStation-start>");
        try{
            pool = JedisConnPool.getPool("validateOrderbBySimSendDay（正常购票）校验同一天同车站同一发车日期只能买N张票");
            jedis = pool.getResource();
            Date date=new Date();
            String redisKey= RedisKeyConst.VERIFY_DAY_SIM+mobile;
            Set<String> orders=jedis.hkeys(redisKey);
            if(orders==null) {
                res=1;
            } else {
                //买票数   退票数   改签数
                int buyNumber=0;
                int refundNumber=0;
                int alertNumber=0;
                for (String hashKey : orders){
                    if(hashKey.startsWith(RedisKeyConst.VERIFY_DAY_SIM_BUY+stationId)&&hashKey.contains(":"+sendDate+":")) {
                        buyNumber+=Integer.parseInt(jedis.hget(redisKey,hashKey));
                    }
                    else if (hashKey.startsWith(RedisKeyConst.VERIFY_DAY_SIM_REFUND+stationId)&&hashKey.contains(":"+sendDate+":"))  {
                        refundNumber+= Integer.parseInt(jedis.hget(redisKey,hashKey));
                    }
                    else if(hashKey.startsWith(RedisKeyConst.VERIFY_DAY_SIM_ALTER+stationId)&&hashKey.contains(":"+sendDate+":")) {
                        alertNumber += Integer.parseInt(jedis.hget(redisKey, hashKey));
                    }
                }
                if(buyNumber-refundNumber>canSellNumber) {
                    res= -1;
                }
                else {
                    res= 1;
                }
            }
        } catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常："+ je.getMessage());
			JedisConnPool.returnBrokenResource(pool, jedis, "validateOrderbBySimSendDay（正常购票）校验同一天同车站同一发车日期只能买N张票 JedisConnectionException");
		} catch (Exception e) {
            logger.error("系统错误["+e.getMessage()+"]");
            res= 0;
        }  finally {
            JedisConnPool.returnResource(pool,jedis,"validateOrderbBySimSendDay（正常购票）校验同一天同车站同一发车日期只能买N张票 finally");
        }
        logger.info("<validateOrderbByStation-end>idno["+mobile+"],stationId["+stationId+"],sendDate["+sendDate+"],canSellNumber["+canSellNumber+"],return["+res+"]</validateOrderbByStation-end>");

        return res;
    }
}
