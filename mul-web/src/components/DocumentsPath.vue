<template>
    <el-row id="documents-path">
        <el-row type="flex">
            <el-link type="primary" disabled>返回上一级</el-link>
            <p>|</p>
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item>全部文件</el-breadcrumb-item>
                <el-breadcrumb-item>活动列表</el-breadcrumb-item>
                <el-breadcrumb-item>活动详情</el-breadcrumb-item>
            </el-breadcrumb>
        </el-row>
    </el-row>
</template>

<script>
    export default {
        name: "DocumentsPath",
        data() {
            return {
                pathList: [],
                currentPath: this.path
            }
        },
        created() {
            // console.log(this.path);
            // if (this.path !== "/") {
            //     let tmp = this.path.split("/");
            //     this.pathList = tmp.slice(1, tmp.length);
            // }
        },
        // props: ['path', 'folders']
    }
</script>

<style lang="scss" scoped>
    #documents-path {
        margin: 8px 0;

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