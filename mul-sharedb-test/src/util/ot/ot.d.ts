import { TextOp } from "ot-text-unicode";
/**
 * 操作
 */
interface operation {
    i?: string;
    d?: string;
    r?: number;
    u?: [string, string];
}
/**
 * 简写操作
 */
declare type op = number | string | {
    d: string;
};
/**
 * Operational Transformation
 */
declare class Ot {
    /**
     * 获取 before -> after 的完整操作
     * @param b 原始字符串 before
     * @param a 当前字符串 after
     */
    static completeDiff(b: string, a: string): operation[];
    /**
     * 获取 before -> after 的简写操作
     * @param b 原始字符串 before
     * @param a 当前字符串 after
     */
    diff(b: string, a: string): op[];
    /**
     * 压缩 (R)ID操作 为 包含 U 的操作
     * @param ops 操作
     */
    compress(ops: operation[]): operation[] | null;
    /**
     * 获取 beforeStrings -> afterStrings 的映射关系
     * @param bs 原始字符串列表 beforeStrings
     * @param as 当前字符串列表 afterStrings
     */
    stringsMap(bs: string[], as: string[]): {
        aliasB: string;
        aliasA: string;
        map: Map<string, string>;
    };
    /**
     * 对字符串执行操作
     * @param str 字符串
     * @param ops 操作（简写）
     */
    apply(str: string, ops: op[]): string;
    /**
     * 获取操作的逆操作
     * @param ops 操作（简写）
     */
    invert(ops: op[]): TextOp | null;
}
export default Ot;
