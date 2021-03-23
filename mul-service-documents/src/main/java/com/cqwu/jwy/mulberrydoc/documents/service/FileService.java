package com.cqwu.jwy.mulberrydoc.documents.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.serializer.HttpSerializer;
import com.cqwu.jwy.mulberrydoc.common.serializer.response.HttpResponse;
import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;
import com.cqwu.jwy.mulberrydoc.common.util.HttpURLConnectionUtil;
import com.cqwu.jwy.mulberrydoc.documents.constant.FileError;
import com.cqwu.jwy.mulberrydoc.documents.dao.FileDao;
import com.cqwu.jwy.mulberrydoc.documents.dao.FolderDao;
import com.cqwu.jwy.mulberrydoc.documents.pojo.File;
import com.cqwu.jwy.mulberrydoc.documents.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        if (checkName(uid, folderHash, type, name))
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
        Map<String, Object> data = new HashMap<>();
        data.put("key", "73FB6FB50711641F153F1F09D04B6F8A");
        data.put("file", file);
        try
        {
            String res = HttpURLConnectionUtil.post("http://localhost:9003/api/file", JSON.parseObject(JSONObject.toJSONString(data)));
            HttpResponse response = JSONObject.parseObject(res, HttpResponse.class);
            if (response.getStatus() == HttpSerializer.STATUS_OK)
            {
                return file;
            }
            else
            {
                throw new WebException(FileError.CREATE_FILE_FAILED);
            }
        }
        catch (IOException e)
        {
            throw new WebException(FileError.CREATE_FILE_FAILED);
        }
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
     * 查询文件
     *
     * @param fileHash 文件Hash
     * @return 结果
     * @throws WebException 异常
     */
    public File queryFile(String fileHash)
    {
        return fileDao.queryFile(fileHash);
    }

    public void removeFile(String uid, List<String> fileHashes)
    {
        Date date = DateUtil.nowDatetime();
        for (String hash : fileHashes)
        {
            fileDao.removeFile(uid, hash, date);
        }
    }

    /**
     * 修改文件属性
     *
     * @param uid       所属用户ID
     * @param hash      文件Hash
     * @param rwStatus  文件读写状态
     * @param ownership 文件所属状态
     */
    public void updateFileAttr(String uid, String hash, Integer rwStatus, Integer ownership)
    {
        fileDao.updateFileAttr(uid, hash, rwStatus, ownership, DateUtil.nowDatetime());
    }

    public void updateFileName(String uid, String hash, String name) throws Exception
    {
        File file = queryFile(hash);
        if (Objects.nonNull(file))
        {
            if (!checkName(uid, file.getFolderHash(), file.getType(), name))
            {
                fileDao.updateFileName(uid, hash, name, DateUtil.nowDatetime());
            }
            else
            {
                throw new WebException("名称重复");
            }
        }
        else
        {
            throw new Exception("文件不存在");
        }
    }

    public List<File> queryDeletedFiles(String uid)
    {
        return fileDao.queryDeletedFiles(uid);
    }

    public void recoveryFile(String uid, List<String> files)
    {
    }

    public void moveFile(String uid, String toFolderHash, List<String> fileHashes) throws WebException
    {
        List<File> files = fileHashes.stream()
                .map(this::queryFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<String> folders = files.stream().map(File::getFolderHash).distinct().collect(Collectors.toList());
        if (folders.contains(toFolderHash))
        {
            throw new WebException("有文件已经包含在该文件夹内，无法移动");
        }
        Map<String, Integer> counter = new HashMap<>();
        files.forEach(file -> {
            String originName = file.getName();
            String name = originName;
            while (checkName(uid, toFolderHash, file.getType(), name))
            {
                String key = String.format("%s:%s", file.getType(), originName);
                if (!counter.containsKey(key))
                {
                    counter.put(key, 1);
                }
                Integer count = counter.get(key);
                name = String.format("%s (%d)", originName, count);
                counter.put(key, count + 1);
            }
            fileDao.moveFile(uid, file.getHash(), toFolderHash, name, DateUtil.nowDatetime());
        });
    }

    /**
     * 判断文件名称是否存在
     *
     * @param uid        用户ID
     * @param folderHash 文件Hash
     * @param type       文件类型
     * @param name       文件名称
     * @return 结果
     */
    public boolean checkName(String uid, String folderHash, String type, String name)
    {
        // 对应文件夹下的所有文件
        List<File> files = fileDao.queryFiles(uid, folderHash);
        return files.stream()
                .map(file -> String.format("%s:%s", file.getType(), file.getName()))
                .anyMatch(str -> Objects.equals(str, String.format("%s:%s", type, name)));
    }
}
