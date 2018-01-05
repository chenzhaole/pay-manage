package com.sys.admin.modules.sys.service.impl;

import com.sys.admin.common.config.GlobalConfig;
import com.sys.admin.modules.sys.service.RedisKeyConst;
import com.sys.admin.modules.sys.service.RedisVerifyService;
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
public class RedisVerifyServiceImpl  implements RedisVerifyService {
    Logger logger = LoggerFactory.getLogger(RedisVerifyServiceImpl.class);
    Jedis jedis=null;
    /*
    *添加购票成功信息
    *
    */
    
    @Override
	public void insertData(String shiftNumber, String idno, String stationId, String sendDate, String businessType) {
        logger.info("<insertData-start>shiftNumber["+shiftNumber+"],idno["+idno+"],stationId["+stationId+"],sendDate["+sendDate+"],businessType["+businessType+"]</insertData-start>");
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = JedisConnPool.getPool("insertData（正常购票2）添加购票成功信息");
            jedis = pool.getResource();
            {
                //redis里数据失效时间
                String timeout=GlobalConfig.getConfig("order.redis.timeout");
                if(timeout==null)
                {
                    //15天的秒数
                    timeout="1296000";
                }
                int intTimeout=Integer.parseInt(timeout);
                Date date=new Date();
                String currDateTime=DateUtils.formatDate(date,"yyyyMMddHHmmss");
                String redisKey= RedisKeyConst.VERIFY_DAY_ID_SHIFT+idno;
                String hashKey=businessType+stationId+":"+shiftNumber+":"+sendDate+":"+currDateTime;
                //改签票删除原来票信息
                if(VERIFY_DAY_ID_SHIFT_ALTER.equals(businessType)) {
                    Set<String> orders=jedis.hkeys(redisKey);
                    if(orders!=null) {
                        for (String order:orders) {
                            if (order.startsWith(RedisKeyConst.VERIFY_DAY_ID_SHIFT_BUY+stationId+":"+shiftNumber+":"+sendDate)) {
                                jedis.hdel(redisKey,order);
                                break;
                            }
                        }
                    }

                }
                jedis.hset(redisKey, hashKey, "1");
                //设置失效时间
                jedis.expire(redisKey,intTimeout);
            }
        } catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
			JedisConnPool.returnBrokenResource(pool, jedis, "insertData（正常购票2）添加购票成功信息 JedisConnectionException");
		} catch (Exception e) {
            logger.info("<insertData-error>error["+e.getMessage()+"]</insertData-error>");
            e.printStackTrace();
        } finally {
        	JedisConnPool.returnResource(pool, jedis,"insertData（正常购票2）添加购票成功信息 finally");
        }
        logger.info("<insertData-end>shiftNumber["+shiftNumber+"],idno["+idno+"],stationId["+stationId+"],sendDate["+sendDate+"],businessType["+businessType+"]</insertData-end>");
    }
    /*
     *校验同一天同班次只能买N张票
     *
     */
    
    @Override
	public int validateOrderbByShift(String shiftNumber, String idno, String stationId, String sendDate, int canSellNumber) {
        int res=0;
        JedisPool pool = null;
        Jedis jedis = null;
        try{
            pool = JedisConnPool.getPool("validateOrderbByShift（正常购票2）校验同一天同班次只能买N张票");
            jedis = pool.getResource();
            logger.info("<validateOrderbByShift-start>shiftNumber["+shiftNumber+"],idno["+idno+"],stationId["+stationId+"],sendDate["+sendDate+"],canSellNumber["+canSellNumber+"]</validateOrderbByShift-start>");
            String redisKey= RedisKeyConst.VERIFY_DAY_ID_SHIFT+idno;
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
                   if(hashKey.startsWith(RedisKeyConst.VERIFY_DAY_ID_SHIFT_BUY+stationId+":"+shiftNumber+":"+sendDate)) {
                       buyNumber++;
                   }
                   else if (hashKey.startsWith(RedisKeyConst.VERIFY_DAY_ID_SHIFT_REFUND+stationId+":"+shiftNumber+":"+sendDate)) {
                       refundNumber++;
                   }
                   else if(hashKey.startsWith(RedisKeyConst.VERIFY_DAY_ID_SHIFT_ALTER+stationId+":"+shiftNumber+":"+sendDate)) {
                       alertNumber++;
                   }
               }
               if(buyNumber-refundNumber>=canSellNumber) {
                   res= -1;
               }
               else {
                   res= 1;
               }
           }
        } catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
			JedisConnPool.returnBrokenResource(pool, jedis, " validateOrderbByShift（正常购票2）校验同一天同班次只能买N张票 JedisConnectionException");
		} catch (Exception e) {
            logger.info("<validateOrderbByShift-error>error["+e.getMessage()+"]</validateOrderbByShift-error>");
            res=0;
            e.printStackTrace();
        } finally {
            JedisConnPool.returnResource(pool, jedis, " validateOrderbByShift（正常购票2）校验同一天同班次只能买N张票 finally");
        }
        logger.info("<validateOrderbByShift-end>shiftNumber["+shiftNumber+"],idno["+idno+"],stationId["+stationId+"],sendDate["+sendDate+"],canSellNumber["+canSellNumber+"],return["+res+"]</validateOrderbByShift-end>");
        return res;
    }
    /*
     *校验同一天同车站只能买N张票
     *
     */
    
    @Override
	public int validateOrderbByStation(String idno, String stationId, String sendDate, int canSellNumber) {
        int res=0;
        JedisPool pool = null;
        Jedis jedis = null;
        logger.info("<validateOrderbByStation-start>idno["+idno+"],stationId["+stationId+"],sendDate["+sendDate+"],canSellNumber["+canSellNumber+"]</validateOrderbByStation-start>");
        try{
            pool = JedisConnPool.getPool("validateOrderbByStation（正常购票2）校验同一天同车站只能买N张票");
            jedis = pool.getResource();
            Date date=new Date();
            String currDate= DateUtils.formatDate(date,"yyyyMMdd");
            String currDateTime=DateUtils.formatDate(date,"yyyyMMddHHmmss");
            String redisKey= RedisKeyConst.VERIFY_DAY_ID_SHIFT+idno;
            Set<String> orders=jedis.hkeys(redisKey);
            if(orders==null)  {
                res=1;
            }
            else {
                //买票数   退票数   改签数
                int buyNumber=0;
                int refundNumber=0;
                int alertNumber=0;
                for (String hashKey : orders){
                    if(hashKey.startsWith(RedisKeyConst.VERIFY_DAY_ID_SHIFT_BUY+stationId)&&hashKey.contains(":"+sendDate+":"))  {
                        buyNumber++;
                    }
                    else if (hashKey.startsWith(RedisKeyConst.VERIFY_DAY_ID_SHIFT_REFUND+stationId)&&hashKey.contains(":"+sendDate+":")) {
                        refundNumber++;
                    }
                    else if(hashKey.startsWith(RedisKeyConst.VERIFY_DAY_ID_SHIFT_ALTER+stationId)&&hashKey.contains(":"+sendDate+":")) {
                        alertNumber++;
                    }
                }
                if(buyNumber-refundNumber>=canSellNumber) {
                    res= -1;
                }
                else {
                    res= 1;
                }
            }
        } catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
			JedisConnPool.returnBrokenResource(pool, jedis, " validateOrderbByStation（正常购票2）校验同一天同车站只能买N张票 JedisConnectionException");
		} catch (Exception e) {
            logger.error("系统错误["+e.getMessage()+"]");
            res= 0;
        } finally {
        	JedisConnPool.returnResource(pool, jedis, " validateOrderbByStation（正常购票2）校验同一天同车站只能买N张票 finally");
        }
        logger.info("<validateOrderbByStation-end>idno["+idno+"],stationId["+stationId+"],sendDate["+sendDate+"],canSellNumber["+canSellNumber+"],return["+res+"]</validateOrderbByStation-end>");

        return res;
    }
    @Override
	public String getMaxNo(String businessType)
    {
        long no=0L;
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = JedisConnPool.getPool("getMaxNo（正常购票2）");
            jedis = pool.getResource();
            no=jedis.incr(businessType);
        } catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
			JedisConnPool.returnBrokenResource(pool, jedis, " getMaxNo（正常购票2） JedisConnectionException");
		} catch (Exception e) {
            no=buildRandom(5);
            no=900000+no;
            e.printStackTrace();
            logger.error("<getOrderMaxId>error["+e.getMessage()+"]</getOrderMaxId>");
        } finally {
        	JedisConnPool.returnResource(pool, jedis, " getMaxNo（正常购票2） finally");
        }
        String strNo = String.valueOf(no);
        int len = strNo.length();
        String tmp = "000000000000000"+strNo;
        String rtn = tmp.substring(tmp.length()-6,tmp.length());
        logger.error("<getOrderMaxId-end>orderid["+no+"]</getOrderMaxId-end>");
        return rtn;
    }
    
    @Override
	public Long getMaxNoLong(String businessType) {
        long no=0L;
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = JedisConnPool.getPool("getMaxNoLong（正常购票2）");
            jedis = pool.getResource();
            no=jedis.incr(businessType);
        } catch (JedisConnectionException je) {
			je.printStackTrace();
			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
			JedisConnPool.returnBrokenResource(pool, jedis, " getMaxNoLong（正常购票2） JedisConnectionException");
		} catch (Exception e) {
            no=buildRandom(5);
            no=900000+no;
            e.printStackTrace();
            logger.error("<getOrderMaxId>error["+e.getMessage()+"]</getOrderMaxId>");
        } finally {
			JedisConnPool.returnResource(pool, jedis, " getMaxNoLong（正常购票2） finally");
        }
        return no;
    }
    // 获取退单号
    @Override
	public String getMaxRefundNo(String businessType) {
    	
    	 long no=0L;
         JedisPool pool = null;
         Jedis jedis = null;
         try {
             pool = JedisConnPool.getPool("getMaxRefundNo（正常购票2）获取退单号");
             jedis = pool.getResource();
             no=jedis.incr(businessType);
         } catch (JedisConnectionException je) {
 			je.printStackTrace();
 			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
 			JedisConnPool.returnBrokenResource(pool, jedis, " getMaxRefundNo（正常购票2）获取退单号 JedisConnectionException");
 		} catch (Exception e) {
             no=buildRandom(5);
             no=900000+no;
             e.printStackTrace();
             logger.error("<getMaxRefundNo>error["+e.getMessage()+"]</getMaxRefundNo>");
         } finally {
  			JedisConnPool.returnResource(pool, jedis, " getMaxRefundNo（正常购票2）获取退单号 finally");
         }
         String strNo = String.valueOf(no);
         int len = strNo.length();
         String tmp = "000000000000000"+strNo;
         String rtn = tmp.substring(tmp.length()-6,tmp.length());
         logger.error("<getMaxRefundNo-end>orderid["+no+"]</getMaxRefundNo-end>");
         return rtn;
    }
    
    
    @Override
	public void updateData(String key, String value){
    	logger.info("<updateData-start>key["+key+"],value["+value+"]</updateData-start>");
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = JedisConnPool.getPool("updateData（正常购票2）");
            jedis = pool.getResource();  {
                jedis.set(key, value);
//                //设置失效时间
//                jedis.expire(key,timeout);
            }
        } catch (JedisConnectionException je) {
 			je.printStackTrace();
 			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
 			JedisConnPool.returnBrokenResource(pool, jedis, " updateData（正常购票2） JedisConnectionException");
 		} catch (Exception e) {
            logger.info("<updateData-error>error["+e.getMessage()+"]</updateData-error>");
            e.printStackTrace();
        } finally {
 			JedisConnPool.returnResource(pool, jedis, " updateData（正常购票2） finally");
        }
    }
    
    
    @Override
	public void insertData(String key, String value, int timeout, String businessType) {
        logger.info("<insertData-start>key["+key+"],value["+value+"]</insertData-start>");
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = JedisConnPool.getPool("insertData（正常购票2）");
            jedis = pool.getResource(); {
	        	String redisKey=businessType+key;
	            jedis.set(redisKey, value);
	            //设置失效时间
	            jedis.expire(redisKey,timeout);
            }
        } catch (JedisConnectionException je) {
 			je.printStackTrace();
 			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
 			JedisConnPool.returnBrokenResource(pool, jedis, " insertData（正常购票2） JedisConnectionException");
 		} catch (Exception e) {
            logger.info("<insertData-error>error["+e.getMessage()+"]</insertData-error>");
            e.printStackTrace();
        } finally {
        	JedisConnPool.returnResource(pool, jedis, " insertData（正常购票2） finally");
        }
   }
    
    @Override
	public String queryData(String key, String businessType) {
        logger.info("<queryData-start>key["+key+"],</queryData-start>");
        JedisPool pool = null;
        Jedis jedis = null;
        String reslut="";
        try {
            pool = JedisConnPool.getPool("queryData（正常购票2）key="+key+" businessType="+businessType);
            jedis = pool.getResource(); {
	        	String redisKey=businessType+key;
	        	reslut=jedis.get(redisKey);              
            }
        } catch (JedisConnectionException je) {
 			je.printStackTrace();
 			logger.error("Redis Jedis连接异常JedisConnectionException："+ je.getMessage());
 			JedisConnPool.returnBrokenResource(pool, jedis, "queryData（正常购票2）key="+key+" businessType="+businessType+" JedisConnectionException");
 		} catch (Exception e) {
            logger.info("<queryData-error>error["+e.getMessage()+"]</queryData-error>");
            e.printStackTrace();
        } finally {
 			JedisConnPool.returnResource(pool, jedis, "queryData（正常购票2）key="+key+" businessType="+businessType+" finally");
        }
        return reslut;
   }
    private  int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        } for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }
}
