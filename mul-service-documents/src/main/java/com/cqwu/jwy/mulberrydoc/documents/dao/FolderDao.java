package com.cqwu.jwy.mulberrydoc.documents.dao;

import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsConstant;
import com.cqwu.jwy.mulberrydoc.documents.constant.FolderError;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Documents;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Folder;
import com.cqwu.jwy.mulberrydoc.documents.util.FolderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件夹持久化Dao
 */
@Component
public class FolderDao
{
    @Autowired
    private MongoTemplate mongo;

    /**
     * 创建 文件夹
     *
     * @param uid              用户 ID
     * @param parentFolderHash 父文件夹 Hash
     * @return 文件夹
     */
    public Folder createFolder(String uid, String name, String parentFolderHash) throws WebException
    {
        // 先判断 父文件夹 是否存在
        Folder parentFolder = queryFolderByHash(uid, parentFolderHash);
        if (Objects.isNull(parentFolder))
        {
            throw new WebException(FolderError.PARENT_FOLDER_NON_EXISTENT);
        }
        String path;
        // 待创建文件夹 路径
        if (parentFolder.getPath().equals(DocumentsConstant.ROOT_FOLDER_PATH))
        {
            path = parentFolder.getPath() + name;
        }
        else
        {
            path = parentFolder.getPath() + DocumentsConstant.ROOT_FOLDER_PATH + name;
        }
        // 待创建文件夹 Hash
        String hash = FolderUtil.generateFolderHash(uid, path);
        // 再判断 待创建文件夹 是否存在
        Folder folder = queryFolderByHash(uid, hash);
        if (Objects.nonNull(folder))
        {
            throw new WebException(FolderError.FOLDER_ALREADY_EXISTENT);
        }
        // 创建文件夹
        folder = new Folder(hash, parentFolder, name, path);

        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(uid));
        Update update = new Update().push("folderList", folder);
        mongo.updateFirst(query, update, Documents.class);
        return folder;
    }

    /**
     * 根据 Hash 获取文件夹
     *
     * @param uid  用户 ID
     * @param hash Hash
     * @return 文件夹
     */
    public Folder queryFolderByHash(String uid, String hash)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(uid));
        List<Documents> documentsList = mongo.find(query, Documents.class);
        if (!CollectionUtils.isEmpty(documentsList))
        {
            Documents documents = documentsList.get(0);
            List<Folder> folderList = documents.getFolderList();
            Optional<Folder> folderOpt = folderList.stream().filter(folder -> folder.getHash().equals(hash)).findFirst();
            return folderOpt.orElse(null);
        }
        return null;
    }

    /**
     * 查询文件夹的子文件夹
     *
     * @param uid   用户 ID
     * @param hash  起始文件夹 Hash
     * @param depth 查询深度
     * @return 文件夹列表
     */
    public List<Folder> querySubfolder(String uid, String hash, int depth)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(uid));
        List<Documents> documentsList = mongo.find(query, Documents.class);
        if (!CollectionUtils.isEmpty(documentsList))
        {
            Documents documents = documentsList.get(0);
            // 所有文件夹
            List<Folder> allFolders = documents.getFolderList();
            // 最终要返回的文件列表
            Set<Folder> result = new HashSet<>();

            Optional<Folder> currentFolderOpt = allFolders
                    .stream()
                    .filter(folder -> folder.getHash().equals(hash)).findFirst();
            // 查找 当前文件夹
            if (currentFolderOpt.isPresent())
            {
                Folder currentFolder = currentFolderOpt.get();
                // 保存
                result.add(currentFolder);
                Set<Folder> folders = new HashSet<>();
                folders.add(currentFolder);
                for (int i = 0; i < depth; i++)
                {
                    // 遍历
                    Set<Folder> tmp = new HashSet<>();
                    for (Folder folder : folders)
                    {
                        // 找出当前遍历 文件夹 的子文件夹
                        tmp.addAll(allFolders
                                           .stream()
                                           .filter(f -> Objects.nonNull(f.getParentHash()) && f.getParentHash().equals(folder.getHash()))
                                           .collect(Collectors.toSet()));
                    }
                    // 如果没有找到，则跳出循环
                    if (tmp.isEmpty())
                    {
                        break;
                    }
                    else
                    {
                        folders.clear();
                        result.addAll(tmp);
                        folders.addAll(tmp);
                    }
                }
                return new ArrayList<>(result);
            }
        }
        return null;
    }
}
