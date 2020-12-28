package com.cqwu.jwy.mulberrydoc.auth.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "eureka.instance")
@Component
public class AuthConfig
{
    private String instanceId;

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }
}
