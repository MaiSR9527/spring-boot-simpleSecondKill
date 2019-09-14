package com.msr.kill.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description: 基础controller
 * @Author: maishuren
 * @Date: 2019/9/6 14:38
 */
@Controller
@RequestMapping("base")
@Slf4j
public class BaseController {

    /**
     * 产生错误时，调用
     *
     * @return 跳转至错误页面
     */
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
