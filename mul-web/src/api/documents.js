import axios from "axios";

const backendPrefix = "/api/documents";

/**
 * 创建文档空间
 * @param uid 用户 ID
 * @returns {Promise<Object<any>>}
 */
const createDocuments = uid => {
    return axios.post(`${backendPrefix}`, {
        id: uid
    })
}

/**
 * 查询文档空间
 * @param hash 文件夹标识
 * @returns {Promise<Object<any>>}
 */
const getDocuments = hash => {
    return axios.get(`${backendPrefix}/${hash}`);
}

export {
    createDocuments,
    getDocuments
}