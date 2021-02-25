package com.cqwu.jwy.mulberrydoc.documents.constant;

import com.cqwu.jwy.mulberrydoc.common.exception.ErrorMsg;

public enum FileError implements ErrorMsg
{
    /***/
    FILE_ALREADY_EXISTENT(1, "文件已存在");

    private final Integer code;
    private final String message;

    FileError(Integer code, String message)
    {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode()
    {
        return code;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
