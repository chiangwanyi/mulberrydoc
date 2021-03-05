import { Connection, User, Socket } from "./connection";

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
}

/**
 * 消息
 */
type Message = {
    /** 发送方ID*/
    from: string;
    /** 数据 */
    data: object;
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
     * 上线
     * @param self 发送方socketId
     * @param connectionMap 连接池
     */
    online(self: string, connectionMap: Map<string, Connection>) {
        this.members.forEach((socketId) => {
            if (self !== socketId) {
                let connection = connectionMap.get(socketId);
                if (connection !== undefined) {
                    connection.socket.emit(MessageType.Online, {
                        from: "system",
                        data: {
                            text: `用户：${connection.user.uid}上线`,
                        },
                        timestamp: new Date().getTime(),
                    });
                }
            }
        });
    }

    /**
     * 下线
     * @param self 发送方socketId
     * @param connectionMap 连接池
     */
    offline(self: string, connectionMap: Map<string, Connection>) {
        this.members.forEach((socketId) => {
            if (self !== socketId) {
                let connection = connectionMap.get(socketId);
                if (connection !== undefined) {
                    connection.socket.emit(MessageType.Offline, {
                        from: "system",
                        data: {
                            text: `用户：${connection.user.uid}下线`,
                        },
                        timestamp: new Date().getTime(),
                    });
                }
            }
        });
    }

    /**
     * 广播
     * @param self 需要跳过的socketId
     * @param text 消息内容
     * @param connectionMap 连接池
     */
    broadcast(
        self: string,
        text: string,
        connectionMap: Map<string, Connection>
    ) {
        let from = connectionMap.get(self);
        if (from !== undefined) {
            let uid = from.user.uid;
            this.members.forEach((socketId) => {
                if (self !== socketId) {
                    let connection = connectionMap.get(socketId);
                    if (connection !== undefined) {
                        let msg = {
                            from: uid,
                            data: {
                                text: text,
                            },
                            timestamp: new Date().getTime(),
                        };
                        connection.socket.emit(MessageType.Broadcast, msg);
                        this.messages.unshift(msg);
                    }
                }
            });
        }
    }

    sync(data: object, connectionMap: Map<string, Connection>) {
        this.members.forEach((socketId) => {
            let connection = connectionMap.get(socketId);
            if (connection !== undefined) {
                connection.socket.emit(MessageType.Sync, {
                    from: "system",
                    data: data,
                    timestamp: new Date().getTime(),
                });
            }
        });
    }

    data(connectionMap: Map<string, Connection>) {
        let members: User[] = [];
        this.members.forEach((socketId) => {
            let connection = connectionMap.get(socketId);
            if (connection !== undefined) {
                let user = connection.user;
                members.push(user);
            }
        });
        return {
            groupId: this.groupId,
            members: members,
            messages: this.messages,
        };
    }
}

export { Group, Message, MessageType };
