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
    import * as StringUtils from "../util/stringUtils";
    import TimeUtils from "../util/timeUtils";
    import Log from "../util/log";
    import Ot from "../util/ot2";
    // import E from "../util/wangEditor"
    import E from "wangeditor";

    // 强制同步数据上限尝试次数
    const forceSyncLimit = 20;

    export default {
        name: "Editor",
        data() {
            return {
                // 监听编辑器内容变化锁
                lock: true,
                updateFlag: false,
                // 中文等字符输入锁
                compositionLock: false,
                // 冻结编辑器
                freeze: false,
                // 编辑器对象
                editor: null,
                // 内容同步状态
                syncStatus: false,
                // 强制同步数据计数器
                forceSyncCount: 0,
                // 同步状态监听器
                statusListener: null,
            }
        },
        methods: {
            /**
             * 初始化编辑器
             */
            initEditor() {
                const mn = "编辑器初始化";
                if (this.editor === null) {
                    console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + "开始初始化编辑器");
                    // 填充内容
                    document.getElementById("container").innerHTML = StringUtils.strToUtf16(this.getDocData());
                    // 创建编辑器实例
                    this.editor = new E('#container');

                    this.editor.config.menus = [
                        'head',
                        'bold',
                        'fontSize',
                        'fontName',
                        'italic',
                        'underline',
                        'strikeThrough',
                        'indent',
                        'foreColor',
                        'backColor',
                        'link',
                        'list',
                        'todo',
                        'justify',
                        'quote',
                        'emoticon',
                        'image',
                        'video',
                        'table',
                        'code',
                    ]

                    this.editor.config.showFullScreen = false;

                    // 监听本地变更
                    this.editor.config.onchange = this.handleChange;
                    // this.editor.config.onchangeTimeout = 1000;
                    this.editor.create();
                    console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + `编辑器初始化成功，500ms后解锁内容编辑`);
                    let updated = this.fillSeq();
                    if (updated) {
                        this.submitOps();
                    } else {
                        this.syncStatus = true;
                    }
                    setTimeout(() => {
                        this.compositionListener();
                        // 启动状态监听器
                        this.startStatusListener();

                        this.lock = false;
                    }, 500);
                    // setInterval(() => {
                    //     this.onChange();
                    // }, 1000);
                } else {
                    console.warn(Log.prefix(TimeUtils.fullTimeString(), mn) + "编辑器已初始化");
                }
            },
            /**
             * 从文档的数据中更新编辑器
             */
            syncData() {
                const mn = "同步数据";
                console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + "开始同步数据");
                // 锁定内容
                this.lock = true;

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
                // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), `原始操作：${JSON.stringify(diff)}`);

                if (diff.length !== 0) {
                    if (diff.length <= 3) {
                        let compressOps = Ot.compress(diff);
                        if (compressOps !== null) {
                            diff = compressOps;
                            // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), `压缩操作：${JSON.stringify(compressOps)}`);
                        } else {
                            // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), "不压缩，直接提交");
                        }
                    } else {
                        // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), "复杂操作，非更新式提交");
                    }

                    this.updateFlag = true;
                    // console.log(JSON.stringify(diff));
                    // 开始更新
                    let index = 0;
                    diff.forEach(op => {
                        if (op.r !== undefined) {
                            index += op.r;
                        } else if (op.i !== undefined) {
                            let insertKey = op.i;
                            for (let i = 0; i < insertKey.length; i++) {
                                let text = map.hashMap.get(insertKey[i]);
                                if (StringUtils.isEmpty(text)) {
                                    throw Error("插入节点时发生异常，hashMap没有对应的字符串");
                                }

                                let tmpNode = document.createElement("div");
                                tmpNode.innerHTML = text;
                                if (index < oldContainer.children.length) {
                                    oldContainer.insertBefore(tmpNode.children.item(0), oldContainer.children.item(index));
                                } else {
                                    oldContainer.appendChild(tmpNode.children.item(0));
                                }
                                index++;
                            }
                        } else if (op.d !== undefined) {
                            let deleteKey = op.d;
                            for (let i = 0; i < deleteKey.length; i++) {
                                // 可以不用取出对应的值
                                let text = map.hashMap.get(deleteKey[i]);
                                if (StringUtils.isEmpty(text)) {
                                    throw Error("InsertLine 时发生异常，hashMap没有对应的字符串");
                                }
                                oldContainer.removeChild(oldContainer.children.item(index));
                            }
                        } else if (op.u !== undefined) {
                            let update = op.u;
                            let oldKey = update[0];
                            let newKey = update[1];
                            for (let i = 0; i < oldKey.length; i++) {
                                // let oldText = map.hashMap.get(oldKey[i]);
                                oldContainer.children.item(index).outerHTML = map.hashMap.get(newKey[i]);
                                index++;
                            }
                        }
                    })
                }

                // let index = 0;
                // diff.forEach(op => {
                //     if (op.r !== undefined) {
                //         index += op.r;
                //     } else if (op.i !== undefined) {
                //         for (let i = 0; i < op.i.length; i++) {
                //             let tmp = document.createElement("div");
                //             let node = map.hashMap.get(op.i.charAt(i));
                //             if (typeof (node) === "string") {
                //                 tmp.innerHTML = node;
                //                 let newChild = tmp.children.item(0);
                //                 oldContainer.insertBefore(newChild, oldContainer.children.item(index));
                //                 index++;
                //             } else {
                //                 throw Error("处理insert操作时遇到非字符串内容");
                //             }
                //         }
                //     } else if (op.d !== undefined) {
                //         for (let i = 0; i < op.d.length; i++) {
                //             oldContainer.removeChild(oldContainer.children.item(index));
                //         }
                //     }
                // })
                console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + "同步数据完成");
                // 解锁，检查变更
                this.checkDataSync();
                setTimeout(() => {
                    this.updateFlag = false;
                    this.lock = false;
                }, 500);
            },
            checkDataSync() {
                const mn = "数据同步检查";
                console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + "开始检查数据同步状态");
                let docData = this.getDocData();
                let containerData = this.getContainerData();
                if (docData === containerData) {
                    this.forceSyncCount = 0;
                    console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + "数据已同步，解锁内容编辑");
                    return true;
                } else {
                    this.lock = true;
                    console.warn(Log.prefix(TimeUtils.fullTimeString(), mn) + "数据不同步");
                    // console.log("文档数据：", docData);
                    // console.log("页面数据：", containerData);
                    this.forceSyncCount++;
                    setTimeout(() => {
                        this.syncData();
                    }, 1000);
                    return false;
                }
            },
            /**
             * 处理内容变更
             */
            handleChange(flag) {
                const mn = "数据处理";
                if (this.freeze) {
                    console.warn(Log.prefix(TimeUtils.fullTimeString(), mn), `编辑器已冻结，等待处理`);
                    return;
                }
                if (this.updateFlag) {
                    console.warn(Log.prefix(TimeUtils.fullTimeString(), mn), `变更来自服务器，跳过`);
                    return;
                }
                // 重新处理延迟时间
                const delayHandleTime = 200;
                // 检查内容是否锁定
                if (this.lock) {
                    console.warn(Log.prefix(TimeUtils.fullTimeString(), mn) + `内容锁定中，${delayHandleTime}ms后重新处理变更`);
                    // 创建延时器重新处理内容变更
                    setTimeout(() => {
                        this.handleChange(true);
                    }, delayHandleTime);
                    return;
                } else {
                    // 锁定内容
                    this.lock = true;
                }
                // 检查数据是否同步
                if (flag === true) {
                    let result = this.checkDataSync();
                    if (result) {
                        console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + "内容已同步，放弃提交");
                        // 解锁，继续监听变更
                        setTimeout(() => {
                            this.lock = false;
                        }, 500);
                        return;
                    }
                }
                this.fillSeq();
                this.submitOps();
                // 解锁，继续监听变更
                this.lock = false;
            },
            /**
             * 提交变更
             **/
            submitOps() {
                const mn = "提交变更";
                console.log(Log.prefix(TimeUtils.fullTimeString(), mn), "开始处理变更");

                // 当前数据
                let oldContainer = document.createElement("div");
                oldContainer.innerHTML = StringUtils.strToUtf16(this.getDocData());
                // 当前页面的最新数据
                let newContainer = document.getElementById(this.editor.textElemId);

                let oldValue = [];
                let newValue = [];
                for (let i = 0; i < oldContainer.children.length; i++) {
                    oldValue.push(oldContainer.children.item(i).outerHTML);
                }
                for (let i = 0; i < newContainer.children.length; i++) {
                    newValue.push(newContainer.children.item(i).outerHTML);
                }

                let map = Ot.map(oldValue, newValue);
                // console.log(map);
                // TODO 需要保存原始数据以便撤销
                let diff = Ot.completeDiff(map.hashA, map.hashB);
                // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), `原始操作：${JSON.stringify(diff)}`);
                // 存在差异
                if (diff.length !== 0) {
                    if (diff.length <= 3) {
                        let compressOps = Ot.compress(diff);
                        if (compressOps !== null) {
                            diff = compressOps;
                            // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), `压缩操作：${JSON.stringify(compressOps)}`);
                        } else {
                            // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), "不压缩，直接提交");
                        }
                    } else {
                        // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), "复杂操作，非更新式提交");
                    }

                    // 开始提交
                    let index = 0;
                    diff.forEach(op => {
                        if (op.r !== undefined) {
                            index += op.r;
                        } else if (op.i !== undefined) {
                            let insertKey = op.i;
                            for (let i = 0; i < insertKey.length; i++) {
                                let text = map.hashMap.get(insertKey[i]);
                                if (StringUtils.isEmpty(text)) {
                                    throw Error("InsertLine 时发生异常，hashMap没有对应的字符串");
                                }
                                this.insertLine(index, text);
                                index++;
                            }
                        } else if (op.d !== undefined) {
                            let deleteKey = op.d;
                            for (let i = 0; i < deleteKey.length; i++) {
                                // 可以不用取出对应的值
                                let text = map.hashMap.get(deleteKey[i]);
                                if (StringUtils.isEmpty(text)) {
                                    throw Error("InsertLine 时发生异常，hashMap没有对应的字符串");
                                }
                                this.deleteLine(index);
                            }
                        } else if (op.u !== undefined) {
                            let update = op.u;
                            let oldKey = update[0];
                            let newKey = update[1];
                            for (let i = 0; i < oldKey.length; i++) {
                                let oldText = map.hashMap.get(oldKey[i]);
                                let newText = map.hashMap.get(newKey[i]);
                                this.updateLine(index, oldText, newText);
                                index++;
                            }
                        }
                    })
                    console.log(Log.prefix(TimeUtils.fullTimeString(), mn), "变更已提交");
                } else {
                    console.log(Log.prefix(TimeUtils.fullTimeString(), mn), "无变化");
                }
            },
            /**
             * DOM序号处理
             **/
            fillSeq() {
                const mn = "处理DOM序号";
                // console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + `开始处理DOM序号`);
                let element = document.getElementById(this.editor.textElemId);
                // 记录出现过的序号
                let indexList = [];
                // 记录有问题的节点下标
                let errorIndexList = [];
                for (let i = 0; i < element.children.length; i++) {
                    // 节点
                    let el = element.children.item(i);

                    // 序号
                    let seq = /([0-9]+)-([0-9]+)/.exec(el.getAttribute("index"));
                    // 如果序号格式错误
                    if (seq === null) {
                        errorIndexList.push(i);
                    } else {
                        let author = seq[1];
                        let index = parseInt(seq[2]);
                        // 如果序号是当前用户的序号
                        if (this.author === author) {
                            // 已经包含了该序号
                            if (indexList.includes(index)) {
                                errorIndexList.push(i);
                            } else {
                                indexList.push(index)
                            }
                        }
                    }
                }
                let nextIndex = 0;
                // 获得下一序号
                if (indexList.length !== 0) {
                    nextIndex = Math.max(...indexList) + 1;
                }
                // 修改有问题的序号
                errorIndexList.forEach(i => {
                    console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + `修复序号有问题的DOM`);
                    element.children.item(i).setAttribute("index", `${this.author}-${nextIndex++}`);
                })
                return errorIndexList.length !== 0;
                // this.lock = false;
                // console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + `处理DOM序号完成`);
            },
            /**
             * 状态监听器
             */
            startStatusListener() {
                const mn = "状态监听器";
                // 检查状态延时时间
                const checkDelay = 1000;
                console.log(Log.prefix(TimeUtils.fullTimeString(), mn), `状态监听器启动，每${checkDelay}ms检查一次同步状态`);
                this.statusListener = setInterval(() => {
                    let docData = this.getDocData();
                    let containerData = this.getContainerData();
                    // console.log(`文档数据：${docData}`);
                    // console.log(`页面数据：${containerData}`);
                    if (docData !== containerData) {
                        this.syncStatus = false;
                        if (this.compositionLock) {
                            console.warn(Log.prefix(TimeUtils.fullTimeString(), mn), `数据不同步，等待输入完毕`);
                            return;
                        }
                        if (this.forceSyncCount < forceSyncLimit) {
                            console.warn(Log.prefix(TimeUtils.fullTimeString(), mn), `数据不同步，count:${this.forceSyncCount}`);
                            this.forceSyncCount++;
                        } else {
                            console.error(Log.prefix(TimeUtils.fullTimeString(), mn) + "数据不同步尝试次数达到上限，冻结编辑器，等待重新同步数据");
                            // this.syncData();
                            clearInterval(this.statusListener);
                            this.statusListener = null;
                        }
                    } else {
                        this.syncStatus = true;
                        this.forceSyncCount = 0;
                        // console.log(Log.prefix(TimeUtils.fullTimeString(), mn), `同步状态正常`);
                    }
                }, checkDelay);
            },
            compositionListener() {
                const mn = "输入法监听器";
                let container = document.getElementById(this.editor.textElemId);
                container.addEventListener("compositionstart", () => {
                    console.warn(Log.prefix(TimeUtils.fullTimeString(), mn), `输入法输入中`);
                    // this.compositionLock = true;
                });
                container.addEventListener("compositionend", () => {
                    // this.compositionLock = false;
                    if (this.statusListener === null) {
                        this.startStatusListener();
                    }
                    console.warn(Log.prefix(TimeUtils.fullTimeString(), mn), `输入法输入完毕`);
                });
            },
            insertLine(index, text) {
                this.doc.submitOp(['data', index, {i: {text: StringUtils.utf16ToStr(text)}}]);
            },
            deleteLine(index) {
                this.doc.submitOp(['data', index, {r: 0}]);
            },
            updateLine(index, oldText, newText) {
                let diff = Ot.diff(StringUtils.utf16ToStr(oldText), StringUtils.utf16ToStr(newText));
                this.doc.submitOp(['data', index, ['text', {es: diff}]]);
            },
            /**
             * 获取文档的数据
             * @returns {string} 数据
             */
            getDocData() {
                let text = "";
                this.doc.data.data.forEach(el => {
                    text += el.text;
                });
                return text;
            },
            /**
             * 获取页面的数据
             * @returns {string} 数据
             */
            getContainerData() {
                let element = document.getElementById(this.editor.textElemId);
                return StringUtils.utf16ToStr(element.innerHTML).trim();
            },
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