package com.sy.im.provider

import android.app.Application
import com.sy.im.client.IMSClientBootstrap
import com.sy.im.client.IMSConnectStatusListener
import com.sy.im.client.IMSEventListener
import com.sy.im.provider.AccountProvider.lastLoginToken
import com.sy.im.provider.AccountProvider.lastLoginUserId

/**
 * 提供上下文
 */
object ContextProvider {

    lateinit var context: Application
        private set

    val bootstrap: IMSClientBootstrap = IMSClientBootstrap.getInstance()

    fun init(application: Application) {
        context = application
    }


}