package com.sy.im.ui.view.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sy.im.model.Message
import com.sy.im.model.TimeMessage

/**
 *@Author：sy
 *@Date：2024/1/28
 */
class ChatViewModel(private val chat: Chat) : ViewModel() {

    companion object {
        private const val TEXT_MSG_MAX_LENGTH = 200
    }

    private val allMessage = mutableListOf<Message>()

    private val lastMessage: Message?
        get() {
            return allMessage.lastOrNull { it !is TimeMessage }
        }

    var chatPageViewState by mutableStateOf(
        value = ChatPageViewState(
            chat = chat,
            topBarTitle = "",
            listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
            messageList = emptyList()
        )
    )
        private set


}