<template>
    <div id="app" v-show="ready">
        <!-- 【导航栏】开始 -->
        <el-col v-if="visibility.navbar" class="navbar" :md="size.md" :lg="size.lg" :style="{'height': height}">
            <Navbar></Navbar>
        </el-col>
        <!-- 【导航栏】结束 -->
        <!-- 【主区域】开始 -->
        <el-col class="main" id="main" :md="24 - size.md" :lg="24 - size.lg" :style="{'height': height}">
            <Header v-if="visibility.header"></Header>
            <router-view></router-view>
        </el-col>
        <!-- 【主区域】结束 -->
    </div>
</template>

<script>
    import Navbar from "./views/Navbar";
    import Header from "./views/Header";
    import * as User from "./api/user";

    export default {
        name: "App",
        data() {
            return {
                height: `${document.documentElement.clientHeight}px`,
                visibility: {
                    navbar: false,
                    header: false,
                },
                user: {},
                size: {
                    md: 6,
                    lg: 4,
                },
                ready: false
            }
        },
        methods: {
            /**
             * 检查模块可视状态
             */
            checkVisibility() {
                let path = this.$route.path;
                if (path === "/login") {
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
            /**
             * 检查用户信息
             */
            checkUser() {
                // 获取用户信息
                User.showProfile()
                    .then(r => {
                        let res = r.data;
                        if (res.status === 200) {
                            console.log("当前登录用户：", res.data);
                            this.$store.state.user = res.data;
                        } else {
                            if (this.$route.path !== "/login") {
                                console.warn("需要登录权限");
                                this.$router.push("/login");
                            }
                        }
                        this.ready = true;
                    })
            }
        },
        created() {
            this.checkVisibility();
            this.checkUser();
        },
        watch: {
            '$route'(to, from) {
                // console.log(`${from.path} -> ${to.path}`)
                this.checkVisibility();
            }
        },
        components: {
            Header,
            Navbar,
        }
    }
</script>

<style lang="scss">
    // 导航栏
    .navbar {
        background-color: rgb(248, 249, 250);
    }

    // 主区域
    .main {
        padding: 10px;
        overflow-y: scroll;
    }
</style>
