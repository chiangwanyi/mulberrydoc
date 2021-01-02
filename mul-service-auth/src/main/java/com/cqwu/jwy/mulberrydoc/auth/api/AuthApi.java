package com.cqwu.jwy.mulberrydoc.auth.api;

import com.cqwu.jwy.mulberrydoc.auth.configure.Instance;
import com.cqwu.jwy.mulberrydoc.auth.constant.AuthConstant;
import com.cqwu.jwy.mulberrydoc.auth.pojo.User;
import com.cqwu.jwy.mulberrydoc.auth.serializer.UserSerializer;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class AuthApi
{
    /** LOG */
    private static final Logger LOG = LoggerFactory.getLogger(AuthApi.class);
    /** 服务配置信息 */
    @Autowired
    private Instance instance;
    /** UserService */
    @Autowired
    private UserService userService;

    /**
     * 返回程序运行状态
     *
     * @return HttpResponse
     */
    @GetMapping("ping")
    public HttpResponse ping()
    {
        return HttpSerializer.success(instance.getInstanceId())
                .status(HttpSerializer.STATUS_OK)
                .msg(ServiceConst.PONG);
    }

    /**
     * 用户注册
     *
     * @return HttpResponse 注册结果
     */
    @PostMapping("register")
    public HttpResponse register(@RequestBody Map<String, Object> obj)
    {
        User info = PojoGenerator.generate(obj, User.class);
        // 转换用户注册信息失败
        if (Objects.isNull(info))
        {
            LOG.warn("转换用户注册信息失败");
            return HttpSerializer.failure()
                    .status(HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(CommonError.INVALID_PARAMETERS)
                    .instances(instance.getInstanceId());
        }
        // 校验用户注册信息
        Map<String, List<String>> errorInfo = Validator.verify(info, User.class);
        if (!errorInfo.isEmpty())
        {
            return HttpSerializer.paramsVerifyFailed(errorInfo, instance.getInstanceId());
        }
        User user;
        try
        {
            user = userService.createUser(info);
        }
        catch (WebException e)
        {
            LOG.warn("注册失败，{}", e.getErrorMsg().getMessage());
            return HttpSerializer.failure()
                    .status(HttpSerializer.STATUS_VALID_FAILED)
                    .msg(e);
        }
        catch (Throwable e)
        {
            LOG.error("注册失败，出现内部错误，error：", e);
            return HttpSerializer.failure()
                    .status(HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(CommonError.INTERNAL_ERROR)
                    .data(e);
        }
        LOG.info("用户注册成功，user：{}", user);
        return HttpSerializer.success()
                .status(HttpSerializer.STATUS_OK)
                .msg(AuthConstant.REGISTER_SUCCESS)
                .data(UserSerializer.commonSerialData(user));
    }
}
