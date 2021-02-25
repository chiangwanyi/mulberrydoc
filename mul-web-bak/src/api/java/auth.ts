import axios from "axios";

const backend = "/api/auth";

/**
 * 用户数据类型
 */
type userInfo = {
    username: string;
    password: string;
};

/**
 * 权限API
 */
class AuthApi {
    /**
     * 用户登录
     * @param user 用户登录信息
     */
    static login(user: userInfo) {
        return axios.post(`${backend}/login`, {
            username: user.username,
            password: user.password,
        });
    }

    /**
     * 用户注册
     * @param user 用户注册信息
     */
    static register(user: userInfo) {
        return axios.post(`${backend}/register`, {
            username: user.username,
            password: user.password,
        });
    }

    /**
     * 获取用户信息
     */
    static profile() {
        return axios.get(`${backend}/profile`);
    }
}

export default AuthApi;
