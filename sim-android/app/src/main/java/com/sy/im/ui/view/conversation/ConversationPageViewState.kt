package com.sy.im.ui.view.conversation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import com.sy.im.model.Conversation

@Stable
data class ConversationPageViewState(
    val listState: LazyListState,
    val conversationList: List<Conversation>,
    val onClickConversation: (Conversation) -> Unit,
    val deleteConversation: (Conversation) -> Unit,
    val pinConversation: (Conversation, Boolean) -> Unit
)