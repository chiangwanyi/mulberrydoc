const config = require("config-lite")(__dirname);

class File {
    // sharedb 连接
    static connection = null;

    static setConnection(conn) {
        this.connection = conn;
    }

    /**
     * 创建文件
     * @param {string} type 文件类型
     * @param {string} file_hash 文件Hash
     */
    static createFile(type, uid, file_hash, doc_type, content, callback) {
        try {
            let doc = this.connection.get(type, file_hash);
            switch (type) {
                // 文档类型
                case "doc": {
                    if (content === undefined || content === null) {
                        content = [
                            {
                                text: "<h1>标题</h1>",
                            },
                            {
                                text: "<p><br></p>",
                            },
                        ]
                        content = escape(JSON.stringify(content))
                    }
                    let s = unescape(content)
                    doc.create(
                        {
                            data: JSON.parse(s),
                        },
                        doc_type
                    );
                    break;
                }
                case "md": {
                    if (content === undefined || content === null) {
                        content = "rich-text"
                    }
                    doc.create(
                        [{ insert: content, attributes: { author: uid } }],
                        "rich-text"
                    );
                    break;
                }
            }
            callback(null);
        } catch (err) {
            callback(err);
        }
    }

    static downloadFile(type, file_hash, callback) {
        let doc = this.connection.get(type, file_hash);
        doc.subscribe(e => {
            let content = "";
            if (e) {
                doc.unsubscribe()
                callback(e, null)
            } else {
                switch (type) {
                    case "doc": {
                        let content_list = doc.data.data;
                        content = escape(JSON.stringify(content_list))
                        break;
                    }
                    case "md": {
                        let content_list = doc.data.ops.map(el => el.insert)
                        content = content_list.join("")
                        break;
                    }
                }
            }
            doc.unsubscribe()
            callback(null, content);
        });
    }
}

module.exports = File;
