<template>
    <div id="rich-text">
        <div>
            <h2>建立连接</h2>
            <label>
                <span>token:</span>
                <input type="text" v-model="token"/>
            </label>
            <label>
                <span>author:</span>
                <input type="text" v-model="author">
            </label>
            <button @click="connectServer">连接</button>
        </div>
        <hr>
        <Editor ref="editor" :doc="doc" :author="author"/>
    </div>
</template>

<script>
    import * as StringUtils from "../util/StringUtils";
    import Editor from "../components/Editor";
    import ReconnectingWebSocket from "reconnecting-websocket";

    const sharedb = require("sharedb/lib/client");
    const json1 = require("ot-json1");
    // 服务地址
    const serverUrl = "ws://192.168.31.123:9000";

    sharedb.types.register(json1.type);

    export default {
        name: "RichText",
        data() {
            return {
                author: "1",
                token: "1",
                ready: false,
                doc: null,
                socket: null,
                collectionName: "mul-sharedb-test",
            }
        },
        methods: {
            /**
             * 连接服务器
             */
            connectServer() {
                if (!StringUtils.isEmpty(this.token) && !StringUtils.isEmpty(this.author)) {
                    // 连接服务
                    this.socket = new ReconnectingWebSocket(`${serverUrl}?token=${this.token}`);
                    // 连接 sharedb
                    let connection = new sharedb.Connection(this.socket);
                    // 获取文档
                    this.doc = connection.get(this.collectionName, this.token);
                    // 订阅文档
                    this.subscribeDoc();
                }
            },
            subscribeDoc() {
                if (this.doc !== null) {
                    this.doc.subscribe(err => {
                        if (err) {
                            console.error("订阅文档失败，error:", err);
                            this.doc.destroy();
                            throw err;
                        }
                        // 订阅文档成功，读取数据成功
                        if (this.doc.data !== undefined || this.doc.type !== undefined) {
                            console.log("订阅文档成功")
                            // 如果未初始化，则初始化编辑器
                            if (!this.ready) {
                                this.$refs.editor.initEditor();
                                this.doc.on('op', (op, source) => {
                                    if (!source) {
                                        console.log(`接收到操作\t op:${JSON.stringify(op)}\t source:${source}`)
                                        this.$refs.editor.syncData();
                                    }
                                });
                            }
                        } else {
                            console.error("读取文档数据失败");
                            this.doc.destroy();
                        }
                    });
                }
            },
        },
        components: {
            Editor,
        }
    }
</script>

<style scoped>

</style>