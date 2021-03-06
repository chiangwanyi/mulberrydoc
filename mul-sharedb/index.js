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
    // 文件初始值
    let content = body.content;
    let uid = body.uid;
    let key = body.key;
    if (key !== config.key) {
        res.status(401).json({
            status: 501,
            msg: "无权限",
            data: {},
        });
    } else {
        File.createFile(type, parseInt(uid), file_hash, json1.type.uri, content, (err) => {
            if (err) {
                console.log(err);
                res.status(401).json({
                    status: 401,
                    msg: "创建文档失败",
                    data: err,
                });
            } else {
                console.log("success");
                res.status(200).json({
                    status: 200,
                    msg: "创建文档成功",
                    data: {},
                });
            }
        });
    }
});

app.post("/api/download", (req, res) => {
    let body = req.body;
    // 文件类型
    let type = body.type;
    // 文件Hash
    let file_hash = body.hash;
    let key = body.key;
    if (key !== config.key) {
        res.status(401).json({
            status: 501,
            msg: "无权限",
            data: {},
        });
    } else {
        File.downloadFile(type, file_hash, (err, content) => {
            if (err) {
                console.log(err);
                res.status(401).json({
                    status: 401,
                    msg: "读取文档内容失败",
                    data: err,
                });
            } else {
                console.log("success");
                res.status(200).json({
                    status: 200,
                    msg: "读取文档内容成功",
                    data: { content: content },
                });
            }
        })
    }
})

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
});
