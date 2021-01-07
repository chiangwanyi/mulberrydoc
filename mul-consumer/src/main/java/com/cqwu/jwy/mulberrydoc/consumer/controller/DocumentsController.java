package com.cqwu.jwy.mulberrydoc.consumer.controller;

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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
public class DocumentsController
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentsController.class);
    @Autowired
    private Instance instance;
    @Autowired
    private RestTemplate restTemplate;
    private RemoteConnector remote;
    @Autowired
    private AuthController authController;

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
    @GetMapping("/documents/ping")
    public Object ping()
    {
        LOG.info("【DocumentsController】测试服务状态");
        HttpResponse res = remote.get(ServiceConst.DOCUMENTS_SERVICE, "ping");
        LOG.info("【DocumentsController】服务状态：{}", res);
        return ResponseUtil.response(res, instance);
    }

    @PostMapping("/documents/createDocuments")
    public Object createDocuments(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【DocumentsController】创建文档空间");
        // 检查参数
        if (Objects.isNull(obj) || obj.isEmpty())
        {
            LOG.warn("【DocumentsController】参数格式错误");
            return HttpSerializer.invalidParamsFailed(null, instance.getInstanceId());
        }
        String uid = (String) obj.get("uid");
        if (StringUtils.isEmpty(uid))
        {
            LOG.warn("【DocumentsController】参数不完整");
            return HttpSerializer.incompleteParamsFailed(instance.getInstanceId());
        }
        // 检查用户是否存在
        HttpResponse res = remote.post(ServiceConst.AUTH_SERVICE, "isExisted", uid);
        // 用户存在
        if (res.getStatus() == HttpSerializer.STATUS_OK)
        {
            res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "createDocuments", uid);
            return ResponseUtil.response(res, instance);
        }
        HttpResponse response = HttpSerializer.operationForbiddenFailed(instance.getInstanceId());
        return response.instances(res.getInstances());
    }
}
