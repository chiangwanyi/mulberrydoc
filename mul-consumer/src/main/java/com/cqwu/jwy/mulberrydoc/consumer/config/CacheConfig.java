package com.cqwu.jwy.mulberrydoc.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 权限 Cache 参数
 */
@ConfigurationProperties(prefix = "auth.cache")
@Component
public class CacheConfig
{
    /** Session Key */
    private String sessionName;
    /** 下一个 UID Key */
    private String nextUidName;
    /** 初始 UID */
    private String defaultUid;

    public String getSessionName()
    {
        return sessionName;
    }

    public void setSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }

    public String getNextUidName()
    {
        return nextUidName;
    }

    public void setNextUidName(String nextUidName)
    {
        this.nextUidName = nextUidName;
    }

    public String getDefaultUid()
    {
        return defaultUid;
    }

    public void setDefaultUid(String defaultUid)
    {
        this.defaultUid = defaultUid;
    }
}
