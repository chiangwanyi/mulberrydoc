const isEmpty = str => {
    return (typeof (str) === "string" && str === "") || str === null || str === undefined;
}

const utf16ToStr = str => {
    let regExp = /[\ud800-\udbff][\udc00-\udfff]/g; // 检测utf16字符正则
    return str.replace(regExp, function (char) {
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

const strToUtf16 = str => {
    let reg = /&#([a-f|0-9]+);/g;
    return str.replace(reg, el => {
        return String.fromCodePoint(parseInt("0x" + el.replace("&#", "").replace(";", ""), 16));
    })
}

export {
    isEmpty,
    utf16ToStr,
    strToUtf16,
}