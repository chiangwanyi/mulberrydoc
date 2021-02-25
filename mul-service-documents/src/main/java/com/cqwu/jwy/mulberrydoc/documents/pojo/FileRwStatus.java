package com.cqwu.jwy.mulberrydoc.documents.pojo;

/**
 * 文件读写状态
 */
public enum FileRwStatus
{
    /** 只读 */
    R(0, "R"),
    /** 可读写 */
    RW(1, "RW");

    private final Integer value;
    private final String name;

    FileRwStatus(Integer value, String name)
    {
        this.value = value;
        this.name = name;
    }

    public Integer getValue()
    {
        return value;
    }

    public String getName()
    {
        return name;
    }
}
