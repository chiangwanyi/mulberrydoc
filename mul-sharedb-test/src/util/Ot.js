class Ot {
    /**
     * OT 构造器
     */
    constructor() {
    }

    /**
     * 获取 before -> latest 的原子操作
     * @param before {string}
     * @param latest {string}
     * @returns {Object} 操作
     */
    opt(before, latest) {
        /**
         * 求最长公共前缀
         * @param before
         * @param latest
         * @returns {number} 最长公共前缀长度
         */
        function lcp(before, latest) {

            /**
             * 求当前最长公共前缀
             * @param strs 字符列表
             * @param mid mid
             * @returns {boolean} 结果
             */
            function startsWithCommon(strs, mid) {
                let common_str = strs[0].substring(0, mid);
                for (let i = 1; i < strs.length; i++) {
                    if (!strs[i].startsWith(common_str)) {
                        return false;
                    }
                }
                return true;
            }

            if (before === "" || latest === "") {
                return 0;
            }
            if (before === latest) {
                return before.length;
            }
            let min_len = Number.MAX_SAFE_INTEGER;
            let strs = [before, latest];
            strs.map(str => {
                min_len = Math.min(min_len, str.length);
            })
            let left = 1;
            let right = min_len;
            while (left <= right) {
                let mid = Math.floor((left + right) / 2);
                if (startsWithCommon(strs, mid)) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            return Math.floor((left + right) / 2);
        }

        /**
         * 求最长公共后缀
         * @param before
         * @param latest
         * @returns {number} 最长公共后缀长度
         */
        function lce(before, latest) {
            if (before === "" || latest === "") {
                return 0;
            }
            if (before === latest) {
                return before.length;
            }
            let min_len = Number.MAX_SAFE_INTEGER;
            let strs = [before, latest];
            strs.map(str => {
                min_len = Math.min(min_len, str.length);
            })
            for (let i = 0; i < min_len; i++) {
                if (before[before.length - i - 1] !== latest[latest.length - i - 1]) {
                    return i;
                }
            }
            return min_len;
        }

        let pl = lcp(before, latest);
        let sl = lce(before, latest);

        // 无动作 NULL
        if (before === latest && pl === sl) {
            return {}
        }

        // 全替换动作 RDI
        if (pl === 0 && sl === 0) {
            return {
                r: 0,
                d: {
                    len: before.length,
                    del: before
                },
                i: latest,
            }
        }

        // 末尾删除 RD
        if (latest.length < before.length && pl === latest.length) {
            return {
                r: pl,
                d: {
                    len: before.length - latest.length,
                    del: before.substr(pl, before.length - latest.length),
                },
            }
        }

        // 字符串增长 RI
        if (latest.length > before.length) {
            // 追加动作 RI
            if (pl === before.length) {
                return {
                    r: pl,
                    i: latest.substring(pl, latest.length),
                }
            }
            // 插入动作 RI
            if (pl > 0 && pl + sl >= before.length) {
                return {
                    r: pl,
                    i: latest.substr(pl, latest.length - before.length),
                }
            }
        }

        // 部分截取替换动作 RDI
        if (pl > 0 || sl > 0) {
            // 处理 abba型 字符串删除
            if (pl + sl > latest.length) {
                return {
                    r: pl,
                    d: {
                        len: before.length - latest.length,
                        del: before.substr(pl, before.length - latest.length),
                    },
                    i: "",
                }
            }
            return {
                r: pl,
                d: {
                    len: before.length - (pl + sl),
                    del: before.substr(pl, before.length - (pl + sl)),
                },
                i: latest.substr(pl, latest.length - (pl + sl),)
            }
        }
    }

    /**
     * 判断操作类型
     * @param opt {Object} 操作
     * @returns {string} 操作类型
     */
    opt_type(opt) {
        if (opt.r !== undefined && opt.d !== undefined && opt.i !== undefined) {
            return "RDI";
        } else if (opt.i === undefined) {
            return "RD";
        }
        return "RI";
    }

    /**
     * 执行操作
     * @param before {string}
     * @param opt {Object}
     * @returns {{operation_type: string, before: string, latest: string}}
     */
    execute(before, opt) {
        let opt_type = this.opt_type(opt);
        let latest = "";
        switch (opt_type) {
            case "RI":
                latest = `${before.substr(0, opt.r)}${opt.i}${before.substring(opt.r, before.length)}`;
                break;
            case "RD":
                latest = `${before.substr(0, opt.r)}`;
                break;
            case "RDI":
                latest = `${before.substr(0, opt.r)}${opt.i}${before.substring(opt.r + opt.d.len, before.length)}`;
                break;
        }
        return {
            operation_type: opt_type,
            before: before,
            latest: latest
        }
    }
}

export default Ot;