package com.cqwu.jwy.mulberrydoc.documents.constant;

import com.cqwu.jwy.mulberrydoc.common.exception.ErrorMsg;

public enum FolderError implements ErrorMsg
{
    /***/
    PARENT_FOLDER_NON_EXISTENT(1, "父文件夹不存在"),
    /***/
    FOLDER_NON_EXISTENT(2, "文件夹不存在"),
    /***/
    FOLDER_ALREADY_EXISTENT(3, "文件夹已存在"),
    /***/
    UPDATE_ROOT_FOLDER_FAILED(4, "不能修改根目录"),
    /***/
    UPDATE_FOLDER_IGNORE(5, "无修改内容"),
    /***/
    EMPTY_FOLDER_NAME(6, "文件夹名称不能为空"),
    /***/
    REMOVE_FOLDER_FAILED(7, "移除文件夹失败");

    private final Integer code;
    private final String message;

    FolderError(Integer code, String message)
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
