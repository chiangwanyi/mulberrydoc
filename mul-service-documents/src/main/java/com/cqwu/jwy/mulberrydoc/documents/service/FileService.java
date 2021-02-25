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
        String folderHash = info.getFolderHash();
        String type = info.getType();
        String name = info.getName();
        String fileHash = FileUtil.generateFolderHash(uid, folderHash, type, name);
        // 判断文件是否存在
        if (isExistedFile(uid, folderHash, type, name))
        {
            throw new WebException(FileError.FILE_ALREADY_EXISTENT);
        }
        File file = new File(uid, folderHash, type, name);
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
     * @param uid        用户ID
     * @param folderHash 文件Hash
     * @param type       文件类型
     * @param name       文件名称
     * @return 结果
     */
    public boolean isExistedFile(String uid, String folderHash, String type, String name)
    {
        // 对应文件夹下的所有文件
        List<File> files = fileDao.queryFiles(uid, folderHash);
        return files.stream()
                .map(file -> String.format("%s:%s", file.getType(), file.getName()))
                .anyMatch(str -> Objects.equals(str, String.format("%s:%s", type, name)));
    }
}
