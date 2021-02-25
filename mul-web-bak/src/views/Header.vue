<template>
    <div class="header">
        <!-- 【Header】开始 -->
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
                <el-avatar class="item" :size="36" shape="square" :src="this.$store.state.user.avatar"
                           @error="errorAvatarHandler">
                    {{this.$store.state.user.username}}
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
        <!-- 【Header】结束 -->
    </div>
</template>

<script>
    export default {
        name: "Header",
        data() {
            return {
                search: "",
            }
        },
        methods: {
            errorAvatarHandler() {
                return true;
            }
        },
        created() {

        },
    }
</script>

<style lang="scss" scoped>
    .header {
        margin-bottom: 20px;

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
</style>