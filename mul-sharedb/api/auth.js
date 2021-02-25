const querystring = require("querystring");
const request = require("request");

const backend = "http://localhost:12000/api";

class Auth {
    /**
     * 验证用户权限
     * @param request HTTP Request
     * @param callback 回调
     */
    static authenticate(req, callback) {
        let data = querystring.parse(req.url.replace("/?", ""));
        if (data !== null) {
            let token = data.token;
            console.log(data.token);
            callback();
            // request(
            //     {
            //         timeout: 5000,
            //         method: "GET",
            //         url: `${backend}/fa`,
            //         qs: {
            //             token: data.token,
            //         },
            //     },
            //     (err, res, body) => {
            //         if (!err && res.statusCode == 200) {
            //             console.log(body);
            //             callback(undefined);
            //         } else {
            //             console.error(err);
            //             callback(err);
            //         }
            //     }
            // );
        } else {
            callback(new Error("参数错误"));
        }
    }
}

module.exports = Auth;
