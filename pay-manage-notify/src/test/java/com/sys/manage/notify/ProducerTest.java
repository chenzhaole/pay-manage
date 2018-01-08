package com.sys.manage.notify;


import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.sys.common.db.RocketMQProducer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProducerTest {

    public static void main(String []args) throws Exception {
        shouldSendMessage();
    }

    public static void shouldSendMessage() throws Exception{
        //1、创建生产者
        DefaultMQProducer producer = new RocketMQProducer.Builder().producerGroup("ProducerTest").instanceName("ProducerTest_1").build();
        //2、启动生产者
        producer.start();
        //3、发送消息
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);

        for (int i = 1; i <= 2; i++) {
            String body = dateStr + " hello7-rocketMQ[" + i+"]";
            Message msg = new Message("ProducerTestTopic_1", "TagA", "KEY" + i, body.getBytes());
            SendResult sendResult = producer.send(msg);
            System.out.println(sendResult + " , body : " + body);
        }

        //3、关闭生产者
        producer.shutdown();
    }

}
