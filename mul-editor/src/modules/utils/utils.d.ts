import { op } from "../ot/ot";
/**
 * 字符串工具类
 */
declare class StringUtil {
    /**
     * 判断字符串是否为空
     * @param str 字符串
     */
    static isEmpty(str: string | undefined | null): boolean;
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
    /**
     * 将 oldContainer 更新为 newContainer
     * @param oldContainer 当前的容器
     * @param newContainer 最新的容器
     */
    static updateContainer(oldContainer: HTMLElement, newContainer: HTMLElement): void;
    static tmpNode(innerHtml: string): HTMLElement;
    /**
     * 在容器的指定位置前插入子节点
     * @param contaienr 容器
     * @param index 要插入的位置
     * @param newNode 插入的节点
     */
    static insertNode(contaienr: HTMLElement, index: number, newNode: Element): void;
    static deleteNode(contaienr: HTMLElement, index: number): void;
    static updateNode(contaienr: HTMLElement, index: number, data: string | op[] | Element): void;
}
export { StringUtil, TimeUtil, DomUtil };
