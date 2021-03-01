<template>
    <div id="doc">
        <div id="container"></div>
    </div>
</template>

<script>
    const sharedb = require("sharedb/lib/client");
    const json1 = require("ot-json1");
    import { StringUtil, TimeUtil } from "@/util/tools/tools";

    import E from "wangeditor";

    sharedb.types.register(json1.type);

    export default {
        name: "Doc",
        data() {
            return {
                // 编辑器对象
                editor: null,
                ready: false,
                doc: null,
                socket: null,
            }
        },
        methods: {
            /**
             * 连接服务器
             */
            connectServer() {
                if (
                    !StringUtil.isEmpty(this.token) &&
                    !StringUtil.isEmpty(this.author)
                ) {
                    // 连接服务
                    this.socket = new ReconnectingWebSocket(
                        `${serverUrl}?token=${this.token}`
                    );
                    // 连接 sharedb
                    let connection = new sharedb.Connection(this.socket);
                    // 获取文档
                    this.doc = connection.get(this.collectionName, this.token);
                    // 订阅文档
                    this.subscribeDoc();
                }
            },
            initEditor() {
                if (this.editor === null) {

                }
            }
        }
    }
</script>

<style scoped>

</style>