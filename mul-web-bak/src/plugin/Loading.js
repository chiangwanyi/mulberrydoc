import {Loading} from "element-ui";

export default {
    install(Vue) {
        Vue.prototype.$showLoading = function (position) {
            this.loading = Loading.service({
                target: document.querySelector(position)
            });
        }
    }
}