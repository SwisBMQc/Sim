package com.sy.im.ui.view.person

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.sy.im.R
import com.sy.im.provider.AccountProvider.lastLoginUserId
import com.sy.im.ui.extends.clickableNoRipple
import com.sy.im.ui.widgets.LoadingDialog

/**
 * 个人信息界面
 */
@Composable
fun PersonalInfoPage() {

    /* 注意 在compose组件中使用viewModel要导入依赖 */

    val viewModel:PersonViewModel = viewModel()
    PersonContent(viewModel.personViewState)
    LoadingDialog(visible = viewModel.loadingDialogVisible)
}

@Composable
private fun PersonContent(viewState: PersonViewState) {

    var profile = viewState.personProfile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        val painter = rememberImagePainter(
            data = profile.imgUrl,
            builder = {
                crossfade(true)
                placeholder(R.drawable.gray_circle)
                error(R.drawable.gray_circle)
            }
        )

        Image(
            painter = painter,
            contentDescription = "profile picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickableNoRipple {
                    viewState.previewImage(profile.imgUrl)
                },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(22.dp))
        Text(text = profile.nickname, style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "userId: $lastLoginUserId", style = MaterialTheme.typography.h5)
        Text(text = "gender: ${profile.gender}", style = MaterialTheme.typography.h5)
        Text(text = "signature:\t ${profile.signature}", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = viewState.navToUpdate)
        ){
            Text(
                text = "修改个人信息",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = viewState.logout)
        ){
            Text(
                text = "退出登录",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}
