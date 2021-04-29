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

    static logout(uid: number) {
        return axios.post("/api/auth/logout", {
            uid: uid
        })
    }

    /**
     * 获取用户信息
     */
    static profile() {
        return axios.get(`/api/auth/profile`);
    }

    static updateAvatar(avatar: string) {
        return axios.post(`/api/auth/avatar`, {
            avatar: avatar
        })
    }

    static queryUserById(id: number) {
        return axios.get(`/api/auth/search/${id}`)
    }

    static checkName(user: userInfo) {
        return axios.get(`/api/auth/name/${user.username}`)
    }
}

export default AuthApi;
