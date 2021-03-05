const app = require("express")();
const server = require("http").Server(app);
const io = require("socket.io")(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"],
    },
});

import { Group } from "./src/group";
import { Connection, Socket } from "./src/connection";
import { LoginData, MessageType } from "./src/model";

/** groupId -> Group */
let groupMap = new Map<string, Group>();
/** socketId -> Connection */
let connectionMap = new Map<string, Connection>();
/** uid -> socketId */
let userMap = new Map<string, string>();

/**
 * 用户上线
 * @param data 用户登录数据
 * @param group 对应组
 * @param socket Socket实例
 * @param online 是否发送上线消息
 * @param sync 是否同步数据
 */
function online(
    data: LoginData,
    group: Group,
    socket: Socket,
    online: boolean,
    sync: boolean
) {
    let socketId = socket.id;
    // 添加成员
    group.addMember(socketId);
    // 记录用户ID与SocketId映射
    userMap.set(data.user.uid, socketId);
    // 更新组Map
    groupMap.set(data.groupId, group);
    // 保存连接
    let connection = new Connection(data.groupId, data.user, socket);
    connectionMap.set(socketId, connection);
    if (online) {
        // 广播上线消息
        group.online(socketId, connectionMap);
    }
    if (sync) {
        // 同步数据
        group.sync(group.data(connectionMap), connectionMap);
    }
}

/**
 * 用户下线
 * @param socketId 用户socketId
 * @param offline 是否发送下线消息
 * @param sync 是否同步数据
 * @param force 是否是强制下线
 */
function offline(
    socketId: string,
    offline: boolean,
    sync: boolean,
    force: boolean
) {
    let connection = connectionMap.get(socketId);
    if (connection !== undefined) {
        let group = groupMap.get(connection.groupId);
        if (group !== undefined) {
            if (offline) {
                // 广播离线消息
                group.offline(socketId, connectionMap);
            }
            if (sync) {
                // 同步数据
                group.sync(group.data(connectionMap), connectionMap);
            }
            if (force) {
                // 发送强制下线消息
                connection.socket.emit(MessageType.Error, {
                    from: "system",
                    data: {
                        text: "账号在别处登录，强制下线",
                    },
                    timestamp: new Date().getTime(),
                });
            }
            // 移除成员
            group.removeMember(socketId);
            // 移除 UserMap 中的映射
            userMap.delete(connection.user.uid);
            // 如果组成员为空
            if (group.isEmpty()) {
                groupMap.delete(connection.groupId);
            }
        }
        // 关闭 Socket 连接
        connection.socket.disconnect(true);
        // 移除 ConnectionMap 中的映射
        connectionMap.delete(socketId);
    }
}

/** 用户连接 */
io.on("connection", (socket: Socket) => {
    let socketId = socket.id;

    /** 用户登录 */
    socket.on("login", (data: LoginData) => {
        console.log(`socketId:${socketId} 登录`);
        let group = groupMap.get(data.groupId);

        // 组不存在
        if (group === undefined) {
            group = new Group(data.groupId);
        }
        // 检查用户是否已经登录
        let check = userMap.get(data.user.uid);
        // 用户已登录，强制下线
        if (check !== undefined) {
            offline(check, false, false, true);
        }
        // 用户上线
        online(data, group, socket, check === undefined, true);
        console.log(`当前组数：${groupMap.size}`);
        console.log(`当前连接数：${connectionMap.size}`);
        console.log("=========");
    });

    /** 用户连接中断 */
    socket.on("disconnect", (reason) => {
        console.log(`[${reason}]socketId:${socketId} 退出`);
        if (reason === "transport close") {
            offline(socketId, true, true, false);
        }
        console.log(`当前组数：${groupMap.size}`);
        console.log(`当前连接数：${connectionMap.size}`);
        console.log("=========");
    });

    /** 用户广播信息 */
    socket.on("broadcast", (text: string) => {
        let connection = connectionMap.get(socketId);
        if (connection !== undefined) {
            let group = groupMap.get(connection.groupId);
            if (group !== undefined) {
                // 广播信息
                group.broadcast(socketId, text, connectionMap);
            }
        }
    });
});

server.listen(9100, () => {
    console.log("Server run at: http://localhost:9100\n");
});
