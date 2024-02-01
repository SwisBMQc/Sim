package com.sy.im.ui.view.chat

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.android.parcel.Parcelize

/**
 *@Author：sy
 *@Date：2024/1/28
 * 使用 @Parcelize 页面跳转传值
 */
@Stable
sealed class Chat(open val id: String) : Parcelable {

//    @Parcelize
//    class PrivateChat(override val id: String) : Chat(id = id)
//
//    @Parcelize
//    class GroupChat(override val id: String) : Chat(id = id)

}