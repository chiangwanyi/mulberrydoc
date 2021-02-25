<template>
    <div class="editor">
        <div class="editor-container" id="container"></div>
    </div>
</template>

<script>
    import Editor from "../module/quill/editor";

    export default {
        name: "MulDoc",
        components: {},
        data() {
            return {
                editorOptions: {
                    author_id: this.$route.query.aid || 0
                },
                quillOptions: {
                    theme: 'snow',
                    modules: {
                        history: {
                            delay: 2000,
                            maxStack: 500,
                            userOnly: true
                        },
                        toolbar: {
                            container: [
                                ['bold', 'italic', 'underline', 'strike'],
                                ['blockquote', 'code-block'],
                                // [{'header': 1}, {'header': 2}],
                                // [{'list': 'ordered'}, {'list': 'bullet'}],
                                // [{'script': 'sub'}, {'script': 'super'}],
                                // [{'indent': '-1'}, {'indent': '+1'}],
                                // [{'direction': 'rtl'}],
                                // [{'size': ['small', false, 'large', 'huge']}],
                                // [{'header': [1, 2, 3, 4, 5, 6, false]}],
                                // [{'color': []}, {'background': []}],
                                // [{'font': []}],
                                // [{'align': []}],
                                // ['clean'],
                                // ['link', 'image', 'video'],
                                // ['undo', 'redo']
                            ],
                            handlers: {
                                undo() {
                                    this.quill.history.undo()
                                },
                                redo() {
                                    this.quill.history.redo()
                                }
                            }
                        },
                    },
                    placeholder: 'Insert text here ...'
                },
                editor: null,
            }
        },
        created() {
            // Icons['undo'] = `<button type="button" class="ql-undo">撤销</button>`;
            // Icons['redo'] = `<button type="button" class="ql-undo">重做</button>`;
        },
        methods: {},
        mounted() {
            console.log()
            this.editor = new Editor(this.$el.querySelector("#container"), this.editorOptions, this.quillOptions);
            let endpoint = "ws://192.168.31.123:8080?docId=asdfa2o3u0zsd9f0";
            let collection = "1:doc";
            let docId = "asdfa2o3u0zsd9f0";
            this.editor.connection.startConnect(endpoint, collection, docId);
        }
    }
</script>

<style scoped>

</style>