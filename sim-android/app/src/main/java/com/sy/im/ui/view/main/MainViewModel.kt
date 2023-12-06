package com.sy.im.ui.view.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.im.logic.SimAPI
import com.sy.im.model.ServerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){

    // 加载遮罩层
    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    var topBarViewState by mutableStateOf(
        value = MainTopBarViewState(
            avatar = "",
            connectState = "(无连接)",
            search = ::search,
            add = ::add
            )
    )
        private set

    var bottomBarViewState by mutableStateOf(
        value = MainBottomBarViewState(
            selectedTab = MainPageTab.Conversation,
            unreadMessageCount = 0,
            onClickTab = ::onClickTab // 点击修改状态
        )
    )
        private set

    var drawerViewState by mutableStateOf(
        value = MainDrawerViewState(
            switchTheme = ::switchTheme,
            feedback = ::feedback
        )
    )
        private set

    /*
    响应式共享状态
    MutableStateFlow允许你在运行时更新其状态，并与其他Flow类型（如NonNullFlow、NullableFlow等）结合使用。
     */
    private val _serverConnectState = MutableStateFlow(value = ServerState.ConnectFailed)
    val serverConnectState: SharedFlow<ServerState> = _serverConnectState

    init {
        viewModelScope.launch {
            launch {

            }
            launch {

            }
            launch {
                // 更新连接状态
                SimAPI.serverConnectState.collect {
                    _serverConnectState.emit(value = it)
                    if (it == ServerState.ConnectSuccess) {
                        requestData()
                        topBarViewState = topBarViewState.copy( connectState = "",avatar = SimAPI.personProfile.value.imgUrl )
                    }
                }
            }
        }
    }

    private fun requestData() {
        viewModelScope.launch {
            SimAPI.mainLogic.refreshUser()
        }
    }

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }

    private fun search() {
        println("search")

    }

    private fun add() {
        println("add")
    }

    private fun switchTheme() {
        println("switchTheme")
    }

    private fun feedback() {
        println("feedback")
    }

    private fun onClickTab(mainPageTab: MainPageTab) {
        if (bottomBarViewState.selectedTab != mainPageTab) {
            bottomBarViewState = bottomBarViewState.copy(selectedTab = mainPageTab)
        }
    }

}


