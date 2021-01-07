package com.cqwu.jwy.mulberrydoc.documents.service;

import com.cqwu.jwy.mulberrydoc.documents.dao.DocumentsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        documentsDao.create(uid);
    }
}
