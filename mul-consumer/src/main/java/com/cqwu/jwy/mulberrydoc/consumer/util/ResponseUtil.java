package com.cqwu.jwy.mulberrydoc.consumer.util;

import com.cqwu.jwy.mulberrydoc.common.constant.CommonError;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.consumer.configure.ConsumerInstance;

import java.util.Objects;

public final class ResponseUtil
{
    private ResponseUtil()
    {
    }

    public static Object response(HttpResponse response, ConsumerInstance instance)
    {
        if (Objects.nonNull(response))
        {
            // 附加自己的实例信息
            return response.instances(instance);
        }
        return HttpSerializer.serverUnreachableFailed(instance, new WebException(CommonError.SERVER_UNREACHABLE));
    }
}
