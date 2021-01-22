<template>
    <div class="wang-test">
        <h1>ShareDB 测试页面</h1>
        <button @click="shutdown">中止</button>
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
        <div>
            <div id="container"></div>
        </div>
    </div>
</template>

<script>
    import ReconnectingWebSocket from "reconnecting-websocket";
    import Ot from "../util/Ot";
    import Editor from "wangeditor";

    const sharedb = require("sharedb/lib/client");
    const json1 = require("ot-json1");

    // const emptyDomReg = new RegExp("<[a-z|A-Z]+[0-9]?><br></[a-z|A-Z]+[0-9]?>");

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
                editor: null,
                freeze: false,
            };
        },
        methods: {
            shutdown() {
                window.location.href = 'http://www.baidu.com';
            },
            connectServer() {
                if (this.groupId === "" || this.userId === "") {
                    window.alert("请检查");
                    return;
                }
                this.socket = new ReconnectingWebSocket(
                    "ws://192.168.31.123:9000?groupId=" +
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
                    if (docData === undefined || docType === undefined) {
                        console.warn(`文档：${this.groupId}未创建，正在创建，然后重新订阅`);
                        this.doc.create({
                            data: [
                                {text: '<h1 id="1-1">我是一级标题</h1>'},
                                {text: '<h2 id="1-2">我是二级标题</h2>'},
                                {text: '<h3 id="1-3">我是三级标题</h3>'},
                                {text: '<p id="2-1">我是文本段落</p>'},
                            ]
                        }, json1.type.uri);
                        this.doc.destroy();
                    } else {
                        this.ready = true;
                        this.doc.subscribe(this.updateLocalData(true));
                        this.doc.on('op', (op, source) => {
                            console.log(`接收到操作\top:${JSON.stringify(op)}\t source:${source}`)
                            if (source) {
                                console.log("操作来自本地");
                            } else {
                                console.log("操作来自服务器，接收");
                                this.updateLocalData(false);
                            }
                        });
                    }
                });
            },
            updateLocalData(init) {
                this.freeze = true;
                let html = "";
                // 获取最新数据
                this.doc.data.data.forEach(el => {
                    html += el.text;
                });
                console.log(html);
                if (init) {
                    console.log("初始化数据");
                    document.getElementById("container").innerHTML = html;
                    this.editor.create();
                } else {
                    // 创建最新数据的DOM元素
                    let element = document.createElement("div");
                    element.innerHTML = this.strToUtf16(html);
                    // 当前的数据DOM
                    let container = document.getElementById(this.editor.textElemId);
                    for (let i = 0; i < element.children.length; i++) {
                        let node = element.children[i];
                        console.log(`第${i}行：${node.outerHTML}`);
                        if (i < container.children.length) {
                            if (container.children[i].outerHTML !== node.outerHTML) {
                                console.log("【修改】");
                                container.children[i].outerHTML = node.outerHTML;
                            } else {
                                console.log("【不变】");
                            }
                        } else {
                            console.log("【添加】");
                            container.appendChild(node);
                        }
                    }
                    if (container.children.length > element.children.length) {
                        console.log("【移除】");
                        container.removeChild(container.children[element.children.length]);
                    }
                    console.log("修改完毕");
                    console.log(document.getElementById(this.editor.textElemId).innerHTML);
                }
                this.freeze = false;
            },
            saveData() {
                if (this.freeze) {
                    console.log("本地修改监听冻结中...");
                    return;
                }
                console.log("监听到本地修改");
                let childNodes = document.getElementById(this.editor.textElemId).children;
                let j = 0;
                for (let i = 0; i < this.doc.data.data.length; i++) {
                    let oldText = this.doc.data.data[i].text;
                    let newText = j < childNodes.length ? childNodes[j++].outerHTML : null;
                    // 先转码
                    if (newText !== null) {
                        newText = this.utf16ToStr(newText);
                    }
                    if (newText === null) {
                        console.log(`第${i}行：【移除行】${oldText} -> ${newText}`);
                        this.deleteLine(i);
                    } else if (oldText === newText) {
                        console.log(`第${i}行：【无变化】`);
                    } else {
                        console.log(`第${i}行：【修改行】${oldText} -> ${newText}`);
                        this.updateLine(oldText, newText, i);
                    }
                }
                while (j < childNodes.length) {
                    let newText = this.utf16ToStr(childNodes[j].outerHTML);
                    console.log(`第${j}行：【增加行】null -> ${newText}`);
                    this.insertLine(j, newText);
                    j++;
                }
                console.log("===============");
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
            updateLine(oldVal, newVal, index) {
                let opt = ot.opt(oldVal, newVal);
                if (opt.d === undefined) {
                    this.doc.submitOp(['data', index, ['text', {es: [opt.r, opt.i]}]]);
                } else {
                    this.doc.submitOp(['data', index, ['text', {es: [opt.r, {d: opt.d.len}, opt.i]}]]);
                }
            },
            insertLine(index, text) {
                this.doc.submitOp(['data', index, {i: {text: text}}]);
            },
            deleteLine(index) {
                this.doc.submitOp(['data', index, {r: 0}]);
            },
        },
        mounted() {
            this.editor = new Editor('#container');
            this.editor.config.onchange = this.saveData;
        }
    }
</script>

<style scoped>

</style>