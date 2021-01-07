package com.cqwu.jwy.mulberrydoc.auth.api;

import com.cqwu.jwy.mulberrydoc.auth.configure.Instance;
import com.cqwu.jwy.mulberrydoc.auth.constant.AuthConstant;
import com.cqwu.jwy.mulberrydoc.auth.constant.AuthError;
import com.cqwu.jwy.mulberrydoc.auth.pojo.User;
import com.cqwu.jwy.mulberrydoc.auth.service.AuthService;
import com.cqwu.jwy.mulberrydoc.auth.service.UserService;
import com.cqwu.jwy.mulberrydoc.auth.util.IdUtil;
import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.JsonResponse;
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
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Autowired
    private IdUtil idUtil;

    /**
     * 返回程序运行状态
     *
     * @return HttpResponse
     */
    @GetMapping("ping")
    public HttpResponse ping()
    {
        LOG.info("【检查运行状态】PING");
        return HttpSerializer.success(instance.getInstanceId())
                .msg(ServiceConst.PONG);
    }

    /**
     * 用户注册
     *
     * @param obj 注册信息
     * @return HttpResponse
     */
    @PostMapping("register")
    public HttpResponse register(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【用户注册】注册信息：{}", obj);
        User info = PojoGenerator.generate(obj, User.class);
        // 转换用户注册信息失败
        if (Objects.isNull(info))
        {
            LOG.warn("【用户注册】转换用户注册信息失败");
            return HttpSerializer.incompleteParamsFailed(instance.getInstanceId());
        }
        // 校验用户注册信息
        Map<String, List<String>> errorInfo = Validator.verify(info, User.class);
        if (!errorInfo.isEmpty())
        {
            LOG.warn("【用户注册】校验字段不通过");
            return HttpSerializer.invalidParamsFailed(errorInfo, instance.getInstanceId());
        }
        User user;
        try
        {
            user = userService.createUser(info);
        }
        catch (WebException e)
        {
            LOG.error("【用户注册】注册失败，{}，error:", e.getErrorMsg().getMessage(), e);
            return HttpSerializer.failure(instance.getInstanceId(), HttpSerializer.STATUS_VALID_FAILED)
                    .msg(e);
        }
        catch (Throwable e)
        {
            LOG.error("【用户注册】注册失败，出现内部错误，error：", e);
            return HttpSerializer.internalError(instance.getInstanceId(), e);
        }
        LOG.info("【用户注册】用户注册成功，user：{}", user);
        return HttpSerializer.success(instance.getInstanceId())
                .msg(AuthConstant.REGISTER_SUCCESS);
    }

    /**
     * 用户登录
     *
     * @param obj 登录信息
     * @return JsonResponse
     */
    @PostMapping("login")
    public JsonResponse login(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【用户登录】登录信息：{}", obj);
        JsonResponse response = new JsonResponse();
        User info = PojoGenerator.generate(obj, User.class);
        // 转换用户注册信息失败
        if (Objects.isNull(info) || StringUtils.isEmpty(info.getUsername()) || StringUtils.isEmpty(info.getPassword()))
        {
            LOG.warn("【用户登录】转换用户登录信息失败，登录信息:{}", info);
            response.data(HttpSerializer.HTTP_RESPONSE_KEY, HttpSerializer.incompleteParamsFailed(instance.getInstanceId()));
        }
        else
        {
            // 用户登录
            Cookie cookie = authService.login(info);
            // 登录成功
            if (Objects.nonNull(cookie))
            {
                LOG.info("【用户登录】用户{} 登录成功", info.getUsername());
                response.data(HttpSerializer.COOKIES_KEY, Collections.singletonList(cookie));
                response.data(HttpSerializer.HTTP_RESPONSE_KEY,
                              HttpSerializer
                                      .success(instance.getInstanceId())
                                      .msg(AuthConstant.LOGIN_SUCCESS));
            }
            // 登录失败
            else
            {
                LOG.info("【用户登录】用户{} 登录成功", info.getUsername());
                response.data(HttpSerializer.HTTP_RESPONSE_KEY,
                              HttpSerializer
                                      .failure(instance.getInstanceId(), HttpSerializer.STATUS_BAD_REQUEST)
                                      .msg(AuthError.LOGIN_FAILED));
            }
        }
        return response;
    }

    /**
     * 判断用户是否已经登录
     *
     * @param sessionValue Session Value
     * @return HttpResponse
     */
    @PostMapping("isLogin")
    public HttpResponse isLogin(@RequestBody String sessionValue)
    {
        LOG.info("【检查用户登录状态】SessionValue:{}", sessionValue);
        if (StringUtils.isEmpty(sessionValue) || Objects.isNull(idUtil.getUserIdBySessionValue(sessionValue)))
        {
            LOG.warn("【检查用户登录状态】未登录");
            return HttpSerializer
                    .failure(instance.getInstanceId(), HttpSerializer.STATUS_FORBIDDEN_FAILED)
                    .msg(AuthError.VERIFICATION_FAILED);
        }
        LOG.info("【检查用户登录状态】已登录");
        return HttpSerializer.success(instance.getInstanceId());
    }

    /**
     * 查询用户信息
     *
     * @param sessionValue SessionValue
     * @return HttpResponse
     */
    @PostMapping("profile")
    public HttpResponse profile(@RequestBody @NotNull String sessionValue)
    {
        LOG.info("【查询用户信息】SessionValue:{}", sessionValue);
        String userId = idUtil.getUserIdBySessionValue(sessionValue);
        LOG.info("【查询用户信息】用户ID:{}", userId);
        User user = userService.queryUserById(userId);
        LOG.info("【查询用户信息】用户信息:{}", user);
        return HttpSerializer.success(instance.getInstanceId())
                .data(user);
    }
}
