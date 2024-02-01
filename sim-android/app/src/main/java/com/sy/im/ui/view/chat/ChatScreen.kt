package com.sy.im.ui.view.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatScreen() {
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chat App") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 聊天消息列表
                ChatMessageList()

                // 输入框
                ChatInputField(
                    messageText = messageText,
                    onMessageTextChanged = { messageText = it }
                )
            }
        }
    )
}

@Composable
fun ChatMessageList() {
    // TODO: 实现聊天消息列表
}

@Composable
fun ChatInputField(
    messageText: String,
    onMessageTextChanged: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = messageText,
            onValueChange = { onMessageTextChanged(it) },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { /* TODO: 处理发送消息的逻辑 */ }),
            placeholder = { Text(text = "Type a message...") }
        )

        Button(
            onClick = { /* TODO: 处理发送消息的逻辑 */ }
        ) {
            Text(text = "Send", fontSize = 16.sp)
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}
