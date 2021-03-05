import { User } from "./connection";

/**
 * 登录数据
 */
type LoginData = {
    groupId: string;
    user: User;
};

/**
 * 消息类型
 */
enum MessageType {
    /** 上线消息：发送给除自己外的所有组内用户 */
    Online = "online",
    /** 离线消息：发送给除自己外的所有组内用户 */
    Offline = "offline",
    /** 广播消息：发送给所有组内用户 */
    Broadcast = "broadcast",
    /** 普通消息：发送给指定（多个）用户*/
    Msg = "msg",
    /** 数据同步：将数据同步给所有组内用户 */
    Sync = "sync",
    /** 错误 */
    Error = "error",
}

export { LoginData, MessageType };
