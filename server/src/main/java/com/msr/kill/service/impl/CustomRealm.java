package com.msr.kill.service.impl;

import com.msr.kill.entity.User;
import com.msr.kill.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 15:12
 */
@Service
@Slf4j
public class CustomRealm extends AuthorizingRealm {


    private static final Long SESSION_KEY_TIME_OUT = 3600_000L;

    @Autowired
    private UserMapper userMapper;



    /**
     * 授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 认证-登录
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String userName = token.getUsername();
        String password = String.valueOf(token.getPassword());
        log.info("当前登录的用户名={} 密码={} ", userName, password);

        User user = userMapper.selectByUserName(userName);
        if (user == null) {
            throw new UnknownAccountException("用户名不存在!");
        }
        if (!Objects.equals(1, user.getIsActive().intValue())) {
            throw new DisabledAccountException("当前用户已被禁用!");
        }
        if (!user.getPassword().equals(password)) {
            throw new IncorrectCredentialsException("用户名密码不匹配!");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUserName(), password, getName());
        setSession("uid", user.getId());
        return info;
    }

    /**
     * 将key与对应的value塞入shiro的session中-最终交给HttpSession进行管理(如果是分布式session配置，那么就是交给redis管理)
     *
     * @param key
     * @param value
     */
    private void setSession(String key, Object value) {
        Session session = SecurityUtils.getSubject().getSession();
        if (session != null) {
            session.setAttribute(key, value);
            session.setTimeout(SESSION_KEY_TIME_OUT);
        }
    }
}
