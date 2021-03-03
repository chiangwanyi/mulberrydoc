import { Connection, Socket } from "./connection";

/**
 * 消息类型
 */
enum MessageType {
    /** 上线消息 */
    Online,
    /** 离线消息 */
    Offline,
    /** 广播消息 */
    Broadcast,
    /** 普通消息 */
    Msg,
}

/**
 * 消息
 */
type Message = {
    /** 用户ID */
    uid: string;
    /** 消息类型 */
    type: MessageType;
    /** 内容 */
    text: string;
    /** 时间戳 */
    timestamp: number;
};

/**
 * 组
 */
class Group {
    /** 组ID */
    groupId: string;
    /** 连接列表：socketId */
    members: string[];
    /** 消息列表 */
    messages: Message[];

    constructor(groupId: string) {
        this.groupId = groupId;
        this.members = [];
        this.messages = [];
    }

    /**
     * 添加成员
     * @param socketId SocketId
     */
    addMember(socketId: string) {
        if (this.members.includes(socketId)) {
            throw Error("连接重复，请重试");
        }
        this.members.push(socketId);
    }

    /**
     * 移除成员
     * @param socketId SocketId
     */
    removeMember(socketId: string) {
        let index = this.members.indexOf(socketId);
        if (index > -1) {
            this.members.splice(index, 1);
            return true;
        }
        return false;
    }

    /**
     * 判断组是否为空
     */
    isEmpty() {
        return this.members.length == 0;
    }

    /**
     * 组内广播信息
     * @param self 自己的socketId
     * @param data 消息数据
     * @param connectionMap 连接池
     */
    broadcast(
        self: string,
        data: Message,
        connectionMap: Map<string, Connection>
    ) {
        console.log(`socketId:${self} 广播消息`);
        this.members.forEach((socketId) => {
            if (socketId !== self) {
                let connection = connectionMap.get(socketId);
                if (connection !== undefined) {
                    connection.socket.emit("msg", data);
                }
            }
        });
        console.log("=========");
    }
}

export { Group, Message, MessageType };
