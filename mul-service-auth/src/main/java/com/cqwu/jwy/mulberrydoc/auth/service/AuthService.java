package com.cqwu.jwy.mulberrydoc.auth.service;

import com.cqwu.jwy.mulberrydoc.auth.cache.Redis;
import com.cqwu.jwy.mulberrydoc.auth.config.CacheConfig;
import com.cqwu.jwy.mulberrydoc.auth.config.SessionConfig;
import com.cqwu.jwy.mulberrydoc.auth.dao.UserDao;
import com.cqwu.jwy.mulberrydoc.auth.pojo.User;
import com.cqwu.jwy.mulberrydoc.auth.util.SecretUtil;
import com.cqwu.jwy.mulberrydoc.common.util.CodecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService
{
    /** LOG */
    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private SessionConfig sessionConfig;
    @Autowired
    private CacheConfig cacheConfig;
    @Autowired
    private SecretUtil secretUtil;
    @Autowired
    private Redis redis;

    /**
     * 登录
     *
     * @param info 用户登录信息
     * @return Cookie
     */
    public Cookie login(User info)
    {
        User user = userDao.queryUserByUsername(info.getUsername());
        // 判断用户是否存在，是否已经移除
        if (Objects.nonNull(user) && Objects.isNull(user.getDeletedAt()))
        {
            String pwd = secretUtil.generatePassword(info.getPassword(), info.getUsername());
            // 校验密码
            if (user.getPassword().equals(pwd))
            {
                String sessionValue = SecretUtil.generateSessionValue(user.getUsername());
                Cookie cookie = new Cookie(sessionConfig.getSessionName(), sessionValue);
                cookie.setMaxAge(sessionConfig.getMaxAge());
                cookie.setPath(sessionConfig.getPath());
                cookie.setHttpOnly(true);

                // 存储 用户 Session 到 Cache
                redis.redisTemplate.opsForValue().set(cacheConfig.getSessionName() + ":" + CodecUtil.sha1(sessionValue.getBytes()), user.getId(), sessionConfig.getMaxAge(), TimeUnit.SECONDS);
                return cookie;
            }
        }
        return null;
    }
}
