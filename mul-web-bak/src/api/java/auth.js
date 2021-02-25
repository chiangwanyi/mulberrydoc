"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = __importDefault(require("axios"));
var backend = "/api/auth";
/**
 * 权限API
 */
var AuthApi = /** @class */ (function () {
    function AuthApi() {
    }
    /**
     * 用户登录
     * @param user 用户登录信息
     */
    AuthApi.login = function (user) {
        return axios_1.default.post(backend + "/login", {
            username: user.username,
            password: user.password,
        });
    };
    /**
     * 用户注册
     * @param user 用户注册信息
     */
    AuthApi.register = function (user) {
        return axios_1.default.post(backend + "/register", {
            username: user.username,
            password: user.password,
        });
    };
    /**
     * 获取用户信息
     */
    AuthApi.profile = function () {
        return axios_1.default.get(backend + "/profile");
    };
    return AuthApi;
}());
exports.default = AuthApi;
