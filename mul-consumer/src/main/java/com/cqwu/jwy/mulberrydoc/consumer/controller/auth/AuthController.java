package com.cqwu.jwy.mulberrydoc.consumer.controller.auth;

import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.serializer.CookieSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.util.RemoteConnector;
import com.cqwu.jwy.mulberrydoc.consumer.configure.Instance;
import com.cqwu.jwy.mulberrydoc.consumer.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@RestController
public class AuthController
{
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private Instance instance;
    @Autowired
    private RestTemplate restTemplate;
    private RemoteConnector remote;

    @PostConstruct
    public void init()
    {
        remote = new RemoteConnector(restTemplate);
    }

    /**
     * 测试服务状态
     *
     * @return 结果
     */
    @GetMapping("/auth/ping")
    public Object ping()
    {
        HttpResponse res = remote.get(ServiceConst.AUTH_SERVICE, "ping");
        return ResponseUtil.response(res, instance);
    }

    /**
     * 用户注册
     *
     * @param obj 注册信息
     * @return 结果
     */
    @PostMapping("/auth/register")
    public Object register(@RequestBody Object obj)
    {
        HttpResponse res = remote.post(ServiceConst.AUTH_SERVICE, "register", obj);
        return ResponseUtil.response(res, instance);
    }

    /**
     * 用户登录
     *
     * @param obj 登录信息
     * @return 结果
     */
    @PostMapping("/auth/login")
    public Object login(@RequestBody Object obj, HttpServletResponse servletResponse)
    {
        Map res = remote.post(ServiceConst.AUTH_SERVICE, "login", obj, Map.class);
        if (Objects.isNull(res) || res.isEmpty())
        {
            LOG.warn("登录后无返回结果");
            return HttpSerializer.internalError(instance.getInstanceId(), null);
        }
        try
        {
            HttpResponse response = HttpSerializer.convert(res.get(HttpSerializer.HTTP_RESPONSE_KEY));
            // 登录成功
            if (Objects.nonNull(response) && Objects.equals(response.getStatus(), HttpSerializer.STATUS_OK))
            {
                // 保存登录信息
                Cookie[] cookies = CookieSerializer.convert(res.get(HttpSerializer.COOKIES_KEY));
                if (Objects.nonNull(cookies) && cookies.length != 0)
                {
                    for (Cookie cookie : cookies)
                    {
                        servletResponse.addCookie(cookie);
                    }
                }
                // 保存失败
                else
                {
                    LOG.error("保存登录信息失败");
                    return HttpSerializer.internalError(instance.getInstanceId(), null);
                }
            }
            return ResponseUtil.response(response, instance);
        }
        catch (Exception e)
        {
            LOG.error("登录失败，error:", e);
            return HttpSerializer.internalError(instance.getInstanceId(), e);
        }
    }
}
