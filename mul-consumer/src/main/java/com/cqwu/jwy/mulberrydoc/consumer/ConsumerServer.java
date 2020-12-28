package com.cqwu.jwy.mulberrydoc.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class ConsumerServer
{
    public static void main(String[] args)
    {
        SpringApplication.run(ConsumerServer.class, args);
    }
}
