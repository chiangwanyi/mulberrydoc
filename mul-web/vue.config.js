module.exports = {
    lintOnSave: false,
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: "http://localhost:12000",
                ws: true,
                changeOrigin: true,
            },
        },
    },
};