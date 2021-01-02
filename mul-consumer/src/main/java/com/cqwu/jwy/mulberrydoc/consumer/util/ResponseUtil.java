package com.cqwu.jwy.mulberrydoc.consumer.util;

import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.consumer.configure.Instance;

import java.util.Objects;

public final class ResponseUtil
{
    private ResponseUtil()
    {
    }

    public static Object response(HttpResponse response, Instance instance)
    {
        if (Objects.nonNull(response))
        {
            // 附加自己的实例ID
            return response.instances(instance.getInstanceId());
        }
        return HttpSerializer.internalError(instance.getInstanceId(), null);
    }
}
