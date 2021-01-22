import E from 'wangeditor';


class Editor {
    constructor(containerId) {
        // 编辑器实例
        this.editor = new E(containerId);
        // 变更监听锁
        this.lock = false;
    }
}

export default Editor;