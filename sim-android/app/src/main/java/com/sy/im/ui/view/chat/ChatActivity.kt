package com.sy.im.ui.view.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sy.im.ui.theme.SimandroidTheme

class ChatActivity : ComponentActivity() {

    companion object {

        private const val keyChat = "keyChat"

        fun navTo(context: Context, chat: Chat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(keyChat, chat)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimandroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    ChatPage(
//                        chatViewModel = chatViewModel,
//                        chatPageAction = chatPageAction
//                    )
                }
            }
        }
    }
}

@Composable
private fun ChatPage(chatViewModel: ChatViewModel, chatPageAction: ChatPageAction) {
    val chatPageViewState = chatViewModel.chatPageViewState
//    val loadMessageViewState = chatViewModel.loadMessageViewState
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatPageTopBar(
                title = chatPageViewState.topBarTitle,
                chat = chatPageViewState.chat
            )
        },
        bottomBar = {
//            ChatPageBottomBar(chatViewModel = chatViewModel)
        }
    ) { innerPadding ->
//        val pullRefreshState = rememberPullRefreshState(
//            refreshing = loadMessageViewState.refreshing,
//            onRefresh = {
//                chatViewModel.loadMoreMessage()
//            })
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues = innerPadding)
//                .pullRefresh(
//                    state = pullRefreshState,
//                    enabled = !loadMessageViewState.loadFinish
//                )
//        ) {
//            MessagePanel(pageViewState = chatPageViewState, pageAction = chatPageAction)
//            PullRefreshIndicator(
//                modifier = Modifier.align(alignment = Alignment.TopCenter),
//                refreshing = loadMessageViewState.refreshing,
//                state = pullRefreshState,
//                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
//                contentColor = MaterialTheme.colorScheme.primary
//            )
//        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatActivityPreview() {
    SimandroidTheme {

    }
}