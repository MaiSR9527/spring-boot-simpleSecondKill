package com.msr.kill.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
@Data
public class ItemKillSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 秒杀成功生成的订单编号
     */
    private String code;
    /**
     * 商品id
     */
    private Integer itemId;
    /**
     * 秒杀id
     */
    private Integer killId;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 秒杀结果: -1无效  0成功(未付款)  1已付款  2已取消
     */
    private Byte status;
    /**
     * 创建时间
     */
    private Date createTime;

    private Integer diffTime;
}