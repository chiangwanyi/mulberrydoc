package com.cqwu.jwy.mulberrydoc.documents.util;

import com.cqwu.jwy.mulberrydoc.common.util.CodecUtil;
import com.cqwu.jwy.mulberrydoc.documents.pojo.File;

public final class FileUtil
{
    private FileUtil()
    {
    }

    /**
     * 生成文件 Hash
     *
     * @return 文件夹标识
     */
    public static String generateFolderHash(File info)
    {
        return CodecUtil.md5(String.format("%s:%s:%s:%s", info.getUid(), info.getFolderHash(), info.getType(), info.getName()).getBytes());
    }
}
