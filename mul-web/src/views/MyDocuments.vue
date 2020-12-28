<template>
    <div class="documents" id="documents">
        <DocumentsTools :current-folder="currentFolder"
                        :data="data"
                        :selected-item-hash="selectedItemHash"
                        @addItem="handleAddItem"
        ></DocumentsTools>
        <DocumentsPath></DocumentsPath>
        <DocumentsList :data="data"
                       :ready="ready"
                       @selectItem="handleSelectItem"
        ></DocumentsList>
    </div>
</template>

<script>
    import * as Documents from "../api/documents";

    import DocumentsTools from "../components/DocumentsTools";
    import DocumentsPath from "../components/DocumentsPath";
    import DocumentsList from "../components/DocumentsList";

    import Folder from "../module/entity/Folder";

    export default {
        name: "MyDocuments",
        data() {
            return {
                ready: false,
                // 包含 子文件夹 和 文件 的列表
                data: [
                    {
                        name: "",
                        updatedAt: "",
                        createdAt: "",
                    },
                    {
                        name: "",
                        updatedAt: "",
                        createdAt: "",
                    },
                ],
                // 当前文件夹
                currentFolder: {},
                // 当前文件夹的所有 子文件夹
                subFolders: [],
                // 文件夹列表缓存
                foldersCache: [],
                // 已查询的 文件夹标识列表
                queriedFoldersHash: [],
                // 被选中的 item 标识
                selectedItemHash: [],
            }
        },
        methods: {
            /**
             * 从路由中获取当前文件夹标识
             */
            getCurrentFolderHash() {
                let path = this.$route.path;
                // 当前查询的 文件夹
                let currentFolderHash;
                if (path === "/documents" || path === "/documents/") {
                    currentFolderHash = "$root";
                } else {
                    currentFolderHash = this.$route.params.pathMatch.replace("/", "");
                }
                return currentFolderHash;
            },
            /**
             * 查询文件夹
             */
            queryFolderAndUpdateCache(hash) {
                return new Promise((resolve, reject) => {
                    Documents.getDocuments(hash)
                        .then(r => {
                            let res = r.data;
                            console.log(res);
                            if (res.status === 200) {
                                let data = res.data;
                                // 标记 当前文件夹 为 已查询
                                this.queriedFoldersHash.push(hash);
                                // 保存 当前文件夹
                                this.currentFolder = new Folder(
                                    data.currentFolder.hash,
                                    data.currentFolder.parentHash,
                                    data.currentFolder.name,
                                    data.currentFolder.path,
                                    data.currentFolder.depth,
                                    data.currentFolder.isFavorite,
                                    data.currentFolder.fileList,
                                    data.currentFolder.createdAt,
                                    data.currentFolder.updatedAt,
                                    data.currentFolder.deletedAt
                                );
                                // 保存 子文件夹列表
                                this.subFolders = data.subFolders.map(folder => {
                                    return new Folder(
                                        folder.hash,
                                        folder.parentHash,
                                        folder.name,
                                        folder.path,
                                        folder.depth,
                                        folder.isFavorite,
                                        folder.fileList,
                                        folder.createdAt,
                                        folder.updatedAt,
                                        folder.deletedAt
                                    )
                                });
                                // 更新 文件夹列表缓存
                                this.updateFoldersCache(this.subFolders.concat(this.currentFolder));

                                this.updateData();
                                resolve(true);
                            } else {
                                reject(res);
                            }
                        })
                        .catch(err => {
                            reject(err);
                        });
                });
            },
            /**
             * 更新 文件夹列表缓存
             * @param {Array} folders
             */
            updateFoldersCache(folders) {
                folders.forEach(folder => {
                    let tmp = this.foldersCache.find(item => {
                        return item.hash === folder.hash;
                    });
                    if (tmp === undefined) {
                        this.foldersCache.push(folder);
                    }
                })
            },
            /**
             * 更新 Data
             */
            updateData() {
                let data = [];
                this.subFolders.forEach(folder => {
                    data.push(folder);
                })
                this.data = data;
            },
            /**
             * 处理添加 Item 事件
             * @param item 对象
             */
            handleAddItem(item) {
                if (item instanceof Folder) {
                    console.log("添加了新的 文件夹：", item)
                    this.subFolders.push(item);
                    this.foldersCache.push(item);
                    this.updateData();
                } else if (item instanceof File) {
                    console.log("暂不支持")
                }
            },
            /**
             * 处理选择 Item 事件
             * @param {[Object]} items
             */
            handleSelectItem(items) {
                this.selectedItemHash = [];
                if (items !== null && items.length !== 0) {
                    items.forEach(item => {
                        this.selectedItemHash.push(item.hash);
                    })
                }
            }
        },
        created() {
            this.foldersCache = [];
            // 当前查询的 文件夹
            let currentFolderHash = this.getCurrentFolderHash();
            console.log("起始查询文件夹：", currentFolderHash)
            this.queryFolderAndUpdateCache(currentFolderHash)
                .then(() => {
                    this.ready = true;
                })
        },
        mounted() {

        },
        watch: {
            '$route'() {
                let currentFolderHash = this.getCurrentFolderHash();
                console.log("准备切换文件夹：", currentFolderHash);
            }
        },
        components: {DocumentsTools, DocumentsPath, DocumentsList}
    }
</script>

<style scoped lang="scss">

</style>