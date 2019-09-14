package com.msr.kill.service;

import com.msr.kill.dto.KillSuccessUserInfo;

/**
 * @Description: 消息消费者业务类接口
 * @Author: maishuren
 * @Date: 2019/9/14 10:36
 */
public interface RabbitReceiverService {

    /**
     * 秒杀异步邮件通知-接收消息
     * @param info 秒杀信息
     */
    public void consumeEmailMsg(KillSuccessUserInfo info);

    /**
     * 用户秒杀成功后超时未支付-监听者
     * @param info 秒杀商品信息
     */
    public void consumeExpireOrder(KillSuccessUserInfo info);
}

