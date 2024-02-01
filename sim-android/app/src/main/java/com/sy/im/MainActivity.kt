package com.sy.im

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.sy.im.model.ServerState
import com.sy.im.provider.AccountProvider
import com.sy.im.provider.ToastProvider.showToast
import com.sy.im.ui.theme.SimandroidTheme
import com.sy.im.ui.view.login.LoginActivity
import com.sy.im.ui.view.main.MainPage
import com.sy.im.ui.view.main.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    /*
    可以在不同的地方复用同一个 ViewModel，而无需为每个 UI 组件创建一个新的 ViewModel
     */
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!AccountProvider.canAutoLogin){
            navToLoginPage()
        }
        setContent {
            SimandroidTheme {
                MainPage(viewModel = mainViewModel)
            }
        }
        initEvent()
    }

    /**
     * 协程响应式监听
     * 跳转到登录界面
     */
    private fun initEvent() {
        lifecycleScope.launch { // 生命周期范围内的协程
            mainViewModel.serverConnectState.collect(){

                Log.i("sim-main", "serverConnectState: $it")

                when (it) {

                    ServerState.KickedOffline -> {
                        showToast(msg = "本账号已在其它客户端登陆，请重新登陆")
                        AccountProvider.onUserLogout()
                        navToLoginPage()
                    }

                    ServerState.Logout, ServerState.UserSigExpired -> { // 下线，过期
                        navToLoginPage()
                    }

                    else -> {

                    }
                }
            }
        }
    }

    /**
     * 导航至登录界面
     */
    private fun navToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}

