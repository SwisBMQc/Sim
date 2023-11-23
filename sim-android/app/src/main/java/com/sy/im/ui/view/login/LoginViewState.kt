package com.sy.im.ui.view.login

import androidx.compose.runtime.Stable

@Stable
data class LoginViewState(
    val lastLoginUserId: String,
    val showPanel: Boolean,
    val loading: Boolean,
    val rememberPassword: Boolean
)