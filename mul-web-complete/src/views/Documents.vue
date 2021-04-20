<template>
    <div id="documents">
        <DocumentsTools :current-folder-hash="currentFolderHash"
                        :current-folder="currentFolder"
                        :data="data"
                        :selected-item="selectedItem"
                        @refresh="updateCurrentFolder"></DocumentsTools>
        <DocumentsPath :current-folder-hash="currentFolderHash"
                       :ready="ready"></DocumentsPath>
        <DocumentsList :data="data"
                       :ready="ready"
                       @selectItem="handleSelectItem"
                       @clickItem="handleClickItem"></DocumentsList>
    </div>
</template>

<script>
    import DocumentsTools from "../components/DocumentsTools";
    import DocumentsPath from "../components/DocumentsPath";
    import DocumentsList from "../components/DocumentsList";

    import DocumentsApi from "@/api/documents";

    export default {
        name: "Documents",
        data() {
            return {
                ready: false,
                // 包含 子文件夹 和 文件 的列表
                data: [
                    {
                        hash: "",
                        name: "",
                        type: "folder",
                        updatedAt: "",
                        createdAt: "",
                    },
                    {
                        hash: "",
                        name: "",
                        type: "doc",
                        updatedAt: "",
                        createdAt: "",
                    },
                ],
                // 当前文件夹
                currentFolder: {},
                // 当前文件夹Hash
                currentFolderHash: "",
                // 被选中的 item 标识
                selectedItem: [],
            }
        },
        methods: {
            /**
             * 从路由中获取当前文件夹标识
             */
            getCurrentFolderHash() {
                const path = this.$route.path;
                // 当前查询的 文件夹
                let currentFolderHash;
                if (path === "/documents" || path === "/documents/") {
                    currentFolderHash = "$root";
                } else {
                    currentFolderHash = this.$route.params.pathMatch.replace("/", "");
                }
                this.currentFolderHash = currentFolderHash;
            },
            /**
             * 处理添加 Item 事件
             * @param item 对象
             */
            handleAddItem(item) {
                // if (item instanceof Folder) {
                //     console.log("添加了新的 文件夹：", item)
                //     this.subFolders.push(item);
                //     this.foldersCache.push(item);
                //     this.updateData();
                // } else if (item instanceof File) {
                //     console.log("暂不支持")
                // }
            },
            /**
             * 处理选择 Item 事件
             * @param  items
             */
            handleSelectItem(items) {
                this.selectedItem = items;
            },
            handleClickItem(item) {
                if (item.type === "folder") {
                    this.$router.push(`/documents/${item.hash}`)
                } else if (item.type === "doc") {
                    window.open(this.$router.resolve({
                        path: `/doc/${item.hash}`,
                    }).href, '_blank');
                } else if (item.type === "md") {
                    window.open(this.$router.resolve({
                        path: `/md/${item.hash}`,
                    }).href, '_blank');
                }
            },
            updateCurrentFolder() {
                const itemList = [];
                this.getCurrentFolderHash();
                DocumentsApi.querySubfolder(this.currentFolderHash)
                    .then(r => {
                        const res = r.data;
                        if (res.status === 200) {
                            const folderList = res.data;
                            folderList.forEach(el => {
                                itemList.push({
                                    hash: el.hash,
                                    name: el.name,
                                    type: "folder",
                                    favorite: el.favorite,
                                    createdAt: el.createdAt,
                                    updatedAt: el.updatedAt,
                                })
                            })
                            if (this.currentFolderHash === "$root") {
                                this.data = itemList;
                            } else {
                                DocumentsApi.queryFolder(this.currentFolderHash)
                                    .then(r => {
                                        const res = r.data;
                                        if (res.status === 200) {
                                            const fileList = res.data.files;
                                            fileList.forEach(el => {
                                                itemList.push({
                                                    hash: el.hash,
                                                    name: el.name,
                                                    type: el.type,
                                                    rwStatus: el.rwStatus,
                                                    ownership: el.ownership,
                                                    favorite: el.favorite,
                                                    createdAt: el.createdAt,
                                                    updatedAt: el.updatedAt,
                                                })
                                            })
                                            this.data = itemList;
                                        }
                                    })
                            }
                        }
                    });
            }
        },
        created() {
            this.updateCurrentFolder();
            setTimeout(() => {
                this.ready = true;
            }, 500);
        },
        watch: {
            $route() {
                this.updateCurrentFolder();
            }
        },
        components: {DocumentsTools, DocumentsPath, DocumentsList}
    }
</script>

<style lang="scss" scoped>
    #documents {
        padding: 10px;
    }
</style>