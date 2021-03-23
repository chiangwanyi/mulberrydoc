package com.cqwu.jwy.mulberrydoc.consumer.controller;

import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class FileController
{
    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);
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
     * 新建文件夹
     *
     * @param obj     参数（File对象）
     * @param request HttpServletRequest
     * @return 结果
     */
    @RequireLogin
    @PostMapping("/file")
    public Object createFile(@RequestBody Map<String, Object> obj, HttpServletRequest request)
    {
        LOG.info("【FileController】创建文件夹");
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        LOG.info("【FileController】用户ID:{}", userId);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "createFile", obj);
        return ResponseUtil.response(res, instance);
    }

    @GetMapping("/file/{fileHash}")
    public Object queryFile(@PathVariable String fileHash)
    {
        LOG.info("【FileController】查询文件");
        Map<String, Object> obj = new HashMap<>();
        obj.put("hash", fileHash);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "queryFile", obj);
        return ResponseUtil.response(res, instance);
    }

    @RequireLogin
    @PostMapping("/file/attr")
    public Object updateFileAttr(@RequestBody Map<String, Object> obj, HttpServletRequest request)
    {
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "updateFileAttr", obj);
        return ResponseUtil.response(res, instance);
    }

    @RequireLogin
    @PostMapping("/file/name")
    public Object updateFileName(@RequestBody Map<String, Object> obj, HttpServletRequest request)
    {
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "updateFileName", obj);
        return ResponseUtil.response(res, instance);
    }
}
