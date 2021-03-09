<template>
    <div id="editor-md" :style="{ height: height }" v-show="ready">
        <div v-if="ready" id="top">
            <div class="header">
                <div style="display: flex;flex-grow: 1;justify-content: flex-start;align-items: center;">
                    <i class="el-icon-arrow-left" style="font-size: 20px;"></i>
                    <p class="file-name" contenteditable="true">{{file.name}}</p>
                    <el-divider direction="vertical"></el-divider>
                    <div style="display: flex;align-items: center;">
                        <el-avatar size="small" v-for="user in members" :key="user.uid">{{user.avatar}}</el-avatar>
                        <i class="el-icon-caret-bottom"></i>
                    </div>
                    <el-divider direction="vertical"></el-divider>
                    <span v-show="!onchange && !freeze" style="font-size: 14px;color: #cccccc;"><i
                            class="el-icon-circle-check"></i>&nbsp;<span
                            style="border-bottom: 1px solid #ccc;">变更已自动保存</span></span>
                    <span v-show="onchange && !freeze" style="font-size: 14px;color: #cccccc;"><i
                            class="el-icon-loading"></i>&nbsp;<span
                            style="border-bottom: 1px solid #ccc;">正在保存</span></span>
                    <span v-show="freeze" style="font-size: 14px;color: rgb(204 9 9);"><i
                            class="el-icon-circle-close"></i>&nbsp;<span
                            style="border-bottom: 1px solid rgb(204 9 9);">离线</span></span>
                </div>
                <div style="display: flex;flex-grow: 1;justify-content: flex-end;align-items: center;">
                    <el-button type="default" size="mini">时间轴</el-button>
                    <el-divider direction="vertical"></el-divider>
                    <el-button type="default" size="mini">下载</el-button>
                    <el-button type="default" size="mini">分享</el-button>
                    <el-divider direction="vertical"></el-divider>
                    <el-avatar>{{this.user.id}}</el-avatar>
                </div>
            </div>
        </div>
        <div class="editor-container" id="container"></div>
        <div id="md">
            <div style="width: 50%;" id="left"></div>
            <div style="width: 50%;" id="right">
                <vue-markdown :source="content"></vue-markdown>
            </div>
        </div>
    </div>
</template>

