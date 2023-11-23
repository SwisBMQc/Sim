package com.sy.im

import android.app.Application
import com.sy.im.client.IMSConnectStatusListener
import com.sy.im.client.IMSEventListener
import com.sy.im.provider.AccountProvider
import com.sy.im.provider.ContextProvider
import com.sy.im.provider.ToastProvider
import com.sy.im.util.IMSConfig

class SimApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AccountProvider.init(this)
        ContextProvider.init(this)
        ToastProvider.init(this)

        val hosts = "[{\"host\":\"192.168.200.1\", \"port\":9000}]"      // 注意不要写127.0.0.1
        ContextProvider.bootstrap.init(
            hosts,
            IMSEventListener(),
            IMSConnectStatusListener(),
            IMSConfig.APP_STATUS_FOREGROUND)  //应用在前台标识为0

    }
}