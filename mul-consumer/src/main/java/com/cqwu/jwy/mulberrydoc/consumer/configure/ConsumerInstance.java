package com.cqwu.jwy.mulberrydoc.consumer.configure;

import com.cqwu.jwy.mulberrydoc.common.configure.ServerInstance;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "eureka.instance")
@Component
public class ConsumerInstance extends ServerInstance
{
}
