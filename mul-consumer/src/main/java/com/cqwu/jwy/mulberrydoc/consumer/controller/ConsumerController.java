package com.cqwu.jwy.mulberrydoc.consumer.controller;

import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.consumer.configure.ConsumerInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController
{
    @Autowired
    private ConsumerInstance instance;

    /**
     * 检查服务状态
     *
     * @return HttpResponse
     */
    @GetMapping("/ping")
    public Object ping()
    {
        return HttpSerializer.success(instance).msg(ServiceConst.PONG);
    }
}
