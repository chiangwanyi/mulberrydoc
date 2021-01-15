const url = require('url');
const querystring = require("querystring");

module.exports = {
    authenticate: (request, callback) => {
        const query = url.parse(request.url).query;
        let err;
        if (query !== null) {
            let params = querystring.parse(query);
            let groupId = params.groupId;
            let userId = params.userId;
            callback(undefined, groupId, userId)
            return;
        }
        callback("无权限", undefined, undefined)
    }
}