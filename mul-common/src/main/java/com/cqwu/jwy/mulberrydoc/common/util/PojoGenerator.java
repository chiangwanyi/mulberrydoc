package com.cqwu.jwy.mulberrydoc.common.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public final class PojoGenerator
{
    private PojoGenerator()
    {
    }

    public static <T> T generate(Map<String, Object> obj, Class<T> type)
    {
        try
        {
            T t = type.newInstance();
            Field[] fields = t.getClass().getDeclaredFields();
            for (Field field : fields)
            {
                String name = field.getName();
                Object value = obj.get(name);
                if (Objects.nonNull(value))
                {
                    field.setAccessible(true);
                    field.set(t, value);
                }
            }
            return t;
        }
        catch (Exception ignore)
        {
            return null;
        }
    }
}
