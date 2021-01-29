"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.DomUtil = exports.TimeUtil = exports.StringUtil = void 0;
var ot_1 = require("../ot/ot");
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
        return ((typeof str === "string" && str === "") ||
            str === null ||
            str === undefined);
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
                code = (H - 0xd800) * 0x400 + 0x10000 + L - 0xdc00; // 转换算法
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
        var month = d.getMonth() + 1 > 9
            ? "" + (d.getMonth() + 1)
            : "0" + (d.getMonth() + 1);
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
    /**
     * 将 oldContainer 更新为 newContainer
     * @param oldContainer 当前的容器
     * @param newContainer 最新的容器
     */
    DomUtil.updateContainer = function (oldContainer, newContainer) {
        var _this = this;
        var oldValue = [];
        var newValue = [];
        if (oldContainer !== null && newContainer !== null) {
            var oldChildren = oldContainer.children;
            var newChildren = newContainer.children;
            for (var i = 0; i < oldChildren.length; i++) {
                var item = oldChildren.item(i);
                if (item !== null) {
                    oldValue.push(item.outerHTML);
                }
            }
            for (var i = 0; i < newChildren.length; i++) {
                var item = newChildren.item(i);
                if (item !== null) {
                    newValue.push(item.outerHTML);
                }
            }
            var stringsMap_1 = ot_1.Ot.stringsMap(oldValue, newValue);
            var diff = ot_1.Ot.completeDiff(stringsMap_1.aliasA, stringsMap_1.aliasB);
            // 存在区别
            if (diff.length !== 0) {
                // 检查是否可压缩
                if (diff.length <= 3) {
                    var compressOps = ot_1.Ot.compress(diff);
                    if (compressOps !== null) {
                        diff = compressOps;
                    }
                }
            }
            // 开始更新
            var index_1 = 0;
            diff.forEach(function (op) {
                // retain
                if (op.r) {
                    index_1 += op.r;
                }
                // insert
                else if (op.i) {
                    var insertKey = op.i;
                    for (var i = 0; i < insertKey.length; i++) {
                        var text = stringsMap_1.map.get(insertKey[i]);
                        if (text === undefined) {
                            throw Error("\u63D2\u5165\u8282\u70B9\u65F6\u53D1\u751F\u5F02\u5E38\uFF0C" + text + "\u6CA1\u6709\u5BF9\u5E94\u7684\u6620\u5C04");
                        }
                        var tmpNode = _this.tmpNode(text);
                        var node = tmpNode.children.item(0);
                        if (node !== null) {
                            if (index_1 < oldContainer.children.length) {
                                oldContainer.insertBefore(node, oldContainer.children.item(index_1));
                            }
                            else {
                                oldContainer.appendChild(node);
                            }
                        }
                        index_1++;
                    }
                }
            });
        }
        else {
            throw Error("容器不存在");
        }
    };
    DomUtil.tmpNode = function (innerHtml) {
        var tmp = document.createElement("div");
        tmp.innerHTML = innerHtml;
        return tmp;
    };
    /**
     * 在容器的指定位置前插入子节点
     * @param contaienr 容器
     * @param index 要插入的位置
     * @param newNode 插入的节点
     */
    DomUtil.insertNode = function (contaienr, index, newNode) {
        if (index < contaienr.children.length) {
            contaienr.insertBefore(newNode, contaienr.children.item(index));
        }
        else if (index === contaienr.children.length) {
            contaienr.appendChild(newNode);
        }
        else {
            throw Error("超出容器范围");
        }
    };
    DomUtil.deleteNode = function (contaienr, index) {
        if (index < contaienr.children.length) {
            var d = contaienr.children.item(index);
            if (d !== null) {
                contaienr.removeChild(d);
            }
            else {
                throw Error("不存在该节点");
            }
        }
        else {
            throw Error("超出容器范围");
        }
    };
    DomUtil.updateNode = function (contaienr, index, data) {
        if (index < contaienr.children.length) {
            var d = contaienr.children.item(index);
            if (d !== null) {
                if (typeof data === op)
                    if (t === "string") {
                    }
                if (typeof (data) === "string") {
                }
                else if (typeof (data) === type)
                    op;
                {
                }
                contaienr.removeChild(d);
            }
            else {
                throw Error("不存在该节点");
            }
        }
        else {
            throw Error("超出容器范围");
        }
    };
    return DomUtil;
}());
exports.DomUtil = DomUtil;
