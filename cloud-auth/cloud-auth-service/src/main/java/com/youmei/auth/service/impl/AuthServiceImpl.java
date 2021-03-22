package com.youmei.auth.service.impl;

import com.youmei.auth.client.UserClient;
import com.youmei.auth.config.JwtProperties;
import com.youmei.auth.pojo.UserInfo;
import com.youmei.auth.service.AuthService;
import com.youmei.auth.util.JwtUtils;
import com.youmei.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public String accredit(String username, String password) {
        // 查询用户是否存在
        User user = userClient.queryUser(username, password);
        if (user == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());

        // 生成token
        try {
            return JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), 30);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
