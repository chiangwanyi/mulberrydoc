import Quill from "quill";
import {type} from "ot-text-unicode";

const Delta = Quill.import("delta");

class UniChar {
    constructor() {
        // 当前ID
        this.id = 48;
    }

    /**
     * 获取下一个字符
     *
     * unicode可见字符 5080个
     * [48-126]      79个字符：  0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~
     * [20000-25000] 5001个字符：汉字
     * @returns {string}
     */
    next() {
        let char = String.fromCharCode(this.id++);
        if (this.id > 126) {
            this.id = 20000;
        } else if (this.id > 25000) {
            throw Error("超出范围");
        }
        return char;
    }
}


class Ot {
    /**
     * 获取字符串 a -> b 的操作（完整名称）
     * @param a {string} 字符串a
     * @param b {string} 字符串b
     * @returns {[]} 操作
     */
    completeDiff(a, b) {
        let delta = new Delta().insert(a).diff(new Delta().insert(b));
        let ops = [];
        let index = 0;
        delta.ops.forEach(el => {
            if (el.retain !== undefined) {
                ops.push({r: el.retain});
                index += el.retain;
            } else if (el.insert !== undefined) {
                ops.push({i: el.insert});
            } else if (el.delete !== undefined) {
                ops.push({d: a.substr(index, el.delete)});
                index += el.delete;
            }
        });
        return ops;
    }

    /**
     * 获取
     * @param {string[]} a
     * @param {string[]} b
     */
    map(a, b) {
        let map = new Map();
        let hashA = "";
        let hashB = "";

        let retA = a.filter(el => typeof (el) !== "string");
        let retB = b.filter(el => typeof (el) !== "string");
        if (retA.length === 0 && retB.length === 0) {
            let unc = new UniChar();
            a.forEach(el => {
                let char;
                if (!map.has(el)) {
                    char = unc.next();
                    map.set(el, char);
                } else {
                    char = map.get(el);
                }
                hashA += char;
            })

            b.forEach(el => {
                let char;
                if (!map.has(el)) {
                    char = unc.next();
                    map.set(el, char);
                } else {
                    char = map.get(el);
                }
                hashB += char;
            })

            let hashMap = new Map();
            map.forEach((v, k) => {
                hashMap.set(v, k);
            })
            return {hashA, hashB, hashMap};
        }
        throw Error("仅支持字符串数组");
    }

    /**
     * 获取字符串 a -> b 的操作
     * @param a {string} 字符串a
     * @param b {string} 字符串b
     * @returns {[]} 操作
     */
    diff(a, b) {
        let delta = new Delta().insert(a).diff(new Delta().insert(b));
        let ops = [];
        let index = 0;
        delta.ops.forEach(el => {
            if (el.retain !== undefined) {
                ops.push(el.retain);
                index += el.retain;
            } else if (el.insert !== undefined) {
                ops.push(el.insert);
            } else if (el.delete !== undefined) {
                ops.push({d: a.substr(index, el.delete)});
                index += el.delete;
            }
        });
        return ops;
    }

    /**
     * 对字符串执行操作
     * @param str {string} 字符串
     * @param ops {[]} 操作
     * @returns {string} 操作
     */
    apply(str, ops) {
        return type.apply(str, ops);
    }

    /**
     * 获取当前操作的逆操作
     * @param ops {[]} 操作
     * @returns {[]} 逆操作
     */
    invert(ops) {
        return type.invert(ops);
    }

    /**
     * 压缩 RID操作 为 包含 U 的操作
     * @param ops {[]} 操作
     */
    compress(ops) {
        let compressOps = [];
        if (ops.length >= 2 && ops.length <= 3) {
            let r = null, i = null, d = null;
            // 取出每一类操作
            ops.forEach(op => {
                if (op.r !== undefined) {
                    r = op.r;
                } else if (op.i !== undefined) {
                    i = op.i;
                } else if (op.d !== undefined) {
                    d = op.d;
                }
            })
            // 是否包含I操作、D操作
            if (i === null || d === null) {
                return null;
            }
            if (r !== null) {
                compressOps.push({r: r});
            }
            // 如果 Insert 长度 > Delete 长度，则压缩为 RUI 操作
            if (i.length > d.length) {
                compressOps.push({u: [d, i.substr(0, d.length)]});
                compressOps.push({i: i.substr(d.length, i.length)})
            }
            // 如果 Insert 长度 < Delete 长度，则压缩为 RUD 操作
            else if (i.length < d.length) {
                compressOps.push({u: [d.substr(0, i.length), i]});
                compressOps.push({d: d.substr(i.length, d.length)});
            }
            // 如果 Insert 长度 = Delete 长度，则压缩为 RU 操作
            else {
                compressOps.push({u: [d, i]});
            }
        } else {
            compressOps = null;
        }
        return compressOps;
    }
}

export default new Ot();