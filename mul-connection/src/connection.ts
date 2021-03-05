import { Message } from "./group";

/**
 * Socket 实例
 */
type Socket = {
    on: (arg0: string, arg1: (data: any) => void) => void;
    emit: (key: string, data: Message) => void;
    disconnect: (close: boolean) => void;
    id: string;
};

/**
 * 用户
 */
type User = {
    uid: string;
    avatar: string;
};

/**
 * 连接实例
 */
class Connection {
    groupId: string;
    /** 用户 */
    user: User;
    /** Socket */
    socket: Socket;

    constructor(groupId: string, user: User, socket: Socket) {
        this.groupId = groupId;
        this.user = user;
        this.socket = socket;
    }
}

export { Connection, User, Socket };
