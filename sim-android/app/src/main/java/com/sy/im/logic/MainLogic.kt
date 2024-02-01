package com.sy.im.logic

import android.content.Context
import android.net.Uri
import android.util.Log
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.sy.im.api.AccountAPI
import com.sy.im.coroutine.ChatCoroutineScope
import com.sy.im.interf.IMSendCallback
import com.sy.im.logic.SimAPI.personProfile
import com.sy.im.model.Group
import com.sy.im.model.Person
import com.sy.im.protobuf.MessageProtobuf
import com.sy.im.provider.AccountProvider
import com.sy.im.provider.ToastProvider
import com.sy.im.util.CompressImageUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MainLogic {

    fun refreshUser(){
        ChatCoroutineScope.launch {
            personProfile.emit(value = getPersonProfile())
        }
    }

    private suspend fun getPersonProfile(): Person {
        return suspendCancellableCoroutine { continuation ->
            AccountAPI.getPersonInfo(
                AccountProvider.lastLoginUserId,
                object : IMSendCallback {
                    override fun onSuccess(msg: MessageProtobuf.Msg?) {
                        val extend = JSON.parseObject(msg?.head?.extend)
                        val data = extend["data"] as Map<String, Any>

                        val person: Person = Gson().fromJson(data["person"].toString(), Person::class.java)
                        Log.i("sim-mainLogic", "getPersonProfile：$person")

                        continuation.resume(value = person)
                    }
                    override fun onError(error: String) {
                        ToastProvider.showToast("获取失败 $error")
                        continuation.resume(value = personProfile.value) // 返回本身
                    }
                }
            )
        }
    }

    suspend fun updatePersonProfile(person: Person) {
        return suspendCancellableCoroutine {

            AccountAPI.updatePersonProfile(
                Gson().toJson(person),
                object : IMSendCallback {
                    override fun onSuccess(msg: MessageProtobuf.Msg?) {
                        refreshUser() // 刷新信息
                    }
                    override fun onError(error: String) {
                        ToastProvider.showToast("修改失败$error")
                    }
                })
            it.resume(Unit) //  恢复协程
        }
    }

    suspend fun uploadAvatar(context: Context, imgUri: Uri): String{
        Log.i("sim-mainLogic", "uploadAvatar")
        val filename = CompressImageUtils.createFileName(context = context, imageUri = imgUri)
        val byteArray = CompressImageUtils.compressImage(context = context, imageUri = imgUri)

        return suspendCancellableCoroutine {
            AccountAPI.uploadProfile(
                filename,
                byteArray,
                object : IMSendCallback {
                    override fun onSuccess(msg: MessageProtobuf.Msg?) {
                        val extend = JSON.parseObject(msg?.head?.extend)
                        val data = extend["data"] as Map<String, Any>
                        val reason = extend["reason"]
                        ToastProvider.showToast("$reason")

                        val imgUrl = data["filePath"] as String

                        it.resume(value = imgUrl)
                    }
                    override fun onError(error: String) {
                        ToastProvider.showToast("$error")
                        it.resume(value = "")
                    }
                }
            )
        }
    }

    suspend fun getFriend(userId: String) : Person? {
        return suspendCancellableCoroutine { continuation ->
            AccountAPI.getPersonInfo(
                userId,
                object : IMSendCallback {
                    override fun onSuccess(msg: MessageProtobuf.Msg?) {
                        val extend = JSON.parseObject(msg?.head?.extend)
                        val data = extend["data"] as Map<String, Any>

                        val person: Person = Gson().fromJson(data["person"].toString(), Person::class.java)
                        Log.i("sim-mainLogic", "searchFriend：$person")

                        continuation.resume(value = person)
                    }
                    override fun onError(error: String) {
                        println(error)
                        continuation.resume(value = null)
                    }
                }
            )
        }
    }

    suspend fun getGroup(groupId: Int): Group? {
        TODO("Not yet implemented")
    }

}