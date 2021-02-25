package com.cqwu.jwy.mulberrydoc.documents.util;

import com.cqwu.jwy.mulberrydoc.common.util.CodecUtil;
import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;

public final class FileUtil
{
    private FileUtil()
    {
    }

    /**
     * 生成文件Hash
     *
     * @param uid        用户ID
     * @param folderHash 文件夹Hash
     * @param type       文件类型
     * @param name       文件名称
     * @return 文件Hash
     */
    public static String generateFolderHash(String uid, String folderHash, String type, String name)
    {
        return CodecUtil.md5(String.format("%s:file:%s:%s:%s:%d", uid, folderHash, type, name, DateUtil.nowDatetime().getTime()).getBytes());
    }
}
