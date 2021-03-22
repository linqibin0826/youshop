package com.youmei.auth.controller;

import com.youmei.auth.config.JwtProperties;
import com.youmei.auth.pojo.UserInfo;
import com.youmei.auth.service.AuthService;
import com.youmei.auth.util.JwtUtils;
import com.youmei.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/accredit")
    public ResponseEntity<Void> accredit(@RequestParam("username") String username, @RequestParam("password") String password,
                                         HttpServletRequest request, HttpServletResponse response) {
        String token = this.authService.accredit(username, password);
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token,
                jwtProperties.getExpire() * 60, null, true);
        return ResponseEntity.ok(null);
    }

    /**
     * 验证用户信息
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("youshop_token")String token,  HttpServletRequest request, HttpServletResponse response){
        try {
            // 从token中解析token信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());

            // 刷新token
            JwtUtils.generateToken(userInfo, this.jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            // 刷新cookie
            CookieUtils.setCookie(request, response, this.jwtProperties.getCookieName(), token, this.jwtProperties.getExpire() * 60);

            // 解析成功返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 出现异常则，响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
