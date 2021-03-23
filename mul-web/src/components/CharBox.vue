<template>
    <div id="chatbox" :style="{width:`${minWidth}px`, height:`${minHeight}px`}" v-show="show">
        <div class="title">
            <h2>即时聊天</h2>
            <div style="cursor: pointer;">
                <a class="close" @click="show = false" title="关闭"></a>
            </div>
        </div>
        <div class="resizeL"></div>
        <div class="resizeT"></div>
        <div class="resizeR"></div>
        <div class="resizeB"></div>
        <div class="resizeLT"></div>
        <div class="resizeTR"></div>
        <div class="resizeBR"></div>
        <div class="resizeLB"></div>
        <div class="content">
            <div class="main" id="main">
                <div class="msg" v-for="(item, index) in messages"
                     :style="{flexDirection: item.user.uid === user.id ? 'row-reverse' : 'row'}" :key="index">
                    <el-avatar size="small">{{item.user.avatar}}</el-avatar>
                    <p>{{item.text}}</p>
                </div>
            </div>
            <div style="background-color: #b1b1b1;display: block;height: 1px;width: 100%;margin: 4px 0;"></div>
            <div class="footer" style="display: flex;flex-direction: row;height: 8%;padding: 0 2px 5px 2px;">
                <textarea style="flex-grow: 6;font-size: 14px;resize: none;border: none;outline: none"
                          v-model="text"></textarea>
                <el-button size="mini" style="flex-grow: 1" @click="send">发送</el-button>
            </div>
        </div>
    </div>
</template>

