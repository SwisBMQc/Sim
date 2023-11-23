package com.sy.im.ui.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sy.im.ui.extends.clickableNoRipple

/**
 * 加载组件
 */
@Composable
fun LoadingDialog(visible: Boolean) {
    if (visible) {
        BackHandler(enabled = true) {

        }
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