package com.cqwu.jwy.mulberrydoc.auth.dao;

import com.cqwu.jwy.mulberrydoc.auth.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 用户信息持久化Dao
 */
@Mapper
public interface UserDao
{
    /** 表名 */
    String USER_TABLE_NAME = " `user` ";

    /** 列名-字段名 映射 */
    String USER_SELECT_SQL = " `id` id, "
            + "`username` username, "
            + "`avatar` avatar, "
            + "`password` password, "
            + "`created_at` createdAt, "
            + "`updated_at` updatedAt, "
            + "`deleted_at` deletedAt ";

    /** 插入列 */
    String USER_INSERT_COLUMN_SQL = " (id, username, avatar, password, created_at, updated_at) ";

    /** 插入字段 */
    String USER_INSERT_FILED_SQL = " (#{id}, #{username}, #{avatar}, #{password}, #{createdAt}, #{updatedAt}) ";

    /**
     * 查询所有【用户】
     *
     * @return 用户列表
     */
    @Select("select" + USER_SELECT_SQL + "from" + USER_TABLE_NAME)
    List<User> queryAll();

    /**
     * 通过【用户名】查询【用户】
     *
     * @param username 用户名
     * @return 用户
     */
    @Select("select" + USER_SELECT_SQL + "from" + USER_TABLE_NAME + "where username = #{username}")
    User queryUserByUsername(String username);

    /**
     * 通过【用户 ID】查询用户
     *
     * @param id 用户 ID
     * @return 用户
     */
    @Select("select" + USER_SELECT_SQL + "from" + USER_TABLE_NAME + "where id = #{id}")
    User queryUserById(String id);

    /**
     * 添加【用户】
     *
     * @param user 用户
     */
    @Insert("insert into" + USER_TABLE_NAME + USER_INSERT_COLUMN_SQL + "values" + USER_INSERT_FILED_SQL)
    void insert(User user);

    @Update("update" + USER_TABLE_NAME + "set avatar = #{arg1}, updated_at = #{arg2} where id = #{arg0} and deleted_at is null")
    void updateAvatar(String id, String avatar, Date date);
}
