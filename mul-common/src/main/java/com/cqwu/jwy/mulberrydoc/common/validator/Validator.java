package com.cqwu.jwy.mulberrydoc.common.validator;

import com.cqwu.jwy.mulberrydoc.common.validator.annotation.Rule;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Pojo校验器
 */
public final class Validator
{
    private Validator()
    {
    }

    public static Map<String, List<String>> verify(Object obj, Class<?> c)
    {
        return verify(obj, c, null);
    }

    /**
     * 校验数据
     *
     * @param obj 实例
     * @param c   实例类
     * @return 校验结果
     */
    public static Map<String, List<String>> verify(Object obj, Class<?> c, List<String> ignoreFields)
    {
        Map<String, List<String>> errorMsg = new HashMap<>();
        for (Field field : c.getDeclaredFields())
        {
            field.setAccessible(true);
            String fieldName = field.getName();
            // 检查字段是否需要校验
            if (Objects.nonNull(ignoreFields) && !ignoreFields.contains(fieldName))
            {
                Rule rule = field.getAnnotation(Rule.class);
                Object argv;
                try
                {
                    argv = field.get(obj);
                }
                catch (Exception e)
                {
                    argv = null;
                }
                List<String> msg = verifyArgv(rule, argv);
                if (!msg.isEmpty())
                {
                    errorMsg.put(fieldName, msg);
                }
            }
        }
        return errorMsg;
    }

    /**
     * 校验字段
     *
     * @param rule 规则
     * @param argv 字段
     * @return 错误信息
     */
    private static List<String> verifyArgv(Rule rule, Object argv)
    {
        // rule 为空，不需要校验
        if (Objects.isNull(rule))
        {
            return Collections.emptyList();
        }
        // rule 不为空，argv 为空
        if (Objects.isNull(argv))
        {
            return Collections.singletonList(String.format("[%s]不能为空", rule.name()));
        }
        List<String> msg = new ArrayList<>();
        if (argv instanceof String)
        {
            msg = verifyString(rule, (String) argv);
        }
        if (argv instanceof Integer)
        {
            msg = verifyInteger(rule, (Integer) argv);
        }
        return msg;
    }

    private static List<String> verifyString(Rule rule, String str)
    {
        List<String> msg = new ArrayList<>();
        if (str.length() > rule.max())
        {
            msg.add(String.format("[%s]长度大于%d", rule.name(), rule.max()));
        }
        if (str.length() < rule.min())
        {
            msg.add(String.format("[%s]长度小于%d", rule.name(), rule.min()));
        }
        return msg;
    }

    private static List<String> verifyInteger(Rule rule, Integer num)
    {
        List<String> msg = new ArrayList<>();
        if (num > rule.max())
        {
            msg.add(String.format("[%s]的值大于%d", rule.name(), rule.max()));
        }
        if (num < rule.min())
        {
            msg.add(String.format("[%s]的值小于%d", rule.name(), rule.min()));
        }
        return msg;
    }
}
