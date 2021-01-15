<template>
    <div class="quill-test">
        <h1>ShareDB 测试页面</h1>
        <span>GroupId:</span><input type="text" v-model="groupId"/><span
    >UserId:</span
    ><input type="text" v-model="userId"/>
        <button @click="connectServer">
            连接
        </button>
        <div>
            <h2>功能测试</h2>
            <button @click="h">H</button>
            <br>
        </div>
        <div>
            <h2>原始数据</h2>
            <span>获取第</span><input type="number" v-model="lineIndex"><span>行数据：</span>
            <br>
            <textarea cols="30" rows="10" v-model="lineText"></textarea>
            <hr>
        </div>
        <div>
            <h2>渲染数据
                <button @click="render">手动渲染</button>
            </h2>
            <div id="tools">
                <button>清除格式</button>
                <span>|</span>
                <button value="b">加粗</button>
                <button value="u">下划线</button>
                <button value="i">斜体</button>
                <button value="d">删除线</button>
                <span>|</span>
                <button>红色</button>
            </div>
            <div id="container" v-html="renderHtml" contenteditable="true"></div>
        </div>
        <p>连接状态：<span>未连接</span></p>
        <div id="editor"></div>
    </div>
</template>

<script>
    import ReconnectingWebSocket from "reconnecting-websocket";
    import marked from "marked";

    const sharedb = require("sharedb/lib/client");
    const richText = require('rich-text');
    import 'quill/dist/quill.snow.css'
    import Quill from "quill";

    const Delta = Quill.import("delta");

    sharedb.types.register(richText.type);

    export default {
        name: "QuillTest",
        data() {
            return {
                socket: null,
                connection: null,
                doc: null,
                groupId: "0",
                userId: "0",
                quill: null,
                lineIndex: undefined,
                lineText: '',
                ready: false,
                renderHtml: "",
            };
        },
        methods: {
            connectServer() {
                if (this.groupId === "" || this.userId === "") {
                    window.alert("请检查");
                    return;
                }
                this.socket = new ReconnectingWebSocket(
                    "ws://localhost:9000?groupId=" +
                    this.groupId +
                    "&" +
                    "userId=" +
                    this.userId
                );
                this.connection = new sharedb.Connection(this.socket);
                this.doc = this.connection.get("mul-sharedb-test", this.groupId);
                console.log("开始订阅文档：", this.groupId);
                this.doc.subscribe((err) => {
                    if (err) throw err;
                    let docData = this.doc.data;
                    let docType = this.doc.type;
                    console.log("文档data：", docData);
                    console.log("文档type：", docType);
                    if (docData === undefined || docType === undefined) {
                        console.warn(`文档：${this.groupId}未创建，正在创建，然后重新订阅`);
                        this.doc.create([
                                {insert: '# 我是一级标题1'},
                                {insert: '## 我是二级标题1'},
                                {insert: '# 我是一级标题2'},
                                {insert: '我是文本段落'},
                            ]
                            , 'rich-text');
                        this.doc.destroy();
                    } else {
                        this.lineIndex = 1;
                        this.ready = true;
                        this.quill = new Quill('#editor', {theme: 'snow'});
                        this.quill.setContents(this.doc.data);
                        this.quill.on('text-change', (delta, oldDelta, source) => {
                            if (source !== 'user') return;
                            this.doc.submitOp(delta, {source: this.quill});
                        });
                        this.doc.on('op', (op, source) => {
                            if (source === this.quill) return;
                            this.quill.updateContents(op);
                            this.render();
                        });
                    }
                });
            },
            h() {
                this.doc.submitOp(new Delta()
                    .retain(9)
                    .insert(' H', {bold: true}), {source: this.quill});
            },
            queryLineData(index) {
                let ops = this.quill.editor.delta.ops;
                for (let i = 0; i < ops.length; i++) {
                    if (i === index - 1) {
                        return ops[i];
                    }
                }
            },
            render() {
                if (!this.ready) {
                    return;
                }
                let ops = this.quill.editor.delta.ops;
                let html = "";
                let index = 1;
                ops.forEach(el => {
                    let text = el.insert;
                    console.log(text);
                    let result = `<div id="line-${index++}">${marked(text)}</div>`;
                    console.log(result);
                    html += result;
                })
                this.renderHtml = html;
                console.log(this.renderHtml);
            }
        },
        mounted() {
        },
        watch: {
            lineIndex(val) {
                if (!this.ready) {
                    return;
                }
                console.log(`读取第${val}行数据`);
                let lineData = this.queryLineData(val);
                console.log(lineData);
                this.lineText = lineData === undefined ? "=====无数据=====" : lineData.insert;
            }
        },
        props: {
            msg: String,
        },
    }
</script>

<style scoped>
    div#container {
        border: 1px solid #000;
    }
</style>