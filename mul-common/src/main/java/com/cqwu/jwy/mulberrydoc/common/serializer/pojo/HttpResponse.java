package com.cqwu.jwy.mulberrydoc.common.serializer.pojo;

import com.cqwu.jwy.mulberrydoc.common.exception.ErrorMsg;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse
{
    private int status;
    private Object msg;
    private Object data;

    public HttpResponse()
    {
    }

    public int getStatus()
    {
        return status;
    }

    public HttpResponse status(int status)
    {
        this.status = status;
        return this;
    }

    public Object getMsg()
    {
        return msg;
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

    public Object getData()
    {
        return data;
    }

    public HttpResponse data(Object data)
    {
        this.data = data;
        return this;
    }

    @Override
    public String toString()
    {
        return "HttpResponse{" +
                "status=" + status +
                ", msg=" + msg +
                ", data=" + data +
                '}';
    }
}
