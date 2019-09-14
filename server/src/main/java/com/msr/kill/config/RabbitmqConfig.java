package com.msr.kill.config;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 10:22
 */
@Configuration
@Slf4j
public class RabbitmqConfig {

    private final Environment env;

    private final CachingConnectionFactory connectionFactory;

    private final SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    public RabbitmqConfig(Environment env, CachingConnectionFactory connectionFactory, SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer) {
        this.env = env;
        this.connectionFactory = connectionFactory;
        this.factoryConfigurer = factoryConfigurer;
    }

    /**
     * @return 单一消费者
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        return factory;
    }

    /**
     * @return 多个消费者
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //确认消费模式-NONE
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.concurrency", int.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.max-concurrency", int.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.simple.prefetch", int.class));
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) ->
                log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause));

        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
                log.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",
                        exchange, routingKey, replyCode, replyText, message));

        return rabbitTemplate;
    }


    /**
     * @return 构建异步发送邮箱通知的消息模型
     */
    @Bean
    public Queue successEmailQueue() {
        return new Queue(env.getProperty("mq.kill.item.success.email.queue"), true);
    }

    @Bean
    public TopicExchange successEmailExchange() {
        return new TopicExchange(env.getProperty("mq.kill.item.success.email.exchange"), true, false);
    }

    @Bean
    public Binding successEmailBinding() {
        return BindingBuilder.bind(successEmailQueue()).to(successEmailExchange()).with(env.getProperty("mq.kill.item.success.email.routing.key"));
    }

    /**
     * @return 构建秒杀成功之后-订单超时未支付的死信队列消息模型
     */
    @Bean
    public Queue successKillDeadQueue() {
        Map<String, Object> argsMap = Maps.newHashMap();
        argsMap.put("x-dead-letter-exchange", env.getProperty("mq.kill.item.success.kill.dead.exchange"));
        argsMap.put("x-dead-letter-routing-key", env.getProperty("mq.kill.item.success.kill.dead.routing.key"));
        return new Queue(env.getProperty("mq.kill.item.success.kill.dead.queue"), true, false, false, argsMap);
    }

    /**
     * @return 基本交换机
     */
    @Bean
    public TopicExchange successKillDeadProdExchange() {
        return new TopicExchange(env.getProperty("mq.kill.item.success.kill.dead.prod.exchange"), true, false);
    }

    /**
     * @return 创建基本交换机+基本路由 -> 死信队列 的绑定
     */
    @Bean
    public Binding successKillDeadProdBinding() {
        return BindingBuilder.bind(successKillDeadQueue()).to(successKillDeadProdExchange()).with(env.getProperty("mq.kill.item.success.kill.dead.prod.routing.key"));
    }

    /**
     * @return 真正的队列
     */
    @Bean
    public Queue successKillRealQueue() {
        return new Queue(env.getProperty("mq.kill.item.success.kill.dead.real.queue"), true);
    }

    /**
     * @return 死信交换机
     */
    @Bean
    public TopicExchange successKillDeadExchange() {
        return new TopicExchange(env.getProperty("mq.kill.item.success.kill.dead.exchange"), true, false);
    }

    /**
     * @return 死信交换机+死信路由->真正队列 的绑定
     */
    @Bean
    public Binding successKillDeadBinding() {
        return BindingBuilder.bind(successKillRealQueue()).to(successKillDeadExchange()).with(env.getProperty("mq.kill.item.success.kill.dead.routing.key"));
    }
}
