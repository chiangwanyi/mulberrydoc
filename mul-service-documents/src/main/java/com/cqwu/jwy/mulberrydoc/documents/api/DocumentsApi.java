package com.cqwu.jwy.mulberrydoc.documents.api;

import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.documents.configure.Instance;
import com.cqwu.jwy.mulberrydoc.documents.service.DocumentsService;
import com.cqwu.jwy.mulberrydoc.documents.service.FolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import static com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsConstant.CREATE_DOCUMENTS_SUCCESS;

@RestController
public class DocumentsApi
{
    private static final Logger LOG = LoggerFactory.getLogger(DocumentsApi.class);
    @Autowired
    private DocumentsService documentsService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private Instance instance;


    /**
     * 创建文档空间
     *
     * @param uid 用户 ID
     * @return HttpResponse
     */
    @PostMapping("createDocuments")
    public HttpResponse createDocuments(@RequestBody @NotNull String uid)
    {
        LOG.info("【创建文档空间】用户ID：{}创建文档空间", uid);
        documentsService.createDocument(uid);
        return HttpSerializer
                .success(instance.getInstanceId())
                .msg(CREATE_DOCUMENTS_SUCCESS);
    }


}
