package com.cqwu.jwy.mulberrydoc.documents.pojo;

/**
 * 文件类型
 */
public enum FileType
{
    DOC(1, "文档"),
    MARKDOWN(2, "Markdown"),
    CHART(3, "表格"),
    TEXT(4, "纯文本文档");

    private Integer type;
    private String name;

    FileType(Integer type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}