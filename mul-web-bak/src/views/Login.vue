<template>
    <div id="login">
        <el-form
            class="login"
            :model="loginInfo"
            ref="loginForm"
            label-position="right"
            label-width="80px"
            :rules="loginRules"
        >
            <el-form-item label="用户名" prop="username">
                <el-input v-model="loginInfo.username"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input
                    v-model="loginInfo.password"
                    type="password"
                ></el-input>
            </el-form-item>
            <el-form-item>
                <el-button
                    class="submit"
                    type="primary"
                    @click="login"
                    :loading="onLogin"
                    >登录</el-button
                >
            </el-form-item>
            <el-form-item>
                <a href="#" @click="showRegisterDialog = true">注册账号</a>
            </el-form-item>
        </el-form>
        <el-dialog title="注册" :visible.sync="showRegisterDialog" width="30%">
            <el-form
                :model="registerInfo"
                ref="registerForm"
                status-icon
                label-position="right"
                :rules="registerRules"
                label-width="80px"
            >
                <el-form-item label="用户名" prop="username">
                    <el-input
                        v-model="registerInfo.username"
                        auto-complete="off"
                    ></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input
                        v-model="registerInfo.password"
                        type="password"
                        autocomplete="off"
                    ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input
                        v-model="registerInfo.confirmPassword"
                        type="password"
                        autocomplete="off"
                    ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="showRegisterDialog = false">取消</el-button>
                <el-button
                    type="primary"
                    @click="register"
                    :loading="onRegister"
                    >注册</el-button
                >
            </div>
        </el-dialog>
    </div>
</template>

<script>
import AuthApi from "./api/java/auth";
import * as Documents from "../api/documents";

export default {
    name: "Login",
    data() {
        // 校验 注册用户名
        const validateUsername = (rule, value, callback) => {
            if (!value) {
                return callback(new Error("用户名不能为空"));
            }
            if (value.length < 1 || value.length > 20) {
                return callback(new Error("用户名长度应该在 1 到 20 个字符"));
            }
            Auth.checkUser(this.registerInfo)
                .then((res) => {
                    let data = res.data;
                    if (data.status === 200) {
                        callback();
                    } else {
                        return callback(new Error(`${data.msg}`));
                    }
                })
                .catch(() => {
                    return callback(new Error("未知错误"));
                });
        };
        // 校验 注册密码
        const validatePassword = (rule, value, callback) => {
            if (!value) {
                return callback(new Error("密码不能为空"));
            }
            if (value.length < 8 || value.length > 20) {
                return callback(new Error("密码长度应该在 8 到 20 个字符"));
            }
            callback();
        };
        // 校验 注册重复密码
        const validateConfirmPassword = (rule, value, callback) => {
            if (!value) {
                return callback(new Error("密码不能为空"));
            }
            if (value !== this.registerInfo.password) {
                return callback(new Error("与密码不一致"));
            }
            callback();
        };
        return {
            // 登录的信息
            loginInfo: {
                username: "",
                password: "",
                rememberMe: false,
            },
            // 注册的信息
            registerInfo: {
                username: "",
                password: "",
                confirmPassword: "",
            },
            onLogin: false,
            onRegister: false,
            // 登录的校验规则
            loginRules: {
                username: [
                    {
                        required: true,
                        message: "请输入用户名",
                        trigger: "blur",
                    },
                ],
                password: [
                    { required: true, message: "请输入密码", trigger: "blur" },
                ],
            },
            // 注册的校验规则
            registerRules: {
                username: [{ validator: validateUsername, trigger: "blur" }],
                password: [{ validator: validatePassword, trigger: "blur" }],
                confirmPassword: [
                    { validator: validateConfirmPassword, trigger: "blur" },
                ],
            },
            showRegisterDialog: false,
        };
    },
    methods: {
        login() {
            this.$refs["loginForm"].validate((valid) => {
                if (valid) {
                    this.onLogin = true;
                    AuthApi.login(this.loginInfo).then((r) => {
                        this.onLogin = false;
                        let res = r.data;
                        if (res.status === 200) {
                            this.$message({
                                message: `${res.msg}`,
                                type: "success",
                            });
                            this.$store.state.user = res.data;
                            this.$router.push("/");
                        } else {
                            this.$message({
                                message: `${res.msg}`,
                                type: "error",
                            });
                        }
                    });
                } else {
                    return false;
                }
            });
        },
        register() {
            this.$refs["registerForm"].validate((valid) => {
                if (valid) {
                    this.onRegister = true;
                    Auth.register(this.registerInfo).then((r) => {
                        this.onRegister = false;
                        let res = r.data;
                        if (res.status === 200) {
                            // 创建 文档空间
                            Documents.createDocuments(res.data.id).then((r) => {
                                let res = r.data;
                                if (res.status === 200) {
                                    this.showRegisterDialog = false;
                                    // 将用户名传递给登录表单
                                    this.loginInfo.username = this.registerInfo.username;
                                    this.registerInfo.username = "";
                                    this.registerInfo.password = "";
                                    this.registerInfo.confirmPassword = "";
                                    this.$message({
                                        message: `注册成功`,
                                        type: "success",
                                    });
                                } else {
                                    this.$message({
                                        message: `${res.msg}`,
                                        type: "error",
                                    });
                                }
                            });
                        } else {
                            this.$message({
                                message: `${res.msg}`,
                                type: "error",
                            });
                        }
                    });
                } else {
                    return false;
                }
            });
        },
    },
};
</script>

<style lang="scss">
.login {
    width: 33.33%;
    margin: 0 auto;

    button.submit {
        width: 100%;
    }
}
</style>