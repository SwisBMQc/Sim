package com.sy.im.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sy.im.R

@Composable
fun DrawerIcon(url:String,drawerOpen: () -> Unit) {

    if (!url.isNullOrBlank()) {
        val painter = rememberImagePainter(
            data = url,
            builder = {
                crossfade(true)
                placeholder(R.drawable.gray_circle)
                error(R.drawable.gray_circle)
            }
        )

        Image(
            painter = painter,
            contentDescription = "Drawer Icon",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = { drawerOpen() }),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
        )
    } else {
        IconButton(onClick = { drawerOpen() }) {
            Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
        }
    }
}
