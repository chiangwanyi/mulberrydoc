package com.cqwu.jwy.mulberrydoc.documents.service;

import com.cqwu.jwy.mulberrydoc.documents.dao.DocumentsDao;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class DocumentsService
{
    @Autowired
    private DocumentsDao documentsDao;

    /**
     * 创建文档空间
     *
     * @param uid 用户 ID
     */
    public void createDocument(String uid)
    {
        documentsDao.createDocuments(uid);
    }

    /**
     * 通过用户ID查询文档空间
     *
     * @param uid 用户ID
     * @return 结果
     */
    public Documents queryDocumentsByUserId(String uid)
    {
        Documents documents = documentsDao.queryDocumentsByUserId(uid);
        if (Objects.isNull(documents))
        {
            return null;
        }
        return documents;
    }
}
