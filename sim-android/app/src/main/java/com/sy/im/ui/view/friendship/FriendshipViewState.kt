package com.sy.im.ui.view.friendship

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import com.sy.im.model.FriendRequest
import com.sy.im.model.Group
import com.sy.im.model.Person

/**
 *@Author：sy
 *@Date：2023/12/15
 */
@Stable
data class FriendshipViewState(
    val listState: LazyListState,
    val requestList: List<FriendRequest>,
    val joinedGroupList: List<Group>,
    val friendList: List<Person>,
    val verify: (FriendRequest, Int)->Unit,
//    val onClickGroupItem: (Group) -> Unit,
//    val onClickFriendItem: (Person) -> Unit
)

