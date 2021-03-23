package com.cqwu.jwy.mulberrydoc.documents.service;

import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.documents.dao.FolderDao;
import com.cqwu.jwy.mulberrydoc.documents.pojo.File;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
     * 获取文件夹的路径
     *
     * @param uid        用户ID
     * @param folderHash 文件夹Hash
     * @return 文件夹路径
     * @throws WebException 异常
     */
    public List<String> getFolderPath(String uid, String folderHash) throws WebException
    {
        return folderDao.getFolderPath(uid, folderHash);
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

    public List<Folder> queryDeletedFolders(String uid) throws WebException
    {
        return folderDao.queryDeletedFolders(uid);
    }

    public void recoveryFolder(String uid, List<String> folders)
    {

    }

    public boolean moveFolder(String uid, List<String> folderHashes, String newParentFolder) throws WebException
    {
        List<Folder> folders = new ArrayList<>();
        for (String hash : folderHashes)
        {
            Folder folder = queryFolderByHash(uid, hash);
            if (Objects.nonNull(folder))
            {
                folders.add(folder);
            }
        }
        // 移动到的文件夹是自己的父文件夹，不允许
        if (folders.stream().map(Folder::getParentHash).distinct().anyMatch(hash -> Objects.equals(hash, newParentFolder)))
        {
            return false;
        }
        Map<String, Integer> counter = new HashMap<>();
        for (Folder folder : folders)
        {
            String originName = folder.getName();
            String name = originName;
            while (folderDao.isExistedFolderName(uid, name, newParentFolder))
            {
                if (!counter.containsKey(originName))
                {
                    counter.put(originName, 1);
                }
                Integer count = counter.get(originName);
                name = String.format("%s (%d)", originName, count);
                counter.put(originName, count + 1);
            }
            folder.setName(name);
            folderDao.moveFolder(uid, folder, newParentFolder);
        }
        return true;
    }
}
