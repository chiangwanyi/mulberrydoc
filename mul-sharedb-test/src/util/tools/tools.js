"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.DomUtil = exports.TimeUtil = exports.StringUtil = void 0;
/**
 * 字符串工具类
 */
var StringUtil = /** @class */ (function () {
    function StringUtil() {
    }
    /**
     * 判断字符串是否为空
     * @param str 字符串
     */
    StringUtil.isEmpty = function (str) {
        return (typeof (str) === "string" && str === "") || str === null || str === undefined;
    };
    /**
     * emoji 字符串编码
     * @param str 字符串
     */
    StringUtil.utf16ToStr = function (str) {
        var regExp = /[\ud800-\udbff][\udc00-\udfff]/g; // 检测utf16字符正则
        return str.replace(regExp, function (char) {
            var H, L, code;
            if (char.length === 2) {
                H = char.charCodeAt(0); // 取出高位
                L = char.charCodeAt(1); // 取出低位
                code = (H - 0xD800) * 0x400 + 0x10000 + L - 0xDC00; // 转换算法
                return "&#" + code.toString(16) + ";";
            }
            else {
                return char;
            }
        });
    };
    /**
     * emoje 字符串解码
     * @param str 字符串
     */
    StringUtil.strToUtf16 = function (str) {
        var reg = /&#([a-f|0-9]+);/g;
        return str.replace(reg, function (el) {
            return String.fromCodePoint(parseInt("0x" + el.replace("&#", "").replace(";", ""), 16));
        });
    };
    return StringUtil;
}());
exports.StringUtil = StringUtil;
/**
 * 时间工具类
 */
var TimeUtil = /** @class */ (function () {
    function TimeUtil() {
    }
    /**
     * 当前时间字符串
     */
    TimeUtil.nowDateTime = function () {
        var d = new Date();
        var month = d.getMonth() + 1 > 9 ? "" + (d.getMonth() + 1) : "0" + (d.getMonth() + 1);
        var date = d.getDate() > 9 ? "" + d.getDate() : "0" + d.getDate();
        var hours = d.getHours() > 9 ? "" + d.getHours() : "0" + d.getHours();
        var minutes = d.getMinutes() > 9 ? "" + d.getMinutes() : "0" + d.getMinutes();
        var seconds = d.getSeconds() > 9 ? "" + d.getSeconds() : "0" + d.getSeconds();
        var milliseconds;
        if (d.getMilliseconds() < 10) {
            milliseconds = "00" + d.getMilliseconds();
        }
        else if (d.getMilliseconds() < 100) {
            milliseconds = "0" + d.getMilliseconds();
        }
        else {
            milliseconds = "" + d.getMilliseconds();
        }
        return d.getFullYear() + "-" + month + "-" + date + " " + hours + ":" + minutes + ":" + seconds + ":" + milliseconds;
    };
    return TimeUtil;
}());
exports.TimeUtil = TimeUtil;
/**
 * DOM工具类
 */
var DomUtil = /** @class */ (function () {
    function DomUtil() {
    }
    return DomUtil;
}());
exports.DomUtil = DomUtil;
