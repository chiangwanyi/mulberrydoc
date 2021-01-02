package com.cqwu.jwy.mulberrydoc.consumer.controller.auth;

import com.cqwu.jwy.mulberrydoc.common.constant.CommonError;
import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
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
        // 校验用户数据是否为 NULL
        if (Objects.isNull(obj))
        {
            return HttpSerializer.failure()
                    .status(HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(CommonError.INCOMPLETE_PARAMETERS)
                    .instances(instance.getInstanceId());
        }
        HttpResponse res = remote.post(ServiceConst.AUTH_SERVICE, "register", obj);
        return ResponseUtil.response(res, instance);
    }
}
