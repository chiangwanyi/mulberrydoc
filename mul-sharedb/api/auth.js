const url = require('url');
const querystring = require("querystring");

module.exports = {
    authenticate: (request, callback) => {
        const query = url.parse(request.url).query;
        let err;
        if (query !== null) {
            let params = querystring.parse(query);
            let docId = params.doc;
            let token = params.token;
            if (docId === "aaa" && token === "12345") {
                callback(undefined, docId, "jwy")
                return;
            }
        }
        callback("无权限", undefined, undefined)
    }
}