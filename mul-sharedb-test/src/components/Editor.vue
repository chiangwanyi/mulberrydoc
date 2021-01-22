<template>
    <div id="editor">
        <div>
            <div><span>同步状态 </span>
                <div class="status-icon" :class="[syncStatus?'status-on':'status-off']"></div>
            </div>
        </div>
        <div id="container"></div>
    </div>
</template>

<script>
    import * as StringUtils from "../util/StringUtils";
    import Ot from "../util/ot2";
    import E from "wangeditor";

    export default {
        name: "Editor",
        data() {
            return {
                // 监听编辑器内容变化锁
                lock: true,
                // 编辑器对象
                editor: null,
                // 内容同步状态
                syncStatus: false,
            }
        },
        methods: {
            /**
             * 初始化编辑器
             */
            initEditor() {
                if (this.editor === null) {
                    console.log("开始初始化编辑器");
                    // 填充内容
                    document.getElementById("container").innerHTML = StringUtils.strToUtf16(this.getDocData());
                    // 创建编辑器实例
                    this.editor = new E('#container');
                    // 监听本地变更
                    this.editor.config.onchange = this.onChange;
                    this.editor.create();
                    console.log("初始化编辑器成功");
                    setTimeout(() => {
                        this.checkStatus();
                        this.lock = false;
                    }, 500);
                } else {
                    console.warn("编辑器已初始化");
                }
            },
            /**
             * 从文档的数据中更新编辑器
             */
            syncData() {
                // 锁定内容
                this.lock = true;
                console.log("锁定内容");
                // 当前最新的数据
                let newContainer = document.createElement("div");
                newContainer.innerHTML = StringUtils.strToUtf16(this.getDocData());
                // 当前页面的数据
                let oldContainer = document.getElementById(this.editor.textElemId);

                let oldValue = [];
                let newValue = [];
                for (let i = 0; i < newContainer.children.length; i++) {
                    newValue.push(newContainer.children.item(i).outerHTML);
                }
                for (let i = 0; i < oldContainer.children.length; i++) {
                    oldValue.push(oldContainer.children.item(i).outerHTML);
                }
                let map = Ot.map(oldValue, newValue);
                // console.log(map);
                let diff = Ot.completeDiff(map.hashA, map.hashB);
                console.log(diff);
                let index = 0;
                diff.forEach(op => {
                    if (op.r !== undefined) {
                        index += op.r;
                    } else if (op.i !== undefined) {
                        for (let i = 0; i < op.i.length; i++) {
                            let tmp = document.createElement("div");
                            let node = map.hashMap.get(op.i.charAt(i));
                            if (typeof (node) === "string") {
                                tmp.innerHTML = node;
                                let newChild = tmp.children.item(0);
                                oldContainer.insertBefore(newChild, oldContainer.children.item(index));
                                index++;
                            } else {
                                throw Error("处理insert操作时遇到非字符串内容");
                            }
                        }
                    } else if (op.d !== undefined) {
                        for (let i = 0; i < op.d.length; i++) {
                            oldContainer.removeChild(oldContainer.children.item(index));
                        }
                    }
                })
                console.log("修改完毕")
                // 解锁，继续监听变更
                setTimeout(() => {
                    console.log(`[${(new Date()).valueOf()}]解锁内容`);
                    this.lock = false;
                    this.onChange();
                }, 1000);
            },
            /**
             * 处理内容变更
             */
            onChange() {
                if (this.lock) {
                    console.log(`[${(new Date()).valueOf()}]内容锁定中`)
                    return;
                } else {
                    this.lock = true;
                }
                // this.updateEditor();
                this.submitOps();
                // 解锁，继续监听变更
                this.lock = false;
            },
            submitOps() {
                console.log("===== 开始分析操作 =====");
                let diff = Ot.diff(this.getDocData(), this.getContainerData());
                console.log(JSON.stringify(diff));
                if (diff.length === 0) {
                    console.log("===== 无变化 =====");
                } else {
                    console.log("===== 提交操作 =====");
                    this.doc.submitOp(['data', ['html', {es: diff}]]);
                }
            },
            /**
             * 获取文档的数据
             * @returns {string} 数据
             */
            getDocData() {
                return this.doc.data.data.html;
            },
            getContainerData() {
                let element = document.getElementById(this.editor.textElemId);
                return StringUtils.utf16ToStr(element.innerHTML);
            },
            checkStatus() {
                setInterval(() => {
                    let docData = this.getDocData();
                    let containerData = this.getContainerData();
                    if (docData !== containerData) {
                        this.syncStatus = false;
                        console.log("状态不同步");
                    } else {
                        this.syncStatus = true;
                    }
                }, 200);
            }
        },
        props: {
            doc: Object,
            author: String,
        },
    }
</script>

<style scoped>
    .status-icon {
        width: 10px;
        height: 10px;
        display: inline-block;
        border-radius: 50%;
    }

    .status-on {
        background-color: #00e000;
    }

    .status-off {
        background-color: red;
    }
</style>