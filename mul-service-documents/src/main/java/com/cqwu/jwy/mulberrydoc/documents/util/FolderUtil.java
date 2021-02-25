package com.cqwu.jwy.mulberrydoc.documents.util;

import com.cqwu.jwy.mulberrydoc.common.util.CodecUtil;
import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;

public final class FolderUtil
{
    private FolderUtil()
    {
    }

    /**
     * 生成文件夹 Hash
     *
     * @param uid        用户ID
     * @param parentHash 文件夹路径
     * @return 文件夹标识
     */
    public static String generateFolderHash(String uid, String parentHash, String name)
    {
        return CodecUtil.md5(String.format("%s:folder:%s:%s:%d", uid, parentHash, name, DateUtil.nowDatetime().getTime()).getBytes());
    }
}
