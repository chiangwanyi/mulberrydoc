import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        addr:{
            sharedb: "ws://localhost:9003",
            connection: "http://localhost:9100",
        },
        user: {
            username: "万艺",
            avatar: "https://giler.oss-cn-chengdu.aliyuncs.com/avatar/ot_ia5Wq_7LGbNAiSbYevNmal_Dw.png"
        }
    },
    mutations: {},
    actions: {},
    modules: {}
})
