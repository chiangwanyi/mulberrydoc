const http = require("http");
const json1 = require('ot-json1');
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

ShareDB.types.register(json1.type);

const server = http.createServer();
const wss = new WebSocket.Server({
    noServer: true
});

server.listen(9000);
console.log("服务开启，监听端口：9000");

wss.on('connection', (ws, token) => {
    console.log("用户连接成功，token:", token)
    let stream = new WebSocketJSONStream(ws);
    share.listen(stream);
});

wss.on('close', () => {
    console.log("用户退出");
})

server.on('upgrade', (request, socket, head) => {
    console.log("正在验证身份...");
    auth.authenticate(request, (err, token) => {
        if (err) {
            console.log("身份认证失败");
            socket.write('HTTP/1.1 401 Unauthorized\r\n\r\n');
            socket.destroy();
            return;
        }
        console.log(`身份认证成功，token：${token}`);
        wss.handleUpgrade(request, socket, head, function done(ws) {
            wss.emit('connection', ws, token);
        });
    })
});