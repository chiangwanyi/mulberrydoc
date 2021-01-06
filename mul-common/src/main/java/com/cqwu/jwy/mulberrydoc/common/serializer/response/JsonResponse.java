package com.cqwu.jwy.mulberrydoc.common.serializer.response;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON Response
 */
public class JsonResponse
{
    private Map<String, Object> data;

    public JsonResponse()
    {
        this.data = new HashMap<>();
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }

    public JsonResponse data(String key, Object value)
    {
        this.data.put(key, value);
        return this;
    }
}
