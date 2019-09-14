package com.msr.kill.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 15:57
 */
@Controller
@Slf4j
public class UserController {

    private final Environment env;

    public UserController(Environment env) {
        this.env = env;
    }

    /**
     * 跳到登录页
     *
     * @return 跳转
     */
    @RequestMapping(value = {"/to/login", "/unauth"})
    public String toLogin() {
        return "login";
    }

    /**
     * 登录认证
     *
     * @param userName 用户名
     * @param password 用户密码
     * @param modelMap 模型
     * @return 返回页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String userName, @RequestParam String password, ModelMap modelMap) {
        String errorMsg = "";
        try {
            if (!SecurityUtils.getSubject().isAuthenticated()) {
                String newPsd = new Md5Hash(password, env.getProperty("shiro.encrypt.password.salt")).toString();
                UsernamePasswordToken token = new UsernamePasswordToken(userName, newPsd);
                SecurityUtils.getSubject().login(token);
            }
        } catch (UnknownAccountException | IncorrectCredentialsException | DisabledAccountException e) {
            errorMsg = e.getMessage();
            modelMap.addAttribute("userName", userName);
        } catch (Exception e) {
            errorMsg = "用户登录异常，请联系管理员!";
            e.printStackTrace();
        }
        if (StringUtils.isBlank(errorMsg)) {
            return "redirect:/index";
        } else {
            modelMap.addAttribute("errorMsg", errorMsg);
            return "login";
        }
    }

    /**
     * 退出登录
     *
     * @return 跳转至登录页面
     */
    @RequestMapping(value = "/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "login";
    }
}
