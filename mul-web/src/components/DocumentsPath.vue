<template>
    <el-row id="documents-path">
        <el-row type="flex" v-show="ready">
            <el-link type="primary" :disabled="currentFolderHash === '$root'"
                     @click="toParentFolder()">返回上一级
            </el-link>
            <p>|</p>
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item>根目录</el-breadcrumb-item>
                <el-breadcrumb-item v-for="item in path" :key="item">{{item}}</el-breadcrumb-item>
            </el-breadcrumb>
        </el-row>
    </el-row>
</template>

<script>
    import DocumentsApi from "@/api/documents";

    export default {
        name: "DocumentsPath",
        data() {
            return {
                folder: {},
                path: []
            }
        },
        methods: {
            setFolderPath() {
                DocumentsApi.getFolderPath(this.currentFolderHash)
                    .then(r => {
                        let res = r.data;
                        if (res.status === 200) {
                            let nameList = res.data;
                            if (nameList.length > 6) {
                                this.path = nameList.slice(0, 5).concat(["...", nameList[nameList.length - 1]])
                            } else {
                                this.path = nameList;
                            }
                        }
                    })
                DocumentsApi.queryFolder(this.currentFolderHash)
                    .then(r => {
                        let res = r.data;
                        if (res.status === 200) {
                            this.folder = res.data.folder;
                        }
                    })
            },
            toParentFolder() {
                this.$router.push(`/documents/${this.folder.parentHash}`)
            }
        },
        created() {
            this.setFolderPath();
        },
        watch: {
            currentFolderHash() {
                this.setFolderPath();
            }
        },
        props: ['currentFolderHash', 'currentFolder', 'ready']
    }
</script>

<style lang="scss" scoped>
    #documents-path {
        margin: 10px 0;

        .el-link {
            font-size: 14px;
            line-height: 14px;
        }

        p {
            display: inline-block;
            font-size: 14px;
            line-height: 14px;
            margin: 0 10px;
            padding: 0;
        }

        .el-breadcrumb {
            font-size: 14px;
            color: #66b1ff !important;

            .el-breadcrumb__item {
                font-size: 14px;
                line-height: 14px;
            }
        }
    }

</style>