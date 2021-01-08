package com.cqwu.jwy.mulberrydoc.common.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cqwu.jwy.mulberrydoc.common.configure.ServerInstance;
import com.cqwu.jwy.mulberrydoc.common.constant.CommonError;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Http序列化器
 */
public final class HttpSerializer
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpSerializer.class);

    /** 正常 */
    public static final int STATUS_OK = 200;
    /** 请求失败 */
    public static final int STATUS_BAD_REQUEST = 400;
    /** 校验失败 */
    public static final int STATUS_VALID_FAILED = 401;
    /** 无操作权限 */
    public static final int STATUS_FORBIDDEN_FAILED = 403;
    /** 内部错误 */
    public static final int INTERNAL_SERVER_ERROR = 500;

    /** HTTP_RESPONSE */
    public static final String HTTP_RESPONSE_KEY = "HttpResponse";
    /** COOKIES */
    public static final String COOKIES_KEY = "Cookies";

    private HttpSerializer()
    {
    }

    /**
     * 成功
     *
     * @return HttpResponse
     */
    private static HttpResponse success()
    {
        return response();
    }

    /**
     * 成功，并设置实例信息
     *
     * @param instance 实例信息
     * @return HttpResponse
     */
    public static HttpResponse success(ServerInstance instance)
    {
        return response()
                .status(HttpSerializer.STATUS_OK)
                .instances(instance);
    }

    /**
     * 失败
     *
     * @return HttpResponse
     */
    private static HttpResponse failure()
    {
        return response();
    }

    /**
     * 失败，并设置实例信息
     *
     * @param instance 实例信息
     * @return HttpResponse
     */
    public static HttpResponse failure(ServerInstance instance, int status)
    {
        return response()
                .status(status)
                .instances(instance);
    }

    /**
     * 将Object转换为HttpResponse
     *
     * @param o Object
     * @return HttpResponse
     */
    public static HttpResponse convert(Object o)
    {
        try
        {
            return JSONObject.parseObject(JSON.toJSONString(o), HttpResponse.class);
        }
        catch (Exception e)
        {
            LOG.error("转换HttpResponse失败，error:", e);
            return null;
        }
    }

    private static HttpResponse response()
    {
        return new HttpResponse();
    }

    /**
     * 参数不完整
     *
     * @param instance 实例信息
     */
    public static HttpResponse incompleteParamsFailed(ServerInstance instance)
    {
        return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                .msg(CommonError.INCOMPLETE_PARAMETERS);
    }

    /**
     * 无操作权限
     *
     * @param instance 实例信息
     * @return HttpResponse
     */
    public static HttpResponse operationForbiddenFailed(ServerInstance instance)
    {
        return HttpSerializer.failure(instance, HttpSerializer.STATUS_FORBIDDEN_FAILED)
                .msg(CommonError.VERIFICATION_FAILED);
    }

    /**
     * 参数校验错误
     *
     * @param instance  实例信息
     * @param errorInfo 错误信息
     * @return HttpResponse
     */
    public static HttpResponse invalidParamsFailed(ServerInstance instance, Object errorInfo)
    {
        return HttpSerializer.failure(instance, HttpSerializer.STATUS_BAD_REQUEST)
                .msg(CommonError.INVALID_PARAMETERS)
                .data(Objects.isNull(errorInfo) ? null : errorInfo);
    }

    /**
     * 服务连接失败
     *
     * @param instance 实例信息
     * @param e        异常
     * @return HttpResponse
     */
    public static HttpResponse serverUnreachableFailed(ServerInstance instance, Throwable e)
    {
        return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                .msg(e);
    }

    /**
     * 内部错误
     *
     * @param instance 实例信息
     * @param e        异常
     * @return HttpResponse
     */
    public static HttpResponse internalError(ServerInstance instance, Throwable e)
    {
        return HttpSerializer.failure(instance, HttpSerializer.INTERNAL_SERVER_ERROR)
                .msg(CommonError.INTERNAL_ERROR)
                .data(e);
    }
}
