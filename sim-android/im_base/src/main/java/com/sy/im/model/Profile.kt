package com.sy.im.model

/**
 *@Author：sy
 *@Date：2023/12/9
 */
data class Person(
    var userId: String = "",
    var nickname: String = "",
    var imgUrl: String = "",
    var gender: String = "",
    var remark: String = "",
    var signature: String = ""
) {

    val showName: String
        get() {
            return remark.ifBlank {
                nickname.ifBlank {
                    userId
                }
            }
        }
}

data class Group(
    var groupId: Int,
    var ownerId: String = "",
    var groupName: String = "",
    var imgUrl: String = "",
    var introduction: String = "",
    val createTime: Long,
    val memberCount: Int
)




