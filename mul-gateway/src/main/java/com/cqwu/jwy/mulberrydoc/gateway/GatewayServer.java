package com.cqwu.jwy.mulberrydoc.gateway;

import com.cqwu.jwy.mulberrydoc.gateway.ribbon.MyRoundRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
//@RibbonClient(name = "MULBERRYDOC-MUL-GATEWAY", configuration = MyRoundRule.class)
public class GatewayServer
{
    public static void main(String[] args)
    {
        SpringApplication.run(GatewayServer.class, args);
    }
}
