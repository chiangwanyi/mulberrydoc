<template>
    <div id="auth">
        <el-row class="container">
            <el-col :span="8" class="op" :style="{ height: height }">
                <el-row class="login">
                    <el-col>
                        <p>欢迎使用&nbsp;<strong>桑叶文档</strong></p>
                    </el-col>
                    <el-col>
                        <el-form
                            class="loginForm"
                            ref="loginForm"
                            :model="loginInfo"
                            label-position="top"
                            label-width="80px"
                            :rules="loginRules"
                        >
                            <el-form-item label="用户名" prop="username">
                                <el-input
                                    v-model="loginInfo.username"
                                ></el-input>
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
                                    >登录
                                </el-button>
                            </el-form-item>
                            <el-form-item>
                                <a href="#" @click="showRegisterDialog = true"
                                    >注册账号</a
                                >
                            </el-form-item>
                        </el-form>
                    </el-col>
                </el-row>
            </el-col>
            <el-col
                :span="16"
                class="pic"
                :style="{ height: height, backgroundImage: `url(${img})` }"
            >
            </el-col>
        </el-row>
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
                    >注册
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import DocumentsApi from "@/api/documents";
import AuthApi from "../api/auth";

export default {
    name: "Authentication",
    data() {
        // 校验 注册用户名
        const validateUsername = (rule, value, callback) => {
            if (!value) {
                return callback(new Error("用户名不能为空"));
            }
            if (value.length < 1 || value.length > 20) {
                return callback(new Error("用户名长度应该在 1 到 20 个字符"));
            }
            AuthApi.checkName(this.registerInfo)
                .then((res) => {
                    let data = res.data;
                    console.log(data);
                    if (data.status === 200) {
                        if (data.data === false) {
                            callback();
                        } else {
                            return callback(new Error(`该用户名已注册`));
                        }
                    } else {
                        return callback(new Error(`内部错误`));
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
            // 页面高度
            height: `${document.documentElement.clientHeight}px`,

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

            // pic背景图片
            img: require("../assets/login-bg-1.jpg"),
            onLogin: false,
            onRegister: false,
            showRegisterDialog: false,
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
                    {
                        required: true,
                        message: "请输入密码",
                        trigger: "blur",
                    },
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
        };
    },
    methods: {
        /**
         * 用户登录
         */
        login() {
            this.$refs["loginForm"].validate((valid) => {
                if (valid) {
                    this.onLogin = true;
                    AuthApi.login(this.loginInfo).then((r) => {
                        this.onLogin = false;
                        const res = r.data;
                        console.log(res);
                        if (res.status === 200) {
                            this.$message({
                                message: `${res.msg}`,
                                type: "success",
                            });
                            this.$router.push("/");
                        } else {
                            this.$message({
                                message: `${res.msg.message}`,
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
            console.log("pass");
            this.$refs["registerForm"].validate((valid) => {
                if (valid) {
                    this.onRegister = true;
                    AuthApi.register(this.registerInfo).then((r) => {
                        this.onRegister = false;
                        const res = r.data;
                        console.log(res);
                        if (res.status === 200) {
                            DocumentsApi.createDocuments(res.data.id).then(
                                (r) => {
                                    if (r.data.status === 200) {
                                        this.$message({
                                            message: `${res.msg}`,
                                            type: "success",
                                        });
                                        this.$router.push("/");
                                    }
                                }
                            );
                        } else {
                            this.$message({
                                message: `${res.msg.message}`,
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
#auth {
    border-color: #e2e8f0;
    height: 100%;

    .container {
        height: 100%;

        .op {
            padding: 20px;

            .login {
                p {
                    font-size: 26px;
                }

                label.el-form-item__label {
                    height: 36px;
                    line-height: 36px;
                    padding: 0;
                }
            }
        }

        .pic {
            background-size: cover;
        }
    }
}
</style>