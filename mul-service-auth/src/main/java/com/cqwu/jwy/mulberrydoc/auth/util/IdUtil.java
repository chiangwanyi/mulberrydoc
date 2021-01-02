package com.cqwu.jwy.mulberrydoc.auth.util;

import com.cqwu.jwy.mulberrydoc.auth.cache.Redis;
import com.cqwu.jwy.mulberrydoc.auth.config.CacheConfig;
import com.cqwu.jwy.mulberrydoc.common.util.CodecUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
public class IdUtil
{
    @Autowired
    private Redis redis;
    @Autowired
    private CacheConfig cacheConfig;

    /**
     * 初始化
     */
    @PostConstruct
    public void init()
    {
        Boolean res = redis.redisTemplate.hasKey(cacheConfig.getNextUidName());
        if (Objects.isNull(res) || !res)
        {
            redis.redisTemplate.opsForValue().set(cacheConfig.getNextUidName(), cacheConfig.getDefaultUid());
        }
    }

    /**
     * 获取下一个 UserId
     *
     * @return UserId
     */
    public String nextUserId()
    {
        return String.valueOf(redis.redisTemplate.opsForValue().increment(cacheConfig.getNextUidName(), 1L));
    }

    /**
     * 根据 Session Value 获取 UserId
     *
     * @param sessionValue Session Value
     * @return UserId
     */
    public String getUserId(String sessionValue)
    {
        if (StringUtils.isEmpty(sessionValue))
        {
            return null;
        }
        return redis.redisTemplate.opsForValue().get(cacheConfig.getSessionName() + ":" + CodecUtil.sha1(sessionValue.getBytes()));
    }
}
