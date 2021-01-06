package com.cqwu.jwy.mulberrydoc.consumer.configure;

import com.cqwu.jwy.mulberrydoc.common.util.RemoteConnector;
import com.cqwu.jwy.mulberrydoc.consumer.config.SessionConfig;
import com.cqwu.jwy.mulberrydoc.consumer.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.PostConstruct;

@Configuration
public class MyWebMvcConfigurator extends WebMvcConfigurationSupport
{
    @Autowired
    private SessionConfig sessionConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Instance instance;
    private RemoteConnector remote;

    @PostConstruct
    public void init()
    {
        remote = new RemoteConnector(restTemplate);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins("*")
                .allowedHeaders("*");
        super.addCorsMappings(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        InterceptorRegistration interceptorRegistration = registry
                .addInterceptor(new AuthInterceptor(sessionConfig, remote, instance));
        interceptorRegistration.addPathPatterns("/**");
    }
}
