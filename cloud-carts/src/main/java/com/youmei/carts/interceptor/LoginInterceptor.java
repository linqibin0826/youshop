package com.youmei.carts.interceptor;

import com.youmei.auth.pojo.UserInfo;
import com.youmei.auth.util.JwtUtils;
import com.youmei.carts.config.JwtProperties;
import com.youmei.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@EnableConfigurationProperties(JwtProperties.class)
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        // 解析token
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

        // 放入线程域
        THREAD_LOCAL.set(userInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        THREAD_LOCAL.remove();
    }

    public static UserInfo getUserInfo() {
        return THREAD_LOCAL.get();
    }
}
