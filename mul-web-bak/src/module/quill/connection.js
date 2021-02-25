import ReconnectingWebSocket from "reconnecting-websocket";
import Sharedb from "sharedb/lib/client";

class Connection {
    /**
     * 构造器
     * @param {Editor} editor
     */
    constructor(editor) {
        this.editor = editor;
        this.quill = editor.quill;
        this.socket = null;
        this.doc = null;
    }

    /**
     * 建立连接
     * @param endpoint ws 地址
     * @param collection Collection
     * @param docId DocId
     */
    startConnect(endpoint, collection, docId) {
        // 先关闭连接
        this.close();
        // 连接 socket 服务器
        this.socket = new ReconnectingWebSocket(endpoint);
        // 连接 shareDB
        let connection = new Sharedb.Connection(this.socket);
        // 初始化 doc
        this.initShareDB(connection.get(collection, docId))
    }

    /**
     * 初始化 Doc
     * @param doc doc
     */
    initShareDB(doc) {
        this.doc = doc
        this.doc.subscribe(err => {
            if (err) {
                console.error(err);
                throw err;
            }
            this.editor.setEditorContent(this.doc.data, 'api');

            this.doc.on('op', (op, source) => {
                if (source === this.quill) return;
                console.log("op:", op)
                this.quill.updateContents(op);
            });
        })
    }

    close() {
        if (this.doc) {
            this.doc.destroy();
            this.doc = null;
        }

        if (this.socket) {
            this.socket.close();
            this.socket = null;
        }
    }
}

export default Connection;