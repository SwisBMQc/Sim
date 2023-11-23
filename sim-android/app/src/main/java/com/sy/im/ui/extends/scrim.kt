package com.sy.im.ui.extends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color

fun Modifier.scrim(color: Color): Modifier = drawWithContent { // 给绘制内容添加背景色
    drawContent()
    drawRect(color = color)
}

fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed { // 创建一个可点击的ui组件，点击时调用onClick
    clickable(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = null   // 交互时不显示动画
    )
}
