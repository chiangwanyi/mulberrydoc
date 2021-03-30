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
import com.cqwu.jwy.mulberrydoc.documents.pojo.File;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Folder;
import com.cqwu.jwy.mulberrydoc.documents.service.DocumentsService;
import com.cqwu.jwy.mulberrydoc.documents.service.FileService;
import com.cqwu.jwy.mulberrydoc.documents.service.FolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsConstant.CREATE_DOCUMENTS_SUCCESS;

@RestController
public class DocumentsApi
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentsApi.class);
    private static final String PARAM_UID = "uid";
    private static final String PARAM_FOLDER = "folder";
    private static final String PARAM_PARENT_HASH = "parentHash";
    private static final String PARAM_HASH = "hash";

    @Autowired
    private DocumentsService documentsService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private FileService fileService;
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
            List<File> files = fileService.queryFiles(uid, folder.getHash());
            Map<String, Object> res = new HashMap<>();
            res.put("folder", folder);
            res.put("files", files);
            return HttpSerializer.success(instance)
                    .msg(FolderConstant.QUERY_FOLDERS_SUCCESS)
                    .data(res);
        }
        catch (WebException e)
        {
            LOG.error("【查询文件夹】查询失败，{}, error：", e.getErrorMsg(), e);
            return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(e);
        }
    }

    /**
     * 根据父文件夹Hash查找所有子文件夹
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
        Folder updateFolder = PojoGenerator.generate(obj.get(PARAM_FOLDER), Folder.class);
        if (StringUtils.isEmpty(uid) || Objects.isNull(updateFolder) || StringUtils.isEmpty(updateFolder.getHash()))
        {
            LOG.warn("【修改文件夹】参数不完整，folder:{}", updateFolder);
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        LOG.info("【修改文件夹】待修改的文件夹数据：{}", updateFolder);
        try
        {
            boolean result = folderService.updateFolder(uid, updateFolder);
            if (result)
            {
                return HttpSerializer.success(instance)
                        .msg(FolderConstant.UPDATE_FOLDERS_SUCCESS);
            }
            else
            {
                return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                        .msg(FolderError.UPDATE_FOLDER_IGNORE);
            }
        }
        catch (WebException e)
        {
            LOG.error("【修改文件夹】修改文件夹时发生异常，error:", e);
            return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                    .msg(e);
        }
    }

    @PostMapping("removeItems")
    public HttpResponse removeItems(@RequestBody Map<String, Object> obj)
    {
        String uid = (String) obj.get("uid");
        List<String> fileHashes = (List) obj.get("fileHashes");
        List<String> folderHashes = (List) obj.get("folderHashes");
        if (StringUtils.isEmpty(uid) || (CollectionUtils.isEmpty(fileHashes) && CollectionUtils.isEmpty(folderHashes)))
        {
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        try
        {
            for (String folderHash : folderHashes)
            {
                folderService.removeFolder(uid, folderHash);
            }
            fileService.removeFile(uid, fileHashes);
            return HttpSerializer.success(instance)
                    .msg(FolderConstant.REMOVE_ITEM_SUCCESS);
        }
        catch (Exception e)
        {
            return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(e);
        }
    }

    @PostMapping("moveItemsTo")
    public HttpResponse moveItemsTo(@RequestBody Map<String, Object> obj)
    {
        String uid = (String) obj.get("uid");
        String toFolderHash = (String) obj.get("toFolder");
        List<String> files = (List) obj.get("files");
        List<String> folders = (List) obj.get("folders");
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(toFolderHash))
        {
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        try
        {
            fileService.moveFile(uid, toFolderHash, files);
            folderService.moveFolder(uid, folders, toFolderHash);
            return HttpSerializer.success(instance);
        }
        catch (WebException e)
        {
            return HttpSerializer.failure(instance, HttpSerializer.STATUS_FORBIDDEN_FAILED)
                    .msg(e);
        }
        catch (Exception e)
        {
            return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(e);
        }
    }

    /**
     * 获取文件夹路径
     *
     * @param obj 参数（UserId, FolderHash）
     * @return HttpResponse
     */
    @PostMapping("getFolderPath")
    public HttpResponse getFolderPath(@RequestBody Map<String, Object> obj)
    {
        LOG.info("【获取文件夹路径】参数：{}", obj);
        String uid = (String) obj.get(PARAM_UID);
        String folderHash = (String) obj.get(PARAM_HASH);
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(folderHash))
        {
            LOG.warn("【获取文件夹路径】参数不完整，folderHash:{}", folderHash);
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        try
        {
            List<String> folderPath = folderService.getFolderPath(uid, folderHash);
            return HttpSerializer.success(instance)
                    .msg(FolderConstant.GET_FOLDER_PATH_SUCCESS)
                    .data(folderPath);
        }
        catch (WebException e)
        {
            LOG.error("【获取文件夹路径】获取文件夹路径时发生异常，error:", e);
            return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(e);
        }
    }

    @PostMapping("queryDeletedItems")
    public HttpResponse queryDeletedItems(@RequestBody Map<String, Object> obj)
    {
        String uid = (String) obj.get(PARAM_UID);
        if (StringUtils.isEmpty(uid))
        {
            return HttpSerializer.incompleteParamsFailed(instance);
        }
        try
        {
            List<File> files = fileService.queryDeletedFiles(uid);
//            List<Folder> folders = folderService.queryDeletedFolders(uid);
            Map<String, Object> data = new HashMap<>();
            data.put("files", files);
//            data.put("folders", folders);
            return HttpSerializer.success(instance)
                    .data(data);
        }
        catch (Exception e)
        {
            return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                    .msg(e);
        }
    }
}
