package com.sy.im.logic

import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sy.im.api.FriendAPI
import com.sy.im.coroutine.ChatCoroutineScope
import com.sy.im.interf.IMSendCallback
import com.sy.im.model.FriendRequest
import com.sy.im.model.Person
import com.sy.im.protobuf.MessageProtobuf
import com.sy.im.provider.ToastProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.reflect.Type
import kotlin.coroutines.resume


/**
 *@Author：sy
 *@Date：2023/12/15
 */
class FriendLogic {

    val requestList = MutableStateFlow<List<FriendRequest>>(value = emptyList())
    val friendList = MutableStateFlow<List<Person>>(value = emptyList())

    /**
     * 验证好友申请
     */
    suspend fun verifyFriend(friendRequestId: Long, result:Int) {
        return suspendCancellableCoroutine {
            FriendAPI.verifyFriend(friendRequestId.toLong(), result,object : IMSendCallback {
                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    refreshRequestList()
                    refreshFriendList()
                }
                override fun onError(error: String?) {
                    ToastProvider.showToast(error)
                }
            })
        }
    }

    suspend fun sendFriendRequest(friendId: String) {
        return suspendCancellableCoroutine {
            FriendAPI.sendFriendRequest(friendId, object : IMSendCallback {
                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    ToastProvider.showToast("已发送")
                }

                override fun onError(error: String?) {
                    ToastProvider.showToast(error)
                }
            })
        }
    }

    private suspend fun getFriendRequests(): List<FriendRequest>? {
        return suspendCancellableCoroutine {
            FriendAPI.getFriendRequests(object : IMSendCallback {
                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    val extend = JSON.parseObject(msg?.head?.extend)
                    val data = extend["data"] as Map<String, Any>

                    // 取出列表
                    val friendRequestListType: Type =
                        object : TypeToken<List<FriendRequest?>?>() {}.type
                    val requests: List<FriendRequest> = Gson().fromJson(data["requests"].toString(), friendRequestListType)
                    println("***** $requests *************")
                    it.resume(value = requests)
                }
                override fun onError(error: String?) {
                    ToastProvider.showToast(error)
                    it.resume(value = null)
                }
            })

        }
    }

    fun refreshRequestList() {
        ChatCoroutineScope.launch {
           requestList.emit(value = getFriendRequests()?.sortedBy { it.updateTime }?: emptyList())
        }
    }

    fun refreshFriendList() {
        ChatCoroutineScope.launch {
            friendList.emit(value = getFriendLists()?.sortedBy { // 按字母排序
                it.showName
            } ?: emptyList())
        }
    }


    private suspend fun getFriendLists(): List<Person>? {
        return suspendCancellableCoroutine { continuation ->

            FriendAPI.getFriendList(object : IMSendCallback {
                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    val extend = JSON.parseObject(msg?.head?.extend)
                    val data = extend["data"] as Map<String, Any>
                    // 取出列表
                    val friendListType: Type =
                        object : TypeToken<List<Person?>?>() {}.type
                    val friends: List<Person> = Gson().fromJson(data["friends"].toString(), friendListType)
                    continuation.resume(value = friends)
                }

                override fun onError(error: String?) {
                    ToastProvider.showToast(error)
                    continuation.resume(value = null)
                }
            })
        }
    }
}