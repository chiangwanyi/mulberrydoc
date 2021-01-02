package com.cqwu.jwy.mulberrydoc.common.serializer;

import com.cqwu.jwy.mulberrydoc.common.constant.CommonError;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;

import java.util.Objects;

/**
 * Http序列化器
 */
public class HttpSerializer
{
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

    private HttpSerializer()
    {
    }

    /**
     * 成功
     *
     * @return HttpResponse
     */
    public static HttpResponse success()
    {
        return response();
    }

    /**
     * 成功，并设置实例ID
     *
     * @param instanceId 实例ID
     * @return HttpResponse
     */
    public static HttpResponse success(String instanceId)
    {
        return response().instances(instanceId);
    }

    /**
     * 失败
     *
     * @return HttpResponse
     */
    public static HttpResponse failure()
    {
        return response();
    }

    /**
     * 失败，并设置实例ID
     *
     * @param instanceId 实例ID
     * @return HttpResponse
     */
    public static HttpResponse failure(String instanceId)
    {
        return response().instances(instanceId);
    }

    private static HttpResponse response()
    {
        return new HttpResponse();
    }

    /**
     * 返回参数校验错误 HttpResponse
     *
     * @param verifyInfo 错误信息
     * @param instanceId 实例Id
     * @return HttpResponse
     */
    public static HttpResponse paramsVerifyFailed(Object verifyInfo, String instanceId)
    {
        return HttpSerializer.failure(instanceId)
                .status(HttpSerializer.STATUS_BAD_REQUEST)
                .msg(CommonError.INCOMPLETE_PARAMETERS)
                .data(Objects.isNull(verifyInfo) ? null : verifyInfo);
    }
}
