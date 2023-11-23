package com.sy.im.ui.view.main

import androidx.compose.runtime.Stable

@Stable
enum class MainPageTab {
    Conversation,
    Friendship,
    Person;
}

@Stable
data class MainTopBarViewState(
    val connectState: String,
    val search: () -> Unit,
    val add: () -> Unit,
    )

@Stable
data class MainBottomBarViewState(
    val selectedTab: MainPageTab,
    val unreadMessageCount: Int,   // 未读消息数
    val onClickTab: (MainPageTab) -> Unit
)

@Stable
data class MainDrawerViewState(
    val switchTheme: () -> Unit,
    val feedback: () -> Unit
)

@Stable
data class AddPageViewState(
    val visible: Boolean,
    val onDismissRequest: () -> Unit,
    val joinGroup: (groupId: String) -> Unit,
    val addFriend: (userId: String) -> Unit
)

@Stable
data class SearchPageViewState(
    val visible: Boolean,
    val onDismissRequest: () -> Unit
)

@Stable
enum class AppTheme {
    Light,
    Dark,
    Gray;
}
