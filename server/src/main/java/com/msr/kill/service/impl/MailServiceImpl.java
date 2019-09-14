package com.msr.kill.service.impl;

import com.msr.kill.dto.MailDto;
import com.msr.kill.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 14:52
 */
@Service
@EnableAsync
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    private final Environment env;

    public MailServiceImpl(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    @Async
    @Override
    public void sendSimpleEmail(MailDto dto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(env.getProperty("mail.send.from"));
            message.setTo(dto.getTos());
            message.setSubject(dto.getSubject());
            message.setText(dto.getContent());
            mailSender.send(message);

            log.info("发送简单文本文件-发送成功!");
        } catch (Exception e) {
            log.error("发送简单文本文件-发生异常： ", e.fillInStackTrace());
        }
    }

    @Async
    @Override
    public void sendHtmlMail(MailDto dto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");
            messageHelper.setFrom(env.getProperty("mail.send.from"));
            messageHelper.setTo(dto.getTos());
            messageHelper.setSubject(dto.getSubject());
            messageHelper.setText(dto.getContent(), true);

            mailSender.send(message);
            log.info("发送花哨邮件-发送成功!");
        } catch (Exception e) {
            log.error("发送花哨邮件-发生异常： ", e.fillInStackTrace());
        }
    }
}
