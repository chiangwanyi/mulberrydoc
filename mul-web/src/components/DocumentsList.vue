<template>
    <el-row id="documents-list">
        <el-table
                :data="data"
                stripe
                :row-style="{cursor: 'pointer'}"
                @selection-change="handleSelectionChange"
                @row-click="handleRowClick">
            <el-table-column type="selection">
            </el-table-column>
            <el-table-column
                    prop="name"
                    sortable
                    label="名称">
                <template slot-scope="scope">
                    <div class="list-item-cell">
                        <img v-show="ready" :src="getIcon(scope.row.type)" alt="">
                        <p :class="{'skeleton': !ready}">{{scope.row.name}}</p>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                    prop="updatedAt"
                    sortable
                    label="最后修改时间">
                <template slot-scope="scope">
                    <div class="list-item-cell">
                        <p :class="{'skeleton': !ready}">{{scope.row.updatedAt | dateFormatter}}</p>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                    prop="createdAt"
                    sortable
                    label="创建时间">
                <template slot-scope="scope">
                    <div class="list-item-cell">
                        <p :class="{'skeleton': !ready}">{{scope.row.createdAt | dateFormatter}}</p>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </el-row>
</template>

<script>
    export default {
        name: "DocumentList",
        data() {
            return {
                icon: {
                    folder: require("../assets/folder.svg"),
                    folderFavorite: require("../assets/folder-favorite.svg"),
                    doc: require("../assets/word.svg"),
                    chart: require("../assets/xchart.svg"),
                    markdown: require("../assets/ppt.svg")
                }
            }
        },
        methods: {
            handleSelectionChange(val) {
                this.$emit("selectItem", val);
            },
            handleRowClick(val) {
                this.$emit("clickItem", val)
            },
            getIcon(type) {
                if (type === "folder") {
                    return this.icon.folder
                }
                if (type === "doc") {
                    return this.icon.doc
                }
                if (type === "md") {
                    return this.icon.markdown
                }
            }
        },
        filters: {
            dateFormatter(date) {
                return new Date(date).toLocaleString();
            }
        },
        props: ["data", "ready"]
    }
</script>

<style scoped lang="scss">
    #documents-list {
        .skeleton {
            background-image: linear-gradient(90deg, #ececec 25%, #dbdbdb 37%, #ececec 25%) !important;
            width: 100% !important;
            height: 0.6rem !important;
            background-size: 400% 100%;
            background-position: 100% 50%;
            animation: skeleton-loading 1.4s ease infinite;
        }

        @keyframes skeleton-loading {
            0% {
                background-position: 100% 50%;
            }
            100% {
                background-position: 0 50%;
            }
        }

        .list-item-cell {
            display: flex;
            flex-direction: row;

            img {
                width: 34px;
                height: 34px;
                margin-right: 4px;
            }

            p {
                margin: 0;
                padding: 0;
                flex-grow: 2;
                line-height: 34px;
                overflow: hidden;
                white-space: nowrap;
                text-overflow: ellipsis;
            }
        }
    }
</style>