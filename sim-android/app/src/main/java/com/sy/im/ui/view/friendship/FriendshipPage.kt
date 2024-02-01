package com.sy.im.ui.view.friendship

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sy.im.model.FriendRequest
import com.sy.im.model.Person
import com.sy.im.ui.widgets.CustomCard
import com.sy.im.ui.widgets.ExpandableItem
import com.sy.im.ui.widgets.FriendRequestCard
import com.sy.im.ui.widgets.LoadingDialog

/**
 *@Author：sy
 *@Date：2023/12/15
 */
@Composable
fun FriendshipPage(){

    val viewModel: FriendshipViewModel = viewModel()
    FriendshipContent(friendshipViewState = viewModel.friendshipState)
    LoadingDialog(visible = viewModel.loadingDialogVisible)
}

@Composable
fun FriendshipContent(friendshipViewState :FriendshipViewState){

    Column {

        ExpandableItem(
            "验证消息",
            isShowItem = true
        ){
            LazyColumn {
                items(friendshipViewState.requestList) {
                    FriendRequestCard(
                        res = it,
                        onClickAgree = { friendshipViewState.verify(it,1) },
                        onClickDisAgree = { friendshipViewState.verify(it,-1) }
                    )
                }
            }
        }

        ExpandableItem(
            title = "联系人",
            isShowItem = true
        ){
            LazyColumn {
                items(friendshipViewState.friendList) { f ->
                    CustomCard(
                        title = f.showName,
                        description = f.signature,
                        imgUrl = f.imgUrl
                    )
                }
            }
        }
    }
}




@Preview
@Composable
fun FriendshipPagePreview() {

//    FriendshipPage()

    val res = listOf(
        FriendRequest(1L, Person("user1","用户1","", signature = "没有个性"),0,0,1789456123),
        FriendRequest(2L, Person("user2","用户2","", signature = "没有个性"),0,1,1789456123),
        FriendRequest(3L, Person("user2","用户3","", signature = "没有个性"),0,-1,1789456123),
        FriendRequest(4L, Person("user4","用户4","", signature = "没有个性"),1,0,1789456123),
    )

    ExpandableItem(
        "验证消息",
        isShowItem = true
    ){
        LazyColumn {
            items(res) {
                FriendRequestCard(
                    res = it,
                    onClickAgree = {},
                    onClickDisAgree = {}
                )
            }
        }
    }
}
