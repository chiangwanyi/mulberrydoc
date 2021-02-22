package com.cqwu.jwy.mulberrydoc.documents.pojo;

import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;
import com.cqwu.jwy.mulberrydoc.common.validator.Rule;
import com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsConstant;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 文件夹
 */
public class Folder
{
    /** 文件夹 Hash */
    private String hash;
    /** 父文件夹 Hash */
    @Rule(name = "父文件夹Hash")
    private String parentHash;
    /** 文件夹 名称 */
    @Rule(name = "名称")
    private String name;
    /** 是否标记为 收藏 */
    private Boolean isFavorite;
    /** 文件Hash 列表 */
    private List<String> fileList;
    /** 创建时间 */
    private Date createdAt;
    /** 修改时间 */
    private Date updatedAt;
    /** 移除时间 */
    private Date deletedAt;

    /**
     * 初始化 文件夹
     */
    public Folder()
    {
        this.isFavorite = false;
        this.createdAt = DateUtil.nowDatetime();
        this.updatedAt = this.createdAt;
        this.deletedAt = null;
    }


    public Folder(String hash, String parentHash, String folderName)
    {
        this();
        this.hash = hash;
        this.parentHash = parentHash;
        this.name = folderName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return Objects.equals(hash, folder.hash);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hash);
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public String getParentHash()
    {
        return parentHash;
    }

    public void setParentHash(String parentHash)
    {
        this.parentHash = parentHash;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean getFavorite()
    {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite)
    {
        isFavorite = favorite;
    }

    public List<String> getFileList()
    {
        return fileList;
    }

    public void setFileList(List<String> fileList)
    {
        this.fileList = fileList;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt()
    {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt)
    {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString()
    {
        return "Folder{" +
                "hash='" + hash + '\'' +
                ", parentHash='" + parentHash + '\'' +
                ", name='" + name + '\'' +
                ", isFavorite=" + isFavorite +
                ", fileList=" + fileList +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
