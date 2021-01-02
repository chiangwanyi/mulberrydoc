package com.cqwu.jwy.mulberrydoc.consumer.Interceptor;

import com.alibaba.fastjson.JSONObject;
import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.util.CookieUtil;
import com.cqwu.jwy.mulberrydoc.common.util.RemoteConnector;
import com.cqwu.jwy.mulberrydoc.common.validator.annotation.RequireLogin;
import com.cqwu.jwy.mulberrydoc.consumer.config.SessionConfig;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class AuthInterceptor implements HandlerInterceptor
{
    private final SessionConfig sessionConfig;
    private final RemoteConnector remote;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod))
        {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 如果有 RequireLogin 注解
        if (method.isAnnotationPresent(RequireLogin.class))
        {
            String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());

            Boolean res = remote.post(ServiceConst.AUTH_SERVICE, "isLogin", sessionValue, Boolean.class);
            if (res)
            {
                return true;
            }

            // 返回错误消息
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("content-type", "application/json;charset=utf-8");
            response.getOutputStream()
                    .write(JSONObject.toJSONString(HttpSerializer.failure()
                                                           .status(HttpSerializer.STATUS_FORBIDDEN_FAILED)
                                                           .msg(""))
                                   .getBytes(StandardCharsets.UTF_8));
            return false;
        }
        return true;
    }

    public AuthInterceptor(SessionConfig sessionConfig, RemoteConnector remote)
    {
        this.sessionConfig = sessionConfig;
        this.remote = remote;
    }
}
