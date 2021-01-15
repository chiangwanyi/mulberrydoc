<template>
    <div class="hello">
        <h1>ShareDB 测试页面</h1>
        <span>GroupId:</span><input type="text" v-model="groupId" /><span
            >UserId:</span
        ><input type="text" v-model="userId" /><button @click="connectServer">
            连接
        </button>
        <p>连接状态：<span>未连接</span></p>
        <textarea id="data" v-model="content"></textarea>
    </div>
</template>

<script>
import ReconnectingWebSocket from "reconnecting-websocket";
var sharedb = require("sharedb/lib/client");

export default {
    name: "HelloWorld",
    data() {
        return {
            socket: null,
            connection: null,
            doc: null,
            groupId: "",
            userId: "",
            content: "",
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
                    console.warn(`文档：${this.groupId}未创建，取消订阅`);
                    this.doc.destroy();
                    //   this.doc.create({
                    //     content: "Hello world.",
                    //   });
                } else {
                    this.content = docData.content;
                }
            });
        },
    },
    mounted() {},
    props: {
        msg: String,
    },
};
</script>

<style scoped>
a {
    color: #42b983;
}
#data {
    width: 400px;
    height: 300px;
}
</style>
