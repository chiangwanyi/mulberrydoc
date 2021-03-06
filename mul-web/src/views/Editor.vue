<template>
    <div id="editor" :style="{ height: height }">
        <Doc ref="editor" :doc="doc" :user="user" :file="file" :ready="ready" @closeServe="closeServer"></Doc>
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
                height: `${document.documentElement.clientHeight}px`,
                user: {},
                uid: null,
                type: "",
                hash: "",
                file: null,
                ready: false,
                doc: null,
                socket: null,
                loading: null,
                flag: false
            }
        },
        methods: {
            /**
             * 连接服务器
             */
            connectServer() {
                // 连接服务
                this.socket = new ReconnectingWebSocket(
                    `ws://192.168.31.123:9003`
                );
                this.socket.addEventListener("close", () => {
                    console.error("文档数据库服务器连接丢失");
                    if (this.flag) {
                        return;
                    }
                    this.socket.close();
                    this.$refs.editor.destroy();
                })
                // 连接 sharedb
                const connection = new sharedb.Connection(this.socket);
                // 获取文档
                this.doc = connection.get(this.type, this.hash);
                // 订阅文档
                this.subscribeDoc();
            },
            closeServer() {
                this.flag = true
                this.socket.close();
            },
            /**
             * 订阅文件
             */
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
                                this.ready = true;
                                setTimeout(() => {
                                    this.$refs.editor.initEditor()
                                        .then(() => {
                                            this.loading.close();
                                            this.$refs.editor.show();
                                            this.doc.on("op", (op, source) => {
                                                if (!source) {
                                                    console.log(`接收到操作\t op:${JSON.stringify(op)}\t source:${source}`);
                                                    this.$refs.editor.syncData();
                                                }
                                            });
                                        })
                                        .catch(e => {
                                            console.error(e);
                                            this.$alert('打开文件失败，请重试', '错误', {
                                                confirmButtonText: '重新加载',
                                                callback: action => {
                                                    if (action === "confirm") {
                                                        window.location.reload();
                                                    }
                                                    this.loading.close();
                                                }
                                            });
                                        });
                                }, 500)
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
            this.loading = this.$loading({
                lock: true,
                background: 'rgba(239,239,239,0.7)'
            });
            AuthApi.profile()
                .then(r => {
                    let res = r.data;
                    if (res.status === 200) {
                        this.user = res.data;
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
                                        this.file = res.data;
                                        this.connectServer();
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
    #editor {
        height: 100%;
    }
</style>