<template>
    <div id="editor">
        <Doc ref="editor" :doc="doc" :uid="uid"></Doc>
    </div>
</template>

<script>
    import {StringUtil} from "@/util/tools";
    import ReconnectingWebSocket from "reconnecting-websocket";
    import AuthApi from "@/api/auth";
    import DocumentsApi from "@/api/documents";
    import Doc from "@/components/editor/Doc";

    const sharedb = require("sharedb/lib/client");
    const json1 = require("ot-json1");

    sharedb.types.register(json1.type);

    export default {
        name: "Editor",
        data() {
            return {
                uid: null,
                type: "",
                hash: "",
                file: null,
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
                if (!StringUtil.isEmpty(this.uid)) {
                    // 连接服务
                    this.socket = new ReconnectingWebSocket(
                        `ws://localhost:9003`
                    );
                    // 连接 sharedb
                    const connection = new sharedb.Connection(this.socket);
                    // 获取文档
                    this.doc = connection.get(this.type, this.hash);
                    // 订阅文档
                    this.subscribeDoc();
                }
            },
            subscribeDoc() {
                if (this.doc !== null) {
                    this.doc.subscribe((err) => {
                        if (err) {
                            console.error("订阅文档失败，error:", err);
                            this.doc.destroy();
                            throw err;
                        }
                        // 订阅文档成功，读取数据成功
                        if (this.doc.data !== undefined || this.doc.type !== undefined) {
                            console.log("订阅文档成功");
                            // 如果未初始化，则初始化编辑器
                            if (!this.ready) {
                                this.$refs.editor.initEditor();
                                this.doc.on("op", (op, source) => {
                                    if (!source) {
                                        console.log(`接收到操作\t op:${JSON.stringify(op)}\t source:${source}`);
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
            }
        },
        created() {
            AuthApi.profile()
                .then(r => {
                    let res = r.data;
                    if (res.status === 200) {
                        this.uid = res.data.id;
                        const pattern = new RegExp("/([a-z]+)/(.{32})")
                        let exec = pattern.exec(this.$route.path);
                        let type = exec[1];
                        let hash = exec[2];
                        if (!StringUtil.isEmpty(type) && !StringUtil.isEmpty(hash)) {
                            this.type = type;
                            this.hash = hash;
                            DocumentsApi.queryFile(hash)
                                .then(r => {
                                    let res = r.data;
                                    if (res.status === 200) {
                                        let file = res.data;
                                        if (file.uid === this.uid) {
                                            this.file = file;
                                            this.connectServer();
                                        }
                                    }
                                })
                        }
                    }
                })
        },
        components: {
            Doc: Doc
        }
    }
</script>

<style scoped>

</style>