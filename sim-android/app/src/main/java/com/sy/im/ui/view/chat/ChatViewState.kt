package com.sy.im.ui.view.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import com.sy.im.model.Message

/**
 *@Author：sy
 *@Date：2024/1/28
 */
@Stable
data class ChatPageViewState(
    val chat: Chat,
    val listState: LazyListState,
    val topBarTitle: String,
    val messageList: List<Message>
)

@Stable
data class ChatPageAction(
    val onClickAvatar: (Message) -> Unit,
    val onClickMessage: (Message) -> Unit,
    val onLongClickMessage: (Message) -> Unit
)