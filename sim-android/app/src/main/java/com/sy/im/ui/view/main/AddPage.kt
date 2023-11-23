package com.sy.im.ui.view.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 添加好友和群组界面
 */
@Composable
fun AddPage(viewState: AddPageViewState){

    var input by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it.trim() },
            label = { Text("input") },
            maxLines = 1,
            modifier = Modifier.weight(3f)
        )
        Button(onClick = { viewState.addFriend(input) }){
            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = "添加朋友",
                fontSize = 20.sp,
            )
        }
        Button(onClick = { viewState.joinGroup(input) }){
            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = "添加群组",
                fontSize = 20.sp,
            )
        }
    }
}