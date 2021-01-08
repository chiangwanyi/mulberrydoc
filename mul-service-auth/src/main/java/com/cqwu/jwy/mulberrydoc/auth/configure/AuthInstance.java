package com.cqwu.jwy.mulberrydoc.auth.configure;

import com.cqwu.jwy.mulberrydoc.common.configure.ServerInstance;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "eureka.instance")
@Component
public class AuthInstance extends ServerInstance
{
}
