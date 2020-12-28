package com.cqwu.jwy.mulberrydoc.auth.api;

import com.cqwu.jwy.mulberrydoc.auth.configure.AuthConfig;
import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.pojo.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApi
{
    @Autowired
    AuthConfig authConfig;

    @GetMapping("ping")
    public HttpResponse ping()
    {
        return HttpSerializer.success()
                .status(HttpSerializer.STATUS_OK)
                .msg(ServiceConst.PONG)
                .data(authConfig);
    }
}
