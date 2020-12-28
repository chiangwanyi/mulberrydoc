<template>
    <div class="home">
        <!-- 【主区域 > Header】开始 -->
        <el-row class="header">
            <!-- 【Header > 搜索栏】开始 -->
            <el-col class="search" :md="16" :lg="10">
                <el-input placeholder="搜索" size="medium" v-model="search">
                    <i slot="prefix" class="el-input__icon el-icon-search"></i>
                </el-input>
            </el-col>
            <!-- 【Header > 搜索栏】结束 -->
            <!-- 【Header > 基础功能区域】开始 -->
            <el-col class="common" :md="8" :lg="14">
                <!-- 【基础功能区域 > 个人信息】开始 -->
                <el-avatar class="item" :size="36" shape="square" :src="user.avatar"
                           @error="errorAvatarHandler">
                    {{user.username}}
                </el-avatar>
                <!-- 【基础功能区域 > 个人信息】结束 -->
                <!-- 【基础功能区域 > 设置】开始 -->
                <div class="item">
                    <el-tooltip class="item" effect="dark" content="设置" placement="bottom">
                        <i class="item-icon el-icon-setting"></i>
                    </el-tooltip>
                </div>
                <!-- 【基础功能区域 > 设置】结束 -->
                <!-- 【基础功能区域 > 通知】开始 -->
                <div class="item">
                    <i class="item-icon el-icon-bell"></i>
                </div>
                <!-- 【基础功能区域 > 通知】结束 -->
            </el-col>
            <!-- 【基础功能区域】结束 -->
        </el-row>
        <!-- 【主区域 > Header】结束 -->
        <!-- 【主区域 > Path】开始 -->
        <el-row class="path">
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>大三</el-breadcrumb-item>
            </el-breadcrumb>
        </el-row>
        <!-- 【主区域 > Path】结束 -->
        <!-- 【主区域 > Folder】开始 -->
        <!--            <el-row class="folder">-->
        <!--                <FolderList :title=`我的`></FolderList>-->
        <!--            </el-row>-->
        <!-- 【主区域 > Folder】结束 -->
        <h4>文档</h4>
        <!-- 【主区域 > Container】开始 -->

        <!-- 【主区域 > Container】结束 -->
    </div>
</template>

<script>
    import FolderList from "../components/FolderList";

    export default {
        name: 'Home',
        data() {
            return {
                height: `${document.documentElement.clientHeight}px`,
                search: "",
                user: {
                    uid: "289dff07669d7a23de0ef88d2f7129e7",
                    username: "蒋万艺",
                    avatar: "aaa"
                },
                path: "/大三",
                folder: [
                    {
                        name: "计算机网络",
                        path: "/大三/计算机网络",
                        is_favorite: true
                    },
                    {
                        name: "离散数学",
                        path: "/大三/离散数学",
                        is_favorite: false
                    },
                ],
                documentTypeIcon: {
                    doc: "../assets/word.svg",
                    table: "../assets/table.svg",
                    markdown: "../assets/md.svg",
                },
                documentFolder: [
                    {
                        name: "测试文档1",
                        type: "文档",
                        from: "蒋万艺",
                        createdAt: "2020/09/12 12:34",
                        updatedAt: "2020/11/14 03:43"
                    },
                    {
                        name: "测试文档2",
                        type: "Markdown",
                        from: "蒋万艺",
                        createdAt: "2020/09/12 12:34",
                        updatedAt: "2020/11/14 03:43"
                    },
                    {
                        name: "测试文档3",
                        type: "纯文本",
                        from: "蒋万艺",
                        createdAt: "2020/09/12 12:34",
                        updatedAt: "2020/11/14 03:43"
                    },
                    {
                        name: "测试文档4",
                        type: "表格",
                        from: "蒋万艺",
                        createdAt: "2020/09/12 12:34",
                        updatedAt: "2020/11/14 03:43"
                    },
                    {
                        name: "测试文档5",
                        type: "思维导图",
                        from: "蒋万艺",
                        createdAt: "2020/09/12 12:34",
                        updatedAt: "2020/11/14 03:43"
                    },
                ]
            }
        },
        methods: {
            errorAvatarHandler() {
                return true;
            }
        },
        components: {FolderList}
    }
</script>

<style lang="scss">
    // 侧边栏
    .navbar {
        background-color: rgb(248, 249, 250);

        // 侧边栏 > Logo
        .el-menu {
            border: none !important;
            background-color: rgb(248, 249, 250) !important;
        }
    }

    // 主区域
    .main {
        padding: 10px;

        // 主区域 > Header
        .header {

            // Header > 基础功能区域
            .common {
                height: 36px;
                display: flex;
                flex-direction: row-reverse;
                text-align: right;

                .item {
                    cursor: pointer;
                    margin: 0 10px;
                    line-height: 36px;

                    i.item-icon {
                        font-size: 20px;
                        line-height: 36px;
                    }
                }
            }
        }

        // 主区域 > Path
        .path {
            margin-top: 20px;
        }

        // 主区域 > Folder
        .folder {
            .folder-list {
                display: flex;
                flex-direction: row;
                flex-wrap: wrap;

                .folder-item {
                    display: flex;
                    flex-direction: row;
                    margin-right: 10px;
                    margin-bottom: 10px;
                    cursor: pointer;
                    border: 1px solid #ebeef5;
                    background-color: #fff;
                    color: #303133;
                    transition: .3s;
                    padding: 20px;
                    border-radius: 4px;

                    img {
                        width: 26px;
                        height: 26px;
                        margin-right: 4px;
                    }

                    p {
                        margin: 0;
                        padding: 0;
                        flex-grow: 2;
                        line-height: 26px;
                        overflow: hidden;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                    }

                    i {
                        font-size: 20px;
                        margin-left: 10px;
                    }
                }
            }
        }

        // 主区域 > Documents
        .documents-table {
            .list-item-cell {
                display: flex;
                flex-direction: row;

                img {
                    width: 24px;
                    height: 24px;
                    margin-right: 4px;
                }

                p {
                    margin: 0;
                    padding: 0;
                    flex-grow: 2;
                    line-height: 26px;
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                }
            }
        }
    }
</style>
