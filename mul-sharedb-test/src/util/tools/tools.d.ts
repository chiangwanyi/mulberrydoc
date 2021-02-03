/**
 * 字符串工具类
 */
declare class StringUtil {
    /**
     * 判断字符串是否为空
     * @param str 字符串
     */
    static isEmpty(str: string): boolean;
    /**
     * emoji 字符串编码
     * @param str 字符串
     */
    static utf16ToStr(str: string): string;
    /**
     * emoje 字符串解码
     * @param str 字符串
     */
    static strToUtf16(str: string): string;
}
/**
 * 时间工具类
 */
declare class TimeUtil {
    /**
     * 当前时间字符串
     */
    static nowDateTime(): string;
}
/**
 * DOM工具类
 */
declare class DomUtil {
}
export { StringUtil, TimeUtil, DomUtil };
