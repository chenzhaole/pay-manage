
package com.sys.gateway.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sys.boss.api.entry.CommonResult;
import com.sys.common.db.JedisConnPool;
import com.sys.common.enums.ErrorCodeEnum;
import com.sys.common.enums.ProxyPayDetailStatusEnum;
import com.sys.common.util.IdUtil;
import com.sys.core.dao.dmo.PlatProxyDetail;
import com.sys.gateway.service.GwDFSendNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 代付明细异步通知
 * @author  duanjintang
 * 日期：2018-11-12
 */
public class DFNotifyConsumerTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(DFNotifyConsumerTask.class);

	private static final String TAG = "[DFNotifyConsumerTask]";
	private static final String BIZ = "[代付明细异步通知]";
	@Autowired
	private GwDFSendNotifyService sendDFNotifyService;

	final ExecutorService threadPool = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors() * 2, new NamedThreadFactory("DFSendNotifyThreadPool"));
	/**
	 * 根据配置 初始化消费者
	 */
	public void init(){
	}

	@Override
	public void run() {
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = JedisConnPool.getPool();
			jedis = pool.getResource();
			for(;;) {
					try {
						String proxyDetail = jedis.rpoplpush(IdUtil.REDIS_PROXY_DETAIL_RESULT_NOTIFY_LIST, IdUtil.REDIS_PROXY_DETAIL_RESULT_NOTIFY_LIST);
						if(proxyDetail==null||"".equals(proxyDetail)){
							Thread.sleep(1000);
							continue;
						}
						logger.info(BIZ + "读取队列的值 redisJsonStrValue=" + proxyDetail);
						jedis.rpop(IdUtil.REDIS_PROXY_DETAIL_RESULT_NOTIFY_LIST);
						Map<String, Object> proxyDetailMap = JSON.parseObject(proxyDetail, Map.class);
						PlatProxyDetail detail = JSON.parseObject(proxyDetailMap.get("detail").toString(), PlatProxyDetail.class);
						String batchStatus = proxyDetailMap.get("batchStatus").toString();
						String notifyUrl   = proxyDetailMap.get("notifyUrl").toString();
						String log_tag = BIZ+"商户代付批次号："+detail.getMchtBatchId()+"，平台批次号："+detail.getPlatBatchId()+",代付明细id:"+detail.getId();
						if(ProxyPayDetailStatusEnum.DF_SUCCESS.getCode().equals(detail.getPayStatus())|| ProxyPayDetailStatusEnum.DF_FAIL.getCode().equals(detail.getPayStatus())){
							CommonResult serviceResult = sendDFNotifyService.sendNotify(detail,batchStatus,notifyUrl,log_tag);
							if (ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())) {
								logger.info(log_tag + "，通知商户成功");
							} else {
								logger.info(log_tag + "，通知商户失败");
								//开启线程，异步通知商户,补抛机制,为了便于排查多线程问题，这里给线程指定名称

								threadPool.execute(new Runnable() {
									@Override
									public void run() {
										int count = 1;//开始补抛
										logger.info(log_tag +"，异步通知商户失败，开始执行补抛,count="+count+",batchStatus="+batchStatus+"，notifyUrl="+notifyUrl+",CacheTrade="+ JSONObject.toJSONString(detail));
										throwMchtNotifyInfo(log_tag, detail,batchStatus,notifyUrl, count);
									}
								});
							}
						}
						Thread.sleep(1);
					} catch (Throwable e) {
						// TODO: handle exception
						logger.error(TAG+""+e.getMessage(),e);
					}
			}
		}
		catch (Throwable e) {
			logger.error(TAG+""+e.getMessage(),e);
		}
	}

	/**
	 * 补抛商户异步通知流水
	 * @param count  补抛次数
	 */
	private void throwMchtNotifyInfo(String log_tag, PlatProxyDetail detail, String batchStatus, String notifyUrl, int count) {
		//总共补抛四次
		int totalCount = 4;
		try{
			if(count > totalCount){
				logger.info(log_tag+"，当前补抛次数是"+count+",补抛次数已经超过"+totalCount+"次，不再对商户异步通知进行补抛");
				return;
			}
			switch (count){
				case 1 :
					logger.info(log_tag+"，异步通知商户失败，开始执行补抛,当前是第"+count+"次补抛,batchStatus="+batchStatus+",notifyUrl="+notifyUrl+"，PlatProxyDetail="+JSONObject.toJSONString(detail));
					break;
				case 2 :
					Thread.sleep(60000);
					logger.info(log_tag +"，异步通知商户失败，开始执行补抛,当前是第"+count+"次补抛,batchStatus="+batchStatus+",notifyUrl="+notifyUrl+"，PlatProxyDetail="+JSONObject.toJSONString(detail));
					break;
				case 3 :
					Thread.sleep(60000);
					logger.info(log_tag +"，异步通知商户失败，开始执行补抛,当前是第"+count+"次补抛,batchStatus="+batchStatus+",notifyUrl="+notifyUrl+"，PlatProxyDetail="+JSONObject.toJSONString(detail));
					break;
				case 4 :
					Thread.sleep(60000);
					logger.info(log_tag +"，异步通知商户失败，开始执行补抛,当前是第"+count+"次补抛,batchStatus="+batchStatus+",notifyUrl="+notifyUrl+"，PlatProxyDetail="+JSONObject.toJSONString(detail));
					break;
			}
			CommonResult serviceResult = sendDFNotifyService.sendNotify(detail,batchStatus,notifyUrl,log_tag);
			logger.info(log_tag +"，异步通知商户失败，执行补抛，当前是第"+count+"次补抛，补抛后sendNotifyService返回的CommonResult="+JSONObject.toJSONString(serviceResult));

			if (ErrorCodeEnum.SUCCESS.getCode().equals(serviceResult.getRespCode())) {
				logger.info(log_tag +"，异步通知商户失败，执行补抛，当前是第"+count+"次补抛，补抛结果为：通知商户成功");
				return;
			} else {
				logger.info(log_tag +"，异步通知商户失败，执行补抛，当前是第"+count+"次补抛，补抛结果为：通知商户失败");
				throwMchtNotifyInfo(log_tag,detail, batchStatus,notifyUrl,count+1);
			}
		}catch (Exception e){
			e.printStackTrace();
			logger.info(log_tag+"，异步通知商户失败，执行补抛，当前是第"+count+"次补抛，抛异常，Exception="+e.getMessage());
			if(count <= totalCount){
				throwMchtNotifyInfo(log_tag, detail, batchStatus,notifyUrl,count+1);
			}
		}
	}


}
