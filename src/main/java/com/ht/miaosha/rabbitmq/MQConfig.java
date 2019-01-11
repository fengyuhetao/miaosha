package com.ht.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by hetao on 2019/1/10.
 */
@Configuration
public class MQConfig {
    public static final String MIAOSHA_QUEUE = "miaosha_queue";

    /**
     * Direct 模式 交换机Exchange
     * @return
     */
    @Bean
    public Queue miaoshaQueue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }
}