<script>
    const get = {
        // byId: function (id) {
        //     return typeof id === "string" ? document.getElementById(id) : id
        // },
        byClass: function (sClass, oParent) {
            let aClass = [];
            let reClass = new RegExp("(^| )" + sClass + "( |$)");
            let aElem = this.byTagName("*", oParent);
            for (let i = 0; i < aElem.length; i++) reClass.test(aElem[i].className) && aClass.push(aElem[i]);
            return aClass
        },
        byTagName: function (elem, obj) {
            return (obj || document).getElementsByTagName(elem)
        }
    };

    export default {
        name: "CharBox",
        data() {
            return {
                minWidth: 400,
                minHeight: 520,
                show: false,
                text: "",
                messages: []
            }
        },
        methods: {
            drag(oDrag, handle) {
                let disX = 0;
                let disY = 0;
                let oMin = get.byClass("min", oDrag)[0];
                let oMax = get.byClass("max", oDrag)[0];
                let oRevert = get.byClass("revert", oDrag)[0];
                let oClose = get.byClass("close", oDrag)[0];
                handle = handle || oDrag;
                handle.style.cursor = "move";

                handle.onmousedown = (event) => {
                    event = event || window.event;
                    disX = event.clientX - oDrag.offsetLeft;
                    disY = event.clientY - oDrag.offsetTop;
                    document.onmousemove = (event) => {
                        let e = event || window.event;
                        let iL = e.clientX - disX;
                        let iT = e.clientY - disY;
                        let maxL = document.documentElement.clientWidth - oDrag.offsetWidth;
                        let maxT = document.documentElement.clientHeight - oDrag.offsetHeight;
                        iL <= 0 && (iL = 0);
                        iT <= 0 && (iT = 0);
                        iL >= maxL && (iL = maxL);
                        iT >= maxT && (iT = maxT);
                        oDrag.style.left = iL + "px";
                        oDrag.style.top = iT + "px";
                        return false
                    };

                    document.onmouseup = () => {
                        document.onmousemove = null;
                        document.onmouseup = null;
                        // this.releaseCapture && this.releaseCapture()
                    };

                    // this.setCapture && this.setCapture();

                    return false
                };
                // //最大化按钮
                // oMax.onclick = () => {
                //     oDrag.style.top = oDrag.style.left = 0;
                //     oDrag.style.width = document.documentElement.clientWidth - 2 + "px";
                //     oDrag.style.height = document.documentElement.clientHeight - 2 + "px";
                //     this.style.display = "none";
                //     oRevert.style.display = "block";
                // };
                // //还原按钮
                // oRevert.onclick = () => {
                //     oDrag.style.width = this.minWidth + "px";
                //     oDrag.style.height = this.minHeight + "px";
                //     oDrag.style.left = (document.documentElement.clientWidth - oDrag.offsetWidth) / 2 + "px";
                //     oDrag.style.top = (document.documentElement.clientHeight - oDrag.offsetHeight) / 2 + "px";
                //     this.style.display = "none";
                //     oMax.style.display = "block";
                // };
                // //最小化按钮
                // oMin.onclick = oClose.onclick = () => {
                //     oDrag.style.display = "none";
                //     let oA = document.createElement("a");
                //     oA.className = "open";
                //     oA.href = "javascript:;";
                //     oA.title = "还原";
                //     document.body.appendChild(oA);
                //     oA.onclick = () => {
                //         oDrag.style.display = "block";
                //         document.body.removeChild(this);
                //         oA.onclick = null;
                //     };
                // };
                //阻止冒泡
                // oMin.onmousedown = (event) => {
                //     oMin.onfocus = () => {
                //         oMin.blur()
                //     };
                //     (event || window.event).cancelBubble = true
                // };
                // oMax.onmousedown = (event) => {
                //     oMax.onfocus = () => {
                //         oMax.blur()
                //     };
                //     (event || window.event).cancelBubble = true
                // };
                // oClose.onmousedown = (event) => {
                //     oClose.onfocus = () => {
                //         oClose.blur()
                //     };
                //     (event || window.event).cancelBubble = true
                // };
            },
            resize(oParent, handle, isLeft, isTop, lockX, lockY) {
                handle.onmousedown = (event) => {
                    event = event || window.event;
                    let disX = event.clientX - handle.offsetLeft;
                    let disY = event.clientY - handle.offsetTop;
                    let iParentTop = oParent.offsetTop;
                    let iParentLeft = oParent.offsetLeft;
                    let iParentWidth = oParent.offsetWidth;
                    let iParentHeight = oParent.offsetHeight;
                    document.onmousemove = (event) => {
                        event = event || window.event;
                        let iL = event.clientX - disX;
                        let iT = event.clientY - disY;
                        let maxW =
                            document.documentElement.clientWidth -
                            oParent.offsetLeft -
                            2;
                        let maxH =
                            document.documentElement.clientHeight -
                            oParent.offsetTop -
                            2;
                        let iW = isLeft
                            ? iParentWidth - iL
                            : handle.offsetWidth + iL;
                        let iH = isTop
                            ? iParentHeight - iT
                            : handle.offsetHeight + iT;
                        isLeft &&
                        (oParent.style.left = iParentLeft + iL + "px");
                        isTop && (oParent.style.top = iParentTop + iT + "px");
                        iW < this.minWidth && (iW = this.minWidth);
                        iW > maxW && (iW = maxW);
                        lockX || (oParent.style.width = iW + "px");
                        iH < this.minHeight && (iH = this.minHeight);
                        iH > maxH && (iH = maxH);
                        lockY || (oParent.style.height = iH + "px");
                        if (
                            (isLeft && iW === this.minWidth) ||
                            (isTop && iH === this.minHeight)
                        )
                            document.onmousemove = null;
                        return false;
                    };
                    document.onmouseup = () => {
                        document.onmousemove = null;
                        document.onmouseup = null;
                    };
                    return false;
                };
            },
            switchDialog() {
                this.show = !this.show;
            },
            receive(data){
                this.messages.push({
                    user: {
                        uid: data.uid,
                        avatar: data.uid
                    },
                    text: data.text
                })
                setTimeout(() => {
                    let element = document.getElementById("main");
                    element.scrollTop = element.scrollHeight - element.clientHeight;
                }, 100)
            },
            send() {
                if (this.text !== "") {
                    this.messages.push({
                        user: {
                            uid: this.user.id,
                            avatar: this.user.id
                        },
                        text: this.text
                    })
                    this.$emit("broadcast", this.text);
                    this.text = "";
                    setTimeout(() => {
                        let element = document.getElementById("main");
                        element.scrollTop = element.scrollHeight - element.clientHeight;
                    }, 100)
                }
            }
        },
        mounted() {
            let oDrag = document.getElementById("chatbox");
            let oTitle = get.byClass("title", oDrag)[0];
            let oL = get.byClass("resizeL", oDrag)[0];
            let oT = get.byClass("resizeT", oDrag)[0];
            let oR = get.byClass("resizeR", oDrag)[0];
            let oB = get.byClass("resizeB", oDrag)[0];
            let oLT = get.byClass("resizeLT", oDrag)[0];
            let oTR = get.byClass("resizeTR", oDrag)[0];
            let oBR = get.byClass("resizeBR", oDrag)[0];
            let oLB = get.byClass("resizeLB", oDrag)[0];
            this.drag(oDrag, oTitle);
            //四角
            this.resize(oDrag, oLT, true, true, false, false);
            this.resize(oDrag, oTR, false, true, false, false);
            this.resize(oDrag, oBR, false, false, false, false);
            this.resize(oDrag, oLB, true, false, false, false);
            //四边
            this.resize(oDrag, oL, true, false, false, true);
            this.resize(oDrag, oT, false, true, true, false);
            this.resize(oDrag, oR, false, false, false, true);
            this.resize(oDrag, oB, false, false, true, false);

            oDrag.style.left = `${document.documentElement.clientWidth * 0.5}px`;
            oDrag.style.top = `${document.documentElement.clientHeight * 0.1}px`;
        },
        props: {
            user: Object
        }
    }
