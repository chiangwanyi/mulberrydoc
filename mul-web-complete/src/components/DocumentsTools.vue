<template>
    <div id="documents-tools">
        <el-row type="flex" justify="space-between">
            <el-col :lg="14" :xl="14">
                <el-button
                    type="primary"
                    size="small"
                    icon="el-icon-upload"
                    @click="uploadVisible = true"
                    >上传</el-button
                >
                <el-button
                    size="small"
                    icon="el-icon-plus"
                    @click="createFormVisible = true"
                    >新建
                </el-button>
                <el-button-group
                    class="btn-group"
                    v-show="selectedItem !== null && selectedItem.length > 0"
                >
                    <el-button
                        size="small"
                        icon="el-icon-minus"
                        @click="removeItem"
                        >移除</el-button
                    >
                    <el-button
                        size="small"
                        v-show="selectedItem.length === 1"
                        @click="renameItem"
                        >重命名</el-button
                    >
                    <el-button
                        size="small"
                        v-show="selectedItem.length > 0"
                        icon="el-icon-right"
                        @click="moveTo"
                        >移动到
                    </el-button>
                    <el-button
                        size="small"
                        v-show="
                            selectedItem.length === 1 &&
                            selectedItem[0].type !== 'folder'
                        "
                        @click="showFileAttr"
                        >属性
                    </el-button>
                    <el-button
                        size="small"
                        v-show="
                            selectedItem.length === 1 &&
                            selectedItem[0].type !== 'folder'
                        "
                        @click="downloadFile"
                        >下载
                    </el-button>
                </el-button-group>
            </el-col>
            <!--            <el-col :lg="8" :xl="8">-->
            <!--                <el-input placeholder="在当前文件夹中查询" size="small" v-model="searchInfo" class="input-with-select">-->
            <!--                    <el-button slot="append" icon="el-icon-search"></el-button>-->
            <!--                </el-input>-->
            <!--            </el-col>-->
        </el-row>
        <el-dialog title="新建" :visible.sync="createFormVisible" width="30%">
            <el-form
                :model="createForm"
                ref="createForm"
                :rules="createRules"
                status-icon
                label-position="right"
                label-width="40px"
            >
                <el-form-item label="类型" prop="type">
                    <el-select v-model="createForm.type" placeholder="请选择">
                        <el-option-group
                            v-for="group in options"
                            :key="group.label"
                            :label="group.label"
                        >
                            <el-option
                                v-for="item in group.options"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            >
                                <img
                                    v-if="item.value === 'folder'"
                                    class="create_icon"
                                    src="../assets/folder.svg"
                                    alt=""
                                />
                                <img
                                    v-else-if="item.value === 'doc'"
                                    class="create_icon"
                                    src="../assets/word.svg"
                                    alt=""
                                />
                                <img
                                    v-else-if="item.value === 'chart'"
                                    class="create_icon"
                                    src="../assets/xchart.svg"
                                    alt=""
                                />
                                <img
                                    v-else-if="item.value === 'md'"
                                    class="create_icon"
                                    src="../assets/ppt.svg"
                                    alt=""
                                />
                                <span class="create_label">{{
                                    item.label
                                }}</span>
                            </el-option>
                        </el-option-group>
                    </el-select>
                </el-form-item>
                <el-form-item label="名称" prop="name">
                    <el-input
                        v-model="createForm.name"
                        placeholder="请输入名称"
                    ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="createFormVisible = false">取消</el-button>
                <el-button
                    type="primary"
                    @click="createItem"
                    :loading="onCreate"
                    >确定</el-button
                >
            </div>
        </el-dialog>
        <el-dialog title="移动到" :visible.sync="moveToVisible" width="30%">
            <el-tree
                :props="props"
                lazy
                :load="loadSubFolder"
                :highlight-current="true"
                @node-click="handleFolderMove"
            ></el-tree>
            <div slot="footer" class="dialog-footer">
                <el-button @click="moveToVisible = false">取消</el-button>
                <el-button type="primary" @click="moveItem" :loading="onCreate"
                    >确定</el-button
                >
            </div>
        </el-dialog>
        <el-dialog
            title="修改文件属性"
            :visible.sync="fileAttributeVisible"
            width="30%"
        >
            <el-form :model="fileAttrForm">
                <el-form-item label="读写状态">
                    <el-radio-group v-model="fileAttrForm.rwStatus">
                        <el-radio label="0">只读</el-radio>
                        <el-radio label="1">读写</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="所属状态">
                    <el-radio-group v-model="fileAttrForm.ownership">
                        <el-radio label="0">私有</el-radio>
                        <el-radio label="1">公开</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="small" @click="fileAttributeVisible = false"
                    >取消</el-button
                >
                <el-button
                    size="small"
                    type="primary"
                    @click="updateFileAttr"
                    :loading="onFileAttr"
                    >确定
                </el-button>
            </div>
        </el-dialog>
        <el-dialog title="上传文件" :visible.sync="uploadVisible" width="50%">
            <el-upload
                class="upload-demo"
                drag
                action="http://localhost:12000/api/file/upload"
                accept=".md,.rc"
                :limit="1"
                :auto-upload="false"
                :with-credentials="true"
                ref="upload"
            >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">
                    将文件拖到此处，或<em>点击上传</em>
                </div>
                <div class="el-upload__tip" slot="tip">
                    只能上传rc/txt文件，且不超过500kb
                </div>
            </el-upload>
            <div slot="footer" class="dialog-footer">
                <el-button @click="uploadVisible = false">取消</el-button>
                <el-button
                    type="primary"
                    @click="submitUpload"
                    :loading="onUpload"
                    >确定</el-button
                >
            </div>
        </el-dialog>
    </div>
