package com.msr.kill.service.impl;

import com.msr.kill.dto.KillSuccessUserInfo;
import com.msr.kill.dto.MailDto;
import com.msr.kill.entity.ItemKillSuccess;
import com.msr.kill.mapper.ItemKillSuccessMapper;
import com.msr.kill.service.MailService;
import com.msr.kill.service.RabbitReceiverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 10:36
 */
@Service
@Slf4j
public class RabbitReceiverServiceImpl implements RabbitReceiverService {


    private final MailService mailService;

    private final Environment env;

    private final ItemKillSuccessMapper itemKillSuccessMapper;

    public RabbitReceiverServiceImpl(MailService mailService, Environment env, ItemKillSuccessMapper itemKillSuccessMapper) {
        this.mailService = mailService;
        this.env = env;
        this.itemKillSuccessMapper = itemKillSuccessMapper;
    }


    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"}, containerFactory = "singleListenerContainer")
    @Override
    public void consumeEmailMsg(KillSuccessUserInfo info) {
        try {
            log.info("秒杀异步邮件通知-接收消息:{}", info);

            //TODO:真正的发送邮件....
            final String content = String.format(env.getProperty("mail.kill.item.success.content"), info.getItemName(), info.getCode());
            MailDto dto = new MailDto(env.getProperty("mail.kill.item.success.subject"), content, new String[]{info.getEmail()});
            mailService.sendHtmlMail(dto);

        } catch (Exception e) {
            log.error("秒杀异步邮件通知-接收消息-发生异常：", e.fillInStackTrace());
        }
    }

    @RabbitListener(queues = {"${mq.kill.item.success.kill.dead.real.queue}"}, containerFactory = "singleListenerContainer")
    @Override
    public void consumeExpireOrder(KillSuccessUserInfo info) {
        try {
            log.info("用户秒杀成功后超时未支付-监听者-接收消息:{}", info);

            if (info != null) {
                ItemKillSuccess entity = itemKillSuccessMapper.selectByPrimaryKey(info.getCode());
                if (entity != null && entity.getStatus().intValue() == 0) {
                    itemKillSuccessMapper.expireOrder(info.getCode());
                }
            }
        } catch (Exception e) {
            log.error("用户秒杀成功后超时未支付-监听者-发生异常：", e.fillInStackTrace());
        }
    }
}
