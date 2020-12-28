module.exports = {
    lintOnSave: false,
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:9001',
                ws: true,
                changeOrigin: true,
            },
        }
    }
}