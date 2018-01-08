package com.sys.manage.notify;


import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sys.common.db.RocketMQConsumer;

import java.util.List;

public class ConsumerTest {

    public static void main(String []args) throws Exception {
        shouldConsumeOneMessage();
        shouldConsumeMultiMessage();
    }

    /**
     * 监听触发，消费一条消息
     * @throws MQClientException
     */
    public static void shouldConsumeOneMessage() throws MQClientException {
        DefaultMQPushConsumer consumer =
                new RocketMQConsumer.Builder()
                        .consumerGroup("ConsumerTest")//消费者所属组
                        .instanceName("ConsumerTest_1")//消费者实例名称
                        //第一次启动是从队列头部开始消费还是队列尾部开始消费,如果非第一次启动，那么按照上次消费的位置继续消费
                        .consumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET)
                        .topic("ProducerTestTopic_1")//订阅主题
                        .subExpression("TagA")//订阅主题下面的某个标签
                        .messageListener(
                                //消息监听器
                                new MessageListenerConcurrently() {
                                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                                        try {
                                            System.out.println("=============one 监听触发==========[start]");
                                            for(MessageExt msg : msgs){
                                                System.out.println("one 拉取消息数量："+msgs.size()+"  "+msg+ " , content : "+ new String(msg.getBody()));
                                            }
                                            System.out.println("=============one 监听触发==========[end]");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                                    }
                        }).build();
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }

    /**
     * 监听触发，消费多条消息
     * <p>
     *     1、consumeMessageBatchMaxSize=10，设置后，正常情况下，先开启consumer，后开启producer，消息不会堆积，producer生产一条，consumer消费一条；
     *     2、consumeMessageBatchMaxSize=10，设置后，若consumer未开启，producer生产多条消息后，消息堆积，开启consumer，一次会消费多条，最大消费10条；
     * </p>
     * @throws MQClientException
     */
    public static void shouldConsumeMultiMessage() throws MQClientException{
        DefaultMQPushConsumer consumer =
                new RocketMQConsumer.Builder()
                        .consumerGroup("ConsumerTest")//消费者所属组
                        .instanceName("ConsumerTest_2")//消费者实例名称
                        //第一次启动是从队列头部开始消费还是队列尾部开始消费,如果非第一次启动，那么按照上次消费的位置继续消费
                        .consumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET)
                        .topic("ProducerTestTopic_1")//订阅主题
                        .subExpression("TagA")//订阅主题下面的某个标签
                        .consumeMessageBatchMaxSize(10)//一次监听触发，消费消息数量的最大值
                        .messageListener(
                                //消息监听器
                                new MessageListenerConcurrently() {
                                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                                        try {
                                            System.out.println("=============multi 监听触发 "+msgs.size()+"==========[start]");
                                            for(MessageExt msg : msgs){
                                                System.out.println("multi 拉取消息数量："+msgs.size()+"  "+msg+ " , content : "+ new String(msg.getBody()));
                                            }
                                            System.out.println("=============multi 监听触发 "+msgs.size()+"==========[end]");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                                    }
                                }).build();
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }




}
