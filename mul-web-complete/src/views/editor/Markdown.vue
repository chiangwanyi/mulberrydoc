<template>
    <div id="markdown" :style="{ height: height }" v-show="ready">
        <div v-if="ready" id="top">
            <div class="header">
                <div style="justify-content: flex-start">
                    <i class="back el-icon-arrow-left"></i>
                    <p class="file-name" contenteditable="false">
                        {{ file.name }}
                    </p>
                    <el-divider direction="vertical"></el-divider>
                    <div class="members">
                        当前有<span style="color: #43c2ff">{{
                            members.length
                        }}</span
                        >人在线
                    </div>
                    <el-divider direction="vertical"></el-divider>
                    <div class="file-status">
                        <span
                            v-show="!onchange && !freeze"
                            style="font-size: 14px; color: #cccccc"
                            ><i class="el-icon-circle-check"></i>&nbsp;<span
                                style="border-bottom: 1px solid #ccc"
                                >变更已自动保存</span
                            ></span
                        >
                        <span
                            v-show="onchange && !freeze"
                            style="font-size: 14px; color: #cccccc"
                            ><i class="el-icon-loading"></i>&nbsp;<span
                                style="border-bottom: 1px solid #ccc"
                                >正在保存</span
                            ></span
                        >
                        <span
                            v-show="freeze"
                            style="font-size: 14px; color: rgb(204, 9, 9)"
                            ><i class="el-icon-circle-close"></i>&nbsp;<span
                                style="border-bottom: 1px solid rgb(204, 9, 9)"
                                >离线</span
                            ></span
                        >
                    </div>
                </div>
                <div style="justify-content: flex-end">
                    <el-button
                        type="default"
                        size="mini"
                        @click="timeAxisVisible"
                        >时间轴</el-button
                    >
                    <el-divider direction="vertical"></el-divider>
                    <el-button type="default" size="mini" @click="downloadFile"
                        >下载</el-button
                    >
                    <el-button type="default" size="mini" @click="shareFile">分享</el-button>
                    <el-divider direction="vertical"></el-divider>
                    <el-avatar :src="this.user.avatar"></el-avatar>
                </div>
            </div>
        </div>
        <div id="toolbar">
            <el-tooltip
                class="item"
                effect="dark"
                content="一级标题"
                placement="top"
            >
                <button id="h1">
                    <i class="w-e-icon-header">1</i>
                </button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="二级标题"
                placement="top"
            >
                <button id="h2"><i class="w-e-icon-header"></i>2</button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="三级标题"
                placement="top"
            >
                <button id="h3"><i class="w-e-icon-header"></i>3</button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="加粗"
                placement="top"
            >
                <button id="bold"><i class="w-e-icon-bold"></i></button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="斜体"
                placement="top"
            >
                <button id="italy"><i class="w-e-icon-italic"></i></button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="删除线"
                placement="top"
            >
                <button id="del"><i class="w-e-icon-strikethrough"></i></button>
            </el-tooltip>
            <el-divider direction="vertical"></el-divider>
            <el-tooltip
                class="item"
                effect="dark"
                content="分割线"
                placement="top"
            >
                <button id="horizon_line">
                    <i class="w-e-icon-split-line"></i>
                </button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="引用"
                placement="top"
            >
                <button id="quote"><i class="w-e-icon-quotes-left"></i></button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="代码块"
                placement="top"
            >
                <button id="code_block"><i class="w-e-icon-font"></i></button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="代码"
                placement="top"
            >
                <button id="code"><i class="w-e-icon-terminal"></i></button>
            </el-tooltip>
            <el-divider direction="vertical"></el-divider>
            <el-tooltip
                class="item"
                effect="dark"
                content="表格"
                placement="top"
            >
                <button id="table"><i class="w-e-icon-table2"></i></button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="链接"
                placement="top"
            >
                <button id="link"><i class="w-e-icon-link"></i></button>
            </el-tooltip>
            <el-tooltip
                class="item"
                effect="dark"
                content="图片"
                placement="top"
            >
                <button id="image"><i class="w-e-icon-image"></i></button>
            </el-tooltip>
        </div>
        <div class="md">
            <div class="left" :style="{ minHeight: height }">
                <div class="editor-container" id="container"></div>
            </div>
            <div class="right markdown">
                <vue-markdown :source="content"></vue-markdown>
            </div>
        </div>
        <img
            id="chat-icon"
            src="../../assets/chat.png"
            alt=""
            @click="switchDialog"
        />
        <CharBox ref="charbox" :user="user" @broadcast="broadcast"></CharBox>
        <div style="z-index: 3000">
            <el-drawer
                title="时间轴"
                :visible.sync="showTimeAxis"
                direction="btt"
                size="100%"
            >
                <div id="time-axis">
                    <el-slider
                        v-model="currentVersion"
                        :max="maxVersion"
                        :min="1"
                        show-input
                    >
                    </el-slider>
                    <div
                        class="md"
                        style="overflow-y: scroll"
                        :style="{ height: height }"
                    >
                        <div class="left">
                            <div class="editor-container" id="snapshot"></div>
                        </div>
                        <div class="right markdown">
                            <vue-markdown
                                :source="timeAxisContent"
                            ></vue-markdown>
                        </div>
                    </div>
                </div>
            </el-drawer>
        </div>
    </div>
