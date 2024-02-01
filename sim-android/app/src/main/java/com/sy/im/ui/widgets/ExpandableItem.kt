package com.sy.im.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 可展开的列表
 *
 * @param title 列表标题
 * @param modifier Modifier
 * @param endText 列表标题的尾部文字，默认为空
 * @param subItemStartPadding 子项距离 start 的 padding 值
 * @param subItem 子项
 * */
@Composable
fun ExpandableItem(
    title: String,
    modifier: Modifier = Modifier,
    endText: String = "",
    isShowItem: Boolean = false,
    subItemStartPadding: Int = 0,
    subItem: @Composable () -> Unit
) {
    var isShowSubItem by remember { mutableStateOf(isShowItem) }

    val arrowRotateDegrees: Float by animateFloatAsState(if (isShowSubItem) 90f else 0f)

    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEF2F7))
                .padding(5.dp,3.dp)
                .clickable {
                    isShowSubItem = !isShowSubItem
                }
        ) {
            Text(text = title)
            Row {
                if (endText.isNotBlank()) {
                    Text(text = endText,
                        modifier = modifier.padding(end = 4.dp).widthIn(0.dp, 100.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }
                Icon(
                    Icons.Outlined.List,
                    contentDescription = title,
                    modifier = Modifier.rotate(arrowRotateDegrees)
                )
            }
        }

        AnimatedVisibility(visible = isShowSubItem) {
            Column(modifier = Modifier.padding(start = subItemStartPadding.dp)) {
                subItem()
            }
        }
    }
}

@Preview
@Composable
fun ShrinkingListPreview() {
    ExpandableItem(
        "验证消息",
        isShowItem = true
    ){
        LazyColumn {
            items(100) {
                Text("Item $it", style = MaterialTheme.typography.body1)
            }
        }
    }
}

