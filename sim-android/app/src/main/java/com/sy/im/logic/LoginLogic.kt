package com.sy.im.logic

import android.util.Log
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.sy.im.api.AccountAPI
import com.sy.im.coroutine.ChatCoroutineScope
import com.sy.im.interf.IMSendCallback
import com.sy.im.logic.SimAPI.personProfile
import com.sy.im.logic.SimAPI.serverConnectState
import com.sy.im.model.Person
import com.sy.im.model.ServerState
import com.sy.im.protobuf.MessageProtobuf
import com.sy.im.provider.AccountProvider
import com.sy.im.provider.ToastProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoginLogic {

    suspend fun login(userId: String,password: String): Boolean {

        return suspendCancellableCoroutine { continuation ->

            AccountAPI.login(userId, password, object : IMSendCallback {

                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    val extend = JSON.parseObject(msg?.head?.extend)
                    val data = extend["data"] as Map<String, Any>
                    Log.i("sim-loginLogic","token："+ data["token"])

                    // 取出token
                    AccountProvider.onUserLogin(userId, data["token"] as String)

                    continuation.resume(value = true) // 恢复挂起，返回成功结果
                }

                override fun onError(error: String) {
                    ToastProvider.showToast("登录失败 $error")
                    continuation.resume(value = false)
                }
            })
        }
    }

    suspend fun register(userId: String,password: String): Boolean {

        return suspendCancellableCoroutine { continuation ->

            AccountAPI.register(userId, password, object : IMSendCallback {

                override fun onSuccess(msg: MessageProtobuf.Msg) {
                    val extend = JSON.parseObject(msg?.head?.extend)
                    val data = extend["data"] as Map<String, Any>

                    // 取出token
                    AccountProvider.onUserLogin(userId, data["token"] as String)

                    // 取出user 更新用户
                    val user: Person = Gson().fromJson(data["person"].toString(), Person::class.java)
                    refreshUser(user)

                    continuation.resume(value = true) // 恢复挂起，返回成功结果
                }

                override fun onError(error: String) {
                    ToastProvider.showToast("注册失败 $error")
                    continuation.resume(value = false)
                }
            })
        }
    }

    suspend fun logout(): Boolean {
        Log.i("sim-loginLogic","logout"+ personProfile.value.userId)
        return suspendCancellableCoroutine { continuation ->

            AccountAPI.logout(object : IMSendCallback {
                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    dispatchServerState(ServerState.Logout)
                    continuation.resume(value = true)
                }
                override fun onError(error: String) {
                    ToastProvider.showToast("操作失败 $error")
                    continuation.resume(value = false)
                }
            })
        }
    }

    fun dispatchServerState(serverState: ServerState) { // 设置服务器状态
        ChatCoroutineScope.launch {
            serverConnectState.emit(value = serverState)
        }
    }

    private fun refreshUser(person: Person) {
        ChatCoroutineScope.launch {
            personProfile.emit(value = person)
        }
    }


}