</template>

<script>
import { Editor, Quill } from "miks-collaborative-editor";
import EditorEvents from "miks-collaborative-editor/editor-events";
import "quill/dist/quill.snow.css";
import AuthApi from "@/api/auth";
import { StringUtil } from "@/util/tools";
import DocumentsApi from "@/api/documents";
import { io } from "socket.io-client";
import CharBox from "@/components/CharBox";
import VueMarkdown from "vue-markdown";

let editorOptions = {
    authorship: {
        author: null,

        // 当前用户段落的颜色
        authorColor: "rgba(208,101,252,0.3)",

        // 其他用户段落的颜色
        colors: [
            "rgba(247,180,82,0.3)",
            "rgba(239,108,145,0.3)",
            "rgba(142,110,213,0.3)",
            "rgba(106,188,145,0.3)",
            "rgba(90,197,195,0.3)",
            "rgba(114,151,227,0.3)",
            "rgba(155,200,110,0.3)",
            "rgba(235,213,98,0.3)",
            "rgba(212,153,185,0.3)",
        ],
        handlers: {
            // 当文档中出现了一个新的用户ID时，使用这个函数来获取用户的信息
            // 必须返回一个Promise
            getAuthorInfoById: (authorId) => {
                console.log(`出现了新的用户：${authorId}`);
                return new Promise((resolve, reject) => {
                    let author = {
                        id: 4,
                        name: "Another author",
                    };

                    if (author) {
                        resolve(author);
                    } else {
                        reject("user not found");
                    }
                });
            },
        },
    },
    image: {
        handlers: {
            // 上传DataURI格式的图片到服务器，并返回一个图片URL
            // 必须返回一个Promise
            imageDataURIUpload: (dataURI) => {
                console.log(dataURI);

                return new Promise((resolve) => {
                    resolve(
                        "https://yd.wemiks.com/banner-2d980584-yuanben.svg"
                    );
                });
            },

            // 上传一个图片外链到服务器，并返回一个内部的图片URL
            // 必须返回一个Promise
            imageSrcUpload: (src) => {
                console.log(src);

                return new Promise((resolve) => {
                    resolve(
                        "https://yd.wemiks.com/banner-2d980584-yuanben.svg"
                    );
                });
            },

            // 图片上传错误的处理
            imageUploadError: (err) => {
                console.log("image upload error: " + err);
            },
        },
    },
};

