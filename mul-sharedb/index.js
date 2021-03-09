const express = require("express");
const bodyParser = require("body-parser");
const json1 = require("ot-json1");
const richText = require('rich-text');
const ShareDB = require("sharedb");
const WebSocket = require("ws");
const WebSocketJSONStream = require("@teamwork/websocket-json-stream");
const config = require("config-lite")(__dirname);
const db = require("sharedb-mongo")(config.mongodb_addr, {
    mongoOptions: { useUnifiedTopology: true },
});

const Auth = require("./api/auth");
const File = require("./api/file");

// 创建 sharedb 实体
const share = new ShareDB({ db });

// 注册 json1 类型
ShareDB.types.register(json1.type);
// 注册 RichText 类型
ShareDB.types.register(richText.type);

// 创建本地 Sharedb 连接
const connection = share.connect();

File.setConnection(connection);

// 创建 APP
const app = express();

// 配置解析 POST 请求
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// 开启 HTTP 服务，监听端口
const server = app.listen(config.port, () => {
    let port = server.address().port;
    console.log("Server run at: http://localhost:%s", port);
});

// 创建文件
app.post("/api/file", (req, res) => {
    let body = req.body;
    // 文件类型
    let type = body.file.type;
    // 文件Hash
    let file_hash = body.file.hash;
    let key = body.key;
    if (key !== config.key) {
        res.status(401).json({
            status: 501,
            msg: "无权限",
            data: {},
        });
    } else {
        File.createFile(type, file_hash, json1.type.uri, (err) => {
            if (err) {
                res.status(401).json({
                    status: 401,
                    msg: "创建文档失败",
                    data: err,
                });
            } else {
                res.status(200).json({
                    status: 200,
                    msg: "创建文档成功",
                    data: {},
                });
            }
        });
    }
});

// 创建 Websocket 服务，监听连接
const wss = new WebSocket.Server({ noServer: true });

wss.on("connection", (ws) => {
    let stream = new WebSocketJSONStream(ws);
    share.listen(stream);
});

wss.on("close", () => {
    console.log("用户退出");
});

// 监听 HTTP 升级 Websocket
server.on("upgrade", (request, socket, head) => {
    wss.handleUpgrade(request, socket, head, function done(ws) {
        wss.emit("connection", ws);
    });
    // Auth.authenticate(request, (err) => {
    //     if (err) {
    //         console.log("身份认证失败");
    //         socket.write("HTTP/1.1 401 Unauthorized\r\n\r\n");
    //         socket.destroy();
    //         return;
    //     } else {
    //         console.log("身份认证成功");
    //         wss.handleUpgrade(request, socket, head, function done(ws) {
    //             wss.emit("connection", ws);
    //         });
    //     }
    // });
});
