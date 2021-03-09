<template>
    <div id="rich-text" :style="{ height: height }" v-show="ready">
        <div v-if="ready" id="top">
            <div class="header">
                <div style="justify-content: flex-start;">
                    <i class="back el-icon-arrow-left"></i>
                    <p class="file-name" contenteditable="true">{{file.name}}</p>
                    <el-divider direction="vertical"></el-divider>
                    <div class="members">
                        <el-avatar size="small" v-for="user in members" :key="user.uid">{{user.avatar}}</el-avatar>
                        <i class="el-icon-caret-bottom"></i>
                    </div>
                    <el-divider direction="vertical"></el-divider>
                    <div class="file-status">
                        <span v-show="!onchange && !freeze" style="font-size: 14px;color: #cccccc;"><i
                                class="el-icon-circle-check"></i>&nbsp;<span
                                style="border-bottom: 1px solid #ccc;">变更已自动保存</span></span>
                        <span v-show="onchange && !freeze" style="font-size: 14px;color: #cccccc;"><i
                                class="el-icon-loading"></i>&nbsp;<span
                                style="border-bottom: 1px solid #ccc;">正在保存</span></span>
                        <span v-show="freeze" style="font-size: 14px;color: rgb(204,9,9);"><i
                                class="el-icon-circle-close"></i>&nbsp;<span
                                style="border-bottom: 1px solid rgb(204,9,9);">离线</span></span>
                    </div>
                </div>
                <div style="justify-content: flex-end;">
                    <el-button type="default" size="mini">时间轴</el-button>
                    <el-divider direction="vertical"></el-divider>
                    <el-button type="default" size="mini">下载</el-button>
                    <el-button type="default" size="mini">分享</el-button>
                    <el-divider direction="vertical"></el-divider>
                    <el-avatar>{{this.user.id}}</el-avatar>
                </div>
            </div>
        </div>
        <div class="editor-container" id="container"></div>
    </div>
</template>

<script>
    import Editor from "miks-collaborative-editor";
    import EditorEvents from "miks-collaborative-editor/editor-events";
    import 'quill/dist/quill.snow.css'
    import AuthApi from "@/api/auth";
    import {StringUtil} from "@/util/tools";
    import DocumentsApi from "@/api/documents";
    import {io} from "socket.io-client";

    let editorOptions = {
        authorship: {
            author: null,

            // 当前用户段落的颜色
            authorColor: "rgba(208,101,252,0.15)",

            // 其他用户段落的颜色
            colors: [
                "rgba(247,180,82,0.15)",
                "rgba(239,108,145,0.15)",
                "rgba(142,110,213,0.15)",
                "rgba(106,188,145,0.15)",
                "rgba(90,197,195,0.15)",
                "rgba(114,151,227,0.15)",
                "rgba(155,200,110,0.15)",
                "rgba(235,213,98,0.15)",
                "rgba(212,153,185,0.15)"
            ],
            handlers: {
                // 当文档中出现了一个新的用户ID时，使用这个函数来获取用户的信息
                // 必须返回一个Promise
                getAuthorInfoById: (authorId) => {
                    console.log(`出现了新的用户：${authorId}`);
                    return new Promise((resolve, reject) => {

                        let author = {
                            id: 4,
                            name: 'Another author'
                        };

                        if (author) {
                            resolve(author);
                        } else {
                            reject("user not found");
                        }
                    });
                }
            }
        },
        image: {
            handlers: {
                // 上传DataURI格式的图片到服务器，并返回一个图片URL
                // 必须返回一个Promise
                imageDataURIUpload: (dataURI) => {

                    console.log(dataURI);

                    return new Promise((resolve) => {
                        resolve('https://yd.wemiks.com/banner-2d980584-yuanben.svg');
                    });
                },

                // 上传一个图片外链到服务器，并返回一个内部的图片URL
                // 必须返回一个Promise
                imageSrcUpload: (src) => {

                    console.log(src);

                    return new Promise((resolve) => {
                        resolve('https://yd.wemiks.com/banner-2d980584-yuanben.svg');
                    });
                },

                // 图片上传错误的处理
                imageUploadError: (err) => {
                    console.log("image upload error: " + err);
                }
            }
        }
    };

    let quillOptions = {
        modules: {
            toolbar: {
                container: "#toolbar",
            },
            history: {
                delay: 0,
                maxStack: 20,
                userOnly: true,
            },
        },
        theme: 'snow'
    };

    export default {
        name: "RichText",
        data() {
            return {
                // 编辑器高度
                height: `${document.documentElement.clientHeight}px`,
                // 用户信息
                user: {},
                // 文件类型
                type: "",
                // 文件Hash
                hash: "",
                // 文件信息
                file: null,
                // Ready
                ready: false,
                // 编辑器实例
                editor: null,

                // Doc连接实例
                doc: null,
                // Connection连接实例
                connection: null,

                // 纯文本
                content: "",
                // 当前用户列表
                members: [],
                // 文件修改中
                onchange: false,
                // 文件冻结中
                freeze: true,
            }
        },
    }
</script>

<style lang="scss" scoped>

</style>