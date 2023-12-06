package com.sy.im.ui.widgets

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sy.im.logic.SimAPI

@Composable
fun ImageUploadButton(
    initialImgUrl: String,
    setImgUrl:(String)->Unit) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    LaunchedEffect(selectedImageUri) {
        // 上传图片
        selectedImageUri?.let { uri ->
            loading = true
            SimAPI.mainLogic.uploadAvatar(context = context, imgUri = uri).let {
                loading = false
                error = it.isNullOrBlank()
                setImgUrl(it)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .background(MaterialTheme.colors.onSurface.copy(0.2f))
                .clickable {
                    if (!loading) {
                        getContent.launch("image/*")
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            var painter = rememberImagePainter(data = initialImgUrl)            // 显示选择的图片
            selectedImageUri?.let { uri ->
                painter = rememberImagePainter(data = uri)
            }
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        // 显示加载中或错误状态
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
            )
        } else if (error) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ImageUploadButtonPreview() {
    MaterialTheme {
//        ImageUploadButton(
//
//        ) {
//            println(it)
//        }
    }
}
