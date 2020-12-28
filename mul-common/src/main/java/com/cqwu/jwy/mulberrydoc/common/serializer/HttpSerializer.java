package com.cqwu.jwy.mulberrydoc.common.serializer;

import com.cqwu.jwy.mulberrydoc.common.constant.CommonError;
import com.cqwu.jwy.mulberrydoc.common.serializer.pojo.HttpResponse;

import java.util.Objects;

public class HttpSerializer
{
    public static final int STATUS_OK = 200;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_VALID_FAILED = 401;
    public static final int STATUS_FORBIDDEN_FAILED = 403;
    public static final int INTERNAL_SERVER_ERROR = 500;

    private HttpSerializer()
    {
    }

    public static HttpResponse success()
    {
        return response();
    }

    public static HttpResponse failure()
    {
        return response();
    }

    private static HttpResponse response()
    {
        return new HttpResponse();
    }

    public static HttpResponse paramsVerifyFailed(Object verifyInfo)
    {
        return HttpSerializer.failure()
                .status(HttpSerializer.STATUS_BAD_REQUEST)
                .msg(CommonError.INCOMPLETE_PARAMETERS)
                .data(Objects.isNull(verifyInfo) ? null : verifyInfo);
    }
}