</script>

<style scoped>
    #chatbox {
        position: absolute;
        top: 100px;
        left: 100px;
        border-radius: 3px;
        background: #ffffff;
        box-shadow: 5px 5px 13px #737373,
        -5px -5px 13px #ffffff;
        z-index: 1600;
    }

    #chatbox .title {
        display: flex;
        flex-direction: row;
        height: 6%;
        border-bottom: 1px solid #A1B4B0;
        align-items: center;
        justify-content: space-between;
        padding: 0 4px;
    }

    #chatbox .title h2 {
        font-size: 14px;
        margin: 0;
    }

    #chatbox .title a, a.open {
        float: left;
        width: 21px;
        height: 19px;
        display: block;
        margin-left: 5px;
        background: url(../assets/tool.png) no-repeat;
    }

    #chatbox .content {
        display: flex;
        flex-direction: column;
        height: 94%;
    }

    #chatbox .content .main {
        height: 92%;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        overflow-y: scroll;
    }

    #chatbox .content .main .msg {
        display: flex;
        align-items: baseline;
        padding: 6px;
    }

    #chatbox .content .main .msg span {
    }

    #chatbox .content .main .msg p {
        word-wrap: break-word;
        max-width: 80%;
        border: 1px solid #ccc;
        margin: 4px;
        border-radius: 4px;
        padding: 4px;
    }

    a.open {
        position: absolute;
        top: 10px;
        left: 50%;
        margin-left: -10px;
        background-position: 0 0;
    }

    a.open:hover {
        background-position: 0 -29px;
    }

    #chatbox .title a.min {
        background-position: -29px 0;
    }

    #chatbox .title a.min:hover {
        background-position: -29px -29px;
    }

    #chatbox .title a.max {
        background-position: -60px 0;
    }

    #chatbox .title a.max:hover {
        background-position: -60px -29px;
    }

    #chatbox .title a.revert {
        background-position: -149px 0;
        display: none;
    }

    #chatbox .title a.revert:hover {
        background-position: -149px -29px;
    }

    #chatbox .title a.close {
        background-position: -89px 0;
    }

    #chatbox .title a.close:hover {
        background-position: -89px -29px;
    }

    #chatbox .content {
        /*overflow: auto;*/
        /*margin: 0 5px;*/
    }

    #chatbox .resizeBR {
        position: absolute;
        width: 14px;
        height: 14px;
        right: 0;
        bottom: 0;
        overflow: hidden;
        cursor: nw-resize;
        background: url(../assets/resize.png) no-repeat;
    }

    #chatbox .resizeL, #chatbox .resizeT, #chatbox .resizeR, #chatbox .resizeB, #chatbox .resizeLT, #chatbox .resizeTR, #chatbox .resizeLB {
        position: absolute;
        background: #000;
        overflow: hidden;
        opacity: 0;
        filter: alpha(opacity=0);
    }

    #chatbox .resizeL, #chatbox .resizeR {
        top: 0;
        width: 2px;
        height: 100%;
        cursor: w-resize;
    }

    #chatbox .resizeR {
        right: 0;
    }

    #chatbox .resizeT, #chatbox .resizeB {
        width: 100%;
        height: 5px;
        cursor: n-resize;
    }

    #chatbox .resizeT {
        top: 0;
    }

    #chatbox .resizeB {
        bottom: 0;
    }

    #chatbox .resizeLT, #chatbox .resizeTR, #chatbox .resizeLB {
        width: 8px;
        height: 8px;
        background: #FF0;
    }

    #chatbox .resizeLT {
        top: 0;
        left: 0;
        cursor: nw-resize;
    }

    #chatbox .resizeTR {
        top: 0;
        right: 0;
        cursor: ne-resize;
    }

    #chatbox .resizeLB {
        left: 0;
        bottom: 0;
        cursor: ne-resize;
    }
</style>