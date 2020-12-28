package com.cqwu.jwy.mulberrydoc.consumer.controller.auth;

import com.cqwu.jwy.mulberrydoc.common.constant.CommonError;
import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.pojo.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.utils.RemoteService;
import com.cqwu.jwy.mulberrydoc.consumer.configure.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Objects;

@RestController
public class AuthController
{
    @Autowired
    private ConsumerConfig consumerConfig;
    @Autowired
    private RestTemplate restTemplate;

    private RemoteService remote;

    @PostConstruct
    public void init()
    {
        remote = new RemoteService(restTemplate);
    }

    @GetMapping("/auth/ping")
    public Object ping()
    {
        HttpResponse response = remote.call(ServiceConst.AUTH_SERVICE, "ping", HttpResponse.class);
        if (Objects.nonNull(response))
        {
            return HttpSerializer.success()
                    .status(HttpSerializer.STATUS_OK)
                    .msg(response)
                    .data(consumerConfig);
        }
        return HttpSerializer.failure()
                .status(HttpSerializer.INTERNAL_SERVER_ERROR)
                .msg(CommonError.INTERNAL_ERROR)
                .data(consumerConfig);
    }
}
