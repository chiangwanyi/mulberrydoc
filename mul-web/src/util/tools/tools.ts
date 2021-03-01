/**
 * 字符串工具类
 */
class StringUtil {
    /**
     * 判断字符串是否为空
     * @param str 字符串
     */
    static isEmpty(str: string): boolean {
        return (str === "") || str === null || str === undefined;
    }

    /**
     * emoji 字符串编码
     * @param str 字符串
     */
    static utf16ToStr(str: string): string {
        let regExp = /[\ud800-\udbff][\udc00-\udfff]/g; // 检测utf16字符正则
        return str.replace(regExp, char => {
            let H, L, code;
            if (char.length === 2) {
                H = char.charCodeAt(0); // 取出高位
                L = char.charCodeAt(1); // 取出低位
                code = (H - 0xD800) * 0x400 + 0x10000 + L - 0xDC00; // 转换算法
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
        return str.replace(reg, el => {
            return String.fromCodePoint(parseInt("0x" + el.replace("&#", "").replace(";", ""), 16));
        })
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
        let month = d.getMonth() + 1 > 9 ? `${d.getMonth() + 1}` : `0${d.getMonth() + 1}`;
        let date = d.getDate() > 9 ? `${d.getDate()}` : `0${d.getDate()}`;
        let hours = d.getHours() > 9 ? `${d.getHours()}` : `0${d.getHours()}`;
        let minutes = d.getMinutes() > 9 ? `${d.getMinutes()}` : `0${d.getMinutes()}`;
        let seconds = d.getSeconds() > 9 ? `${d.getSeconds()}` : `0${d.getSeconds()}`;
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
    // /**
    //  * 将 oldContainer 更新为 newContainer
    //  * @param oldContainer 当前的HTML
    //  * @param newContainer 最新的HTML
    //  */
    // static updateContainer(oldContainer:HTMLElement, newContainer:HTMLElement):void {
    //
    // }

    static tmpContainer(innerHtml: string): HTMLElement {
        let tmp = document.createElement("div");
        tmp.innerHTML = innerHtml;
        return tmp;
    }
}

export {StringUtil, TimeUtil, DomUtil};
