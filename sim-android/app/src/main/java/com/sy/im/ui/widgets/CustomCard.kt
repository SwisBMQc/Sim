package com.sy.im.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sy.im.R
import com.sy.im.model.FriendRequest
import com.sy.im.ui.extends.clickableNoRipple
import com.sy.im.ui.theme.SimandroidTheme

@Composable
fun ItemCard(
    title: String = "",
    description: String = "",
    imgUrl: String = "",
    time: String = "",
    unreadMessageCount: Int = 0,
    onClickCard: () -> Unit = {},
) {
    CustomCard(
        title = title,
        description = description,
        imgUrl = imgUrl,
        onClickCard = onClickCard
    ) {
        Text(
            text = time,
            modifier = Modifier.padding(0.dp,4.dp))
        if(unreadMessageCount > 0){
            UnreadBadge(unreadCount = unreadMessageCount,0)
        }
    }
}

@Composable
fun ButtonCard(
    title: String = "",
    description: String = "",
    imgUrl: String = "",
    onClickButton: () -> Unit // New parameter for button click
){
    CustomCard(
        title = title,
        description = description,
        imgUrl = imgUrl
    ){
        var isButtonVisible by remember{ mutableStateOf(true) }
        if (isButtonVisible){
            Button(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    onClickButton()
                    isButtonVisible = !isButtonVisible
                },
                content = {Text(text = "添加")}
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.sent),
                contentDescription = "message Sent",
                modifier = Modifier.padding(0.dp,10.dp),
                tint = Color(0xFF4D9629)
            )
        }
    }
}

/**
 * 验证消息
 */
@Composable
fun FriendRequestCard(
    res: FriendRequest,
    onClickAgree: ()->Unit,
    onClickDisAgree: ()->Unit,
){
    CustomCard(
        title = res.friend.showName,
        description = res.friend.signature,
        imgUrl = res.friend.imgUrl
    ){
        when (res.isAgreed) {
            0 -> {
                if (res.type == 0){
                    Text(text = "等待验证",color = Color.Gray)
                } else{
                    var isClickable by remember{ mutableStateOf(true) }
                    Row {
                        Button(
                            enabled = isClickable,
                            onClick = {
                                onClickAgree()
                                isClickable = !isClickable
                            },
                            content = {Text(text = "同意")}
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        OutlinedButton(
                            enabled = isClickable,
                            onClick = {
                                onClickDisAgree()
                                isClickable = !isClickable},
                            content = {Text(text = "拒绝")}
                        )
                    }
                }
            }
            1 -> {
                val str: String = if (res.type == 0){
                    "验证已通过"
                } else{
                    "已同意"
                }
                Text(text = str, color = Color.Gray)
            }
            -1 -> {
                val str: String = if (res.type == 0){
                    "对方已拒绝"
                } else{
                    "已拒绝"
                }
                Text(text = str, color = Color.Gray)
            }
        }
    }

}

@Composable
fun CustomCard(
    title: String = "",
    description: String = "",
    imgUrl: String = "",
    onClickCard: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickableNoRipple { onClickCard }
            .padding(all = 8.dp)
    ) {
        Image(
            painter = rememberImagePainter( data = imgUrl.ifBlank {R.drawable.gray_circle} ),
            contentDescription = "Contact profile picture",
            // 图片参数
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape) // 形状为圆形
        )

        // 添加间隔
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = title,
                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold, // 加粗字体
                modifier = Modifier.padding(0.dp,3.dp)
            )
            Text(text = description?:"",
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column {
            if (content != null) {
                content()
            }
        }
    }
}




// 预览可组合函数 必须无参函数
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimandroidTheme {
        Column {

//            ItemCard(title = "华仔", description = "我用华为的窝",time = "13:00")
//            CustomCard(title = "华仔")
            ButtonCard(
                title = "华仔", description = "我用华为的窝",
                onClickButton = { println("********") }
            )
        }
    }
}