package com.sy.im.logic

import android.util.Log
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.sy.im.api.AccountApi
import com.sy.im.coroutine.ChatCoroutineScope
import com.sy.im.interf.IMSendCallback
import com.sy.im.model.Person
import com.sy.im.model.ServerState
import com.sy.im.protobuf.MessageProtobuf
import com.sy.im.provider.AccountProvider
import com.sy.im.provider.ToastProvider
import com.sy.im.util.FillEmptyUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoginLogic {

    val personProfile = MutableStateFlow(value = FillEmptyUtil.setEmpty(Person()))

    val serverConnectState = MutableSharedFlow<ServerState>()

    suspend fun login(userId: String,input: String,way: Int): Boolean {

        return suspendCancellableCoroutine { continuation ->

            AccountApi.login(userId, input, way, object : IMSendCallback {

                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    val extend = msg?.head?.extend
                    val data = JSON.parseObject(extend)["data"] as Map<String, Any>

                    // 取出token
                    AccountProvider.onUserLogin(userId, data["token"] as String)

                    // 取出user 更新用户
                    val user: Person = Gson().fromJson(data["person"].toString(), Person::class.java)
                    refreshUser(FillEmptyUtil.setEmpty(user))

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

            AccountApi.register(userId, password, object : IMSendCallback {

                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    val extend = msg?.head?.extend
                    val data = JSON.parseObject(extend)["data"] as Map<String, Any>

                    // 取出token
                    AccountProvider.onUserLogin(userId, data["token"] as String)

                    // 取出user 更新用户
                    val user: Person = Gson().fromJson(data["person"].toString(), Person::class.java)
                    refreshUser(FillEmptyUtil.setEmpty(user))

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

            AccountApi.logout(personProfile.value.userId,object : IMSendCallback {
                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    dispatchServerState(serverState = ServerState.Logout)
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

    fun refreshUser(){
        ChatCoroutineScope.launch {
            personProfile.emit(value = getPersonProfile())
        }
    }

    private suspend fun getPersonProfile(): Person {
        return suspendCancellableCoroutine { continuation ->

            AccountApi.getPersonProfile(AccountProvider.lastLoginUserId,
                object : IMSendCallback {
                override fun onSuccess(msg: MessageProtobuf.Msg?) {
                    val extend = msg?.head?.extend
                    val data = JSON.parseObject(extend)["data"] as Map<String, Any>

                    val person: Person = Gson().fromJson(data["person"].toString(), Person::class.java)
                    Log.i("sim-loginLogic", "getPersonProfile：$person")

                    continuation.resume(value = FillEmptyUtil.setEmpty(person))
                }
                override fun onError(error: String) {
                    ToastProvider.showToast("获取失败 $error")
                    continuation.resume(value = personProfile.value) // 返回本身
                }
            })
        }
    }

    suspend fun updatePersonProfile(person: Person) {
        return suspendCancellableCoroutine {

            AccountApi.updatePersonProfile(
                AccountProvider.lastLoginUserId,
                Gson().toJson(person),
                object : IMSendCallback {
                    override fun onSuccess(msg: MessageProtobuf.Msg?) {
                        refreshUser()
                    }
                    override fun onError(error: String) {
                        ToastProvider.showToast("修改失败$error")
                    }
                })
            it.resume(Unit) //  恢复协程
        }
    }
}