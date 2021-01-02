package com.cqwu.jwy.mulberrydoc.common.exception;

public class WebException extends Exception
{
    private ErrorMsg errorMsg;

    @Deprecated
    public WebException(String message)
    {
        super(message);
    }

    public WebException(ErrorMsg errorMsg)
    {
        super(errorMsg.getMessage());
        this.errorMsg = errorMsg;
    }

    public WebException(ErrorMsg errorMsg, Object... obj)
    {
        super(errorMsg.formatMessage(obj));
        this.errorMsg = errorMsg;
    }

    public ErrorMsg getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(ErrorMsg errorMsg)
    {
        this.errorMsg = errorMsg;
    }
}
