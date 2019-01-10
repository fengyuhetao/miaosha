package com.ht.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Created by hetao on 2019/1/10.
 */
@Service
public class MQReceiver {

    private static Logger log =  LoggerFactory.getLogger(MQReceiver.class);

    /**
     * direct 模式
     * @param message
     */
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive Message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("receive Message topic1:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("receive Message topic2:" + message);
    }

    @RabbitListener(queues = MQConfig.FANOUT_QUEUE1)
    public void receiveFanout1(String message) {
        log.info("receive Message fanout1:" + message);
    }

    @RabbitListener(queues = MQConfig.FANOUT_QUEUE2)
    public void receiveFanout2(String message) {
        log.info("receive Message fanout2:" + message);
    }
}
