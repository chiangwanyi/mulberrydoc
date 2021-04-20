<template>
    <div id="trash-tools">
        <el-row type="flex" justify="space-between">
            <el-col :lg="14" :xl="14">
                <el-button-group
                    class="btn-group"
                    v-show="selectedItem !== null && selectedItem.length > 0"
                >
                    <el-button type="danger" size="small" @click="deleteItem"
                        >删除</el-button
                    >
                    <el-button size="small" @click="recovery">还原</el-button>
                </el-button-group>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import DocumentsApi from "@/api/documents";

export default {
    name: "TrashTools",
    methods: {
        deleteItem() {
            this.$confirm("确认将所选文件删除？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    let list = this.selectedItem.map((el) => el.hash);
                    DocumentsApi.deleteFile(list).then((r) => {
                        let res = r.data;
                        if (res.status === 200) {
                            this.$emit("refresh");
                            this.$message({
                                type: "success",
                                message: "删除成功!",
                            });
                        }
                    });
                })
                .catch((e) => {
                    console.error(e);
                });
        },

        recovery() {
            this.$confirm("确认将所选文件还原？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    let list = this.selectedItem.map((el) => el.hash);
                    DocumentsApi.recoveryFile(list).then((r) => {
                        let res = r.data;
                        if (res.status === 200) {
                            this.$emit("refresh");
                            this.$message({
                                type: "success",
                                message: "还原成功!",
                            });
                        }
                    });
                })
                .catch((e) => {
                    console.error(e);
                });
        },
    },
    props: ["currentFolder", "currentFolderHash", "data", "selectedItem"],
};
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