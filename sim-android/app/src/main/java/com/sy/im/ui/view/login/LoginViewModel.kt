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
            lastLoginUserId = AccountProvider.lastLoginUserId,
            showPanel = true,
            loading = false,
            rememberPassword = false
        )
    )
        private set


    suspend fun onClickLoginButton(userId: String,password :String): Boolean {
        loginViewState = loginViewState.copy(
            lastLoginUserId = userId,
            loading = true
        )
        return login(userId,password)
    }

    suspend fun onClickRegisterButton(userId: String,password :String): Boolean {
        loginViewState = loginViewState.copy(
            lastLoginUserId = userId,
            loading = true
        )
        return register(userId,password)
    }

    private suspend fun login(userId: String, password: String): Boolean {
        return if (SimAPI.loginLogic.login(userId, password)) {
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