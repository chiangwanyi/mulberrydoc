<template>
    <div id="doc" v-if="ready" v-show="display">
        <div id="top">
            <div class="header">
                <div style="display: flex;flex-grow: 1;justify-content: flex-start;align-items: center;">
                    <i class="el-icon-arrow-left" style="font-size: 20px;"></i>
                    <p class="file-name" contenteditable="true">{{file.name}}</p>
                    <el-divider direction="vertical"></el-divider>
                    <div style="display: flex;align-items: center;">
                        <el-avatar size="small">1</el-avatar>
                        <el-avatar size="small">2</el-avatar>
                        <el-avatar size="small">3</el-avatar>
                        <el-avatar size="small">4</el-avatar>
                        <i class="el-icon-caret-bottom"></i>
                    </div>
                    <el-divider direction="vertical"></el-divider>
                    <i class="el-icon-circle-check"
                       style="font-size: 14px;color: #cccccc;border-bottom: 1px solid #ccc;">&nbsp;自动保存</i>
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
            <div id="toolbar-container"></div>
        </div>
        <div id="text-container" :style="{minHeight: height}"></div>
    </div>
</template>

<script>
    import {StringUtil} from "@/util/tools";
    import Ot from "@/util/ot"
    import E from "wangeditor";
    import {io} from "socket.io-client";

    // 强制同步数据上限尝试次数
    const forceSyncLimit = 20;

    export default {
        name: "Doc",
        data() {
            return {
                icon: {
                    folder: require("../../assets/folder.svg"),
                    folderFavorite: require("../../assets/folder-favorite.svg"),
                    doc: require("../../assets/word.svg"),
                    chart: require("../../assets/xchart.svg")
                },
                height: `${document.documentElement.clientHeight}px`,
                width: `${document.documentElement.clientWidth}px`,
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
                display: false,
                listener: {

                },
                socket: null,
            }
        },
        methods: {
            destroy() {
                this.lock = true;
            },
            startConnection() {
                return new Promise((resolve, reject) => {
                    let socket = io("http://127.0.0.1:9100", {
                        reconnection: false
                    });
                    setTimeout(() => {
                        if (socket.connected) {
                            this.socket = socket;
                            // 登录通信服务器
                            socket.emit("login", {
                                groupId: this.file.hash,
                                user: {
                                    uid: this.user.id,
                                    avatar: this.user.id,
                                },
                            });
                            // 监听服务器掉线
                            socket.on("disconnect", () => {
                                console.log("服务器掉线")
                                console.log(socket.id);
                            });
                            resolve();
                        } else {
                            reject(Error("连接通信服务器失败"));
                        }
                    }, 500);
                })
            },
            show() {
                this.display = true;
            },
            /**
             * 初始化编辑器
             */
            initEditor() {
                return new Promise((resolve, reject) => {
                    try {
                        if (this.editor === null) {
                            console.log("开始初始化编辑器");
                            // 创建编辑器实例
                            this.editor = new E('#toolbar-container', '#text-container');
                            // 设置工具栏
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
                            // 关闭全屏模式
                            this.editor.config.showFullScreen = false;
                            // 监听本地变更
                            this.editor.config.onchange = this.handleChange;
                            // 渲染
                            this.editor.create();
                            // 设置内容
                            this.editor.txt.html(StringUtil.strToUtf16(this.getDocData()));
                            console.log(`编辑器初始化成功，500ms后解锁内容编辑`);
                            // 处理DOM序号
                            let updated = this.fillSeq();
                            // 如果处理了序号，则提交变更
                            if (updated) {
                                this.submitOps();
                            } else {
                                this.syncStatus = true;
                            }
                            setTimeout(() => {
                                // 启动输入法监听
                                this.compositionListener();
                                // 启动状态监听器
                                this.startStatusListener();
                                this.lock = false;
                                this.startConnection()
                                    .then(() => {
                                        resolve();
                                    })
                                    .catch((e) => {
                                        reject(e);
                                    })
                            }, 500);
                        } else {
                            console.warn("编辑器已初始化");
                        }
                    } catch (e) {
                        reject(e);
                    }
                })
            },
            /**
             * 从文档的数据中更新编辑器
             */
            syncData() {
                console.log("开始同步数据");
                // 锁定内容
                this.lock = true;

                // 当前最新的数据
                let newContainer = document.createElement("div");
                newContainer.innerHTML = StringUtil.strToUtf16(this.getDocData());
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

                let map = Ot.stringsMap(oldValue, newValue);
                let diff = Ot.completeDiff(map.aliasB, map.aliasA);
                if (diff.length !== 0) {
                    if (diff.length <= 3) {
                        let compressOps = Ot.compress(diff);
                        if (compressOps !== null) {
                            diff = compressOps;
                            console.log(`压缩操作：${JSON.stringify(compressOps)}`);
                        } else {
                            console.log("不压缩，直接提交");
                        }
                    } else {
                        console.log("复杂操作，非更新式提交");
                    }

                    this.updateFlag = true;
                    // 开始更新
                    let index = 0;
                    diff.forEach(op => {
                        if (op.r !== undefined) {
                            index += op.r;
                        } else if (op.i !== undefined) {
                            let insertKey = op.i;
                            for (let i = 0; i < insertKey.length; i++) {
                                let text = map.map.get(insertKey[i]);
                                if (StringUtil.isEmpty(text)) {
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
                                let text = map.map.get(deleteKey[i]);
                                if (StringUtil.isEmpty(text)) {
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
                                oldContainer.children.item(index).outerHTML = map.map.get(newKey[i]);
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
                console.log("同步数据完成");
                // 解锁，检查变更
                this.checkDataSync();
                setTimeout(() => {
                    this.updateFlag = false;
                    this.lock = false;
                }, 500);
            },
            checkDataSync() {
                console.log("开始检查数据同步状态");
                let docData = this.getDocData();
                let containerData = this.getContainerData();
                if (docData === containerData) {
                    this.forceSyncCount = 0;
                    console.log("数据已同步，解锁内容编辑");
                    return true;
                } else {
                    this.lock = true;
                    console.warn("数据不同步");
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
                if (this.freeze) {
                    console.warn(`编辑器已冻结，等待处理`);
                    return;
                }
                if (this.updateFlag) {
                    console.warn(`变更来自服务器，跳过`);
                    return;
                }
                // 重新处理延迟时间
                const delayHandleTime = 200;
                // 检查内容是否锁定
                if (this.lock) {
                    console.warn(`内容锁定中，${delayHandleTime}ms后重新处理变更`);
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
                        console.log("内容已同步，放弃提交");
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
                if (this.lock) {
                    console.warn("编辑器已锁定");
                    return;
                }
                console.log("开始处理变更");

                // 当前数据
                let oldContainer = document.createElement("div");
                oldContainer.innerHTML = StringUtil.strToUtf16(this.getDocData());
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

                let map = Ot.stringsMap(oldValue, newValue);
                // TODO 需要保存原始数据以便撤销
                let diff = Ot.completeDiff(map.aliasB, map.aliasA);
                // 存在差异
                if (diff.length !== 0) {
                    if (diff.length <= 3) {
                        let compressOps = Ot.compress(diff);
                        if (compressOps !== null) {
                            diff = compressOps;
                            console.log(`压缩操作：${JSON.stringify(compressOps)}`);
                        } else {
                            console.log("不压缩，直接提交");
                        }
                    } else {
                        console.log("复杂操作，非更新式提交");
                    }

                    // 开始提交
                    let index = 0;
                    diff.forEach(op => {
                        if (op.r !== undefined) {
                            index += op.r;
                        } else if (op.i !== undefined) {
                            let insertKey = op.i;
                            for (let i = 0; i < insertKey.length; i++) {
                                let text = map.map.get(insertKey[i]);
                                if (StringUtil.isEmpty(text)) {
                                    throw Error("InsertLine 时发生异常，hashMap没有对应的字符串");
                                }
                                this.insertLine(index, text);
                                index++;
                            }
                        } else if (op.d !== undefined) {
                            let deleteKey = op.d;
                            for (let i = 0; i < deleteKey.length; i++) {
                                // 可以不用取出对应的值
                                let text = map.map.get(deleteKey[i]);
                                if (StringUtil.isEmpty(text)) {
                                    throw Error("InsertLine 时发生异常，hashMap没有对应的字符串");
                                }
                                this.deleteLine(index);
                            }
                        } else if (op.u !== undefined) {
                            let update = op.u;
                            let oldKey = update[0];
                            let newKey = update[1];
                            for (let i = 0; i < oldKey.length; i++) {
                                let oldText = map.map.get(oldKey[i]);
                                let newText = map.map.get(newKey[i]);
                                this.updateLine(index, oldText, newText);
                                index++;
                            }
                        }
                    })
                    console.log("变更已提交");
                } else {
                    console.log("无变化");
                }
            },
            /**
             * DOM序号处理
             **/
            fillSeq() {
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
                        let uid = seq[1];
                        let index = parseInt(seq[2]);
                        // 如果序号是当前用户的序号
                        if (this.uid === uid) {
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
                    console.log(`修复序号有问题的DOM`);
                    element.children.item(i).setAttribute("index", `${this.uid}-${nextIndex++}`);
                })
                return errorIndexList.length !== 0;
                // this.lock = false;
                // console.log(Log.prefix(TimeUtils.fullTimeString(), mn) + `处理DOM序号完成`);
            },
            /**
             * 状态监听器
             */
            startStatusListener() {
                // 检查状态延时时间
                const checkDelay = 1000;
                console.log(`状态监听器启动，每${checkDelay}ms检查一次同步状态`);
                this.statusListener = setInterval(() => {
                    let docData = this.getDocData();
                    let containerData = this.getContainerData();
                    // console.log(`文档数据：${docData}`);
                    // console.log(`页面数据：${containerData}`);
                    if (docData !== containerData) {
                        this.syncStatus = false;
                        if (this.compositionLock) {
                            console.warn(`数据不同步，等待输入完毕`);
                            return;
                        }
                        if (this.forceSyncCount < forceSyncLimit) {
                            console.warn(`数据不同步，count:${this.forceSyncCount}`);
                            this.forceSyncCount++;
                        } else {
                            console.error("数据不同步尝试次数达到上限，冻结编辑器，等待重新同步数据");
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
                let container = document.getElementById(this.editor.textElemId);
                container.addEventListener("compositionstart", () => {
                    console.warn(`输入法输入中`);
                    // this.compositionLock = true;
                });
                container.addEventListener("compositionend", () => {
                    // this.compositionLock = false;
                    if (this.statusListener === null) {
                        this.startStatusListener();
                    }
                    console.warn(`输入法输入完毕`);
                });
            },
            insertLine(index, text) {
                this.doc.submitOp(['data', index, {i: {text: StringUtil.utf16ToStr(text)}}]);
            },
            deleteLine(index) {
                this.doc.submitOp(['data', index, {r: 0}]);
            },
            updateLine(index, oldText, newText) {
                let diff = Ot.diff(StringUtil.utf16ToStr(oldText), StringUtil.utf16ToStr(newText));
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
                return StringUtil.utf16ToStr(element.innerHTML).trim();
            },
        },
        props: {
            doc: Object,
            user: Object,
            file: Object,
            ready: Boolean,
        }
    }
</script>

<style lang="scss" scoped>
    #doc {
        background-color: #f9fafb;
        padding-top: 100px;

        #top {
            z-index: 100000;
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

                p.file-name {
                    outline: none;
                    font-size: 20px;
                    margin: 0 6px;
                }

                i {
                    vertical-align: middle;
                    cursor: pointer;
                }

                img.file-icon {
                    vertical-align: middle;
                    width: 34px;
                    height: 34px;
                    line-height: 50px;
                }

                input.file-name {
                    vertical-align: middle;
                    border: none;
                    border-bottom: 1px solid #ccc;
                    outline: none;
                    padding: 0;
                    width: 100px;
                    height: 34px;
                    font-size: 20px;
                    display: inline-block;
                }
            }

            #toolbar-container {
                border: 1px solid #ccc;
            }
        }

        #text-container {
            background: #ffffff;
            border: 1px solid #e8e6e6;
            width: 50%;
            padding: 40px;
            margin: 0 auto;
        }
    }
</style>