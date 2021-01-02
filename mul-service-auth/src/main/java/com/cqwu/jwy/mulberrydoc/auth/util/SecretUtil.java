package com.cqwu.jwy.mulberrydoc.auth.util;

import com.cqwu.jwy.mulberrydoc.auth.config.SecretConfig;
import com.cqwu.jwy.mulberrydoc.common.util.CodecUtil;
import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecretUtil
{
    @Autowired
    private SecretConfig secretConfig;

    /**
     * 生成密码密文
     *
     * @param raw  密码明文
     * @param salt 盐
     * @return 密码密文
     */
    public String generatePassword(String raw, String salt)
    {
        return CodecUtil.sha1((raw + salt + secretConfig.getPasswordSalt()).getBytes());
    }

    /**
     * 生成 Session Value 字符串
     *
     * @param username 用户名
     * @return Session Value 字符串
     */
    public static String generateSessionValue(String username)
    {
        return CodecUtil.sha1((username + ":" + DateUtil.nowDatetime().getTime()).getBytes());
    }
}
