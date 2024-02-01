package com.sy.im.model

/**
 * 客户端的状态集合
 */
enum class ServerState {
    Logout,         //下线
    Connecting,
    ConnectSuccess,
    ConnectFailed,
    UserSigExpired, // 签名过期
    KickedOffline   // 被踢下线
}