<script>
    import Editor from "miks-collaborative-editor";
    import EditorEvents from "miks-collaborative-editor/editor-events";
    import 'quill/dist/quill.snow.css'
    import AuthApi from "@/api/auth";
    import {StringUtil} from "@/util/tools";
    import DocumentsApi from "@/api/documents";
    import {io} from "socket.io-client";
    import VueMarkdown from "vue-markdown"

    let editorOptions = {
        authorship: {
            author: null,

            // 当前用户段落的颜色
            authorColor: "rgba(208,101,252,0.30)",

            // 其他用户段落的颜色
            colors: [
                "rgba(247,180,82,0.30)",
                "rgba(239,108,145,0.30)",
                "rgba(142,110,213,0.30)",
                "rgba(106,188,145,0.30)",
                "rgba(90,197,195,0.30)",
                "rgba(114,151,227,0.30)",
                "rgba(155,200,110,0.30)",
                "rgba(235,213,98,0.30)",
                "rgba(212,153,185,0.30)"
            ],
            handlers: {
                // 当文档中出现了一个新的用户ID时，使用这个函数来获取用户的信息
                // 必须返回一个Promise
                getAuthorInfoById: (authorId) => {
                    console.log(`出现了新的用户：${authorId}`);
                    return new Promise((resolve, reject) => {

                        let author = {
                            id: 4,
                            name: 'Another author'
                        };

                        if (author) {
                            resolve(author);
                        } else {
                            reject("user not found");
                        }
                    });
                }
            }
        },
        image: {
            handlers: {
                // 上传DataURI格式的图片到服务器，并返回一个图片URL
                // 必须返回一个Promise
                imageDataURIUpload: (dataURI) => {

                    console.log(dataURI);

                    return new Promise((resolve) => {
                        resolve('https://yd.wemiks.com/banner-2d980584-yuanben.svg');
                    });
                },

                // 上传一个图片外链到服务器，并返回一个内部的图片URL
                // 必须返回一个Promise
                imageSrcUpload: (src) => {

                    console.log(src);

                    return new Promise((resolve) => {
                        resolve('https://yd.wemiks.com/banner-2d980584-yuanben.svg');
                    });
                },

                // 图片上传错误的处理
                imageUploadError: (err) => {
                    console.log("image upload error: " + err);
                }
            }
        }
    };

    let toolbarOptions = [
        ['bold', 'italic', 'underline', 'strike'],
        [{'header': 1}, {'header': 2}, {'header': 3}],
        [{'list': 'ordered'}, {'list': 'bullet'}, {'indent': '+1'}, {'indent': '-1'}],
        ['align', 'color', 'background'],
        ['blockquote', 'code-block', 'link', 'image']
    ];

    let quillOptions = {
        modules: {
            // container: '',
            toolbar: toolbarOptions,
            history: {
                delay: 0,
                maxStack: 20,
                userOnly: true,
            },
        },
        theme: 'snow'
    };

    export default {
        name: "Markdown",
        data() {
            return {
                // 编辑器高度
                height: `${document.documentElement.clientHeight}px`,
                // 用户信息
                user: {},
                // 文件类型
                type: "",
                // 文件Hash
                hash: "",
                // 文件信息
                file: null,
                // Ready
                ready: false,

                // 编辑器实例
                editor: null,
                // Doc连接实例
                doc: null,
                // Connection连接实例
                connection: null,

                content: "",

                // 当前用户列表
                members: [],

                // 文件修改中
                onchange: false,

                // 文件冻结中
                freeze: true,
            }
        },
        methods: {
            // 重新布局
            rearrange() {
                const container = document.querySelector("div#container");
                const left = document.querySelector("div#left");
                const toolbar = document.querySelector("div.ql-toolbar");
                console.log(toolbar)
                toolbar.setAttribute("style", "position: fixed;\n" +
                    "    width: 100%;\n" +
                    "    background-color: #fff;\n" +
                    "    border: none;\n" +
                    "    border-bottom: 1px solid #ccc;\n" +
                    "    border-top: 1px solid #ccc;\n" +
                    "    z-index: 500;")
                // 渲染Markdown
                this.content = this.editor.quill.getText();
                if (left !== null) {
                    // 移动container位置
                    left.appendChild(container);
                }
            },

            // 监听文件变更
            onEditorTextChanged() {
                this.onchange = true;
                this.content = this.editor.quill.getText();
                setTimeout(() => {
                    this.onchange = false;
                }, 500)
            },

            // 连接通信服务器
            startConnection() {
                return new Promise((resolve, reject) => {
                    let socket = io("http://127.0.0.1:9100", {
                        reconnection: false
                    });
                    setTimeout(() => {
                        if (socket.connected) {
                            // 登录通信服务器
                            socket.emit("login", {
                                groupId: this.file.hash,
                                user: {
                                    uid: this.user.id,
                                    avatar: this.user.id,
                                },
                            });
                            socket.on("online", (data) => {
                                console.log(`===== 收到[Online]消息 =====`);
                                console.log(data);
                            });
                            socket.on("offline", (data) => {
                                console.log(`===== 收到[Offline]消息 =====`);
                                console.log(data);
                            });
                            socket.on("broadcast", (data) => {
                                console.log(`===== 收到[Broadcast]消息 =====`);
                                console.log(data);
                                // this.$refs.charbox.receive({
                                //     uid: data.from,
                                //     avatar: data.from,
                                //     text: data.data.text
                                // });
                            });
                            socket.on("sync", (data) => {
                                console.log(`===== 收到[Sync]消息 =====`);
                                console.log(data)
                                this.members = data.data.members;
                            });
                            socket.on("error", (data) => {
                                console.log(`===== 收到[Error]消息 =====`);
                                console.error(data);
                                this.$alert('通信服务器连接中断', '错误', {
                                    confirmButtonText: '确定',
                                    callback: () => {
                                    }
                                });
                            });
                            // 监听服务器掉线
                            socket.on("disconnect", () => {
                                console.error("通信服务器连接丢失");
                                this.destroy("服务器连接中断");
                            });
                            this.connection = socket;
                            resolve();
                        } else {
                            this.destroy();
                            reject(Error("连接通信服务器失败"));
                        }
                    }, 500);
                })
            },

            // 连接文件服务器
            startServer() {
                return new Promise((resolve, reject) => {
                    try {
                        // quillOptions.modules.container = "toolbar";
                        this.editor = new Editor("#container", editorOptions, quillOptions);
                        this.editor.on(EditorEvents.editorTextChanged, this.onEditorTextChanged);
                        // 连接文档数据库
                        this.editor.syncThroughWebsocket("ws://192.168.31.123:9003", this.type, this.hash);
                        this.doc = this.editor.synchronizer.socket;
                        this.doc.addEventListener("close", () => {
                            console.error("文档数据库服务器连接丢失");
                            this.doc.close();
                        })
                        setTimeout(() => {
                            resolve();
                        }, 500)
                    } catch (e) {
                        reject(e);
                    }
                })
            }
        },
        mounted() {
            this.loading = this.$loading({
                lock: true,
                background: 'rgba(239,239,239,0.7)'
            });
            AuthApi.profile()
                .then(r => {
                    let res = r.data;
                    if (res.status === 200) {
                        // 保存用户信息和Auth信息
                        this.user.id = res.data.id;
                        this.user.name = res.data.username;
                        editorOptions.authorship.author = res.data;

                        const pattern = new RegExp("/([a-z]+)/(.{32})")
                        let exec = pattern.exec(this.$route.path);
                        let type = exec[1];
                        let hash = exec[2];
                        if (!StringUtil.isEmpty(type) && !StringUtil.isEmpty(hash)) {
                            // 保存文件信息
                            this.type = type;
                            this.hash = hash;

                            DocumentsApi.queryFile(hash)
                                .then(r => {
                                    let res = r.data;
                                    if (res.status === 200) {
                                        this.file = res.data;
                                        this.startServer()
                                            .then(() => {
                                                this.startConnection()
                                                    .then(() => {
                                                        this.rearrange();
                                                        this.freeze = false;
                                                        this.ready = true;
                                                        this.loading.close();
                                                    })
                                            });
                                    }
                                })
                        }
                    }
                })
        },
        components: {
            VueMarkdown
        }
    }
</script>

<style lang="scss" scoped>
    #editor-md {
        background-color: #f9fafb;
        padding-top: 50px;

        #top {
            z-index: 1500;
            position: fixed;
            background-color: #ffffff;

            top: 0;
            width: 100%;
            margin-bottom: 10px;

            .header {
                height: 50px;
                line-height: 50px;
                padding: 0 20px;
                display: flex;
                flex-direction: row;
                flex-wrap: nowrap;
                align-items: center;
            }
        }

        #md {
            display: flex;
            flex-direction: row;
            width: 100%;
            margin-top: 40px;

            #container {
                flex-grow: 1;
            }

            #show {
                flex-grow: 1;
            }
        }
    }
</style>