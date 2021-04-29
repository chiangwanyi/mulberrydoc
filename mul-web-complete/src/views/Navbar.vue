<!-- Navbar.vue 导航栏 -->
<template>
    <div id="navbar">
        <!-- 【Logo】开始 -->
        <div class="logo">
            <img src="../assets/logo.svg" alt="logo" />
            <h2>桑叶文档</h2>
        </div>
        <!-- 【Logo】结束 -->
        <!-- 【目录】开始 -->
        <el-menu class="category" :default-active="activeIndex" router>
            <!--            <el-menu-item index="/home">-->
            <!--                <i class="el-icon-house"></i>-->
            <!--                <span>主页</span>-->
            <!--            </el-menu-item>-->
            <el-menu-item index="/documents">
                <i class="el-icon-star-off"></i>
                <span>我的文档</span>
            </el-menu-item>
            <el-menu-item index="/trash">
                <i class="el-icon-delete"></i>
                <span>回收站</span>
            </el-menu-item>
        </el-menu>
        <!-- 【目录】结束 -->
        <div class="profile" @click="showProfile">
            <el-avatar
                class="item"
                :src="user.avatar"
                :size="36"
                shape="square"
            ></el-avatar>
            <p>{{ user.username }}</p>
        </div>
        <el-dialog title="个人信息" :visible.sync="dialogVisible" width="30%">
            <el-form
                :model="profileForm"
                ref="form"
                label-position="right"
                label-width="60px"
            >
                <el-form-item label="头像">
                    <div style="display: flex; align-items: center">
                        <el-avatar
                            class="item"
                            :src="user.avatar"
                            :size="36"
                            shape="square"
                        ></el-avatar>
                        <el-divider direction="vertical"></el-divider>
                        <div
                            style="
                                margin-right: 10px;
                                display: flex;
                                cursor: pointer;
                            "
                            v-for="item in avatarList"
                            :key="item"
                            v-show="item !== user.avatar"
                            @click="updateAvatar(item)"
                        >
                            <el-tooltip
                                class="item"
                                effect="dark"
                                content="切换头像"
                                placement="top-start"
                            >
                                <el-avatar
                                    class="avatarList"
                                    :src="item"
                                    :size="36"
                                    shape="square"
                                ></el-avatar>
                            </el-tooltip>
                        </div>
                    </div>
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input
                        v-model="profileForm.username"
                        disabled
                    ></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button size="small" type="danger" @click="logout"
                        >退出登录</el-button
                    >
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script>
import AuthApi from "@/api/auth";

export default {
    name: "Navbar",
    data() {
        return {
            activeIndex: this.$route.path,
            user: {},
            dialogVisible: false,
            avatarList: [
                "http://giler.oss-cn-chengdu.aliyuncs.com/avatar/bot_01.png",
                "http://giler.oss-cn-chengdu.aliyuncs.com/avatar/bot_02.png",
                "http://giler.oss-cn-chengdu.aliyuncs.com/avatar/bot_06.png",
                "http://giler.oss-cn-chengdu.aliyuncs.com/avatar/bot_11.png",
                "http://giler.oss-cn-chengdu.aliyuncs.com/avatar/bot_13.png",
            ],
            profileForm: {},
        };
    },
    methods: {
        logout() {
            this.$confirm("是否退出登录?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    AuthApi.logout(this.user.id).then(r => {
                        if (r.data.status === 200) {
                            this.$router.push("/auth");
                        }
                    })
                })
                .catch(() => {});
        },
        showProfile() {
            this.profileForm = this.user;
            this.dialogVisible = true;
        },
        updateAvatar(avatar) {
            console.log(avatar);
            AuthApi.updateAvatar(avatar).then((r) => {
                let res = r.data;
                if (res.status === 200) {
                    this.$message({
                        type: "success",
                        message: "修改头像成功",
                    });
                    AuthApi.profile().then((r) => {
                        let res = r.data;
                        if (res.status === 200) {
                            this.user = res.data;
                        }
                    });
                }
            });
        },
    },
    created() {
        AuthApi.profile().then((r) => {
            let res = r.data;
            if (res.status === 200) {
                this.user = res.data;
            }
        });
    },
};
</script>

<style lang="scss" scoped>
#navbar {
    .logo {
        display: flex;
        flex-direction: row;
        padding: 20px 20px;

        img {
            width: 36px;
            height: 36px;
            margin-right: 2px;
        }

        h2 {
            margin: 0;
            padding: 0;
        }
    }

    .el-menu {
        border: none !important;
        background-color: rgb(248, 249, 250) !important;
    }

    .profile {
        position: absolute;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        width: 10%;
        bottom: 10px;
        cursor: pointer;

        p {
            margin-left: 10px;
        }
    }
}
</style>