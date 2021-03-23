package com.cqwu.jwy.mulberrydoc.documents.pojo;

/**
 * 文件所属状态
 */
public enum FileOwnership
{
    /** 私有 */
    PRIVATE(0, "private"),
    /** 公开 */
    PUBLIC(1, "public");

    private final Integer value;
    private final String name;

    FileOwnership(Integer value, String name)
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