let quillOptions = {
    modules: {
        toolbar: {
            container: "#toolbar",
        },
        keyboard: {
            bindings: {
                "list autofill": {
                    prefix: /^\s{0,}(1){1,1}(\.|-|\*|\[ ?\]|\[x\])$/,
                },
            },
        },
        history: {
            delay: 0,
            maxStack: 20,
            userOnly: true,
        },
    },
    theme: "snow",
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
            // 文档服务器连接实例
            socket: null,
            // 通信服务器连接实例
            connection: null,
            // 文档实例
            doc: null,
            // 文档纯文本
            content: "",

            // 当前用户列表
            members: [],
            // 文件修改中
            onchange: false,
            // 文件冻结中
            freeze: true,

            // 加载中
            loading: null,

            // 显示时间轴
            showTimeAxis: false,
            // 时间轴编辑器（只读）
            timeAxisEditor: null,
            // 当前 version
            currentVersion: 1,
            // 最新 version
            maxVersion: 0,
            // 时间轴文档
            timeAxisDoc: null,
            // 时间轴文档纯文本
            timeAxisContent: "",
        };
    },
    methods: {
        shareFile() {
            navigator.clipboard.writeText(window.location.href).then(() => {
                this.$message({
                    message: "复制文档链接成功，粘贴到浏览器中即可查看",
                    type: "success",
                });
            });
        },
        downloadFile() {
            let file = this.file;
            DocumentsApi.downloadFile(this.file.hash).then((res) => {
                //定义文件名等相关信息
                const blob = res.data;
                const reader = new FileReader();
                reader.readAsDataURL(blob);
                reader.onload = (e) => {
                    const a = document.createElement("a");
                    let type = "";
                    if (file.type === "doc") {
                        type = "rc";
                    } else if (file.type === "md") {
                        type = "md";
                    }
                    a.download = `${file.name}.${type}`;
                    a.href = e.target.result;
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                };
            });
        },
        getSnapshot(version) {
            this.timeAxisEditor.fetchSnapshot(
                this.type,
                this.hash,
                version,
                (error, snapshot) => {
                    this.timeAxisDoc.setContents(snapshot.data, "api");
                    this.timeAxisContent = this.timeAxisDoc.getText();
                }
            );
        },
        timeAxisVisible() {
            this.showTimeAxis = !this.showTimeAxis;
            this.currentVersion = 1;
            // 更新最新版本数
            this.maxVersion = this.doc.version;
            // 初始化数据
            this.timeAxisEditor.fetchSnapshot(
                this.type,
                this.hash,
                1,
                (error, snapshot) => {
                    if (this.timeAxisDoc === null) {
                        this.timeAxisDoc = new Quill("#snapshot", {
                            theme: "snow",
                        });
                    }
                    this.timeAxisDoc.setContents(snapshot.data, "api");
                    this.timeAxisContent = this.timeAxisDoc.getText();
                }
            );
        },
        /**
         * 广播消息
         */
        broadcast(text) {
            this.connection.emit("broadcast", text);
        },
        /**
         * 打开对话框
         */
        switchDialog() {
            this.$refs.charbox.switchDialog();
        },
        /**
         * 渲染 Markdown
         */
        render() {
            this.content = this.editor.quill.getText();
        },
        /**
         * 设置 Toolbar
         */
        setToolbar() {
            const button_list = [
                { id: "h1", format: "# " },
                { id: "h2", format: "## " },
                { id: "h3", format: "###" },
                { id: "bold", format: "****" },
                { id: "italy", format: "**" },
                { id: "del", format: "~~~~" },
                { id: "horizon_line", format: "---" },
                { id: "quote", format: "> " },
                { id: "code_block", format: "``" },
                { id: "code", format: "```\n\n```" },
                {
                    id: "table",
                    format: "|     |     |\n| --- | --- |\n|    |    |\n",
                },
                { id: "link", format: "[]()" },
                {
                    id: "image",
                    format: '<img src="" alt="" style="zoom:100%;" />',
                },
            ];
            button_list.forEach((btn) => {
                document
                    .querySelector(`button#${btn.id}`)
                    .addEventListener("click", () => {
                        let selection = this.editor.quill.getSelection(true);
                        this.editor.quill.insertText(
                            selection.index,
                            btn.format,
                            { author: this.user.id },
                            "user"
                        );
                    });
            });
        },
        /**
         * 销毁编辑器
         */
        destroy() {
            this.freeze = true;
            this.editor.quill.enable(false);
        },
        /**
         * 监听文件变更
         */
        onEditorTextChanged() {
            this.onchange = true;
            this.render();
            setTimeout(() => {
                this.onchange = false;
            }, 500);
        },
        /**
         * 连接通信服务器
         * @returns {Promise<unknown>}
         */
        startConnection() {
            return new Promise((resolve, reject) => {
                let socket = io("http://127.0.0.1:9100", {
                    reconnection: false,
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
                            this.$refs.charbox.receive({
                                uid: data.from,
                                avatar: data.from,
                                text: data.data.text,
                            });
                        });
                        socket.on("sync", (data) => {
                            console.log(`===== 收到[Sync]消息 =====`);
                            console.log(data);
                            this.members = data.data.members;
                        });
                        socket.on("error", (data) => {
                            console.log(`===== 收到[Error]消息 =====`);
                            console.error(data);
                            this.$alert("通信服务器连接中断", "错误", {
                                confirmButtonText: "确定",
                                callback: () => {},
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
            });
        },
        /**
         * 连接文件服务器
         * @returns {Promise<unknown>}
         */
        startServer() {
            return new Promise((resolve, reject) => {
                try {
                    this.editor = new Editor(
                        "#container",
                        editorOptions,
                        quillOptions
                    );
                    this.editor.on(
                        EditorEvents.editorTextChanged,
                        this.onEditorTextChanged
                    );
                    // 连接文档数据库
                    this.editor.syncThroughWebsocket(
                        "ws://localhost:9003",
                        this.type,
                        this.hash
                    );
                    this.socket = this.editor.synchronizer.socket;
                    this.doc = this.editor.synchronizer.doc;
                    this.timeAxisEditor = this.editor.synchronizer.connection;
                    this.socket.addEventListener("close", () => {
                        console.error("文档数据库服务器连接丢失");
                        this.socket.close();
                    });
                    setTimeout(() => {
                        resolve();
                    }, 500);
                } catch (e) {
                    reject(e);
                }
            });
        },
    },
    mounted() {
        this.loading = this.$loading({
            lock: true,
            background: "rgba(239,239,239,0.7)",
        });
        AuthApi.profile().then((r) => {
            let res = r.data;
            if (res.status === 200) {
                // 保存用户信息和Auth信息
                this.user.id = res.data.id;
                this.user.name = res.data.username;
                this.user.avatar = res.data.avatar;
                editorOptions.authorship.author = res.data;

                const pattern = new RegExp("/([a-z]+)/(.{32})");
                let exec = pattern.exec(this.$route.path);
                let type = exec[1];
                let hash = exec[2];
                if (!StringUtil.isEmpty(type) && !StringUtil.isEmpty(hash)) {
                    // 保存文件信息
                    this.type = type;
                    this.hash = hash;

                    DocumentsApi.queryFile(hash).then((r) => {
                        let res = r.data;
                        if (res.status === 200) {
                            this.file = res.data;
                            this.startServer().then(() => {
                                this.startConnection().then(() => {
                                    this.render();
                                    this.setToolbar();
                                    this.freeze = false;
                                    this.ready = true;
                                    this.loading.close();
                                });
                            });
                        }
                    });
                }
            }
        });
    },
    watch: {
        currentVersion(val) {
            this.getSnapshot(val);
            // console.log(val)
        },
    },
    components: {
        VueMarkdown,
        CharBox,
    },
};
</script>

<style src="../../assets/css/markdown.css"></style>

<style lang="scss" scoped>
#markdown {
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

            > div {
                display: flex;
                flex-grow: 1;
                align-items: center;
            }

            i.back {
                font-size: 20px;
            }

            p.file-name {
                outline: none;
                font-size: 20px;
                margin: 0 6px;
            }

            .members {
                display: flex;
                align-items: center;
            }
        }
    }

    #toolbar {
        position: fixed;
        width: 100%;
        background-color: #fff;
        border: none;
        border-bottom: 1px solid #ccc;
        border-top: 1px solid #ccc;
        z-index: 500;
        display: flex;
        align-items: center;
        justify-content: center;

        button {
            color: #93a5ac;
            width: 38px;
        }
    }

    .md {
        display: flex;
        flex-direction: row;
        width: 100%;
        margin-top: 40px;

        .left {
            width: 50%;
            background-color: #ffffff;
        }

        .right {
            width: 50%;
            background-color: #ffffff;
            padding: 6px;
            border-top: 1px solid #ccc;
        }

        #container {
            flex-grow: 1;
            font-size: 16px;
        }

        #show {
            flex-grow: 1;
        }
    }

    #chat-icon {
        z-index: 1000;
        cursor: pointer;
        width: 50px;
        height: 50px;
        position: fixed;
        right: 50px;
        bottom: 50px;
    }

    #time-axis {
        padding: 10px;
    }
}
</style>