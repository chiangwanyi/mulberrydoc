package com.cqwu.jwy.mulberrydoc.documents.dao;

import com.cqwu.jwy.mulberrydoc.documents.pojo.File;
import org.apache.ibatis.annotations.*;

import java.util.Date;
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
    @Select("select" + SELECT_SQL + "from" + TABLE_NAME + "where uid = #{uid} and deleted_at is null")
    List<File> queryAllFiles(String uid);

    /**
     * 查询文件
     *
     * @param hash 文件Hash
     * @return 文件
     */
    @Select("select" + SELECT_SQL + "from" + TABLE_NAME + "where hash = #{hash} and deleted_at is null")
    File queryFile(String hash);

    @Select("select" + SELECT_SQL + "from" + TABLE_NAME + "where hash = #{hash} and deleted_at is not null")
    File queryDeletedFile(String hash);

    /**
     * 查询用户指定文件夹的所有文件
     *
     * @param uid        用户ID
     * @param folderHash 文件夹Hash
     * @return 文件列表
     */
    @Select("select" + SELECT_SQL + "from" + TABLE_NAME + "where uid = #{arg0} and folder_hash = #{arg1} and deleted_at is null")
    List<File> queryFiles(String uid, String folderHash);

    @Update("update" + TABLE_NAME + "set deleted_at = #{arg2} where uid = #{arg0} and hash = #{arg1} and deleted_at is null")
    void removeFile(String uid, String hash, Date date);

    @Select("select" + SELECT_SQL + "from" + TABLE_NAME + "where uid = #{uid} and deleted_at is not null")
    List<File> queryDeletedFiles(String uid);

    /**
     * 修改文件属性
     *
     * @param uid       所属用户ID
     * @param hash      文件Hash
     * @param rwStatus  文件读写状态
     * @param ownership 文件所属状态
     * @param date      文件修改日期
     */
    @Update("update" + TABLE_NAME + "set rw_status = ${arg2}, ownership = ${arg3}, updated_at = #{arg4} where uid = #{arg0} and hash = #{arg1} and deleted_at is null")
    void updateFileAttr(String uid, String hash, Integer rwStatus, Integer ownership, Date date);

    /**
     * 修改文件名称
     *
     * @param uid  所属用户ID
     * @param hash 文件Hash
     * @param name 文件名称
     * @param date 修改时间
     */
    @Update("update" + TABLE_NAME + "set name = '${arg2}', updated_at = #{arg3} where uid = #{arg0} and hash = #{arg1} and deleted_at is null")
    void updateFileName(String uid, String hash, String name, Date date);

    @Update("update" + TABLE_NAME + "set folder_hash = '${arg2}', name = '${arg3}', updated_at = #{arg4} where uid = #{arg0} and hash = #{arg1}")
    void moveFile(String uid, String hash, String toFolderHash, String newName, Date date);

    @Update("update" + TABLE_NAME + "set updated_at = #{arg2}, deleted_at = null where uid = #{arg0} and hash = #{arg1} and deleted_at is not null")
    void recoveryFile(String uid, String hash, Date date);

    @Delete("delete from"+ TABLE_NAME + "where uid = #{arg0} and hash = #{arg1}")
    void deletedFile(String uid, String hash);

    @Update("update" + TABLE_NAME + "set updated_at = #{arg1} where hash = #{arg0}")
    void writeFile(String hash, Date date);
}
