package com.cqwu.jwy.mulberrydoc.documents.pojo;

import com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsConstant;
import com.cqwu.jwy.mulberrydoc.documents.util.FolderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户文档空间
 */
public class Documents
{
    /** 用户ID */
    private String uid;
    /** 文件夹列表 */
    private List<Folder> folderList;

    /**
     * 创建文档空间，并创建根目录
     *
     * @param uid 用户 ID
     */
    public Documents(String uid)
    {
        this.uid = uid;
        Folder folder = new Folder(FolderUtil.generateFolderHash(uid, DocumentsConstant.ROOT_FOLDER_PATH));
        List<Folder> folderList = new ArrayList<>();
        folderList.add(folder);
        this.folderList = folderList;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public List<Folder> getFolderList()
    {
        return folderList;
    }

    public void setFolderList(List<Folder> folderList)
    {
        this.folderList = folderList;
    }
}
