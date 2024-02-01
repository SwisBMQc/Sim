package com.sy.im.ui.view.main

import androidx.compose.runtime.Stable
import com.sy.im.model.Group
import com.sy.im.model.Person
import kotlin.reflect.KFunction2

@Stable
enum class MainPageTab {
    Conversation,
    Friendship,
    Person;
}

@Stable
data class MainTopBarViewState(
    val personProfile: Person,
    val showSearchPage: () -> Unit,
    val showAddDialog: (Int) -> Unit,
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
data class SearchViewState(
    val visible: Boolean,
    val onDismissRequest:()-> Unit,
    val search: (String) -> List<String>,

    /**
     * 点击查询结果
     */
    val resultTap: () -> Unit
)

@Stable
data class AddViewState(
    val visible: Boolean,
    val onDismissRequest: () -> Unit,
    val searchFriend: KFunction2<String, (Person?) -> Unit, Unit>,
    val searchGroup: KFunction2<String, (Group?) -> Unit, Unit>,
    val way: Int,
    val joinGroup: (groupId: Int) -> Unit,
    val addFriend: (userId: String) -> Unit
)


@Stable
enum class AppTheme {
    Light,
    Dark,
    Gray;
}
