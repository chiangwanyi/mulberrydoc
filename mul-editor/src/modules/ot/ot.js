"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.Ot = void 0;
var ot_text_unicode_1 = require("ot-text-unicode");
var quill_delta_1 = __importDefault(require("quill-delta"));
/**
 * 字符串映射生成器
 */
var uniqueChar = /** @class */ (function () {
    function uniqueChar() {
        // 当前ID
        this.id = 48;
    }
    /**
     * 获取下一个字符
     * unicode可见字符 5080个
     * [48-126]      79个字符：  0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~
     * [20000-25000] 5001个字符：汉字
     */
    uniqueChar.prototype.next = function () {
        var char = String.fromCharCode(this.id++);
        if (this.id > 126) {
            this.id = 20000;
        }
        else if (this.id > 25000) {
            throw Error("超出ID范围");
        }
        return char;
    };
    return uniqueChar;
}());
/**
 * Operational Transformation
 */
var Ot = /** @class */ (function () {
    function Ot() {
    }
    /**
     * 获取 before -> after 的完整操作
     * @param b 原始字符串 before
     * @param a 当前字符串 after
     */
    Ot.completeDiff = function (b, a) {
        var delta = new quill_delta_1.default().insert(b).diff(new quill_delta_1.default().insert(a));
        var op = [];
        var index = 0;
        delta.ops.forEach(function (el) {
            if (el.retain !== undefined) {
                op.push({ r: el.retain });
                index += el.retain;
            }
            else if (el.insert !== undefined) {
                op.push({ i: el.insert.toString() });
            }
            else if (el.delete !== undefined) {
                op.push({ d: b.substr(index, el.delete) });
                index += el.delete;
            }
        });
        return op;
    };
    /**
     * 获取 before -> after 的简写操作
     * @param b 原始字符串 before
     * @param a 当前字符串 after
     */
    Ot.diff = function (b, a) {
        var delta = new quill_delta_1.default().insert(b).diff(new quill_delta_1.default().insert(a));
        var ops = [];
        var index = 0;
        delta.ops.forEach(function (el) {
            if (el.retain !== undefined) {
                ops.push(el.retain);
                index += el.retain;
            }
            else if (el.insert !== undefined) {
                ops.push(el.insert.toString());
            }
            else if (el.delete !== undefined) {
                ops.push({ d: b.substr(index, el.delete) });
                index += el.delete;
            }
        });
        return ops;
    };
    /**
     * 压缩 (R)ID操作 为 包含 U 的操作
     * @param ops 操作
     */
    Ot.compress = function (ops) {
        var compressOps = [];
        // 只有操作数 >= 2 && <= 3 的操作可以压缩
        if (ops.length >= 2 && ops.length <= 3) {
            var r_1 = -1;
            var i_1 = "";
            var d_1 = "";
            // 取出每一类操作
            ops.forEach(function (op) {
                if (op.r !== undefined) {
                    r_1 = op.r;
                }
                else if (op.i !== undefined) {
                    i_1 = op.i;
                }
                else if (op.d !== undefined) {
                    d_1 = op.d;
                }
            });
            // 是否包含I操作、D操作
            if (i_1 === "" || d_1 === "") {
                return null;
            }
            // 保持 retain 操作
            if (r_1 !== -1) {
                compressOps.push({ r: r_1 });
            }
            // 如果 insert 长度 > delete 长度，则压缩为 RUI 操作
            if (i_1.length > d_1.length) {
                compressOps.push({ u: [d_1, i_1.substr(0, d_1.length)] });
                compressOps.push({ i: i_1.substr(d_1.length, i_1.length) });
            }
            // 如果 insert 长度 < delete 长度，则压缩为 RUD 操作
            else if (i_1.length < d_1.length) {
                compressOps.push({ u: [d_1.substr(0, i_1.length), i_1] });
                compressOps.push({ d: d_1.substr(i_1.length, d_1.length) });
            }
            // 如果 insert 长度 = delete 长度，则压缩为 RU 操作
            else {
                compressOps.push({ u: [d_1, i_1] });
            }
        }
        else {
            return null;
        }
        return compressOps;
    };
    /**
     * 获取 beforeStrings -> afterStrings 的映射关系
     * @param bs 原始字符串列表 beforeStrings
     * @param as 当前字符串列表 afterStrings
     */
    Ot.stringsMap = function (bs, as) {
        // 字符串 -> 字符串映射
        var stringToAliasMap = new Map();
        // 字符串映射 -> 字符串
        var aliasToStringMap = new Map();
        // 原始字符串列表的映射字符串
        var aliasB = "";
        // 当前字符串列表的映射字符串
        var aliasA = "";
        var unc = new uniqueChar();
        bs.forEach(function (el) {
            var char;
            if (!stringToAliasMap.has(el)) {
                char = unc.next();
                stringToAliasMap.set(el, char);
            }
            else {
                char = stringToAliasMap.get(el);
            }
            aliasB += char;
        });
        as.forEach(function (el) {
            var char;
            if (!stringToAliasMap.has(el)) {
                char = unc.next();
                stringToAliasMap.set(el, char);
            }
            else {
                char = stringToAliasMap.get(el);
            }
            aliasA += char;
        });
        stringToAliasMap.forEach(function (v, k) {
            aliasToStringMap.set(v, k);
        });
        return { aliasB: aliasB, aliasA: aliasA, map: aliasToStringMap };
    };
    /**
     * 对字符串执行操作
     * @param str 字符串
     * @param ops 操作（简写）
     */
    Ot.apply = function (str, ops) {
        return ot_text_unicode_1.type.apply(str, ops);
    };
    /**
     * 获取操作的逆操作
     * @param ops 操作（简写）
     */
    Ot.invert = function (ops) {
        if (ot_text_unicode_1.type.invert) {
            return ot_text_unicode_1.type.invert(ops);
        }
        else {
            return null;
        }
    };
    return Ot;
}());
exports.Ot = Ot;
