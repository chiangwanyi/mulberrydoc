import axios from "axios";

class DocumentsApi {
    /**
     * 查询父文件夹下的所有子文件夹
     * @param folder_hash 文件夹Hash
     */
    static querySubfolder(folder_hash: string) {
        return axios.get(`/api/documents/${folder_hash}`)
    }

    /**
     * 查询文件夹信息
     * @param folder_hash 文件夹Hash
     */
    static queryFolder(folder_hash: string) {
        return axios.get(`/api/folder/${folder_hash}`)
    }

    /**
     * 获取文件夹路径
     * @param folder_hash 文件夹Hash
     */
    static getFolderPath(folder_hash: string) {
        return axios.get(`/api/folder/path/${folder_hash}`)
    }

    /**
     * 查询文件
     * @param file_hash 文件Hash
     */
    static queryFile(file_hash: string) {
        return axios.get(`/api/file/${file_hash}`)
    }
}

export default DocumentsApi