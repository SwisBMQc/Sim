package com.sy.im.ui.view.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.im.ui.view.friendship.FriendshipPage
import com.sy.im.ui.view.person.PersonalInfoPage
import com.sy.im.ui.widgets.DrawerIcon
import com.sy.im.ui.widgets.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainPage(viewModel: MainViewModel) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (viewModel.bottomBarViewState.selectedTab != MainPageTab.Person) {
                MainTopBar(
                    viewState = viewModel.topBarViewState,
                    scope = scope,
                    scaffoldState = scaffoldState
                ) }
            },
        drawerContent = { DrawerContent(viewModel.drawerViewState, scope, scaffoldState) },
        bottomBar = { MainBottomBar(viewState = viewModel.bottomBarViewState)}
    ){
        innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding)
            ) {
                when (viewModel.bottomBarViewState.selectedTab) {
                    MainPageTab.Conversation -> {
                        Text(text = "ConversationPage()")
//                        ConversationPage()
                    }

                    MainPageTab.Friendship -> {
                        FriendshipPage()
                    }

                    MainPageTab.Person -> {
                        PersonalInfoPage()
                    }
                }
            }
    }
    AddDialog(viewState = viewModel.addViewState)
    LoadingDialog(visible= viewModel.loadingDialogVisible)
}

@Composable
private fun MainTopBar(
    viewState: MainTopBarViewState,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    var isMenuVisible by remember{ mutableStateOf(false) }

    Column {
        TopAppBar(
            modifier = Modifier.height(60.dp),
            title = { Text(text = viewState.personProfile.showName) },
            navigationIcon = {
                DrawerIcon( url = viewState.personProfile.imgUrl, drawerOpen = { scope.launch { scaffoldState.drawerState.open() } })
            },
            actions = {
                IconButton(onClick = { isMenuVisible = !isMenuVisible }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More",modifier = Modifier.size(30.dp))
                }
                Box(
                    modifier = Modifier
                        .padding(end = 10.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    DropdownMenu(
                        modifier = Modifier.background(color = MaterialTheme.colors.background),
                        expanded = isMenuVisible,
                        onDismissRequest = {
                            isMenuVisible = false
                        }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                viewState.showAddDialog(1)
                                isMenuVisible = false
                            }
                        ){
                            Text(
                                text = "添加好友",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        }
                        DropdownMenuItem(
                            onClick = {
                                viewState.showAddDialog(2)
                                isMenuVisible = false
                            }
                        ){
                            Text(
                                text = "加入群聊",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun DrawerContent(viewState: MainDrawerViewState,scope: CoroutineScope,scaffoldState: ScaffoldState) {
    // 防止用户在侧边栏打开时误触返回按钮而直接退出应用
    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = viewState.switchTheme)
        ){
            Text(
                text = "切换主题",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = viewState.feedback)
        ){
            Text(
                text = "反馈问题",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

