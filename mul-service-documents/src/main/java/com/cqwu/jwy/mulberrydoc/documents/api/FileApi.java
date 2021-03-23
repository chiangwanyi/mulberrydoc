package com.cqwu.jwy.mulberrydoc.documents.api;

import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.util.PojoGenerator;
import com.cqwu.jwy.mulberrydoc.common.validator.Validator;
import com.cqwu.jwy.mulberrydoc.documents.configure.DocumentsInstance;
import com.cqwu.jwy.mulberrydoc.documents.constant.FileConstant;
import com.cqwu.jwy.mulberrydoc.documents.pojo.File;
import com.cqwu.jwy.mulberrydoc.documents.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class FileApi
{
    private static final Logger LOG = LoggerFactory.getLogger(FileApi.class);
    private static final String PARAM_UID = "uid";
    private static final String PARAM_FILE = "file";
    private static final String PARAM_FILE_HASH = "hash";

    @Autowired
    private DocumentsInstance instance;
    @Autowired
    private FileService fileService;

    /**
     * 创建文件
     *
     * @param obj 参数（UserId, File实体）
     * @return HttpResponse
     */
    @PostMapping("createFile")
    public HttpResponse createFile(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【创建文件】参数：{}", obj);
        // 用户ID
        String uid = (String) obj.get(PARAM_UID);
        // 待创建的文件
        File info = PojoGenerator.generate(obj.get(PARAM_FILE), File.class);

        if (StringUtils.isEmpty(uid) || Objects.isNull(info))
        {
            LOG.warn("【创建文件】参数不完整");
            return HttpSerializer.incompleteParamsFailed(instance);
        }

        Map<String, List<String>> errorMsg = Validator.verify(info, File.class);
        // 校验失败
        if (!errorMsg.isEmpty())
        {
            LOG.warn("【创建文件夹】校验字段不通过");
            return HttpSerializer.invalidParamsFailed(instance, errorMsg);
        }

        try
        {
            File file = fileService.createFile(uid, info);
            return HttpSerializer.success(instance)
                    .msg(FileConstant.CREATE_FILE_SUCCESS)
                    .data(file);
        }
        catch (WebException e)
        {
            LOG.error("【创建文件】创建失败，{}，error:", e.getErrorMsg(), e);
            return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(e);
        }
        catch (Exception e)
        {
            LOG.error("【创建文件】创建失败，error:", e);
            return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(e);
        }
    }

    /**
     * 查询文件
     *
     * @param obj 参数
     * @return 结果
     */
    @PostMapping("queryFile")
    public HttpResponse queryFile(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【查询文件】参数：{}", obj);
        String hash = (String) obj.get(PARAM_FILE_HASH);
        if (StringUtils.isEmpty(hash))
        {
            LOG.warn("【查询文件】参数不完整");
            return HttpSerializer.incompleteParamsFailed(instance);
        }

        try
        {
            File file = fileService.queryFile(hash);
            return HttpSerializer.success(instance)
                    .msg(FileConstant.QUERY_FILE_SUCCESS)
                    .data(file);
        }
        catch (Exception e)
        {
            LOG.error("【查询文件】创建失败，error:", e);
            return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(e);
        }
    }

    @PostMapping("updateFileAttr")
    public HttpResponse updateFileAttr(@RequestBody Map<String, Object> obj)
    {
        String uid = (String) obj.get(PARAM_UID);
        String hash = (String) obj.get(PARAM_FILE_HASH);
        Integer rwStatus = (Integer) obj.get("rw_status");
        Integer ownership = (Integer) obj.get("ownership");
        try
        {
            fileService.updateFileAttr(uid, hash, rwStatus, ownership);
            return HttpSerializer.success(instance);
        }
        catch (Exception e)
        {
            return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(e);
        }
    }

    @PostMapping("updateFileName")
    public HttpResponse updateFileName(@RequestBody Map<String, Object> obj)
    {
        String uid = (String) obj.get(PARAM_UID);
        String hash = (String) obj.get(PARAM_FILE_HASH);
        String name = (String) obj.get("name");
        try
        {
            fileService.updateFileName(uid, hash, name);
            return HttpSerializer.success(instance);
        }
        catch (WebException e)
        {
            return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(e);
        }
        catch (Exception e)
        {
            return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(e);
        }
    }

//    public HttpResponse checkFileName(@RequestBody Map<String, Object> obj) {
//        String uid = (String) obj.get(PARAM_UID);
//        String hash = (String) obj.get(PARAM_FILE_HASH);
//        String name = (String) obj.get("name");
//        try {
//            File file = fileService.queryFile(hash);
//            if (Objects.nonNull(file)) {
//                fileService.isExistedFile(uid, file.getFolderHash(), file.getType(), name)
//
//            } else {
//                return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
//                        .msg("文件不存在");
//            }
//        } catch (Exception e) {
//
//        }
//
//    }
}
