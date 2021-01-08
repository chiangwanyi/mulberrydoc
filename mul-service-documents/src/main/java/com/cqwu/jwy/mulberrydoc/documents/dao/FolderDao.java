package com.cqwu.jwy.mulberrydoc.documents.dao;

import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsConstant;
import com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsError;
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
    private DocumentsDao documentsDao;
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

    public Folder updateFolder(String uid, Map<String, Object> info)
    {
        return null;
    }

    /**
     * 根据 Hash 获取文件夹
     *
     * @param uid  用户 ID
     * @param hash Hash
     * @return 文件夹
     */
    public Folder queryFolderByHash(String uid, String hash) throws WebException
    {
        // 查询文档空间
        Documents documents = documentsDao.queryDocumentsByUserId(uid);
        if (Objects.nonNull(documents))
        {
            List<Folder> folderList = documents.getFolderList();
            Optional<Folder> folderOpt;
            // 如果查询的文件夹是 根目录别名 $root
            if (Objects.equals(hash, DocumentsConstant.ROOT_FOLDER_HASH_ALIAS))
            {
                folderOpt = folderList
                        .stream()
                        .filter(folder -> Objects.equals(folder.getPath(), DocumentsConstant.ROOT_FOLDER_PATH))
                        .findFirst();
            }
            else
            {
                folderOpt = folderList.stream()
                        .filter(folder -> Objects.equals(folder.getHash(), hash))
                        .findFirst();
            }
            return folderOpt.orElse(null);
        }
        throw new WebException(DocumentsError.DOCUMENTS_NON_EXISTENT);
    }

    /**
     * 查询文件夹的子文件夹
     *
     * @param uid        用户ID
     * @param parentHash 父文件夹 Hash
     * @return 文件夹列表
     */
    public List<Folder> querySubfolder(String uid, String parentHash) throws WebException
    {
        // 查询文档空间
        Documents documents = documentsDao.queryDocumentsByUserId(uid);
        if (Objects.nonNull(documents))
        {
            // 所有文件夹
            List<Folder> allFolders = documents.getFolderList();
            // 如果父文件夹 Hash 是根目录别名
            if (Objects.equals(parentHash, DocumentsConstant.ROOT_FOLDER_HASH_ALIAS))
            {
                Optional<Folder> rootFolderOpt = allFolders.stream()
                        .filter(folder -> Objects.equals(folder.getPath(), DocumentsConstant.ROOT_FOLDER_PATH))
                        .findFirst();
                if (rootFolderOpt.isPresent())
                {
                    Folder rootFolder = rootFolderOpt.get();
                    return allFolders.stream()
                            .filter(folder -> Objects.equals(folder.getParentHash(), rootFolder.getHash()))
                            .collect(Collectors.toList());
                }
            }
            // 判断父文件夹是否存在
            if (allFolders.stream().anyMatch(folder -> Objects.equals(folder.getHash(), parentHash)))
            {
                //过滤出所有 parentHash 满足条件的文件夹
                return allFolders.stream()
                        .filter(folder -> Objects.equals(folder.getParentHash(), parentHash))
                        .collect(Collectors.toList());
            }
            throw new WebException(FolderError.FOLDER_NON_EXISTENT);
        }
        throw new WebException(DocumentsError.DOCUMENTS_NON_EXISTENT);
    }
}
