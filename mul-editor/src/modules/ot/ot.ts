import { type } from "ot-text-unicode";
import Delta from "quill-delta";

/**
 * 字符串映射生成器
 */
class uniqueChar {
    // 当前ID
    id: number;

    constructor() {
        // 当前ID
        this.id = 48;
    }

    /**
     * 获取下一个字符
     * unicode可见字符 5080个
     * [48-126]      79个字符：  0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~
     * [20000-25000] 5001个字符：汉字
     */
    next(): string {
        let char = String.fromCharCode(this.id++);
        if (this.id > 126) {
            this.id = 20000;
        } else if (this.id > 25000) {
            throw Error("超出ID范围");
        }
        return char;
    }
}

/**
 * 操作
 */
interface operation {
    // insert 插入的字符串
    i?: string;
    // delete 删除的字符串
    d?: string;
    // retain 跳过（保持）的字符串长度
    r?: number;
    // update 将 str1 更新为 str2
    u?: [string, string];
}

/**
 * 简写操作
 */
type op =
    | number
    | string
    | {
          d: string;
      };

/**
 * Operational Transformation
 */
class Ot {
    /**
     * 获取 before -> after 的完整操作
     * @param b 原始字符串 before
     * @param a 当前字符串 after
     */
    static completeDiff(b: string, a: string): operation[] {
        let delta = new Delta().insert(b).diff(new Delta().insert(a));
        let op: operation[] = [];
        let index = 0;
        delta.ops.forEach((el) => {
            if (el.retain !== undefined) {
                op.push({ r: el.retain });
                index += el.retain;
            } else if (el.insert !== undefined) {
                op.push({ i: el.insert.toString() });
            } else if (el.delete !== undefined) {
                op.push({ d: b.substr(index, el.delete) });
                index += el.delete;
            }
        });
        return op;
    }

    /**
     * 获取 before -> after 的简写操作
     * @param b 原始字符串 before
     * @param a 当前字符串 after
     */
    static diff(b: string, a: string): op[] {
        let delta = new Delta().insert(b).diff(new Delta().insert(a));
        let ops: op[] = [];
        let index = 0;
        delta.ops.forEach((el) => {
            if (el.retain !== undefined) {
                ops.push(el.retain);
                index += el.retain;
            } else if (el.insert !== undefined) {
                ops.push(el.insert.toString());
            } else if (el.delete !== undefined) {
                ops.push({ d: b.substr(index, el.delete) });
                index += el.delete;
            }
        });
        return ops;
    }

    /**
     * 压缩 (R)ID操作 为 包含 U 的操作
     * @param ops 操作
     */
    static compress(ops: operation[]): operation[] | null {
        let compressOps: operation[] = [];
        // 只有操作数 >= 2 && <= 3 的操作可以压缩
        if (ops.length >= 2 && ops.length <= 3) {
            let r: number = -1;
            let i: string = "";
            let d: string = "";
            // 取出每一类操作
            ops.forEach((op) => {
                if (op.r !== undefined) {
                    r = op.r;
                } else if (op.i !== undefined) {
                    i = op.i;
                } else if (op.d !== undefined) {
                    d = op.d;
                }
            });
            // 是否包含I操作、D操作
            if (i === "" || d === "") {
                return null;
            }
            // 保持 retain 操作
            if (r !== -1) {
                compressOps.push({ r: r });
            }
            // 如果 insert 长度 > delete 长度，则压缩为 RUI 操作
            if (i.length > d.length) {
                compressOps.push({ u: [d, i.substr(0, d.length)] });
                compressOps.push({ i: i.substr(d.length, i.length) });
            }
            // 如果 insert 长度 < delete 长度，则压缩为 RUD 操作
            else if (i.length < d.length) {
                compressOps.push({ u: [d.substr(0, i.length), i] });
                compressOps.push({ d: d.substr(i.length, d.length) });
            }
            // 如果 insert 长度 = delete 长度，则压缩为 RU 操作
            else {
                compressOps.push({ u: [d, i] });
            }
        } else {
            return null;
        }
        return compressOps;
    }

    /**
     * 获取 beforeStrings -> afterStrings 的映射关系
     * @param bs 原始字符串列表 beforeStrings
     * @param as 当前字符串列表 afterStrings
     */
    static stringsMap(bs: string[], as: string[]) {
        // 字符串 -> 字符串映射
        let stringToAliasMap = new Map<string, string>();
        // 字符串映射 -> 字符串
        let aliasToStringMap = new Map<string, string>();
        // 原始字符串列表的映射字符串
        let aliasB = "";
        // 当前字符串列表的映射字符串
        let aliasA = "";

        let unc = new uniqueChar();
        bs.forEach((el) => {
            let char;
            if (!stringToAliasMap.has(el)) {
                char = unc.next();
                stringToAliasMap.set(el, char);
            } else {
                char = stringToAliasMap.get(el);
            }
            aliasB += char;
        });

        as.forEach((el) => {
            let char;
            if (!stringToAliasMap.has(el)) {
                char = unc.next();
                stringToAliasMap.set(el, char);
            } else {
                char = stringToAliasMap.get(el);
            }
            aliasA += char;
        });

        stringToAliasMap.forEach((v, k) => {
            aliasToStringMap.set(v, k);
        });
        return { aliasB, aliasA, map: aliasToStringMap };
    }

    /**
     * 对字符串执行操作
     * @param str 字符串
     * @param ops 操作（简写）
     */
    static apply(str: string, ops: op[]) {
        return type.apply(str, ops);
    }

    /**
     * 获取操作的逆操作
     * @param ops 操作（简写）
     */
    static invert(ops: op[]) {
        if (type.invert) {
            return type.invert(ops);
        } else {
            return null;
        }
    }
}

export { Ot, op, operation };
