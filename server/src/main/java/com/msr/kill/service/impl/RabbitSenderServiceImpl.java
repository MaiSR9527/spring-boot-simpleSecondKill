package com.msr.kill.service.impl;

import com.msr.kill.dto.KillSuccessUserInfo;
import com.msr.kill.mapper.ItemKillSuccessMapper;
import com.msr.kill.service.RabbitSenderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 10:36
 */
@Service
@Slf4j
public class RabbitSenderServiceImpl implements RabbitSenderService {

    private final RabbitTemplate rabbitTemplate;

    private final Environment env;

    private final ItemKillSuccessMapper itemKillSuccessMapper;

    public RabbitSenderServiceImpl(RabbitTemplate rabbitTemplate, Environment env, ItemKillSuccessMapper itemKillSuccessMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.env = env;
        this.itemKillSuccessMapper = itemKillSuccessMapper;
    }

    @Override
    public void sendKillSuccessEmailMsg(String orderNum) {
        log.info("秒杀成功，开始异步发送消息");

        try {
            if (StringUtils.isNoneBlank(orderNum)) {
                KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNum);

                //TODO:发送消息的逻辑
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                rabbitTemplate.setExchange(env.getProperty("kill.item.success.email.exchange"));
                rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.email.routing.key"));

                //TODO:将info充当信息发送至队列
                rabbitTemplate.convertAndSend(info, message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, KillSuccessUserInfo.class);
                    return message;
                });
            }

        } catch (AmqpException e) {
            log.error("秒杀成功异步发送邮件通知消息-发生异常，消息为：{}", orderNum, e.fillInStackTrace());
        }
    }

    @Override
    public void sendKillSuccessOrderExpireMsg(String orderNum) {
        try {
            if (StringUtils.isNotBlank(orderNum)) {
                KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNum);
                if (info != null) {
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.kill.dead.prod.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.kill.dead.prod.routing.key"));
                    rabbitTemplate.convertAndSend(info, message -> {
                        MessageProperties mp = message.getMessageProperties();
                        mp.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        mp.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, KillSuccessUserInfo.class);

                        //TODO：动态设置TTL(为了测试方便，暂且设置10s)
                        mp.setExpiration(env.getProperty("mq.kill.item.success.kill.expire"));
                        return message;
                    });
                }
            }
        } catch (Exception e) {
            log.error("秒杀成功后生成抢购订单-发送信息入死信队列，等待着一定时间失效超时未支付的订单-发生异常，消息为：{}", orderNum, e.fillInStackTrace());
        }
    }

}
