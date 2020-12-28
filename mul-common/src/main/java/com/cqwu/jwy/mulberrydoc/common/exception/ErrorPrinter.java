package com.cqwu.jwy.mulberrydoc.common.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class ErrorPrinter
{
    private ErrorPrinter()
    {
    }

    public static String getStackTrace(Throwable e)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(output));
        return "\n" + output.toString();
    }
}
