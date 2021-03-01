module.exports = {
    lintOnSave: false,
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: "http://192.168.31.123:12000",
                ws: true,
                changeOrigin: true,
            },
        },
    },
};