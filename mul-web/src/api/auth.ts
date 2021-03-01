import axios from "axios";

/**
 * 用户数据类型
 */
type userInfo = {
    username: string;
    password: string;
    rememberMe: boolean;
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
        return axios.post(`/api/auth/login`, {
            username: user.username,
            password: user.password,
        });
    }

    /**
     * 用户注册
     * @param user 用户注册信息
     */
    static register(user: userInfo) {
        return axios.post(`/api/auth/register`, {
            username: user.username,
            password: user.password,
        });
    }

    /**
     * 获取用户信息
     */
    static profile() {
        return axios.get(`/api/auth/profile`);
    }
}

export default AuthApi;
