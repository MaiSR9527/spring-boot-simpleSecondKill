package com.msr.kill.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 09:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 接收人
     */
    private String[] tos;
}
