package com.cqwu.jwy.mulberrydoc.consumer.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.cqwu.jwy.mulberrydoc.common.constant.CommonError;
import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.util.CookieUtil;
import com.cqwu.jwy.mulberrydoc.common.util.RemoteConnector;
import com.cqwu.jwy.mulberrydoc.consumer.config.SessionConfig;
import com.cqwu.jwy.mulberrydoc.consumer.configure.ConsumerInstance;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AuthInterceptor implements HandlerInterceptor
{
    private final SessionConfig sessionConfig;
    private final RemoteConnector remote;
    private final ConsumerInstance instance;

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
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("content-type", "application/json;charset=utf-8");

            String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
            // 存在 SessionValue
            if (!StringUtils.isEmpty(sessionValue))
            {
                HttpResponse res = remote.post(ServiceConst.AUTH_SERVICE, "isLogin", sessionValue);
                // 调用验证方法成功
                if (Objects.nonNull(res))
                {
                    // 验证成功
                    if (res.getStatus() == HttpSerializer.STATUS_OK)
                    {
                        return true;
                    }
                    // 验证失败
                    else
                    {
                        res.instances(instance.getInstanceId());
                        response.getOutputStream().write(JSONObject.toJSONString(res).getBytes(StandardCharsets.UTF_8));
                        return false;
                    }
                }
                // 调用验证方法失败，内部错误
                else
                {
                    response.getOutputStream()
                            .write(JSONObject.toJSONString(
                                    HttpSerializer
                                            .failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                                            .msg(CommonError.INTERNAL_ERROR))
                                           .getBytes(StandardCharsets.UTF_8));
                    return false;
                }
            }
            // 无 SessionValue，返回错误消息
            response.getOutputStream()
                    .write(JSONObject.toJSONString(
                            HttpSerializer
                                    .failure(instance, HttpSerializer.STATUS_FORBIDDEN_FAILED)
                                    .msg(CommonError.VERIFICATION_FAILED))
                                   .getBytes(StandardCharsets.UTF_8));
            return false;
        }
        return true;
    }

    public AuthInterceptor(SessionConfig sessionConfig, RemoteConnector remote, ConsumerInstance instance)
    {
        this.sessionConfig = sessionConfig;
        this.remote = remote;
        this.instance = instance;
    }
}
