<template>
    <div class="trash">
        <br>
        <TrashTools
                :data="data"
                :selected-item="selectedItem"
                @refresh="refresh"></TrashTools>
        <TrashList :data="data"
                   :ready="ready"
                   @selectItem="handleSelectItem"></TrashList>
    </div>
</template>

<script>
    import TrashList from "../components/TrashList";
    import TrashTools from "@/components/TrashTools";
    import DocumentsApi from "@/api/documents";

    export default {
        name: "Trash",
        data() {
            return {
                ready: false,
                data: [
                    {
                        hash: "",
                        name: "",
                        type: "doc",
                        updatedAt: "",
                        createdAt: "",
                    },
                ],
                selectedItem: [],
            }
        },
        methods: {
            handleSelectItem(items) {
                this.selectedItem = items;
            },
            // handleClickItem(item) {
            //     if (item.type === "folder") {
            //         this.$router.push(`/documents/${item.hash}`)
            //     } else if (item.type === "doc") {
            //         window.open(this.$router.resolve({
            //             path: `/doc/${item.hash}`,
            //         }).href, '_blank');
            //     } else if (item.type === "md") {
            //         window.open(this.$router.resolve({
            //             path: `/md/${item.hash}`,
            //         }).href, '_blank');
            //     }
            // },
            refresh() {
                DocumentsApi.queryDeletedFiles()
                    .then(r => {
                        let res = r.data;
                        if (res.status === 200) {
                            this.data = res.data.files;
                            // res.data.folders.map(item => item.type = "folder");
                            // this.data = res.data.files.concat(res.data.folders);
                            this.ready = true;
                        }
                    })
            }
        },
        created() {
            this.refresh();
        },
        components: {TrashList, TrashTools}
    }
</script>

<style scoped>

</style>