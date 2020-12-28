import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
// import Loading from "./plugin/Loading";
import router from './router'
import store from './store'

import 'element-ui/lib/theme-chalk/index.css'
import 'element-ui/lib/theme-chalk/display.css';

Vue.config.productionTip = false

Vue.use(ElementUI)
// Vue.use(Loading)

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')
