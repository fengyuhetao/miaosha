package com.ht.miaosha.rabbitmq;

import com.ht.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * Created by hetao on 2019/1/10.
 */
@Service
public class MQSender {
    private static final Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void send(Object message) {
        String msg = RedisService.beanToString(message);
        logger.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendTopic(Object message) {
        String msg = RedisService.beanToString(message);
        logger.info("send message topic:" + msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.#", msg+"2");
    }

    public void sendFanout(Object message) {
        String msg = RedisService.beanToString(message);
        logger.info("send message fanout:" + msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
    }
}
