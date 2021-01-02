package com.cqwu.jwy.mulberrydoc.auth.constant;

import com.cqwu.jwy.mulberrydoc.common.exception.ErrorMsg;

public enum AuthError implements ErrorMsg
{
    /***/
    USERNAME_CONFLICT(1, "该用户名已被注册"),
    /***/
    LOGIN_FAILED(2, "用户名或密码错误"),
    /***/
    REGISTER_FAILED(3, "注册时发生异常"),
    /***/
    REQUIRE_LOGIN(4, "请登录后重试"),
    /***/
    USER_NOT_FOUND(5, "用户不存在");

    private final Integer code;
    private final String message;

    AuthError(Integer code, String message)
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
