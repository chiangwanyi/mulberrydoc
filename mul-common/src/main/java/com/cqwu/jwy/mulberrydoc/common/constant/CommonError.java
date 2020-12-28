package com.cqwu.jwy.mulberrydoc.common.constant;

import com.cqwu.jwy.mulberrydoc.common.exception.ErrorMsg;

public enum CommonError implements ErrorMsg
{
    /***/
    INCOMPLETE_PARAMETERS(1, "参数不完整"),
    /***/
    INTERNAL_ERROR(2, "内部错误");

    private final Integer code;
    private final String message;

    CommonError(Integer code, String message)
    {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode()
    {
        return this.code;
    }

    @Override
    public String getMessage()
    {
        return this.message;
    }
}
