package com.cqwu.jwy.mulberrydoc.documents.serializer;

import com.cqwu.jwy.mulberrydoc.documents.pojo.Documents;

import java.util.HashMap;
import java.util.Map;

/**
 * 文档空间序列化器
 */
public final class DocumentsSerializer
{
    private DocumentsSerializer()
    {
    }

    /**
     * 生成文档空间序列化数据
     *
     * @param documents 文档空间
     * @return 数据
     */
    public static Map<String, Object> serialData(Documents documents)
    {
        Map<String, Object> data = new HashMap<>();
        return data;
    }
}
