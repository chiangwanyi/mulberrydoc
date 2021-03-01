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

    static getFolderPath(folder_hash:string) {
        return axios.get(`/api/folder/path/${folder_hash}`)
    }
}

export default DocumentsApi