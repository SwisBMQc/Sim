package com.sy.im.model

/**
 *@Author：sy
 *@Date：2023/12/16
 */
data class FriendRequest(
    val id: Long,
    val friend: Person,
    val type: Int,  // 发送方0，接收方1
    val isAgreed : Int,
    val updateTime: Long
    )
