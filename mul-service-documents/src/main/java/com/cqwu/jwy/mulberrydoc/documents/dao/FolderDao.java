package com.cqwu.jwy.mulberrydoc.documents.dao;

import com.cqwu.jwy.mulberrydoc.common.exception.WebException;
import com.cqwu.jwy.mulberrydoc.common.util.DateUtil;
import com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsConstant;
import com.cqwu.jwy.mulberrydoc.documents.constant.DocumentsError;
import com.cqwu.jwy.mulberrydoc.documents.constant.FolderError;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Documents;
import com.cqwu.jwy.mulberrydoc.documents.pojo.Folder;
import com.cqwu.jwy.mulberrydoc.documents.util.FolderUtil;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件夹持久化Dao
 */
@Component
public class FolderDao
{
    private static final Logger LOG = LoggerFactory.getLogger(FolderDao.class);
    private static final String PARAM_UID = "uid";
//    private static final String PARAM_HASH = "hash";
//    private static final String PARAM_PARENT_HASH = "parentHash";
//    private static final String PARAM_NAME = "name";
//    private static final String PARAM_PATH = "path";
//    private static final String PARAM_IS_FAVORITE = "isFavorite";
//    private static final String PARAM_UPDATED_AT = "updatedAt";
//    private static final String PARAM_DELETED_AT = "deletedAt";

    @Autowired
    private DocumentsDao documentsDao;
    @Autowired
    private MongoTemplate mongo;

    /**
     * 创建文件夹
     *
     * @param uid        用户 ID
     * @param parentHash 父文件夹 Hash
     * @return 文件夹
     */
    public Folder createFolder(String uid, String name, String parentHash) throws WebException
    {
        // 如果 父文件夹 不是根目录
        if (!Objects.equals(parentHash, DocumentsConstant.ROOT_FOLDER_HASH_ALIAS))
        {
            // 先判断 父文件夹 是否存在
            Folder parentFolder = queryFolderByHash(uid, parentHash);
            if (Objects.isNull(parentFolder))
            {
                throw new WebException(FolderError.PARENT_FOLDER_NON_EXISTENT);
            }
        }
        // 待创建文件夹 Hash
        String hash = FolderUtil.generateFolderHash(uid, parentHash, name);
        // 再判断 待创建文件夹 是否存在
        Folder folder = queryFolderByHash(uid, hash);
        if (Objects.nonNull(folder))
        {
            throw new WebException(FolderError.FOLDER_ALREADY_EXISTENT);
        }
        // 创建文件夹
        folder = new Folder(hash, parentHash, name);

        Query query = new Query();
        query.addCriteria(Criteria.where(PARAM_UID).is(uid));
        Update update = new Update().push("folderList", folder);
        mongo.updateFirst(query, update, Documents.class);
        return folder;
    }

