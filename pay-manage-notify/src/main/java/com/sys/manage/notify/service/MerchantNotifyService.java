package com.sys.manage.notify.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sys.boss.api.entry.trade.TradeNotify;
import com.sys.common.db.JedisConnPool;
import com.sys.common.db.RocketMQConsumer;
import com.sys.common.util.IdUtil;
import com.sys.common.util.PostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class MerchantNotifyService {
    private Logger logger = LoggerFactory.getLogger(MerchantNotifyService.class);

    private DefaultMQPushConsumer consumer;

    @PostConstruct
    private void initConsumer(){
        logger.info("=================>>manage-notify消费者初始化<<=================");
        try {
            consumer = new RocketMQConsumer.Builder().consumerGroup("MchtNofityConsumerGroup").instanceName("MchtNofityConsumer")
                    .consumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET).topic("TRADE_MCHT").subExpression("MCHT_NOTIFY")
                    .messageListener( //消息监听器
                            new MessageListenerConcurrently() {
                                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                                    return send(msgs,context);
                                }
                            }).build();
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
            logger.error("manage-notify消费者初始化异常："+e.getMessage());
        }
    }

    private ConsumeConcurrentlyStatus send(List<MessageExt> msgs, ConsumeConcurrentlyContext context){
        MessageExt msg = msgs.get(0);
        String json = new String(msgs.get(0).getBody());
        String response = null;
        TradeNotify tradeNotify = null;
        try {
            logger.info("商户异步通知消费者监听到消息：queueId={} queueOffset={} msgId={} commitLogOffset={} reconsumeTimes={} content={}",
                    msg.getQueueId(),msg.getQueueOffset(),msg.getMsgId(),msg.getCommitLogOffset(),msg.getReconsumeTimes(),json);
            tradeNotify = JSON.parseObject(json,TradeNotify.class);
            //1、异步通知商户订单支付结果
            if(tradeNotify!=null){
                String requestUrl = tradeNotify.getUrl();
                String requestMsg = JSON.toJSONString(tradeNotify.getResponse());
                logger.info("[start] 异步通知商户开始，请求地址：{} 请求内容：{}",requestUrl,requestMsg);
                response = PostUtil.postMsg(requestUrl,requestMsg);
                logger.info("[end] 异步通知商户结束，请求地址：{} 请求内容：{} 商户响应：{}",requestUrl,requestMsg,response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("商户异步通知异常，消息内容：{} 异常：{} ",json,e.getMessage());
        }finally{
            //2、商户响应失败的存入redis
            if(!"SUCCESS".equalsIgnoreCase(response)){
                save2memo(tradeNotify);
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private void save2memo(TradeNotify tradeNotify){
        if(tradeNotify!=null){
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = JedisConnPool.getPool("异步通知商户失败，获取redis pool");
                jedis = pool.getResource();
                String redisKey = IdUtil.REDIS_TRADE_PLAT_PAY_NOTIFY + ":" + tradeNotify.getResponse().getBody().getOrderId() + "";
                String value = JSON.toJSONString(tradeNotify);
                jedis.set(redisKey, value);
                // 设置失效时间(1天)
                jedis.expire(redisKey, 3600 * 24);
            } catch (JedisConnectionException je) {
                je.printStackTrace();
                logger.error("Redis Jedis连接异常：" + je.getMessage());
                JedisConnPool.returnBrokenResource(pool, jedis, "异步通知商户失败，JedisConnectionException");
            } catch (Exception e) {
                logger.info("<mchtNotify-error>error[" + e.getMessage() + "]</mchtNotify-error>");
                e.printStackTrace();
            } finally {
                JedisConnPool.returnResource(pool, jedis, "mchtNotify异步通知商户 finally");
            }
        }
    }
}
