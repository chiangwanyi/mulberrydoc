package com.cqwu.jwy.mulberrydoc.documents.pojo;

/**
 * 文件所属状态
 */
public enum FileOwnership
{
    /** 私有 */
    PRIVATE(0, "private"),
    /** 公开 */
    PUBLIC(1, "public"),
    /** 好友共享 */
    FRIEND(2, "friend"),
    /** 小组共享 */
    GROUP(3, "group:%s");

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
