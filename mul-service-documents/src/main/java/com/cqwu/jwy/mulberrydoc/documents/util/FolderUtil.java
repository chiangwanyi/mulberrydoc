package com.cqwu.jwy.mulberrydoc.documents.util;

import com.cqwu.jwy.mulberrydoc.common.util.CodecUtil;

public final class FolderUtil
{
    private FolderUtil()
    {
    }

    /**
     * 生成文件夹 Hash
     *
     * @param uid  用户 ID
     * @param path 文件夹路径
     * @return 文件夹标识
     */
    public static String generateFolderHash(String uid, String path)
    {
        return CodecUtil.md5((uid + ":folder:" + path).getBytes());
    }
}
