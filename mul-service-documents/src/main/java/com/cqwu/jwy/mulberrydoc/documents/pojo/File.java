package com.cqwu.jwy.mulberrydoc.documents.pojo;

import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;
import com.cqwu.jwy.mulberrydoc.common.validator.Rule;

import java.util.Date;

/**
 * 文件
 */
public class File
{
    /** 文件标识 */
    private String hash;
    /** 所属用户ID */
    private String uid;
    /** 所属文件夹标识 */
    @Rule(name = "所属文件夹")
    private String folderHash;
    /** 文件类型 */
    @Rule(name = "文件类型")
    private String type;
    /** 文件名称 */
    @Rule(name = "文件名称")
    private String name;
    /**
     * 文件读写状态
     * 0: R  只读
     * 1: RW 可读写
     */
    private Integer rwStatus;
    /**
     * 文件所属状态
     * 0：private   私有
     * 1：public    公开
     * 2：friend    好友共享
     * 3：group:xxx 小组共享
     */
    private Integer ownership;
    /** 创建时间 */
    private Date createdAt;
    /** 修改时间 */
    private Date updatedAt;
    /** 移除时间 */
    private Date deletedAt;

    public File()
    {
    }

    public File(String uid, String folder, String type, String name)
    {
        this.uid = uid;
        this.folderHash = folder;
        this.type = type;
        this.name = name;
        this.rwStatus = FileRwStatus.RW.getValue();
        this.ownership = FileOwnership.PRIVATE.getValue();
        this.createdAt = DateUtil.nowDatetime();
        this.updatedAt = this.createdAt;
        this.deletedAt = null;
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getFolderHash()
    {
        return folderHash;
    }

    public void setFolderHash(String folderHash)
    {
        this.folderHash = folderHash;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
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

    public Integer getRwStatus()
    {
        return rwStatus;
    }

    public void setRwStatus(Integer rwStatus)
    {
        this.rwStatus = rwStatus;
    }

    public Integer getOwnership()
    {
        return ownership;
    }

    public void setOwnership(Integer ownership)
    {
        this.ownership = ownership;
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
}
