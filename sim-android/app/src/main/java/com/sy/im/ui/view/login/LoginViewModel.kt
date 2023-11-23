package com.sy.im.ui.view.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sy.im.logic.SimAPI
import com.sy.im.provider.AccountProvider
import kotlinx.coroutines.delay

/**
 * 登录逻辑视图
 */
class LoginViewModel : ViewModel() {


    var loginViewState by mutableStateOf(
        value = LoginViewState(
            lastLoginUserId = "",
            showPanel = false,
            loading = false,
            rememberPassword = false
        )
    )
        private set


    /**
     * 自动登录
     * 当没有缓存userid，或自动登录关闭，则不进行自动登录
     */
    suspend fun tryLogin(): Boolean {

        val lastLoginUserId = AccountProvider.lastLoginUserId
        val lastLoginToken = AccountProvider.lastLoginToken

        return if (lastLoginUserId.isBlank() || !AccountProvider.canAutoLogin) {
            loginViewState = loginViewState.copy(
                lastLoginUserId = lastLoginUserId,
                showPanel = true,
                loading = false
            )
            false
        } else {
            loginViewState = loginViewState.copy(
                lastLoginUserId = lastLoginUserId,
                showPanel = false,
                loading = true
            )
            login(userId = lastLoginUserId,lastLoginToken,0)
        }
    }

    suspend fun onClickLoginButton(userId: String,password :String): Boolean {
        loginViewState = loginViewState.copy(
            lastLoginUserId = userId,
            loading = true
        )
        return login(userId,password,1)
    }

    suspend fun onClickRegisterButton(userId: String,password :String): Boolean {
        loginViewState = loginViewState.copy(
            lastLoginUserId = userId,
            loading = true
        )
        return register(userId,password)
    }

    private suspend fun login(userId: String,input: String,way: Int): Boolean {
        return if (SimAPI.loginLogic.login(userId, input, way)) {
            delay(timeMillis = 250)
            true
        } else {
            loginViewState = loginViewState.copy(
                lastLoginUserId = userId,
                showPanel = true,
                loading = false
            )
            false
        }
    }

    private suspend fun register(userId: String,password: String): Boolean {
        return if (SimAPI.loginLogic.register(userId,password)) {
            delay(timeMillis = 250)
            true
        } else {
            loginViewState = loginViewState.copy(
                lastLoginUserId = userId,
                showPanel = true,
                loading = false
            )
            false
        }
    }

}