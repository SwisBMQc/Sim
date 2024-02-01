package com.sy.im.ui.view.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.im.logic.SimAPI
import com.sy.im.model.Group
import com.sy.im.model.Person
import com.sy.im.model.ServerState
import kotlinx.coroutines.delay
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
            personProfile = Person(),
            showSearchPage = ::showSearchPage,
            showAddDialog = ::showAddDialog
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

    var addViewState by mutableStateOf(
        value = AddViewState(
            visible = false,
            onDismissRequest = ::onDismissRequestAdd,
            searchFriend = ::searchFriend,
            searchGroup = ::searchGroup,
            way = 1,
            joinGroup = ::joinGroup,
            addFriend = ::addFriend
        )
    )
        private set

    /*
    响应式共享状态
    MutableStateFlow允许你在运行时更新其状态，并与其他Flow类型（如NonNullFlow、NullableFlow等）结合使用。
     */
    private val _serverConnectState = MutableStateFlow(value = ServerState.ConnectSuccess)
    val serverConnectState: SharedFlow<ServerState> = _serverConnectState

    init {
        viewModelScope.launch {
            launch {
                println("*************************")
            }
            launch {
                SimAPI.personProfile.collect {
                    topBarViewState = topBarViewState.copy(personProfile = it) // 账号信息
                }
            }
            launch {
                SimAPI.serverConnectState.collect {
                    _serverConnectState.emit(value = it)
                    if (it == ServerState.ConnectSuccess) {
                        requestData()
                    }
                }
            }
        }
    }

    private fun requestData() {
        viewModelScope.launch {
            SimAPI.mainLogic.refreshUser()
            SimAPI.friendLogic.refreshRequestList()
            SimAPI.friendLogic.refreshFriendList()
        }
    }

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }


    private fun showSearchPage() {
        TODO("Not yet implemented")
    }

    private fun showAddDialog(way: Int) {
        addViewState = addViewState.copy(visible = true, way = way)
    }

    private fun onDismissRequestAdd() {
        addViewState = addViewState.copy(visible = false)
    }

    private fun searchGroup(groupId: String, callback: (Group?) -> Unit) {
        viewModelScope.launch {
            val group = SimAPI.mainLogic.getGroup(groupId.toInt())
            callback(group)
        }
    }

    private fun searchFriend(userId: String, callback: (Person?) -> Unit) {
        viewModelScope.launch {
            val person = SimAPI.mainLogic.getFriend(userId)
            callback(person)
        }
    }

    private fun addFriend(userId: String) {
        viewModelScope.launch {
           SimAPI.friendLogic.sendFriendRequest(userId)
        }
    }

    private fun joinGroup(groupId: Int) {
        println("joinGroup $groupId")
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


