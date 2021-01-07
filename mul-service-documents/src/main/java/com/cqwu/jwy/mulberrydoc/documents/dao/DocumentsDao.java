package com.cqwu.jwy.mulberrydoc.documents.dao;

import com.cqwu.jwy.mulberrydoc.documents.pojo.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentsDao
{
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 创建文档空间
     *
     * @param uid 用户 ID
     */
    public void create(String uid)
    {
        Documents documents = new Documents(uid);
        mongoTemplate.insert(documents);
    }
}
