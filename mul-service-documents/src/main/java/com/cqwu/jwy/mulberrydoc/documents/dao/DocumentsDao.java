package com.cqwu.jwy.mulberrydoc.documents.dao;

import com.cqwu.jwy.mulberrydoc.documents.pojo.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class DocumentsDao
{
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 创建文档空间
     *
     * @param uid 用户ID
     */
    public void create(String uid)
    {
        Documents documents = new Documents(uid);
        mongoTemplate.insert(documents);
    }

    /**
     * 通过用户ID查询文档空间
     *
     * @param uid 用户ID
     * @return 文档空间
     */
    public Documents queryDocumentsByUserId(String uid)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(uid));
        List<Documents> documents = mongoTemplate.find(query, Documents.class);
        if (CollectionUtils.isEmpty(documents))
        {
            return null;
        }
        return documents.get(0);
    }
}
