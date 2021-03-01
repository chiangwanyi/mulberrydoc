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
    static createFile(type, file_hash, doc_type, callback) {
        try {
            let doc = this.connection.get(type, file_hash);
            switch (type) {
                // 文档类型
                case "doc": {
                    doc.create({
                        data: [
                            {
                                text: "<h1>标题</h1>",
                            },
                            {
                                text: "<p><br></p>",
                            },
                        ],
                    }, doc_type);
                }
            }
            callback(null);
        } catch (err) {
            callback(err);
        }
    }
}

module.exports = File;
