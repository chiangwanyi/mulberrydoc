package com.cqwu.jwy.mulberrydoc.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 权限 Secret 参数
 */
@ConfigurationProperties(prefix = "auth.secret")
@Component
public class SecretConfig
{
    /** 用户登录密码 盐 */
    private String passwordSalt;

    public String getPasswordSalt()
    {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt)
    {
        this.passwordSalt = passwordSalt;
    }
}
