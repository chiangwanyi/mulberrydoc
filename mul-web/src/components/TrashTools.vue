<template>
    <div id="trash-tools">
        <el-row type="flex" justify="space-between">
            <el-col :lg="14" :xl="14">
                <el-button-group class="btn-group" v-show="selectedItem !== null && selectedItem.length > 0">
                    <el-button type="danger" size="small">删除</el-button>
                    <el-button size="small" @click="recovery">还原</el-button>
                </el-button-group>
            </el-col>
        </el-row>
    </div>
</template>

<script>
    export default {
        name: "TrashTools",
        methods: {
            recovery() {

            }
        },
        props: ["currentFolder", "currentFolderHash", "data", "selectedItem"]
    }
</script>

<style scoped lang="scss">
    .create_icon {
        float: left;
        width: 28px;
        height: 28px;
    }

    .create_label {
        float: left;
        margin-left: 4px;
    }

    .el-button-group {
        margin-left: 10px;
    }
</style>