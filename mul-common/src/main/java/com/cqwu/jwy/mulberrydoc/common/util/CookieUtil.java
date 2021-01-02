package com.cqwu.jwy.mulberrydoc.common.util;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class CookieUtil
{
    /**
     * 从 Cookie 中获取对应的值
     *
     * @param name    Cookie Name
     * @param cookies Cookie
     * @return Cookie Value
     */
    public static String getCookieValue(String name, Cookie[] cookies) {
        if (Objects.nonNull(cookies) && cookies.length != 0) {
            Optional<Cookie> opt = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findAny();
            if (opt.isPresent()) {
                return opt.get().getValue();
            }
        }
        return null;
    }
}
