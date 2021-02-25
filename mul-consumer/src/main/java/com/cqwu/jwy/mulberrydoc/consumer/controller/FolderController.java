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

@RestController
public class FolderController
{
    private static final Logger LOG = LoggerFactory.getLogger(FolderController.class);
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
     * @param obj     参数（Folder对象）
     * @param request HttpServletRequest
     * @return 结果
     */
    @RequireLogin
    @PostMapping("/folder")
    public Object createFolder(@RequestBody Map<String, Object> obj, HttpServletRequest request)
    {
        LOG.info("【FolderController】创建文件夹");
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        LOG.info("【FolderController】用户ID:{}", userId);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "createFolder", obj);
        return ResponseUtil.response(res, instance);
    }

    /**
     * 修改文件夹
     *
     * @param body    参数
     * @param request HttpServletRequest
     * @return 结果
     */
    @RequireLogin
    @PatchMapping("/folder")
    public Object updateFolder(@RequestBody Map<String, Object> body, HttpServletRequest request)
    {
        LOG.info("【FolderController】修改文件夹");
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        LOG.info("【FolderController】用户ID:{}", userId);
        Map<String, Object> obj = new HashMap<>();
        obj.put("uid", userId);
        obj.put("folder", body.get("folder"));
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "updateFolder", obj);
        return ResponseUtil.response(res, instance);
    }

    /**
     * 移除文件夹
     *
     * @param body    参数
     * @param request HttpServletRequest
     * @return 结果
     */
    @RequireLogin
    @DeleteMapping("/folder")
    public Object removeFolder(@RequestBody Map<String, Object> body, HttpServletRequest request)
    {
        LOG.info("【FolderController】移除文件夹");
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        LOG.info("【FolderController】用户ID:{}", userId);
        Map<String, Object> obj = new HashMap<>();
        obj.put("uid", userId);
        obj.put("hash", body.get("hash"));
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "removeFolder", obj);
        return ResponseUtil.response(res, instance);
    }
}
