package com.cqwu.jwy.mulberrydoc.auth.api;

import com.cqwu.jwy.mulberrydoc.auth.AuthServer;
import com.cqwu.jwy.mulberrydoc.auth.configure.Instance;
import com.cqwu.jwy.mulberrydoc.auth.constant.AuthConstant;
import com.cqwu.jwy.mulberrydoc.auth.constant.AuthError;
import com.cqwu.jwy.mulberrydoc.auth.pojo.User;
import com.cqwu.jwy.mulberrydoc.auth.serializer.UserSerializer;
import com.cqwu.jwy.mulberrydoc.auth.service.AuthService;
import com.cqwu.jwy.mulberrydoc.auth.service.UserService;
import com.cqwu.jwy.mulberrydoc.common.constant.CommonError;
import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.util.PojoGenerator;
import com.cqwu.jwy.mulberrydoc.common.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import java.util.*;

@RestController
public class AuthApi
{
    /** LOG */
    private static final Logger LOG = LoggerFactory.getLogger(AuthApi.class);
    @Autowired
    private Instance instance;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    /**
     * 返回程序运行状态
     *
     * @return HttpResponse
     */
    @GetMapping("ping")
    public HttpResponse ping()
    {
        return HttpSerializer.success(instance.getInstanceId())
                .msg(ServiceConst.PONG);
    }

    /**
     * 用户注册
     *
     * @param obj 注册信息
     * @return 结果
     */
    @PostMapping("register")
    public HttpResponse register(@RequestBody Map<String, Object> obj)
    {
        User info = PojoGenerator.generate(obj, User.class);
        // 转换用户注册信息失败
        if (Objects.isNull(info))
        {
            LOG.warn("转换用户注册信息失败");
            return HttpSerializer.incompleteParamsFailed(instance.getInstanceId());
        }
        // 校验用户注册信息
        Map<String, List<String>> errorInfo = Validator.verify(info, User.class);
        if (!errorInfo.isEmpty())
        {
            return HttpSerializer.invalidParamsFailed(errorInfo, instance.getInstanceId());
        }
        User user;
        try
        {
            user = userService.createUser(info);
        }
        catch (WebException e)
        {
            LOG.error("注册失败，{}，error:", e.getErrorMsg().getMessage(), e);
            return HttpSerializer.failure(instance.getInstanceId(), HttpSerializer.STATUS_VALID_FAILED)
                    .msg(e);
        }
        catch (Throwable e)
        {
            LOG.error("注册失败，出现内部错误，error：", e);
            return HttpSerializer.internalError(instance.getInstanceId(), e);
        }
        LOG.info("用户注册成功，user：{}", user);
        return HttpSerializer.success(instance.getInstanceId())
                .msg(AuthConstant.REGISTER_SUCCESS);
    }

    /**
     * 用户登录
     *
     * @param obj 登录信息
     * @return 结果
     */
    @PostMapping("login")
    public Map login(@RequestBody Map<String, Object> obj)
    {
        Map<String, Object> res = new HashMap<>();
        User info = PojoGenerator.generate(obj, User.class);
        // 转换用户注册信息失败
        if (Objects.isNull(info) || StringUtils.isEmpty(info.getUsername()) || StringUtils.isEmpty(info.getPassword()))
        {
            LOG.warn("转换用户登录信息失败，info:{}", info);
            res.put(HttpSerializer.HTTP_RESPONSE_KEY, HttpSerializer.incompleteParamsFailed(instance.getInstanceId()));
        }
        else
        {
            // 用户登录
            Cookie cookie = authService.login(info);
            // 登录成功
            if (Objects.nonNull(cookie))
            {
                res.put(HttpSerializer.COOKIES_KEY, Collections.singletonList(cookie));
                res.put(HttpSerializer.HTTP_RESPONSE_KEY, HttpSerializer
                        .success(instance.getInstanceId())
                        .msg(AuthConstant.LOGIN_SUCCESS));
            }
            // 登录失败
            else
            {
                res.put(HttpSerializer.HTTP_RESPONSE_KEY, HttpSerializer
                        .failure(instance.getInstanceId(), HttpSerializer.STATUS_BAD_REQUEST)
                        .msg(AuthError.LOGIN_FAILED));
            }
        }
        return res;
    }
}
