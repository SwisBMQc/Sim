package com.sy.im.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.im.ui.theme.UnRead

/**
 * 自定义组件
 *@Author：sy
 *@Date：2023/12/7
 */
@Composable
fun CustomButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        shape = RoundedCornerShape(20.dp),
        content = {
            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = text,
                fontSize = 16.sp,
                color = Color.White
            )
        },
        onClick = onClick
    )
}

/**
 * 未读消息图标
 */
@Composable
fun UnreadBadge(unreadCount: Int,y: Int = -11) {
    Text(
        modifier = Modifier
            .offset(x = 12.dp, y = y.dp)
            .size(size = 17.dp)
            .background(color = UnRead, shape = CircleShape)
            .wrapContentSize(align = Alignment.Center),
        text = if (unreadCount > 99) {
            "99+"
        } else {
            unreadCount.toString()
        },
        color = Color.White,
        fontSize = 12.sp,
        textAlign = TextAlign.Center
    )
}

