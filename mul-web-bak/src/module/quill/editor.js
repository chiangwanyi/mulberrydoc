import Quill from 'quill'
import richText from "rich-text";
import Sharedb from "sharedb/lib/client";

import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'
import Composition from "./composition";
import Connection from "./connection";
import Authorship from "./authorship";

Sharedb.types.register(richText.type);

class Editor {
    /**
     * Editor构造器
     * @param container 容器
     * @param editorOptions Editor参数
     * @param quillOptions Quill参数
     */
    constructor(container, editorOptions, quillOptions) {
        this.options = editorOptions;
        this.quill = new Quill(container, quillOptions);
        this.connection = new Connection(this);
        this.author = new Authorship();
        // this.composition = new Composition(this, this.connection, editorOptions.author_id);
    }

    /**
     * 设置 Quill 内容
     * @param {Delta} delta
     * @param {string} source
     */
    setEditorContent(delta, source) {
        this.quill.setContents(delta, source);
    }
}

export default Editor;