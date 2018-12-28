package com.ht.miaosha.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by hetao on 2018/12/21.
 * 获取配置文件中所有以redis开头的配置
 */
@Component
@ConfigurationProperties(prefix="redis")
@Data
public class RedisConfig {
    private String host;

    private int port;

    private int timeout;

    private String password;

    private int poolMaxTotal;

    private int poolMaxIdle;

    private int poolMaxWait;
}
