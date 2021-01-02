package com.cqwu.jwy.mulberrydoc.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 权限 Session 参数
 */
@ConfigurationProperties(prefix = "auth.session")
@Component
public class SessionConfig
{
    /** 路径 */
    private String path;
    /** Cookie Name */
    private String sessionName;
    /** 最大存活时间 */
    private Integer maxAge;

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public void setSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }

    public Integer getMaxAge()
    {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge)
    {
        this.maxAge = maxAge;
    }
}
