package com.msr.kill.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Description: ZooKeeper组件自定义配置
 * @Author: maishuren
 * @Date: 2019/9/14 15:05
 */
@Configuration
public class ZookeeperConfig {

    private final Environment env;

    public ZookeeperConfig(Environment env) {
        this.env = env;
    }


    /**
     * 自定义注入ZooKeeper客户端操作实例
     *
     * @return 返回对象
     */
    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(env.getProperty("zk.host"))
                .namespace(env.getProperty("zk.namespace"))
                //重试策略
                .retryPolicy(new RetryNTimes(5, 1000))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }
}
