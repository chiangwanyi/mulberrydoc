package com.cqwu.jwy.mulberrydoc.auth.pojo;


import com.cqwu.jwy.mulberrydoc.common.validator.Rule;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable
{
    /** 用户 ID */
    private String id;
    /** 用户名 */
    @Rule(name = "用户名", min = 1, max = 20)
    private String username;
    /** 用户密码 */
    @Rule(name = "密码", min = 8, max = 20)
    private String password;
    /** 创建日期 */
    private Date createdAt;
    /** 最后修改日期 */
    private Date updatedAt;
    /** 移除日期 */
    private Date deletedAt;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
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
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
