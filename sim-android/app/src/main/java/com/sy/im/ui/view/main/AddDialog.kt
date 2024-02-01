package com.sy.im.ui.view.main

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.sy.im.ui.widgets.ButtonCard
import com.sy.im.ui.widgets.SearchBar
import com.sy.im.ui.widgets.ShowDialog

/**
 *@Author：sy
 *@Date：2023/12/7
 */
@Composable
fun AddDialog( viewState: AddViewState ) {
    var state by remember { mutableStateOf<ButtonCardState?>(null) }

    ShowDialog(
        visible = viewState.visible,
        onDismissRequest = viewState.onDismissRequest ) {
        SearchBar( "请输入 userId")
        {
            viewState.searchFriend(it) { friend ->
                state = if (friend != null){
                    ButtonCardState(
                        title = friend.showName,
                        description = friend.signature,
                        imgUrl = friend.imgUrl,
                        onClickButton = { viewState.addFriend(it) }
                    )
                } else{
                    null
                }
            }
        }

        // 显示搜索结果
        state?.let {
            ButtonCard(
                it.title,
                it.description,
                it.imgUrl,
                it.onClickButton
            )
        }
    }
}

data class ButtonCardState(
    val title : String,
    val description : String,
    val imgUrl : String,
    val onClickButton:()-> Unit
)


@Preview(showBackground = true)
@Composable
fun AddDialogPreview() {
    MaterialTheme {

    }
}