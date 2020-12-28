class File {
    /**
     * 文件 构造器
     * @param {String} type         文件类型
     * @param {String} hash         文件标识
     * @param {String} name         文件名称
     * @param {Boolean} isFavorite  是否标记为收藏
     * @param {Date} createdAt      创建时间
     * @param {Date} updatedAt      修改时间
     * @param {Date} deletedAt      移除时间
     */
    constructor(type, hash, name, isFavorite, createdAt, updatedAt, deletedAt) {
        this.type = type;
        this.hash = hash;
        this.name = name;
        this.isFavorite = isFavorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}

export default File;