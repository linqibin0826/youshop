package com.youmei.carts.config;

import com.youmei.auth.util.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "youshop.jwt")
public class JwtProperties {

    private String publicKeyPath;

    private PublicKey publicKey;

    private String cookieName;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtProperties.class);

    // 在构造方法执行后运行
    @PostConstruct
    public void init() {

        try {
            PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
            this.publicKey = publicKey;

        } catch (Exception e) {
            LOGGER.error("初始化公钥失败!");
            throw new RuntimeException();
        }

    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}
