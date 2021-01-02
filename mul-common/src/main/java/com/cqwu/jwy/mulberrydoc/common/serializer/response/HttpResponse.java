package com.cqwu.jwy.mulberrydoc.common.serializer.response;

import com.cqwu.jwy.mulberrydoc.common.exception.ErrorMsg;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 统一服务返回类
 */
public class HttpResponse
{
    /** 状态码 */
    private int status;
    /** 消息 */
    private Object msg;
    /** 数据 */
    private Object data;
    /** 服务信息 */
    private final Set<Object> instances = new HashSet<>();

    public HttpResponse()
    {
    }

    public HttpResponse status(int status)
    {
        this.status = status;
        return this;
    }

    public HttpResponse msg(Object msg)
    {
        if (msg instanceof WebException)
        {
            WebException exception = (WebException) msg;
            Map<String, Object> data = new HashMap<>();
            data.put("code", exception.getErrorMsg().getCode());
            data.put("message", exception.getMessage());
            this.msg = data;
        }
        else if (msg instanceof ErrorMsg)
        {
            Map<String, Object> data = new HashMap<>();
            ErrorMsg errorMsg = (ErrorMsg) msg;
            data.put("code", errorMsg.getCode());
            data.put("message", errorMsg.getMessage());
            this.msg = data;
        }
        else
        {
            this.msg = msg;
        }
        return this;
    }

    public HttpResponse data(Object data)
    {
        this.data = data;
        return this;
    }

    public HttpResponse instances(String instanceId)
    {
        this.instances.add(instanceId);
        return this;
    }

    public int getStatus()
    {
        return status;
    }

    public Object getMsg()
    {
        return msg;
    }

    public Object getData()
    {
        return data;
    }

    public Set<Object> getInstances()
    {
        return instances;
    }

    @Override
    public String toString()
    {
        return "HttpResponse{" +
                "status=" + status +
                ", msg=" + msg +
                ", data=" + data +
                ", instances=" + instances +
                '}';
    }
}
