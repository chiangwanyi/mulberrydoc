import Vue from "vue";
import ElementUI from 'element-ui';
import App from "./App.vue";
import router from "./router";
import store from "./store";
import axios from 'axios';

import 'element-ui/lib/theme-chalk/index.css';

Vue.use(ElementUI);

Vue.config.productionTip = false;

// 全局拦截器处理【无权限】
axios.interceptors.response.use(response => {
    let res = response.data;
    if (res.status === 403) {
        router.push("/auth").then(r => {
        })
    }
    return response;
}, error => {
})

new Vue({
    router,
    store,
    render: (h) => h(App),
}).$mount("#app");
