package com.cqwu.jwy.mulberrydoc.documents.service;

import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.documents.dao.FolderDao;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FolderService
{
    @Autowired
    private FolderDao folderDao;

    /**
     * 创建文件夹
     *
     * @param uid    用户 ID
     * @param folder 文件夹信息
     * @return 文件夹
     * @throws WebException 异常
     */
    public Folder createFolder(String uid, Folder folder) throws WebException
    {
        return folderDao.createFolder(uid, folder.getName(), folder.getParentHash());
    }

    /**
     * 根据文件夹 Hash 查询文件夹
     *
     * @param uid  用户 ID
     * @param hash 文件夹 Hash
     * @return 文件夹
     */
    public Folder queryFolderByHash(String uid, String hash) throws WebException
    {
        return folderDao.queryFolderByHash(uid, hash);
    }

    /**
     * 根据父文件夹 Hash 查询所有子文件夹
     *
     * @param uid        用户ID
     * @param parentHash 父文件夹 Hash
     * @return 子文件夹列表
     */
    public List<Folder> querySubfolderByParentHash(String uid, String parentHash) throws WebException
    {
        return folderDao.querySubfolder(uid, parentHash);
    }

    /**
     * 修改文件夹
     *
     * @param uid          用户ID
     * @param updateFolder 修改信息
     * @return 修改结果
     * @throws WebException 异常
     */
    public boolean updateFolder(String uid, Folder updateFolder) throws WebException
    {
        return folderDao.updateFolder(uid, updateFolder);
    }

    /**
     * 移除文件夹
     *
     * @param uid  用户ID
     * @param hash 文件夹Hash
     * @return 结果
     * @throws WebException 异常
     */
    public boolean removeFolder(String uid, String hash) throws WebException
    {
        return folderDao.removeFolder(uid, hash);
    }
}
