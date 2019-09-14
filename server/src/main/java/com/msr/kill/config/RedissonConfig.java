package com.msr.kill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 15:03
 */
@Configuration
public class RedissonConfig {

    private final Environment env;

    public RedissonConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public RedissonClient redissonClient(){
        Config config=new Config();
        config.useSingleServer()
                .setAddress(env.getProperty("redis.config.host"))
                .setPassword(env.getProperty("spring.redis.password"));
        return Redisson.create(config);
    }
}
