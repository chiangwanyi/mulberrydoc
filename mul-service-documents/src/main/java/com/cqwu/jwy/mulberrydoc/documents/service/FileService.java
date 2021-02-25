package com.cqwu.jwy.mulberrydoc.documents.service;

import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;
import com.cqwu.jwy.mulberrydoc.documents.constant.FileError;
import com.cqwu.jwy.mulberrydoc.documents.dao.FileDao;
import com.cqwu.jwy.mulberrydoc.documents.dao.FolderDao;
import com.cqwu.jwy.mulberrydoc.documents.pojo.File;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Folder;
import com.cqwu.jwy.mulberrydoc.documents.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FileService
{
    @Autowired
    private FileDao fileDao;

    @Autowired
    private FolderDao folderDao;

    /**
     * 创建文件
     *
     * @param uid  用户ID
     * @param info 文件信息
     * @return 创建的文件实体
     * @throws WebException 异常
     */
    public File createFile(String uid, File info) throws WebException
    {
        String fileHash = FileUtil.generateFolderHash(uid, info.getFolderHash(), info.getType(), info.getName());
        // 判断文件是否存在
        if (isExistedFile(uid, fileHash))
        {
            throw new WebException(FileError.FILE_ALREADY_EXISTENT);
        }
        File file = new File(uid, info.getFolderHash(), info.getType(), info.getName());
        if (Objects.nonNull(info.getRwStatus()))
        {
            file.setRwStatus(info.getRwStatus());
        }
        if (Objects.nonNull(info.getOwnership()))
        {
            file.setOwnership(info.getOwnership());
        }
        file.setHash(fileHash);
        fileDao.insert(file);
        folderDao.addFile(uid, file.getFolderHash(), fileHash);
        return file;
    }

    /**
     * 查询文件夹的所有文件
     *
     * @param uid        用户ID
     * @param folderHash 文件夹Hash
     * @return 文件列表
     * @throws WebException 异常
     */
    public List<File> queryFiles(String uid, String folderHash) throws WebException
    {
        return fileDao.queryFiles(uid, folderHash);
    }



    /**
     * 判断文件是否存在
     *
     * @param uid      用户ID
     * @param fileHash 文件Hash
     * @return 结果
     */
    public boolean isExistedFile(String uid, String fileHash)
    {
        List<File> files = fileDao.queryAllFiles(uid);
        return files.stream().anyMatch(file -> Objects.equals(file.getHash(), fileHash));
    }
}
