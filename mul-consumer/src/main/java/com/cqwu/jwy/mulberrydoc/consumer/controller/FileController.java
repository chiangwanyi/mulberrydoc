package com.cqwu.jwy.mulberrydoc.consumer.controller;

import com.alibaba.fastjson.JSONObject;
import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.util.CookieUtil;
import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;
import com.cqwu.jwy.mulberrydoc.common.util.FileUtil;
import com.cqwu.jwy.mulberrydoc.common.util.RemoteConnector;
import com.cqwu.jwy.mulberrydoc.consumer.config.SessionConfig;
import com.cqwu.jwy.mulberrydoc.consumer.configure.ConsumerInstance;
import com.cqwu.jwy.mulberrydoc.consumer.interceptor.RequireLogin;
import com.cqwu.jwy.mulberrydoc.consumer.util.ResponseUtil;
import com.netflix.discovery.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class FileController {
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
    public void init() {
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
    public Object createFile(@RequestBody Map<String, Object> obj, HttpServletRequest request) {
        LOG.info("【FileController】创建文件夹");
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        LOG.info("【FileController】用户ID:{}", userId);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "createFile", obj);
        return ResponseUtil.response(res, instance);
    }

    @GetMapping("/file/{fileHash}")
    public Object queryFile(@PathVariable String fileHash) {
        LOG.info("【FileController】查询文件");
        Map<String, Object> obj = new HashMap<>();
        obj.put("hash", fileHash);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "queryFile", obj);
        return ResponseUtil.response(res, instance);
    }

    @RequireLogin
    @PostMapping("/file/attr")
    public Object updateFileAttr(@RequestBody Map<String, Object> obj, HttpServletRequest request) {
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "updateFileAttr", obj);
        return ResponseUtil.response(res, instance);
    }

    @RequireLogin
    @PostMapping("/file/name")
    public Object updateFileName(@RequestBody Map<String, Object> obj, HttpServletRequest request) {
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "updateFileName", obj);
        return ResponseUtil.response(res, instance);
    }

    @RequireLogin
    @PostMapping("/file/recovery")
    public Object recoveryFile(@RequestBody Map<String, Object> obj, HttpServletRequest request) {
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "recoveryFile", obj);
        return ResponseUtil.response(res, instance);
    }

    @RequireLogin
    @PostMapping("/file/delete")
    public Object deleteFile(@RequestBody Map<String, Object> obj, HttpServletRequest request) {
        String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
        String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
        obj.put("uid", userId);
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "deleteFile", obj);
        return ResponseUtil.response(res, instance);
    }

    @RequireLogin
    @PostMapping("/file/upload")
    public Object fileUpload(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile image) throws Exception {
        BufferedReader reader = null;
        try {
            String sessionValue = CookieUtil.getCookieValue(sessionConfig.getSessionName(), request.getCookies());
            String userId = remote.post(ServiceConst.AUTH_SERVICE, "uid", sessionValue, String.class);
            String folder = request.getParameter("folder");
            String originalType = Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf(".")).toLowerCase();
            String name = image.getOriginalFilename().replace(originalType, "");
            String fileType = null;
            if (Objects.equals(originalType, ".rc")) {
                fileType = "doc";
            } else if (Objects.equals(originalType, ".md")) {
                fileType = "md";
            }
            File targetFile = new File("E:\\Program\\mulberrydoc\\upload", String.format("%d%s", DateUtil.nowDatetime().getTime(), originalType));
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            image.transferTo(targetFile);
            StringBuilder sbf = new StringBuilder();
            reader = new BufferedReader(new FileReader(targetFile));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
                if (Objects.equals(fileType, "md")) {
                    sbf.append("\n");
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("uid", userId);
            Map<String, Object> file = new HashMap<>();
            file.put("folderHash", folder);
            file.put("type", fileType);
            file.put("name", name);
            data.put("file", file);
            data.put("content", sbf.toString());
            HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "createFile", data);
            return ResponseUtil.response(res, instance);
        } catch (Throwable e) {
            if (Objects.nonNull(reader)) {
                reader.close();
            }
            return ResponseUtil.response(new HttpResponse(), instance);
        }
    }

    @PostMapping("/file/download")
    public Object fileDownload(HttpServletResponse response, @RequestBody Map<String, Object> obj) {
        HttpResponse res = remote.post(ServiceConst.DOCUMENTS_SERVICE, "downloadFile", obj);
        if (res.getStatus() == 200) {
            Map<String, String> data = (Map) res.getData();
            String name = data.get("name");
            String content = data.get("content");
            String type = data.get("type");

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            response.setHeader("Content-Disposition", String.format("attachment;fileName=%s.%s", name, type));
            try {
                OutputStream os = response.getOutputStream();
                os.write(content.getBytes(StandardCharsets.UTF_8));
                os.close();
            } catch (Exception e) {

            }
        }
        return ResponseUtil.response(res, instance);
    }

    @GetMapping("/file/write/{fileHash}")
    public Object writeFile(@PathVariable String fileHash) {
        return remote.post(ServiceConst.DOCUMENTS_SERVICE, "writeFile", fileHash);
    }
}