    /**
     * 修改文件夹
     *
     * @param uid          用户ID
     * @param updateFolder 更新的信息
     * @return 结果
     * @throws WebException 异常
     */
    public boolean updateFolder(String uid, Folder updateFolder) throws WebException
    {
        // 查询文件夹
        Folder oldFolder = queryFolderByHash(uid, updateFolder.getHash());
        if (Objects.isNull(oldFolder))
        {
            throw new WebException(FolderError.FOLDER_NON_EXISTENT);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where(PARAM_UID).is(uid).and("folderList.hash").is(oldFolder.getHash()));

        Update update = new Update();
        // 文件夹被修改
        boolean flag = false;

        // 处理 收藏标记
        Boolean updateFavorite = updateFolder.getFavorite();
        if (Objects.nonNull(updateFavorite) && !Objects.equals(updateFavorite, oldFolder.getFavorite()))
        {
            flag = true;
            update.set("folderList.$.isFavorite", updateFavorite);
        }

        // 一次修改操作，文件夹名称 和 父文件夹 修改只能存在一种

        String updateFolderName = updateFolder.getName();
        String updateFolderParentHash = updateFolder.getParentHash();
        // 处理 文件夹名称
        if (!StringUtils.isEmpty(updateFolderName)
                && !Objects.equals(updateFolderName, oldFolder.getName())
                && !isExistedFolderName(uid, updateFolderName, oldFolder.getParentHash()))
        {
            flag = true;
            update.set("folderList.$.name", updateFolderName);

            oldFolder.setName(updateFolderName);
        }
        // 处理 文件夹的父文件夹
        else if (!StringUtils.isEmpty(updateFolderParentHash)
                && !Objects.equals(updateFolderParentHash, oldFolder.getParentHash()))
        {
            flag = true;
            update.set("folderList.$.parentHash", updateFolderParentHash);

            oldFolder.setParentHash(updateFolderParentHash);
        }

        // 如果修改了原始数据
        if (flag)
        {
            update.set("folderList.$.updatedAt", DateUtil.nowDatetime());
            UpdateResult result = mongo.updateFirst(query, update, Documents.class);
            return result.getModifiedCount() > 0L;
        }
        return false;
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
        boolean res = true;
        // 查询文件夹
        Folder folder = queryFolderByHash(uid, hash);
        if (Objects.isNull(folder))
        {
            throw new WebException(FolderError.FOLDER_NON_EXISTENT);
        }

        // 查找出该文件夹所有的关联文件夹
        List<Folder> folders = iterativeSearchFolders(uid, folder);

        for (Folder f : folders)
        {
            Criteria criteria = new Criteria();
            criteria.and(PARAM_UID).is(uid);
            criteria.and("folderList.hash").is(f.getHash());
            Query query = new Query(criteria);
            Update update = new Update();
            update.set("folderList.$.deletedAt", DateUtil.nowDatetime());
            UpdateResult result = mongo.updateMulti(query, update, Documents.class);
            long modifiedCount = result.getModifiedCount();
            if (modifiedCount == 0L)
            {
                res = false;
            }
        }
        return res;
    }

    /**
     * 添加文件
     *
     * @param uid        用户ID
     * @param folderHash 文件夹标识
     * @param fileHash   文件标识
     * @return 结果
     * @throws WebException 异常
     */
    public boolean addFile(String uid, String folderHash, String fileHash) throws WebException
    {
        // 查询文件夹
        Folder folder = queryFolderByHash(uid, folderHash);
        if (Objects.isNull(folder))
        {
            throw new WebException(FolderError.FOLDER_NON_EXISTENT);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where(PARAM_UID).is(uid).and("folderList.hash").is(folderHash));
        Update update = new Update();
        update.push("folderList.$.fileList", fileHash);
        UpdateResult result = mongo.updateFirst(query, update, Documents.class);
        return result.getModifiedCount() > 0L;
    }

