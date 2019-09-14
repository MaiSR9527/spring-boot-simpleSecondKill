package com.msr.kill.service;

import com.msr.kill.dto.MailDto;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 14:52
 */
public interface MailService {

    /**
     * 发送简单邮件
     * @param dto 邮件信息
     */
    public void sendSimpleEmail(final MailDto dto);

    /**
     * 发送简单的花哨邮件
     * @param dto 邮件信息
     */
    public void sendHtmlMail(final MailDto dto);
}
