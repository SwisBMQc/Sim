package com.sy.im.provider

import android.app.Application
import com.sy.im.client.IMSClientBootstrap

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