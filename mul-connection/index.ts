const app = require("express")();
const server = require("http").Server(app);
const io = require("socket.io")(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"],
    },
});

import { Group, Message, MessageType } from "./src/group";
import { Connection, Socket } from "./src/connection";
import { LoginData } from "./src/model";

/** groupId -> Group */
let groupMap = new Map<string, Group>();
/** socketId -> Connection */
let connectionMap = new Map<string, Connection>();

/** 用户连接 */
io.on("connection", (socket: Socket) => {
    let socketId = socket.id;

    /** 用户登录 */
    socket.on("login", (data: LoginData) => {
        console.log(`socketId:${socketId} 登录`);

        let groupId = data.groupId;
        let user = data.user;

        let group = groupMap.get(groupId);

        // 组不存在
        if (group === undefined) {
            group = new Group(groupId);
        }
        // 添加成员
        group.addMember(socketId);
        groupMap.set(groupId, group);
        // 保存连接
        let connection = new Connection(groupId, user, socket);
        connectionMap.set(socketId, connection);
        // 广播上线消息
        group.broadcast(
            socketId,
            {
                uid: connection.user.uid,
                type: MessageType.Online,
                text: `user:${connection.user.uid} 上线`,
                timestamp: new Date().getTime(),
            },
            connectionMap
        );
        console.log(`当前组数：${groupMap.size}`);
        console.log(`当前连接数：${connectionMap.size}`);
        console.log("=========");
    });

    /** 用户连接中断 */
    socket.on("disconnect", (reason) => {
        console.log(`[${reason}]socketId:${socketId} 退出`);
        let connection = connectionMap.get(socketId);
        if (connection !== undefined) {
            let group = groupMap.get(connection.groupId);
            if (group !== undefined) {
                // 广播离线消息
                group.broadcast(
                    socketId,
                    {
                        uid: connection.user.uid,
                        type: MessageType.Offline,
                        text: `user:${connection.user.uid} 离线`,
                        timestamp: new Date().getTime(),
                    },
                    connectionMap
                );
                // 移除成员
                group.removeMember(socketId);
                // 如果组成员为空
                if (group.isEmpty()) {
                    groupMap.delete(connection.groupId);
                }
            }
            // 关闭 Socket 连接
            connection.socket.disconnect(true);
            // 删除连接
            connectionMap.delete(socketId);
        }
        console.log(`当前组数：${groupMap.size}`);
        console.log(`当前连接数：${connectionMap.size}`);
        console.log("=========");
    });

    /** 用户广播信息 */
    socket.on("broadcast", (data: Message) => {
        let connection = connectionMap.get(socketId);
        if (connection !== undefined) {
            let group = groupMap.get(connection.groupId);
            if (group !== undefined) {
                // 广播信息
                group.broadcast(socketId, data, connectionMap);
            }
        }
    });
});

server.listen(9100, () => {
    console.log("Serve on http://localhost:9100\n");
});
