package com.sy.im.ui.view.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.sy.im.MainActivity
import com.sy.im.ui.theme.SimandroidTheme
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    /*
        与当前Activity的生命周期绑定
     */
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimandroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginPage(
                        viewState = loginViewModel.loginViewState,
                        onClickRegisterButton = { userId, password ->
                            run {
                                onClickRegisterButton(userId, password)
                            }
                        },
                        onClickLoginButton = { userId, password ->
                            run {
                                onClickLoginButton(userId, password)
                            }
                        })
                }
            }
        }
    }

    // 点击登录
    private fun onClickLoginButton(userId: String, password: String) {
        lifecycleScope.launch {
            val result = loginViewModel.onClickLoginButton(userId, password)
            if (result) {
                navToMainPage()
                finish()
            }
        }
    }

    // 点击注册按钮
    private fun onClickRegisterButton(userId: String, password: String) {
        lifecycleScope.launch {
            val result = loginViewModel.onClickRegisterButton(userId, password)
            if (result) {
                navToMainPage()
                finish()
            }
        }
    }

    private fun navToMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}