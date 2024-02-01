package com.sy.im.ui.view.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.im.ui.theme.UnRead
import com.sy.im.ui.widgets.UnreadBadge

@Composable
fun MainBottomBar(viewState: MainBottomBarViewState) {

    BottomAppBar(){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            ) {

            for (tab in MainPageTab.values()) {
                val selected = viewState.selectedTab == tab
                val icon: ImageVector
                val unreadCount: Int    // 未读消息数量
                when (tab) {
                    MainPageTab.Conversation -> {
                        icon = Icons.Rounded.Email
                        unreadCount = viewState.unreadMessageCount
                    }

                    MainPageTab.Friendship -> {
                        icon = Icons.Filled.Person
                        unreadCount = 0
                    }

                    MainPageTab.Person -> {
                        icon = Icons.Rounded.AccountBox
                        unreadCount = 0
                    }
                }
                BottomBarItem(
                    icon = icon,
                    selected = selected,
                    unreadCount = unreadCount,
                    onClick = { viewState.onClickTab(tab)}
                )
            }
        }
    }
}


@Composable
fun BottomBarItem(
    icon: ImageVector,
    label: String ?= null,
    selected: Boolean,
    onClick: () -> Unit,
    unreadCount: Int? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color.White else Color.Gray,
            modifier = Modifier.size(30.dp)
        )
        if (unreadCount != null && unreadCount > 0) {
            UnreadBadge(unreadCount = unreadCount)
        }
    }
}



//@Preview
//@Composable
//fun BottomTest(){
//    BottomBarWithBadge(1)
//}
