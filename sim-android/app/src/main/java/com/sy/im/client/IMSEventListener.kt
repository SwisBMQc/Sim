package com.sy.im.client

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import com.sy.im.interf.OnEventListener
import com.sy.im.protobuf.MessageProtobuf
import com.sy.im.provider.ContextProvider.context
import com.sy.im.message.MessageType
import com.sy.im.provider.AccountProvider
import com.sy.im.util.MessageProcessor
import java.util.*

class IMSEventListener : OnEventListener {
    override fun dispatchMsg(msg: MessageProtobuf.Msg?) {
        MessageProcessor.getInstance().receiveMsg(msg)
    }

    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }


    override fun getHeartbeatMsg(): MessageProtobuf.Msg {
        val headBuilder = MessageProtobuf.Head.newBuilder()
            .setMsgId(UUID.randomUUID().toString())
            .setMsgType(MessageType.HEARTBEAT.msgType)
            .setFromId(AccountProvider.lastLoginUserId)
            .setTimestamp(System.currentTimeMillis())
        val builder = MessageProtobuf.Msg.newBuilder()
            .setHead(headBuilder.build())
        return builder.build()
    }

    // 0 为使用默认值
    override fun getReconnectInterval(): Int = 0

    override fun getConnectTimeout(): Int = 0

    override fun getForegroundHeartbeatInterval(): Int = 0

    override fun getBackgroundHeartbeatInterval(): Int = 0

    override fun getResendCount(): Int = 0

    override fun getResendInterval(): Int = 0
}