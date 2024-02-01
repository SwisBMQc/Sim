package com.sy.im.ui.view.friendship

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.im.logic.SimAPI
import com.sy.im.model.FriendRequest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *@Author：sy
 *@Date：2023/12/15
 */
class FriendshipViewModel : ViewModel() {
    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    var friendshipState by mutableStateOf(
        value = FriendshipViewState(
            listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
            requestList = emptyList(),
            joinedGroupList = emptyList(),
            friendList = emptyList(),
            verify = ::verify,
//            onClickGroupItem = ::onClickGroupItem,
//            onClickFriendItem = ::onClickFriendItem
        )
    )
        private set

    private fun verify(request: FriendRequest,result: Int) {
        viewModelScope.launch {
            SimAPI.friendLogic.verifyFriend(request.id, result)
        }
    }


    init {
        viewModelScope.launch {
            launch {
                SimAPI.friendLogic.requestList.collect {
                    friendshipState = friendshipState.copy(requestList = it)
                }
            }
            launch {
                SimAPI.friendLogic.friendList.collect {
                    friendshipState = friendshipState.copy(friendList = it)
                }
            }
        }


//        ComposeChat.groupProvider.refreshJoinedGroupList()
//        ComposeChat.friendshipProvider.refreshFriendList()
    }

    private fun requestData() {
        viewModelScope.launch {
            SimAPI.friendLogic.refreshRequestList()
            SimAPI.friendLogic.refreshFriendList()
        }
    }

}