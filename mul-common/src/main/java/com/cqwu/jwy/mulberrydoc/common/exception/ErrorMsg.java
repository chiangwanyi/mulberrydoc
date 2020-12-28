package com.cqwu.jwy.mulberrydoc.common.exception;

public interface ErrorMsg
{
    Integer getCode();

    String getMessage();

    default String formatMessage(Object... obj)
    {
        return String.format(getMessage(), obj);
    }
}
