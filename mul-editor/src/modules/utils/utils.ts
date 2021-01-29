import { Ot, op } from "../ot/ot";

/**
 * 字符串工具类
 */
class StringUtil {
    /**
     * 判断字符串是否为空
     * @param str 字符串
     */
    static isEmpty(str: string | undefined | null): boolean {
        return (
            (typeof str === "string" && str === "") ||
            str === null ||
            str === undefined
        );
    }

    /**
     * emoji 字符串编码
     * @param str 字符串
     */
    static utf16ToStr(str: string): string {
        let regExp = /[\ud800-\udbff][\udc00-\udfff]/g; // 检测utf16字符正则
        return str.replace(regExp, (char) => {
            let H, L, code;
            if (char.length === 2) {
                H = char.charCodeAt(0); // 取出高位
                L = char.charCodeAt(1); // 取出低位
                code = (H - 0xd800) * 0x400 + 0x10000 + L - 0xdc00; // 转换算法
                return `&#${code.toString(16)};`;
            } else {
                return char;
            }
        });
    }

    /**
     * emoje 字符串解码
     * @param str 字符串
     */
    static strToUtf16(str: string): string {
        let reg = /&#([a-f|0-9]+);/g;
        return str.replace(reg, (el) => {
            return String.fromCodePoint(
                parseInt("0x" + el.replace("&#", "").replace(";", ""), 16)
            );
        });
    }
}

/**
 * 时间工具类
 */
class TimeUtil {
    /**
     * 当前时间字符串
     */
    static nowDateTime(): string {
        let d = new Date();
        let month =
            d.getMonth() + 1 > 9
                ? `${d.getMonth() + 1}`
                : `0${d.getMonth() + 1}`;
        let date = d.getDate() > 9 ? `${d.getDate()}` : `0${d.getDate()}`;
        let hours = d.getHours() > 9 ? `${d.getHours()}` : `0${d.getHours()}`;
        let minutes =
            d.getMinutes() > 9 ? `${d.getMinutes()}` : `0${d.getMinutes()}`;
        let seconds =
            d.getSeconds() > 9 ? `${d.getSeconds()}` : `0${d.getSeconds()}`;
        let milliseconds: string;
        if (d.getMilliseconds() < 10) {
            milliseconds = `00${d.getMilliseconds()}`;
        } else if (d.getMilliseconds() < 100) {
            milliseconds = `0${d.getMilliseconds()}`;
        } else {
            milliseconds = `${d.getMilliseconds()}`;
        }
        return `${d.getFullYear()}-${month}-${date} ${hours}:${minutes}:${seconds}:${milliseconds}`;
    }
}

/**
 * DOM工具类
 */
class DomUtil {
    /**
     * 将 oldContainer 更新为 newContainer
     * @param oldContainer 当前的容器
     * @param newContainer 最新的容器
     */
    static updateContainer(
        oldContainer: HTMLElement,
        newContainer: HTMLElement
    ): void {
        let oldValue: string[] = [];
        let newValue: string[] = [];
        if (oldContainer !== null && newContainer !== null) {
            let oldChildren = oldContainer.children;
            let newChildren = newContainer.children;
            for (let i = 0; i < oldChildren.length; i++) {
                let item = oldChildren.item(i);
                if (item !== null) {
                    oldValue.push(item.outerHTML);
                }
            }
            for (let i = 0; i < newChildren.length; i++) {
                let item = newChildren.item(i);
                if (item !== null) {
                    newValue.push(item.outerHTML);
                }
            }
            let stringsMap = Ot.stringsMap(oldValue, newValue);
            let diff = Ot.completeDiff(stringsMap.aliasA, stringsMap.aliasB);

            // 存在区别
            if (diff.length !== 0) {
                // 检查是否可压缩
                if (diff.length <= 3) {
                    let compressOps = Ot.compress(diff);
                    if (compressOps !== null) {
                        diff = compressOps;
                    }
                }
            }

            // 开始更新
            let index = 0;
            diff.forEach((op) => {
                // retain
                if (op.r) {
                    index += op.r;
                }
                // insert
                else if (op.i) {
                    let insertKey = op.i;
                    for (let i = 0; i < insertKey.length; i++) {
                        let text = stringsMap.map.get(insertKey[i]);
                        if (text === undefined) {
                            throw Error(
                                `插入节点时发生异常，${text}没有对应的映射`
                            );
                        }

                        let tmpNode = this.tmpNode(text);
                        let node = tmpNode.children.item(0);
                        if (node) {
                            this.insertNode(oldContainer, index, node);
                            index++;
                        } else {
                            throw Error("创建临时节点失败");
                        }
                    }
                }
            });
        } else {
            throw Error("容器不存在");
        }
    }

    static tmpNode(innerHtml: string): HTMLElement {
        let tmp = document.createElement("div");
        tmp.innerHTML = innerHtml;
        return tmp;
    }

    /**
     * 在容器的指定位置前插入子节点
     * @param contaienr 容器
     * @param index 要插入的位置
     * @param newNode 插入的节点
     */
    static insertNode(
        contaienr: HTMLElement,
        index: number,
        newNode: Element
    ): void {
        if (index < contaienr.children.length) {
            contaienr.insertBefore(newNode, contaienr.children.item(index));
        } else if (index === contaienr.children.length) {
            contaienr.appendChild(newNode);
        } else {
            throw Error("超出容器范围");
        }
    }

    static deleteNode(contaienr: HTMLElement, index: number): void {
        if (index < contaienr.children.length) {
            let target = contaienr.children.item(index);
            if (target) {
                contaienr.removeChild(target);
            } else {
                throw Error("不存在该节点");
            }
        } else {
            throw Error("超出容器范围");
        }
    }

    static updateNode(
        contaienr: HTMLElement,
        index: number,
        node: Element | null,
        ops: op[] | null
    ): void {
        if (index < contaienr.children.length) {
            let target = contaienr.children.item(index);
            if (target) {
                if (node) {
                    target.outerHTML = node.outerHTML;
                } else if (ops) {
                    let newValue = Ot.apply(target.outerHTML, ops);
                    target.outerHTML = newValue;
                }
            } else {
                throw Error("不存在该节点");
            }
        } else {
            throw Error("超出容器范围");
        }
    }
}

export { StringUtil, TimeUtil, DomUtil };
