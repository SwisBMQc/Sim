package com.sy.im.ui.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sy.im.ui.extends.clickableNoRipple
import kotlinx.coroutines.delay

/**
 * 加载组件
 */
@Composable
fun LoadingDialog(visible: Boolean) {
    if (visible){
        ProgressWithAutoClose()
    }
}

@Composable
private fun ProgressWithAutoClose() {
    var isVisible by remember { mutableStateOf(true) }

    // 7s后自动关闭
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(10000) // 10 seconds delay
            isVisible = false
        }
    }

    if (isVisible) {
        BackHandler(enabled = true) {}
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickableNoRipple {}
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.Center),
                color = MaterialTheme.colors.primary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoadingDialogPreview() {
    MaterialTheme {
        var visible by remember { mutableStateOf(false) }

        Button(onClick = { visible = true }) {
            Text(text = "打开圈圈")
        }

        LoadingDialog(visible)
    }
}