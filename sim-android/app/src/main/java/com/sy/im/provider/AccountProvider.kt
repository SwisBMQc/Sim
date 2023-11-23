package com.sy.im.provider

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.sy.im.coroutine.ChatCoroutineScope
import com.sy.im.model.Person
import com.sy.im.model.ServerState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 账号相关全局变量
 */
object AccountProvider {

    private const val KEY_GROUP = "AccountGroup"

    private const val KEY_LAST_LOGIN_USER_ID = "keyLastLoginUserId"

    private const val KEY_LAST_LOGIN_TOKEN = "keyLastLoginToken"

    private const val KEY_AUTO_LOGIN = "keyAutoLogin"

    private lateinit var preferences: SharedPreferences

    fun init(application: Application) {
        preferences = application.getSharedPreferences(KEY_GROUP, Context.MODE_PRIVATE)
    }

    val lastLoginUserId: String
        get() = preferences.getString(KEY_LAST_LOGIN_USER_ID, "") ?: ""

    val lastLoginToken: String
        get() = preferences.getString(KEY_LAST_LOGIN_USER_ID, "") ?: ""

    val canAutoLogin: Boolean
        get() = preferences.getBoolean(KEY_AUTO_LOGIN, false)

    fun onUserLogin(userId: String,token: String) {   // 打开自动登录
        preferences.edit().apply {
            putString(KEY_LAST_LOGIN_USER_ID, userId)
            putString(KEY_LAST_LOGIN_TOKEN, token)
            putBoolean(KEY_AUTO_LOGIN, true)
            apply()
        }
    }

    fun onUserLogout() {    // 关闭自动登录
        preferences.edit().putBoolean(KEY_AUTO_LOGIN, false).apply()
    }


}