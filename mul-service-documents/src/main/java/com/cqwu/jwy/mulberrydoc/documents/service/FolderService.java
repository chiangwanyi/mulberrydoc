package com.cqwu.jwy.mulberrydoc.documents.service;

import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.documents.dao.FolderDao;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Folder queryFolderByHash(String uid, String hash)
    {
        return folderDao.queryFolderByHash(uid, hash);
    }
}
