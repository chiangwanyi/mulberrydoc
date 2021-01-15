<template>
    <div class="wang-test">
        <h1>ShareDB 测试页面</h1>
        <div>
            <h2>建立连接</h2>
            <span>GroupId:</span><input type="text" v-model="groupId"/><span
        >UserId:</span
        ><input type="text" v-model="userId"/>
            <button @click="connectServer">
                连接
            </button>
        </div>
        <hr>
        <!--        <div>-->
        <!--            <h2>操作</h2>-->
        <!--            <span>获取第</span><input type="number" v-model="lineIndex"><span>行数据：</span>-->
        <!--            <br>-->
        <!--            <textarea cols="30" rows="10" v-model="lineText"></textarea><br>-->
        <!--            <button @click="updateLine">提交修改</button>-->
        <!--            <hr>-->
        <!--            <button @click="insertNewLine(0, 'aaa')">创建新行</button>-->
        <!--            <button @click="deleteLine(0)">删除行</button>-->
        <!--        </div>-->
        <!--        <hr>-->
        <!--        <div>-->
        <!--            <h2>原始数据</h2>-->
        <!--            <p v-for="(item, index) in originData" :key="index">{{item.text}}</p>-->
        <!--        </div>-->
        <div>
                <div id="container"></div>
        </div>
    </div>
</template>

<script>
    import ReconnectingWebSocket from "reconnecting-websocket";
    import Ot from "../util/ot";
    import Editor from 'wangeditor';

    const sharedb = require("sharedb/lib/client");
    const json1 = require("ot-json1");

    sharedb.types.register(json1.type);

    const ot = new Ot();

    export default {
        name: "WangEditorTest",
        data() {
            return {
                socket: null,
                connection: null,
                doc: null,
                groupId: "2",
                userId: "0",
                ready: false,
                lineIndex: undefined,
                lineText: '',
                originData: [],
                originJson: [],
                editor: null,
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
                        this.doc.create({
                            data: [
                                {text: '<h1>我是一级标题</h1>'},
                                {text: '<h2>我是二级标题</h2>'},
                                {text: '<h3>我是三级标题</h3>'},
                                {text: '<p>我是文本段落</p>'},
                            ]
                        }, json1.type.uri);
                        this.doc.destroy();
                    } else {
                        this.ready = true;
                        this.doc.subscribe(this.updatedOriginData);
                        setTimeout(() => {
                            this.lineIndex = 1;
                        }, 500);
                        this.doc.on('op', (op, source) => {
                            console.log("接收到操作：" + JSON.stringify(op));
                            if (source) {
                                console.log("操作来自本地");
                            } else {
                                console.log("操作来自服务器，接收");
                                this.updatedOriginData();
                            }
                        });
                    }
                });
            },
            h() {
                this.doc.submitOp(['data', 0, ['text', {es: [4, {d: 2}]}]], {source: this.userId});
            },
            queryLineData(index) {
                for (let i = 0; i < this.originData.length; i++) {
                    if (i === index - 1) {
                        return this.strToUtf16(this.originData[i].text);
                    }
                }
            },
            updatedOriginData() {
                this.originData = this.doc.data.data;
                let html = "";
                this.originData.forEach(el => {
                    html += el.text;
                })
                if (this.editor === null) {
                    document.querySelector("div#container").innerHTML = html;
                    this.editor = new Editor('#container');
                    this.editor.config.onchange = (newHtml) => {
                        console.log("本地修改");
                        console.log(newHtml);
                        // this.originJson = this.editor.txt.getJSON();
                    }
                    this.editor.create();
                } else {
                    this.editor.txt.html(html);
                }
                // this.originJson = this.editor.txt.getJSON();
            },
            utf16ToStr(str) {
                let regExp = /[\ud800-\udbff][\udc00-\udfff]/g; // 检测utf16字符正则
                return str.replace(regExp, function (char) {
                    let H, L, code;
                    if (char.length === 2) {
                        H = char.charCodeAt(0); // 取出高位
                        L = char.charCodeAt(1); // 取出低位
                        code = (H - 0xD800) * 0x400 + 0x10000 + L - 0xDC00; // 转换算法
                        return `&#${code.toString(16)};`;
                    } else {
                        return char;
                    }
                });
            },
            strToUtf16(str) {
                let reg = /&#([a-f|0-9]+);/g;
                return str.replace(reg, el => {
                    return String.fromCodePoint(parseInt("0x" + el.replace("&#", "").replace(";", ""), 16));
                })
            },
            updateLine() {
                let index = this.lineIndex;
                let newVal = this.utf16ToStr(this.lineText);
                let oldVal = this.originData[index - 1].text;
                console.log(`${oldVal} -> ${newVal}`);
                let opt = ot.opt(oldVal, newVal);
                console.log(opt);
                if (opt.d === undefined) {
                    this.doc.submitOp(['data', index - 1, ['text', {es: [opt.r, opt.i]}]]);
                } else {
                    this.doc.submitOp(['data', index - 1, ['text', {es: [opt.r, {d: opt.d.len}, opt.i]}]]);
                }
                this.updatedOriginData();
            },
            insertNewLine(index, text) {
                this.doc.submitOp(['data', index, {i: {text: text}}]);
                this.updatedOriginData();
            },
            deleteLine(index) {
                this.doc.submitOp(['data', index, {r: 0}]);
                this.updatedOriginData();
            },
            render() {
                // this.updatedOriginData();
            }
        },
        watch: {
            lineIndex(val) {
                if (!this.ready) {
                    return;
                }
                console.log(`读取第${val}行数据`);
                let lineData = this.queryLineData(val);
                this.lineText = lineData === undefined ? "=====无数据=====" : lineData;
            }
        },
    }
</script>

<style scoped>

</style>