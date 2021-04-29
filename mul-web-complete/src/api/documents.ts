import axios from "axios";

type folder = {
    hash: string,
    parentHash: string;
    name: string;
}

type file = {
    folderHash: string;
    type: string;
    name: string;
}

class DocumentsApi {
    static createDocuments(uid:number){
        return axios.post("/api/documents", {
            uid: uid
        })
    }

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

    /**
     * 创建文件夹
     * @param folder 文件夹信息
     */
    static createFolder(folder: folder) {
        return axios.post(`/api/folder`, {
            folder: folder
        })
    }

    static updateFolder(folder: folder) {
        return axios.patch(`/api/folder`, {
            folder: folder
        })
    }

    static updateFileAttr(hash: string, rwStatus: Number, ownership: number) {
        return axios.post(`/api/file/attr`, {
            hash: hash,
            rw_status: rwStatus,
            ownership: ownership
        })
    }

    static updateFileName(hash: string, name: string) {
        return axios.post(`/api/file/name`, {
            hash: hash,
            name: name
        })
    }

    /**
     * 创建文件
     * @param file 文件信息
     */
    static createFile(file: file) {
        return axios.post(`/api/file`, {
            file: file
        })
    }

    static recoveryFile(files: Array<String>) {
        return axios.post(`/api/file/recovery`, {
            files: files,
        })
    }

    static deleteFile(files: Array<String>) {
        return axios.post(`/api/file/delete`, {
            files: files,
        })
    }

    static queryDeletedFiles() {
        return axios.get(`/api/trash`)
    }

    static removeItems(folders: Array<String>, files: Array<String>) {
        return axios.post(`/api/documents/remove`, {
            folders: folders,
            files: files,
        })
    }

    static moveItemsTo(toFolderHash: string, folders: Array<String>, files: Array<String>) {
        return axios.post(`/api/documents/move`, {
            toFolder: toFolderHash,
            folders: folders,
            files: files,
        })
    }

    static uploadFile(data: FormData, folderHash: string) {
        return axios.post(`/api/file/upload?folder=${folderHash}`, data, {
            headers: { "Content-Type": "multipart/form-data" }
        })
    }

    static downloadFile(hash: string) {
        return axios({
            url: '/api/file/download',
            method: 'post',
            data: { hash: hash },
            responseType: 'blob'     //接收类型设置，否者返回字符型
        })
    }

    static writeFile(hash:string){
        axios.get(`/api/file/write/${hash}`).then(r => {
            let res = r.data
            console.log(res);
        })
    }
}

export default DocumentsApi