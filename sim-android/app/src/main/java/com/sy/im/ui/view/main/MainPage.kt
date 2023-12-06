package com.sy.im.ui.view.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                           scaffoldState = scaffoldState) }
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
                        Text(text = "FriendshipPage()")
//                        FriendshipPage()
                    }

                    MainPageTab.Person -> {
                        PersonalInfoPage()
                    }
                }
            }
        LoadingDialog(viewModel.loadingDialogVisible)
    }
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
            title = { Text(text = viewState.connectState) },
            navigationIcon = {
                DrawerIcon( url = viewState.avatar, drawerOpen = { scope.launch { scaffoldState.drawerState.open() } })
            },
            actions = {
                IconButton(onClick = viewState.search ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                IconButton(onClick = { isMenuVisible = !isMenuVisible }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                }
            }
        )
        if (isMenuVisible) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.End)
                    .padding(end = 10.dp)
            )  {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { isMenuVisible = false },
                ) {
                    DropdownMenuItem(
                        onClick = viewState.add ) {
                        Row() {
                            Icon(Icons.Filled.Add, contentDescription = "add new friends")
                            Text("新朋友")
                        }
                    }
                    DropdownMenuItem(
                        onClick = viewState.add ) {
                        Row() {
                            Icon(Icons.Filled.Add, contentDescription = "add new friends")
                            Text("群组")
                        }
                    }
                }
            }
        }
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
    Column(modifier = Modifier.fillMaxSize(),
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

