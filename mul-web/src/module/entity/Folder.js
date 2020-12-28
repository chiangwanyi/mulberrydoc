class Folder {
    /**
     * 文件夹 构造器
     * @param {String} hash         文件夹标识
     * @param {String} parentHash   父文件夹标识
     * @param {String} name         文件夹名称
     * @param {String} path         文件夹完整路径
     * @param {Number} depth        文件夹深度
     * @param {Boolean} isFavorite  是否标记为收藏
     * @param {[File]} fileList     文件列表
     * @param {Date} createdAt      创建时间
     * @param {Date} updatedAt      修改时间
     * @param {Date} deletedAt      移除时间
     */
    constructor(hash, parentHash, name, path, depth, isFavorite, fileList, createdAt, updatedAt, deletedAt) {
        this.hash = hash;
        this.parentHash = parentHash;
        this.name = name;
        this.path = path;
        this.depth = depth;
        this.isFavorite = isFavorite
        this.fileList = fileList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}

export default Folder;