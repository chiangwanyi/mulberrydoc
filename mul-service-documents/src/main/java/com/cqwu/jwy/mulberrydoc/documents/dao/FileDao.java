package com.cqwu.jwy.mulberrydoc.documents.dao;

import com.cqwu.jwy.mulberrydoc.documents.pojo.File;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件持久化Dao
 */
@Mapper
public interface FileDao
{
    /** 表名 */
    String TABLE_NAME = " `file` ";

    /** 列名-字段名 映射 */
    String SELECT_SQL = " `hash` hash, "
            + "`uid` uid, "
            + "`folder_hash` folderHash, "
            + "`type` type, "
            + "`name` name, "
            + "`rw_status` rwStatus, "
            + "`ownership` ownership, "
            + "`created_at` createdAt, "
            + "`updated_at` updatedAt, "
            + "`deleted_at` deletedAt ";

    /** 插入列 */
    String INSERT_COLUMN_SQL = " (hash, uid, folder_hash, type, name, rw_status, ownership, created_at, updated_at) ";

    /** 插入字段 */
    String INSERT_FILED_SQL = " (#{hash}, #{uid}, #{folderHash}, #{type}, #{name}, #{rwStatus}, #{ownership}, #{createdAt}, #{updatedAt}) ";

    /**
     * 创建文件
     *
     * @param file 文件
     */
    @Insert("insert into" + TABLE_NAME + INSERT_COLUMN_SQL + "values" + INSERT_FILED_SQL)
    void insert(File file);

    /**
     * 查询用户的所有文件
     *
     * @param uid 用户ID
     * @return 文件列表
     */
    @Select("select" + SELECT_SQL + "from" + TABLE_NAME + "where uid = #{uid}")
    List<File> queryAllFiles(String uid);

    /**
     * 查询用户指定文件夹的所有文件
     *
     * @param uid        用户ID
     * @param folderHash 文件夹Hash
     * @return 文件列表
     */
    @Select("select" + SELECT_SQL + "from" + TABLE_NAME + "where uid = #{arg0} and folder_hash = #{arg1}")
    List<File> queryFiles(String uid, String folderHash);
}
