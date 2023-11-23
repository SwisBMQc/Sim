package com.sy.im.model

import androidx.compose.runtime.Stable

/**
 * 客户端的状态集合
 */
@Stable // 该集合不轻易改变
enum class ServerState {
    Logout,         //下线
    Connecting,
    ConnectSuccess,
    ConnectFailed,
    UserSigExpired, // 签名过期
    KickedOffline   // 被踢下线
}