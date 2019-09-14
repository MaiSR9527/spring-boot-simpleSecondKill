package com.msr.kill.service;

/**
 * @Description: rabbitmq发送服务
 * @Author: maishuren
 * @Date: 2019/9/14 10:34
 */

public interface RabbitSenderService {

    /**
     * 秒杀商品成功异步发送消息
     * @param orderNum 订单编号
     */
    void sendKillSuccessEmailMsg(String orderNum);

    /**
     * 秒杀商品成功后生成抢购订单-发送消息进入死信队列，等待着一定时间失效超时为支付的订单
     * @param orderNum 订单编号
     */
     void sendKillSuccessOrderExpireMsg(final String orderNum);
}
