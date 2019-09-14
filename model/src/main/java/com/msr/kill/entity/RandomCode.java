package com.msr.kill.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
@Data
public class RandomCode implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String code;
}