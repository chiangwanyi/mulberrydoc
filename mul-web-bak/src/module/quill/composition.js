import Quill from "quill";

const Delta = Quill.import("delta");

/**
 * Composition 中文输入流
 */
class Composition {
    /**
     * 构造器
     * @param {Editor} editor Editor 对象
     * @param {Connection} conn Connection 对象
     * @param aid
     */
    constructor(editor, conn, aid) {
        this.editor = editor;
        this.quill = editor.quill;
        this.conn = conn;
        this.authorId = aid;

        // this.lock = false;
        // this.beforeText = "";
        //
        // this.quill.root.addEventListener('compositionstart', () => {
        //         console.log("composition start")
        //     this.beforeText = this.quill.getContents();
        //     this.lock = true;
        // })
        //
        // this.quill.root.addEventListener("compositionend", () => {
        //     console.log("composition end")
        //     let beforeDelta = new Delta(this.beforeText);
        //     let afterDelta = new Delta(this.quill.getContents());
        //     let diff = beforeDelta.diff(afterDelta);
        //     this.conn.doc.submitOp(diff, {source: this.quill});
        //     this.lock = false;
        // })

        this.quill.on('text-change', (delta, oldDelta, source) => {
            if (source !== 'user') return;
            console.log("text-change:", delta)
            //
            // let authorDelta = new Delta();
            // let authorFormat = {author: 1};
            //
            let flag = false;
            for (let i = 0; i < delta.ops.length; i++) {
                // if (delta.ops[i].delete) {
                // console.log("删除（跳过）", JSON.parse(JSON.stringify(delta.ops[i])))
                // if (delta.ops[i].attributes) {
                //     delta.ops[i].attributes.author = this.authorId
                // } else {
                //     delta.ops[i].attributes = {
                //         author: this.authorId
                //     }
                // }
                // continue;
                // }
                if (delta.ops[i].insert || delta.ops[i].retain) {
                    // console.log("插入：", JSON.parse(JSON.stringify(delta.ops[i])))
                    if (delta.ops[i].attributes) {
                        if (delta.ops[i].attributes.author && delta.ops[i].attributes.author !== this.authorId) {
                            flag = true;
                        }
                        delta.ops[i].attributes.author = this.authorId
                    } else {
                        delta.ops[i].attributes = {
                            author: this.authorId
                        }
                    }
                }
                // if (delta.ops[i].retain) {
                //     console.log("保持：", JSON.parse(JSON.stringify(delta.ops[i])))
                //     if (delta.ops[i].attributes) {
                //         delta.ops[i].attributes.author = this.authorId
                //     } else {
                //         delta.ops[i].attributes = {
                //             author: this.authorId
                //         }
                //     }
                // }
                // if (delta.ops[i].insert || (delta.ops[i].retain && delta.ops[i].attributes)) {
                //     delta.ops[i].attributes = delta.ops[i].attributes || {};
                //     delta.ops[i].attributes = this.authorId;
                // }
            }
            console.log("最终：", JSON.parse(JSON.stringify(delta.ops)))
            console.log("=========")

            // console.log(delta)
            // delta.ops.forEach(op => {
            //     console.log(op)
            //     if (op.delete) {
            //         return;
            //     }
            //     if (op.insert || (op.retain && op.attributes)) {
            //         //
            //         //     op.attributes = op.attributes || {};
            //         //     op.attributes.author = 1;
            //         //
            //         //     authorDelta.retain(
            //         //         op.retain || op.insert.length || 1,
            //         //         authorFormat,
            //         //     );
            //         // } else {
            //         //     authorDelta.retain(op.retain);
            //     }
            // })
            // console.log(authorDelta)
            this.conn.doc.submitOp(delta, {source: this.quill});
            console.log(this.conn.doc.data)
            if (flag === true) {
                let myDelta = new Delta(this.quill.getContents());
                console.log(myDelta)
                let dbDelta = this.conn.doc.data;
                console.log(dbDelta)
                let diff = dbDelta.diff(myDelta);
                console.log(diff)
                // this.quill.updateContents(delta, "user");
                // let dbDelta = new Delta(this.conn.doc.data);
                // let myDelta = new Delta(this.quill.getContents());
                // let diff = myDelta.diff(dbDelta);
                // console.log(JSON.parse(JSON.stringify(diff)))
                // this.quill.updateContents(diff, "user");
                // this.editor.setEditorContent(this.conn.doc.data, 'user');
            }
            // this.conn.doc.submitOp(delta, {source: this.quill});
        });
    }
}

export default Composition;
