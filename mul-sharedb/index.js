const http = require("http");
const json1 = require("ot-json1");
const ShareDB = require("sharedb");
const WebSocket = require("ws");
const WebSocketJSONStream = require("@teamwork/websocket-json-stream");
const db = require("sharedb-mongo")("mongodb://localhost:27017/mul-sharedb", {
    mongoOptions: { useUnifiedTopology: true },
});
const Auth = require("./api/auth");

// 创建 shareDB 实体
const share = new ShareDB({ db });

// 注册 json1 类型
ShareDB.types.register(json1.type);

// 创建 http 服务，监听连接
const server = http.createServer();
server.listen(9003);
console.log("Server run at: http://localhost:9003");

// 创建 websocket 服务，监听连接
const wss = new WebSocket.Server({ noServer: true });

// WS Connection
wss.on("connection", (ws) => {
    // let stream = new WebSocketJSONStream(ws);
    // share.listen(stream);
});

wss.on("close", () => {
    console.log("用户退出");
});

// 监听 http -> websocket
server.on("upgrade", (request, socket, head) => {
    Auth.authenticate(request, (err) => {
        if (err) {
            console.log("身份认证失败");
            socket.write("HTTP/1.1 401 Unauthorized\r\n\r\n");
            socket.destroy();
            return;
        } else {
            console.log("身份认证成功");
            wss.handleUpgrade(request, socket, head, function done(ws) {
                wss.emit("connection", ws);
            });
        }
    });
});
