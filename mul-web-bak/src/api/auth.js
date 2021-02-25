import axios from "axios";

const backendPrefix = "/api/auth"

/**
 * 检查注册用户信息
 * @param user 用户信息
 * @returns {Promise<Object<any>>}
 */
const checkUser = user => {
    return axios.post(`${backendPrefix}/check`, {
        username: user.username
    })
}

/**
 * 用户注册
 * @param user 用户信息
 * @returns {Promise<Object<any>>}
 */
const register = user => {
    return axios.post(`${backendPrefix}/register`, {
        username: user.username,
        password: user.password,
    })
}

/**
 * 用户登录
 * @param user
 * @returns {Promise<Object<any>>}
 */
const login = user => {
    return axios.post(`${backendPrefix}/login`, {
        username: user.username,
        password: user.password,
    })
}

export {
    checkUser,
    register,
    login,
}