package com.cqwu.jwy.mulberrydoc.documents.constant;


import com.cqwu.jwy.mulberrydoc.common.exception.ErrorMsg;

public enum DocumentsError implements ErrorMsg
{
    /***/
    DOCUMENTS_NON_EXISTENT(1, "文档空间不存在"),
    /***/
    FOLDER_NON_EXISTENT(2, "文档空间中不存在该文件夹"),
    /***/
    QUERY_DOCUMENTS_FAILED(3, "查询文档空间失败");

    private final Integer code;
    private final String message;

    DocumentsError(Integer code, String message)
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
