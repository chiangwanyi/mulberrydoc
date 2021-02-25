package com.cqwu.jwy.mulberrydoc.consumer.controller;

import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.util.CookieUtil;
import com.cqwu.jwy.mulberrydoc.common.util.RemoteConnector;
import com.cqwu.jwy.mulberrydoc.consumer.config.SessionConfig;
import com.cqwu.jwy.mulberrydoc.consumer.configure.ConsumerInstance;
import com.cqwu.jwy.mulberrydoc.consumer.interceptor.RequireLogin;
import com.cqwu.jwy.mulberrydoc.consumer.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class DocumentsController
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentsController.class);
    @Autowired
    private ConsumerInstance instance;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AuthController authController;
    @Autowired
    private SessionConfig sessionConfig;

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
    @GetMapping("/documents/ping")
    public Object ping()
    {
        LOG.info("【DocumentsController】测试服务状态");
        HttpResponse res = remote.get(ServiceConst.DOCUMENTS_SERVICE, "ping");
        LOG.info("【DocumentsController】服务状态：{}", res);
        return ResponseUtil.response(res, instance);
    }

    /**
     * 创建文档空间
     *
     * @param obj 参数（UserId）
     * @return 结果
     */
    @PostMapping("/documents")
    public Object createDocuments(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【DocumentsController】创建文档空间");
        // 检查参数
        if (Objects.isNull(obj) || obj.isEmpty())
        {
            LOG.warn("【DocumentsController】参数格式错误");
            return HttpSerializer.invalidParamsFailed(instance, null);
        }
        String uid = (String) obj.get("uid");
        if (StringUtils.isEmpty(uid))
        {
            LOG.warn("【DocumentsController】参数不完整");
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        // 检查用户是否存在
        HttpResponse res = remote.post(ServiceConst.AUTH_SERVICE, "isExisted", uid);
        // 用户存在
        if (res.getStatus() == HttpSerializer.STATUS_OK)
        {
            res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "createDocuments", uid);
            return ResponseUtil.response(res, instance);
        }
        HttpResponse response = HttpSerializer.operationForbiddenFailed(instance);
        return response.instances(res.getInstances());
    }

    /**
     * 查询父文件夹下的所有子文件夹
     *
     * @param parentHash 父文件夹 Hash
     * @param request    HttpServletRequest
     * @return 结果
     */
    @GetMapping("/documents/{parentHash}")
    public Object querySubfolder(@PathVariable String parentHash, HttpServletRequest request)
    {
        LOG.info("【DocumentsController】查询父文件夹下的所有子文件夹");
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        LOG.info("【DocumentsController】用户ID:{}", userId);
        Map<String, Object> obj = new HashMap<>();
        obj.put("uid", userId);
        obj.put("parentHash", parentHash);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "querySubfolder", obj);
        return ResponseUtil.response(res, instance);
    }
}
