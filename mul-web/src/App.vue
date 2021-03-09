<!-- App.vue 页面入口 -->
<template>
    <div id="app">
        <!-- 【导航栏】开始 -->
        <el-col class="navbar" v-if="visibility.navbar" :md="size.md" :lg="size.lg" :style="{ height: height }">
            <Navbar></Navbar>
        </el-col>
        <!-- 【导航栏】结束 -->

        <!-- 【主区域】开始 -->
        <el-col class="main" :md="24 - size.md" :lg="24 - size.lg" :style="{ height: height }">
            <Header v-if="visibility.header"></Header>
            <router-view></router-view>
        </el-col>
        <!-- 【主区域】结束 -->
    </div>
</template>

<script>
    import Navbar from "@/views/Navbar";
    import Header from "@/views/Header";

    export default {
        name: "App",
        data() {
            return {
                // 页面高度
                height: `${document.documentElement.clientHeight}px`,
                // 区域可视化状态
                visibility: {
                    // 导航栏
                    navbar: true,
                    // 顶栏
                    header: true,
                },
                // 区域尺寸
                size: {
                    md: 6,
                    lg: 4,
                },
            };
        },
        methods: {
            /**
             * 检查模块可视状态
             */
            checkVisibility() {
                // 全屏状态
                const pattern = new RegExp("/doc/.*|/md/.*|/auth");
                if (pattern.test(this.$route.path)) {
                    this.visibility.navbar = false;
                    this.visibility.header = false;
                    this.size.md = 0;
                    this.size.lg = 0;
                } else {
                    this.visibility.navbar = true;
                    this.visibility.header = true;
                    this.size.md = 6;
                    this.size.lg = 4;
                }
            },
        },
        created() {
            this.checkVisibility()
        },
        watch: {
            // 路由跳转时需要检查区域可视化
            $route() {
                this.checkVisibility();
            },
        },
        components: {
            Navbar,
            Header
        },
    };
</script>

<style lang="scss" scoped>
    #app {
        font-family: Avenir, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;

        // 导航栏
        .navbar {
            background-color: rgb(248, 249, 250);
        }

        // 主区域
        .main {
            /*padding: 10px;*/
            overflow-y: scroll;
        }
    }
</style>
