class Heartbeat {
    /** 用户初始心跳值 */
    value: number = 10;
    /** 用户所属组 */
    groupHash: string;

    constructor(groupHash: string) {
        this.groupHash = groupHash;
    }
}

export default Heartbeat;
