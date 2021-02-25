const http = require("http");
const richText = require("rich-text");
const ShareDB = require("sharedb");
const WebSocket = require("ws");
const WebSocketJSONStream = require("@teamwork/websocket-json-stream");
const auth = require("./api/auth");
const db = require('sharedb-mongo')('mongodb://localhost:27017/mulberry-doc', {
    mongoOptions: {
        useUnifiedTopology: true
    }
});

const share = new ShareDB({
    db
});

ShareDB.types.register(richText.type);

const server = http.createServer();
const wss = new WebSocket.Server({
    noServer: true
});

server.listen(9000);
console.log("服务开启，监听端口：9000");

wss.on('connection', (ws, groupId) => {
    console.log("用户连接成功，groupId:", groupId)
    let stream = new WebSocketJSONStream(ws);
    share.listen(stream);
});

wss.on('close', () => {
    console.log("用户退出");
})

let connection = share.connect();

server.on('upgrade', (request, socket, head) => {
    console.log("正在验证身份...");
    auth.authenticate(request, (err, groupId, userId) => {
        if (err) {
            console.log("身份认证失败");
            socket.write('HTTP/1.1 401 Unauthorized\r\n\r\n');
            socket.destroy();
            return;
        }
        console.log(`身份认证成功，userId：${userId}`);
        // console.log("开始获取连接...");
        // let connection = connectionMap.get(groupId)
        // if (connection === undefined) {
        //     connection = share.connect();
        //     connectionMap.set(groupId, connection);
        // }
        // console.log("当前连接池数量：" + connectionMap.size);
        // let doc = connection.get("mul-sharedb-test", groupId);
        // console.log(doc);
        // doc.create({
        //     content: 'Hello world.'
        // });
        // console.log(doc);
        // doc.fetch();
        // console.log(doc);
        // doc.fetch(err => {
        //     if (err) {
        //         throw err;
        //     }
        //     // if (doc.type === null) {
        //     //     doc.create([{
        //     //         insert: 'Hi！我是蒋万艺'
        //     //     }], 'rich-text');
        //     // }
        //     if (doc.type === null) {
        //         doc.create({
        //             content: 'Hello world.'
        //         });
        //     }
        // });
        wss.handleUpgrade(request, socket, head, function done(ws) {
            wss.emit('connection', ws, groupId);
        });
    })
});