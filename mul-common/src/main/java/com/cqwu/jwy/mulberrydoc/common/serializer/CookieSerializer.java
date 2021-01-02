package com.cqwu.jwy.mulberrydoc.common.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;

/**
 * Cookie序列化器
 */
public final class CookieSerializer
{
    private static final Logger LOG = LoggerFactory.getLogger(CookieSerializer.class);

    private CookieSerializer()
    {
    }

    /**
     * 将Object转换为Cookie List
     *
     * @param o Object
     * @return Cookie List
     */
    public static Cookie[] convert(Object o)
    {
        try
        {
            return JSONObject.parseObject(JSON.toJSONString(o), Cookie[].class);
        }
        catch (Exception e)
        {
            LOG.error("转换Cookie List失败，error:", e);
            return null;
        }
    }
}
