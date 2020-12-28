package com.cqwu.jwy.mulberrydoc.auth.ribbon.rule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRoundRule
{
    @Bean
    public IRule myRule() {
        return new RoundRobinRule();
    }
}
