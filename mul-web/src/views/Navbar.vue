<template>
    <div class="nav">
        <!-- 【Logo】开始 -->
        <Logo/>
        <!-- 【Logo】结束 -->
        <!-- 【目录】开始 -->
        <el-menu
                class="category"
                :default-active="activeIndex"
                router>
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
    import Logo from "../components/Logo";

    export default {
        name: "Navbar",
        data() {
            return {
                activeIndex: this.$route.path,
            };
        },
        components: {
            Logo,
        }
    }
</script>

<style lang="scss" scoped>
    // 侧边栏 > Logo
    .el-menu {
        border: none !important;
        background-color: rgb(248, 249, 250) !important;
    }
</style>