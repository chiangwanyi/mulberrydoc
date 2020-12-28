import axios from "axios";

const backendPrefix = "/api/folder";

/**
 * 创建文件夹
 * @param {{name: string, parentHash: string}} folder 文件夹信息
 * @returns {Promise<Object<any>>}
 */
const createFolder = folder => {
    return axios.post(`${backendPrefix}`, {
        parentHash: folder.parentHash,
        name: folder.name
    })
}

export {
    createFolder,
}