    /**
     * 根据 Hash 查询文件夹
     *
     * @param uid  用户 ID
     * @param hash Hash
     * @return 文件夹，不存在则返回null
     */
    public Folder queryFolderByHash(String uid, String hash) throws WebException
    {
        // 查询文档空间
        Documents documents = documentsDao.queryDocumentsByUserId(uid);
        if (Objects.nonNull(documents))
        {
            // 所有文件夹
            List<Folder> allFolders = documents.getFolderList().stream()
                    .filter(folder -> Objects.isNull(folder.getDeletedAt()))
                    .collect(Collectors.toList());
            return allFolders.stream()
                    .filter(folder -> Objects.equals(folder.getHash(), hash))
                    .findFirst().orElse(null);
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
            List<Folder> allFolders = documents.getFolderList().stream()
                    .filter(folder -> Objects.isNull(folder.getDeletedAt()))
                    .collect(Collectors.toList());
            // 如果父文件夹 Hash 是根目录别名，直接返回所有文件夹
            if (Objects.equals(parentHash, DocumentsConstant.ROOT_FOLDER_HASH_ALIAS))
            {
                return allFolders.stream().
                        filter(folder -> Objects.equals(folder.getParentHash(), DocumentsConstant.ROOT_FOLDER_HASH_ALIAS))
                        .collect(Collectors.toList());
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

    /**
     * 查找文件夹的所有关联文件夹（包括自己）
     *
     * @param uid           用户ID
     * @param currentFolder 查询的文件夹，如果是根目录则为null
     * @return 所有关联文件夹（包括自己，根目录除外）
     */
    public List<Folder> iterativeSearchFolders(String uid, Folder currentFolder) throws WebException
    {
        // 查询文档空间
        Documents documents = documentsDao.queryDocumentsByUserId(uid);
        if (Objects.nonNull(documents))
        {
            // 所有文件夹
            List<Folder> allFolders = documents.getFolderList().stream()
                    .filter(folder -> Objects.isNull(folder.getDeletedAt()))
                    .collect(Collectors.toList());
            // 如果文件夹是根目录
            if (Objects.isNull(currentFolder))
            {
                return allFolders;
            }

            // 文件夹列表
            List<Folder> result = new ArrayList<>();
            // 处理队列
            Queue<String> queue = new LinkedList<>();
            // 如果该文件夹存在，则入栈
            Optional<Folder> opt = allFolders.stream()
                    .filter(folder -> Objects.equals(folder.getHash(), currentFolder.getHash()))
                    .findFirst();
            opt.ifPresent(folder ->
                          {
                              // 入栈
                              queue.offer(folder.getHash());
                              // 保存
                              result.add(folder);
                          });
            // 栈不为空
            while (!queue.isEmpty())
            {
                String hash = queue.poll();
                // 查找父文件夹为当前文件夹的所有文件夹
                List<Folder> collect = allFolders.stream()
                        .filter(folder -> Objects.equals(folder.getParentHash(), hash))
                        .collect(Collectors.toList());
                // 保存
                result.addAll(collect);
                // 将满足条件的文件夹入栈
                collect.forEach(folder -> queue.offer(folder.getHash()));
            }
            return result;
        }
        throw new WebException(DocumentsError.DOCUMENTS_NON_EXISTENT);
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
        // 查询文档空间
        Documents documents = documentsDao.queryDocumentsByUserId(uid);
        if (Objects.nonNull(documents))
        {
            if (Objects.equals(folderHash, DocumentsConstant.ROOT_FOLDER_HASH_ALIAS))
            {
                return new ArrayList<>();
            }
            // 所有文件夹
            List<Folder> allFolders = documents.getFolderList().stream()
                    .filter(folder -> Objects.isNull(folder.getDeletedAt()))
                    .collect(Collectors.toList());
            Optional<Folder> currentFolderOpt = allFolders.stream()
                    .filter(folder -> Objects.equals(folder.getHash(), folderHash))
                    .findFirst();
            if (currentFolderOpt.isPresent())
            {
                List<String> folderNameList = new ArrayList<>();
                Folder currentFolder = currentFolderOpt.get();
                folderNameList.add(currentFolder.getName());
                while (!Objects.equals(currentFolder.getParentHash(), DocumentsConstant.ROOT_FOLDER_HASH_ALIAS))
                {
                    for (Folder folder : allFolders)
                    {
                        if (Objects.equals(folder.getHash(), currentFolder.getParentHash()))
                        {
                            currentFolder = folder;
                            folderNameList.add(currentFolder.getName());
                            break;
                        }
                    }
                }
                Collections.reverse(folderNameList);
                return folderNameList;
            }
            else
            {
                throw new WebException(FolderError.FOLDER_NON_EXISTENT);
            }
        }
        throw new WebException(DocumentsError.DOCUMENTS_NON_EXISTENT);
    }

    /**
     * 检查文件夹名称是否存在
     *
     * @param uid        用户ID
     * @param name       文件夹名称
     * @param parentHash 父文件夹 Hash
     * @return 结果
     * @throws WebException 异常
     */
    public boolean isExistedFolderName(String uid, String name, String parentHash) throws WebException
    {
        if (StringUtils.isEmpty(name))
        {
            throw new WebException(FolderError.FOLDER_NON_EXISTENT);
        }
        List<Folder> folders = querySubfolder(uid, parentHash);
        return folders.stream().anyMatch(folder -> Objects.equals(name, folder.getName()));
    }
}
