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
            <el-menu-item index="/home">
                <i class="el-icon-house"></i>
                <span>主页</span>
            </el-menu-item>
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
    </div>
</template>

<script>
export default {
    name: "Navbar",
    data() {
        return {
            activeIndex: this.$route.path,
        };
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
}
</style>