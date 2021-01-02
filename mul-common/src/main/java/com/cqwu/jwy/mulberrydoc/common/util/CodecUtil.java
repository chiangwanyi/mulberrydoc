package com.cqwu.jwy.mulberrydoc.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 编码工具
 */
public final class CodecUtil
{
    private static final Base64 base64 = new Base64();

    public static String md5(byte[] content)
    {
        return DigestUtils.md5Hex(content);
    }

    /**
     * SHA1 加密
     *
     * @param content 加密内容
     * @return 加密结果
     */
    public static String sha1(byte[] content)
    {
        return DigestUtils.sha1Hex(content);
    }

    /**
     * Base64 编码
     *
     * @param content 编码内容
     * @return 编码结果
     */
    public static String base64Encode(byte[] content)
    {
        return base64.encodeToString(content);
    }

    /**
     * Base64 解码
     *
     * @param content 解码内容
     * @return 解码结果
     */
    public static String base64Decode(byte[] content)
    {
        return new String(base64.decode(content));
    }
}