</template>

<script>
// import * as FolderAPI from "../api/folder";
// import Folder from "../module/entity/Folder";

import DocumentsApi from "@/api/documents";

export default {
    name: "DocumentsTools",
    data() {
        const validateType = (rule, value, callback) => {
            if (!value) {
                return callback(new Error("请选择类型"));
            }
            if (this.currentFolderHash === "$root" && value !== "folder") {
                return callback(new Error("根目录只能创建文件夹"));
            }
            callback();
        };
        const validateName = (rule, value, callback) => {
            if (!value) {
                return callback(new Error("名称不能为空"));
            }
            // let find = this.data.find(item => item.name === value);
            // if (find !== undefined) {
            //     return callback(new Error('该文件/文件夹已存在'));
            // }
            callback();
        };
        return {
            onCreate: false,
            onFileAttr: false,
            onUpload: false,
            moveToVisible: false,
            createFormVisible: false,
            fileAttributeVisible: false,
            uploadVisible: false,
            createForm: {
                type: "",
                name: "",
            },
            fileAttrForm: {
                rwStatus: "1",
                ownership: "1",
            },
            options: [
                {
                    options: [
                        {
                            value: "folder",
                            label: "文件夹",
                        },
                    ],
                },
                {
                    label: "文件",
                    options: [
                        {
                            value: "doc",
                            label: "文档",
                        },
                        {
                            value: "md",
                            label: "Markdown",
                        },
                    ],
                },
            ],
            searchInfo: "",
            createRules: {
                type: [{ validator: validateType, trigger: "change" }],
                name: [{ validator: validateName, trigger: "blur" }],
            },
            props: {
                children: "subfolder",
                label: "name",
            },

            treeFolder: null,
            treeResolve: null,

            selectedMoveToFolderHash: null,
        };
    },
    methods: {
        downloadFile() {
            let file = this.selectedItem[0];
            DocumentsApi.downloadFile(file.hash).then((res) => {
                //定义文件名等相关信息
                const blob = res.data;
                const reader = new FileReader();
                reader.readAsDataURL(blob);
                reader.onload = (e) => {
                    const a = document.createElement("a");
                    let type = "";
                    if (file.type === "doc") {
                        type = "rc";
                    } else if (file.type === "md") {
                        type = "md";
                    }
                    a.download = `${file.name}.${type}`;
                    a.href = e.target.result;
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                };
            });
        },

        submitUpload() {
            let formdata = new FormData();
            formdata.append("file", this.$refs.upload.uploadFiles[0].raw);
            DocumentsApi.uploadFile(formdata, this.currentFolderHash).then(
                (r) => {
                    let res = r.data;
                    if (res.status === 200) {
                        this.uploadVisible = false;
                        this.$refs.upload.clearFiles();
                        this.$emit("refresh");
                        this.$message({
                            type: "success",
                            message: "上传文件成功",
                        });
                    } else if (res.status == 400) {
                        console.log(res);
                        this.$message.error(res.msg.message);
                    }
                }
            );
        },
        moveItem() {
            let folders = [];
            let files = [];
            this.selectedItem.forEach((el) => {
                if (el.type === "folder") {
                    folders.push(el.hash);
                } else {
                    files.push(el.hash);
                }
            });
            DocumentsApi.moveItemsTo(
                this.selectedMoveToFolderHash,
                folders,
                files
            ).then((r) => {
                if (r.data.status === 200) {
                    this.moveToVisible = false;
                    this.$emit("refresh");
                    this.$message({
                        type: "success",
                        message: "移动成功！",
                    });
                }
            });
        },
        moveTo() {
            this.moveToVisible = true;
            if (this.treeFolder !== null) {
                this.treeFolder.childNodes = [];
                this.loadSubFolder(this.treeFolder, this.treeResolve);
            }
        },
        handleFolderMove(data) {
            this.selectedMoveToFolderHash = data.hash;
        },
        loadSubFolder(folder, resolve) {
            let selectedFolders = this.selectedItem
                .map((item) => {
                    if (item.type === "folder") {
                        return item.hash;
                    }
                })
                .filter((hash) => hash !== undefined);
            if (folder.level === 0) {
                this.treeFolder = folder;
                this.treeResolve = resolve;
                return resolve([{ name: "根目录", hash: "$root" }]);
            } else {
                let hash = folder.data.hash;
                DocumentsApi.querySubfolder(hash).then((r) => {
                    let res = r.data;
                    if (res.status === 200) {
                        let subfolder = res.data;
                        let result = subfolder
                            .map((folder) => {
                                if (!selectedFolders.includes(folder.hash)) {
                                    return {
                                        name: folder.name,
                                        hash: folder.hash,
                                    };
                                }
                            })
                            .filter((item) => item !== undefined);
                        setTimeout(() => {
                            resolve(result);
                        }, 100);
                    }
                });
            }
        },
        /**
         * 创建 Item
         */
        createItem() {
            this.$refs["createForm"].validate((valid) => {
                if (valid) {
                    this.onCreate = true;
                    if (this.createForm.type === "folder") {
                        DocumentsApi.createFolder({
                            parentHash: this.currentFolderHash,
                            name: this.createForm.name,
                        }).then((r) => {
                            let res = r.data;
                            if (res.status === 200) {
                                this.createFormVisible = false;
                                this.$emit("refresh");
                                this.$message({
                                    message: res.msg,
                                    type: "success",
                                });
                                this.createForm = {
                                    type: "",
                                    name: "",
                                };
                            } else {
                                this.$message.error(res.msg.message);
                            }
                        });
                    } else if (this.createForm.type === "doc") {
                        DocumentsApi.createFile({
                            folderHash: this.currentFolderHash,
                            name: this.createForm.name,
                            type: this.createForm.type,
                        }).then((r) => {
                            let res = r.data;
                            if (res.status === 200) {
                                this.createFormVisible = false;
                                this.$emit("refresh");
                                this.$message({
                                    message: res.msg,
                                    type: "success",
                                });
                                this.createForm = {
                                    type: "",
                                    name: "",
                                };
                            } else {
                                this.$message.error(res.msg.message);
                            }
                        });
                    } else if (this.createForm.type === "md") {
                        DocumentsApi.createFile({
                            folderHash: this.currentFolderHash,
                            name: this.createForm.name,
                            type: this.createForm.type,
                        }).then((r) => {
                            let res = r.data;
                            if (res.status === 200) {
                                this.createFormVisible = false;
                                this.$emit("refresh");
                                this.$message({
                                    message: res.msg,
                                    type: "success",
                                });
                                this.createForm = {
                                    type: "",
                                    name: "",
                                };
                            } else {
                                this.$message.error(res.msg.message);
                            }
                        });
                    }
                    this.onCreate = false;
                } else {
                    return false;
                }
            });
        },
        /**
         * 移除 Item
         */
        removeItem() {
            this.$confirm("确认将所选移入回收站？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    let folders = [];
                    let files = [];
                    this.selectedItem.forEach((el) => {
                        if (el.type === "folder") {
                            folders.push(el.hash);
                        } else {
                            files.push(el.hash);
                        }
                    });
                    DocumentsApi.removeItems(folders, files).then((r) => {
                        let res = r.data;
                        console.log(res);
                        if (res.status === 200) {
                            this.$emit("refresh");
                            this.$message({
                                type: "success",
                                message: "移除成功!",
                            });
                        }
                    });
                })
                .catch((e) => {
                    console.error(e);
                });
        },
        renameItem() {
            let folderHash = null;
            let fileHash = null;
            this.selectedItem.forEach((el) => {
                if (el.type === "folder") {
                    folderHash = el.hash;
                } else {
                    fileHash = el.hash;
                }
            });
            if (folderHash !== null) {
                this.data.forEach((f) => {
                    if (f.hash === folderHash) {
                        this.$prompt("请输入名称", "重命名", {
                            confirmButtonText: "确定",
                            cancelButtonText: "取消",
                            inputErrorMessage: "名称已存在",
                            inputValue: f.name,
                        }).then(({ value }) => {
                            f.name = value;
                            DocumentsApi.updateFolder(f).then((r) => {
                                let res = r.data;
                                if (res.status === 200) {
                                    this.$message({
                                        type: "success",
                                        message: "重命名文件夹成功",
                                    });
                                    this.$emit("refresh");
                                }
                            });
                        });
                    }
                });
            } else {
                this.data.forEach((f) => {
                    if (f.hash === fileHash) {
                        this.$prompt("请输入名称", "重命名", {
                            confirmButtonText: "确定",
                            cancelButtonText: "取消",
                            inputErrorMessage: "名称已存在",
                            inputValue: f.name,
                        }).then(({ value }) => {
                            DocumentsApi.updateFileName(fileHash, value).then(
                                (r) => {
                                    let res = r.data;
                                    console.log(res);
                                    if (res.status === 200) {
                                        this.$message({
                                            type: "success",
                                            message: "重命名文件成功",
                                        });
                                        this.$emit("refresh");
                                    }
                                }
                            );
                        });
                    }
                });
            }
        },
        updateFileAttr() {
            DocumentsApi.updateFileAttr(
                this.selectedItem[0].hash,
                parseInt(this.fileAttrForm.rwStatus),
                parseInt(this.fileAttrForm.ownership)
            ).then((r) => {
                let res = r.data;
                if (res.status === 200) {
                    this.fileAttributeVisible = false;
                    this.$message({
                        type: "success",
                        message: "修改成功",
                    });
                    this.$emit("refresh");
                }
            });
        },
        showFileAttr() {
            this.fileAttrForm.rwStatus = `${this.selectedItem[0].rwStatus}`;
            this.fileAttrForm.ownership = `${this.selectedItem[0].ownership}`;
            this.fileAttributeVisible = true;
        },
    },
    props: ["currentFolder", "currentFolderHash", "data", "selectedItem"],
};
</script>

<style scoped lang="scss">
.create_icon {
    float: left;
    width: 28px;
    height: 28px;
}

.create_label {
    float: left;
    margin-left: 4px;
}

.el-button-group {
    margin-left: 10px;
}

.upload-demo {
    text-align: center;
}
</style>