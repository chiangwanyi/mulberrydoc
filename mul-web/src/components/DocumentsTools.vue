<template>
    <div id="documents-tools">
        <el-row type="flex" justify="space-between">
            <el-col :lg="14" :xl="14">
                <el-button type="primary" size="small" icon="el-icon-upload">上传</el-button>
                <el-button size="small" icon="el-icon-plus" @click="createFormVisible = true">新建
                </el-button>
                <el-button-group class="btn-group" v-show="selectedItemHash !== null && selectedItemHash.length > 0">
                    <el-button size="small" icon="el-icon-share">分享</el-button>
                    <el-button size="small" icon="el-icon-download">下载</el-button>
                    <el-button size="small" icon="el-icon-minus" @click="removeItem">移除</el-button>
                    <el-button size="small">重命名</el-button>
                    <el-button size="small">移动到</el-button>
                    <el-button size="small">复制到</el-button>
                </el-button-group>
            </el-col>
            <el-col :lg="8" :xl="8">
                <el-input placeholder="在当前文件夹中查询" size="small" v-model="searchInfo" class="input-with-select">
                    <el-button slot="append" icon="el-icon-search"></el-button>
                </el-input>
            </el-col>
        </el-row>
        <el-dialog title="新建" :visible.sync="createFormVisible" width="30%">
            <el-form :model="createForm" ref="createForm" :rules="createRules" status-icon label-position="right"
                     label-width="40px">
                <el-form-item label="类型" prop="type">
                    <el-select v-model="createForm.type" placeholder="请选择">
                        <el-option-group
                                v-for="group in options"
                                :key="group.label"
                                :label="group.label">
                            <el-option
                                    v-for="item in group.options"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                <img v-if="item.value === 'folder'" class="create_icon" src="../assets/folder.svg"
                                     alt="">
                                <img v-else-if="item.value === 'doc'" class="create_icon" src="../assets/word.svg"
                                     alt="">
                                <img v-else-if="item.value === 'chart'" class="create_icon" src="../assets/xchart.svg"
                                     alt="">
                                <img v-else-if="item.value === 'markdown'" class="create_icon" src="../assets/ppt.svg"
                                     alt="">
                                <span class="create_label">{{ item.label }}</span>
                            </el-option>
                        </el-option-group>
                    </el-select>
                </el-form-item>
                <el-form-item label="名称" prop="name">
                    <el-input v-model="createForm.name" placeholder="请输入名称"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="createFormVisible = false">取消</el-button>
                <el-button type="primary" @click="createItem" :loading="onCreate">确定</el-button>
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
                    return callback(new Error('请选择类型'));
                }
                if (this.currentFolderHash === "$root" && value !== "folder") {
                    return callback(new Error('根目录只能创建文件夹'));
                }
                callback();
            }
            const validateName = (rule, value, callback) => {
                if (!value) {
                    return callback(new Error('名称不能为空'));
                }
                // let find = this.data.find(item => item.name === value);
                // if (find !== undefined) {
                //     return callback(new Error('该文件/文件夹已存在'));
                // }
                callback();
            }
            return {
                onCreate: false,
                createFormVisible: false,
                createForm: {
                    type: "",
                    name: ""
                },
                options: [
                    {
                        options: [{
                            value: 'folder',
                            label: '文件夹'
                        }]
                    },
                    {
                        label: '文件',
                        options: [{
                            value: 'doc',
                            label: '文档'
                        }
                            // , {
                            //     value: 'chart',
                            //     label: '表格'
                            // }
                            // , {
                            //     value: 'markdown',
                            //     label: 'Markdown'
                            // }
                        ]
                    }],
                searchInfo: "",
                createRules: {
                    type: [
                        {validator: validateType, trigger: 'change'}
                    ],
                    name: [
                        {validator: validateName, trigger: 'blur'}
                    ],
                }
            }
        },
        methods: {
            /**
             * 创建 Item
             */
            createItem() {
                this.$refs['createForm'].validate(valid => {
                    if (valid) {
                        this.onCreate = true;
                        if (this.createForm.type === "folder") {
                            DocumentsApi.createFolder({
                                parentHash: this.currentFolderHash,
                                name: this.createForm.name
                            }).then(r => {
                                let res = r.data
                                if (res.status === 200) {
                                    this.createFormVisible = false;
                                    this.$emit("refresh");
                                    this.$message({
                                        message: res.msg,
                                        type: 'success'
                                    });
                                    this.createForm = {
                                        type: "",
                                        name: ""
                                    }
                                } else {
                                    this.$message.error(res.msg.message);
                                }
                            })
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
                this.$confirm('确认将所选文件移入回收站？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$message({
                        type: 'success',
                        message: '移除成功!'
                    });
                }).catch((e) => {
                    console.error(e)
                });
            }
        },
        props: ["currentFolder", "currentFolderHash", "data", "selectedItemHash"]
    }
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
</style>