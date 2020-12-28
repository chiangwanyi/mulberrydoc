const http = require("http");
const richText = require("rich-text");
const ShareDB = require("sharedb");
const WebSocket = require("ws");
const WebSocketJSONStream = require("@teamwork/websocket-json-stream");

const auth = require("./api/auth");

const db = require('sharedb-mongo')('mongodb://localhost:27017/mulberry-doc', {mongoOptions: {useUnifiedTopology: true}});

ShareDB.types.register(richText.type);
const share = new ShareDB({db});

const server = http.createServer();
const wss = new WebSocket.Server({noServer: true});

wss.on('connection', ws => {
    console.log("用户连接成功")
    let stream = new WebSocketJSONStream(ws);
    share.listen(stream);
});

server.on('upgrade', (request, socket, head) => {
    auth.authenticate(request, (err, docId, username) => {
        if (err) {
            socket.write('HTTP/1.1 401 Unauthorized\r\n\r\n');
            socket.destroy();
            return;
        }
        let connection = share.connect();
        let doc = connection.get("examples", 'richtext');
        doc.fetch(function (err) {
            if (err) throw err;
            if (doc.type === null) {
                doc.create([{
                    insert: 'Hi！我是蒋万艺'
                }], 'rich-text');
            }
        });
        wss.handleUpgrade(request, socket, head, function done(ws) {
            wss.emit('connection', ws, request);
        });
    })
});

server.listen(8080);