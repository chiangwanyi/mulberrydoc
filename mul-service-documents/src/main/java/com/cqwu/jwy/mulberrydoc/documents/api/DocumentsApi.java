package com.cqwu.jwy.mulberrydoc.documents.api;

import com.cqwu.jwy.mulberrydoc.common.constant.ServiceConst;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.util.PojoGenerator;
import com.cqwu.jwy.mulberrydoc.common.validator.Validator;
import com.cqwu.jwy.mulberrydoc.documents.configure.DocumentsInstance;
import com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsError;
import com.cqwu.jwy.mulberrydoc.documents.constant.FolderConstant;
import com.cqwu.jwy.mulberrydoc.documents.constant.FolderError;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Documents;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Folder;
import com.cqwu.jwy.mulberrydoc.documents.service.DocumentsService;
import com.cqwu.jwy.mulberrydoc.documents.service.FolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsConstant.CREATE_DOCUMENTS_SUCCESS;

@RestController
public class DocumentsApi
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentsApi.class);
    private static final String PARAM_UID = "uid";
    private static final String PARAM_PARENT_HASH = "parentHash";
    private static final String PARAM_HASH = "hash";
    private static final String PARAM_FOLDER = "folder";

    @Autowired
    private DocumentsService documentsService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private DocumentsInstance instance;

    /**
     * 返回程序运行状态
     *
     * @return HttpResponse
     */
    @GetMapping("ping")
    public HttpResponse ping()
    {
        LOG.info("【检查运行状态】PING");
        return HttpSerializer.success(instance)
                .msg(ServiceConst.PONG);
    }

    /**
     * 创建文档空间
     *
     * @param uid 用户ID
     * @return HttpResponse
     */
    @PostMapping("createDocuments")
    public HttpResponse createDocuments(@RequestBody @NotNull String uid)
    {
        LOG.info("【创建文档空间】用户ID：{}创建文档空间", uid);
        // 判断文档空间是否已经创建
        Documents documents = documentsService.queryDocumentsByUserId(uid);
        // 未创建
        if (Objects.isNull(documents))
        {
            documentsService.createDocument(uid);
            LOG.info("【创建文档空间】用户ID：{}创建文档空间成功", uid);
            return HttpSerializer
                    .success(instance)
                    .msg(CREATE_DOCUMENTS_SUCCESS);
        }
        // 已创建
        LOG.warn("【创建文档空间】用户ID：{}的文档空间已存在，创建文档空间失败，", uid);
        return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                .msg(DocumentsError.DOCUMENTS_EXISTENT);
    }

    /**
     * 根据文件夹 Hash 查询文件夹
     *
     * @param obj 参数（UserId, FolderHash）
     * @return HttpResponse
     */
    @PostMapping("queryFolder")
    public HttpResponse queryFolder(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【查询文件夹】参数：{}", obj);
        String uid = (String) obj.get(PARAM_UID);
        String hash = (String) obj.get(PARAM_HASH);
        // 参数不完整
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(hash))
        {
            LOG.warn("【查询文件夹】参数不完整");
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        LOG.info("【查询文件夹】用户ID：{}\t文件夹Hash：{}", uid, hash);
        try
        {
            Folder folder = folderService.queryFolderByHash(uid, hash);
            // 文件夹不存在
            if (Objects.isNull(folder))
            {
                return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                        .msg(FolderError.FOLDER_NON_EXISTENT);
            }
            return HttpSerializer.success(instance)
                    .msg(FolderConstant.QUERY_FOLDERS_SUCCESS)
                    .data(folder);
        }
        catch (WebException e)
        {
            LOG.error("【查询文件夹】查询失败，{}, error：", e.getErrorMsg(), e);
            return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(e);
        }
    }

    /**
     * 根据父文件夹Hash查找子文件夹
     *
     * @param obj 参数（UserId, ParentFolderHash）
     * @return HttpResponse
     */
    @PostMapping("querySubfolder")
    public HttpResponse querySubfolder(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【查询子文件夹列表】参数：{}", obj);
        String uid = (String) obj.get(PARAM_UID);
        String parentHash = (String) obj.get(PARAM_PARENT_HASH);
        // 参数不完整
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(parentHash))
        {
            LOG.warn("【查询子文件夹列表】参数不完整");
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        LOG.info("【查询子文件夹列表】用户ID：{}\t父文件夹Hash：{}", uid, parentHash);
        try
        {
            List<Folder> subfolder = folderService.querySubfolderByParentHash(uid, parentHash);
            LOG.info("【查询子文件夹列表】查询成功，文件夹列表：{}", subfolder);
            return HttpSerializer.success(instance)
                    .msg(FolderConstant.QUERY_FOLDERS_SUCCESS)
                    .data(subfolder);
        }
        // 文档空间不存在，查询失败
        catch (WebException e)
        {
            LOG.error("【查询子文件夹列表】查询失败，{}, error：", e.getErrorMsg(), e);
            return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(e);
        }
    }

    /**
     * 创建文件夹
     *
     * @param obj 参数（UserId, Folder实体）
     * @return HttpResponse
     */
    @PostMapping("createFolder")
    public HttpResponse createFolder(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【创建文件夹】参数：{}", obj);
        // 用户ID
        String uid = (String) obj.get(PARAM_UID);
        // 待创建的文件夹
        Folder info = PojoGenerator.generate(obj.get(PARAM_FOLDER), Folder.class);

        if (StringUtils.isEmpty(uid) || Objects.isNull(info))
        {
            LOG.warn("【创建文件夹】参数不完整");
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        Map<String, List<String>> errorMsg = Validator.verify(info, Folder.class);
        // 校验失败
        if (!errorMsg.isEmpty())
        {
            LOG.warn("【创建文件夹】校验字段不通过");
            return HttpSerializer.invalidParamsFailed(instance, errorMsg);
        }
        // 创建文件夹
        try
        {
            Folder folder = folderService.createFolder(uid, info);
            return HttpSerializer.success(instance)
                    .msg(FolderConstant.CREATE_FOLDER_SUCCESS)
                    .data(folder);
        }
        // 创建失败
        catch (WebException e)
        {
            LOG.error("【创建文件夹】创建失败，{}，error:", e.getErrorMsg(), e);
            return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(e);
        }
    }

    /**
     * 修改文件夹
     *
     * @param obj 参数（UserId, Folder实体）
     * @return HttpResponse
     */
    @PostMapping("updateFolder")
    public HttpResponse updateFolder(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【修改文件夹】参数：{}", obj);
        String uid = (String) obj.get(PARAM_UID);
        // 待创建的文件夹
        Folder info = PojoGenerator.generate(obj.get(PARAM_FOLDER), Folder.class);
        if (StringUtils.isEmpty(uid) || Objects.isNull(info) || StringUtils.isEmpty(info.getHash()) || StringUtils.isEmpty(info.getName()))
        {
            LOG.warn("【修改文件夹】参数不完整");
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        return null;
    }
}
