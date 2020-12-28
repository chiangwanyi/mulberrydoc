<template>
    <div id="folder-list">
        <div class="operator">
            <el-button icon="el-icon-back"></el-button>
            <el-button icon="el-icon-right"></el-button>
        </div>
        <el-row type="flex" class="list">
            <el-col :xs="24" :sm="12" :md="12" :lg="6" :xl="6" v-for="item in data" :key="item.hash">
                <el-row type="flex" class="item">
                    <el-col :span="20">
                        <div class="info" @click.stop="onFolderClick(item.hash)">
                            <img v-if="item.is_favorite" src="../assets/folder-favorite.svg" alt="收藏">
                            <img v-else src="../assets/folder.svg" alt="普通">
                            <p>{{item.path.replace(currentFolder.path, "").replace("/", "")}}</p>
                        </div>
                    </el-col>
                    <el-col :span="4">
                        <el-dropdown class="operator" trigger="click">
                            <i class="el-icon-arrow-up"></i>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item>新建</el-dropdown-item>
                                <el-dropdown-item icon="el-icon-check" divided>移除</el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
    </div>
</template>

<script>
    export default {
        name: "FolderList",
        data() {
            return {
                folderIcon: {
                    normal: "../assets/folder.svg",
                    fav: "../assets/folder-favorite.svg",
                },
                currentPath: this.currentFolder.path
            }
        },
        methods: {
            onFolderClick(path) {
                this.$router.push(`/documents/${path}`);
            },
        },
        watch: {
            'currentFolder'() {
                this.currentPath = this.currentFolder.path;
            }
        },
        props: ['title', 'data', 'currentFolder']
    }
</script>

<style lang="scss" scoped>
    #folder-list {
        .operator {
            height: 60px;
            line-height: 60px;

            .el-button {
                margin-left: 6px;
            }
        }

        .list {
            display: flex;
            flex-direction: row;
            flex-wrap: wrap;

            .item {
                margin-right: 10px;
                margin-bottom: 10px;
                cursor: pointer;
                border: 1px solid #ebeef5;
                background-color: #fff;
                color: #303133;
                transition: .3s;
                border-radius: 4px;

                .info {
                    display: flex;
                    flex-direction: row;

                    img {
                        width: 30px;
                        height: 46px;
                        margin-right: 6px;
                        margin-left: 12px;
                    }

                    p {
                        margin: 0;
                        padding: 0;
                        flex-grow: 2;
                        line-height: 46px;
                        overflow: hidden;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                    }
                }

                .operator {
                    height: 46px;
                    padding-left: 10px;

                    i {
                        font-size: 20px;
                        line-height: 46px;
                    }
                }
            }
        }
    }
</style>