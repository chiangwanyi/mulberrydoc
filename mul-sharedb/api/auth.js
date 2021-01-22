const url = require('url');
const querystring = require("querystring");

module.exports = {
    authenticate: (request, callback) => {
        const query = url.parse(request.url).query;
        if (query !== null) {
            let params = querystring.parse(query);
            let token = params.token;
            callback(undefined, token)
            return;
        }
        callback("无权限", undefined, undefined)
    }
}